# SQL Server Virtual Schema 2.0.0, released 2021-02-12

Code name: Fixed boolean literal mapping, removed SQL_DIALECT proprty support

## Summary 

The SQL_DIALECT property used when executing a CREATE VIRTUAL SCHEMA from the Exasol database is obsolete from this version. 
Please, do not provide this property anymore.

## Bug Fixes

* 4: Fixed boolean literal mapping.

## Runtime Dependencies

* Updated `com.exasol:virtual-schema-common-jdbc:8.0.0` to `9.0.1`
* Updated `com.exasol:error-reporting-java:0.2.0` to `0.2.2`

## Test Dependencies

* Updated `org.junit.jupiter:junit-jupiter:5.7.0` to `5.7.1`
* Updated `org.mockito:mockito-junit-jupiter:3.6.28` to `3.7.7`
* Updated `com.exasol:exasol-testcontainers:3.3.1` to `3.5.0`
* Updated `org.testcontainers:junit-jupiter:1.15.0` to `1.15.1`
* Updated `org.testcontainers:mssqlserver:1.15.0` to `1.15.1`
* Updated `com.microsoft.sqlserver:mssql-jdbc:8.4.1.jre11` to `9.0.2.jre11`
* Updated `com.exasol:test-db-builder-java:2.0.0` to `3.0.0`
* Updated `com.exasol:hamcrest-resultset-matcher:1.2.2` to `1.4.0` 

## Plugin Dependencies

* Updated `org.jacoco:jacoco-maven-plugin:0.8.5` to `0.8.6`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.7` to `2.8.1`