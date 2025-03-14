# Microsoft SQL Server SQL Dialect User Guide

[Microsoft SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server) is a Relational Database Management System (RDBMS) developed by Microsoft. 

## Registering the JDBC Driver in EXAOperation

First download the [SQL Server JDBC driver](https://github.com/microsoft/mssql-jdbc/releases).
We recommend using a `jre8` driver.

Now register the driver in EXAOperation:

1. Click "Software"
1. Switch to tab "JDBC Drivers"
1. Click "Browse..."
1. Select JDBC driver file
1. Click "Upload"
1. Click "Add"
1. In a dialog "Add EXACluster JDBC driver" configure the JDBC driver (see below)

You need to specify the following settings when adding the JDBC driver via EXAOperation.

| Parameter | Value                                          |
|-----------|------------------------------------------------|
| Name      | `SQLSERVER`                                    |
| Main      | `com.microsoft.sqlserver.jdbc.SQLServerDriver` |
| Prefix    | `jdbc:sqlserver:`                              |
| Files     | `mssql-jdbc-<version>.jre8.jar`                |

## Uploading the JDBC Driver to EXAOperation

1. [Create a bucket in BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/create_new_bucket_in_bucketfs_service.htm)
1. Upload the driver to BucketFS

## Installing the Adapter Script

Upload the latest available release of [SQL Server Virtual Schema](https://github.com/exasol/sqlserver-virtual-schema/releases) to Bucket FS.

Then create a schema to hold the adapter script.

```sql
CREATE SCHEMA SCHEMA_FOR_VS_SCRIPT;
```

The SQL statement below creates the adapter script, defines the Java class that serves as entry point and tells the UDF framework where to find the libraries (JAR files) for Virtual Schema and database driver.

```sql
CREATE OR REPLACE JAVA ADAPTER SCRIPT SCHEMA_FOR_VS_SCRIPT.ADAPTER_SCRIPT_SQLSERVER AS
  %scriptclass com.exasol.adapter.RequestDispatcher;
  %jar /buckets/<BFS service>/<bucket>/virtual-schema-dist-12.0.0-sqlserver-2.1.4.jar;
  %jar /buckets/<BFS service>/<bucket>/mssql-jdbc-<version>.jre8.jar;
/
```

## Defining a Named Connection

Define the connection to SQL Server as shown below. We recommend using TLS to secure the connection.

```sql
CREATE OR REPLACE CONNECTION SQLSERVER_JDBC_CONNECTION
TO 'jdbc:sqlserver://<server name>:<port>'
USER '<user>'
IDENTIFIED BY '<passsword>';
```

## Creating a Virtual Schema

Below you see how an SQL Server Virtual Schema is created.

```sql
CREATE VIRTUAL SCHEMA <virtual schema name>
    USING SCHEMA_FOR_VS_SCRIPT.ADAPTER_SCRIPT_SQLSERVER
    WITH
    CONNECTION_NAME = 'SQLSERVER_JDBC_CONNECTION'
    CATALOG_NAME   = '<database name>'
    SCHEMA_NAME = '<schema name>';
```


Please, do not forget to specify the `SCHEMA_NAME` property.

Provide the SQL server's database name using one of the suggested ways:
1. Via the `CATALOG_NAME` property;
1. Via connection string definition: `jdbc:sqlserver://<server name>:<port>/<database name>`;

## Data Types Conversion

MS SERVER Data Type | Supported | Converted Exasol Data Type| Known limitations
--------------------|-----------|---------------------------|-------------------
BIGINT              |  ✓        | DECIMAL                   | 
BINARY              |  ×        |                           | 
BIT                 |  ✓        | BOOLEAN                   | 
CHAR                |  ✓        | CHAR                      | 
DATE                |  ✓        | DATE                      | 
DATETIME            |  ✓        | TIMESTAMP                 | 
DATETIME2           |  ✓        | TIMESTAMP                 | 
DATETIMEOFFSET      |  ✓        | VARCHAR(34)               | 
DECIMAL             |  ✓        | DECIMAL                   |  
FLOAT               |  ✓        | DOUBLE PRECISION          |  
GEOMETRY            |  ×        |                           | 
GEOGRAPHY           |  ×        |                           | 
HIERARCHYID         |  ×        |                           | 
IMAGE               |  ×        |                           | 
INT                 |  ✓        | DECIMAL                   | 
MONEY               |  ✓        | DECIMAL                   | 
NCHAR               |  ✓        | CHAR                      | 
NTEXT               |  ✓        | VARCHAR(2000000)          | 
NVARCHAR            |  ✓        | VARCHAR                   | 
NUMERIC             |  ✓        | DECIMAL                   | 
SQL_VARIANT         |  ×        |                           | 
REAL                |  ✓        | DOUBLE PRECISION          | 
ROWVERSION          |  ×        |                           | 
SMALLDATETIME       |  ✓        | TIMESTAMP                 | 
SMALLINT            |  ✓        | DECIMAL                   | 
SMALLMONEY          |  ✓        | DECIMAL                   | 
TEXT                |  ✓        | VARCHAR(2000000)          | 
TIME                |  ✓        | VARCHAR(16)               |  
TINYINT             |  ✓        | DECIMAL                   | 
UNIQUEIDENTIFIER    |  ✓        | CHAR(36)                  | 
VARBINARY           |  ×        |                           | 
VARCHAR             |  ✓        | VARCHAR                   | 
XML                 |  ×        |                           | 

## Testing information

In the following matrix you find combinations of JDBC driver and dialect version that we tested.

| Virtual Schema Version | SQL SERVER Version     | Driver Name       | Driver Version |
|------------------------|------------------------|-------------------|----------------|
| 2.1.1                  | 2019-CU17-ubuntu-20.04 | MS SQL JDBC JRE 8 | 11.2.0.jre8    |
| 2.1.2                  | 2022-CU10-ubuntu-22.04 | MS SQL JDBC JRE 8 | 12.4.2.jre8    |
| Latest, 2.1.4          | 2022-CU17-ubuntu-22.04 | MS SQL JDBC JRE 8 | 12.8.1.jre8    |


## Known Issues

* Select with boolean expressions, such as `SELECT c1 = 1` or `SELECT c2 IS NULL` won't work with this Virtual Schema 
because SQL Server doesn't support a boolean data type. But the same behaviour can be achieved using a `CASE WHEN`
expression, i.e. `SELECT CASE WHEN c1 = 1 THEN true ELSE false END` or `SELECT CASE WHEN c2 IS NULL THEN true ELSE false END`
as in this case the Virtual Schema will handle the result as an SQLServer BIT type that will be converted to an Exasol BOOLEAN.

* SQL Server doesn't support `NULLS FIRST / LAST` syntax and always orders `NULL` first when using ascending order, and last 
when using descending order. As a workaround an `NVL` function can be used with the highest value depending on the column type, 
e.g. `ORDER BY NVL(NUM, 9999999999), NVL(NAME, 'ZZZZZZ')`