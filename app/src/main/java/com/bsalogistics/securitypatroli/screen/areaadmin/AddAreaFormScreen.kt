package com.bsalogistics.securitypatroli.screen.areaadmin

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bsalogistics.securitypatroli.R
import com.bsalogistics.securitypatroli.component.AlertDialogModel
import com.bsalogistics.securitypatroli.component.AlertDialogType
import com.bsalogistics.securitypatroli.component.ButtonType
import com.bsalogistics.securitypatroli.component.LoadingDialog
import com.bsalogistics.securitypatroli.component.MyAlertDialog
import com.bsalogistics.securitypatroli.component.MyButton
import com.bsalogistics.securitypatroli.component.MyToolbar
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.Area
import com.bsalogistics.securitypatroli.network.FormAddAreaBody
import com.bsalogistics.securitypatroli.screen.NavigationRoutes
import com.bsalogistics.securitypatroli.utils.PreferencesManager
import com.bsalogistics.securitypatroli.utils.ReqLocationPermission
import com.google.gson.Gson
import timber.log.Timber

@Composable
fun AddAreaFormScreen(navController: NavController, areaGson: String?, viewModel: AreaAdminViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val user = PreferencesManager(context).getDataUser()
    val eventAreaFormState by viewModel.eventAreaForm.collectAsState(null)
    val addAreaState by viewModel.addArea.collectAsState(null)

    val area : Area? = Gson().fromJson(areaGson ?: "", Area::class.java)

    val checkPermission = rememberSaveable {
        mutableStateOf(false)
    }

    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val msg = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    msg.value?.let {
        LoadingDialog(message = msg.value)
    }

    /**
     * Check Permission
     */
    ReqLocationPermission(
        onPermissionGranted = {
            Timber.tag("MYTAG").e("onPermissionGranted")
            checkPermission.value = true
        },
        onPermissionDenied = {
            Timber.tag("MYTAG").e("onPermissionDenied")
        }) {
        Timber.tag("MYTAG").e("onPermissionsRevoked")
    }

    when(eventAreaFormState) {
        UiAreaAdminEvent.OnSuccessAddArea -> {

        }
        UiAreaAdminEvent.OnErrorArea -> {
            openDialog.value = true

            MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Error", typeDialog = AlertDialogType.FAILURE), onConfirm = {
                openDialog.value = false
            } )
        }
        else -> {}
    }

    when (addAreaState) {
        is APIResponse.Error -> {
            (addAreaState as (APIResponse.Error)).errorMsg?.let {

                openDialog.value = true

                MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = it, typeDialog = AlertDialogType.FAILURE), onConfirm = {
                    openDialog.value = false
                    viewModel.onEvent(AreaListAdminEvent.OnErrorArea)
                } )
            }
        }
        is APIResponse.Loading -> {
            LoadingDialog("Simpan data")
        }

        is APIResponse.Success -> {

            addAreaState?.data?.let {
                openDialog.value = true
                msg.value = null

                MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = it.message ?: "Success", typeDialog = AlertDialogType.SUCCESS), onConfirm = {
                    openDialog.value = false
                    navController.navigateUp()
                    viewModel.onEvent(AreaListAdminEvent.OnSuccessArea)

                } )

            }
        }
        null -> {}
    }

    Column(modifier = Modifier
        .safeDrawingPadding()
        .fillMaxSize()) {

        MyToolbar(navController = navController, title = "Tambah Area")

        Box(modifier = Modifier.padding(20.dp)) {
            AreaInput(checkAccessLocation = checkPermission.value, onClickScanner = {

                navController.navigate(route = NavigationRoutes.Area.ScannerAddAreaScreen.route)

            }, areaName = area?.name, onClickSave = {

                viewModel.formAddArea.value = FormAddAreaBody(name = area!!.name, latitude = area.latitude, longitude = area.longitude, userid = user.userid)

                viewModel.onEvent(AreaListAdminEvent.AddArea)

            })
        }


    }
}


@Composable
fun AreaInput(onClickScanner : () -> Unit = {}, checkAccessLocation : Boolean = false, onClickSave : () -> Unit = {}, areaName: String? = null) {

    Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_default_form)), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
            .clickable {
                onClickScanner.invoke()
            }
            , contentAlignment = Alignment.Center)
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Timber.tag("MYTAG").e("areaName $areaName")

                if (areaName.isNullOrBlank()) {
                    AreaNotScanned(checkAccessLocation = checkAccessLocation)
                } else {
                    AreaScannedForSave(areaName = areaName)
                }
            }
        }

        MyButton(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
            enabled = !areaName.isNullOrBlank(),
            text = "Simpan",
            buttonType = ButtonType.PRIMARY, onClick = {
                onClickSave.invoke()
            })

    }
}

@Composable
fun AreaNotScanned(checkAccessLocation : Boolean = false) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = "Camera", modifier = Modifier.size(100.dp), tint = Color.Gray)

            Text(text = "Open Scanner \nuntuk menambahkan titik Area",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                textAlign = TextAlign.Center
            )

            if (!checkAccessLocation) {
                Text(text = "Akses lokasi belum diizinkan!",
                    modifier = Modifier,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AreaScannedForSave(areaName : String = "Contoh") {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.LocationOn, contentDescription = "Camera", modifier = Modifier.size(100.dp), tint = Color.Gray)

            Text(
                buildAnnotatedString {
                    append("Area ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(areaName)
                    }
                    append(" sudah berhasil discan, Silahkan Simpan Data untuk melanjutkan proses")
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
