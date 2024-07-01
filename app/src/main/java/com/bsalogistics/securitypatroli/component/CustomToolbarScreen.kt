package com.bsalogistics.securitypatroli.component

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomToolbarScreen(navController: NavHostController, title: String, isBack: Boolean){
//    val scaffoldState = rememberScaffoldState()
//    val scope = rememberCoroutineScope()
//    var isDrawerOpen = remember {
//        mutableStateOf(false)
//    }
//    TopAppBar(
//        colors = TopAppBarDefaults.smallTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            titleContentColor = MaterialTheme.colorScheme.primary,
//        ),
//        title = {
//            Text(text = title,color = Color.Black,
//                fontSize = 18.sp)
//        },
//        modifier = Modifier.background(colorPrimary),
//        navigationIcon = {
//            if (isBack){
//                IconButton(onClick = {navController.navigateUp()}) {
//                    Icon(Icons.Filled.ArrowBack, "backIcon")
//                }
//            }else{
//                IconButton(onClick = {
//                    scope.launch {
//                        scaffoldState.drawerState.open()
//                        Log.i("Drawer", "drawer Open: ")
//                    }
//                }) {
//                    Icon(Icons.Filled.Menu, "backIcon")
//                }
//            }
//        }
//    )
}