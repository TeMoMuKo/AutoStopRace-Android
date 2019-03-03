package pl.temomuko.autostoprace.data.remote.model

data class UserEntity(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val teamNumber: Long
)