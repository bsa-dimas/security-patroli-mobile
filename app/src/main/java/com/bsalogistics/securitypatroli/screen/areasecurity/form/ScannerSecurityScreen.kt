package com.bsalogistics.securitypatroli.screen.areasecurity.form

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bsalogistics.securitypatroli.component.AlertDialogModel
import com.bsalogistics.securitypatroli.component.AlertDialogType
import com.bsalogistics.securitypatroli.component.ListError
import com.bsalogistics.securitypatroli.component.ListLoading
import com.bsalogistics.securitypatroli.component.LoadingDialog
import com.bsalogistics.securitypatroli.component.MyAlertDialog
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.Area
import com.bsalogistics.securitypatroli.network.BaseResponse
import com.bsalogistics.securitypatroli.screen.NavigationRoutes
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaEvent
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaListSecurityViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.gson.Gson
import qrscanner.QrScanner
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerSecurityScreen(navController: NavController, viewModel: AreaListSecurityViewModel = hiltViewModel(), onSuccess : (String) -> Unit = {}, onFailed : @Composable (String) -> Unit = {}, onCancel : () -> Unit = {}) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val checkAreaState by viewModel.checkAreaFormTransaction.collectAsState(null)

//    BackHandler {
//        navController.navigateUp()
//        navController.currentBackStackEntry
//            ?.savedStateHandle
//            ?.set("areaName", null)
//        onCancel.invoke()
//    }


    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }

    when (checkAreaState) {
        is APIResponse.Error -> {
            (checkAreaState as (APIResponse.Error)).errorMsg?.let {

                openDialog.value = true

                MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = it, typeDialog = AlertDialogType.FAILURE), onConfirm = {
                    openDialog.value.not()
                    viewModel.resetStateCheckArea()
                    navController.navigateUp()
                } )
            }
        }
        is APIResponse.Loading -> {
            LoadingDialog("Checking area")
        }

        is APIResponse.Success -> {
            (checkAreaState as APIResponse.Success<BaseResponse<Area>>).data?.data?.id?.let {

                viewModel.resetStateCheckArea()

                onSuccess(it)
            }

        }
        null -> {}
    }

    if (cameraPermissionState.status.isGranted) {
        QrScanner(
            modifier = Modifier,
            flashlightOn = false,
            launchGallery = false,
            onCompletion = { areaName ->

                viewModel.onEvent(AreaEvent.FindAreaByName(areaName))

                Timber.tag("MYTAG").e("onCompletion result scan -> $areaName")
            },
            onGalleryCallBackHandler = {

            },
            onFailure = {
                navController.navigateUp()
                Timber.tag("MYTAG").e("onFailure result scan -> $it")
            }
        )
    } else if (cameraPermissionState.status.shouldShowRationale) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Camera Permission permanently denied", modifier = Modifier.align(Alignment.Center))
        }
    } else {
        SideEffect {
            cameraPermissionState.run { launchPermissionRequest() }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Text("No Camera Permission", modifier = Modifier.align(Alignment.Center))
        }
    }

}