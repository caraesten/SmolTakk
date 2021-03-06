package com.smoltakk.repositories

import com.smoltakk.models.User
import com.smoltakk.repositories.di.RepositorySingleton
import com.smoltakk.repositories.di.SaltSecret
import com.smoltakk.repositories.di.TokenSecret
import com.smoltakk.repositories.util.hashSha512
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime
import javax.inject.Inject
import com.smoltakk.db.User as DbUser

interface UserRepository : Repository {
    sealed class UserUpdateStatus() {
        class Success(val user: User) : UserUpdateStatus()
        abstract class Failure : UserUpdateStatus()
        object Invalid : Failure()
        object AlreadyExists : Failure()
    }

    fun createUser(newUserEmail: String, newUserUsername: String, newUserPassword: String): UserUpdateStatus
    fun loginUser(email: String, password: String): User?
    fun getAllUsers(): List<User>
    fun findUserById(id: Int): User?
    fun findUserByUsername(username: String): User?
    fun findUserByAuthToken(authToken: String): User?
    fun updateUserProfile(user: User,
                          username: String?,
                          password: String?,
                          email: String?,
                          titleTextColor: String?,
                          titleBackgroundColor: String?): UserUpdateStatus
}

@RepositorySingleton
class UserRepositoryImpl @Inject constructor(override val database: Database,
                                             @SaltSecret private val saltSecret: String,
                                             @TokenSecret private val tokenSecret: String) :
    UserRepository {
    override fun createUser(newUserEmail: String, newUserUsername: String, newUserPassword: String): UserRepository.UserUpdateStatus {
        val id = DbUser.insertAndGetId {
            val timestamp = System.currentTimeMillis().toString()

            it[username] = newUserUsername
            it[email] = newUserEmail
            val saltString = generateSalt(timestamp, newUserUsername)
            it[salt] = saltString
            it[hashedPassword] = generateHashedPassword(saltString, newUserPassword)
            it[authToken] = generateToken(newUserUsername, timestamp)
            it[tokenIssued] = LocalDateTime.now()
        }
        return findUserById(id.value)?.let { UserRepository.UserUpdateStatus.Success(it) } ?: UserRepository.UserUpdateStatus.Invalid
    }

    override fun loginUser(email: String, password: String): User? {
        val obj = DbUser.select { DbUser.email eq email }.firstOrNull()
        return obj?.let { row ->
            val salt = row[DbUser.salt]
            if (row[DbUser.hashedPassword] == generateHashedPassword(salt, password)) {
                hydrateUser(row)
            } else null
        }
    }

    override fun getAllUsers(): List<User> {
        return DbUser.selectAll().map(::hydrateUser)
    }

    override fun findUserById(id: Int): User? {
        val obj = DbUser.select { DbUser.id eq id }.firstOrNull()
        return obj?.let (::hydrateUser)
    }

    override fun findUserByUsername(username: String): User? {
        val obj = DbUser.select { DbUser.username eq username }.firstOrNull()
        return obj?.let (::hydrateUser)
    }

    override fun findUserByAuthToken(authToken: String) = DbUser.select { DbUser.authToken eq authToken }.firstOrNull()?.let(::hydrateUser)

    override fun updateUserProfile(
        user: User,
        username: String?,
        password: String?,
        email: String?,
        titleTextColor: String?,
        titleBackgroundColor: String?
    ): UserRepository.UserUpdateStatus {
        if (!username.isNullOrEmpty()) {
            val existingUser = findUserByUsername(username)
            if (existingUser != null) {
                return UserRepository.UserUpdateStatus.AlreadyExists
            }
        }

        val newTitleTextColor = if (!titleTextColor.isNullOrEmpty()) {
            try { titleTextColor.toLong(16); titleTextColor } catch(e: Exception) { null }
        } else { null }

        val newTitleBackgroundColor = if (!titleBackgroundColor.isNullOrEmpty()) {
            try { titleBackgroundColor.toLong(16); titleBackgroundColor } catch(e: Exception) { null }
        } else { null }

        val intermediateUser = user.copy(
            username = username ?: user.username,
            email = email ?: user.email,
            titleTextColor = newTitleTextColor ?: user.titleTextColor,
            titleBackgroundColor = newTitleBackgroundColor ?: user.titleBackgroundColor)
        val timestamp = System.currentTimeMillis().toString()
        val (hashedPassword, salt) = password?.let {
            val newSalt = generateSalt(timestamp, intermediateUser.username)
            Pair(generateHashedPassword(newSalt, password), newSalt)
        } ?: Pair(null, null)
        val newToken = if (!username.isNullOrEmpty() || !hashedPassword.isNullOrEmpty()) {
            generateToken(intermediateUser.username, timestamp)
        } else {
            user.authToken
        }

        val newUser = intermediateUser.copy(authToken = newToken)

        val result = DbUser.update({DbUser.id eq user.id }) { dbUser ->
            dbUser[DbUser.username] = newUser.username
            dbUser[DbUser.email] = newUser.email
            dbUser[DbUser.titleTextColor] = newUser.titleTextColor
            dbUser[DbUser.titleBackgroundColor] = newUser.titleBackgroundColor
            if (!hashedPassword.isNullOrEmpty()) {
                dbUser[DbUser.hashedPassword] = hashedPassword
                dbUser[DbUser.salt] = salt!!
            }
            dbUser[DbUser.authToken] = newToken
            dbUser[DbUser.tokenIssued] = LocalDateTime.now()
        }

        return if (result == 1) UserRepository.UserUpdateStatus.Success(
            newUser
        ) else UserRepository.UserUpdateStatus.Invalid
    }

    private fun hydrateUser(row: ResultRow): User {
        return User(
            email = row[DbUser.email],
            username = row[DbUser.username],
            authToken = row[DbUser.authToken],
            id = row[DbUser.id].value,
            titleTextColor = row[DbUser.titleTextColor],
            titleBackgroundColor = row[DbUser.titleBackgroundColor]
        )
    }

    private fun generateSalt(timestamp: String, newUserUsername: String) = (timestamp + newUserUsername + saltSecret).hashSha512()

    private fun generateHashedPassword(saltString: String, password: String) = (password + saltString).hashSha512()

    private fun generateToken(username: String, timestamp: String) = (username + timestamp + tokenSecret).hashSha512()
}