package com.bsalogistics.securitypatroli.screen.auth.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bsalogistics.securitypatroli.R
import com.bsalogistics.securitypatroli.component.AlertDialogModel
import com.bsalogistics.securitypatroli.component.AlertDialogType
import com.bsalogistics.securitypatroli.component.MyAlertDialog
import com.bsalogistics.securitypatroli.screen.NavigationRoutes
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaEvent
import com.bsalogistics.securitypatroli.utils.PreferencesManager

@Preview(showBackground = true)
@Composable
fun DashboardScreen(navController: NavController = rememberNavController()) {

    Column(modifier = Modifier.safeDrawingPadding(), horizontalAlignment = Alignment.CenterHorizontally) {

        TopDashboard()

        Spacer(modifier = Modifier.weight(1f))

        BoxList(navController)
    }

}

@Composable
fun TopDashboard() {
    Column(modifier = Modifier.safeDrawingPadding()) {
        AppName()
        Spacer(modifier = Modifier.height(20.dp))
        Profile()
    }
}

@Composable
fun AppName() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Security Patroli", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(20.dp))
    }
}

@Composable
fun Profile() {

    val user = PreferencesManager(LocalContext.current).getDataUser()

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Image(painterResource(id = R.drawable.policeman), contentDescription = "icon", modifier = Modifier.height(100.dp))
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun BoxList(navController: NavController) {
    val context = LocalContext.current
    val user = PreferencesManager(context).getDataUser()

    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }

    MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Keluar aplikasi?", typeDialog = AlertDialogType.CONFIRM), onConfirm = {
        openDialog.value = false
        PreferencesManager(context).clear()

        navController.navigate(route = NavigationRoutes.Unauthenticated.NavigationRoute.route) {
            popUpTo(route = NavigationRoutes.Area.NavigationRoute.route) {
                inclusive = true
            }
        }

    }, onDismiss = {
        openDialog.value = false
    } )

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BoxItem(text = "List Area", modifier = Modifier.weight(1f), onClick = {
                navController.navigate(route = NavigationRoutes.Area.AreaListForAdmin.route)
            })
            BoxItem(text = "Report Area", modifier = Modifier.weight(1f), onClick = {
                navController.navigate(route = NavigationRoutes.Area.ReportAreaScreen.route)
            })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BoxItem(text = "Monitor Area", modifier = Modifier.weight(1f), onClick = {
                navController.navigate(route = "${NavigationRoutes.Area.AreaListForSecurity.route}/${user.typeuser}")
            })
            BoxItem(text = "Keluar", modifier = Modifier.weight(1f), onClick = {
                openDialog.value = true
            })
        }
    }
}

@Composable
fun BoxItem(modifier : Modifier = Modifier, text: String = "Item", onClick : () -> Unit = {}) {
    Box(modifier = modifier
        .height(200.dp)
        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
        .clickable {
            onClick.invoke()
        },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}