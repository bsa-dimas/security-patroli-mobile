package com.bsalogistics.securitypatroli.screen.areasecurity.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.APIStatus
import com.bsalogistics.securitypatroli.network.ApiInterfaceImpl
import com.bsalogistics.securitypatroli.network.AreaDetail
import com.bsalogistics.securitypatroli.network.AreaFormTransaction
import com.bsalogistics.securitypatroli.network.BaseResponse
import com.bsalogistics.securitypatroli.network.NetworkService
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaDetailEvent
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaEvent
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AreaFormDetailViewModel @Inject constructor(apiService: NetworkService) : ViewModel() {
    val apiInterfaceImpl = ApiInterfaceImpl(apiInterface = apiService)

    private val _areaDetail = MutableStateFlow<APIResponse<BaseResponse<AreaDetail>>>(
        APIResponse.Loading(null))
    val areaDetail: StateFlow<APIResponse<BaseResponse<AreaDetail>>> = _areaDetail

    fun onEvent(event: AreaDetailEvent) {
        when (event) {
            is AreaDetailEvent.GetAreaDetail -> {
                viewModelScope.launch {
                    try {

                        apiInterfaceImpl.areaDetail(event.areaId)
                            .collect { res ->

                                when (res.status) {
                                    APIStatus.LOADING -> {

                                    }
                                    APIStatus.SUCCESS -> {
                                        res.data?.let {
                                            if (it.success) {
                                                _areaDetail.value = APIResponse.Success(it)
                                            }
                                        }
                                    }
                                    APIStatus.ERROR -> {
                                        _areaDetail.value = APIResponse.Error(errorMsg = res.errorMsg )
                                    }
                                }

                                Timber.tag("MYTAG").e("GetAreaDetail...${Gson().toJson(res)}")
                            }

                    } catch (ex: Exception) {

                        _areaDetail.value = APIResponse.Error(errorMsg = ex.localizedMessage )

                        Timber.tag("MYTAG").e("GetAreaDetail catch...${ex.localizedMessage}")
                    }
                }
            }
        }
    }


}