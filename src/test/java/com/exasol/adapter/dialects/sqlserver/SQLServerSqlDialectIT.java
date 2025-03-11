package com.exasol.adapter.dialects.sqlserver;

import static com.exasol.dbbuilder.dialects.exasol.AdapterScript.Language.JAVA;
import static com.exasol.matcher.ResultSetMatcher.matchesResultSet;
import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.matcher.TypeMatchMode;

@Tag("integration")
@Testcontainers
class SQLServerSqlDialectIT {
    private static final String MS_SQL_SERVER_CONTAINER_NAME = "mcr.microsoft.com/mssql/server:2022-CU17-ubuntu-22.04";
    private static final String SCHEMA_SQL_SERVER = "SCHEMA_SQL_SERVER";
    private static final String TABLE_SQL_SERVER_NUMERIC_AND_DATE_DATA_TYPES = "TABLE_SQL_SERVER_NUMERIC_AND_DATE";
    private static final String TABLE_SQL_SERVER_STRING_DATA_TYPES = "TABLE_SQL_SERVER_STRING";
    private static final String TABLE_SQL_SERVER_SIMPLE = "TABLE_SQL_SERVER_SIMPLE";
    private static final int MS_SQL_SERVER_PORT = 1433;
    private static final String JDBC_CONNECTION_NAME = "JDBC";
    private static final String VIRTUAL_SCHEMA_JDBC = "VIRTUAL_SCHEMA_JDBC";
    private static final String JDBC_DRIVER_NAME = "mssql-jdbc.jar";
    private static final Path JDBC_DRIVER_PATH = Path.of("target/sqlserver-driver/" + JDBC_DRIVER_NAME);
    public static final String VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION = "virtual-schema-dist-12.0.0-sqlserver-2.1.4.jar";
    public static final Path PATH_TO_VIRTUAL_SCHEMAS_JAR = Path.of("target", VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
    public static final String SCHEMA_EXASOL = "SCHEMA_EXASOL";
    public static final String ADAPTER_SCRIPT_EXASOL = "ADAPTER_SCRIPT_EXASOL";
    public static final String DOCKER_IP_ADDRESS = "172.17.0.1";
    public static final String JDBC_DRIVER_CONFIGURATION_FILE_NAME = "settings.cfg";

    private static Connection exasolConnection;

    @Container
    private static final MSSQLServerContainer<?> MS_SQL_SERVER_CONTAINER = new MSSQLServerContainer<>(
            MS_SQL_SERVER_CONTAINER_NAME);
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> EXASOL_CONTAINER = new ExasolContainer<>()
            .withReuse(true);

    @BeforeAll
    static void beforeAll() throws BucketAccessException, TimeoutException, SQLException, FileNotFoundException {
        uploadDriverToBucket();
        uploadVsJarToBucket();
        createSqlServerSchema();
        createSimpleTable();
        createSqlServerTableNumericAndDateDataTypes();
        createSqlServerTableStringDataTypes();
        exasolConnection = EXASOL_CONTAINER.createConnection("");
        final ExasolObjectFactory exasolFactory = new ExasolObjectFactory(exasolConnection);
        final ExasolSchema exasolSchema = exasolFactory.createSchema(SCHEMA_EXASOL);
        final AdapterScript adapterScript = createAdapterScript(exasolSchema);
        final String connectionString = "jdbc:sqlserver://" + DOCKER_IP_ADDRESS + ":"
                + MS_SQL_SERVER_CONTAINER.getMappedPort(MS_SQL_SERVER_PORT) + ";trustServerCertificate=true";
        final ConnectionDefinition connectionDefinition = exasolFactory.createConnectionDefinition(JDBC_CONNECTION_NAME,
                connectionString, MS_SQL_SERVER_CONTAINER.getUsername(), MS_SQL_SERVER_CONTAINER.getPassword());
        exasolFactory.createVirtualSchemaBuilder(VIRTUAL_SCHEMA_JDBC).adapterScript(adapterScript)
                .connectionDefinition(connectionDefinition)
                .properties(Map.of("CATALOG_NAME", "master", "SCHEMA_NAME", SCHEMA_SQL_SERVER)).build();
    }

    @AfterAll
    static void afterAll() throws SQLException {
        exasolConnection.close();
    }

    private static void createSqlServerSchema() throws SQLException {
        try (final Statement statement = MS_SQL_SERVER_CONTAINER.createConnection("").createStatement()) {
            statement.execute("CREATE SCHEMA " + SCHEMA_SQL_SERVER);
        }
    }

    private static AdapterScript createAdapterScript(final ExasolSchema schema) {
        final String content = "%scriptclass com.exasol.adapter.RequestDispatcher;\n" //
                + "%jar /buckets/bfsdefault/default/" + VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION + ";\n" //
                + "%jar /buckets/bfsdefault/default/drivers/jdbc/" + JDBC_DRIVER_NAME + ";\n";
        return schema.createAdapterScript(ADAPTER_SCRIPT_EXASOL, JAVA, content);
    }

    private static void uploadDriverToBucket() throws BucketAccessException, TimeoutException, FileNotFoundException {
        final Bucket bucket = EXASOL_CONTAINER.getDefaultBucket();
        final Path pathToSettingsFile = Path.of("src", "test", "resources", JDBC_DRIVER_CONFIGURATION_FILE_NAME);
        bucket.uploadFile(pathToSettingsFile, "drivers/jdbc/" + JDBC_DRIVER_CONFIGURATION_FILE_NAME);
        bucket.uploadFile(JDBC_DRIVER_PATH, "drivers/jdbc/" + JDBC_DRIVER_NAME);
    }

    private static void uploadVsJarToBucket() throws BucketAccessException, TimeoutException, FileNotFoundException {
        final Bucket bucket = EXASOL_CONTAINER.getDefaultBucket();
        bucket.uploadFile(PATH_TO_VIRTUAL_SCHEMAS_JAR, VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
    }

    private ResultSet getExpectedResultSet(final List<String> expectedColumns, final List<String> expectedRows)
            throws SQLException {
        try (final Statement statement = exasolConnection.createStatement()) {
            final String expectedValues = expectedRows.stream().map(row -> "(" + row + ")")
                    .collect(Collectors.joining(","));
            final String qualifiedExpectedTableName = SCHEMA_EXASOL + ".EXPECTED";
            statement.execute("CREATE OR REPLACE TABLE " + qualifiedExpectedTableName + "("
                    + String.join(", ", expectedColumns) + ")");
            statement.execute("INSERT INTO " + qualifiedExpectedTableName + " VALUES" + expectedValues);
            return statement.executeQuery("SELECT * FROM " + qualifiedExpectedTableName);
        }
    }

    private ResultSet getActualResultSet(final String query) throws SQLException {
        try (final Statement statement = exasolConnection.createStatement()) {
            return statement.executeQuery(query);
        }
    }

    private String getExplainVirtualString(final String query) throws SQLException {
        try (final Statement statement = exasolConnection.createStatement()) {
            final ResultSet explainVirtual = statement.executeQuery("EXPLAIN VIRTUAL " + query);
            explainVirtual.next();
            return explainVirtual.getString("PUSHDOWN_SQL");
        }
    }

    @ParameterizedTest
    @CsvSource(value = { //
            "c1  | DECIMAL(19)      | -9223372036854775808  | 9223372036854775807", //
            "c2  | DECIMAL(10)      | -2147483648           | 2147483647", //
            "c3  | DECIMAL(5)       | -32768                | 32767", //
            "c4  | DECIMAL(3)       | 0                     | 255", //
            "c5  | BOOLEAN          | false                 | true", //
            "c6  | DECIMAL(30, 8)   | 6                     | 999.99999999", //
            "c7  | DECIMAL(10, 5)   | 7.43                  | 6.43", //
            "c8  | DECIMAL(19, 4)   | -922337203685477.5808 | 922337203685477.5807", //
            "c9  | DECIMAL(10, 4)   | -214748.3648          | 214748.3647", //
            "c10 | DOUBLE PRECISION | -1.79E+308            | 1.79E+308", //
            "c11 | DOUBLE PRECISION | -3978.4560546875      | 3978.4560546875" //
    }, delimiter = '|')
    void testSupportedNumericDataTypes(final String columnName, final String expectedColumnType,
            final String expectedValueFirst, final String expectedValueSecond) throws SQLException {
        final String query = "SELECT \"" + columnName + "\" FROM " + VIRTUAL_SCHEMA_JDBC + "."
                + TABLE_SQL_SERVER_NUMERIC_AND_DATE_DATA_TYPES;
        final ResultSet expected = getExpectedResultSet(List.of("col1 " + expectedColumnType), //
                List.of(expectedValueFirst, expectedValueSecond));
        assertThat(getActualResultSet(query), matchesResultSet(expected));
    }

    @ParameterizedTest
    @CsvSource(value = { //
            "c12 | VARCHAR(16) | 01:02:03.0000000                   | 23:59:59.0000000", //
            "c13 | DATE        | 0001-01-01                         | 9999-12-31", //
            "c14 | TIMESTAMP   | 1900-01-01 00:00:00                | 2078-12-31 23:59:00", //
            "c15 | TIMESTAMP   | 1753-01-01 00:00:00.0              | 9999-12-30 23:59:59.000", //
            "c16 | TIMESTAMP   | 0001-01-01 00:00:00.0              | 9999-12-30 23:59:59.0", //
            "c17 | VARCHAR(34) | 0001-01-01 13:00:00.0000000 +12:15 | 9999-12-30 23:59:59.9999999 +12:15" //
    }, delimiter = '|')
    void testSupportedDateAndTimeDataTypes(final String columnName, final String expectedColumnType,
            final String expectedValueFirst, final String expectedValueSecond) throws SQLException {
        final String query = "SELECT \"" + columnName + "\" FROM " + VIRTUAL_SCHEMA_JDBC + "."
                + TABLE_SQL_SERVER_NUMERIC_AND_DATE_DATA_TYPES;
        final ResultSet expected = getExpectedResultSet(List.of("col1 " + expectedColumnType), //
                List.of("'" + expectedValueFirst + "'", "'" + expectedValueSecond + "'"));
        assertThat(getActualResultSet(query), matchesResultSet(expected));
    }

    @ParameterizedTest
    @CsvSource(value = { //
            "c18 | CHAR(5)          | abcde", //
            "c19 | VARCHAR(100)     | abc", //
            "c20 | VARCHAR(2000000) | def", //
            "c21 | CHAR(5)          | nabcd", //
            "c22 | VARCHAR(100)     | nabc", //
            "c23 | VARCHAR(2000000) | ndef", //
            "c29 | CHAR(36)         | B14BC077-F1DF-457C-9F7E-7CB9E0BC1CF3", //
            "c31 | VARCHAR(2000000) | <Team name=\"Braves\"/>" //
    }, delimiter = '|')
    void testSupportedStringDataTypes(final String columnName, final String expectedColumnType,
            final String expectedValue) throws SQLException {
        final String query = "SELECT \"" + columnName + "\" FROM " + VIRTUAL_SCHEMA_JDBC + "."
                + TABLE_SQL_SERVER_STRING_DATA_TYPES;
        final ResultSet expected = getExpectedResultSet(List.of("col1 " + expectedColumnType), //
                List.of("'" + expectedValue + "'"));
        assertThat(getActualResultSet(query), matchesResultSet(expected));
    }

    @ParameterizedTest
    @CsvSource(value = { "c24", "c25", "c26", "c27", "c28", "c30", "c32", "c33" })
    void testUnsupportedDataTypes(final String columnName) {
        final String query = "SELECT \"" + columnName + "\" FROM " + VIRTUAL_SCHEMA_JDBC + "."
                + TABLE_SQL_SERVER_STRING_DATA_TYPES;
        final SQLException exception = assertThrows(SQLException.class, () -> getActualResultSet(query));
        assertThat(exception.getMessage(), containsString("object \"" + columnName + "\" not found"));
    }

    @Test
    void testSelectStarWithArtificialWhereClauseTrue() {
        assertVsQuery("SELECT * FROM " + VIRTUAL_SCHEMA_JDBC + "." + TABLE_SQL_SERVER_SIMPLE + " WHERE 1 = 1", //
                table() //
                        .row(-9223372036854775808L, "00:00:00.0000000", "first") //
                        .row(0, "01:02:03.0000000", "second") //
                        .row(9223372036854775807L, "23:59:59.0000000", "third") //
                        .matches(TypeMatchMode.NO_JAVA_TYPE_CHECK));
    }

    @Test
    void testSelectStarWithArtificialWhereClauseFalse() {
        assertVsQuery("SELECT * FROM " + VIRTUAL_SCHEMA_JDBC + "." + TABLE_SQL_SERVER_SIMPLE + " WHERE 1 = 0", //
                table("DECIMAL", "VARCHAR", "VARCHAR").matches());
    }

    private void assertVsQuery(final String sql, final Matcher<ResultSet> expected) {
        try {
            assertThat(query(sql), expected);
        } catch (final SQLException exception) {
            fail("Unable to run assertion query: " + sql + "\nCaused by: " + exception.getMessage());
        }
    }

    protected ResultSet query(final String sql) throws SQLException {
        return exasolConnection.createStatement().executeQuery(sql);
    }

    @Test
    void testCount() throws SQLException {
        final String query = "SELECT COUNT(*) FROM " + VIRTUAL_SCHEMA_JDBC + "." + TABLE_SQL_SERVER_SIMPLE;
        final String expectedRewrittenQuery = "SELECT COUNT_BIG(*) FROM";
        assertAll(() -> assertThat(getActualResultSet(query), table("BIGINT").row(3L).matches()),
                () -> assertThat(getExplainVirtualString(query), containsString(expectedRewrittenQuery)));
    }

    @Test
    void testGetDate() throws SQLException {
        final String query = "SELECT CURRENT_DATE FROM " + VIRTUAL_SCHEMA_JDBC + "." + TABLE_SQL_SERVER_SIMPLE
                + " LIMIT 1";
        final ResultSet expected = getExpectedResultSet(List.of("col1 DATE"),
                List.of("'" + LocalDate.now().toString() + "'"));
        final String expectedRewrittenQuery = "SELECT TOP 1 CAST(GETDATE() AS DATE) FROM";
        assertAll(() -> assertThat(getActualResultSet(query), matchesResultSet(expected)),
                () -> assertThat(getExplainVirtualString(query), containsString(expectedRewrittenQuery)));
    }

    private static void createSimpleTable() throws SQLException {
        try (final Statement statement = MS_SQL_SERVER_CONTAINER.createConnection("").createStatement()) {
            statement.execute("CREATE TABLE " + SCHEMA_SQL_SERVER + "." + TABLE_SQL_SERVER_SIMPLE + " (" //
                    + "bigint_col bigint, " //
                    + "time_col time(7), " //
                    + "varchar_col varchar(100), " //
                    + ")");
            statement.execute("INSERT INTO " + SCHEMA_SQL_SERVER + "." + TABLE_SQL_SERVER_SIMPLE + " VALUES" //
                    + "(-9223372036854775808, '00:00:00', 'first'), " //
                    + "(0, '01:02:03', 'second'), " //
                    + "(9223372036854775807, '23:59:59', 'third') " //
            );
        }
    }

    private static void createSqlServerTableNumericAndDateDataTypes() throws SQLException {
        try (final Statement statement = MS_SQL_SERVER_CONTAINER.createConnection("").createStatement()) {
            statement.execute(
                    "CREATE TABLE " + SCHEMA_SQL_SERVER + "." + TABLE_SQL_SERVER_NUMERIC_AND_DATE_DATA_TYPES + " (" //
                    // Exact numerics
                            + "c1 bigint, " //
                            + "c2 int, " //
                            + "c3 smallint, " //
                            + "c4 tinyint, " //
                            + "c5 bit, " //
                            + "c6 decimal(30,8), " //
                            + "c7 numeric(10,5), " //
                            + "c8 money, " //
                            + "c9 smallmoney, " //
                            // Approximate numerics
                            + "c10 float(53), " //
                            + "c11 real, " //
                            // Date and time
                            + "c12 time(7), " //
                            + "c13 date, " //
                            + "c14 smalldatetime, " //
                            + "c15 datetime, " //
                            + "c16 datetime2, " //
                            + "c17 datetimeoffset " //
                            + ")");
            statement.execute("INSERT INTO " + SCHEMA_SQL_SERVER + "." + TABLE_SQL_SERVER_NUMERIC_AND_DATE_DATA_TYPES //
                    + " VALUES(" //
                    + "-9223372036854775808, -2147483648, -32768, 0, 0, 6, 7.43, -922337203685477.5808, -214748.3648, " //
                    + "-1.79E+308, -3978.456, " //
                    + "'01:02:03', '0001-01-01', '1900-01-01 00:00:00', '1753-01-01 00:00:00', '0001-01-01 00:00:00', '0001-01-01 13:00:00.0000000 +12:15' " //
                    + ")");
            statement.execute("INSERT INTO " + SCHEMA_SQL_SERVER + "." + TABLE_SQL_SERVER_NUMERIC_AND_DATE_DATA_TYPES //
                    + " VALUES(" //
                    + "9223372036854775807, 2147483647, 32767, 255, 1, 999.99999999, 6.43, 922337203685477.5807, 214748.3647, " //
                    + "1.79E+308, 3978.456, " //
                    + "'23:59:59', '9999-12-31', '2078-12-31 23:59:00', '9999-12-30 23:59:59', '9999-12-30 23:59:59', '9999-12-30 23:59:59.9999999 +12:15' " //
                    + ")");
        }
    }

    private static void createSqlServerTableStringDataTypes() throws SQLException {
        try (final Statement statement = MS_SQL_SERVER_CONTAINER.createConnection("").createStatement()) {
            statement.execute("CREATE TABLE " + SCHEMA_SQL_SERVER + "." + TABLE_SQL_SERVER_STRING_DATA_TYPES + " (" //
            // Character strings
                    + "c18 char(5), " //
                    + "c19 varchar(100), " //
                    + "c20 text, " //
                    // Unicode character strings
                    + "c21 nchar(5), " //
                    + "c22 nvarchar(100), " //
                    + "c23 ntext, " //
                    // Binary strings
                    + "c24 binary(10), " //
                    + "c25 varbinary(9), " //
                    + "c26 image, " //
                    // Other data types
                    + "c27 rowversion, " //
                    + "c28 hierarchyid, " //
                    + "c29 uniqueidentifier, " //
                    + "c30 sql_variant, " //
                    + "c31 xml, " //
                    + "c32 geometry, " //
                    + "c33 geography " //
                    + ")");
            statement.execute("INSERT INTO " + SCHEMA_SQL_SERVER + "." + TABLE_SQL_SERVER_STRING_DATA_TYPES //
                    + "(c18,c19,c20,c21,c22,c23,c24,c25,c26,c28,c29,c30,c31,c32,c33)" //
                    + " VALUES(" //
                    + "'abcde', 'abc', 'def', " //
                    + "'nabcd', 'nabc', 'ndef', " //
                    + "CAST(123456 AS BINARY(10)), CAST(N'Test' as VARBINARY(9)), CAST(123456 AS BINARY(2)), " //
                    + "'/1/', CAST('B14BC077-F1DF-457C-9F7E-7CB9E0BC1CF3' AS UNIQUEIDENTIFIER), 'BaseType', '<Team name=\"Braves\"/>', geometry::STGeomFromText('LINESTRING (100 100, 20 180, 180 180)', 0), geography::STGeomFromText('LINESTRING(-122.360 47.656, -122.343 47.656 )', 4326) " //
                    + ")");
        }
    }
}
