package repositories

import models.User
import org.jetbrains.exposed.sql.*
import util.hashSha512
import java.time.LocalDateTime

interface UserRepository {
    sealed class UserUpdateStatus() {
        class Success(val user: User) : UserUpdateStatus()
        abstract class Failure : UserUpdateStatus()
        object Invalid : Failure()
        object AlreadyExists : Failure()
    }

    fun createUser(newUserEmail: String, newUserUsername: String, newUserPassword: String): User?
    fun loginUser(email: String, password: String): User?
    fun getAllUsers(): List<User>
    fun findUserById(id: Int): User?
    fun findUserByUsername(username: String): User?
    fun findUserByAuthToken(authToken: String): User?
    fun updateUserProfile(user: User, username: String?, password: String?, email: String?): UserUpdateStatus
}

class UserRepositoryImpl(private val saltSecret: String, private val tokenSecret: String) : UserRepository {
    override fun createUser(newUserEmail: String, newUserUsername: String, newUserPassword: String): User? {
        val id = db.User.insertAndGetId {
            val timestamp = System.currentTimeMillis().toString()

            it[username] = newUserUsername
            it[email] = newUserEmail
            val saltString = generateSalt(timestamp, newUserUsername)
            it[salt] = saltString
            it[hashedPassword] = generateHashedPassword(saltString, newUserPassword)
            it[authToken] = generateToken(newUserUsername, timestamp)
            it[tokenIssued] = LocalDateTime.now()
        }
        return findUserById(id.value)
    }

    override fun loginUser(email: String, password: String): User? {
        val obj = db.User.select { db.User.email eq email }.firstOrNull()
        return obj?.let { row ->
            if (row[db.User.hashedPassword] == (password + saltSecret).hashSha512()) {
                val user = hydrateUser(row)
                val newToken = generateToken(user.username, System.currentTimeMillis().toString())
                db.User.update ({ db.User.email eq user.email}) {
                    it[authToken] = newToken
                    it[tokenIssued] = LocalDateTime.now()
                }
                user.copy(authToken = newToken)
            } else null
        }
    }

    override fun getAllUsers(): List<User> {
        return db.User.selectAll().map(::hydrateUser)
    }

    override fun findUserById(id: Int): User? {
        val obj = db.User.select { db.User.id eq id }.firstOrNull()
        return obj?.let (::hydrateUser)
    }

    override fun findUserByUsername(username: String): User? {
        val obj = db.User.select { db.User.username eq username }.firstOrNull()
        return obj?.let (::hydrateUser)
    }

    override fun findUserByAuthToken(authToken: String) = db.User.select { db.User.authToken eq authToken }.firstOrNull()?.let(::hydrateUser)

    override fun updateUserProfile(
        user: User,
        username: String?,
        password: String?,
        email: String?
    ): UserRepository.UserUpdateStatus {
        if (!username.isNullOrEmpty()) {
            val existingUser = findUserByUsername(username)
            if (existingUser != null) {
                return UserRepository.UserUpdateStatus.AlreadyExists
            }
        }
        val intermediateUser = user.copy(
            username = username ?: user.username,
            email = email ?: user.email)
        val timestamp = System.currentTimeMillis().toString()
        val hashedPassword = password?.let {
            generateHashedPassword(generateSalt(timestamp, intermediateUser.username), password)
        }
        val newToken = if (!username.isNullOrEmpty() || !hashedPassword.isNullOrEmpty()) {
            generateToken(intermediateUser.username, timestamp)
        } else {
            user.authToken
        }

        val newUser = intermediateUser.copy(authToken = newToken)

        val result = db.User.update({db.User.id eq user.id }) { dbUser ->
            dbUser[db.User.username] = newUser.username
            dbUser[db.User.email] = newUser.email
            if (!hashedPassword.isNullOrEmpty()) {
                dbUser[db.User.hashedPassword] = hashedPassword
                dbUser[db.User.authToken] = newToken
                dbUser[db.User.tokenIssued] = LocalDateTime.now()
            }
        }

        return if (result == 1) UserRepository.UserUpdateStatus.Success(newUser) else UserRepository.UserUpdateStatus.Invalid
    }

    private fun hydrateUser(row: ResultRow): User {
        return User(
            username = row[db.User.username],
            email = row[db.User.email],
            authToken = row[db.User.authToken],
            id = row[db.User.id].value
        )
    }

    private fun generateSalt(timestamp: String, newUserUsername: String) = (timestamp + newUserUsername + saltSecret).hashSha512()

    private fun generateHashedPassword(saltString: String, password: String) = (password + saltString).hashSha512()

    private fun generateToken(username: String, timestamp: String) = (username + timestamp + tokenSecret).hashSha512()
}