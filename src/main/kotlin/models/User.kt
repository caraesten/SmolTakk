package models

data class User(
    val email: String,
    val username: String,
    val authToken: String,
    val id: Int) {

    companion object {
        fun getEmptyUser(): User = User("", "", "", -1)
    }
}
