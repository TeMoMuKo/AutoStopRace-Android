package pl.temomuko.autostoprace.data.remote.api

data class CreateLocationRequest(
    val latitude: Double,
    val longitude: Double,
    val message: String?
)