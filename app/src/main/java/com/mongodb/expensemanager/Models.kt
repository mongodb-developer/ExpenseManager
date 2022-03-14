package com.mongodb.expensemanager

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class ExpenseInfo : RealmObject() {

    @PrimaryKey
    var expenseId: String = UUID.randomUUID().toString()
    var expenseName: String = ""
    var expenseValue: Int = 0
}