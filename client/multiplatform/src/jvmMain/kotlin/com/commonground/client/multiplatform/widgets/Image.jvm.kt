package com.commonground.client.multiplatform.widgets

import java.io.File
import java.net.URL

actual fun File.readBytes2(): ByteArray {
    return this.readBytes()
}

actual fun URL.readBytes2(): ByteArray {
    return this.readBytes()
}