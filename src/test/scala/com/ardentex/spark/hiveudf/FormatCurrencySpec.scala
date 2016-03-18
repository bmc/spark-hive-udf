package com.ardentex.spark.hiveudf

import java.util.Locale

import org.apache.hadoop.io.{DoubleWritable, FloatWritable, LongWritable}
import org.scalatest.{FlatSpec, Matchers}

class FormatCurrencySpec extends FlatSpec with Matchers {
  val udf = new FormatCurrency

  def doTest(data: Array[(Double, String)], lang: String): Unit = {
    for ((input, expected) <- data)
    //udf.evaluate(new DoubleWritable(input),lang) should be (expected)
    udf.evaluate(input,lang) should be (expected)
  }

  "FormatCurrency" should "return a valid currency string for the US locale" in {
    val data = Array(
      (2999100.01, "$2,999,100.01"),
      (.11,        "$0.11"),
      (999.0,      "$999.00"),
      (1122.0,     "$1,122.00")
    )

    doTest(data, "en_US")
  }

  it should "return a valid currency string for the en_GB locale" in {
    val data = Array(
      (2999100.01, "£2,999,100.01"),
      (.11,        "£0.11"),
      (999.0,      "£999.00"),
      (1122.0,     "£1,122.00")
    )

    doTest(data, "en_GB")
  }

  it should "return a currency string in the default locale for a bad locale string" in {
    withDefaultLocale("fr", "FR") {
      val data = Array(
        (2999100.01, "2 999 100,01 €"),
        (.11,        "0,11 €"),
        (999.0,      "999,00 €"),
        (1122.0,     "1 122,00 €")
      )

      doTest(data, "nnyy")
    }
  }

  it should "return a currency string in the default locale if no locale is specified" in {
    withDefaultLocale("se", "SE") {
      val data = Array(
        (2999100.01, "SEK 2,999,100.01"),
        (.11,        "SEK 0.11"),
        (999.0,      "SEK 999.00"),
        (1122.0,     "SEK 1,122.00")
      )

      doTest(data, null)
    }
  }

  it should "return a valid currency string for the jp_JP locale" in {
    val data = Array(
      (2999100.01, "JPY 2,999,100"),
      (.11,        "JPY 0"),
      (999.0,      "JPY 999"),
      (1122.0,     "JPY 1,122")
    )

    doTest(data, "jp_JP")
  }


  it should "return an empty string for a null input" in {
    udf.evaluate(null, null) should be ("")
  }

  private def withDefaultLocale(lang: String, variant: String)
                               (code: => Unit): Unit = {
    val locale = Locale.getDefault
    val newLocale = new Locale(lang, variant)
    Locale.setDefault(newLocale)

    try {
      code
    }

    finally {
      Locale.setDefault(locale)
    }

  }
}


