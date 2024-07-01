package com.bsalogistics.securitypatroli.network

import com.bsalogistics.securitypatroli.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NetworkService {

    @POST("login")
    suspend fun login(@Body loginBody: LoginBody): Response<BaseResponse<User>>

    @GET("get-area-scanned")
    suspend fun getListArea(): Response<BaseResponse<MutableList<AreaFormTransaction>>>

    @POST("save-form-security")
    suspend fun saveArea(@Body formArea: FormAreaBody): Response<BaseResponse<AreaFormTransaction>>

    @GET("find-area")
    suspend fun findArea(@Query("name") areaName: String): Response<BaseResponse<Area>>

}