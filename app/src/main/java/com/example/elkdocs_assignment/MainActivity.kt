package com.example.elkdocs_assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clockanimation.ClockSurfaceView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val surfaceView = findViewById<ClockSurfaceView>(R.id.surfaceView)
    }
}