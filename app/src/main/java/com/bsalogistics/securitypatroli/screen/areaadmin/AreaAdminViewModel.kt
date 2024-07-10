package com.bsalogistics.securitypatroli.screen.areaadmin

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.APIStatus
import com.bsalogistics.securitypatroli.network.ApiInterfaceImpl
import com.bsalogistics.securitypatroli.network.Area
import com.bsalogistics.securitypatroli.network.AreaFormTransaction
import com.bsalogistics.securitypatroli.network.BaseResponse
import com.bsalogistics.securitypatroli.network.FormAddAreaBody
import com.bsalogistics.securitypatroli.network.FormAreaBody
import com.bsalogistics.securitypatroli.network.NetworkService
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaEvent
import com.bsalogistics.securitypatroli.screen.areasecurity.form.UiAreaFormEvent
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AreaAdminViewModel @Inject constructor(apiService: NetworkService, savedStateHandle: SavedStateHandle) : ViewModel() {
    val apiInterfaceImpl = ApiInterfaceImpl(apiInterface = apiService)

    private val _formAddArea = mutableStateOf<FormAddAreaBody>(FormAddAreaBody())
    val formAddArea: MutableState<FormAddAreaBody> get() = _formAddArea

    private val _areaList = MutableStateFlow<APIResponse<BaseResponse<MutableList<Area>>>>(
        APIResponse.Loading(null))
    val areaList: StateFlow<APIResponse<BaseResponse<MutableList<Area>>>> = _areaList

    private val _addArea = MutableStateFlow<APIResponse<BaseResponse<Area>>>(
        APIResponse.Error(null))
    val addArea: StateFlow<APIResponse<BaseResponse<Area>>> = _addArea

    private val _dropArea = MutableStateFlow<APIResponse<BaseResponse<Area>>>(
        APIResponse.Error(null))
    val dropArea: StateFlow<APIResponse<BaseResponse<Area>>> = _dropArea

    private val _eventAreaForm = MutableStateFlow<UiAreaAdminEvent>(UiAreaAdminEvent.Init)
    val eventAreaForm: StateFlow<UiAreaAdminEvent> = _eventAreaForm

    fun sendAreaFormEvent(event: UiAreaAdminEvent) {
        viewModelScope.launch {
            _eventAreaForm.value = event
        }
    }

    fun onEvent(event: AreaListAdminEvent) {
        when (event) {
            AreaListAdminEvent.GetList -> {
                viewModelScope.launch {
                    try {

                        apiInterfaceImpl.getListAreaAdmin()
                            .collect { res ->

                                when (res.status) {
                                    APIStatus.LOADING -> {

                                    }
                                    APIStatus.SUCCESS -> {
                                        res.data?.let {
                                            if (it.success) {
                                                _areaList.value = APIResponse.Success(it)
                                            }
                                        }
                                    }
                                    APIStatus.ERROR -> {
                                        _areaList.value = APIResponse.Error(errorMsg = res.errorMsg )
                                    }
                                }

                                Timber.tag("MYTAG").e("getListArea...${Gson().toJson(res)}")
                            }

                    } catch (ex: Exception) {

                        _areaList.value = APIResponse.Error(errorMsg = ex.localizedMessage )

                        Timber.tag("MYTAG").e("getListArea catch...${ex.localizedMessage}")
                    }
                }
            }
            AreaListAdminEvent.AddArea -> {
                _addArea.value = APIResponse.Loading(null)
                viewModelScope.launch {
                    try {

                        apiInterfaceImpl.addArea(formAddArea.value)
                            .collect { res ->

                                when (res.status) {
                                    APIStatus.LOADING -> {

                                    }
                                    APIStatus.SUCCESS -> {
                                        res.data?.let {
                                            if (it.success) {
                                                _addArea.value = APIResponse.Success(it)
                                            } else {
                                                _addArea.value = APIResponse.Error(errorMsg = res.data.message )
                                            }
                                        }
                                    }
                                    APIStatus.ERROR -> {
                                        _addArea.value = APIResponse.Error(errorMsg = res.errorMsg )
                                    }
                                }

                                Timber.tag("MYTAG").e("getListArea...${Gson().toJson(res)}")
                            }

                    } catch (ex: Exception) {

                        _addArea.value = APIResponse.Error(errorMsg = ex.localizedMessage )

                        Timber.tag("MYTAG").e("getListArea catch...${ex.localizedMessage}")
                    }
                }
            }
            is AreaListAdminEvent.DropArea -> {
                _dropArea.value = APIResponse.Loading(null)
                viewModelScope.launch {
                    try {

                        apiInterfaceImpl.dropArea(event.id)
                            .collect { res ->

                                when (res.status) {
                                    APIStatus.LOADING -> {

                                    }
                                    APIStatus.SUCCESS -> {
                                        res.data?.let {
                                            if (it.success) {
                                                _dropArea.value = APIResponse.Success(it)
                                            } else {
                                                _dropArea.value = APIResponse.Error(errorMsg = res.data.message )
                                            }
                                        }
                                    }
                                    APIStatus.ERROR -> {
                                        _dropArea.value = APIResponse.Error(errorMsg = res.errorMsg )
                                    }
                                }

                                Timber.tag("MYTAG").e("dropArea...${Gson().toJson(res)}")
                            }

                    } catch (ex: Exception) {

                        _dropArea.value = APIResponse.Error(errorMsg = ex.localizedMessage )

                        Timber.tag("MYTAG").e("dropArea catch...${ex.localizedMessage}")
                    }
                }
            }
            AreaListAdminEvent.OnErrorArea -> {
                _addArea.value = APIResponse.Error(null)
            }
            AreaListAdminEvent.OnSuccessArea -> {
                _addArea.value = APIResponse.Error(null)
//                sendAreaFormEvent(UiAreaAdminEvent.OnSuccessAddArea)
            }
            else -> {}
        }
    }

}