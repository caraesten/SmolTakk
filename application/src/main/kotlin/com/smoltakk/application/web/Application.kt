package com.smoltakk.application.web

import com.github.mustachejava.DefaultMustacheFactory
import com.smoltakk.application.di.DaggerApplicationComponent
import com.smoltakk.models.Configuration
import com.smoltakk.repositories.di.DaggerRepositoriesComponent
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.mustache.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun ApplicationConfig.createConfiguration() = Configuration(
    this.property("ktor.application.saltSecret").getString(),
    this.property("ktor.application.tokenSecret").getString(),
    this.property("ktor.db.jdbcUrl").getString(),
    this.property("ktor.db.jdbcUser").getString(),
    this.property("ktor.db.jdbcPassword").getString()
)

fun Application.main() {
    val appConfiguration = this.environment.config.createConfiguration()

    val reposComponent = DaggerRepositoriesComponent.factory().newRepositoriesComponent(appConfiguration)

    val applicationComponent = DaggerApplicationComponent.builder().setRepositoriesComponent(reposComponent).build()
    install(DefaultHeaders)
    install(CallLogging)
    install(Sessions) {
        cookie<SiteSession>("SESSION_COOKIE")
    }
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }
    install(Routing) { applicationComponent.router().configureRouting(this) }
}
