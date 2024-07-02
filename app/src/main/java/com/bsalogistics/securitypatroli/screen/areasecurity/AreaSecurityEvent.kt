package com.bsalogistics.securitypatroli.screen.areasecurity

import android.net.Uri
import com.bsalogistics.securitypatroli.network.FormAreaBody
import okhttp3.MultipartBody

sealed interface AreaEvent {
    data object GetList : AreaEvent
    data object SignOut : AreaEvent
    data object GotoScan : AreaEvent
    data class CheckArea(val areaName: String) : AreaEvent
    data object onClickErrorScanArea : AreaEvent
    data object onClickErrorSaveArea : AreaEvent
    data object onClickSuccessSaveArea : AreaEvent
    data class SaveFormSecurity(val areaBody: FormAreaBody) : AreaEvent
    data class SaveFormSecurityWithPhoto(val photos: MultipartBody) : AreaEvent

}