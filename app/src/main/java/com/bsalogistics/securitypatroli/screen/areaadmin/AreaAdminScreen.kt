package com.bsalogistics.securitypatroli.screen.areaadmin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.bsalogistics.securitypatroli.component.AlertDialogModel
import com.bsalogistics.securitypatroli.component.AlertDialogType
import com.bsalogistics.securitypatroli.component.ListEmpty
import com.bsalogistics.securitypatroli.component.ListError
import com.bsalogistics.securitypatroli.component.ListLoading
import com.bsalogistics.securitypatroli.component.LoadingDialog
import com.bsalogistics.securitypatroli.component.MyAlertDialog
import com.bsalogistics.securitypatroli.component.MyToolbar
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.Area
import com.bsalogistics.securitypatroli.screen.NavigationRoutes
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaListSecurityViewModel
import com.google.gson.Gson

@Composable
fun AreaAdminScreen(navController: NavController) {
    val listState = rememberLazyListState()
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                expanded = expandedFab,
                onClick = {
                    navController.navigate(route = NavigationRoutes.Area.AddAreaForm.route)
                },
                icon = { Icon(Icons.Filled.Add, "Extended floating action button.") },
                text = { Text(text = "Tambah data") },
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding),
        ) {
            MyToolbar(navController = navController, title = "Data Area")

            GenerateListArea(state = listState)

        }
    }
}

@Composable
private fun GenerateListArea(viewModel: AreaAdminViewModel = hiltViewModel(), state : LazyListState = rememberLazyListState()){

    val areaListState by viewModel.areaList.collectAsState(null)

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(AreaListAdminEvent.GetList)
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
            val list : MutableList<Area> = (areaListState as APIResponse.Success).data?.data ?: mutableListOf()
            ViewList( list = list, state = state )
        }
        null -> {}
    }

}

@Composable
private fun ViewList(list : MutableList<Area> = mutableListOf(), state : LazyListState = rememberLazyListState()) {

    if (list.isEmpty()) {

        ListEmpty("Data Area Kosong")

    } else {
        Column(Modifier.padding(top = 10.dp)) {
            Text(text = "Total data: ${list.size}", color = Color.Unspecified.copy(alpha = 0.5f), style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.padding(horizontal = 20.dp))
            LazyColumn (state = state, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(list.size) { i ->
                    AreaListItem(list[i])

                    if (i < list.lastIndex)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp), thickness = 1.dp, color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun AreaListItem(item : Area, viewModel: AreaAdminViewModel = hiltViewModel()) {

    val dropAreaState by viewModel.dropArea.collectAsState(null)

    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }


    when (dropAreaState) {
        is APIResponse.Error -> {
            (dropAreaState as (APIResponse.Error)).errorMsg?.let {

            }
        }
        is APIResponse.Loading -> {
            LoadingDialog("Hapus data")
        }

        is APIResponse.Success -> {

        }
        null -> {}
    }

    if (openDialog.value) {
        MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = "Hapus ${item.name}?", typeDialog = AlertDialogType.CONFIRM), onConfirm = {
            openDialog.value = false
            viewModel.onEvent(AreaListAdminEvent.DropArea(item.id))
        }, onDismiss = {
            openDialog.value = false
        } )
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            openDialog.value = true
        }) {

        Column (modifier = Modifier
            .padding(20.dp))
        {
            Text(
                modifier = Modifier,
                text = item.name,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

    }

}