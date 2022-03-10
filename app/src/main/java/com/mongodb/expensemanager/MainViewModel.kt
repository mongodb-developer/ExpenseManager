package com.mongodb.expensemanager

import androidx.lifecycle.*
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val realm: Realm) : ViewModel() {

    private val _expenses = MutableLiveData<List<ExpenseInfo>>()
    val expenses: LiveData<List<ExpenseInfo>> = _expenses

    val totalExpense: LiveData<Int> = Transformations.map(_expenses) {
        it.sumOf { it.expenseValue }
    }

    init {
        getAllExpense()
        observeExpenseList()
    }

    fun addExpense(value: Int, name: String) {

        val expenseInfo = ExpenseInfo().apply {
            this.expenseValue = value
            this.expenseName = name
        }

        realm.executeTransactionAsync {
            it.copyToRealm(expenseInfo)
        }
    }

    private fun getAllExpense() {
        realm.executeTransactionAsync {
            val result = it.where(ExpenseInfo::class.java).findAll()
            _expenses.postValue(it.copyFromRealm(result))
        }
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

    private fun observeExpenseList() {
        val result = realm.where(ExpenseInfo::class.java).findAll()
        result.addChangeListener(RealmChangeListener<RealmResults<ExpenseInfo>> {
            _expenses.postValue(realm.copyFromRealm(it))
        })
    }

}