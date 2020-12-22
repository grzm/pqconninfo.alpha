package com.grzm.pqconninfo.alpha.impl;

import com.grzm.pqconninfo.alpha.jdbc.JdbcConnectionParameter;
import com.grzm.pqconninfo.alpha.jdbc.JdbcConnectionParameters;
import com.grzm.pqconninfo.alpha.util.PropertyElf;
import com.grzm.pqconninfo.alpha.PqConninfo;
import com.grzm.pqconninfo.alpha.PqConninfoOption;
import com.grzm.pqconninfo.alpha.PqSslmode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
   PQconninfoOption          JDBC param
   :authtype

   :host                      :host

   :hostaddr                  n/a

   :port                      :port

   :dbname                    :dbname

   :client-encoding           n/a (set by driver)
   In JDBC, there's an :allowEncodingChanges boolean used to specify
   whether you can change encodings for handling things like COPY.

   :options                   options String,

   :user                      :user String,

   :password                  :password String,


   :requiressl                :ssl boolean
   :libpq/requiressl is deprecated in favor of :libpq/sslmode.
    It's enumerated values are "1" and "0", for true and false.

   TODO figure out if I should set :jdbc/ssl to true depending on the
    :libpq/sslmode value, in addition to setting :jdbc/sslmode

   :sslmode              :sslmode String
   :libpq/sslmode enumerated values:
      disable, allow, prefer, require, verify-ca, verify-full
   :jdbc/sslmode enumerated values:
      disable, allow, prefer, require, verify-ca, verify-full

   :sslcert                   :sslcert String - full path to certificate file,

   :sslkey                    :sslkey String - full path to key file,

   :sslcrl                    n/a,

   :sslrootcert               :sslrootcert String - file name (not path?),

   ;; int
   :connect_timeout           :connectTimeout int
   :libpq/connect_timeout is int seconds; 0, or negative means disabled

   :jdbc/connectTimeout int - in seconds; 0 means disabled,

                              :loginTimeout int -- in seconds
                              :socketTimeout int -- in seconds
                              :cancelSignalTimeout int -- in seconds

   :krbsrvname                :kerberosServerName,

   :application-name          :ApplicationName,

   :fallback-application-name nil,

   :keepalives 1/0             :tcpKeepAlive boolean,
   :libpq/keepalives is enumerated "1"/"0" for true/false

   :keepalives-idle            n/a
   :keepalives-interval        n/a
   :keepalives-count           n/a
   :tty (obsolete)             n/a
   :requirepeer                n/a

   :gssencmode                :gssEncMode String
   :libpq/gssencmode enumerated values: "disable", "prefer", "require"
   :jdbc/gssEncMode String enumerated values:
      "disable", "allow", "prefer", "require"

   :gsslib                    :gsslib String
   :libpq/gsslib  {gssapi}, only recognized on Windows
   :jdbc/gsslib String enumerated {auto, sspi, gssapi}

   :service                    n/a

   :sslpassword                :sslpassword String

   :replication                :replication String
   :libpq/replication values are enumerated:
   "true", "on", "yes, "1" -- truthy values
   "database"
   "false", "off", "no","0" --  falsey values

   :jdbc/replication are enumerated "true" and "database".
   If libpq/replication is falsey, just don't set :jdbc/replication

   JBDC connection parameters with no corresponding libpq parameter:
                              :sslfactory String - classname
                              :sslfactoryary String - (deprecated)
                              :sslhostnameverifier String - classname
                              :sslpasswordcallback String -- classname
                              :protocolVersion int
                              :loggerLevel String
                              :loggerFile String -- path
                              :allowEncodingChanges boolean
                              :loadBalanceHosts boolean
 */

public final class JdbcElf {

    private JdbcElf() {
    }

    /**
     * Read a PqConninfoOption value as an Integer.
     *
     * @param opt      the PqConninfoOption to read
     * @param conninfo the PqConninfo providing the values
     * @return the Integer value of the PqConninfoOption, or null
     */
    static Integer integerFromConninfoOption(final PqConninfo conninfo,
                                             final PqConninfoOption opt) {
        String val = conninfo.get(opt);

        if (val == null) {
            return null;
        }

        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Read a PqConninfoOption value as a String.
     *
     * @param opt      the PqConninfoOption to read
     * @param conninfo the PqConninfo providing the values
     * @return the String value of the PqConninfoOption, or null
     */
    static String stringFromConninfoOption(final PqConninfo conninfo,
                                           final PqConninfoOption opt) {
        Object val = conninfo.get(opt);

        if (val == null) {
            return null;
        }
        // TODO Should we return null if val.toString() is blank?
        return val.toString();
    }

    /**
     * Read a PqConninfoOption value represented as an integer string as a
     * boolean.
     * <p>
     * PqConninfo options are consistent in using "0" as false and "1" as true.
     *
     * @param opt      the PqConninfoOption to read
     * @param conninfo the PqConninfo instance providing the values
     * @return the boolean value of the PqConninfo option, or null
     */
    static Boolean booleanFromConninfoOptionInteger(
            final PqConninfo conninfo, final PqConninfoOption opt) {
        String val = stringFromConninfoOption(conninfo, opt);

        if (val == null) {
            return null;
        }

        switch (val) {
            case "0":
                return false;
            case "1":
                return true;
            default:
                // TODO Should we throw here instead?
                return null;
        }
    }

    /**
     * Get the port value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the port value, or null
     */
    public static Integer getPort(final PqConninfo conninfo) {
        return integerFromConninfoOption(conninfo, PqConninfoOption.PORT);
    }

    /**
     * Get the dbname value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the dbname value
     */
    public static String getDbname(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo, PqConninfoOption.DBNAME);
    }

    /**
     * Get the host value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the host value
     */
    public static String getHost(final PqConninfo conninfo) {
        return conninfo.get(PqConninfoOption.HOST);
    }

    /**
     * Get the user value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the user value
     */
    public static String getUser(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo, PqConninfoOption.USER);
    }

    /**
     * Get the password value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the password value
     */
    public static String getPassword(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo, PqConninfoOption.PASSWORD);
    }

    /**
     * Get the options value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the options value
     */
    public static String getOptions(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo, PqConninfoOption.OPTIONS);
    }

    /*
    static Boolean getSsl(PqConninfo conninfo) {
        // TODO I think there used to be a PQ conninfo option called requiressl
        // Confirm if/when it was removed. Include for backwards compatibility?
        // TODO Should ssl be true if sslmode is set?
        return null;
    }
     */

    /**
     * Enumeration of JDBC sslmode connetion parameter values.
     */
    enum Sslmode {
        DISABLE("disable"),
        ALLOW("allow"),
        PREFER("prefer"),
        REQUIRE("require"),
        VERIFY_CA("verify-ca"),
        VERIFY_FULL("verify-full");

        /**
         * String representation of the JDBC sslmode enumerated value.
         */
        public final String keyword;

        Sslmode(final String keyword) {
            this.keyword = keyword;
        }
    }

    /**
     * Mapping of PqConninfo sslmode values to JDBC sslmode values.
     */
    static final HashMap<String, Sslmode> PQ_SSLMODE_MAP
            = new HashMap<String, Sslmode>() {{
        put(PqSslmode.DISABLE.keyword, Sslmode.DISABLE);
        put(PqSslmode.ALLOW.keyword, Sslmode.ALLOW);
        put(PqSslmode.PREFER.keyword, Sslmode.PREFER);
        put(PqSslmode.REQUIRE.keyword, Sslmode.REQUIRE);
        put(PqSslmode.VERIFY_CA.keyword, Sslmode.VERIFY_CA);
        put(PqSslmode.VERIFY_FULL.keyword, Sslmode.VERIFY_FULL);
    }};

    /**
     * Get the sslmode value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the sslmode value
     */
    public static String getSslmode(final PqConninfo conninfo) {
        String pqSslmode = stringFromConninfoOption(conninfo,
                PqConninfoOption.SSLMODE);

        if (pqSslmode == null) {
            return null;
        }

        Sslmode sslmode = PQ_SSLMODE_MAP.get(pqSslmode);

        if (sslmode == null) {
            return null;
        }

        return sslmode.keyword;
    }

    /**
     * Get the sslcert value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the sslcert value
     */
    public static String getSslcert(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo, PqConninfoOption.SSLCERT);
    }

    /**
     * Get the sslkey value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the sslkey value
     */
    public static String getSslkey(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo, PqConninfoOption.SSLKEY);
    }

    /**
     * Get the sslrootcert value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the sslrootcert value
     */
    public static String getSslrootcert(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo,
                PqConninfoOption.SSLROOTCERT);
    }

    /**
     * Get the connectTimeout value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the connectTimeout value
     */
    public static Integer getConnectTimeout(final PqConninfo conninfo) {
        Integer connectTimeout = integerFromConninfoOption(conninfo,
                PqConninfoOption.CONNECT_TIMEOUT);
        if (connectTimeout == null) {
            return null;
        }

        if (connectTimeout <= 0) {
            return 0;
        }

        return connectTimeout;
    }

    /**
     * Get the kerberosServerName value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the kerberosServerName value
     */
    public static String getKerberosServerName(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo,
                PqConninfoOption.KRBSRVNAME);
    }

    /**
     * Get the ApplicationName value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the ApplicationName value
     */
    public static String getApplicationName(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo,
                PqConninfoOption.APPLICATION_NAME);
    }

    /**
     * Get the tcpKeepAlive value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the tcpKeepAlive value
     */
    public static Boolean getTcpKeepAlive(final PqConninfo conninfo) {
        return booleanFromConninfoOptionInteger(conninfo,
                PqConninfoOption.KEEPALIVES);
    }

    /**
     * Enumeration of JDBC gssencmode values.
     */
    enum Gssencmode {
        DISABLE("disable"),
        ALLOW("allow"), // no corresponding conninfo gssencmode value
        PREFER("prefer"),
        REQUIRE("require");

        /**
         * String representation of the JDBC gssencmode value.
         */
        public final String keyword;

        Gssencmode(final String keyword) {
            this.keyword = keyword;
        }
    }

    /**
     * Mapping of PqConninfo gssencmode values to JDBC gssencmode values.
     */
    static final HashMap<String, Gssencmode> PQ_GSSENCMODE_MAP
            = new HashMap<String, Gssencmode>() {{
        put("disable", Gssencmode.DISABLE);
        put("prefer", Gssencmode.PREFER);
        put("require", Gssencmode.REQUIRE);
    }};

    /**
     * Get the gssencmode value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the gssencmode value
     */
    public static String getGssencmode(final PqConninfo conninfo) {
        String pqGssencmode = stringFromConninfoOption(conninfo,
                PqConninfoOption.GSSENCMODE);

        if (pqGssencmode == null) {
            return null;
        }

        Gssencmode gssencmode = PQ_GSSENCMODE_MAP.get(pqGssencmode);

        return (gssencmode == null) ? null : gssencmode.keyword;
    }

    /**
     * Enumerated JDBC gsslib connection parameter values.
     */
    enum Gsslib {
        AUTO("auto"), // not a recognized PqConninfo gsslib value
        SSPI("sspi"), // not a recognized PqConninfo gsslib value
        GSSAPI("gssapi");

        /**
         * String representation of the JDBC gsslib value.
         */
        public final String keyword;

        Gsslib(final String keyword) {
            this.keyword = keyword;
        }
    }

    /**
     * Mapping of PqConninfo gsslib values to JDBC gsslib values.
     */
    static final HashMap<String, Gsslib> PQ_GSSLIB_MAP
            = new HashMap<String, Gsslib>() {{
        put("gssapi", Gsslib.GSSAPI);
    }};

    /**
     * Get the gsslib value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the gsslib value
     */
    public static String getGsslib(final PqConninfo conninfo) {
        String pqGsslib = stringFromConninfoOption(conninfo,
                PqConninfoOption.GSSLIB);

        if (pqGsslib == null) {
            return null;
        }

        Gsslib gsslib = PQ_GSSLIB_MAP.get(pqGsslib);
        return (gsslib == null) ? null : gsslib.keyword;
    }

    /**
     * Get the sslpassword value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the sslpassword value
     */
    public static String getSslpassword(final PqConninfo conninfo) {
        return stringFromConninfoOption(conninfo, PqConninfoOption.SSLPASSWORD);
    }

    /**
     * Enumeration of the JDBC replication connection parameter values.
     */
    enum Replication {
        TRUE("true"),
        DATABASE("database");

        /**
         * The string representation of the JDBC replication value.
         */
        public final String keyword;

        Replication(final String keyword) {
            this.keyword = keyword;
        }
    }

    /**
     * Mapping of PqConninfo replication values to JDBC replication values.
     */
    static final HashMap<String, Replication> PQ_REPLICATION_MAP
            = new HashMap<String, Replication>() {{
        put("true", Replication.TRUE);
        put("on", Replication.TRUE);
        put("yes", Replication.TRUE);
        put("1", Replication.TRUE);
        put("database", Replication.DATABASE);
    }};

    /**
     * Get the replication value from the PqConninfo instance.
     *
     * @param conninfo the PqConninfo instance
     * @return the replication value
     */
    public static String getReplication(final PqConninfo conninfo) {
        String pqReplication = stringFromConninfoOption(conninfo,
                PqConninfoOption.REPLICATION);

        if (pqReplication == null) {
            return null;
        }

        Replication replication = PQ_REPLICATION_MAP.get(pqReplication);

        return (replication == null) ? null : replication.keyword;
    }

    /**
     * The JDBC URL scheme.
     */
    static final String JDBC_URL_SCHEME = "jdbc:postgresql";

    /**
     * Makes a JDBC URL from the parameters in the given
     * JdbcReadableConnectionParameters instance. This is the minimal JDBC URL
     * without a query string.
     *
     * @param params the source JdbcReadableConnectionParameters instance
     * @return the JDBC URL for the given connection parameters
     */
    public static String makeUrl(
            final JdbcConnectionParameters params) {
        if (params.getHost() == null) {
            if (params.getDbname() == null) {
                return JDBC_URL_SCHEME + ":/";
            } else {
                return JDBC_URL_SCHEME + ":" + params.getDbname();
            }
        }

        return JDBC_URL_SCHEME + "://" + params.getHost()
                + (params.getPort() != null ? ":" + params.getPort() : "")
                + "/" + (params.getDbname() == null ? "" : params.getDbname());

    }

    /**
     * URL-encodes the given query string parameter and value.
     *
     * @param name the query string parameter name
     * @param val  the query string parameter value
     * @return the encoded query string parameter, "name=encodedValue"
     */
    static String encodeUrlParam(final String name, final String val) {
        if (name != null && val != null) {
            /*
              The PostgreSQL JDBC Driver uses java.net.UrlEncoder and
              java.net.UrlDecoder (with UTF-8 as the default) for parsing jdbc
              url parameters. See org.postgresql.util.URLCoder.decode. Per the
              comments, this isn't a public interface, so
              *caveat programmator*.

              The java.net.UrlEncoder.encode method uses `+` for encoding
              spaces, rather than `%20`. Per spec, percent encoding is required
              for URL query parameters. This is a long-standing gotcha when
              using java.net.UrlEncoder.

              We're not encoding the name, as the keyword values don't contain
              characters requiring encoding.
             */
            try {
                return name + "="
                        + URLEncoder.encode(val,
                        StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                // This should never throw, as we're always using UTF-8
                // encoding, referenced from StandardCharsets.
                return null;
            }
        }

        return null;
    }

    /**
     * Returns a JDBC URL query string for the given connection parameters.
     *
     * @param params the source connection parameters
     * @return the JDBC URL query string
     */
    static String makeQueryParameterString(
            final JdbcConnectionParameters params) {
        Stream<JdbcConnectionParameter> authParams
                = Stream.of(JdbcConnectionParameter.USER,
                JdbcConnectionParameter.PASSWORD);

        Stream<JdbcConnectionParameter> otherParams
                = Arrays.stream(JdbcConnectionParameter.values())
                .filter(p -> p != JdbcConnectionParameter.PASSWORD
                        && p != JdbcConnectionParameter.USER)
                .filter(p -> p.isQueryParameter)
                .sorted();

        List<String> ps = Stream.concat(authParams, otherParams)
                .map(p -> {
                    String name = p.keyword;
                    Object val = PropertyElf.getProperty(name, params);
                    if (val == null) {
                        return null;
                    }
                    return encodeUrlParam(name, val.toString());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (ps.isEmpty()) {
            return null;
        }

        return String.join("&", ps);
    }

    /**
     * Constructs the full JDBC URL with query string from the given
     * connection parameters.
     *
     * @param params the source connection parameters
     * @return the JDBC URL with query string
     */
    public static String makeUrlWithQueryString(
            final JdbcConnectionParameters params) {
        String url = makeUrl(params);
        String paramString = makeQueryParameterString(params);
        return url + ((paramString == null) ? "" : "?" + paramString);
    }

    /**
     * Returns Properties suitable for creating a PostgreSQL connection with
     * an accompanying minimal JDBC URL.
     *
     * @param params The
     * @return the connection properties
     */
    public static Properties makeInfo(
            final JdbcConnectionParameters params) {
        Properties props = new Properties();
        Arrays.stream(JdbcConnectionParameter.values())
                .filter(p -> p.isQueryParameter)
                .forEach(p -> {
                    String propName = p.keyword;
                    Object val = PropertyElf.getProperty(propName, params);
                    if (val != null) {
                        props.setProperty(propName, val.toString());
                    }
                });
        return props;
    }

    /**
     * Returns the given JdbcConnectionParameters as Properties.
     *
     * @param params the JdbcConnectionParameters instance
     * @return the connection parameter properties
     */
    public static Properties makeProperties(
            final JdbcConnectionParameters params) {
        Properties props = new Properties();
        Arrays.stream(JdbcConnectionParameter.values())
                .forEach(p -> {
                    String propName = p.keyword;
                    Object val = PropertyElf.getProperty(propName, params);
                    if (val != null) {
                        props.setProperty(propName, val.toString());
                    }
                });
        return props;
    }
}
