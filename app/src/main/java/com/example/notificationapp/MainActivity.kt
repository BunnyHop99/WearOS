package com.example.notificationapp

import android.annotation.SuppressLint
import android.app.*

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color

import android.os.Bundle

import android.view.GestureDetector
import android.view.MotionEvent

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notificationapp.databinding.ActivityMainBinding

import org.eclipse.paho.android.service.MqttAndroidClient

import org.eclipse.paho.client.mqttv3.MqttException

import org.eclipse.paho.client.mqttv3.IMqttToken

import org.eclipse.paho.client.mqttv3.IMqttActionListener

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken


import org.eclipse.paho.client.mqttv3.MqttCallback
import java.lang.Exception
import java.lang.Math.abs



class MainActivity : Activity(), GestureDetector.OnGestureListener {

    private var channelID = "channelID"
    private var channelName = "channelName"

    private var notificationID = 10

    //Broker data
    private val MQTTHOST : String = "wss://awesome-engineer.cloudmqtt.com:443/mqtt"
    private val USERNAME : String = "vinnyiht"
    private val PASSWORD : String = "pmH4p_mZ-ae3"
    val topicStr : String = "topic"

    private fun createNotificationChannel() {

            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelID, channelName, importance).apply {
                lightColor = Color.RED
                enableLights(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
    }


    lateinit var gestureDetector: GestureDetector
    var x2:Float = 0.0f
    var x1:Float = 0.0f

    companion object{
        const val MIN_DISTANCE = 150
    }

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gestureDetector = GestureDetector(this, this)

        fun guardarNot(){
            val sharedPreferences : SharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("keytable", "Mesa 4")
            editor.putString("keykitchen", "Cocina")
            editor.putString("keybar", "Bar")
            editor.apply()
        }

        val statusText = findViewById<TextView>(R.id.statusText)

        //Configuracion MQTT

        val clientId = MqttClient.generateClientId()

        val client = MqttAndroidClient(this.applicationContext, MQTTHOST,clientId)

        //MQTT Subscribe

        fun setSubscription(){
            val topic = topicStr
            val qos = 1
            try {
                client.subscribe(topic, qos)
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }

        val options = MqttConnectOptions()
        options.userName = USERNAME
        options.password = PASSWORD.toCharArray()
        options.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1

        try {
            val token = client.connect(options)
            token.actionCallback = object : IMqttActionListener {

                @SuppressLint("SetTextI18n")
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // We are connected
                    setSubscription()
                    statusText.text = "Connected"
                    statusText.setTextColor(Color.parseColor("#00FF0A"))
                }

                @SuppressLint("SetTextI18n")
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(applicationContext,"Connected failed",Toast.LENGTH_LONG).show()
                    statusText.text = "Disconnected"
                    statusText.setTextColor(Color.parseColor("#FF0000"))
                }

            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }

        //MQTT Publish

        fun publish(){
            val topic = topicStr
            val message = "hola"
            try {
                client.publish(topic, message.toByteArray(),1, false)
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, channelID).also {
            val resultIntent = Intent(this, ListActivity::class.java)
            val resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            it.setSmallIcon(R.drawable.ic_message)
            it.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            it.priority = NotificationCompat.PRIORITY_HIGH
            it.setAutoCancel(true)
            it.setContentIntent(resultPendingIntent)
        }.build()

        val notificationManager : NotificationManagerCompat = NotificationManagerCompat.from(this)

        val btnSend = findViewById<Button>(R.id.btn_newNotification)
        btnSend.setOnClickListener {
            publish()
        }

        //subscribe
        client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                Toast.makeText(applicationContext,"Connection lost",Toast.LENGTH_LONG).show()
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                notificationManager.notify(notificationID, notification)
                guardarNot()
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        gestureDetector.onTouchEvent(event)

        when (event?.action){

            0-> {
                x1 = event.x
            }

            1-> {
                x2 = event.x

                val valueX:Float = x2-x1

                if(abs(valueX) > MIN_DISTANCE) {
                    if (x1 > x2){
                        val resultIntent = Intent(this, ListActivity::class.java)
                        startActivity(resultIntent)
                    }
                }

            }
        }

        return super.onTouchEvent(event)
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onShowPress(p0: MotionEvent?) {
        TODO("Not yet implemented")
    }


    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onScroll(
        p1: MotionEvent?,
        p2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {
        //TODO("Not yet implemented")
    }

    override fun onFling(
        p0: MotionEvent?,
        p1: MotionEvent?,
        p2: Float,
        p3: Float
    ): Boolean {
        //TODO("Not yet implemented")
        return false
    }

}