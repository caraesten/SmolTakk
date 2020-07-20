package com.smoltakk.repositories.tools

import com.smoltakk.repositories.UserRepository

object UpdateUser : UserTools() {
    fun run(username: String, newUsername: String? = null, newEmail: String? = null, newPassword: String? = null) {
        val userRepository = getUserRepo()
        userRepository.withTransaction {
            val userToUpdate = userRepository.findUserByUsername(username)
            if (userToUpdate == null) {
                println("ERROR! Can't find user!")
            } else {
                val status = userRepository.updateUserProfile(
                    userToUpdate,
                    newUsername,
                    newPassword,
                    newEmail
                )
                if (status is UserRepository.UserUpdateStatus.Success) {
                    println("success")
                } else {
                    println("error! $status")
                }
            }
        }
    }
}