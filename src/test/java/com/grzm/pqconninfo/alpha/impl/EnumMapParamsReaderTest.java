package com.grzm.pqconninfo.alpha.impl;

import com.grzm.pqconninfo.alpha.PqConninfo;
import com.grzm.pqconninfo.alpha.PqConninfoOption;
import com.grzm.pqconninfo.alpha.jdbc.JdbcConnectionParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumMap;
import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EnumMapParamsReaderTest {

    static Stream<Arguments> jdbcUrlParamProvider() {
        return Stream.of(
                arguments("jdbc:postgresql://some_host:9999/some_database",
                        "jdbc:postgresql://some_host:9999/some_database?user=some_user&password=some_password&kerberosServerName=someKerberosServerName&ApplicationName=some+application+name",
                        new Properties() {{
                            setProperty("host", "some_host");
                            setProperty("dbname", "some_database");
                            setProperty("port", "9999");
                            setProperty("user", "some_user");
                            setProperty("password", "some_password");
                            setProperty("ApplicationName", "some application name");
                            setProperty("kerberosServerName", "someKerberosServerName");
                        }},
                        new Properties() {{
                            setProperty("user", "some_user");
                            setProperty("password", "some_password");
                            setProperty("ApplicationName", "some application name");
                            setProperty("kerberosServerName", "someKerberosServerName");
                        }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "some_host");
                            put(PqConninfoOption.PORT, "9999");
                            put(PqConninfoOption.DBNAME, "some_database");
                            put(PqConninfoOption.USER, "some_user");
                            put(PqConninfoOption.PASSWORD, "some_password");
                            put(PqConninfoOption.APPLICATION_NAME, "some application name");
                            put(PqConninfoOption.KRBSRVNAME, "someKerberosServerName");
                        }}),
                arguments("jdbc:postgresql://some_host/some_database",
                        "jdbc:postgresql://some_host/some_database?user=some_user&password=some_password&kerberosServerName=someKerberosServerName&ApplicationName=some+application+name",
                        new Properties() {{
                            setProperty("host", "some_host");
                            setProperty("dbname", "some_database");
                            setProperty("user", "some_user");
                            setProperty("password", "some_password");
                            setProperty("ApplicationName", "some application name");
                            setProperty("kerberosServerName", "someKerberosServerName");
                        }},
                        new Properties() {{
                            setProperty("user", "some_user");
                            setProperty("password", "some_password");
                            setProperty("ApplicationName", "some application name");
                            setProperty("kerberosServerName", "someKerberosServerName");
                        }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "some_host");
                            put(PqConninfoOption.DBNAME, "some_database");
                            put(PqConninfoOption.USER, "some_user");
                            put(PqConninfoOption.PASSWORD, "some_password");
                            put(PqConninfoOption.APPLICATION_NAME, "some application name");
                            put(PqConninfoOption.KRBSRVNAME, "someKerberosServerName");
                        }}
                ));
    }

    @ParameterizedTest
    @MethodSource("jdbcUrlParamProvider")
    void getJdbcUrl(String expectedUrl, String expectedUrlWithQueryString,
                    Properties expectedProps,
                    Properties expectedUrlProps,
                    EnumMap<PqConninfoOption, String> opts) {

        PqConninfo conninfo = PqConninfo.from(opts);
        JdbcConnectionParameters params = new EnumMapParamsReader().read(conninfo);

        assertEquals(expectedUrl, params.getUrl());
        assertEquals(expectedUrlProps, params.getInfo());
        assertEquals(expectedUrlWithQueryString, params.getUrlWithQueryString());
        assertEquals(expectedProps, params.toProperties());
    }

    @Test
    void jdbcConnectionParametersFromPqConninfo() {
        EnumMap<PqConninfoOption, String> opts = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.HOST, "some_host");
            put(PqConninfoOption.PORT, "6543");
            put(PqConninfoOption.DBNAME, "some_database");
            put(PqConninfoOption.USER, "some_user");
            put(PqConninfoOption.PASSWORD, "some_password");
            put(PqConninfoOption.OPTIONS, "some options");
            put(PqConninfoOption.SSLMODE, "require");
            put(PqConninfoOption.SSLCERT, "/path/to/ssl/cert");
            put(PqConninfoOption.SSLKEY, "/path/to/ssl/key");
            put(PqConninfoOption.SSLROOTCERT, "/path/to/ssl/root/cert");
            put(PqConninfoOption.CONNECT_TIMEOUT, "-2");
            put(PqConninfoOption.KRBSRVNAME, "someKerberosServerName");
            put(PqConninfoOption.APPLICATION_NAME, "some application name");
            put(PqConninfoOption.KEEPALIVES, "1");
            put(PqConninfoOption.GSSENCMODE, "disable");
            put(PqConninfoOption.GSSLIB, "gssapi");
            put(PqConninfoOption.SSLPASSWORD, "some ssl password");
            put(PqConninfoOption.REPLICATION, "yes");
        }};

        PqConninfo conninfo = PqConninfo.from(opts);

        JdbcConnectionParameters params = new EnumMapParamsReader().read(conninfo);

        Properties expectedProps = new Properties() {{
            setProperty("host", "some_host");
            setProperty("port", "6543");
            setProperty("dbname", "some_database");
            setProperty("user", "some_user");
            setProperty("password", "some_password");
            setProperty("options", "some options");
            setProperty("sslmode", "require");
            setProperty("sslcert", "/path/to/ssl/cert");
            setProperty("sslkey", "/path/to/ssl/key");
            setProperty("sslrootcert", "/path/to/ssl/root/cert");
            setProperty("connectTimeout", "0");
            setProperty("kerberosServerName", "someKerberosServerName");
            setProperty("ApplicationName", "some application name");
            setProperty("tcpKeepAlive", "true");
            setProperty("gssencmode", "disable");
            setProperty("gsslib", "gssapi");
            setProperty("sslpassword", "some ssl password");
            setProperty("replication", "true");
        }};

        assertEquals(expectedProps, params.toProperties());

    }
}
