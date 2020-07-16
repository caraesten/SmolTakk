package web

import com.github.mustachejava.DefaultMustacheFactory
import com.smoltakk.db.DatabaseFactory
import controllers.*
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.mustache.Mustache
import io.ktor.routing.Routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import repositories.MessagesRepository
import repositories.MessagesRepositoryImpl
import repositories.UserRepository
import repositories.UserRepositoryImpl

fun Application.main() {
    // TODO: Move to DI
    val saltSecret = this.environment.config.propertyOrNull("ktor.application.saltSecret")?.getString() ?: ""
    val tokenSecret = this.environment.config.propertyOrNull("ktor.application.tokenSecret")?.getString() ?: ""

    val dbUrl = this.environment.config.propertyOrNull("ktor.db.jdbcUrl")?.getString()!!
    val dbUser = this.environment.config.propertyOrNull("ktor.db.jdbcUser")?.getString()!!
    val dbPassword = this.environment.config.propertyOrNull("ktor.db.jdbcPassword")?.getString()!!

    val database = DatabaseFactory(dbUrl, dbUser, dbPassword).init()

    val userRepo: UserRepository = UserRepositoryImpl(database, saltSecret, tokenSecret)
    val messagesRepo: MessagesRepository = MessagesRepositoryImpl(database, userRepo)

    val homePageController: HomePageController = HomePageControllerImpl(userRepo, messagesRepo)
    val adminController: AdminController = AdminControllerImpl(userRepo, messagesRepo)
    val loginController: LoginController = LoginControllerImpl(userRepo)
    val messagesController: MessagesController = MessagesControllerImpl(userRepo, messagesRepo)
    val profileController: ProfileController = ProfileControllerImpl(userRepo)

    val router: Router = Router(homePageController, messagesController, profileController, loginController, adminController)
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
