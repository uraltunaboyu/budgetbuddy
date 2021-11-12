package com.tunaboyu.budgetbuddy.util

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Locale.ENGLISH

class Converters {
  companion object {
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy", ENGLISH)
  }
  @TypeConverter
  fun fromDateLong(value: Long): LocalDate {
    return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate()
  }
  
  @TypeConverter
  fun toDateLong(value: LocalDate): Long {
    return value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
  }
}