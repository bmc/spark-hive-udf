package com.ardentex.spark.hiveudf

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import org.scalatest.{FlatSpec, Matchers}

class FormatTimestampSpec extends FlatSpec with Matchers {
  val udf = new FormatTimestamp
  val timestampParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  "FormatTimestamp.evaluate" should "properly format a timestamp" in {
    val format = "yyyy/MMM/dd hh:mm a"
    val data = Array(
      ("2013-01-29 23:49:03", "2013/Jan/29 11:49 PM"),
      ("1941-09-03 10:01:53", "1941/Sep/03 10:01 AM"),
      ("1888-07-01 01:01:59", "1888/Jul/01 01:01 AM")
    )

    for ((input, expected) <- data) {
      val ts = new Timestamp(timestampParser.parse(input).getTime)
      udf.evaluate(ts, format) should be (expected)
    }
  }

  it should "return an empty string when the timestamp is null" in {
    udf.evaluate(null, "yyyy-MM-dd") should be ("")
  }

  it should "return an empty string when the format is null" in {
    udf.evaluate(new Timestamp((new Date).getTime), null) should be ("")
  }

  it should "return Timestamp.toString when the format is bad" in {
    val ts = new Timestamp((new Date).getTime)
    udf.evaluate(ts, "bad format") should be (ts.toString)
  }
}
