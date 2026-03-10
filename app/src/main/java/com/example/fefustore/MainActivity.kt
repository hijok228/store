package com.example.fefustore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fefustore.navigation.AppNavGraph
import com.example.fefustore.ui.theme.FEFUStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FEFUStoreTheme {
                AppNavGraph()
            }
        }
    }
}