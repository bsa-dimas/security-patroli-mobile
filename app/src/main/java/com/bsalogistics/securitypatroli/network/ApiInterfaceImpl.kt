package com.bsalogistics.securitypatroli.network

import com.bsalogistics.securitypatroli.models.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ApiInterfaceImpl (
    private val apiInterface: NetworkService
) : Repository {

    override suspend fun login(loginBody: LoginBody): Flow<APIResponse<BaseResponse<User>>> {
        return apiInterface.login(loginBody).getApiResponse()
    }

    override suspend fun getListArea(): Flow<APIResponse<BaseResponse<MutableList<AreaFormTransaction>>>> {
        return apiInterface.getListArea().getApiResponse()
    }

    override suspend fun saveArea(areaBody: FormAreaBody): Flow<APIResponse<BaseResponse<AreaFormTransaction>>> {
        return apiInterface.saveArea(areaBody).getApiResponse()
    }

    override suspend fun saveAreaWithPhoto(image: MultipartBody): Flow<APIResponse<BaseResponse<AreaFormTransaction>>> {
        return apiInterface.saveAreaWithPhoto(image).getApiResponse()
    }

    override suspend fun findArea(areaName: String): Flow<APIResponse<BaseResponse<Area>>> {
        return apiInterface.findArea(areaName).getApiResponse()
    }

}