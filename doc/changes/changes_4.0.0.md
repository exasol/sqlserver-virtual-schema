# Virtual Schema for SQLServer 4.0.0, released 2026-10-29

Code name: Safer dependency parsing

## Summary

This release fixes vulnerabilities in test dependencies used to process class files and SSH host keys.

## Breaking Changes

* This project now requires Java 17 and is not compatible with Exasol 8.

## Security

* #54: Fixed CVE-2026-110257 (unvalidated bootstrap method allocation) in dependency `org.ow2.asm:asm:jar:9.10.1:test`
* #55: Fixed CVE-2026-110258 (excessive subroutine memory allocation) in dependency `org.ow2.asm:asm:jar:9.10.1:test`
* #56: Fixed CVE-2026-110259 (recursive jump processing) in dependency `org.ow2.asm:asm:jar:9.10.1:test`
* #57: Fixed CVE-2026-110260 (quadratic frame computation) in dependency `org.ow2.asm:asm:jar:9.10.1:test`
* #58: Fixed CVE-2026-110261 (slow frame computation) in dependency `org.ow2.asm:asm:jar:9.10.1:test`
* #59: Fixed CVE-2026-110263 (recursive signature parsing) in dependency `org.ow2.asm:asm:jar:9.10.1:test`
* #52: Fixed CVE-2026-110262 (excessive constructor frame allocation) in dependency `org.ow2.asm:asm-commons:jar:9.10.1:test`
* #53: Fixed CVE-2026-110270 (exponential subroutine inlining allocation) in dependency `org.ow2.asm:asm-commons:jar:9.10.1:test`
* #50: Fixed CVE-2026-86231 (improper certificate revocation check) in dependency `com.github.mwiede:jsch:jar:2.28.5:test`
* Fixed CVE-2026-116486 (incorrect symbolic name to object mapping) in dependency `org.objenesis:objenesis:jar:3.3:test`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:14.0.4` to `14.1.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:3.0.0` to `3.0.1`
* Updated `com.exasol:exasol-testcontainers:8.0.1` to `8.0.2`
* Updated `com.exasol:hamcrest-resultset-matcher:1.7.3` to `1.7.4`
* Updated `com.exasol:test-db-builder-java:4.0.2` to `4.0.3`
* Updated `com.exasol:udf-debugging-java:0.6.20` to `0.6.22`
* Updated `com.exasol:virtual-schema-common-jdbc:14.0.4` to `14.1.1`
* Updated `com.microsoft.sqlserver:mssql-jdbc:13.4.0.jre8` to `13.4.0.jre11`
* Updated `org.junit.jupiter:junit-jupiter:5.14.4` to `6.1.3`
* Updated `org.mockito:mockito-junit-jupiter:5.23.0` to `5.24.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.18` to `2.0.20`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.0` to `2.1.3`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.2`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:10.0.0` to `10.0.1`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.1` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.15.0` to `3.16.0`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:null` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.6` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.4` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.5.0` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.6` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0` to `3.3.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.3` to `1.8.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.21.0` to `2.23.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.15` to `0.8.16`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970` to `5.8.0.7211`
