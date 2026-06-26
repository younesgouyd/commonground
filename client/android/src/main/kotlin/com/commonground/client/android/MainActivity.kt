package com.commonground.client.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.commonground.client.multiplatform.data.AndroidFileStorage
import com.commonground.client.multiplatform.ui.MainUi

class MainActivity  : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val fileStorage = AndroidFileStorage(applicationContext)
        setContent {
            MainUi(
                modifier = Modifier.fillMaxSize(),
                fileStorage = fileStorage
            )
        }
    }
}