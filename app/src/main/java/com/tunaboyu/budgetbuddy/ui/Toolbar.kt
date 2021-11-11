package com.tunaboyu.budgetbuddy.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tunaboyu.budgetbuddy.databinding.ActivityMainBinding

class Toolbar : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)
    setSupportActionBar(binding.toolbar)
  }
}