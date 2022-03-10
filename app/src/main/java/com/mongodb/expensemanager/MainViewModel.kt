package com.mongodb.expensemanager

import androidx.lifecycle.*
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.kotlin.toFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    private fun getAllExpense(): Flow<List<ExpenseInfo>> {
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

    private fun observeExpenseList() {
        val result = realm.where(ExpenseInfo::class.java).findAll()
        result.addChangeListener(RealmChangeListener<RealmResults<ExpenseInfo>> {
            _expenses.postValue(realm.copyFromRealm(it))
        })
    }

}