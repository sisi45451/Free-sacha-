package app.callnotes.core.data.di

import app.callnotes.core.data.repository.OfflineFirstRecordingRepository
import app.callnotes.core.domain.repository.RecordingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindRecordingRepository(impl: OfflineFirstRecordingRepository): RecordingRepository

    companion object {
        /** Horloge injectable : les tests peuvent fixer l'heure. */
        @Provides
        fun provideClock(): Clock = Clock.systemUTC()
    }
}
