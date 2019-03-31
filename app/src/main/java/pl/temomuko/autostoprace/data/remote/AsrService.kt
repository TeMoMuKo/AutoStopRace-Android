package pl.temomuko.autostoprace.data.remote

import okhttp3.MultipartBody
import pl.temomuko.autostoprace.data.remote.model.CreateLocationRequest
import pl.temomuko.autostoprace.data.remote.model.LocationEntity
import pl.temomuko.autostoprace.data.remote.model.TeamEntity
import pl.temomuko.autostoprace.data.remote.model.UserEntity
import retrofit2.Response
import retrofit2.http.*
import rx.Completable
import rx.Single

interface AsrService {

    @GET("teams")
    fun getAllTeams(): Single<List<TeamEntity>>

    @GET("teams/{teamNumber}/locations")
    fun getTeamLocations(@Path("teamNumber") teamNumber: Long): Single<List<LocationEntity>>

    @GET("user/team/locations")
    fun getUserTeamLocations(): Single<List<LocationEntity>>

    @GET("user")
    fun authorize(@Header("Authorization") base64HeaderValue: String): Single<Response<UserEntity>>

    @GET("user")
    fun validateToken(): Single<UserEntity>

    @Multipart
    @POST("locations")
    fun addLocation(
        @Part("locationData") locationRequest: CreateLocationRequest,
        @Part image: MultipartBody.Part?
    ): Single<LocationEntity>

    //todo implement on backend side
    @POST("user/password")
    fun resetPassword(email: String): Completable

    @POST("logout")
    fun logout(): Completable
}