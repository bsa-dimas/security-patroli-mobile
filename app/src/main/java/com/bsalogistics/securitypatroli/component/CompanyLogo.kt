package com.bsalogistics.securitypatroli.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.bsalogistics.securitypatroli.R
import com.bsalogistics.securitypatroli.ui.theme.AppTheme

@Composable
fun CompanyLogo() {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "2024@")
        Image(modifier = Modifier.height(20.dp), painter = painterResource(id = R.drawable.bsalogistics), contentDescription = "bsalogistics")
    }

}

@Preview(showBackground = true)
@Composable
fun CompanyLogoPreview() {
    CompanyLogo()
}