package controllers

import io.ktor.application.ApplicationCall
import io.ktor.http.Parameters
import io.ktor.request.ContentTransformationException
import io.ktor.request.receiveParameters
import com.smoltakk.repositories.Repository

interface Controller {
    fun <T> withTransaction(repo: Repository, block: () -> T): T {
        return repo.withTransaction { block() }
    }
}

suspend fun ApplicationCall.getParametersOrNull(): Parameters? {
    return try {
        this.receiveParameters()
    } catch (e: ContentTransformationException) { null }
}