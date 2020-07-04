package repositories

import io.ktor.config.ApplicationConfig
import models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import util.hashSha512
import java.time.LocalDateTime

interface UserRepository {
    fun createUser(newUserEmail: String, newUserUsername: String, newUserPassword: String): User?
    fun loginUser(email: String, password: String): User?
    fun findUserById(id: Int): User?
    fun findUserByAuthToken(authToken: String): User?
}

class UserRepositoryImpl(private val saltSecret: String, private val tokenSecret: String) : UserRepository {
    override fun createUser(newUserEmail: String, newUserUsername: String, newUserPassword: String): User? {
        val id = db.User.insertAndGetId {
            val timestamp = System.currentTimeMillis().toString()

            it[username] = newUserUsername
            it[email] = newUserEmail
            val saltString = (timestamp + newUserUsername + saltSecret).hashSha512()
            it[salt] = saltString
            it[hashedPassword] = (newUserPassword + saltSecret).hashSha512()
            it[authToken] = generateToken(newUserUsername, timestamp, tokenSecret)
            it[tokenIssued] = LocalDateTime.now()
        }
        return findUserById(id.value)
    }

    override fun loginUser(email: String, password: String): User? {
        val obj = db.User.select { db.User.email eq email }.firstOrNull()
        return obj?.let { row ->
            if (row[db.User.hashedPassword] == (password + saltSecret).hashSha512()) {
                val user = hydrateUser(row)
                val newToken = generateToken(user.username, System.currentTimeMillis().toString(), tokenSecret)
                db.User.update ({ db.User.email eq user.email}) {
                    it[authToken] = newToken
                    it[tokenIssued] = LocalDateTime.now()
                }
                user.copy(authToken = newToken)
            } else null
        }
    }

    override fun findUserById(id: Int): User? {
        val obj = db.User.select { db.User.id eq id }.firstOrNull()
        return obj?.let (::hydrateUser)
    }

    override fun findUserByAuthToken(authToken: String) = db.User.select { db.User.authToken eq authToken }.firstOrNull()?.let(::hydrateUser)

    private fun hydrateUser(row: ResultRow): User {
        return User(
            username = row[db.User.username],
            email = row[db.User.email],
            authToken = row[db.User.authToken],
            id = row[db.User.id].value
        )
    }

    private fun generateToken(username: String, timestamp: String, tokenSecret: String) = (username + timestamp + tokenSecret).hashSha512()
}