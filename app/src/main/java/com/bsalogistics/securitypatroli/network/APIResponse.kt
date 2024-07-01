package com.bsalogistics.securitypatroli.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

enum class APIStatus {
    LOADING, SUCCESS, ERROR
}

data class BaseResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("code") val code: Int? = null,
    @SerializedName("data") val data: T? = null,
)

data class ErrorMsg (
    var code : Int? = null,
    var errorMessage: String? = null
)

sealed class APIResponse<out T>(val status: APIStatus, val data: T?, val errorMsg: String?) {
    class Loading<T>(data: T? = null) : APIResponse<T>(APIStatus.LOADING, data, null)

    class Success<T>(data: T?) : APIResponse<T>(APIStatus.SUCCESS, data, null)

    class Error<T>(data: T? = null, errorMsg: String? = null) :
        APIResponse<T>(APIStatus.ERROR, data, errorMsg)
}

suspend fun <T> Response<T>?.getApiResponse(): Flow<APIResponse<T>> {
    try {
        return flow<APIResponse<T>> {
            emit(APIResponse.Loading())

            this@getApiResponse?.let { response ->
                if (response.isSuccessful && response.code() == 200) {
                    emit(APIResponse.Success(data = response.body()))
                } else {
                    try {
//                        val gson = Gson()
//                        val type = object : TypeToken<BaseResponse<T>>() {}.type
//                        val errorResponse: BaseResponse<T>? =
//                            gson.fromJson(response.errorBody()?.charStream(), type)

                        val errorMsg = ErrorMsg()
                        errorMsg.code = response.code()
                        errorMsg.errorMessage = response.message()

                        emit(APIResponse.Error(errorMsg = response.message()))
                    } catch (e: Exception) {
                        val errorMsg = ErrorMsg()
                        errorMsg.code = response.code()
                        errorMsg.errorMessage = e.message ?: response.message()

                        emit(APIResponse.Error(errorMsg = response.message()))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    } catch (ex: Exception) {
        return flow {
            emit(APIResponse.Error(errorMsg = ex.localizedMessage))
        }
    }
}