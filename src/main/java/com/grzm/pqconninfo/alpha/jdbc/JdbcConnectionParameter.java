package com.grzm.pqconninfo.alpha.jdbc;

/**
 * JDBC connection parameters that can be supplied by libpq conninfo options.
 */
public enum JdbcConnectionParameter {
    HOST("host", false),
    PORT("port", false),
    DBNAME("dbname", false),
    USER("user"),
    PASSWORD("password"),
    OPTIONS("options"),
    SSL("ssl"), // TODO how to set?
    SSLMODE("sslmode"),
    SSLCERT("sslcert"),
    SSLKEY("sslkey"),
    SSLROOTCERT("sslrootcert"),
    CONNECT_TIMEOUT("connectTimeout"),
    KERBEROS_SERVER_NAME("kerberosServerName"),
    APPLICATION_NAME("ApplicationName"),
    TCP_KEEP_ALIVE("tcpKeepAlive"),
    GSSENCMODE("gssencmode"),
    GSSLIB("gsslib"),
    SSLPASSWORD("sslpassword"),
    REPLICATION("replication");

    /**
     * The property name of the parameter.
     */
    public final String keyword;

    /**
     * Whether or not the connection parameter is a query parameter in a JDBC
     * URL, as opposed to connection parameters such as host, port, and dbname
     * which are used in the host and path parts of the URL.
     */
    public final boolean isQueryParameter;
    /*
      TODO We can probably flip this from isQueryParameter to isProperty, as
      only those parameters that can be passed as properties to JDBC connection
      constructors.
     */

    /**
     * Constructor for connection parameters.
     *
     * @param keyword The name of the connection parameter
     * @param isQueryParameter whether the parameter is a query parameter.
     */
    JdbcConnectionParameter(final String keyword,
                            final boolean isQueryParameter) {
        this.keyword = keyword;
        this.isQueryParameter = isQueryParameter;
    }

    /**
     * Constructor for connection parameters that are also query parameters.
     * This is kinda the opposite of maybe expected behavior: often if we don't
     * supply an argument, we would assume it's absence, not its presence.
     * However, the presence is the common case, so it's more convenient to use
     * the verbose form in the rare case.
     *
     * These constructors are private, however, so this infelicity is confined
     * to the internals of this class.
     *
     * @param keyword The name of the connection parameter.
     */
    JdbcConnectionParameter(final String keyword) {
        this(keyword, true);
    }
}
