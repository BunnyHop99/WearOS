package com.example.notificationapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView


val notificacionesLista : MutableList<String> = mutableListOf()

fun putElement(sb : String){
    notificacionesLista.add(sb)
}

fun delElement(position : Int){
    notificacionesLista.removeAt(position)
}



