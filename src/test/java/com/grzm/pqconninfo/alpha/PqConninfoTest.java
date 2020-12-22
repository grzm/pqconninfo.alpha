package com.grzm.pqconninfo.alpha;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PqConninfoTest {

    static final String envServiceFileContents = "[some-service]\n"
            + "dbname=env-service-file-dbname\n"
            + "port=env-service-file-port\n";

    static final String userServiceFileContents = "[some-service]\n"
            + "dbname=user-service-file-dbname\n"
            + "port=user-service-file-port\n";

    static final String envSysconfdirServiceFileContents = "[some-service]\n"
            + "dbname=env-sysconfdir-service-file-dbname\n"
            + "port=env-sysconfdir-service-file-port\n"
            + "host=env-sysconfdir-service-file-host\n";

    static final String configSysconfdirServiceFileContents = "[some-service]\n"
            + "dbname=config-sysconfdir-service-file-dbname\n"
            + "port=config-sysconfdir-service-file-port\n"
            + "host=config-sysconfdir-service-file-host\n"
            + "user=config-sysconfdir-service-file-user\n";

    static Stream<Arguments> contextProvider() {
        return Stream.of(
                arguments(
                        new BasicTestContext(
                                new HashMap<String, String>() {{
                                    put("PGAPPNAME", "env-application_name");
                                    put("PGAUTHTYPE", "env-authtype");
                                    put("PGCHANNELBINDING", "env-channel_binding");
                                    put("PGCLIENTENCODING", "env-client_encoding");
                                    put("PGCONNECT_TIMEOUT", "env-connect_timeout");
                                    put("PGDATABASE", "env-dbname");
                                    put("PGGSSENCMODE", "env-gssencmode");
                                    put("PGGSSLIB", "env-gsslib");
                                    put("PGHOST", "env-host");
                                    put("PGHOSTADDR", "env-hostaddr");
                                    put("PGKRBSRVNAME", "env-krbsrvname");
                                    put("PGOPTIONS", "env-options");
                                    put("PGPASSFILE", "env-passfile");
                                    put("PGPASSWORD", "env-password");
                                    put("PGPORT", "env-port");
                                    put("PGREQUIREPEER", "env-requirepeer");
                                    put("PGSERVICE", "env-service");
                                    put("PGSSLCERT", "env-sslcert");
                                    put("PGSSLCOMPRESSION", "env-sslcompression");
                                    put("PGSSLCRL", "env-sslcrl");
                                    put("PGSSLKEY", "env-sslkey");
                                    put("PGSSLMODE", "env-sslmode");
                                    put("PGSSLROOTCERT", "env-sslrootcert");
                                    put("PGTARGETSESSIONATTRS", "env-target_session_attrs");
                                    put("PGTTY", "env-tty");
                                    put("PGUSER", "env-user");
                                }}
                        ),
                        new Properties(),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.APPLICATION_NAME, "env-application_name");
                            put(PqConninfoOption.AUTHTYPE, "env-authtype");
                            put(PqConninfoOption.CHANNEL_BINDING, "env-channel_binding");
                            put(PqConninfoOption.CLIENT_ENCODING, "env-client_encoding");
                            put(PqConninfoOption.CONNECT_TIMEOUT, "env-connect_timeout");
                            put(PqConninfoOption.DBNAME, "env-dbname");
                            put(PqConninfoOption.GSSENCMODE, "env-gssencmode");
                            put(PqConninfoOption.GSSLIB, "env-gsslib");
                            put(PqConninfoOption.HOST, "env-host");
                            put(PqConninfoOption.HOSTADDR, "env-hostaddr");
                            put(PqConninfoOption.KRBSRVNAME, "env-krbsrvname");
                            put(PqConninfoOption.OPTIONS, "env-options");
                            put(PqConninfoOption.PASSFILE, "env-passfile");
                            put(PqConninfoOption.PASSWORD, "env-password");
                            put(PqConninfoOption.PORT, "env-port");
                            put(PqConninfoOption.REQUIREPEER, "env-requirepeer");
                            put(PqConninfoOption.SERVICE, "env-service");
                            put(PqConninfoOption.SSLCERT, "env-sslcert");
                            put(PqConninfoOption.SSLCOMPRESSION, "env-sslcompression");
                            put(PqConninfoOption.SSLCRL, "env-sslcrl");
                            put(PqConninfoOption.SSLKEY, "env-sslkey");
                            put(PqConninfoOption.SSLMODE, "env-sslmode");
                            put(PqConninfoOption.SSLROOTCERT, "env-sslrootcert");
                            put(PqConninfoOption.TARGET_SESSION_ATTRS, "env-target_session_attrs");
                            put(PqConninfoOption.TTY, "env-tty");
                            put(PqConninfoOption.USER, "env-user");
                        }}
                ),
                arguments(
                        new BasicTestContext(),
                        new Properties() {{
                            setProperty("host", "props-host");
                            setProperty("port", "props-port");
                            setProperty("dbname", "props-dbname");
                            setProperty("user", "props-user");
                        }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "props-host");
                            put(PqConninfoOption.PORT, "props-port");
                            put(PqConninfoOption.DBNAME, "props-dbname");
                            put(PqConninfoOption.USER, "props-user");
                        }}
                ),
                arguments(
                        new BasicTestContext(new HashMap<String, String>() {{
                            put("PGSERVICE", "some-service");
                            put("PGSERVICEFILE", "value doesn't matter for test");
                        }}) {{
                            setEnvServiceFileContents(envServiceFileContents);
                            setUserServiceFileContents(userServiceFileContents);
                            setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                            setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                        }},
                        new Properties(),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.SERVICE, "some-service");
                            put(PqConninfoOption.PORT, "env-service-file-port");
                            put(PqConninfoOption.DBNAME, "env-service-file-dbname");
                        }}
                ),
                arguments(
                        new BasicTestContext(new HashMap<String, String>() {{
                            put("PGSERVICE", "some-service");
                        }}) {{
                            setEnvServiceFileContents(envServiceFileContents);
                            setUserServiceFileContents(userServiceFileContents);
                            setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                            setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                        }},
                        new Properties(),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.SERVICE, "some-service");
                            put(PqConninfoOption.PORT, "user-service-file-port");
                            put(PqConninfoOption.DBNAME, "user-service-file-dbname");
                        }}
                ),
                arguments(
                        new BasicTestContext(new HashMap<String, String>() {{
                            put("PGSERVICE", "some-service");
                            put("PGSYSCONFDIR", "value does not matter for test");
                        }}) {{
                            setEnvServiceFileContents(envServiceFileContents);
                            setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                            setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);

                        }},
                        new Properties(),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.SERVICE, "some-service");
                            put(PqConninfoOption.HOST, "env-sysconfdir-service-file-host");
                            put(PqConninfoOption.PORT, "env-sysconfdir-service-file-port");
                            put(PqConninfoOption.DBNAME, "env-sysconfdir-service-file-dbname");
                        }}
                ),
                arguments(
                        new BasicTestContext(new HashMap<String, String>() {{
                            put("PGSERVICE", "some-service");
                        }}) {{
                            setEnvServiceFileContents(envServiceFileContents);
                            setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                            setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                        }},
                        new Properties(),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.SERVICE, "some-service");
                            put(PqConninfoOption.HOST, "config-sysconfdir-service-file-host");
                            put(PqConninfoOption.PORT, "config-sysconfdir-service-file-port");
                            put(PqConninfoOption.DBNAME, "config-sysconfdir-service-file-dbname");
                            put(PqConninfoOption.USER, "config-sysconfdir-service-file-user");
                        }}
                ),
                arguments(
                        new BasicTestContext(new HashMap<String, String>() {{
                            put("PGPASSFILE", "a passfile by any other name");
                        }}) {{
                            setEnvPassfileContents("*:*:*:*:env-password");
                            setUserPassfileContents("*:*:*:*:user-password");
                        }},
                        new Properties() {{
                            setProperty("host", "props-host");
                            setProperty("port", "props-port");
                            setProperty("dbname", "props-dbname");
                            setProperty("user", "props-user");
                        }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "props-host");
                            put(PqConninfoOption.PORT, "props-port");
                            put(PqConninfoOption.DBNAME, "props-dbname");
                            put(PqConninfoOption.USER, "props-user");
                            put(PqConninfoOption.PASSWORD, "env-password");
                            put(PqConninfoOption.PASSFILE, "a passfile by any other name");
                        }}
                ),
                arguments(
                        new BasicTestContext() {{
                            setEnvPassfileContents("*:*:*:*:env-password");
                            setUserPassfileContents("*:*:*:*:user-password");
                        }},
                        new Properties() {{
                            setProperty("host", "props-host");
                            setProperty("port", "props-port");
                            setProperty("dbname", "props-dbname");
                            setProperty("user", "props-user");
                        }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "props-host");
                            put(PqConninfoOption.PORT, "props-port");
                            put(PqConninfoOption.DBNAME, "props-dbname");
                            put(PqConninfoOption.USER, "props-user");
                            put(PqConninfoOption.PASSWORD, "user-password");
                        }}
                ),
                arguments(new BasicTestContext() {{
                              setSystemUser("system-user");
                              setEnvPassfileContents("*:*:*:system-user:system-user-env-password\n"
                                      + "*:*:*:*:env-password");
                              setUserPassfileContents("*:*:*:system-user:system-user-user-password\n"
                                      + "*:*:*:*:user-password");
                          }},
                        new Properties() {{
                            setProperty("host", "props-host");
                            setProperty("port", "props-port");
                            setProperty("dbname", "props-dbname");
                        }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "props-host");
                            put(PqConninfoOption.PORT, "props-port");
                            put(PqConninfoOption.DBNAME, "props-dbname");
                            put(PqConninfoOption.USER, "system-user");
                            put(PqConninfoOption.PASSWORD, "system-user-user-password");
                        }}
                        ),
                arguments(
                        new BasicTestContext(new HashMap<String,String>() {{
                            put("PGPASSFILE", "passfile-name");
                        }}) {{
                            setSystemUser("system-user");
                            setEnvPassfileContents("*:*:*:system-user:system-user-env-password\n"
                                    + "*:*:*:*:env-password");
                            setUserPassfileContents("*:*:*:system-user:system-user-user-password\n"
                                    + "*:*:*:*:user-password");

                        }},
                        new Properties() {{
                            setProperty("host", "props-host");
                            setProperty("port", "props-port");
                            setProperty("dbname", "props-dbname");
                        }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "props-host");
                            put(PqConninfoOption.PORT, "props-port");
                            put(PqConninfoOption.DBNAME, "props-dbname");
                            put(PqConninfoOption.USER, "system-user");
                            put(PqConninfoOption.PASSWORD, "system-user-env-password");
                            put(PqConninfoOption.PASSFILE, "passfile-name");
                        }}
                )

        );
    }

    @ParameterizedTest
    @MethodSource("contextProvider")
    void pqConninfoFromContext(Context context,
                               Properties props,
                               EnumMap<PqConninfoOption, String> expectedOpts) {

        PqConninfo conninfo = PqConninfoReader.read(context, props);
        PqConninfo expected = PqConninfo.from(expectedOpts);
        assertEquals(expected, conninfo);
    }

    @Test
    void redactedPasswords() {
        EnumMap<PqConninfoOption, String> opts = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.USER, "alice");
            put(PqConninfoOption.SSLPASSWORD, "change-me");
            put(PqConninfoOption.PASSWORD, "super-secret");
        }};

        PqConninfo conninfo = PqConninfo.from(opts);
        assertEquals("PqConninfo{opts={PASSWORD=****, SSLPASSWORD=****, USER=alice}}",
                conninfo.toString());
        assertEquals("super-secret", conninfo.get(PqConninfoOption.PASSWORD));
        assertEquals("change-me", conninfo.get(PqConninfoOption.SSLPASSWORD));
        assertEquals("alice", conninfo.get(PqConninfoOption.USER));

        PqConninfo conninfo2 = PqConninfo.from(opts);
        assertEquals(conninfo, conninfo2);
        assertNotSame(conninfo, conninfo2);

    }
}