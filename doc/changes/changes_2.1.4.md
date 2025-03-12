# Virtual Schema for SQLServer 2.1.4, released 2025-03-12

Code name: Boolean and ordering fixes.

## Summary

This release fixes an issue where using boolean expressions in the SELECT clause, e.g, `SELECT false FROM ...` would
generate an invalid query.

It also fixes queries using `ORDER BY` as `NULLS FIRST / LAST` is not supported by SQL Server.

## Bugfixes

* #33: "FALSE" constant is incorrectly pushed down as "1=0"
* #32: MS SQL doesn't support "NULLS LAST" part of ORDER BY clause #32

## Dependency Updates

### Test Dependency Updates

* Added `com.exasol:exasol-test-setup-abstraction-java:2.1.7`
* Updated `com.exasol:exasol-testcontainers:7.0.1` to `7.1.3`
* Updated `com.exasol:hamcrest-resultset-matcher:1.6.5` to `1.7.0`
* Updated `com.exasol:test-db-builder-java:3.5.4` to `3.6.0`
* Added `com.exasol:udf-debugging-java:0.6.15`
* Updated `com.microsoft.sqlserver:mssql-jdbc:12.4.2.jre8` to `12.8.1.jre8`
* Updated `org.slf4j:slf4j-jdk14:2.0.12` to `2.0.17`
* Updated `org.testcontainers:junit-jupiter:1.19.7` to `1.20.6`
* Updated `org.testcontainers:mssqlserver:1.19.7` to `1.20.6`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.1` to `2.0.3`
* Updated `com.exasol:project-keeper-maven-plugin:4.2.0` to `4.5.0`
* Added `com.exasol:quality-summarizer-maven-plugin:0.2.0`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.16` to `0.17`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.6.0` to `3.7.1`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.2.0` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.12.1` to `3.13.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.2.5` to `3.5.2`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.2` to `3.1.3`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.3.0` to `3.4.2`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.12.1` to `3.21.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.2.5` to `3.5.2`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0` to `3.2.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.2` to `2.18.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.11` to `0.8.12`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594` to `5.0.0.4389`
