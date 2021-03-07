package com.example.imagerecog

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main2.*


class MainActivity2 : AppCompatActivity(), IngredientItemClicked {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        val list = args!!.getSerializable("myList") as ArrayList<String>

        recyclerView.layoutManager= LinearLayoutManager(this)

        for (i in list) {
            Toast.makeText(baseContext, "I found $i", Toast.LENGTH_SHORT).show()
        }
      //  val items = fetchData()
        val adaptor= IngredientsListAdaptor(list as ArrayList<String>, this)
        recyclerView.adapter=adaptor


    }

  /* private fun fetchData():ArrayList<String>{
        val list = ArrayList<String>()
        for ( i in 0 until 100){
            list.add("Item $i")
        }
        return list
    }*/

    override fun onItemClicked(item: String) {
        Toast.makeText(this, "Item clicked is $item", Toast.LENGTH_SHORT).show()
    }


}