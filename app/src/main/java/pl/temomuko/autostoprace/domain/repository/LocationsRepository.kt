package pl.temomuko.autostoprace.domain.repository

import android.content.Context
import android.net.Uri
import okhttp3.MediaType
import pl.temomuko.autostoprace.domain.model.LocationRecord
import pl.temomuko.autostoprace.data.remote.AsrService
import pl.temomuko.autostoprace.data.remote.model.CreateLocationRequest
import pl.temomuko.autostoprace.data.remote.model.LocationEntity
import rx.Completable
import rx.Single
import javax.inject.Inject
import okhttp3.RequestBody
import okhttp3.MultipartBody
import pl.temomuko.autostoprace.injection.AppContext
import java.io.File

class LocationsRepository @Inject constructor(
    private val asrService: AsrService,
    @AppContext private val context: Context
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
                addImageToLocation(locationId = it.id, imageFileUri = locationRecord.imageUri)
                    .andThen(Single.just(it))
            }
    }

    private fun addImageToLocation(locationId: Long, imageFileUri: Uri): Completable {
        val openInputStream = context.contentResolver.openInputStream(imageFileUri)
        val imageFilePart = MultipartBody.Part.createFormData(
            "image",
            "image_for_$locationId",
            RequestBody.create(MediaType.parse("image/jpeg"), openInputStream?.readBytes() ?: byteArrayOf())
        )
        return asrService.addLocationImage(locationId, imageFilePart)
    }

    fun getTeamLocations(teamNumber: Long): Single<List<LocationRecord>> {
        return asrService.getTeamLocations(teamNumber)
            .map { locations -> locations.map { it.toLocationRecord() } }
    }

    fun getUserTeamLocations(): Single<List<LocationRecord>> {
        return asrService.getUserTeamLocations()
            .map { locations -> locations.map { it.toLocationRecord() }}
    }
}