package com.c213310029_muhammadsyahrulromadhon.kelompok13

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button

class MainAwal : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.tampilanawal)

            var handler = Handler ()
            handler.postDelayed({
                var intent = Intent(this@MainAwal, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
        }
    }