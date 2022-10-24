# Virtual Schema for SQLServer 2.0.3, released 2022-??-??

Code name: 2.0.3: Upgrade dependencies

This release upgrades dependencies and reduces the number of runtime dependencies, fixing CVE-2022-21724 in the PostgreSQL JDBC driver.

## Bugfixes

* #15: Updated virtual-schema-common-jdbc and fixed vulnerabilities.
* #13: Fixed broken links check

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:db-fundamentals-java:0.1.2` to `0.1.3`
* Updated `com.exasol:error-reporting-java:0.4.0` to `1.0.0`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.3` to `10.0.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:5.1.0` to `6.2.0`
* Updated `com.exasol:hamcrest-resultset-matcher:1.5.0` to `1.5.2`
* Updated `com.exasol:test-db-builder-java:3.2.1` to `3.4.0`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.3` to `10.0.1`
* Updated `com.microsoft.sqlserver:mssql-jdbc:9.4.0.jre8` to `11.2.1.jre8`
* Updated `org.junit.jupiter:junit-jupiter:5.8.1` to `5.9.1`
* Updated `org.mockito:mockito-junit-jupiter:4.0.0` to `4.8.1`
* Updated `org.testcontainers:junit-jupiter:1.16.0` to `1.17.5`
* Updated `org.testcontainers:mssqlserver:1.16.0` to `1.17.5`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.3.1` to `0.4.0`
* Updated `com.exasol:error-code-crawler-maven-plugin:0.7.0` to `1.1.2`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.0` to `2.8.0`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.13` to `0.15`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.8.1` to `3.10.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M3` to `3.0.0-M5`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.0` to `3.2.2`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M3` to `3.0.0-M5`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.8.1` to `2.10.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.6` to `0.8.8`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0` to `3.2.0`
