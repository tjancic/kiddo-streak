package com.kiddostreak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kiddostreak.core.designsystem.theme.KiddoStreakTheme
import com.kiddostreak.feature.widget.WidgetUpdater

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KiddoStreakTheme {
                KiddoStreakApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        WidgetUpdater.updateAll(this)
    }
}
