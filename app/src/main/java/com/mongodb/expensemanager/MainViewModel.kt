package com.mongodb.expensemanager

import androidx.lifecycle.*
import io.realm.Realm
import io.realm.kotlin.toFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MainViewModel(private val realm: Realm) : ViewModel() {

    val expenses: LiveData<List<ExpenseInfo>> = getAllExpense().asLiveData()

    val totalExpense: LiveData<Int> =
        getAllExpense().map { it.sumOf { it.expenseValue } }.asLiveData()

    fun addExpense(value: Int, name: String) {

        val expenseInfo = ExpenseInfo().apply {
            this.expenseValue = value
            this.expenseName = name
        }

        realm.executeTransactionAsync {
            it.copyToRealm(expenseInfo)
        }
    }

    private fun getAllExpense(): Flow<List<ExpenseInfo>> {
        // Copy to realm for issue with livedata
        return realm.where(ExpenseInfo::class.java).findAllAsync().toFlow()
    }

    fun removeExpense(expenseInfo: ExpenseInfo) {
        realm.executeTransactionAsync { realm ->
            val result = realm.where(ExpenseInfo::class.java)
                .equalTo("expenseId", expenseInfo.expenseId)
                .findFirst()

            result?.let {
                result.deleteFromRealm()
            }
        }
    }

    override fun onCleared() {
        //TODO : Highlight this
        super.onCleared()
        realm.close()
    }

}