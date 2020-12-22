package com.grzm.pqconninfo.alpha;

/**
 * Enumerated conninfo options corresponding roughly to
 * _internalPQconninfoOption in interfaces/libpq/fe-connect.c.
 */
public enum PqConninfoOption {
    APPLICATION_NAME("application_name", "PGAPPNAME"),
    /**
     * AUTHTYPE is obsolete, but included for completeness.
     */
    AUTHTYPE("authtype", "PGAUTHTYPE"),
    CHANNEL_BINDING("channel_binding", "PGCHANNELBINDING"),
    CLIENT_ENCODING("client_encoding", "PGCLIENTENCODING"),
    CONNECT_TIMEOUT("connect_timeout", "PGCONNECT_TIMEOUT"),
    DBNAME("dbname", "PGDATABASE"),
    FALLBACK_APPLICATION_NAME("fallback_application_name"),
    GSSENCMODE("gssencmode", "PGGSSENCMODE"),
    GSSLIB("gsslib", "PGGSSLIB"),
    HOST("host", "PGHOST"),
    HOSTADDR("hostaddr", "PGHOSTADDR"),
    KEEPALIVES("keepalives"),
    KEEPALIVES_COUNT("keepalives_count"),
    KEEPALIVES_IDLE("keepalives_idle"),
    KEEPALIVES_INTERNAL("keepalives_internal"),
    KRBSRVNAME("krbsrvname", "PGKRBSRVNAME"),
    OPTIONS("options", "PGOPTIONS"),
    /**
     * PASSFILE isn't used as a connection param.
     */
    PASSFILE("passfile", "PGPASSFILE"),
    PASSWORD("password", "PGPASSWORD"),
    PORT("port", "PGPORT"),
    REPLICATION("replication"),
    REQUIREPEER("requirepeer", "PGREQUIREPEER"),
    /**
     * SERVICE isn't used as a connection param.
     */
    SERVICE("service", "PGSERVICE"),
    SSLCERT("sslcert", "PGSSLCERT"),
    SSLCOMPRESSION("sslcompression", "PGSSLCOMPRESSION"),
    SSLCRL("sslcrl", "PGSSLCRL"),
    SSLKEY("sslkey", "PGSSLKEY"),
    SSLMODE("sslmode", "PGSSLMODE"),
    SSLPASSWORD("sslpassword"),
    SSLROOTCERT("sslrootcert", "PGSSLROOTCERT"),
    TARGET_SESSION_ATTRS("target_session_attrs", "PGTARGETSESSIONATTRS"),
    TCP_USER_TIMEOUT("tcp_user_timeout"),
    /**
     * TTY is obsolete, but included for completeness.
     */
    TTY("tty", "PGTTY"),
    USER("user", "PGUSER");

    /**
     * The struct field name in _internalPQconninfoOption. For consistency with
     * libpq, I'm using these keywords as property names, even though the
     * spellings aren't consistent with Java conventions.
     */
    public final String keyword;

    /**
     * The environment variable name for the associated option
     * (when one is available).
     */
    public final String environmentVariable;

    /**
     * Constructor used for options without environment variables.
     *
     * @param keyword keyword/property name for the option
     */
    PqConninfoOption(final String keyword) {
        this.keyword = keyword;
        this.environmentVariable = null;
    }

    /**
     * @param keyword             keyword/property name for the option
     * @param environmentVariable name of environment variable for the option
     */
    PqConninfoOption(final String keyword, final String environmentVariable) {
        this.keyword = keyword;
        this.environmentVariable = environmentVariable;
    }

}
