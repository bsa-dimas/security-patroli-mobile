package com.bsalogistics.securitypatroli.screen.areasecurity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bsalogistics.securitypatroli.R
import com.bsalogistics.securitypatroli.component.AlertDialogModel
import com.bsalogistics.securitypatroli.component.AlertDialogType
import com.bsalogistics.securitypatroli.component.ListEmpty
import com.bsalogistics.securitypatroli.component.ListError
import com.bsalogistics.securitypatroli.component.ListLoading
import com.bsalogistics.securitypatroli.component.MyAlertDialog
import com.bsalogistics.securitypatroli.component.MyToolbar
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.AreaFormTransaction
import com.bsalogistics.securitypatroli.screen.NavigationRoutes
import com.bsalogistics.securitypatroli.screen.UiEvent
import com.bsalogistics.securitypatroli.utils.PreferencesManager

@Composable
fun AreaListSecurity(navController: NavController, viewModel: AreaListSecurityViewModel = hiltViewModel(), typeuser: String = "") {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> {
                }

                is UiEvent.Navigate -> {
                    navController.navigate(route = event.route)
                }

                is UiEvent.SignOut -> {

                    PreferencesManager(context).clear()

                    navController.navigate(route = NavigationRoutes.Unauthenticated.NavigationRoute.route) {
                        popUpTo(route = NavigationRoutes.Area.NavigationRoute.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }


    Column(modifier = Modifier
        .safeDrawingPadding()
        .fillMaxSize()) {

        if (typeuser == "admin") {
            MyToolbar(navController = navController, title = "Monitoring Area")
        } else {
            TopBar()
        }

        ProfileSecurity()
        Box(modifier = Modifier.weight(1f)) {
            GenerateList()
        }
        ButtonAction()
    }
}


@Composable
private fun TopBar() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Security Patroli", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(20.dp))
    }
}

@Composable
private fun ProfileSecurity() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Image(painterResource(id = R.drawable.policeman), contentDescription = "icon", modifier = Modifier.height(100.dp))
            Text(text = "Policeman", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ButtonAction (
    viewModel: AreaListSecurityViewModel = hiltViewModel()
) {

    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }

    MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Keluar aplikasi?", typeDialog = AlertDialogType.CONFIRM), onConfirm = {
        openDialog.value = false
        viewModel.onEvent(AreaEvent.SignOut)
    }, onDismiss = {
        openDialog.value = false
    } )

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {

        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {

                viewModel.onEvent(AreaEvent.GotoScan)
            } ) {
                Row(modifier = Modifier.padding(10.dp)) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "qrcode")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = "Scan Area")
                }
            }
        }

        Box(modifier = Modifier) {
            TextButton(modifier = Modifier, onClick = {
                openDialog.value = true
            }) {
                Row(modifier = Modifier.padding(10.dp)) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "logout")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = "Keluar")
                }
            }
        }

    }

}

@Composable
private fun GenerateList(viewModel: AreaListSecurityViewModel = hiltViewModel()) {
    val areaListState by viewModel.areaFormTransactionScannedList.collectAsState(null)

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(AreaEvent.GetList)
    }

    when (areaListState) {
        is APIResponse.Error -> {
            (areaListState as (APIResponse.Error)).errorMsg?.let {
                ListError( it )
            }
        }
        is APIResponse.Loading -> {
            ListLoading()
        }
        is APIResponse.Success -> {
            val list : MutableList<AreaFormTransaction> = (areaListState as APIResponse.Success).data?.data ?: mutableListOf()
            ViewList( list = list )
        }
        null -> {}
    }
}

@Composable
private fun ViewList(list : MutableList<AreaFormTransaction> = mutableListOf()) {

    if (list.isEmpty()) {

        ListEmpty("Data Area Kosong")

    } else {
        LazyColumn (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(list.size) { i ->
                AreaListItem(list[i])

                if (i < list.lastIndex)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp), thickness = 1.dp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun AreaListItem(item : AreaFormTransaction = AreaFormTransaction(area = "Test"), onItemClick: () -> Unit = {}) {
    Column (modifier = Modifier.padding(20.dp)) {
        Text(
            modifier = Modifier,
            text = item.area,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(text = "Security: ${item.userid} \nPatroli: ${item.created_at ?: "-"}",
            color = Color.Unspecified.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelSmall)
    }
}