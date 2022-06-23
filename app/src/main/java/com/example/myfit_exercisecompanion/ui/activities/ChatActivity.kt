package com.example.myfit_exercisecompanion.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myfit_exercisecompanion.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Chats"
    }
}