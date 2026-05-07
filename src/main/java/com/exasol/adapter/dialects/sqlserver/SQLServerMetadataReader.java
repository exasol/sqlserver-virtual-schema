package com.exasol.adapter.dialects.sqlserver;

import java.sql.Connection;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.BaseIdentifierConverter;
import com.exasol.adapter.dialects.IdentifierConverter;
import com.exasol.adapter.jdbc.*;

/**
 * This class implement a SQLServer-specific metadata reader.
 */
public class SQLServerMetadataReader extends AbstractRemoteMetadataReader {
    /**
     * Create a new instance of a {@link SQLServerMetadataReader}.
     *
     * @param connection  JDBC connection to the remote data source
     * @param properties  user-defined adapter properties
     * @param exaMetadata Exasol metadata
     */
    public SQLServerMetadataReader(final Connection connection, final AdapterProperties properties, final ExaMetadata exaMetadata) {
        super(connection, properties, exaMetadata);
    }

    @Override
    protected ColumnMetadataReader createColumnMetadataReader() {
        return new SQLServerColumnMetadataReader(this.connection, this.properties, this.exaMetadata, getIdentifierConverter());
    }

    @Override
    protected TableMetadataReader createTableMetadataReader() {
        return new BaseTableMetadataReader(this.connection, this.columnMetadataReader, this.properties, this.exaMetadata,
                this.identifierConverter);
    }

    @Override
    protected IdentifierConverter createIdentifierConverter() {
        return BaseIdentifierConverter.createDefault();
    }
}
