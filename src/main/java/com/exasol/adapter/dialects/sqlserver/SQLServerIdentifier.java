package com.exasol.adapter.dialects.sqlserver;

import java.util.Objects;

import com.exasol.db.Identifier;
import com.exasol.errorreporting.ExaError;

/**
 * Represents an identifier in the Sql Server database.
 */
public class SQLServerIdentifier implements Identifier {
    private final String id;

    private SQLServerIdentifier(final String id) {
        this.id = id;
    }

    /**
     * Get the quoted identifier as a {@link String}.
     *
     * @return quoted identifier
     */
    @Override
    public String quote() {
        return "[" + this.id + "]";
    }

    /**
     * Create a new {@link SQLServerIdentifier}.
     *
     * @param id the identifier as {@link String}
     * @return new {@link SQLServerIdentifier} instance
     */
    public static SQLServerIdentifier of(final String id) {
        if (validate(id)) {
            return new SQLServerIdentifier(id);
        } else {
            throw new AssertionError(ExaError.messageBuilder("E-VSSQLS-3")
                    .message("E-ID-4: Unable to create identifier \"{{id|uq}}\" because it contains illegal characters."
                            + " For information about valid identifiers, please refer to"
                            + " https://docs.microsoft.com/sql/relational-databases/databases/database-identifiers?view=sql-server-ver15",
                            id)
                    .toString());
        }
    }

    private static boolean validate(final String id) {
        return !id.contains("[") && !id.contains("]") && !id.contains("\\");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SQLServerIdentifier)) {
            return false;
        }
        final SQLServerIdentifier that = (SQLServerIdentifier) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
