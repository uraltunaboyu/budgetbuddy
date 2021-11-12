package com.tunaboyu.budgetbuddy.util

import android.view.LayoutInflater
import com.tunaboyu.budgetbuddy.databinding.ActivityMainBinding

class Binding {
  companion object {
    @Volatile
    private var INSTANCE: ActivityMainBinding? = null
    
    fun initBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
      return synchronized(this) {
        val instance = ActivityMainBinding.inflate(layoutInflater)
        INSTANCE = instance
        instance
      }
    }
    
    fun getBinding(): ActivityMainBinding {
     return INSTANCE!!
    }
  }
}