package com.example.financeaudit.domain.model

enum class TransactionType {
    INCOME, EXPENSE
}

data class Category(
    val id: String,
    val name: String,
    val type: TransactionType
)

object CategoryData {

    val expenseCategories = listOf(
        Category("food", "Food & Drink",TransactionType.EXPENSE),
        Category("groceries", "Groceries",TransactionType.EXPENSE),
        Category("transport", "Transport",TransactionType.EXPENSE),
        Category("housing", "Housing",TransactionType.EXPENSE),
        Category("bills", "Bills", TransactionType.EXPENSE),
        Category("shopping", "Shopping", TransactionType.EXPENSE),
        Category("entertainment", "Entertainment", TransactionType.EXPENSE),
        Category("health", "Health", TransactionType.EXPENSE),
        Category("education", "Education", TransactionType.EXPENSE),
        Category("other_expense", "Other", TransactionType.EXPENSE)
    )

    val incomeCategories = listOf(
        Category("salary", "Salary",TransactionType.INCOME),
        Category("business", "Business",TransactionType.INCOME),
        Category("gift", "Gift",TransactionType.INCOME),
        Category("investment", "Investment",TransactionType.INCOME),
        Category("other_income", "Other",TransactionType.INCOME)
    )

    val allCategories = incomeCategories + expenseCategories

    fun getCategoryById(id: String): Category {
        return allCategories.find { it.id == id } ?: expenseCategories.last()
    }
}