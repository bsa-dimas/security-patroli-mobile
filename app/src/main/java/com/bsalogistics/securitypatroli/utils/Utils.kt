package com.bsalogistics.securitypatroli.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 10) {
    outputStream().use { out ->
//        bitmap = addWatermark(bitmap, "ok ok ok OK OK").com
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

fun bitmapToFile(context: Context, bitmap: Bitmap) : File {
    val wrapper = ContextWrapper(context)
    var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
    file = File(file,"${UUID.randomUUID()}.jpg")
    val stream: OutputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
    stream.flush()
    stream.close()
    return file
}

@SuppressLint("SimpleDateFormat")
fun timeMark() : String {
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return formatter.format(time)
}