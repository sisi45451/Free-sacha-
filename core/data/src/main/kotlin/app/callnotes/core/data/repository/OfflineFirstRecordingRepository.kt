package app.callnotes.core.data.repository

import app.callnotes.core.database.dao.RecordingDao
import app.callnotes.core.database.entity.SyncMetadata
import app.callnotes.core.database.mapper.toEntity
import app.callnotes.core.database.mapper.toModel
import app.callnotes.core.domain.repository.FeedFilter
import app.callnotes.core.domain.repository.RecordingRepository
import app.callnotes.core.model.Recording
import app.callnotes.core.model.RecordingListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import javax.inject.Inject

/**
 * Repository « local d'abord » : toutes les lectures et écritures passent par
 * la base locale. La synchronisation (étape 6) enverra ensuite les lignes
 * marquées modifiées (`is_dirty`).
 */
class OfflineFirstRecordingRepository @Inject constructor(
    private val recordingDao: RecordingDao,
    private val clock: Clock,
) : RecordingRepository {

    override fun observeFeed(filter: FeedFilter): Flow<List<RecordingListItem>> =
        recordingDao.observeFeed(type = filter.type, limit = filter.limit)
            .map { rows -> rows.map { it.toModel() } }

    override fun observeRecording(id: String): Flow<Recording?> =
        recordingDao.observeById(id).map { it?.toModel() }

    override suspend fun upsert(recording: Recording) {
        val now = clock.millis()
        val existing = recordingDao.getById(recording.id)
        // On conserve la date de création et les champs techniques existants.
        val sync = existing?.sync?.touched(now) ?: SyncMetadata.created(now)
        recordingDao.upsert(
            recording.toEntity(
                sync = sync,
                remoteAudioPath = existing?.remoteAudioPath,
                transcriptionEngine = existing?.transcriptionEngine,
            ),
        )
    }

    override suspend fun delete(id: String) {
        recordingDao.softDeleteCascade(id, nowMs = clock.millis())
    }
}
