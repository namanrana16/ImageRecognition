package com.example.imagerecog



import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ingredientanalyzer.Ingredients
import kotlinx.android.synthetic.main.activity_main2.*
import java.text.DecimalFormat


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
            val rating:Int= com.example.ingredientanalyzer.Ingredients.value(i)
            avg+=rating
        }
        avg/=list.size
        var df = DecimalFormat("#.##")
        avg=df.format(avg).toFloat()


      /*  for (i in list) {
            Toast.makeText(baseContext, "I found $i", Toast.LENGTH_SHORT).show()
        }*/
      //  val items = fetchData()

        val adaptor= IngredientsListAdaptor(list, this)
        recyclerView.adapter=adaptor
        val ratingText=findViewById<TextView>(R.id.ratingValue)
        ratingText.text="$avg/10"
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