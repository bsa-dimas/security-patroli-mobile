package com.bsalogistics.securitypatroli.screen.areasecurity

import com.bsalogistics.securitypatroli.network.AreaFormTransaction
import com.bsalogistics.securitypatroli.network.FormAreaBody
import okhttp3.MultipartBody

sealed interface AreaEvent {
    data object GetList : AreaEvent
    data object SignOut : AreaEvent
    data object GotoScan : AreaEvent
    data class FindAreaById(val id: String) : AreaEvent
    data class FindAreaByName(val name: String) : AreaEvent
    data object onClickErrorScanArea : AreaEvent
    data object onClickErrorSaveArea : AreaEvent
    data object onClickSuccessSaveArea : AreaEvent
    data class onClickArea(val area: AreaFormTransaction) : AreaEvent
    data class SaveFormSecurity(val areaBody: FormAreaBody) : AreaEvent
    data class SaveFormSecurityWithPhoto(val photos: MultipartBody) : AreaEvent
}

sealed interface AreaDetailEvent {
    data class GetAreaDetail(val areaId: String) : AreaDetailEvent
}