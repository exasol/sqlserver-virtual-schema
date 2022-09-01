package com.exasol.adapter.dialects.sqlserver;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.adapternotes.ColumnAdapterNotes;
import com.exasol.adapter.adapternotes.ColumnAdapterNotesJsonConverter;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.SqlDialectFactory;
import com.exasol.adapter.dialects.rewriting.SqlGenerationContext;
import com.exasol.adapter.dialects.rewriting.SqlGenerationVisitorException;
import com.exasol.adapter.metadata.*;
import com.exasol.adapter.sql.*;

import jakarta.json.stream.JsonParsingException;

class SQLServerSqlGenerationVisitorTest {
    private SQLServerSqlGenerationVisitor visitor;

    @BeforeEach
    void beforeEach() {
        final SqlDialectFactory factory = new SQLServerSqlDialectFactory();
        final SqlDialect dialect = factory.createSqlDialect(null, AdapterProperties.emptyProperties());
        final SqlGenerationContext context = new SqlGenerationContext("test_catalog", "test_schema", false);
        this.visitor = new SQLServerSqlGenerationVisitor(dialect, context);
    }

    @Test
    void testVisitSqlSelectListAnyValue() throws AdapterException {
        final SqlSelectList sqlSelectList = SqlSelectList.createAnyValueSelectList();
        assertThat(this.visitor.visit(sqlSelectList), equalTo("true"));
    }

    @Test
    void testVisitSqlStatementSelect() throws AdapterException {
        final SqlStatementSelect select = (SqlStatementSelect) getTestSqlNode();
        assertThat(this.visitor.visit(select), //
                equalTo("SELECT TOP 10 [USER_ID], COUNT_BIG([URL])" //
                        + " FROM [test_catalog].[test_schema].[CLICKS]" //
                        + " WHERE 1 < [USER_ID]" //
                        + " GROUP BY [USER_ID] HAVING 1 < COUNT_BIG([URL])" //
                        + " ORDER BY [USER_ID] NULLS LAST"));
    }

    @CsvSource({ "ADD_DAYS, DAY", //
            "ADD_HOURS, HOUR", //
            "ADD_MINUTES, MINUTE", //
            "ADD_SECONDS, SECOND", //
            "ADD_YEARS, YEAR", //
            "ADD_WEEKS, WEEK" })
    @ParameterizedTest
    void testVisitSqlFunctionScalarAddDate(final ScalarFunction scalarFunction, final String expected)
            throws AdapterException {
        final SqlFunctionScalar sqlFunctionScalar = createSqlFunctionScalarForDateTest(scalarFunction);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo("DATEADD(" + expected + ",10,[test_column])"));
    }

    private SqlFunctionScalar createSqlFunctionScalarForDateTest(final ScalarFunction scalarFunction) {
        final List<SqlNode> arguments = new ArrayList<>();
        arguments.add(new SqlColumn(1,
                ColumnMetadata.builder().name("test_column")
                        .adapterNotes("{\"jdbcDataType\":93, " + "\"typeName\":\"TIMESTAMP\"}")
                        .type(DataType.createChar(20, DataType.ExaCharset.UTF8)).build()));
        arguments.add(new SqlLiteralExactnumeric(new BigDecimal(10)));
        return new SqlFunctionScalar(scalarFunction, arguments);
    }

    @CsvSource({ "SECONDS_BETWEEN, SECOND", //
            "MINUTES_BETWEEN, MINUTE", //
            "HOURS_BETWEEN, HOUR", //
            "DAYS_BETWEEN, DAY", //
            "MONTHS_BETWEEN, MONTH", //
            "YEARS_BETWEEN, YEAR" })
    @ParameterizedTest
    void testVisitSqlFunctionScalarTimeBetween(final ScalarFunction scalarFunction, final String expected)
            throws AdapterException {
        final SqlFunctionScalar sqlFunctionScalar = createSqlFunctionScalarForDateTest(scalarFunction);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo("DATEDIFF(" + expected + ",10,[test_column])"));
    }

    @CsvSource({ "CURRENT_DATE, CAST(GETDATE() AS DATE)", //
            "CURRENT_TIMESTAMP,  GETDATE()", //
            "SYSDATE, CAST(SYSDATETIME() AS DATE)", //
            "SYSTIMESTAMP, SYSDATETIME()" })
    @ParameterizedTest
    void testVisitSqlFunctionScalarWithoutArguments(final ScalarFunction scalarFunction, final String expected)
            throws AdapterException {
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(scalarFunction, null);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo(expected));
    }

    @CsvSource(value = { "INSTR : CHARINDEX('test2', 'test', 'test3')", //
            "LPAD : RIGHT ( REPLICATE('test3','test2') + LEFT('test','test2'),'test2')", //
            "RPAD : LEFT(RIGHT('test','test2') + REPLICATE('test3','test2'),'test2')" //
    }, delimiter = ':')
    @ParameterizedTest
    void testVisitSqlFunctionScalarWithThreeArguments(final ScalarFunction scalarFunction, final String expected)
            throws AdapterException {
        final List<SqlNode> arguments = new ArrayList<>();
        arguments.add(new SqlLiteralString("test"));
        arguments.add(new SqlLiteralString("test2"));
        arguments.add(new SqlLiteralString("test3"));
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(scalarFunction, arguments);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo(expected));
    }

    @CsvSource(value = { "ST_X : 'left'.STX", //
            "ST_Y : 'left'.STY", //
            "ST_ENDPOINT : CAST('left'.STEndPoint() AS VARCHAR(8000))", //
            "ST_ISCLOSED : 'left'.STIsClosed()", //
            "ST_ISRING : 'left'.STIsRing()", //
            "ST_LENGTH : 'left'.STLength()", //
            "ST_NUMPOINTS : 'left'.STNumPoints()", //
            "ST_POINTN : CAST('left'.STPointN('right') AS VARCHAR(8000))", //
            "ST_STARTPOINT : CAST('left'.STStartPoint() AS VARCHAR(8000))", //
            "ST_AREA : 'left'.STArea()", //
            "ST_EXTERIORRING : CAST('left'.STExteriorRing() AS VARCHAR(8000))", //
            "ST_INTERIORRINGN : CAST('left'.STInteriorRingN ('right') AS VARCHAR(8000))", //
            "ST_NUMINTERIORRINGS : 'left'.STNumInteriorRing()", //
            "ST_GEOMETRYN : CAST('left'.STGeometryN('right') AS VARCHAR(8000))", //
            "ST_NUMGEOMETRIES : 'left'.STNumGeometries()", //
            "ST_BOUNDARY : CAST('left'.STBoundary() AS VARCHAR(8000))", //
            "ST_BUFFER : CAST('left'.STBuffer('right') AS VARCHAR(8000))", //
            "ST_CENTROID : CAST('left'.STCentroid() AS VARCHAR(8000))", //
            "ST_CONTAINS : 'left'.STContains('right')", //
            "ST_CONVEXHULL : CAST('left'.STConvexHull() AS VARCHAR(8000))", //
            "ST_CROSSES : 'left'.STCrosses('right')", //
            "ST_DIFFERENCE : CAST('left'.STDifference('right') AS VARCHAR(8000))", //
            "ST_DIMENSION : 'left'.STDimension()", //
            "ST_DISJOINT : CAST('left'.STDisjoint('right') AS VARCHAR(8000))", //
            "ST_DISTANCE : 'left'.STDistance('right')", //
            "ST_ENVELOPE : CAST('left'.STEnvelope() AS VARCHAR(8000))", //
            "ST_EQUALS : 'left'.STEquals('right')", //
            "ST_GEOMETRYTYPE : 'left'.STGeometryType()", //
            "ST_INTERSECTION : CAST('left'.STIntersection('right') AS VARCHAR(8000))", //
            "ST_INTERSECTS : 'left'.STIntersects('right')", //
            "ST_ISEMPTY : 'left'.STIsEmpty()", //
            "ST_ISSIMPLE : 'left'.STIsSimple()", //
            "ST_OVERLAPS : 'left'.STOverlaps('right')", //
            "ST_SYMDIFFERENCE : CAST('left'.STSymDifference ('right') AS VARCHAR(8000))", //
            "ST_TOUCHES : 'left'.STTouches('right')", //
            "ST_UNION : CAST('left'.STUnion('right') AS VARCHAR(8000))", //
            "ST_WITHIN : 'left'.STWithin('right')", //
            "BIT_AND : 'left' & '" + "" + "right'", //
            "BIT_OR : 'left' | 'right'", //
            "BIT_XOR : 'left' ^ 'right'", //
            "BIT_NOT : ~ 'left'", //
            "HASH_MD5 : CONVERT(Char, HASHBYTES('MD5','left'), 2)", //
            "HASH_SHA1 : CONVERT(Char, HASHBYTES('SHA1','left'), 2)", //
            "ZEROIFNULL : ISNULL('left',0)" //
    }, delimiter = ':')
    @ParameterizedTest
    void testVisitSqlFunctionScalarWithTwoArguments(final ScalarFunction scalarFunction, final String expected)
            throws AdapterException {
        final List<SqlNode> arguments = List.of(new SqlLiteralString("left"), new SqlLiteralString("right"));
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(scalarFunction, arguments);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo(expected));
    }

    private static SqlNode getTestSqlNode() {
        return new DialectTestData().getTestSqlNode();
    }

    private static class DialectTestData {
        private SqlNode getTestSqlNode() {
            // SELECT USER_ID, count(URL) FROM CLICKS
            // WHERE 1 < USER_ID
            // GROUP BY USER_ID
            // HAVING 1 < COUNT(URL)
            // ORDER BY USER_ID
            // LIMIT 10;
            final TableMetadata clicksMeta = getClicksTableMetadata();
            final SqlTable fromClause = new SqlTable("CLICKS", clicksMeta);
            final SqlSelectList selectList = SqlSelectList.createRegularSelectList(List.of(
                    new SqlColumn(0, clicksMeta.getColumns().get(0)), new SqlFunctionAggregate(AggregateFunction.COUNT,
                            List.of(new SqlColumn(1, clicksMeta.getColumns().get(1))), false)));
            final SqlNode whereClause = new SqlPredicateLess(new SqlLiteralExactnumeric(BigDecimal.ONE),
                    new SqlColumn(0, clicksMeta.getColumns().get(0)));
            final SqlExpressionList groupBy = new SqlGroupBy(List.of(new SqlColumn(0, clicksMeta.getColumns().get(0))));
            final SqlNode countUrl = new SqlFunctionAggregate(AggregateFunction.COUNT,
                    List.of(new SqlColumn(1, clicksMeta.getColumns().get(1))), false);
            final SqlNode having = new SqlPredicateLess(new SqlLiteralExactnumeric(BigDecimal.ONE), countUrl);
            final SqlOrderBy orderBy = new SqlOrderBy(List.of(new SqlColumn(0, clicksMeta.getColumns().get(0))),
                    List.of(true), List.of(true));
            final SqlLimit limit = new SqlLimit(10);
            return SqlStatementSelect.builder().selectList(selectList).fromClause(fromClause).whereClause(whereClause)
                    .groupBy(groupBy).having(having).orderBy(orderBy).limit(limit).build();
        }

        private TableMetadata getClicksTableMetadata() {
            final ColumnAdapterNotesJsonConverter converter = ColumnAdapterNotesJsonConverter.getInstance();
            final List<ColumnMetadata> columns = new ArrayList<>();
            final ColumnAdapterNotes decimalAdapterNotes = ColumnAdapterNotes.builder() //
                    .jdbcDataType(3) //
                    .typeName("DECIMAL") //
                    .build();
            final ColumnAdapterNotes varcharAdapterNotes = ColumnAdapterNotes.builder() //
                    .jdbcDataType(12) //
                    .typeName("VARCHAR") //
                    .build();
            columns.add(ColumnMetadata.builder() //
                    .name("USER_ID") //
                    .adapterNotes(converter.convertToJson(decimalAdapterNotes)).type(DataType.createDecimal(18, 0)) //
                    .nullable(true) //
                    .identity(false) //
                    .defaultValue("") //
                    .comment("") //
                    .build());
            columns.add(ColumnMetadata.builder() //
                    .name("URL") //
                    .adapterNotes(converter.convertToJson(varcharAdapterNotes)) //
                    .type(DataType.createVarChar(10000, DataType.ExaCharset.UTF8)) //
                    .nullable(true) //
                    .identity(false) //
                    .defaultValue("") //
                    .comment("") //
                    .build());
            return new TableMetadata("CLICKS", "", columns, "");
        }
    }

    @Test
    void testVisitLiteralBoolTrue() {
        assertThat(this.visitor.visit(new SqlLiteralBool(true)), equalTo("1 = 1"));
    }

    @Test
    void testVisitLiteralBoolFalse() {
        assertThat(this.visitor.visit(new SqlLiteralBool(false)), equalTo("1 = 0"));
    }

    @Test
    void testVisitColumn() throws AdapterException {
        final SqlColumn column = new SqlColumn(0,
                ColumnMetadata.builder().name("col1").type(DataType.createBool()).adapterNotes("invalidJson").build());
        column.setParent(SqlSelectList.createRegularSelectList(List.of(column)));
        final SqlGenerationVisitorException exception = assertThrows(SqlGenerationVisitorException.class,
                () -> this.visitor.visit(column));
        assertThat(exception.getMessage(), equalTo("E-VSSQLS-1: Unable to get a JDBC data type for an sql column 0."));
        assertThat(exception.getCause().getCause(), instanceOf(JsonParsingException.class));
    }
}