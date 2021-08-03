# Virtual Schema for SQLServer 2.0.1, released 2021-08-03

Code name: Dependency Updates

## Summary

In this release we updated the dependencies. By that we fixed transitive CVE-2021-36090.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:db-fundamentals-java:0.1.1` to `0.1.2`
* Updated `com.exasol:error-reporting-java:0.2.2` to `0.4.0`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.1` to `9.0.3`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:3.5.0` to `4.0.0`
* Updated `com.exasol:hamcrest-resultset-matcher:1.4.0` to `1.4.1`
* Updated `com.exasol:test-db-builder-java:3.0.0` to `3.2.1`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.1` to `9.0.3`
* Updated `com.microsoft.sqlserver:mssql-jdbc:9.2.0.jre8` to `9.2.1.jre11`
* Updated `org.junit.jupiter:junit-jupiter:5.7.1` to `5.7.2`
* Updated `org.mockito:mockito-junit-jupiter:3.7.7` to `3.11.2`
* Updated `org.testcontainers:junit-jupiter:1.15.1` to `1.16.0`
* Updated `org.testcontainers:mssqlserver:1.15.1` to `1.16.0`

### Plugin Dependency Updates

* Added `com.exasol:error-code-crawler-maven-plugin:0.1.1`
* Updated `com.exasol:project-keeper-maven-plugin:0.4.2` to `0.10.0`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.13`
* Updated `org.apache.maven.plugins:maven-jar-plugin:2.4` to `3.2.0`
