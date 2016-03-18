package com.ardentex.spark.hiveudf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.DoubleWritable;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * This UDF takes a double and converts it to a currency string. A decimal
 * type is more suited to money than a double, but Hadoop's IO formats don't
 * seem to support decimal.
 */
public class FormatCurrency extends UDF {

  /** The actual conversion routine.
   *
   * @param n          the double
   * @param localeName the locale name string (e.g., "en_US") to use to format
   *                   the string, or null to use the default local.
   *
   * @return the formatted string
   */
  public String evaluate(Double n, String localeName) {
    Locale locale;
    if (localeName == null)
      locale = Locale.getDefault();
    else {
      String[] pieces = localeName.split("_");
      if (pieces.length != 2)
        locale = Locale.getDefault();
      else
        locale = new Locale(pieces[0], pieces[1]);
    }

    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

    if (n == null) {
      return "";
    }
    else {
      return fmt.format(n.doubleValue());
    }
  }
}
