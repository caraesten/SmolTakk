package web

import com.github.mustachejava.DefaultMustacheFactory
import controllers.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.mustache.Mustache
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import repositories.MessagesRepository
import repositories.MessagesRepositoryImpl
import repositories.UserRepository
import repositories.UserRepositoryImpl

fun Application.main() {
    // TODO: Move to DI
    val saltSecret = this.environment.config.propertyOrNull("ktor.application.saltSecret")?.getString() ?: ""
    val tokenSecret = this.environment.config.propertyOrNull("ktor.application.tokenSecret")?.getString() ?: ""

    val userRepo: UserRepository = UserRepositoryImpl(saltSecret, tokenSecret)
    val messagesRepo: MessagesRepository = MessagesRepositoryImpl(userRepo)

    val homePageController: HomePageController = HomePageControllerImpl()
    val adminController: AdminController = AdminControllerImpl(userRepo)
    val loginController: LoginController = LoginControllerImpl()
    val messagesController: MessagesController = MessagesControllerImpl(userRepo, messagesRepo)
    val profileController: ProfileController = ProfileControllerImpl(userRepo)

    val router: Router = Router(homePageController, messagesController, profileController, loginController, adminController)
    install(DefaultHeaders)
    install(CallLogging)
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }
    install(Routing) { router.configureRouting(this) }
}
