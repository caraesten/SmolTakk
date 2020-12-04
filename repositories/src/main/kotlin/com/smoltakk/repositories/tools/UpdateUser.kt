package com.smoltakk.repositories.tools

import com.smoltakk.repositories.UserRepository

object UpdateUser : UserTools() {
    fun run(username: String, newUsername: String? = null, newEmail: String? = null, newPassword: String? = null) {
        val userRepository = getUserRepo()
        userRepository.withTransaction {
            val userToUpdate = userRepository.findUserByUsername(username)
            val status = if (userToUpdate == null) {
                println("Creating user...")
                if (newEmail.isNullOrBlank() || newUsername.isNullOrBlank() || newPassword.isNullOrBlank()) {
                    println("ERROR: you need all fields to create a user!")
                    UserRepository.UserUpdateStatus.Invalid
                } else {
                    userRepository.createUser(newEmail, newUsername, newPassword)
                }
            } else {
                println("Updating user...")
                userRepository.updateUserProfile(
                    userToUpdate,
                    newUsername,
                    newPassword,
                    newEmail,
                    userToUpdate.titleTextColor,
                    userToUpdate.titleBackgroundColor
                )
            }
            if (status is UserRepository.UserUpdateStatus.Success) {
                println("success")
            } else {
                println("error! $status")
            }
        }
    }
}