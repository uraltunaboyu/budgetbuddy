package com.tunaboyu.budgetbuddy

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class OnSwipeTouchListener(context: Context, val onSwipeLeft: () -> Unit, val onSwipeRight: () -> Unit): View.OnTouchListener {
  private val gestureDetector = GestureDetector(context, GestureListener())
  
  override fun onTouch(view: View, event: MotionEvent): Boolean {
    return gestureDetector.onTouchEvent(event)
  }
  
  inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
    private val SWIPE_DISTANCE_THRESHOLD = 75
    private val SWIPE_VELOCITY_THRESHOLD = 75
    
    override fun onDown(e: MotionEvent?): Boolean {
      return true
    }
    
    override fun onFling(
      e1: MotionEvent?,
      e2: MotionEvent?,
      velocityX: Float,
      velocityY: Float
    ): Boolean {
      if (e1 == null || e2 == null) return false
      val distanceX = e2.x - e1.x
      val distanceY = e2.y - e1.y
      
      if (abs(distanceX) > abs(distanceY) && abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && abs(
          velocityX
        ) > SWIPE_VELOCITY_THRESHOLD
      ) {
        if (distanceX > 0) {
          onSwipeRight()
        } else {
          onSwipeLeft()
        }
        return true
      }
      return false
    }
  }
}