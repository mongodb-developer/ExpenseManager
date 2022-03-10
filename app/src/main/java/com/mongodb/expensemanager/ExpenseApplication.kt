package com.mongodb.expensemanager

import android.app.Application
import com.mongodb.expensemanager.di.koinModules
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ExpenseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        setupKoin()

        val config = RealmConfiguration.Builder()
            .name("expenseDB.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
    }

    private fun setupKoin() {
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@ExpenseApplication)
            modules(modules = koinModules())
        }
    }

}