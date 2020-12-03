package web

import com.github.mustachejava.DefaultMustacheFactory
import com.smoltakk.models.Configuration
import com.smoltakk.repositories.di.DaggerRepositoriesComponent
import controllers.*
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
    // TODO: Move to DI
    val appConfiguration = this.environment.config.createConfiguration()

    val reposComponent = DaggerRepositoriesComponent.factory().newRepositoriesComponent(appConfiguration)
    val userRepo = reposComponent.userRepository()
    val messagesRepo = reposComponent.messagesRepository()

    val homePageController: HomePageController = HomePageControllerImpl(userRepo, messagesRepo)
    val adminController: AdminController = AdminControllerImpl(userRepo, messagesRepo)
    val loginController: LoginController = LoginControllerImpl(userRepo)
    val messagesController: MessagesController = MessagesControllerImpl(userRepo, messagesRepo)
    val profileController: ProfileController = ProfileControllerImpl(userRepo)

    val router = Router(homePageController, messagesController, profileController, loginController, adminController)
    install(DefaultHeaders)
    install(CallLogging)
    install(Sessions) {
        cookie<SiteSession>("SESSION_COOKIE")
    }
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }
    install(Routing) { router.configureRouting(this) }
}
