package com.ardentex.spark.hiveudf

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import org.apache.hadoop.hive.ql.exec.UDF

/** This UDF takes a SQL Timestamp and converts it to a string, using a
  * Java `SimpleDateFormat` string to dictate the format.
  */
class FormatTimestamp extends UDF {

  def evaluate(t: Timestamp, fmt: String): String = {
    if ((t == null) || (fmt == null)) {
      ""
    }
    else {
      try {
        val formatter = new SimpleDateFormat(fmt)
        formatter.format(new Date(t.getTime))
      }
      catch {
        // Bad format possibly. Return Timestmap.toString. (We could return
        // an error message, as well, but this is fine for now.)
        case _: Exception =>
          t.toString
      }
    }
  }
}
