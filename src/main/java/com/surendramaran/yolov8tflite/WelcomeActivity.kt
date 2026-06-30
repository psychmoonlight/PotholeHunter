// WelcomeActivity.kt
package com.surendramaran.yolov8tflite


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.surendramaran.yolov8tflite.databinding.ActivityWelcomeBinding


class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.startButton.setOnClickListener {
            // Start MainActivity when the button is clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.MapButton.setOnClickListener {
            // Start MainActivity when the button is clicked
            val intent = Intent(this, ShowMapActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
