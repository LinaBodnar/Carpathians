package com.bodnar.carpathians

import android.app.Application
import com.bodnar.carpathians.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class CarpathiansApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CarpathiansApplication)
        }
    }
}
