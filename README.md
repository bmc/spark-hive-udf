# Sample Hive UDF project

## Introduction

This project is just an example, containing several
[Hive User Defined Functions][] (UDFs), for use in Apache Spark. It's
intended to demonstrate how to build a Hive UDF in Scala and use it within
[Apache Spark][].

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
$ ./activator package
```

That command will download the dependencies (if they haven't already been
downloaded), compile the code, run the unit tests, and create a jar file
in `target/scala-2.10`.

### Building with Maven

Honestly, I'm not a big fan of Maven; I prefer SBT or Gradle. But, if you
prefer Maven (or are simply required to use it for your project), you _can_
build this project with Maven. I've included a `pom.xml`. Just run:

```
$ mvn package
```

to build `target/hiveudf-0.0.1.jar`. Be sure to change the jar paths,
below, if you use Maven to build the jar.

**NOTE**: The `test` target currently doesn't invoke the ScalaTest unit
tests, and I really don't like Maven enough to spend the time to figure
out why.

## Running in Spark

The following Python code demonstrates the UDFs in this package and assumes
that you've packaged the code into `target/scala-2.10/hiveudf_2.10-0.0.1.jar`.
These commands assume Spark local mode, but they should also work fine within
a cluster manager like Spark Standalone or YARN.

First, fire up PySpark:

```
$ pyspark --jars target/scala-2.10/hiveudf_2.10-0.0.1.jar
```

At the PySpark prompt, enter the following. (I use IPython with PySpark,
by setting environment variable `IPYTHON` to `1`. The IPython prompts are 
displayed below. Obviously, you should not type them.)

```
In [1]: from datetime import datetime

In [2]: from collections import namedtuple

In [3]: Person = namedtuple('Person', ('first_name', 'last_name', 'birth_date', 'salary'))

In [4]: fmt = "%Y-%m-%d"

In [5]: people = [
   ...: Person('Joe', 'Smith', datetime.strptime("1993-10-20", fmt), 70000l),
   ...: Person('Jenny', 'Harmon', datetime.strptime("1987-08-02", fmt), 94000l)
   ...: ]

In [6]: df = sc.parallelize(people).toDF()

In [7]: sqlContext.sql("CREATE TEMPORARY FUNCTION to_hex AS 'com.ardentex.spark.hiveudf.ToHex'")

In [8]: sqlContext.sql("CREATE TEMPORARY FUNCTION datestring AS 'com.ardentex.spark.hiveudf.FormatTimestamp'")

In [9]: df.registerTempTable("people")

In [10]: df2 = sqlContext.sql("SELECT first_name, last_name, datestring(birth_date, 'MMMM dd, yyyy') as birth_date2, to_hex(salary) as hex_salary FROM people")
```

Then, take a look at the second DataFrame:

```
In [11]: df2.show()
+----------+---------+----------------+----------+
|first_name|last_name|     birth_date2|hex_salary|
+----------+---------+----------------+----------+
|       Joe|    Smith|October 20, 1993|   0x11170|
|     Jenny|   Harmon| August 02, 1987|   0x16f30|
+----------+---------+----------------+----------+
```

## Converting to Java

The Scala code for the UDFs is relatively straightforward and easy to
read. If you prefer to write your UDFs in Java, it shouldn't be that
difficult for you write Java versions.

## Maven



[Hive User Defined Functions]: https://cwiki.apache.org/confluence/display/Hive/LanguageManual+UDF
[Apache Spark]: http://spark.apache.org
[SBT]: http://scala-sbt.org
