package com.bsalogistics.securitypatroli.network

import com.bsalogistics.securitypatroli.models.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface Repository {
    suspend fun login(loginBody: LoginBody): Flow<APIResponse<BaseResponse<User>>>
    suspend fun getListArea(): Flow<APIResponse<BaseResponse<MutableList<AreaFormTransaction>>>>
    suspend fun saveArea(areaBody: FormAreaBody): Flow<APIResponse<BaseResponse<AreaFormTransaction>>>
    suspend fun saveAreaWithPhoto(image: MultipartBody): Flow<APIResponse<BaseResponse<Boolean>>>
    suspend fun findArea(areaName: String): Flow<APIResponse<BaseResponse<Area>>>
    suspend fun areaDetail(areaId: String): Flow<APIResponse<BaseResponse<AreaDetail>>>
}