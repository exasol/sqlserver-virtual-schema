package com.exasol.adapter.dialects.sqlserver;

import static com.exasol.adapter.metadata.DataType.ExaCharset.UTF8;

import java.sql.Connection;
import java.sql.Types;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.IdentifierConverter;
import com.exasol.adapter.jdbc.BaseColumnMetadataReader;
import com.exasol.adapter.jdbc.JDBCTypeDescription;
import com.exasol.adapter.metadata.DataType;

/**
 * This class implements a SQLServer-specific column metadata reader.
 */
public class SQLServerColumnMetadataReader extends BaseColumnMetadataReader {
    private static final int SQL_SERVER_DATETIME_OFFSET = -155;

    /**
     * Create a new instance of the {@link SQLServerColumnMetadataReader}.
     *
     * @param connection          JDBC connection to the remote data source
     * @param properties          user-defined adapter properties
     * @param identifierConverter converter between source and Exasol identifiers
     */
    public SQLServerColumnMetadataReader(final Connection connection, final AdapterProperties properties,
            final IdentifierConverter identifierConverter) {
        super(connection, properties, identifierConverter);
    }

    @Override
    public DataType mapJdbcType(final JDBCTypeDescription jdbcTypeDescription) {
        switch (jdbcTypeDescription.getJdbcType()) {
        case Types.NUMERIC:
            return mapJdbcTypeNumericToDecimalWithFallbackToDouble(jdbcTypeDescription);
        case SQL_SERVER_DATETIME_OFFSET:
            return DataType.createVarChar(jdbcTypeDescription.getPrecisionOrSize(), UTF8);
        default:
            return super.mapJdbcType(jdbcTypeDescription);
        }
    }
}