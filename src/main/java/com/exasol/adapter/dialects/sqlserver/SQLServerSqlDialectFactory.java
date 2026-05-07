package com.exasol.adapter.dialects.sqlserver;

import com.exasol.adapter.dialects.*;
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
    public SqlDialect createSqlDialect(final JDBCAdapterContext context) {
        return new SQLServerSqlDialect(context);
    }

    @Override
    public String getSqlDialectVersion() {
        final VersionCollector versionCollector = new VersionCollector(
                "META-INF/maven/com.exasol/sqlserver-virtual-schema/pom.properties");
        return versionCollector.getVersionNumber();
    }

    @Override
    public String getAdapterProjectShortTag() {
        return "VSSQLS";
    }
}
