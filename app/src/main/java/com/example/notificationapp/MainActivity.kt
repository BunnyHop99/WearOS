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

        //Configuracion MQTT

        val clientId = MqttClient.generateClientId()

        val client = MqttAndroidClient(this.applicationContext, MQTTHOST,clientId)

        //MQTT Subscribe

        fun setSubscription(){
            val topic = topicStr
            val qos = 1
            try {
                val subToken = client.subscribe(topic, qos)
                subToken.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        // The message was published
                        Toast.makeText(applicationContext,"suscrito",Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(
                        asyncActionToken: IMqttToken,
                        exception: Throwable
                    ) {
                        // The subscription could not be performed, maybe the user was not
                        // authorized to subscribe on the specified topic e.g. using wildcards
                    }
                }
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
                    Toast.makeText(applicationContext,"onSuccess",Toast.LENGTH_SHORT).show()
                    setSubscription()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(applicationContext,"onFailure",Toast.LENGTH_SHORT).show()
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
                client.publish(topic, message.toByteArray(),0, false)
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }

        createNotificationChannel()

        val buttonIntent = Intent(this, ReplyActivity::class.java)
        buttonIntent.putExtra("EXTRA_ARG", "Botón presionado")
        val buttonPendingIntent = PendingIntent.getActivity(
            this,
            BUTTON_INTENT_REQUEST,
            buttonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val action = NotificationCompat.Action.Builder(
            R.drawable.ic_message,
            "Voy en camino",
            buttonPendingIntent
        ).build()

        val notification = NotificationCompat.Builder(this, channelID).also {
            it.setContentTitle("Cocina")
            it.setContentText("Acude a cocina")
            it.setSmallIcon(R.drawable.ic_message)
            it.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            it.priority = NotificationCompat.PRIORITY_HIGH
            it.addAction(action)
            it.setAutoCancel(true)
        }.build()

        val notificationManager : NotificationManagerCompat = NotificationManagerCompat.from(this)

        val btnSend = findViewById<Button>(R.id.btn_newNotification)
        btnSend.setOnClickListener {
            notificationManager.notify(notificationID, notification)
        }

        val btnPublish = findViewById<Button>(R.id.btn_publish)
        btnPublish.setOnClickListener {
            publish()
        }


    }

}