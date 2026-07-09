package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.document
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import org.w3c.files.get

@Composable
actual fun SystemFilePicker(onFileChosen: (ByteArray) -> Unit, dismiss: () -> Unit) {
    LaunchedEffect(Unit) {
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.accept = "image/*"
        input.onchange = { _ ->
            val files = input.files
            if (files != null && files.length > 0) {
                val file = files[0]!!
                val reader = FileReader()
                reader.onload = { event ->
                    val arrayBuffer = event.target.asDynamic().result
                    val int8Array = Int8Array(buffer = arrayBuffer)
                    val bytes = ByteArray(int8Array.length) { i -> int8Array[i] }
                    onFileChosen(bytes)
                }
                reader.onerror = { _ -> dismiss() }
                reader.readAsArrayBuffer(file)
            } else {
                dismiss()
            }
        }
        input.oncancel = { dismiss() }
        input.click()
    }
}