package com.example.imagerecog


import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main2.*


private val itemMap = mapOf<String, Int>(
        "Sugar" to 3,
        "Wheat Protein" to 6,
        "Salt" to 7,
        "Maida" to 3,
        "Yeast" to 5,
        "Atta" to 10,
        "Preservative" to 0
    ).withDefault { 0 }




fun foodValue(item: String): Int {

    return itemMap.getValue(item)
}


class MainActivity2 : AppCompatActivity(), IngredientItemClicked {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        val list = args!!.getSerializable("myList") as ArrayList<String>

        recyclerView.layoutManager= LinearLayoutManager(this)

        var avg:Float= 0F
        for (i in list){
            val rating:Int= foodValue(i)
            avg+=rating
        }
        avg/=list.size

      /*  for (i in list) {
            Toast.makeText(baseContext, "I found $i", Toast.LENGTH_SHORT).show()
        }*/
      //  val items = fetchData()

        val adaptor= IngredientsListAdaptor(list, this)
        recyclerView.adapter=adaptor
        val ratingText=findViewById<TextView>(R.id.ratingValue)
        ratingText.text=avg.toString()
        if (avg>5){
            ratingText.setTextColor(Color.parseColor("#00FF00"))
        }
        else {
            ratingText.setTextColor(Color.parseColor("#FF0000"))
        }


    }


    override fun onItemClicked(item: String) {
        Toast.makeText(this, "Item clicked is $item", Toast.LENGTH_SHORT).show()
    }


}