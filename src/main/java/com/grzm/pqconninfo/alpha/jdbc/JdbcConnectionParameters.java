package com.grzm.pqconninfo.alpha.jdbc;

import com.grzm.pqconninfo.alpha.PqConninfo;
import com.grzm.pqconninfo.alpha.impl.JdbcElf;

import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/*
  TODO support multiple hosts
  Multiple hosts with libpq
  https://www.postgresql.org/docs/current/libpq-connect.html
  Connection Fail-over in JDBC, supports multiple hosts
  https://jdbc.postgresql.org/documentation/head/connect.html#connection-parameters
 */

/**
 * Connection Parameters for PostgreSQL JDBC Driver.
 */

public final class JdbcConnectionParameters {

    // url params
    private final String host;
    private final Integer port;
    private final String dbname;

    // query params
    private final String user;
    private final String password;
    private final String options;
    private final String sslmode;
    private final String sslcert;
    private final String sslkey;
    private final String sslrootcert;
    private final Integer connectTimeout;
    private final String kerberosServerName;
    private final String applicationName;
    private final Boolean tcpKeepAlive;
    private final String gssencmode;
    private final String gsslib;
    private final String sslpassword;
    private final String replication;

    private JdbcConnectionParameters(final PqConninfo conninfo) {
        this.host = JdbcElf.getHost(conninfo);
        this.port = JdbcElf.getPort(conninfo);
        this.dbname = JdbcElf.getDbname(conninfo);
        this.user = JdbcElf.getUser(conninfo);
        this.password = JdbcElf.getPassword(conninfo);
        this.options = JdbcElf.getOptions(conninfo);
        this.sslmode = JdbcElf.getSslmode(conninfo);
        this.sslcert = JdbcElf.getSslcert(conninfo);
        this.sslkey = JdbcElf.getSslkey(conninfo);
        this.sslrootcert = JdbcElf.getSslrootcert(conninfo);
        this.connectTimeout = JdbcElf.getConnectTimeout(conninfo);
        this.kerberosServerName = JdbcElf.getKerberosServerName(conninfo);
        this.applicationName = JdbcElf.getApplicationName(conninfo);
        this.tcpKeepAlive = JdbcElf.getTcpKeepAlive(conninfo);
        this.gssencmode = JdbcElf.getGssencmode(conninfo);
        this.gsslib = JdbcElf.getGsslib(conninfo);
        this.sslpassword = JdbcElf.getSslpassword(conninfo);
        this.replication = JdbcElf.getReplication(conninfo);
    }

    /**
     * Creates a JdbcConnectionParameters instance populated with values from
     * the system.
     *
     * @param conninfo the source PqConninfo instance
     * @return a new JdbcConnectionParameters instance
     */
    public static JdbcConnectionParameters from(final PqConninfo conninfo) {
        return new JdbcConnectionParameters(conninfo);
    }

    /**
     * Returns the JDBC URL corresponding to the JDBC connection parameters.
     * This is the short version of the JDBC url without query parameters
     *
     * @return the JDBC URL
     */
    public String getUrl() {
        return JdbcElf.makeUrl(this);
    }

    /**
     * Returns the JDBC URL including query string for the corresponding value
     * for given JDBCConnectionParameters instance.
     *
     * @return the JDBC URL including the query string
     */
    public String getUrlWithQueryString() {
        return JdbcElf.makeUrlWithQueryString(this);
    }

    /**
     * Returns the JDBC connection properties that necessary to accompany
     * a minimal JDBC URL.
     *
     * @return the JDBC connection parameter properties
     */
    public Properties getInfo() {
        return JdbcElf.makeInfo(this);
    }

    /**
     * Returns the given JDBC connection parameters as Java Properties.
     *
     * @return the JDBC connection parameters as properties
     */
    public Properties toProperties() {
        return JdbcElf.makeProperties(this);
    }

    /**
     * Returns the connectTimeout value for the JDBC connection parameters.
     *
     * @return the connectTimeout value
     */
    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Returns the dbname value for the JDBC connection parameters.
     *
     * @return the dbname value
     */
    public String getDbname() {
        return dbname;
    }

    /**
     * Returns the gssencmode value for the JDBC connection parameters.
     *
     * @return the gssencmode value
     */
    public String getGssencmode() {
        return gssencmode;
    }

    /**
     * Returns the gsslib value for the JDBC connection parameters.
     *
     * @return the gsslib value
     */
    public String getGsslib() {
        return gsslib;
    }

    /**
     * Returns the host value for the JDBC connection parameters.
     *
     * @return the host value
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the options value for the JDBC connection parameters.
     *
     * @return the options value
     */
    public String getOptions() {
        return options;
    }

    /**
     * Returns the password value for the JDBC connection parameters.
     *
     * @return the password value
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the port value for the JDBC connection parameters.
     *
     * @return the port value
     */
    public Integer getPort() {
        return port;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JdbcConnectionParameters that = (JdbcConnectionParameters) o;
        return Objects.equals(host, that.host)
                && Objects.equals(port, that.port)
                && Objects.equals(dbname, that.dbname)
                && Objects.equals(user, that.user)
                && Objects.equals(password, that.password)
                && Objects.equals(options, that.options)
                && Objects.equals(sslmode, that.sslmode)
                && Objects.equals(sslcert, that.sslcert)
                && Objects.equals(sslkey, that.sslkey)
                && Objects.equals(sslrootcert, that.sslrootcert)
                && Objects.equals(connectTimeout, that.connectTimeout)
                && Objects.equals(kerberosServerName, that.kerberosServerName)
                && Objects.equals(applicationName, that.applicationName)
                && Objects.equals(tcpKeepAlive, that.tcpKeepAlive)
                && Objects.equals(gssencmode, that.gssencmode)
                && Objects.equals(gsslib, that.gsslib)
                && Objects.equals(sslpassword, that.sslpassword)
                && Objects.equals(replication, that.replication);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, dbname, user, password,
                options, sslmode, sslcert, sslkey, sslrootcert,
                connectTimeout, kerberosServerName, applicationName,
                tcpKeepAlive, gssencmode, gsslib, sslpassword, replication);
    }

    @Override
    public String toString() {
        return "JdbcConnectionParameters{"
                + Arrays.stream(new String[]{
                (host == null ? null : "host='" + host + '\''),
                (port == null ? null : "port=" + port),
                (dbname == null ? null : "dbname='" + dbname + '\''),
                (user == null ? null : "user='" + user + '\''),
                (password == null ? null : "password='****'"),
                (options == null ? null : "options='" + options + '\''),
                (sslmode == null ? null : "sslmode='" + sslmode + '\''),
                (sslcert == null ? null : "sslcert='" + sslcert + '\''),
                (sslkey == null ? null : "sslkey='" + sslkey + '\''),
                (sslrootcert == null
                        ? null : "sslrootcert='" + sslrootcert + '\''),
                (connectTimeout == null
                        ? null : "connectTimeout=" + connectTimeout),
                (kerberosServerName == null
                        ? null
                        : "kerberosServerName='" + kerberosServerName + '\''),
                (applicationName == null ? null
                        : "ApplicationName='" + applicationName + '\''),
                (tcpKeepAlive == null ? null : "tcpKeepAlive=" + tcpKeepAlive),
                (gssencmode == null
                        ? null : "gssencmode='" + gssencmode + '\''),
                (gsslib == null ? null : "gsslib='" + gsslib + '\''),
                (sslpassword == null ? null : "sslpassword='****'"),
                (replication == null
                        ? null : "replication='" + replication + '\'')})
                .filter(Objects::nonNull).collect(Collectors.joining(", "))
                + '}';
    }

    /**
     * Returns the sslcert value for the JDBC connection parameters.
     *
     * @return the sslcert value
     */
    public String getSslcert() {
        return sslcert;
    }

    /**
     * Returns the sslkey value for the JDBC connection parameters.
     *
     * @return the sslkey value
     */
    public String getSslkey() {
        return sslkey;
    }

    /**
     * Returns the sslmode value for the JDBC connection parameters.
     *
     * @return the sslmode value
     */
    public String getSslmode() {
        return sslmode;
    }

    /**
     * Returns the sslrootcert value for the JDBC connection parameters.
     *
     * @return the sslrootcert value
     */
    public String getSslrootcert() {
        return sslrootcert;
    }

    /**
     * Returns the user value for the JDBC connection parameters.
     *
     * @return the user value
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the kerberosServerName value for the JDBC connection parameters.
     *
     * @return the kerberosServerName value
     */
    public String getKerberosServerName() {
        return kerberosServerName;
    }

    /**
     * Returns the ApplicationName value for the JDBC connection parameters.
     *
     * @return the ApplicationName value
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Returns the replication value for the JDBC connection parameters.
     *
     * @return the replication value
     */
    public String getReplication() {
        return replication;
    }

    /**
     * Returns the sslpassword value for the JDBC connection parameters.
     *
     * @return the sslpassword value
     */
    public String getSslpassword() {
        return sslpassword;
    }

    /**
     * Returns the gsslib value for the JDBC connection parameters.
     *
     * @return the tcpKeepAlive value
     */
    public Boolean getTcpKeepAlive() {
        return tcpKeepAlive;
    }

}
