package com.bsalogistics.securitypatroli.screen.areasecurity.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bsalogistics.securitypatroli.component.AlertDialogModel
import com.bsalogistics.securitypatroli.component.AlertDialogType
import com.bsalogistics.securitypatroli.component.ButtonType
import com.bsalogistics.securitypatroli.component.InputTextField
import com.bsalogistics.securitypatroli.component.LoadingDialog
import com.bsalogistics.securitypatroli.component.MyAlertDialog
import com.bsalogistics.securitypatroli.component.MyButton
import com.bsalogistics.securitypatroli.component.MyToolbar
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.Area
import com.bsalogistics.securitypatroli.network.AreaFormTransaction
import com.bsalogistics.securitypatroli.network.FormAreaBody
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaEvent
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaListSecurityViewModel
import com.bsalogistics.securitypatroli.utils.ReqLocationPermission
import com.bsalogistics.securitypatroli.utils.calcCrow
import com.bsalogistics.securitypatroli.utils.getCurrentLocationScanner
import com.bsalogistics.securitypatroli.utils.roundOffDecimal
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber

@Composable
fun AreaFormScreen(navController: NavController, viewModel: AreaListSecurityViewModel = hiltViewModel(), areaName: String) {

    val checkAreaState by viewModel.checkAreaFormTransaction.collectAsState(null)
    val saveAreaState by viewModel.saveFormSecurity.collectAsState(null)
    val eventAreaFormState by viewModel.eventAreaForm.collectAsState(null)

    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val areaFormTransactionModel = remember {
        mutableStateOf<Area>(Area())
    }

    when(eventAreaFormState) {
        UiAreaFormEvent.CheckingArea -> {}
        UiAreaFormEvent.OutOfArea -> {
            openDialog.value = true
            MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Gagal dalam range area!\nSilahkan bergeser dan coba lagi", typeDialog = AlertDialogType.CONFIRM),
                onConfirm = {
                    openDialog.value.not()
                    viewModel.sendAreaFormEvent(UiAreaFormEvent.TryGetLocation)
                },
                onDismiss = {
                    openDialog.value = false
                    viewModel.sendAreaFormEvent(UiAreaFormEvent.Init)
                }
            )
        }
        UiAreaFormEvent.TryGetLocation -> {}
        else -> {}
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(AreaEvent.CheckArea(areaName = areaName))
    }

    when (checkAreaState) {
        is APIResponse.Error -> {
            (checkAreaState as (APIResponse.Error)).errorMsg?.let {

                openDialog.value = true

                MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = it, typeDialog = AlertDialogType.FAILURE), onConfirm = {
                    openDialog.value.not()
                    viewModel.onEvent(AreaEvent.onClickErrorScanArea)
                } )
            }
        }
        is APIResponse.Loading -> {
            LoadingDialog("Checking area")
        }

        is APIResponse.Success -> {

            checkAreaState?.data?.data?.let {
                areaFormTransactionModel.value = it
            }

            Timber.tag("MYTAG").e("Success")
        }
        null -> {}
    }

    when (saveAreaState) {
        is APIResponse.Error -> {
            (saveAreaState as (APIResponse.Error)).errorMsg?.let {

                openDialog.value = true

                MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = it, typeDialog = AlertDialogType.FAILURE), onConfirm = {
                    openDialog.value.not()
                    viewModel.onEvent(AreaEvent.onClickErrorSaveArea)
                } )
            }
        }
        is APIResponse.Loading -> {
            LoadingDialog("Simpan data")
        }

        is APIResponse.Success -> {

            openDialog.value = true

            MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = (saveAreaState as APIResponse.Success).data?.message ?: "Success", typeDialog = AlertDialogType.SUCCESS), onConfirm = {
                openDialog.value.not()
                viewModel.onEvent(AreaEvent.onClickSuccessSaveArea)
                navController.navigateUp()
            } )

            Timber.tag("MYTAG").e("Success")
        }
        null -> {}
    }

    AreaInput(navController = navController, areaFormTransaction = areaFormTransactionModel.value)

}

@Composable
private fun AreaInput(navController: NavController, areaFormTransaction: Area, viewModel: AreaListSecurityViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    val checkPermission = rememberSaveable {
        mutableStateOf(false)
    }

    val latitude = rememberSaveable {
        mutableDoubleStateOf(0.0)
    }

    val longitude = rememberSaveable {
        mutableDoubleStateOf(0.0)
    }

    val keterangan = rememberSaveable {
        mutableStateOf("")
    }

    viewModel.eventAreaForm.collectAsState().value.let { state ->
        if (state is UiAreaFormEvent.TryGetLocation) {
            getCurrentLocationScanner(
                onGetCurrentLocationSuccess = {

                    latitude.doubleValue = it.first
                    longitude.doubleValue = it.second

                    /**
                     * Function save
                     */
                    val selisih = calcCrow(latitude.doubleValue, longitude.doubleValue, areaFormTransaction.latitude, areaFormTransaction.longitude)
                    Timber.tag("MYTAG").e("Selisih ${roundOffDecimal(selisih)}")
                    if (selisih < 10.0) {
                        viewModel.onEvent(AreaEvent.SaveFormSecurity(areaBody = FormAreaBody(
                            userid = "admin@admin.com", area = areaFormTransaction.name, latitude_actual = latitude.doubleValue, longitude_actual = longitude.doubleValue, keterangan = keterangan.value
                        ) ) )
                    } else {
                        viewModel.sendAreaFormEvent(UiAreaFormEvent.OutOfArea)
                    }

                    Timber
                        .tag("MYTAG")
                        .e("Try Again onGetCurrentLocationSuccess LATITUDE: ${it.first}, LONGITUDE: ${it.second})}")
                },
                onGetCurrentLocationFailed = {

                    Timber
                        .tag("MYTAG")
                        .e("Try Again onGetCurrentLocationFailed ${it.localizedMessage}")
                }, fusedLocationProviderClient = fusedLocationProviderClient, context = context
            )
        }
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

    LaunchedEffect(key1 = checkPermission) {
        if (checkPermission.value) {
            getCurrentLocationScanner(
                onGetCurrentLocationSuccess = {

                    latitude.doubleValue = it.first
                    longitude.doubleValue = it.second

                    Timber
                        .tag("MYTAG")
                        .e("onGetCurrentLocationSuccess LATITUDE: ${it.first}, LONGITUDE: ${it.second})}")
                },
                onGetCurrentLocationFailed = {

                    Timber
                        .tag("MYTAG")
                        .e("onGetCurrentLocationFailed ${it.localizedMessage}")
                }, fusedLocationProviderClient = fusedLocationProviderClient, context = context
            )
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding()) {

        MyToolbar(navController = navController, title = "Patroli Area")

        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            AreaScan(lokasi = areaFormTransaction.name)

            BoxPhoto()

            InputTextField (
                onValueChanged = {
                    keterangan.value = it
                },
                label = "Keterangan",
                isError = false,
                error = "Please Enter Valid Email"
            )

            MyButton(modifier = Modifier.fillMaxWidth(),
                enabled = true,
                text = "Simpan",
                buttonType = ButtonType.PRIMARY, onClick = {

                    /**
                     * Function save
                     */
                    val selisih = calcCrow(latitude.doubleValue, longitude.doubleValue, areaFormTransaction.latitude, areaFormTransaction.longitude)
                    Timber.tag("MYTAG").e("Selisih ${roundOffDecimal(selisih)}")
                    if (selisih < 10.0) {
                        viewModel.onEvent(AreaEvent.SaveFormSecurity(areaBody = FormAreaBody(
                            userid = "admin@admin.com", area = areaFormTransaction.name, latitude_actual = latitude.doubleValue, longitude_actual = longitude.doubleValue, keterangan = keterangan.value
                        ) ) )
                    } else {
                        viewModel.sendAreaFormEvent(UiAreaFormEvent.OutOfArea)
                    }


                })

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AreaScan(lokasi: String = "Lokasi") {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.LocationOn, contentDescription = "MyLocation")
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                buildAnnotatedString {
                    append("Kamu berada di ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(lokasi)
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BoxPhoto() {
    OutlinedCard(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clickable {

        }
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
            Icon(Icons.Filled.AddAPhoto, contentDescription = "take photo")
            Text(text = "Tap untuk mengambil foto")
        }
    }
}