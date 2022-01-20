package com.example.notificationapp

import android.app.Activity
import android.content.SharedPreferences

import android.os.Bundle
import android.view.View
import com.example.notificationapp.databinding.ActivityListBinding

import android.widget.*
import android.widget.Toast

import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener

class ListActivity : Activity() {

    private lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("preferences", MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPref.edit()

        val savedKey = sharedPref.getInt("key", 0)

        val key = savedKey

        val savedTable = sharedPref.getString("keytable", null)
        val savedKitchen = sharedPref.getString("keykitchen", null)
        val savedBar = sharedPref.getString("keybar", null)

        val arrayAdapter :ArrayAdapter<*>
        val notificacionesLista : MutableList<String> = mutableListOf()

        val lvDatos = findViewById<View>(R.id.LvDatos) as ListView

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notificacionesLista)
        lvDatos.adapter = arrayAdapter

        fun putElement(){
            notificacionesLista.add(savedBar.toString())
            notificacionesLista.add(savedKitchen.toString())
            notificacionesLista.add(savedTable.toString())
        }

        lvDatos.onItemLongClickListener = OnItemLongClickListener { parent, view, position, arg3 ->
            arrayAdapter.remove(notificacionesLista.get(position))
            arrayAdapter.notifyDataSetChanged()
            editor.remove("keybar")
            editor.apply()
            false
        }
    }
}