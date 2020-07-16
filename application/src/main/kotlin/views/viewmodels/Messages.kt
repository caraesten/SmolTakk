package views.viewmodels

import com.smoltakk.models.Message

data class Messages<T : Message>(val messages: List<T>, val header: Message? = null)