package com.bsalogistics.securitypatroli.screen.areasecurity.form

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.bsalogistics.securitypatroli.R
import com.bsalogistics.securitypatroli.component.ListError
import com.bsalogistics.securitypatroli.component.ListLoading
import com.bsalogistics.securitypatroli.component.MyToolbar
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.AreaDetail
import com.bsalogistics.securitypatroli.network.AreaFormTransaction
import com.bsalogistics.securitypatroli.network.PhotoArea
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaDetailEvent
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaEvent
import com.bsalogistics.securitypatroli.ui.theme.AppTheme
import timber.log.Timber

@Composable
fun AreaFormDetailScreen(navController: NavController, areaId: String) {

    Column(modifier = Modifier
        .safeDrawingPadding()
        .fillMaxSize()) {

        MyToolbar(navController = navController, title = "Detail Area")

        Box(modifier = Modifier.padding(20.dp)) {
            GenerateData(areaId = areaId)
        }


    }

}

@Composable
fun GetDataPhotosArea(photos : MutableList<PhotoArea>) {

    if (photos.isNotEmpty()) {
        LazyVerticalGrid(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(photos.size) { i ->
                Box(modifier = Modifier) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppTheme.dimens.paddingSmall),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data = photos[i].url_photo)
                            .crossfade(enable = true)
                            .scale(Scale.FILL)
                            .build(),
                        contentDescription = photos[i].filename
                    )
                }

            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Tidak ada photo")
        }
    }

}

@Composable
private fun GenerateData(viewModel: AreaFormDetailViewModel = hiltViewModel(), areaId : String) {
    val dataState by viewModel.areaDetail.collectAsState(null)

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(AreaDetailEvent.GetAreaDetail(areaId))
    }

    when (dataState) {
        is APIResponse.Error -> {
            (dataState as (APIResponse.Error)).errorMsg?.let {
                ListError( it )
            }
        }
        is APIResponse.Loading -> {
            ListLoading()
        }
        is APIResponse.Success -> {
            val data : AreaDetail? = (dataState as APIResponse.Success).data?.data
            data?.let {
                ViewDetail(data = data)
            }

        }
        else -> {}
    }


}

@Composable
fun ViewDetail(data: AreaDetail) {
    Column {
        AreaScan( area = data )
        Box(modifier = Modifier
            .weight(1f)) {
            data.photos?.let {
                GetDataPhotosArea(photos = it)
            }
        }
    }
}

@Composable
private fun AreaScan(area : AreaDetail) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.LocationOn, contentDescription = "MyLocation")
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(area.area)
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                buildAnnotatedString {
                    append("Security: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(area.userid)
                    }
                    append("\nPatroli: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(area.created_at)
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 12.sp)
            )
            Text(text = area.keterangan ?: "-")
        }
    }
}

