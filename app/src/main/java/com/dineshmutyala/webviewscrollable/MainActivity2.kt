package com.dineshmutyala.webviewscrollable

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        test_button.setOnClickListener { startActivity(getIntentForMain()) }
    }
    private fun getIntentForMain() = Intent(this, MainActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        putExtra("URL", MainActivity.PETSMART_URL)
    }
}