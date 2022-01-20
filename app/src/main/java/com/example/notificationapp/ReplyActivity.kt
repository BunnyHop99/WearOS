package com.example.notificationapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.notificationapp.databinding.ActivityReplyBinding

class ReplyActivity : Activity() {

    private lateinit var binding: ActivityReplyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}