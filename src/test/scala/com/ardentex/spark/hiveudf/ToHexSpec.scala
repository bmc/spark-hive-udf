package com.ardentex.spark.hiveudf

import org.apache.hadoop.io.LongWritable
import org.scalatest.{FlatSpec, Matchers}

class ToHexSpec extends FlatSpec with Matchers {
  val udf = new ToHex

  "ToHex.evaluate" should "return a valid hex string" in {
    val data = Array(
      (234908234222L, "0x36b19f31ee"),
      (0L,            "0x0"),
      (Long.MaxValue, "0x7fffffffffffffff"),
      (-10L,          "0xfffffffffffffff6")
    )

    for ((input, expected) <- data) {
      udf.evaluate(new LongWritable(input)) should be (expected)
    }
  }

  it should "return an empty string for a null input" in {
    udf.evaluate(null) should be ("")
  }
}
