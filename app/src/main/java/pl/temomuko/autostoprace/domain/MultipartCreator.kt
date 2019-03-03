package pl.temomuko.autostoprace.domain

import android.content.Context
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pl.temomuko.autostoprace.injection.AppContext
import javax.inject.Inject

class MultipartCreator @Inject constructor(
    @AppContext private val context: Context
) {

    fun createImageMultipartFromUri(uri: Uri): MultipartBody.Part {
        val openInputStream = context.contentResolver.openInputStream(uri)
        return MultipartBody.Part.createFormData(
            "file",
            "location_image",
            RequestBody.create(
                MediaType.parse("image/jpeg"),
                openInputStream?.readBytes() ?: byteArrayOf()
            )
        )
    }
}
