package com.weikappinc.weikapp

import android.app.Application
import com.weikappinc.weikapp.di.appModules
import leakcanary.LeakSentry
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()

            androidContext(this@App)

            modules(appModules)
        }

        /*ioThread {
            val currentUserDataDao: CurrentUserDataDao by inject()

            val currentUser = CurrentUserData("21jkas5-435", "b.ovidiu.2312@gmail.com", "Ovidiu")

            currentUserDataDao.insert(currentUser)
        }*/

        LeakSentry.config = LeakSentry.config.copy(watchFragmentViews = false)
    }
}