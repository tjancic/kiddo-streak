package com.kiddostreak.app

import android.app.Application
import com.kiddostreak.app.di.appModules
import com.kiddostreak.core.notifications.NotificationChannelManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class KiddoStreakApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@KiddoStreakApplication)
            modules(appModules)
        }

        NotificationChannelManager.createChannels(this)
    }
}
