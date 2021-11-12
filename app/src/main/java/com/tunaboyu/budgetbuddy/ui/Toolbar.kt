package com.tunaboyu.budgetbuddy.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tunaboyu.budgetbuddy.databinding.ActivityMainBinding
import com.tunaboyu.budgetbuddy.util.Binding

class Toolbar : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = Binding.getBinding()
    val view = binding.root
    setContentView(view)
    setSupportActionBar(binding.toolbar)
  }
}