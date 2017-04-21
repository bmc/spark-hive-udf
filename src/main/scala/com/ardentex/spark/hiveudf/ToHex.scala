package com.ardentex.spark.hiveudf

import org.apache.hadoop.hive.ql.exec.UDF
import org.apache.hadoop.io.LongWritable

/** This UDF takes a long integer and converts it to a hexadecimal string.
  */
class ToHex extends UDF {

  def evaluate(n: LongWritable): String = {
    Option(n)
      .map { num =>
        // Use Scala string interpolation. It's the easiest way, and it's
        // type-safe, unlike String.format().
        f"0x${num.get}%x"
      }
      .getOrElse("")
  }
}
