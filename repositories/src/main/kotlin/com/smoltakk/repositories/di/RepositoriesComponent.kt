package com.smoltakk.repositories.di

import com.smoltakk.db.di.DaggerDatabaseComponent
import com.smoltakk.models.Configuration
import com.smoltakk.repositories.MessagesRepository
import com.smoltakk.repositories.MessagesRepositoryImpl
import com.smoltakk.repositories.UserRepository
import com.smoltakk.repositories.UserRepositoryImpl
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import org.jetbrains.exposed.sql.Database
import javax.inject.Singleton

@Component(modules=[RepositoriesModule::class])
@Singleton
interface RepositoriesComponent {

    fun userRepository(): UserRepository

    fun messagesRepository(): MessagesRepository

    @Component.Factory
    abstract class Factory {
        fun newRepositoriesComponent(config: Configuration): RepositoriesComponent {
            val dbComponent = DaggerDatabaseComponent.factory().newDatabaseComponent(
                config.dbUrl, config.dbUser, config.dbPassword)
            return newRepositoriesComponent(dbComponent.database(), config.saltSecret, config.tokenSecret)
        }

        internal abstract fun newRepositoriesComponent(@BindsInstance database: Database,
                                                       @BindsInstance @SaltSecret saltSecret: String,
                                                       @BindsInstance @TokenSecret tokenSecret: String): RepositoriesComponent
    }
}

@Module
interface RepositoriesModule {
    @Binds
    fun bindMessagesRepository(messagesRepositoryImpl: MessagesRepositoryImpl): MessagesRepository

    @Binds
    fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}