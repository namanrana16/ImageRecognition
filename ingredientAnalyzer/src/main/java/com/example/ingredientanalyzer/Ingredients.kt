package com.example.ingredientanalyzer

import android.widget.Toast


public class Ingredients {


    companion object {

        val itemMap = mapOf<String, Int>(
            "Sugar" to 3,
            "WheatProtein" to 6,
            "WholeWheatFlour" to 10,
            "Salt" to 4,
            "Maida" to 3,
            "Yeast" to 8,
            "Refined" to 2,
            "RefinedSoyabeanOil" to 3,
            "Preservative" to 0,
            "Emulsifier" to 0,
            "Atta" to 10,
            "Preservative" to 1
        ).withDefault { 5 }

        public fun value(ing: String): Int {
            for ((k,v) in itemMap) {
                if (ing.contains(k)) {
                    return v
                }


            }
            return 5


        }


    }
}