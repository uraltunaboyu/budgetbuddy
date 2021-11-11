package com.tunaboyu.budgetbuddy.ui

import android.content.Context
import android.graphics.Color
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View

class TransactionCardTouchListener(
  val view: View,
  context: Context,
  private val editView: () -> Unit,
  private val deleteView: () -> Unit
) : View.OnTouchListener {
  private val deleteDistance = 475F
  private val editDistance = -deleteDistance
  private val gestureDetector = GestureDetector(context, GestureListener())
  private var readyToDelete = false
  private var readyToEdit = false
  
  override fun onTouch(view: View, event: MotionEvent): Boolean {
    if (event.action == ACTION_UP) {
      val animation = view.animate().translationX(if (readyToDelete) 1000F else 0F)
      if (readyToDelete) {
        animation.withEndAction(deleteView)
      } else if (readyToEdit) {
        animation.withEndAction(editView)
        readyToEdit = false
        view.setBackgroundColor(Color.WHITE)
      }
      animation.start()
    }
    view.performClick()
    return gestureDetector.onTouchEvent(event)
  }
  
  inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
    override fun onScroll(
      e1: MotionEvent?,
      e2: MotionEvent?,
      distanceX: Float,
      distanceY: Float
    ): Boolean {
      if (e1 == null || e2 == null) return false
      val distanceX = e2.x - e1.x
      view.x += distanceX
      readyToDelete = view.x > deleteDistance
      readyToEdit = view.x < editDistance
      if (readyToDelete) view.setBackgroundColor(Color.RED)
      if (readyToEdit) view.setBackgroundColor(Color.YELLOW)
      if (!readyToEdit && !readyToDelete) view.setBackgroundColor(Color.WHITE)
      return true
    }
    
    override fun onDown(e: MotionEvent?): Boolean {
      return true
    }
  }
}