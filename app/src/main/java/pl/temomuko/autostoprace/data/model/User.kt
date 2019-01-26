package pl.temomuko.autostoprace.data.model

data class User(
    val id: Int,
    val teamNumber: Int,
    val firstName: String,
    val lastName: String,
    val email: String
) {

    val username: String
        get() = "$firstName $lastName"
}
