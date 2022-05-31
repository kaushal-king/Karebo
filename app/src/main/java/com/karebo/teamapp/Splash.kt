package com.karebo.teamapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_splash)
        // val content: View = findViewById(android.R.id.content)
        val intent = Intent(this@Splash, Drawer::class.java)
        startActivity(intent)
        finish()
//        content.viewTreeObserver.addOnPreDrawListener(
//            object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    return if (splashViewModel.getIsReady()) {
//
//                        content.viewTreeObserver.removeOnPreDrawListener(this)
//                        true
//
//                    } else {
//                        false
//                    }
//                }
//            }
//        )
    }
}