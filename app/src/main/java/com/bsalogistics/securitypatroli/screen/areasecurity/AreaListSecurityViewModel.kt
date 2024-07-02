package com.bsalogistics.securitypatroli.screen.areasecurity


import android.R.attr.password
import android.content.Context
import android.graphics.Bitmap
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
import com.bsalogistics.securitypatroli.network.NetworkService
import com.bsalogistics.securitypatroli.screen.NavigationRoutes
import com.bsalogistics.securitypatroli.screen.UiEvent
import com.bsalogistics.securitypatroli.screen.areasecurity.form.UiAreaFormEvent
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File
import java.lang.String.format
import javax.inject.Inject


@HiltViewModel
class AreaListSecurityViewModel @Inject constructor(apiService: NetworkService, savedStateHandle: SavedStateHandle) : ViewModel() {
    val apiInterfaceImpl = ApiInterfaceImpl(apiInterface = apiService)

    private val _uri = mutableStateListOf<Uri>()
    val uri: List<Uri> get() = _uri

    private val _file = mutableStateOf<File?>(null)
    var file: MutableState<File?> = _file

    private val _event = Channel<UiEvent>()
    val event = _event.receiveAsFlow()

    private val _eventAreaForm = MutableStateFlow<UiAreaFormEvent>(UiAreaFormEvent.Init)
    val eventAreaForm: StateFlow<UiAreaFormEvent> = _eventAreaForm

    private val _areaFormTransactionScannedList = MutableStateFlow<APIResponse<BaseResponse<MutableList<AreaFormTransaction>>>>(APIResponse.Loading(null))
    val areaFormTransactionScannedList: StateFlow<APIResponse<BaseResponse<MutableList<AreaFormTransaction>>>> = _areaFormTransactionScannedList

    private val _checkAreaFormTransaction = MutableStateFlow<APIResponse<BaseResponse<Area>>>(APIResponse.Loading(null))
    val checkAreaFormTransaction: StateFlow<APIResponse<BaseResponse<Area>>> = _checkAreaFormTransaction

    private val _saveFormSecurity = MutableStateFlow<APIResponse<BaseResponse<AreaFormTransaction>>>(APIResponse.Error(null))
    val saveFormSecurity: StateFlow<APIResponse<BaseResponse<AreaFormTransaction>>> = _saveFormSecurity

    fun getListUri() : List<Uri>{
        return uri
    }

    fun addUri(uri: Uri) {
        _uri.add(uri)
    }

    fun removeUri(uri: Uri) {
        _uri.remove(uri)
    }

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun sendAreaFormEvent(event: UiAreaFormEvent) {
        viewModelScope.launch {
            _eventAreaForm.value = event
        }
    }

    fun onEvent(event: AreaEvent) {
        when (event) {
            AreaEvent.GetList -> {

                viewModelScope.launch {
                    try {

                        apiInterfaceImpl.getListArea()
                            .collect { res ->

                                when (res.status) {
                                    APIStatus.LOADING -> {

                                    }
                                    APIStatus.SUCCESS -> {
                                        res.data?.let {
                                            if (it.success) {
                                                _areaFormTransactionScannedList.value = APIResponse.Success(it)
                                            }
                                        }
                                    }
                                    APIStatus.ERROR -> {
                                        _areaFormTransactionScannedList.value = APIResponse.Error(errorMsg = res.errorMsg )
                                    }
                                }

                                Timber.tag("MYTAG").e("getListArea...${Gson().toJson(res)}")
                            }

                    } catch (ex: Exception) {

                        _areaFormTransactionScannedList.value = APIResponse.Error(errorMsg = ex.localizedMessage )

                        Timber.tag("MYTAG").e("getListArea catch...${ex.localizedMessage}")
                    }
                }
            }
            AreaEvent.GotoScan -> {
                sendEvent(UiEvent.Navigate(NavigationRoutes.Area.ScannerSecurityScreen.route))
            }
            AreaEvent.SignOut -> {
                sendEvent(UiEvent.SignOut)
            }

            is AreaEvent.CheckArea -> {

                viewModelScope.launch {
                    try {
                        apiInterfaceImpl.findArea(event.areaName)
                            .collect { res ->

                                when (res.status) {
                                    APIStatus.LOADING -> {

                                    }
                                    APIStatus.SUCCESS -> {
                                        res.data?.let {
                                            if (it.success) {
                                                _checkAreaFormTransaction.value = APIResponse.Success(it)
                                            } else {
                                                _checkAreaFormTransaction.value = APIResponse.Error(errorMsg = res.data.message )

                                            }
                                        }
                                    }
                                    APIStatus.ERROR -> {
                                        _checkAreaFormTransaction.value = APIResponse.Error(errorMsg = res.errorMsg )
                                    }
                                }

                                Timber.tag("MYTAG").e("getListArea...${Gson().toJson(res)}")
                            }

                    } catch (ex: Exception) {

                        _checkAreaFormTransaction.value = APIResponse.Error(errorMsg = ex.localizedMessage )

                        Timber.tag("MYTAG").e("findArea catch...${ex.localizedMessage}")
                    }
                }

            }

            AreaEvent.onClickErrorScanArea -> {
                _checkAreaFormTransaction.value = APIResponse.Error(null)
                sendEvent(UiEvent.NavigateBack)
            }

            AreaEvent.onClickErrorSaveArea -> {
                _saveFormSecurity.value = APIResponse.Error(null)
            }

            AreaEvent.onClickSuccessSaveArea -> {
                _saveFormSecurity.value = APIResponse.Error(null)
            }

            is AreaEvent.SaveFormSecurity -> {
                _saveFormSecurity.value = APIResponse.Loading(null)
                viewModelScope.launch {
                    try {

                        apiInterfaceImpl.saveArea( areaBody = event.areaBody )
                            .collect { res ->

                                when (res.status) {
                                    APIStatus.LOADING -> {

                                    }
                                    APIStatus.SUCCESS -> {
                                        res.data?.let {
                                            if (it.success) {
                                                _saveFormSecurity.value = APIResponse.Success(it)
                                            } else {
                                                _saveFormSecurity.value = APIResponse.Error(errorMsg = res.data.message )
                                            }
                                        }
                                    }
                                    APIStatus.ERROR -> {
                                        _saveFormSecurity.value = APIResponse.Error(errorMsg = res.errorMsg )
                                    }
                                }

                                Timber.tag("MYTAG").e("getListArea...${Gson().toJson(res)}")
                            }

                    } catch (ex: Exception) {

                        _saveFormSecurity.value = APIResponse.Error(errorMsg = ex.localizedMessage )

                        Timber.tag("MYTAG").e("getListArea catch...${ex.localizedMessage}")
                    }
                }
            }

            is AreaEvent.SaveFormSecurityWithPhoto -> {
                _saveFormSecurity.value = APIResponse.Loading(null)
                viewModelScope.launch {
                    try {

                        apiInterfaceImpl.saveAreaWithPhoto(image = event.photos)
                            .collect { res ->

                                when (res.status) {
                                    APIStatus.LOADING -> {

                                    }
                                    APIStatus.SUCCESS -> {
                                        res.data?.let {
                                            if (it.success) {
                                                _saveFormSecurity.value = APIResponse.Success(it)
                                            } else {
                                                _saveFormSecurity.value = APIResponse.Error(errorMsg = res.data.message )
                                            }
                                        }
                                    }
                                    APIStatus.ERROR -> {
                                        _saveFormSecurity.value = APIResponse.Error(errorMsg = res.errorMsg )
                                    }
                                }

                                Timber.tag("MYTAG").e("getListArea...${Gson().toJson(res)}")
                            }

                    } catch (ex: Exception) {

                        _saveFormSecurity.value = APIResponse.Error(errorMsg = ex.localizedMessage )

                        Timber.tag("MYTAG").e("getListArea catch...${ex.localizedMessage}")
                    }
                }
            }

            else -> {}
        }
    }

}