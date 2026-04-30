package com.commonground.client.multiplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.commonground.client.multiplatform.data.RepoStore
import com.commonground.client.multiplatform.ui.MainUi

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var instance: MainActivity // TODO
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repoStore = RepoStore()
        setContent {
            MainUi(repoStore)
        }
    }
}