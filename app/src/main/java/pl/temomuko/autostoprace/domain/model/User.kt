package pl.temomuko.autostoprace.domain.model

data class User(
    val id: Long,
    val teamNumber: Long,
    val firstName: String,
    val lastName: String,
    val email: String
) {

    val username: String
        get() = "$firstName $lastName"
}
