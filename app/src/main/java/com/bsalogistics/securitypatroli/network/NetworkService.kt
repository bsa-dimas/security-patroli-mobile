package com.bsalogistics.securitypatroli.network

import com.bsalogistics.securitypatroli.models.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {

    @POST("login")
    suspend fun login(@Body loginBody: LoginBody): Response<BaseResponse<User>>

    @GET("get-area-scanned")
    suspend fun getListArea(): Response<BaseResponse<MutableList<AreaFormTransaction>>>

    @GET("area")
    suspend fun getListAreaAdmin(): Response<BaseResponse<MutableList<Area>>>

    @POST("area")
    suspend fun addArea(@Body formArea: FormAddAreaBody): Response<BaseResponse<Area>>

    @POST("area/{id}")
    suspend fun dropArea(@Path("id") id: String): Response<BaseResponse<Area>>

    @POST("save-form-security")
    suspend fun saveArea(@Body formArea: FormAreaBody): Response<BaseResponse<AreaFormTransaction>>

    @POST("save-form-security")
    suspend fun saveAreaWithPhoto(
        @Body body: MultipartBody
    ): Response<BaseResponse<Boolean>>

    @POST("save-form-security")
    suspend fun saveAreaWithPhoto(
        @Body body: MultipartBody.Part
    ): Response<BaseResponse<AreaFormTransaction>>

    @GET("find-area-id")
    suspend fun findAreaById(@Query("id") id: String): Response<BaseResponse<Area>>

    @GET("find-area-name")
    suspend fun findAreaByName(@Query("name") name: String): Response<BaseResponse<Area>>

    @GET("get-area-detail")
    suspend fun areaDetail(@Query("areaId") areaId: String): Response<BaseResponse<AreaDetail>>

}