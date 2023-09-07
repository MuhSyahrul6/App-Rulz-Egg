package com.c213310029_muhammadsyahrulromadhon.kelompok13

import  android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MainActivity : AppCompatActivity() {

    private val brokerUrl = "tcp://free.mqtt.iyoti.id:1883"
    private val clientId = MqttClient.generateClientId()
    private val topicTemperature = "Syahrulgtg/temperature"
    private val topicHumidity = "Syahrulgtg/humidity"

    private lateinit var mqttClient: MqttClient
    private lateinit var suhuTextView: TextView
    private lateinit var kelembabanTextView: TextView
    private lateinit var buttonConnect: Button
    private lateinit var buttonDisconnect: Button
    private var isMqttConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        suhuTextView = findViewById(R.id.suhuTextView)
        kelembabanTextView = findViewById(R.id.kelembabanTextView)
        buttonConnect = findViewById(R.id.buttonConnect)
        buttonDisconnect = findViewById(R.id.buttonDisconnect)

        buttonConnect.setOnClickListener {
            connectToMqttBroker()
            showToast("terkoneksi dengan MQTT!")
        }

        buttonDisconnect.setOnClickListener {
            disconnectFromMqttBroker()
            showToast("terputus koneksi dari MQTT!")
        }
    }

    private fun connectToMqttBroker() {
        try {
            mqttClient = MqttClient(brokerUrl, clientId, MemoryPersistence())
            mqttClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.d("MQTT", "Connection lost.")
                    isMqttConnected = false
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val value = message?.payload?.toString(Charsets.UTF_8)
                    Log.d("MQTT", "Message received: $value")
                    runOnUiThread { handleSensorData(topic, value) }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Not used in this example
                }
            })

            val options = MqttConnectOptions()
            options.isCleanSession = true

            mqttClient.connect(options)
            mqttClient.subscribe(topicTemperature, 0)
            mqttClient.subscribe(topicHumidity, 0)

            // Disable the Connect button and enable the Disconnect button
            buttonConnect.isEnabled = false
            buttonDisconnect.isEnabled = true
            isMqttConnected = true

        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun disconnectFromMqttBroker() {
        try {
            mqttClient.disconnect()

            // Enable the Connect button and disable the Disconnect button
            buttonConnect.isEnabled = true
            buttonDisconnect.isEnabled = false
            isMqttConnected = false
            clearSensorData()

        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun handleSensorData(topic: String?, value: String?) {
        if (isMqttConnected) {
            when (topic) {
                topicTemperature -> {
                    val suhu = value?.toIntOrNull()
                    if (suhu != null) {
                        suhuTextView.text = suhu.toString()
                        if (suhu > 37) {
                            showTemperatureWarningDialog()
                        }
                    }
                }
                topicHumidity -> {
                    val kelembaban = value?.toIntOrNull()
                    if (kelembaban != null) {
                        kelembabanTextView.text = kelembaban.toString()
                    }
                }
            }
        }
    }

    private fun clearSensorData() {
        suhuTextView.text = ""
        kelembabanTextView.text = ""
    }

    private fun showTemperatureWarningDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Peringatan")
            .setMessage("Suhu temperature terlalu panas!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}