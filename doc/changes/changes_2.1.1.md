# Virtual Schema for SQLServer 2.1.1, released 2023-??-??

Code name: Dependency Upgrade on Top of 2.1.0

## Summary

This release fixes vulnerability CVE-2023-42503 in transitive dependency to `org.apache.commons:commons-compress` via `exasol-testcontainers` by updating dependencies.

## Security

* #23: Fixed vulnerability CVE-2023-42503 in test dependency `org.apache.commons:commons-compress`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:10.4.0` to `11.0.2`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.5.1` to `6.6.2`
* Updated `com.exasol:hamcrest-resultset-matcher:1.5.2` to `1.6.1`
* Updated `com.exasol:test-db-builder-java:3.4.2` to `3.5.1`
* Updated `com.exasol:virtual-schema-common-jdbc:10.4.0` to `11.0.2`
* Updated `com.microsoft.sqlserver:mssql-jdbc:11.2.1.jre8` to `12.4.1.jre11`
* Updated `org.junit.jupiter:junit-jupiter:5.9.2` to `5.10.0`
* Updated `org.mockito:mockito-junit-jupiter:5.2.0` to `5.6.0`
* Updated `org.testcontainers:junit-jupiter:1.17.6` to `1.19.1`
* Updated `org.testcontainers:mssqlserver:1.17.6` to `1.19.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.2` to `1.3.0`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.3` to `2.9.12`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.4.2` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.1` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.1.0` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M8` to `3.1.2`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M8` to `3.1.2`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:2.0.1`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.3.0` to `1.5.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.14.2` to `2.16.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.8` to `0.8.10`
