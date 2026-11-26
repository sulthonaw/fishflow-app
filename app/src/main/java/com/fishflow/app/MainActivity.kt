package com.fishflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fishflow.app.core.ui.theme.FishFlowTheme
import com.fishflow.app.navigation.FishFlowNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FishFlowTheme {
                FishFlowNavGraph()
            }
        }
    }
}
