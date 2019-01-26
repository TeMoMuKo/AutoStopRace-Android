package pl.temomuko.autostoprace.data.remote.api

import retrofit2.Response
import retrofit2.http.*
import rx.Completable
import rx.Single

interface Asr2019Service {

    @GET("teams")
    fun getAllTeams(): Single<List<TeamEntity>>

    @GET("teams/{teamNumber}/locations")
    fun getTeamLocations(@Path("teamNumber") teamNumber: Long): Single<List<LocationEntity>>

    @GET("/locations")
    fun getUserTeamLocations(): Single<List<LocationEntity>>

    @GET("/user")
    fun authorize(@Header("Authorization") base64HeaderValue: String): Single<Response<UserEntity>>

    @GET("/user")
    fun validateToken(): Single<UserEntity>

    @POST("locations")
    fun addLocation(@Body createLocationRequest: CreateLocationRequest): Single<LocationEntity>

    //todo multipart
    @PUT("locations/{locationId}/image")
    fun addLocationImage(@Path("locationId") locationId: Int): Completable

    //todo implement on backend side
    @POST("user/password")
    fun resetPassword(email: String): Completable

    @GET("logout")
    fun logout(): Completable
}