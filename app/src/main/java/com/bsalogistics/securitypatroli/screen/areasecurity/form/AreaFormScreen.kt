package com.bsalogistics.securitypatroli.screen.areasecurity.form

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bsalogistics.securitypatroli.BuildConfig
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
import com.bsalogistics.securitypatroli.network.FormAreaBody
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaEvent
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaListSecurityViewModel
import com.bsalogistics.securitypatroli.utils.PreferencesManager
import com.bsalogistics.securitypatroli.utils.ReqLocationPermission
import com.bsalogistics.securitypatroli.utils.addWatermark
import com.bsalogistics.securitypatroli.utils.bitmapToFile
import com.bsalogistics.securitypatroli.utils.calcCrow
import com.bsalogistics.securitypatroli.utils.getCurrentLocationScanner
import com.bsalogistics.securitypatroli.utils.roundOffDecimal
import com.bsalogistics.securitypatroli.utils.timeMark
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import java.util.UUID


@Composable
fun AreaFormScreen(navController: NavController, viewModel: AreaListSecurityViewModel = hiltViewModel(), areaName: String) {

    val checkAreaState by viewModel.checkAreaFormTransaction.collectAsState(null)
    val saveAreaState by viewModel.saveFormSecurity.collectAsState(null)
    val eventAreaFormState by viewModel.eventAreaForm.collectAsState(null)
    val scope = rememberCoroutineScope()

    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val areaFormTransactionModel = remember {
        mutableStateOf<Area>(Area())
    }

    val countTryGetLocation = rememberSaveable { mutableIntStateOf(0) }

    when(eventAreaFormState) {
        UiAreaFormEvent.OutOfArea -> {

            if (countTryGetLocation.intValue > 2) {
                openDialog.value = true
                MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Sertakan photo area sekitar jika ingin tetap melanjutkan proses!", typeDialog = AlertDialogType.FAILUREWITHDISMISS),
                    onConfirm = {
                        openDialog.value.not()
                        countTryGetLocation.intValue = 0
                        viewModel.requireTakepicture.value = true
                        viewModel.sendAreaFormEvent(UiAreaFormEvent.Init)

                    },
                    onDismiss = {
                        openDialog.value = false
                        viewModel.sendAreaFormEvent(UiAreaFormEvent.Init)
                    }
                )
            } else {
                openDialog.value = true
                MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Gagal dalam range area!\nSilahkan bergeser dan coba lagi", typeDialog = AlertDialogType.CONFIRM),
                    onConfirm = {
                        openDialog.value.not()

                        scope.launch {

                            countTryGetLocation.intValue += 1
                            Timber.tag("MYTAG").e("countTryGetLocation: ${countTryGetLocation.intValue}")

                            viewModel.sendAreaFormEvent(UiAreaFormEvent.LoadingGetLocation)
                            delay(2000)
                            viewModel.sendAreaFormEvent(UiAreaFormEvent.TryGetLocation)
                        }

                    },
                    onDismiss = {
                        openDialog.value = false
                        viewModel.sendAreaFormEvent(UiAreaFormEvent.Init)
                    }
                )
            }


        }
        UiAreaFormEvent.TryGetLocation -> {
        }
        UiAreaFormEvent.LoadingGetLocation -> {
            LoadingDialog("Mencari lokasi saat ini")
        }
        UiAreaFormEvent.SuccessGetLocation -> {

            viewModel.sendAreaFormEvent(UiAreaFormEvent.Init)

        }
        UiAreaFormEvent.FailedGetLocation -> {
            openDialog.value = true

            MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Gagal mendapatkan lokasi", typeDialog = AlertDialogType.FAILURE), onConfirm = {
                openDialog.value.not()
                navController.navigateUp()
            } )
        }
        UiAreaFormEvent.RequireFormData -> {
            openDialog.value = true

            MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Data belum diisi!", typeDialog = AlertDialogType.FAILURE), onConfirm = {
                openDialog.value.not()
            } )

        }
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
                    navController.navigateUp()
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
    val user = PreferencesManager(context).getDataUser()
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


                    viewModel.sendAreaFormEvent(UiAreaFormEvent.SuccessGetLocation)

                    /**
                     * Function save
                     */
                    val selisih = calcCrow(latitude.doubleValue, longitude.doubleValue, areaFormTransaction.latitude, areaFormTransaction.longitude)

                    if (selisih < 10.0) {
                        if (viewModel.requireTakepicture.value && viewModel.getListUri().isNotEmpty()) {
                            viewModel.onEvent(
                                AreaEvent.SaveFormSecurityWithPhoto(
                                    photos = createMultipartBodyMultiple(
                                        uris = viewModel.uri,
                                        area = FormAreaBody(
                                            userid = user.userid,
                                            area = areaFormTransaction.name,
                                            latitude_actual = latitude.doubleValue,
                                            longitude_actual = longitude.doubleValue,
                                            distance = selisih,
                                            keterangan = keterangan.value
                                        )
                                    )
                                )
                            )
                        } else {
                            viewModel.sendAreaFormEvent(UiAreaFormEvent.RequireFormData)
                        }
                    } else {
                        viewModel.sendAreaFormEvent(UiAreaFormEvent.OutOfArea)
                    }

                    Timber.tag("MYTAG").e("Selisih ${roundOffDecimal(selisih)}")
                    Timber
                        .tag("MYTAG")
                        .e("Try Again onGetCurrentLocationSuccess LATITUDE: ${it.first}, LONGITUDE: ${it.second})}")
                },
                onGetCurrentLocationFailed = {

                    viewModel.sendAreaFormEvent(UiAreaFormEvent.FailedGetLocation)

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
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            AreaScan(lokasi = areaFormTransaction.name)

            InputTextField (
                input = keterangan.value,
                onValueChanged = {
                    keterangan.value = it
//                    viewModel.keterangan.value = it
                },
                label = "Keterangan",
                isError = false,
                error = "Please Enter Valid Email"
            )

            if (viewModel.requireTakepicture.value) {
                BoxPhoto()
            }

            MyButton(modifier = Modifier.fillMaxWidth(),
                enabled = true,
                text = "Simpan",
                buttonType = ButtonType.PRIMARY, onClick = {


                    /**
                     * Function save with photo
                     */
                    if (viewModel.requireTakepicture.value) {
                        val selisih = calcCrow(latitude.doubleValue, longitude.doubleValue, areaFormTransaction.latitude, areaFormTransaction.longitude)
                        if (viewModel.requireTakepicture.value && viewModel.getListUri().isNotEmpty()) {
                            viewModel.onEvent(
                                AreaEvent.SaveFormSecurityWithPhoto(
                                    photos = createMultipartBodyMultiple(
                                        uris = viewModel.uri,
                                        area = FormAreaBody(
                                            userid = user.userid,
                                            area = areaFormTransaction.name,
                                            latitude_actual = latitude.doubleValue,
                                            longitude_actual = longitude.doubleValue,
                                            distance = selisih,
                                            keterangan = keterangan.value
                                        )
                                    )
                                )
                            )
                        } else {
                            viewModel.sendAreaFormEvent(UiAreaFormEvent.RequireFormData)
                        }

                    } else {

                        /**
                         * Function save without photo
                         */
                        val selisih = calcCrow(latitude.doubleValue, longitude.doubleValue, areaFormTransaction.latitude, areaFormTransaction.longitude)
                        Timber.tag("MYTAG").e("Selisih ${roundOffDecimal(selisih)}")
                        if (selisih < 10.0) {
                            viewModel.onEvent(
                                AreaEvent.SaveFormSecurityWithPhoto(
                                    photos = createMultipartBodyMultiple(
                                        uris = viewModel.uri,
                                        area = FormAreaBody(
                                            userid = user.userid,
                                            area = areaFormTransaction.name,
                                            latitude_actual = latitude.doubleValue,
                                            longitude_actual = longitude.doubleValue,
                                            distance = selisih,
                                            keterangan = keterangan.value
                                        )
                                    )
                                )
                            )
                        } else {
                            viewModel.sendAreaFormEvent(UiAreaFormEvent.OutOfArea)
                        }
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
private fun BoxPhoto(viewModel: AreaListSecurityViewModel = hiltViewModel()) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val user = PreferencesManager(context).getDataUser()

    var selectedUri = rememberSaveable {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }

    var capturedImageUri by rememberSaveable {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {

                scope.launch {
                    val path = getRealPathFromURI(viewModel.uriChoosePhoto.value, context)

                    val newFile = File(path!!)

                    val uriNew = Uri.fromFile(
                        bitmapToFile(context, addWatermark(BitmapFactory.decodeFile(newFile.path), timeMark() ))
                    )

                    viewModel.addUri(uriNew)
                    capturedImageUri = uriNew
                }

            }

        }
    )

    MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Hapus foto?", typeDialog = AlertDialogType.CONFIRM), onConfirm = {
        openDialog.value = false
        viewModel.removeUri(uri = selectedUri.value)
    }, onDismiss = {
        openDialog.value = false
    } )

    OutlinedCard(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clickable {

            viewModel.createUriPhoto(context)

            cameraLauncher.launch(viewModel.uriChoosePhoto.value)
        }
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
            Icon(Icons.Filled.AddAPhoto, contentDescription = "take photo")
            Text(text = "Tap untuk mengambil foto")
        }
    }
    Text(text = "*Sertakan photo area sekitar", color = Color.Red)

    if (viewModel.getListUri().isNotEmpty()) {
        viewModel.getListUri().forEach { uriCurrent ->

            OutlinedCard(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable {
                }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uriCurrent)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            selectedUri.value = uriCurrent
                            openDialog.value = true
                        }
                )
            }

        }
    }

}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )

    return image
}

fun createMultipartBody(path : String, area: FormAreaBody) : MultipartBody {
    val file = File(path)
    return MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("userid", area.userid)
        .addFormDataPart("area", area.area)
        .addFormDataPart("latitude_actual", area.latitude_actual.toString())
        .addFormDataPart("longitude_actual", area.longitude_actual.toString())
        .addFormDataPart("keterangan", area.keterangan)
        .addFormDataPart("image", file.name, file.asRequestBody())
        .build()
}

fun createMultipartBodyMultiple(uris : List<Uri>, area: FormAreaBody) : MultipartBody {

    val part = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("userid", area.userid)
        .addFormDataPart("area", area.area)
        .addFormDataPart("latitude_actual", area.latitude_actual.toString())
        .addFormDataPart("longitude_actual", area.longitude_actual.toString())
        .addFormDataPart("keterangan", area.keterangan)
        .addFormDataPart("distance", area.distance.toString())
        .addFormDataPart("countimage", uris.size.toString())

    uris.forEachIndexed { i, e ->
        val file = File(e.path!!)
        part.addFormDataPart("image$i", file.name, file.asRequestBody())
    }

    return part
        .build()
}



fun getRealPathFromURI(uri: Uri, context: Context): String? {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    val nameIndex =  returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    val size = returnCursor.getLong(sizeIndex).toString()
    val file = File(context.filesDir, name)
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        var read = 0
        val maxBufferSize = 1 * 1024 * 1024
        val bytesAvailable: Int = inputStream?.available() ?: 0
        //int bufferSize = 1024;
        val bufferSize = Math.min(bytesAvailable, maxBufferSize)
        val buffers = ByteArray(bufferSize)
        while (inputStream?.read(buffers).also {
                if (it != null) {
                    read = it
                }
            } != -1) {
            outputStream.write(buffers, 0, read)
        }
        Timber.tag("MYTAG").e("File Size " + file.length())
        inputStream?.close()
        outputStream.close()
        Timber.tag("MYTAG").e("File Path " + file.path)

    } catch (e: java.lang.Exception) {
        Timber.tag("MYTAG").e("Exception "+e.message!!)
    }
    return file.path
}


