package com.exasol.adapter.dialects.sqlserver;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.exasol.adapter.AdapterProperties;

public class SQLServerDialectFactoryTest {
    private SQLServerSqlDialectFactory factory;

    @BeforeEach
    void beforeEach() {
        this.factory = new SQLServerSqlDialectFactory();
    }

    @Test
    void testGetName() {
        assertThat(this.factory.getSqlDialectName(), equalTo("SQLSERVER"));
    }

    @Test
    void testCreateDialect() {
        assertThat(this.factory.createSqlDialect(null, AdapterProperties.emptyProperties()),
                instanceOf(SQLServerSqlDialect.class));
    }
}