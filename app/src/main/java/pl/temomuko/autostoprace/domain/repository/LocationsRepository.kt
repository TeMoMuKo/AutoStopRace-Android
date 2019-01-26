package pl.temomuko.autostoprace.domain.repository

import android.content.Context
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pl.temomuko.autostoprace.data.remote.AsrService
import pl.temomuko.autostoprace.data.remote.model.CreateLocationRequest
import pl.temomuko.autostoprace.data.remote.model.LocationEntity
import pl.temomuko.autostoprace.domain.model.LocationRecord
import rx.Completable
import rx.Single
import javax.inject.Inject

class LocationsRepository @Inject constructor(
    private val asrService: AsrService,
    private val multipartCreator: MultipartCreator
) {

    fun postLocation(locationRecord: LocationRecord): Single<LocationEntity> {
        val createLocationRequest =
            CreateLocationRequest(
                latitude = locationRecord.latitude,
                longitude = locationRecord.longitude,
                message = locationRecord.message
            )
        return asrService.addLocation(createLocationRequest)
            .flatMap {
                addImageToLocation(locationId = it.id, imageUri = locationRecord.imageUri)
                    .andThen(Single.just(it))
            }
    }

    private fun addImageToLocation(locationId: Long, imageUri: Uri): Completable {
        val imageMultipart = multipartCreator.createImageMultipartFromUri(imageUri)
        return asrService.addLocationImage(locationId, imageMultipart)
    }

    fun getTeamLocations(teamNumber: Long): Single<List<LocationRecord>> {
        return asrService.getTeamLocations(teamNumber)
            .map { locations -> locations.map { it.toLocationRecord() } }
    }

    fun getUserTeamLocations(): Single<List<LocationRecord>> {
        return asrService.getUserTeamLocations()
            .map { locations -> locations.map { it.toLocationRecord() } }
    }
}

class MultipartCreator @Inject constructor(
    private val context: Context
) {

    fun createImageMultipartFromUri(uri: Uri): MultipartBody.Part {
        val openInputStream = context.contentResolver.openInputStream(uri)
        return MultipartBody.Part.createFormData(
            "image",
            null,
            RequestBody.create(
                MediaType.parse("image/jpeg"), openInputStream?.readBytes() ?: byteArrayOf()
            )
        )
    }
}
