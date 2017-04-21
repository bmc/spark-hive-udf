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
    val optRes =
      for { ts <- Option(t)     // null check
            f  <- Option(fmt) } // null check
      yield try {
        val formatter = new SimpleDateFormat(fmt)
        formatter.format(new Date(t.getTime))
      }
      catch {
        // Bad format. Return Timestmap.toString. (We could return
        // an error message, as well, but this is fine for now.)
        case _: IllegalArgumentException =>
          t.toString
      }

    optRes.getOrElse("")
  }
}
