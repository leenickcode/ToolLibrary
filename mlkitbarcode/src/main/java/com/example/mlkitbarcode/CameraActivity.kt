package com.example.mlkitbarcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout

class CameraActivity : AppCompatActivity() {

    lateinit var  framelayout:FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        framelayout = findViewById(R.id.framelayout)
        supportFragmentManager.beginTransaction().replace(R.id.framelayout,CameraFragment.newInstance("",""))
            .commit()
    }
}