package com.mongodb.expensemanager

import io.realm.RealmObject
import java.util.*

open class ExpenseInfo : RealmObject() {

    var expenseId: String = UUID.randomUUID().toString()
    var expenseName: String = ""
    var expenseValue: Int = 0
}