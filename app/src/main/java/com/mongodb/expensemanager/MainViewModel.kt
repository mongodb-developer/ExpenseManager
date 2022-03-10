package com.mongodb.expensemanager

import androidx.lifecycle.*
import io.realm.Realm
import io.realm.notifications.ResultsChange
import io.realm.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val realm: Realm) : ViewModel() {

    val expenses: LiveData<List<ExpenseInfo>> = liveData {
        getAllExpense()
    }

    val totalExpense: LiveData<Int> = Transformations.map(expenses) {
        it.sumOf { it.expenseValue }
    }

    init {
        getAllExpense()
    }

    fun addExpense(value: Int, name: String) {

        val expenseInfo = ExpenseInfo().apply {
            this.expenseValue = value
            this.expenseName = name
        }

        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                copyToRealm(expenseInfo)
            }
        }
    }

    private fun getAllExpense(): Flow<ResultsChange<ExpenseInfo>> {
        return realm.query<ExpenseInfo>().find().asFlow()
    }

    fun removeExpense(expenseInfo: ExpenseInfo) {

        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                findLatest(expenseInfo)?.also {
                    delete(it)
                }
            }
        }
    }
}