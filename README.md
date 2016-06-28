# Sample Hive UDF project

## Introduction

This project is just an example, containing several
[Hive User Defined Functions][] (UDFs), for use in Apache Spark. It's
intended to demonstrate how to build a Hive UDF in Scala or Java and use it
within [Apache Spark][].

## Why use a Hive UDF?

One especially good use of Hive UDFs is with Python and DataFrames.
Native Spark UDFs written in Python are slow, because they have to be
executed in a Python process, rather than a JVM-based Spark Executor.
For a Spark Executor to run a Python UDF, it must:

* send data from the partition over to a Python process associated with
  the Executor, and
* wait for the Python process to deserialize the data, run the UDF on it,
  reserialize the data, and send it back.

By contrast, a Hive UDF, whether written in Scala or Java, can be executed
in the Executor JVM, _even if the DataFrame logic is in Python_.

There's really only one drawback: a Hive UDF _must_ be invoked via SQL.
You can't call it as a function from the DataFrame API.

## Building

This project builds with [SBT][], but you don't have to download SBT. Just use
the `activator` script in the root directory. To build the jar file, use
this command:

```
$ ./activator jar
```

That command will download the dependencies (if they haven't already been
downloaded), compile the code, run the unit tests, and create a jar file
in `target/scala-2.10`.

### Building with Maven

Honestly, I'm not a big fan of Maven. I had a Maven `pom.xml` file here, but
I got tired of maintaining an annoying XML Maven build file, when I'm already
maintaining an SBT build file.  Just use `activator`, as described above.

## Running in Spark

The following Python code demonstrates the UDFs in this package and assumes
that you've packaged the code into `target/scala-2.10/hiveudf_2.10-0.0.1.jar`.
These commands assume Spark local mode, but they should also work fine within
a cluster manager like Spark Standalone or YARN.

You can also use Hive UDFs from Scala, by the way.

First, fire up PySpark:

```
$ pyspark --jars target/scala-2.10/hiveudf_2.10-0.0.1.jar
```

At the PySpark prompt, enter the following. (If you're using IPython,
`%paste` works best.)

```
from datetime import datetime
from collections import namedtuple
from decimal import Decimal

Person = namedtuple('Person', ('first_name', 'last_name', 'birth_date', 'salary', 'children'))

fmt = "%Y-%m-%d"

people = [
    Person('Joe', 'Smith', datetime.strptime("1993-10-20", fmt), 70000.0, 2l),
    Person('Jenny', 'Harmon', datetime.strptime("1987-08-02", fmt), 94000.0, 1l)
]

df = sc.parallelize(people).toDF()

sqlContext.sql("CREATE TEMPORARY FUNCTION to_hex AS 'com.ardentex.spark.hiveudf.ToHex'")
sqlContext.sql("CREATE TEMPORARY FUNCTION datestring AS 'com.ardentex.spark.hiveudf.FormatTimestamp'")
sqlContext.sql("CREATE TEMPORARY FUNCTION currency AS 'com.ardentex.spark.hiveudf.FormatCurrency'")

df.registerTempTable("people")
df2 = sqlContext.sql("SELECT first_name, last_name, datestring(birth_date, 'MMMM dd, yyyy') as birth_date2, currency(salary, 'en_US') as pr_salary, to_hex(children) as hex_children FROM people")
```

Then, take a look at the second DataFrame:

```
df2.show()

+----------+---------+----------------+----------+------------+
|first_name|last_name|     birth_date2| pr_salary|hex_children|
+----------+---------+----------------+----------+------------+
|       Joe|    Smith|October 20, 1993|$70,000.00|         0x2|
|     Jenny|   Harmon| August 02, 1987|$94,000.00|         0x1|
+----------+---------+----------------+----------+------------+
```

## "Why did you write these things in Scala?"

Because, after writing Scala for the last 7 years, I find Java annoying. But,
I did include a Java UDF in this repo; take a look at the `FormatCurrency` UDF. 
The others are in Scala and, really, they're not hard to translate
to Java.

[Hive User Defined Functions]: https://cwiki.apache.org/confluence/display/Hive/LanguageManual+UDF
[Apache Spark]: http://spark.apache.org
[SBT]: http://scala-sbt.org
