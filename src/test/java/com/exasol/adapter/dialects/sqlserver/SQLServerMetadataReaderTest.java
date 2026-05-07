package com.exasol.adapter.dialects.sqlserver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.jdbc.BaseTableMetadataReader;

@ExtendWith(MockitoExtension.class)
class SQLServerMetadataReaderTest {
    private SQLServerMetadataReader reader;

    @BeforeEach
    void beforeEach(@Mock final ExaMetadata exaMetadataMock) {
        when(exaMetadataMock.getDatabaseVersion()).thenReturn("1.2.3");
        this.reader = new SQLServerMetadataReader(null, AdapterProperties.emptyProperties(), exaMetadataMock);
    }

    @Test
    void testGetTableMetadataReader() {
        assertThat(this.reader.getTableMetadataReader(), instanceOf(BaseTableMetadataReader.class));
    }

    @Test
    void testGetColumnMetadataReader() {
        assertThat(this.reader.getColumnMetadataReader(), instanceOf(SQLServerColumnMetadataReader.class));
    }
}
