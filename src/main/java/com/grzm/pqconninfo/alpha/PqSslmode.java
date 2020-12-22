package com.grzm.pqconninfo.alpha;

/**
 * Enumerated SSL mode values.
 *
 * Details can be found in the PostgreSQL documentation:
 * @see <a href="https://www.postgresql.org/docs/current/libpq-ssl.html#LIBPQ-SSL-SSLMODE-STATEMENTS">libpq SSL Modes</a>
 */
public enum PqSslmode {
    DISABLE("disable"),
    ALLOW("allow"),
    PREFER("prefer"),
    REQUIRE("require"),
    VERIFY_CA("verify-ca"),
    VERIFY_FULL("verify-full");

    /**
     * Value of the SSL mode option.
     */
    public final String keyword;

    /**
     * @param keyword Value of the SSL mode option.
     */
    PqSslmode(final String keyword) {
        this.keyword = keyword;
    }
}
