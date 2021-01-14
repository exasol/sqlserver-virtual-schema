package com.exasol.adapter.dialects.sqlserver;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.SqlDialectFactory;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.logging.VersionCollector;

/**
 * Factory for the SQL Server dialect.
 */
public class SQLServerSqlDialectFactory implements SqlDialectFactory {
    @Override
    public String getSqlDialectName() {
        return SQLServerSqlDialect.NAME;
    }

    @Override
    public SqlDialect createSqlDialect(final ConnectionFactory connectionFactory, final AdapterProperties properties) {
        return new SQLServerSqlDialect(connectionFactory, properties);
    }

    @Override
    public String getSqlDialectVersion() {
        final VersionCollector versionCollector = new VersionCollector(
                "META-INF/maven/com.exasol/sqlserver-virtual-schema/pom.properties");
        return versionCollector.getVersionNumber();
    }
}