package com.example.agenciaviajes.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object ImageStorageHelper {

    fun guardarImagen(context: Context, uri: Uri): String {
        val input = context.contentResolver.openInputStream(uri)
        val archivo = File(context.filesDir, "img_${UUID.randomUUID()}.jpg")
        input?.use { inputStream ->
            archivo.outputStream().use { output -> inputStream.copyTo(output) }
        }
        return archivo.absolutePath
    }
}