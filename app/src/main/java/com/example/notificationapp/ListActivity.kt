package com.example.notificationapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

import android.os.Bundle
import android.view.View
import com.example.notificationapp.databinding.ActivityListBinding

import android.widget.*
import android.widget.Toast

import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import com.google.android.gms.auth.api.signin.internal.Storage
import android.widget.TextView

import android.view.LayoutInflater
import java.lang.Override as Override1


class ListActivity : Activity() {

    private lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arrayAdapter: ArrayAdapter<*>
        val lista = findViewById<ListView>(R.id.LvDatos)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notificacionesLista)
        lista.adapter = arrayAdapter

        lista.setOnItemLongClickListener(OnItemLongClickListener { arg0, arg1, pos, id -> // TODO Auto-generated method stub
            delElement(pos)
            arrayAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Mesero en camino", Toast.LENGTH_LONG).show()
            true
        })
    }
}