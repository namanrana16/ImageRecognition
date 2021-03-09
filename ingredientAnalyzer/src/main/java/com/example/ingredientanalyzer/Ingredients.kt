package com.example.ingredientanalyzer

private val itemMap = mapOf<String, Int>(
    "Sugar" to 3,
    "Wheat Protein" to 6,
    "Salt" to 7,
    "Maida" to 3,
    "Yeast" to 5,
    "Atta" to 10,
    "Preservative" to 0
).withDefault { 0 }


class Ingredients {

    fun value(ing: String): Int {
        return itemMap.getValue(ing)
    }




}