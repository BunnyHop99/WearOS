package com.example.notificationapp

import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notificationapp.databinding.ActivityMainBinding

import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.MqttException

import org.eclipse.paho.client.mqttv3.IMqttToken

import org.eclipse.paho.client.mqttv3.IMqttActionListener

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.UnsupportedEncodingException
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken

import com.google.android.gms.wearable.MessageEvent

import org.eclipse.paho.client.mqttv3.MqttCallback
import java.lang.Exception
import java.util.*


class MainActivity : Activity() {

    private var channelID = "channelID"
    private var channelName = "channelName"

    private var notificationID = 10

    private lateinit var mqttClient: MqttAndroidClient

    companion object {
        const val  INTENT_REQUEST = 0
        const val BUTTON_INTENT_REQUEST = 1
    }

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

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // We are connected
                    setSubscription()
                    statusText.text = "Connected"
                    statusText.setTextColor(Color.parseColor("#00FF0A"))
                }

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
            val resultIntent = Intent(this, ReplyActivity::class.java)
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
                /*Timer().scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {

                    }
                }, 0, 5000)*/
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })

    }

}