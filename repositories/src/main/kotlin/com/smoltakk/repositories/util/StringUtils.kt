package com.smoltakk.repositories.util

import java.security.MessageDigest

fun String.hashSha512(): String {
    val md = MessageDigest.getInstance("SHA-512")
    val digest = md.digest(this.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}
