package app.callnotes.core.security.di

import app.callnotes.core.security.DatabaseKeyProvider
import app.callnotes.core.security.KeystoreDatabaseKeyProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    abstract fun bindDatabaseKeyProvider(impl: KeystoreDatabaseKeyProvider): DatabaseKeyProvider
}
