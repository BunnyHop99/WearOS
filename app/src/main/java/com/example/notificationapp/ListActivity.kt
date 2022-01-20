package com.example.notificationapp

import android.app.Activity
import android.content.SharedPreferences

import android.os.Bundle
import android.view.View
import com.example.notificationapp.databinding.ActivityListBinding

import android.widget.*



class ListActivity : Activity() {

    private lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("preferences", MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPref.edit()

        val savedKey = sharedPref.getString("key", null)

        val key = savedKey

        val savedTable = sharedPref.getString("keytable", null)
        val savedKitchen = sharedPref.getString("keykitchen", null)
        val savedBar = sharedPref.getString("keybar", null)

        val arrayAdapter :ArrayAdapter<*>
        val notificacionesLista : MutableList<String> = mutableListOf()
        val lvDatos = findViewById<ListView>(R.id.LvDatos)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notificacionesLista)
        lvDatos.adapter = arrayAdapter

        fun removeItem(remove:Int){
            notificacionesLista.removeAt(remove)
            arrayAdapter.notifyDataSetChanged()
        }

        if (key == "keytable"){
            if (savedTable != null) {
                notificacionesLista.add(savedTable)
            }
        } else if (key == "keykitchen"){
            if (savedKitchen != null) {
                notificacionesLista.add(savedKitchen)
            }
        } else if (key == "keybar"){
            if (savedBar != null) {
                notificacionesLista.add(savedBar)
            }
        }

        lvDatos.setOnItemClickListener(){parent,view,position,id->
            notificacionesLista.get(position)
            removeItem(position)
            editor.remove(key)
            editor.apply()
        }

    }
}