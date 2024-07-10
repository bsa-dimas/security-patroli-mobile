package com.bsalogistics.securitypatroli.network

import com.bsalogistics.securitypatroli.models.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

class ApiInterfaceImpl (
    private val apiInterface: NetworkService
) : Repository {

    override suspend fun login(loginBody: LoginBody): Flow<APIResponse<BaseResponse<User>>> {
        return apiInterface.login(loginBody).getApiResponse()
    }

    override suspend fun getListArea(): Flow<APIResponse<BaseResponse<MutableList<AreaFormTransaction>>>> {
        return apiInterface.getListArea().getApiResponse()
    }

    override suspend fun getListAreaAdmin(): Flow<APIResponse<BaseResponse<MutableList<Area>>>> {
        return apiInterface.getListAreaAdmin().getApiResponse()
    }

    override suspend fun saveArea(areaBody: FormAreaBody): Flow<APIResponse<BaseResponse<AreaFormTransaction>>> {
        return apiInterface.saveArea(areaBody).getApiResponse()
    }

    override suspend fun addArea(areaBody: FormAddAreaBody): Flow<APIResponse<BaseResponse<Area>>> {
        return apiInterface.addArea(areaBody).getApiResponse()
    }

    override suspend fun dropArea(id: String): Flow<APIResponse<BaseResponse<Area>>> {
        return apiInterface.dropArea(id).getApiResponse()
    }

    override suspend fun saveAreaWithPhoto(image: MultipartBody): Flow<APIResponse<BaseResponse<Boolean>>> {
        return apiInterface.saveAreaWithPhoto(image).getApiResponse()
    }

    override suspend fun findAreaById(id: String): Flow<APIResponse<BaseResponse<Area>>> {
        return apiInterface.findAreaById(id).getApiResponse()
    }

    override suspend fun findAreaByName(name: String): Flow<APIResponse<BaseResponse<Area>>> {
        return apiInterface.findAreaByName(name).getApiResponse()
    }

    override suspend fun areaDetail(areaId: String): Flow<APIResponse<BaseResponse<AreaDetail>>> {
        return apiInterface.areaDetail(areaId).getApiResponse()
    }

}