package org.tunaboyu.budgetbuddy

import org.tunaboyu.budgetbuddy.models.Category

class CategoryHolder {
    private val categories = mutableSetOf<Category>()

    fun createCategory(name: String): Category {
        val newCategory = Category(name)
        categories.add(newCategory)
        return newCategory
    }

    fun removeCategory(category: Category) {
        categories.remove(category)
    }

    fun getCategoryIfExists(name: String): Category? {
        return categories.firstOrNull { it.toString() == name }
    }
}