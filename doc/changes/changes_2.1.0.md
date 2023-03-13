# Virtual Schema for SQLServer 2.1.0, released 2023-??-??

Code name: Dependency Upgrade on Top of 2.0.3

## Summary

Updated dependencies to replace `com.exasol:exasol-script-api` by `udf-api-java/1.0.1` as `com.exasol:exasol-script-api` had been available on discontinued maven repository `maven.exasol.com`.

## Refactorings

* #19: Updated dependencies

* ISSUE_NUMBER: description

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:1.0.0` to `1.0.1`
* Updated `com.exasol:virtual-schema-common-jdbc:10.0.1` to `10.4.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.2.0` to `6.5.1`
* Updated `com.exasol:test-db-builder-java:3.4.0` to `3.4.2`
* Updated `com.exasol:virtual-schema-common-jdbc:10.0.1` to `10.4.0`
* Updated `com.microsoft.sqlserver:mssql-jdbc:11.2.1.jre8` to `12.2.0.jre11`
* Updated `org.junit.jupiter:junit-jupiter:5.9.1` to `5.9.2`
* Updated `org.mockito:mockito-junit-jupiter:4.8.1` to `5.2.0`
* Updated `org.testcontainers:junit-jupiter:1.17.5` to `1.17.6`
* Updated `org.testcontainers:mssqlserver:1.17.5` to `1.17.6`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.0` to `0.4.2`
* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.2` to `1.2.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.8.0` to `2.9.3`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.15` to `0.16`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.3.0` to `3.4.2`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M5` to `3.0.0-M8`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.2` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M5` to `3.0.0-M8`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.2.7` to `1.3.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.10.0` to `2.14.2`
