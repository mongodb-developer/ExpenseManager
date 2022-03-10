package com.mongodb.expensemanager.di

import com.mongodb.expensemanager.ExpenseApplication
import com.mongodb.expensemanager.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun koinModules() = module {

    viewModel {
        MainViewModel((androidApplication() as ExpenseApplication).realm)
    }
}