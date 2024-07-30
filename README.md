# CSV Loader

CSV Loader is a tool for loading CSV files into a DB.

The use of CSV Loader as of v0.8 is specific to [Flyway's Java-based Migration](https://flywaydb.org/documentation/concepts/migrations#java-based-migrations). We are planning to run the jar alone and support the Maven Plugin etc. with the next version migration.

## Required software

- Java 17+
- Maven/Gradle

## Usage (Flyway Java-based Migration)

Here, I will explain how to use CSV Loader within Flyway java-based migration.
First, configure the project with the [Flyway Maven Plugin](https://flywaydb.org/documentation/usage/maven/) working. The directory structure is as follows.

- Directory structure

```txt
project
├── pom.xml
└── src/main
    ├── java
    │   └── db/migration
    │       └── V2__AddRecords.java
    └── resources
        └── db/migration
            ├── V1__CreateTables.sql
            └── V2__AddRecords
                ├── tabe-list.txt
                ├── TABLE_1.csv
                └── subdir
                    └── TABLE_2.csv
```

Here, let's say we create a table with v1__createtables.sql and load the data from a CSV file with v2__addrecords.java.

- V1__CreateTables.sql

```sql
CREATE TABLE TABLE_1 (
  COLUMN_1 INT,
  COLUMN_2 VARCHAR(10)
);

:
```

Next, add the CSV Loader dependency to your pom.xml.

- pom.xml

```xml
  <dependencies>
    <dependency>
      <groupId>dev.aulait.cl</groupId>
      <artifactId>csv-loader-flyway</artifactId>
      <version>0.8</version>
    </dependency>
  </dependences>
```

Next, create a Migration class to load data. This class inherits BaseJavacsvMigration.

- V2__AddRecords.java

```java
package db.migration;

import dev.aulayt.csvloader.flyway.basejavacsvmigration;

@SuppressWarnings("squid:S101")
public class V2__AddRecords extends BaseJavaCsvMigration {}
```

In table-list.txt, the table names of the tables to be loaded in 1 version are described line by line. Loading is performed sequentially from top to bottom.

- table-list.txt

```txt
TABLE_1
subdir/TABLE_2
```

In the CSV file, column names are listed in the first line, and data to be loaded after the second line is described. 
If [null] is set, null will be registered.

- TABLE_1.csv

```csv
COLUMN_1,COLUMN_2
1,One
2,Two
3,Three
4,[null]
```

After completing the above preparations, execute Flyway Migration with the following command.

```sh
mvn flyway:migrate
```

Once the migration is successfully completed, CSV data will be loaded into TABLE_1.