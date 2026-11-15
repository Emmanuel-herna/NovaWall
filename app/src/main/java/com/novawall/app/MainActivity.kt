package com.novawall.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.novawall.app.ui.HomeScreen
import com.novawall.app.ui.HomeViewModel
import com.novawall.app.ui.theme.NovaWallTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NovaWallTheme(darkTheme = true, dynamicColor = false) {
                HomeScreen(viewModel = homeViewModel)
            }
        }
    }
}
