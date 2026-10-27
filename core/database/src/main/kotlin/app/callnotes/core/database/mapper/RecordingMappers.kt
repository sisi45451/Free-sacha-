package app.callnotes.core.database.mapper

import app.callnotes.core.database.dao.FeedRow
import app.callnotes.core.database.entity.RecordingEntity
import app.callnotes.core.database.entity.SyncMetadata
import app.callnotes.core.model.AudioFile
import app.callnotes.core.model.PhoneNumber
import app.callnotes.core.model.ProcessingState
import app.callnotes.core.model.Recording
import app.callnotes.core.model.RecordingListItem
import java.time.Duration
import java.time.Instant

// Conversions entre les lignes de la base (types SQLite simples) et les
// modèles du domaine (java.time, objets imbriqués).

private fun phoneNumberOf(normalized: String?, raw: String?): PhoneNumber? =
    if (normalized == null && raw == null) {
        null
    } else {
        PhoneNumber(normalized = normalized ?: raw.orEmpty(), raw = raw ?: normalized.orEmpty())
    }

fun FeedRow.toModel() = RecordingListItem(
    id = id,
    type = type,
    title = title,
    startedAt = Instant.ofEpochMilli(startedAt),
    duration = Duration.ofMillis(durationMs),
    direction = direction,
    counterpartNumber = phoneNumberOf(counterpartNumber, counterpartRaw),
    contactId = contactId,
    contactName = contactName,
    contactPhotoUri = contactPhotoUri,
    oneLineSummary = oneLineSummary,
    stage = processingStage,
    openTodoCount = openTodoCount,
)

fun RecordingEntity.toModel() = Recording(
    id = id,
    type = type,
    title = title,
    startedAt = Instant.ofEpochMilli(startedAt),
    duration = Duration.ofMillis(durationMs),
    direction = direction,
    counterpartNumber = phoneNumberOf(counterpartNumber, counterpartRaw),
    identificationSource = identificationSource,
    primaryContactId = primaryContactId,
    location = location,
    folderId = folderId,
    parentRecordingId = parentRecordingId,
    audio = AudioFile(
        uri = audioUri,
        storage = audioStorage,
        mimeType = audioMimeType,
        sizeBytes = audioSizeBytes,
        sourceFileName = sourceFileName,
    ),
    processing = ProcessingState(
        stage = processingStage,
        error = processingError,
        transcribedUntil = Duration.ofMillis(transcribedUntilMs),
    ),
    language = language,
)

/**
 * Convertit un modèle en ligne de base. [sync] porte les métadonnées de
 * synchronisation (création ou mise à jour), calculées par le repository.
 * [remoteAudioPath] et [transcriptionEngine] sont des données techniques
 * conservées depuis la ligne existante.
 */
fun Recording.toEntity(
    sync: SyncMetadata,
    remoteAudioPath: String? = null,
    transcriptionEngine: String? = null,
) = RecordingEntity(
    id = id,
    type = type,
    title = title,
    startedAt = startedAt.toEpochMilli(),
    durationMs = duration.toMillis(),
    direction = direction,
    counterpartNumber = counterpartNumber?.normalized,
    counterpartRaw = counterpartNumber?.raw,
    identificationSource = identificationSource,
    primaryContactId = primaryContactId,
    location = location,
    folderId = folderId,
    parentRecordingId = parentRecordingId,
    audioUri = audio.uri,
    audioStorage = audio.storage,
    audioMimeType = audio.mimeType,
    audioSizeBytes = audio.sizeBytes,
    sourceFileName = audio.sourceFileName,
    remoteAudioPath = remoteAudioPath,
    processingStage = processing.stage,
    processingError = processing.error,
    transcribedUntilMs = processing.transcribedUntil.toMillis(),
    language = language,
    transcriptionEngine = transcriptionEngine,
    sync = sync,
)
