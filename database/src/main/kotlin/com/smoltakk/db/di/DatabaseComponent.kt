package com.smoltakk.db.di

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import org.jetbrains.exposed.sql.Database
import javax.inject.Qualifier
import javax.inject.Singleton

@Component(modules = [DatabaseModule::class])
@Singleton
interface DatabaseComponent {

    fun database(): Database

    @Component.Factory
    interface Factory {
        fun newDatabaseComponent(@BindsInstance @DbUrl dbUrl: String,
                                 @BindsInstance @DbUser dbUser: String,
                                 @BindsInstance @DbPassword dbPassword: String): DatabaseComponent
    }
}

@Module
internal class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@DbUrl dbUrl: String, @DbUser dbUser: String, @DbPassword dbPassword: String): Database {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = dbUrl
        config.username = dbUser
        config.password = dbPassword
        config.maximumPoolSize = 2
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return Database.connect(HikariDataSource(config))
    }
}

@Qualifier
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class DbUrl()

@Qualifier
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class DbUser()

@Qualifier
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class DbPassword()