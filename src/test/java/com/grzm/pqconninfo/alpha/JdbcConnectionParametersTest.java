package com.grzm.pqconninfo.alpha;

import com.grzm.pqconninfo.alpha.jdbc.JdbcConnectionParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumMap;
import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JdbcConnectionParametersTest {

    @Test
    void redactedPasswords() {
        EnumMap<PqConninfoOption, String> opts = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.USER, "alice");
            put(PqConninfoOption.SSLPASSWORD, "change-me");
            put(PqConninfoOption.PASSWORD, "super-secret");
        }};

        PqConninfo conninfo = PqConninfo.from(opts);
        JdbcConnectionParameters params = JdbcConnectionParameters.from(conninfo);

        assertEquals("JdbcConnectionParameters{user='alice', password='****', sslpassword='****'}",
                params.toString());
        assertEquals("super-secret", params.getPassword());
        assertEquals("change-me", params.getSslpassword());
        assertEquals("alice", params.getUser());

        JdbcConnectionParameters params2 = JdbcConnectionParameters.from(conninfo);
        assertEquals(params, params2);
        assertNotSame(params, params2);

    }

    static Stream<Arguments> optsForPropertiesProvider() {
        return Stream.of(
                arguments(
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.USER, "some-user");
                            put(PqConninfoOption.PASSWORD, "some-password");
                        }},
                        "jdbc:postgresql:/",
                        new Properties(){{
                            setProperty("user", "some-user");
                            setProperty("password", "some-password");
                        }},
                        "jdbc:postgresql:/?user=some-user&password=some-password",
                        new Properties() {{
                            setProperty("user", "some-user");
                            setProperty("password", "some-password");
                        }}),

                arguments(
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.DBNAME, "some-dbname");
                            put(PqConninfoOption.USER, "some-user");
                            put(PqConninfoOption.PASSWORD, "some-password");
                        }},
                        "jdbc:postgresql:some-dbname",
                        new Properties() {{
                            setProperty("user", "some-user");
                            setProperty("password", "some-password");
                        }},
                        "jdbc:postgresql:some-dbname?user=some-user&password=some-password",
                        new Properties() {{
                            setProperty("dbname", "some-dbname");
                            setProperty("user", "some-user");
                            setProperty("password", "some-password");
                        }}),

                arguments(
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "some-host");
                            put(PqConninfoOption.PORT, "6543");
                            put(PqConninfoOption.USER, "some-user");
                            put(PqConninfoOption.PASSWORD, "some-password");
                        }},
                        "jdbc:postgresql://some-host:6543/",
                        new Properties() {{
                            setProperty("user", "some-user");
                            setProperty("password", "some-password");
                        }},
                        "jdbc:postgresql://some-host:6543/?user=some-user&password=some-password",
                        new Properties() {{
                            setProperty("host", "some-host");
                            setProperty("port", "6543");
                            setProperty("user", "some-user");
                            setProperty("password", "some-password");
                        }}),

                arguments(
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "some-host");
                            put(PqConninfoOption.PORT, "6543");
                            put(PqConninfoOption.DBNAME, "some-dbname");
                            put(PqConninfoOption.USER, "some-user");
                            put(PqConninfoOption.PASSWORD, "some-password");
                        }},
                        "jdbc:postgresql://some-host:6543/some-dbname",
                        new Properties() {{
                            setProperty("user", "some-user");
                            setProperty("password", "some-password");
                        }},
                        "jdbc:postgresql://some-host:6543/some-dbname?user=some-user&password=some-password",
                        new Properties() {{
                            setProperty("host", "some-host");
                            setProperty("port", "6543");
                            setProperty("dbname", "some-dbname");
                            setProperty("user", "some-user");
                            setProperty("password", "some-password");
                        }})
        );
    }

    @ParameterizedTest
    @MethodSource("optsForPropertiesProvider")
    void toProperties(EnumMap<PqConninfoOption, String> opts,
                      String expectedUrl, Properties expectedInfo,
                      String expectedUrlWithQueryString,
                      Properties expectedProps) {
        PqConninfo conninfo = PqConninfo.from(opts);
        JdbcConnectionParameters params = JdbcConnectionParameters.from(conninfo);
        assertEquals(expectedUrl, params.getUrl());
        assertEquals(expectedInfo, params.getInfo());
        assertEquals(expectedUrlWithQueryString, params.getUrlWithQueryString());
        assertEquals(expectedProps, params.toProperties());
    }

}
