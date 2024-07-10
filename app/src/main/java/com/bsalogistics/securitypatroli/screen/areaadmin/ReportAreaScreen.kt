package com.bsalogistics.securitypatroli.screen.areaadmin

import android.os.Build
import android.text.format.DateUtils
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bsalogistics.securitypatroli.component.ButtonType
import com.bsalogistics.securitypatroli.component.MyButton
import com.bsalogistics.securitypatroli.component.MyToolbar
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportAreaScreen(navController: NavController) {

    var showDialog by remember { mutableStateOf(false) }
    var showDialog2 by remember { mutableStateOf(false) }

    val dateState = rememberDatePickerState()
    val millisToLocalDate = dateState.selectedDateMillis?.let {
        DateUtils().convertMillisToLocalDate(it)
    }
    val dateToString = millisToLocalDate?.let {
        DateUtils().dateToString(millisToLocalDate)
    } ?: "Pilih tanggal mulai"

    val dateState2 = rememberDatePickerState()
    val millisToLocalDate2 = dateState2.selectedDateMillis?.let {
        DateUtils().convertMillisToLocalDate(it)
    }
    val dateToString2 = millisToLocalDate2?.let {
        DateUtils().dateToString(millisToLocalDate2)
    } ?: "Pilih tanggal akhir"

    Column(modifier = Modifier
        .safeDrawingPadding()
        .fillMaxSize()) {

        MyToolbar(navController = navController, title = "Report Area")

        Box(modifier = Modifier.padding(20.dp)) {

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))) {
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp),
                        text = dateToString,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "tanggal")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))) {
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp),
                        text = dateToString2,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        showDialog2 = true
                    }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "tanggal")
                    }
                }

                MyButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                    enabled = millisToLocalDate != null && millisToLocalDate2 != null,
                    text = "Kirim Data Report Ke Email",
                    buttonType = ButtonType.PRIMARY, onClick = {

                    })

            }

            if (showDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text(text = "OK")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                ) {
                    DatePicker(
                        state = dateState,
                        showModeToggle = true
                    )
                }
            }

            if (showDialog2) {
                DatePickerDialog(
                    onDismissRequest = { showDialog2 = false },
                    confirmButton = {
                        Button(
                            onClick = { showDialog2 = false }
                        ) {
                            Text(text = "OK")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog2 = false }
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                ) {
                    DatePicker(
                        state = dateState2,
                        showModeToggle = true
                    )
                }
            }

        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun DateUtils.convertMillisToLocalDate(millis: Long) : LocalDate {
    return Instant
        .ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertMillisToLocalDateWithFormatter(date: LocalDate, dateTimeFormatter: DateTimeFormatter) : LocalDate {
    //Convert the date to a long in millis using a dateformmater
    val dateInMillis = LocalDate.parse(date.format(dateTimeFormatter), dateTimeFormatter)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    //Convert the millis to a localDate object
    return Instant
        .ofEpochMilli(dateInMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}


@RequiresApi(Build.VERSION_CODES.O)
fun DateUtils.dateToString(date: LocalDate): String {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())
    val dateInMillis = convertMillisToLocalDateWithFormatter(date, dateFormatter)
    return dateFormatter.format(dateInMillis)
}

@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (day.position == DayPosition.MonthDate) Color.DarkGray else Color.LightGray
            )
        } else {
            Text(text = "Not support")
        }
    }
}

@Composable
fun Day(day: CalendarDay, onClick: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                enabled = day.position == DayPosition.MonthDate, // Only month-dates are clickable.
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) { // Change the color of in-dates and out-dates, you can also hide them completely!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (day.position == DayPosition.MonthDate) Color.White else Color.Gray
            )
        }
    }
}