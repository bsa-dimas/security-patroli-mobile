package com.bsalogistics.securitypatroli.network

import com.bsalogistics.securitypatroli.models.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface Repository {
    suspend fun login(loginBody: LoginBody): Flow<APIResponse<BaseResponse<User>>>
    suspend fun getListArea(): Flow<APIResponse<BaseResponse<MutableList<AreaFormTransaction>>>>
    suspend fun getListAreaAdmin(): Flow<APIResponse<BaseResponse<MutableList<Area>>>>
    suspend fun saveArea(areaBody: FormAreaBody): Flow<APIResponse<BaseResponse<AreaFormTransaction>>>
    suspend fun addArea(areaBody: FormAddAreaBody): Flow<APIResponse<BaseResponse<Area>>>
    suspend fun dropArea(id: String): Flow<APIResponse<BaseResponse<Area>>>
    suspend fun saveAreaWithPhoto(image: MultipartBody): Flow<APIResponse<BaseResponse<Boolean>>>
    suspend fun findAreaById(id: String): Flow<APIResponse<BaseResponse<Area>>>
    suspend fun findAreaByName(name: String): Flow<APIResponse<BaseResponse<Area>>>
    suspend fun areaDetail(areaId: String): Flow<APIResponse<BaseResponse<AreaDetail>>>
}