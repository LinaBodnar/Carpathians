package com.bodnar.carpathians.di

import eu.anifantakis.lib.ksafe.KSafe
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { KSafe(androidContext()) }
}
