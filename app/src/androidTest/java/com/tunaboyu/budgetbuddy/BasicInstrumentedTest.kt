package com.tunaboyu.budgetbuddy

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.tunaboyu.budgetbuddy.model.Budget
import com.tunaboyu.budgetbuddy.model.Transaction
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class BasicInstrumentedTest {
  private lateinit var budget: Budget
  private lateinit var transaction: Transaction
  
  @get:Rule
  var activityRule = ActivityScenarioRule(MainActivity::class.java)
  
  @Before
  fun initVars() {
    budget = Budget(20)
    transaction = Transaction("10/10/10", 10, "")
    println("Did init")
  }
  
  @Test
  fun basicTest() {
    assertTrue(true)
  }
}