package pl.temomuko.autostoprace.data.remote.model

data class CreateLocationRequest(
    val latitude: Double,
    val longitude: Double,
    val message: String?
)