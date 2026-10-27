package app.callnotes.core.database.di

import android.content.Context
import androidx.room.Room
import app.callnotes.core.database.CallNotesDatabase
import app.callnotes.core.database.dao.AnalysisOutputDao
import app.callnotes.core.database.dao.ChatDao
import app.callnotes.core.database.dao.ContactDao
import app.callnotes.core.database.dao.FolderDao
import app.callnotes.core.database.dao.RecordingDao
import app.callnotes.core.database.dao.SearchDao
import app.callnotes.core.database.dao.SourceFileDao
import app.callnotes.core.database.dao.TodoDao
import app.callnotes.core.database.dao.TranscriptDao
import app.callnotes.core.security.DatabaseKeyProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Ouvre la base chiffrée. La phrase de passe est aléatoire, générée au
     * premier lancement et protégée par une clé matérielle Android Keystore :
     * elle ne quitte jamais le téléphone et n'est pas sauvegardée.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        keyProvider: DatabaseKeyProvider,
    ): CallNotesDatabase {
        // Bibliothèque native de SQLCipher, à charger avant toute ouverture.
        System.loadLibrary("sqlcipher")
        val factory = SupportOpenHelperFactory(keyProvider.getOrCreatePassphrase())
        return Room.databaseBuilder(context, CallNotesDatabase::class.java, CallNotesDatabase.NAME)
            .openHelperFactory(factory)
            .build()
    }

    @Provides fun recordingDao(db: CallNotesDatabase): RecordingDao = db.recordingDao()
    @Provides fun transcriptDao(db: CallNotesDatabase): TranscriptDao = db.transcriptDao()
    @Provides fun todoDao(db: CallNotesDatabase): TodoDao = db.todoDao()
    @Provides fun contactDao(db: CallNotesDatabase): ContactDao = db.contactDao()
    @Provides fun folderDao(db: CallNotesDatabase): FolderDao = db.folderDao()
    @Provides fun analysisOutputDao(db: CallNotesDatabase): AnalysisOutputDao = db.analysisOutputDao()
    @Provides fun chatDao(db: CallNotesDatabase): ChatDao = db.chatDao()
    @Provides fun searchDao(db: CallNotesDatabase): SearchDao = db.searchDao()
    @Provides fun sourceFileDao(db: CallNotesDatabase): SourceFileDao = db.sourceFileDao()
}
