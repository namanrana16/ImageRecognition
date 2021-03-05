package com.example.imagerecog

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val myList = intent.getSerializableExtra("mylist") as ArrayList<*>?

        val intent2 = Intent(this,MyItemRecyclerViewAdapter::class.java)
        val bundle2 = Bundle()
        bundle2.putParcelableArrayList("mylist2", myList as java.util.ArrayList<out Parcelable>?)
        intent2.putExtras(bundle2)
        startActivity(intent2)
    }


}