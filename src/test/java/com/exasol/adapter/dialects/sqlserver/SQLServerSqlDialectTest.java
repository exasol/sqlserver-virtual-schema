package com.exasol.adapter.dialects.sqlserver;

import static com.exasol.adapter.AdapterProperties.*;
import static com.exasol.adapter.capabilities.AggregateFunctionCapability.*;
import static com.exasol.adapter.capabilities.LiteralCapability.*;
import static com.exasol.adapter.capabilities.MainCapability.*;
import static com.exasol.adapter.capabilities.PredicateCapability.*;
import static com.exasol.adapter.capabilities.ScalarFunctionCapability.*;
import static com.exasol.adapter.capabilities.ScalarFunctionCapability.ST_INTERSECTION;
import static com.exasol.adapter.capabilities.ScalarFunctionCapability.ST_UNION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.capabilities.Capabilities;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.adapter.jdbc.RemoteMetadataReaderException;
import com.exasol.adapter.properties.PropertyValidationException;
import com.exasol.adapter.sql.AggregateFunction;
import com.exasol.adapter.sql.ScalarFunction;

@ExtendWith(MockitoExtension.class)
class SQLServerSqlDialectTest {
    private SQLServerSqlDialect dialect;
    @Mock
    private ConnectionFactory connectionFactoryMock;

    @BeforeEach
    void beforeEach() {
        this.dialect = new SQLServerSqlDialect(this.connectionFactoryMock, AdapterProperties.emptyProperties());
    }

    @Test
    void testGetCapabilities() {
        final Capabilities capabilities = this.dialect.getCapabilities();
        assertAll(() -> assertThat(capabilities.getMainCapabilities(),
                containsInAnyOrder(SELECTLIST_PROJECTION, SELECTLIST_EXPRESSIONS, FILTER_EXPRESSIONS,
                        AGGREGATE_SINGLE_GROUP, AGGREGATE_GROUP_BY_COLUMN, AGGREGATE_GROUP_BY_EXPRESSION,
                        AGGREGATE_GROUP_BY_TUPLE, AGGREGATE_HAVING, ORDER_BY_COLUMN, ORDER_BY_EXPRESSION, LIMIT)), //
                () -> assertThat(capabilities.getLiteralCapabilities(),
                        containsInAnyOrder(BOOL, NULL, DATE, TIMESTAMP, TIMESTAMP_UTC, DOUBLE, EXACTNUMERIC, STRING,
                                INTERVAL)), //
                () -> assertThat(capabilities.getPredicateCapabilities(),
                        containsInAnyOrder(AND, OR, NOT, EQUAL, NOTEQUAL, LESS, LESSEQUAL, LIKE, LIKE_ESCAPE, BETWEEN,
                                REGEXP_LIKE, IN_CONSTLIST, IS_NULL, IS_NOT_NULL)),
                () -> assertThat(capabilities.getAggregateFunctionCapabilities(),
                        containsInAnyOrder(COUNT, COUNT_STAR, COUNT_DISTINCT, SUM, SUM_DISTINCT, MIN, MAX, AVG,
                                AVG_DISTINCT, MEDIAN, FIRST_VALUE, LAST_VALUE, STDDEV, STDDEV_DISTINCT, STDDEV_POP,
                                STDDEV_POP_DISTINCT, VARIANCE, VARIANCE_DISTINCT, VAR_POP, VAR_POP_DISTINCT)), //
                () -> assertThat(capabilities.getScalarFunctionCapabilities(),
                        containsInAnyOrder(ADD, SUB, MULT, FLOAT_DIV, NEG, ABS, ACOS, ASIN, ATAN, ATAN2, CEIL, COS, COT,
                                DEGREES, EXP, FLOOR, LOG, MOD, POWER, RADIANS, RAND, ROUND, SIGN, SIN, SQRT, TAN, TRUNC,
                                ASCII, CHR, CONCAT, INSTR, LENGTH, LOCATE, LOWER, LPAD, LTRIM, REPEAT, REPLACE, REVERSE,
                                RIGHT, RPAD, RTRIM, SOUNDEX, SPACE, SUBSTR, TRIM, UNICODE, UPPER, ADD_DAYS, ADD_HOURS,
                                ADD_MINUTES, ADD_MONTHS, ADD_SECONDS, ADD_WEEKS, ADD_YEARS, SECONDS_BETWEEN,
                                MINUTES_BETWEEN, HOURS_BETWEEN, DAYS_BETWEEN, MONTHS_BETWEEN, YEARS_BETWEEN, DAY, MONTH,
                                YEAR, SYSDATE, SYSTIMESTAMP, CURRENT_DATE, CURRENT_TIMESTAMP, ST_X, ST_Y, ST_ENDPOINT,
                                ST_ISCLOSED, ST_ISRING, ST_LENGTH, ST_NUMPOINTS, ST_POINTN, ST_STARTPOINT, ST_AREA,
                                ST_EXTERIORRING, ST_INTERIORRINGN, ST_NUMINTERIORRINGS, ST_GEOMETRYN, ST_NUMGEOMETRIES,
                                ST_BOUNDARY, ST_BUFFER, ST_CENTROID, ST_CONTAINS, ST_CONVEXHULL, ST_CROSSES,
                                ST_DIFFERENCE, ST_DIMENSION, ST_DISJOINT, ST_DISTANCE, ST_ENVELOPE, ST_EQUALS,
                                ST_GEOMETRYTYPE, ST_INTERSECTION, ST_INTERSECTS, ST_ISEMPTY, ST_ISSIMPLE, ST_OVERLAPS,
                                ST_SYMDIFFERENCE, ST_TOUCHES, ST_UNION, ST_WITHIN, BIT_AND, BIT_NOT, BIT_OR, BIT_XOR,
                                CASE, HASH_MD5, HASH_SHA1, NULLIFZERO, ZEROIFNULL)));
    }

    @Test
    void testGetName() {
        assertThat(this.dialect.getName(), equalTo("SQLSERVER"));
    }

    @Test
    void testMetadataReaderClass() {
        assertThat(this.dialect.createRemoteMetadataReader(), instanceOf(SQLServerMetadataReader.class));
    }

    @Test
    void testCreateMetadataReaderFails() throws SQLException {
        when(this.connectionFactoryMock.getConnection()).thenThrow(new SQLException("mock"));
        final RemoteMetadataReaderException exception = assertThrows(RemoteMetadataReaderException.class,
                this.dialect::createRemoteMetadataReader);
        assertThat(exception.getMessage(),
                equalTo("E-VSSQLS-2: Unable to create SQL Server remote metadata reader. Caused by: 'mock'"));
    }

    @Test
    void testValidateCatalogProperty() throws PropertyValidationException {
        final SqlDialect sqlDialect = new SQLServerSqlDialect(null, new AdapterProperties(Map.of( //
                CONNECTION_NAME_PROPERTY, "MY_CONN", //
                CATALOG_NAME_PROPERTY, "MY_CATALOG")));
        sqlDialect.validateProperties();
    }

    @Test
    void testValidateSchemaProperty() throws PropertyValidationException {
        final SqlDialect sqlDialect = new SQLServerSqlDialect(null, new AdapterProperties(Map.of( //
                CONNECTION_NAME_PROPERTY, "MY_CONN", //
                SCHEMA_NAME_PROPERTY, "MY_SCHEMA")));
        sqlDialect.validateProperties();
    }

    @Test
    void testGetScalarFunctionAliases() {
        assertAll(
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.ATAN2),
                        Matchers.equalTo("ATN2")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.CEIL),
                        Matchers.equalTo("CEILING")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.CHR),
                        Matchers.equalTo("CHAR")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.LENGTH),
                        Matchers.equalTo("LEN")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.LOCATE),
                        Matchers.equalTo("CHARINDEX")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.REPEAT),
                        Matchers.equalTo("REPLICATE")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.SUBSTR),
                        Matchers.equalTo("SUBSTRING")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.NULLIFZERO),
                        Matchers.equalTo("NULLIF")));
    }

    @Test
    void testGetAggregateFunctionAliases() {
        assertAll(
                () -> assertThat(this.dialect.getAggregateFunctionAliases().get(AggregateFunction.STDDEV),
                        Matchers.equalTo("STDEV")), //
                () -> assertThat(this.dialect.getAggregateFunctionAliases().get(AggregateFunction.STDDEV_POP),
                        Matchers.equalTo("STDEVP")), //
                () -> assertThat(this.dialect.getAggregateFunctionAliases().get(AggregateFunction.VARIANCE),
                        Matchers.equalTo("VAR")), //
                () -> assertThat(this.dialect.getAggregateFunctionAliases().get(AggregateFunction.VAR_POP),
                        Matchers.equalTo("VARP")));
    }

    @CsvSource({ "tableName, [tableName]", "table \"name, [table \"name]" })
    @ParameterizedTest
    void testApplyQuote(final String identifier, final String expected) {
        assertThat(this.dialect.applyQuote(identifier), equalTo(expected));
    }

    @CsvSource({ "[tableName]", "[table name", "table name]", "table[name", "table]name", "table\\name" })
    @ParameterizedTest
    void testApplyQuoteThrowsException(final String identifier) {
        assertThrows(AssertionError.class, () -> this.dialect.applyQuote(identifier));
    }

    @ValueSource(strings = { "ab:'ab'", "a'b:'a''b'", "a''b:'a''''b'", "'ab':'''ab'''" })
    @ParameterizedTest
    void testGetLiteralString(final String definition) {
        assertThat(this.dialect.getStringLiteral(definition.substring(0, definition.indexOf(':'))),
                equalTo(definition.substring(definition.indexOf(':') + 1)));
    }

    @Test
    void testGetLiteralStringNull() {
        assertThat(this.dialect.getStringLiteral(null), CoreMatchers.equalTo("NULL"));
    }

    @Test
    void testRequiresCatalogQualifiedTableNames() {
        assertThat(this.dialect.requiresCatalogQualifiedTableNames(null), equalTo(true));
    }

    @Test
    void testRequiresSchemaQualifiedTableNames() {
        assertThat(this.dialect.requiresSchemaQualifiedTableNames(null), equalTo(true));
    }

    @Test
    void testGetDefaultNullSorting() {
        assertThat(this.dialect.getDefaultNullSorting(), equalTo(SqlDialect.NullSorting.NULLS_SORTED_LOW));
    }

    @Test
    void testGetSqlGenerationVisitor() {
        assertThat(this.dialect.getSqlGenerator(null), CoreMatchers.instanceOf(SQLServerSqlGenerationVisitor.class));
    }

    @Test
    void testSupportsJdbcCatalogs() {
        assertThat(this.dialect.supportsJdbcCatalogs(), equalTo(SqlDialect.StructureElementSupport.MULTIPLE));
    }

    @Test
    void testSupportsJdbcSchemas() {
        assertThat(this.dialect.supportsJdbcSchemas(), equalTo(SqlDialect.StructureElementSupport.MULTIPLE));
    }
}