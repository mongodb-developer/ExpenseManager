package com.mongodb.expensemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import com.mongodb.expensemanager.ui.theme.ExpenseManagerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val TAG: String = "MainActivity"
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDialogOpen = remember { mutableStateOf(false) }
            val expenseTotal = viewModel.totalExpense.observeAsState(initial = 0)


            ExpenseManagerTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxHeight(),
                    floatingActionButton = { fab(isDialogOpen, viewModel) },
                    topBar = {
                        TopAppBar(
                            content = {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "My Expense Sheet")
                                    Text(text = "Total: ${expenseTotal.value}")
                                }
                            }
                        )
                    }
                ) {
                    expenseList(viewModel)
                }
            }
        }
    }

    @Composable
    fun expenseItemView(item: ExpenseInfo, viewModel: MainViewModel) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable {}
        ) {
            val text = item.expenseName + "         ----          " + item.expenseValue

            Text(text = text)
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_done_24),
                contentDescription = "done",
                modifier = Modifier.clickable(onClick = {
                    viewModel.removeExpense(item)
                })
            )
        }
    }

    @Composable
    fun expenseList(viewModel: MainViewModel) {

        val expenses = viewModel.expenses.observeAsState(emptyList())

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            items(items = expenses.value) { item ->
                expenseItemView(item, viewModel)
            }
        }
    }

    @Composable
    fun fab(isDialogOpen: MutableState<Boolean>, taskViewModel: MainViewModel) {

        FloatingActionButton(onClick = {
            isDialogOpen.value = !isDialogOpen.value
        }) {
            val addIcon = painterResource(id = R.drawable.ic_baseline_add_24)
            Icon(painter = addIcon, contentDescription = "Add Icon")
        }

        if (isDialogOpen.value) {
            addNewDialog(isDialogOpen, taskViewModel)
        }
    }

    @Composable
    fun addNewDialog(isDialogOpen: MutableState<Boolean>, viewModel: MainViewModel) {

        val expenseDesc = remember { mutableStateOf("") }
        val expenseValue = remember { mutableStateOf(0) }

        Dialog(
            properties = DialogProperties(),
            onDismissRequest = { isDialogOpen.value = false },
            content = {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.White)
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        text = "Add New Task",
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = expenseDesc.value,
                        onValueChange = { expenseDesc.value = it },
                        label = { Text(text = "Expense Desc") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally),
                    )

                    OutlinedTextField(
                        value = expenseValue.value.toString(),
                        onValueChange = {
                            if (it.isNotBlank() && it.isDigitsOnly())
                                expenseValue.value = it.toInt()
                        },
                        label = { Text(text = "Expense Value") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            expenseDesc.value = ""
                            expenseValue.value = 0
                            isDialogOpen.value = false
                        }) {
                            Text(text = "Cancel")
                        }

                        Button(onClick = {
                            viewModel.addExpense(
                                name = expenseDesc.value,
                                value = expenseValue.value
                            )
                            expenseDesc.value = ""
                            expenseValue.value = 0
                            isDialogOpen.value = false
                        }) {
                            Text(text = "Ok")
                        }
                    }
                }
            }
        )
    }


}