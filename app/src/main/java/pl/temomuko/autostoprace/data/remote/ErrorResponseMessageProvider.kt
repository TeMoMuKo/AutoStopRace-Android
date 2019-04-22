package pl.temomuko.autostoprace.data.remote

import okhttp3.ResponseBody
import pl.temomuko.autostoprace.data.remote.model.ErrorResponseEntity
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class ErrorResponseMessageProvider @Inject constructor(
    private val retrofit: Retrofit
) {

    fun getErrorFromResponseBody(response: Response<*>): String? {
        return try {
            val errorBody = response.errorBody()
            return getErrorResponseConverter().convert(errorBody)?.errorMessage
        } catch (e: Exception) {
            null
        }
    }

    private fun getErrorResponseConverter(): Converter<ResponseBody, ErrorResponseEntity> {
        return retrofit.responseBodyConverter(
            ErrorResponseEntity::class.java,
            arrayOfNulls<Annotation>(0)
        )
    }
}
