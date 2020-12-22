package com.grzm.pqconninfo.alpha.impl;

import com.grzm.pqconninfo.alpha.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Stream;

import static com.grzm.pqconninfo.alpha.impl.EnumMapConninfoReader.putAllIfAbsent;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EnumMapConninfoReaderTest {

    static final EnumMap<PqConninfoOption, String> emptyOpts = new EnumMap<>(PqConninfoOption.class);

    static Stream<Arguments> putAllIfAbsentProvider() {
        final EnumMap<PqConninfoOption, String> a
                = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.USER, "a-user");
            put(PqConninfoOption.PASSWORD, "a-password");
        }};

        final EnumMap<PqConninfoOption, String> b
                = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.USER, "b-user");
            put(PqConninfoOption.DBNAME, "b-dbname");
        }};

        return Stream.of(
                arguments(emptyOpts, emptyOpts, emptyOpts),
                arguments(a, emptyOpts, a),
                arguments(a, b, new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                    put(PqConninfoOption.USER, "a-user");
                    put(PqConninfoOption.PASSWORD, "a-password");
                    put(PqConninfoOption.DBNAME, "b-dbname");
                }})
        );
    }

    @ParameterizedTest
    @MethodSource("putAllIfAbsentProvider")
    void putAllIfAbsentTest(final EnumMap<PqConninfoOption, String> a,
                            final EnumMap<PqConninfoOption, String> b,
                            final EnumMap<PqConninfoOption, String> expected) {
        EnumMap<PqConninfoOption, String> aCopy = a.clone();
        putAllIfAbsent(aCopy, b);
        assertEquals(expected, aCopy);
    }

    // initial props

    static Stream<Arguments> propsProvider() {
        return Stream.of(
                arguments(new Properties() {{
                              setProperty("user", "prop-user");
                          }}, new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                              put(PqConninfoOption.USER, "prop-user");
                          }}
                ),
                arguments(new Properties() {{
                              setProperty("application_name", "prop-app-name");
                              setProperty("user", "prop-user");
                              setProperty("no-such-prop", "prop-no-such-prop");

                          }}, new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                              put(PqConninfoOption.APPLICATION_NAME, "prop-app-name");
                              put(PqConninfoOption.USER, "prop-user");
                          }}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("propsProvider")
    void optsFrom(final Properties props, final EnumMap<PqConninfoOption, String> expected) {
        EnumMap<PqConninfoOption, String> opts = EnumMapConninfoReader.optsFrom(props);
        assertEquals(expected, opts);
    }

    static final MapEnvVars emptyEnv = new MapEnvVars(new HashMap<>());

    // service file
    static Stream<Arguments> serviceFileInfoProvider() {
        final String envServiceFileContents = String.join("\n", Arrays.asList(
                "[some-service]",
                "host=env-service-file/some-service/host",
                "port=env-service-file/some-service/port",
                "dbname=env-service-file/some-service/dbname",
                "user=env-service-file/some-service/user",
                "[env-service]",
                "host=env-service-file/env-service/host",
                "port=env-service-file/env-service/port",
                "dbname=env-service-file/env-service/dbname",
                "user=env-service-file/env-service/user",
                "[env-service-file-service]",
                "host=env-service-file/env-service-file-service/host",
                "port=env-service-file/env-service-file-service/port",
                "dbname=env-service-file/env-service-file-service/dbname",
                "user=env-service-file/env-service-file-service/user"


        ));

        final EnumMap<PqConninfoOption, String> envServiceFileSomeServiceOpts =
                new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                    put(PqConninfoOption.SERVICE, "some-service");
                    put(PqConninfoOption.HOST, "env-service-file/some-service/host");
                    put(PqConninfoOption.PORT, "env-service-file/some-service/port");
                    put(PqConninfoOption.DBNAME, "env-service-file/some-service/dbname");
                    put(PqConninfoOption.USER, "env-service-file/some-service/user");
                }};


        final EnumMap<PqConninfoOption, String> envServiceFileEnvServiceOpts =
                new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                    put(PqConninfoOption.SERVICE, "env-service");
                    put(PqConninfoOption.HOST, "env-service-file/env-service/host");
                    put(PqConninfoOption.PORT, "env-service-file/env-service/port");
                    put(PqConninfoOption.DBNAME, "env-service-file/env-service/dbname");
                    put(PqConninfoOption.USER, "env-service-file/env-service/user");
                }};

        final String userServiceFileContents = String.join("\n", Arrays.asList(
                "[some-service]",
                "host=user-service-file/some-service/host",
                "port=user-service-file/some-service/port",
                "dbname=user-service-file/some-service/dbname",
                "user=user-service-file/some-service/user",
                "[env-service]",
                "host=user-service-file/env-service/host",
                "port=user-service-file/env-service/port",
                "dbname=user-service-file/env-service/dbname",
                "user=user-service-file/env-service/user",
                "[user-service]",
                "host=user-service-file/user-service/host",
                "port=user-service-file/user-service/port",
                "dbname=user-service-file/user-service/dbname",
                "user=user-service-file/user-service/user"
        ));

        final EnumMap<PqConninfoOption, String> userServiceFileSomeServiceOpts =
                new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                    put(PqConninfoOption.SERVICE, "some-service");
                    put(PqConninfoOption.HOST, "user-service-file/some-service/host");
                    put(PqConninfoOption.PORT, "user-service-file/some-service/port");
                    put(PqConninfoOption.DBNAME, "user-service-file/some-service/dbname");
                    put(PqConninfoOption.USER, "user-service-file/some-service/user");
                }};

        final EnumMap<PqConninfoOption, String> userServiceFileEnvServiceOpts =
                new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                    put(PqConninfoOption.SERVICE, "env-service");
                    put(PqConninfoOption.HOST, "user-service-file/env-service/host");
                    put(PqConninfoOption.PORT, "user-service-file/env-service/port");
                    put(PqConninfoOption.DBNAME, "user-service-file/env-service/dbname");
                    put(PqConninfoOption.USER, "user-service-file/env-service/user");
                }};

        final String envSysconfdirServiceFileContents = String.join("\n", Arrays.asList(
                "[some-service]",
                "host=env-sysconfdir-service-file/some-service/host",
                "port=env-sysconfdir-service-file/some-service/port",
                "dbname=env-sysconfdir-service-file/some-service/dbname",
                "user=env-sysconfdir-service-file/some-service/user",
                "[env-service]",
                "host=env-sysconfdir-service-file/env-service/host",
                "port=env-sysconfdir-service-file/env-service/port",
                "dbname=env-sysconfdir-service-file/env-service/dbname",
                "user=env-sysconfdir-service-file/env-service/user",
                "[env-sysconfdir-service]",
                "host=env-sysconfdir-service-file/env-sysconfdir-service/env-sysconfdir-service/host",
                "port=env-sysconfdir-service-file/env-sysconfdir-service/port",
                "dbname=env-sysconfdir-service-file/env-sysconfdir-service/dbname",
                "user=env-sysconfdir-service-file/env-sysconfdir-service/user"
        ));


        final EnumMap<PqConninfoOption, String> envSysconfdirServiceFileSomeServiceOpts =
                new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                    put(PqConninfoOption.SERVICE, "some-service");
                    put(PqConninfoOption.HOST, "env-sysconfdir-service-file/some-service/host");
                    put(PqConninfoOption.PORT, "env-sysconfdir-service-file/some-service/port");
                    put(PqConninfoOption.DBNAME, "env-sysconfdir-service-file/some-service/dbname");
                    put(PqConninfoOption.USER, "env-sysconfdir-service-file/some-service/user");
                }};

        final EnumMap<PqConninfoOption, String> envSysconfdirServiceFileEnvServiceOpts =
                new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                    put(PqConninfoOption.SERVICE, "env-service");
                    put(PqConninfoOption.HOST, "env-sysconfdir-service-file/env-service/host");
                    put(PqConninfoOption.PORT, "env-sysconfdir-service-file/env-service/port");
                    put(PqConninfoOption.DBNAME, "env-sysconfdir-service-file/env-service/dbname");
                    put(PqConninfoOption.USER, "env-sysconfdir-service-file/env-service/user");
                }};

        final String configSysconfdirServiceFileContents = String.join("\n", Arrays.asList(
                "[some-service]",
                "host=config-sysconfdir-service-file/some-service/host",
                "port=config-sysconfdir-service-file/some-service/port",
                "dbname=config-sysconfdir-service-file/some-service/dbname",
                "user=config-sysconfdir-service-file/some-service/user",
                "[env-service]",
                "host=config-sysconfdir-service-file/env-service/host",
                "port=config-sysconfdir-service-file/env-service/port",
                "dbname=config-sysconfdir-service-file/env-service/dbname",
                "user=config-sysconfdir-service-file/env-service/user",
                "[config-sysconfdir-service]",
                "host=config-sysconfdir-service-file/config-sysconfdir-service/host",
                "port=config-sysconfdir-service-file/config-sysconfdir-service/port",
                "dbname=config-sysconfdir-service-file/config-sysconfdir-service/dbname",
                "user=config-sysconfdir-service-file/config-sysconfdir-service/user"
        ));

        final EnumMap<PqConninfoOption, String> configSysconfdirServiceFileSomeServiceOpts =
                new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                    put(PqConninfoOption.SERVICE, "some-service");
                    put(PqConninfoOption.HOST, "config-sysconfdir-service-file/some-service/host");
                    put(PqConninfoOption.PORT, "config-sysconfdir-service-file/some-service/port");
                    put(PqConninfoOption.DBNAME, "config-sysconfdir-service-file/some-service/dbname");
                    put(PqConninfoOption.USER, "config-sysconfdir-service-file/some-service/user");
                }};

        final EnumMap<PqConninfoOption, String> configSysconfdirServiceFileEnvServiceOpts =
                new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                    put(PqConninfoOption.SERVICE, "env-service");
                    put(PqConninfoOption.HOST, "config-sysconfdir-service-file/env-service/host");
                    put(PqConninfoOption.PORT, "config-sysconfdir-service-file/env-service/port");
                    put(PqConninfoOption.DBNAME, "config-sysconfdir-service-file/env-service/dbname");
                    put(PqConninfoOption.USER, "config-sysconfdir-service-file/env-service/user");
                }};

        final EnumMap<PqConninfoOption, String> someServiceOpts = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.SERVICE, "some-service");
        }};

        final EnumMap<PqConninfoOption, String> envServiceOpts = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.SERVICE, "env-service");
        }};

        final EnumMap<PqConninfoOption, String> currentOpts = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.HOST, "opts-host");
            put(PqConninfoOption.PORT, "opts-port");
        }};

        return Stream.of(
                arguments("empty context, empty opts (degenerate case)",
                        new BasicTestContext(emptyEnv),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class),
                        null
                ),

                arguments("empty context, non-empty opts (degenerate case)",
                        new BasicTestContext(emptyEnv),
                        currentOpts,
                        null),

                arguments("PGSERVICEFILE set, PGSERVICEFILE exists, PGSERVICE set, empty opts",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                              put(EnvVars.PGSERVICEFILE, "env-service-file");
                              put(EnvVars.PGSYSCONFDIR, "env-sysconfdir-file");
                              put(PqConninfoOption.SERVICE.environmentVariable, "env-service");
                          }})) {{
                              setEnvServiceFileContents(envServiceFileContents);
                              setUserServiceFileContents(userServiceFileContents);
                              setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                              setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                          }},
                        emptyOpts,
                        envServiceFileEnvServiceOpts
                ),

                arguments("PGSERVICEFILE set, PGSERVICEFILE exists, opts has service",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                              put(EnvVars.PGSERVICEFILE, "env-service-file");
                              put(PqConninfoOption.SERVICE.environmentVariable, "env-service");
                          }})) {{
                              setEnvServiceFileContents(envServiceFileContents);
                          }},
                        someServiceOpts,
                        envServiceFileSomeServiceOpts
                ),


                arguments("PGSERVICEFILE set, PGSERVICEFILE exists, non-empty opts, opts shadows service file",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                              put(EnvVars.PGSERVICEFILE, "env-service-file");
                              put(PqConninfoOption.SERVICE.environmentVariable, "env-service");
                          }})) {{
                              setEnvServiceFileContents(envServiceFileContents);
                          }},
                        currentOpts,
                        envServiceFileEnvServiceOpts
                ),

                arguments("PGSERVICEFILE set, PGSERVICEFILE doesn't exist, PGSERVICE set, empty opts",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                              put(EnvVars.PGSERVICEFILE, "env-service-file");
                              put(EnvVars.PGSYSCONFDIR, "env-sysconfdir-file");
                              put(PqConninfoOption.SERVICE.environmentVariable, "env-service");
                          }})) {{
                              setUserServiceFileContents(userServiceFileContents);
                              setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                              setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                          }},
                        emptyOpts,
                        envServiceOpts
                ),

                arguments("user service file exists",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                              put(PqConninfoOption.SERVICE.environmentVariable, "env-service");
                          }})) {{
                              setUserServiceFileContents(userServiceFileContents);
                          }},
                        currentOpts,
                        userServiceFileEnvServiceOpts
                ),

                arguments("env PGSYSCONFDIR not set, PGSYSCONFDIR/pg_service.conf file exists,",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                              put(PqConninfoOption.SERVICE.environmentVariable, "env-service");
                          }})) {{
                              setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                          }},
                        currentOpts,
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.SERVICE, "env-service");
                        }}
                ),

                arguments("env PGSYSCONFDIR set, PGSYSCONFDIR/pg_service.conf file exists,",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                              put(PqConninfoOption.SERVICE.environmentVariable, "env-service");
                              put(EnvVars.PGSYSCONFDIR, "env-sysconfdir");
                          }})) {{
                              setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                          }},
                        currentOpts,
                        envSysconfdirServiceFileEnvServiceOpts
                ),

                arguments("configSysconfdir/pg_service.conf exists",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                              put(PqConninfoOption.SERVICE.environmentVariable, "env-service");
                          }})) {{
                              setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                          }},
                        currentOpts,
                        configSysconfdirServiceFileEnvServiceOpts
                ),

                arguments("PGSERVICE not set, everything else populated",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                              put(EnvVars.PGSERVICEFILE, "env-service-file");
                              put(EnvVars.PGSYSCONFDIR, "env-sysconfdir-file");
                          }})) {{
                              setEnvServiceFileContents(envServiceFileContents);
                              setUserServiceFileContents(userServiceFileContents);
                              setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                              setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                          }},
                        currentOpts,
                        null
                ),

                arguments("PGSERVICE not set, everything else populated",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                            put(EnvVars.PGSERVICEFILE, "env-service-file");
                            put(EnvVars.PGSYSCONFDIR, "env-sysconfdir-file");
                        }})) {{
                            setEnvServiceFileContents(envServiceFileContents);
                            setUserServiceFileContents(userServiceFileContents);
                            setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                            setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                        }},
                        someServiceOpts,
                        envServiceFileSomeServiceOpts
                ),

                arguments("PGSERVICE not set, PGSERVICEFILE not set",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                            put(EnvVars.PGSYSCONFDIR, "env-sysconfdir-file");
                        }})) {{
                            setEnvServiceFileContents(envServiceFileContents);
                            setUserServiceFileContents(userServiceFileContents);
                            setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                            setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                        }},
                        someServiceOpts,
                        userServiceFileSomeServiceOpts
                ),

                arguments("PGSERVICE not set, PGSERVICEFILE not set, no user service file",
                        new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                            put(EnvVars.PGSYSCONFDIR, "env-sysconfdir-file");
                        }})) {{
                            setEnvServiceFileContents(envServiceFileContents);
                            setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                            setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                        }},
                        someServiceOpts,
                        envSysconfdirServiceFileSomeServiceOpts
                ),

                arguments("PGSERVICE not set, PGSERVICEFILE not set, no user service file, PGSYSCONFDIR not set",
                        new BasicTestContext(emptyEnv) {{
                            setEnvServiceFileContents(envServiceFileContents);
                            setEnvSysconfdirServiceFileContents(envSysconfdirServiceFileContents);
                            setConfigSysconfdirServiceFileContents(configSysconfdirServiceFileContents);
                        }},
                        someServiceOpts,
                        configSysconfdirServiceFileSomeServiceOpts
                )
        );

    }

    @ParameterizedTest
    @MethodSource("serviceFileInfoProvider")
    void serviceFileInfo(final String description,
                         final Context context,
                         final EnumMap<PqConninfoOption, String> sourceOpts,
                         final EnumMap<PqConninfoOption, String> expected) {
        EnumMap<PqConninfoOption, String> opts = EnumMapConninfoReader.serviceOpts(context, sourceOpts);
        assertEquals(expected, opts, description);
    }

    // environment variables

    static Stream<Arguments> environmentVariablesProvider() {
        return Stream.of(
                arguments(new BasicTestContext(emptyEnv),
                        emptyOpts,
                        emptyOpts
                ),
                arguments(new BasicTestContext(emptyEnv),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "opts-host");
                            put(PqConninfoOption.PORT, "opts-port");
                        }},
                        emptyOpts
                ),
                arguments(new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                            put(PqConninfoOption.HOST.environmentVariable, "env-host");
                            put(PqConninfoOption.PORT.environmentVariable, "env-port");
                        }})),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "opts-host");
                            put(PqConninfoOption.PORT, "opts-port");
                        }},
                        emptyOpts
                ),
                arguments(new BasicTestContext(new MapEnvVars(new HashMap<String, String>() {{
                            put(PqConninfoOption.HOST.environmentVariable, "env-host");
                            put(PqConninfoOption.PORT.environmentVariable, "env-port");
                            put(PqConninfoOption.DBNAME.environmentVariable, "env-dbname");
                            put(PqConninfoOption.USER.environmentVariable, "env-user");
                        }})),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "opts-host");
                            put(PqConninfoOption.PORT, "opts-port");
                        }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.DBNAME, "env-dbname");
                            put(PqConninfoOption.USER, "env-user");
                        }}
                )
        );

    }

    @ParameterizedTest
    @MethodSource("environmentVariablesProvider")
    void environmentVariables(final Context context,
                              final EnumMap<PqConninfoOption, String> sourceOpts,
                              final EnumMap<PqConninfoOption, String> expected) {
        EnumMap<PqConninfoOption, String> opts = EnumMapConninfoReader.environmentVariables(context, sourceOpts);
        assertEquals(expected, opts);
    }

    // system user

    static Stream<Arguments> systemUserProvider() {
        return Stream.of(
                arguments(new BasicTestContext(emptyEnv),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class),
                        null
                ),
                arguments(new BasicTestContext(emptyEnv),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.USER, "opts-user");
                        }},
                        null
                ),
                arguments(new BasicTestContext(emptyEnv) {{
                              setSystemUser("system-user");
                          }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.USER, "opts-user");
                        }},
                        null
                ),
                arguments(new BasicTestContext(emptyEnv) {{
                              setSystemUser("system-user");
                          }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.USER, "system-user");
                        }}
                )
        );

    }

    @ParameterizedTest
    @MethodSource("systemUserProvider")
    void systemUser(final Context context,
                    final EnumMap<PqConninfoOption, String> sourceOpts,
                    final EnumMap<PqConninfoOption, String> expected) {
        EnumMap<PqConninfoOption, String> opts = EnumMapConninfoReader.systemUser(context, sourceOpts);
        assertEquals(expected, opts);
    }

    // passfile

    static Stream<Arguments> passfileInfoProvider() {
        return Stream.of(
                arguments(new BasicTestContext(emptyEnv),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class),
                        null
                ),
                arguments(new BasicTestContext(emptyEnv),
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.PASSWORD, "opts-password");
                        }},
                        null
                ),
                arguments(new BasicTestContext(emptyEnv) {{
                              setUserPassfileContents("*:*:*:*:user-passfile-password");
                          }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.PASSWORD, "opts-password");
                        }},
                        null
                ),
                arguments(new BasicTestContext(emptyEnv) {{
                              setUserPassfileContents("*:*:*:*:user-passfile-password");
                          }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.HOST, "some-host");
                            put(PqConninfoOption.PORT, "some-port");
                            put(PqConninfoOption.DBNAME, "some-dbname");
                            put(PqConninfoOption.USER, "some-user");
                        }},
                        new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                            put(PqConninfoOption.PASSWORD, "user-passfile-password");
                        }}
                )
        );

    }

    @ParameterizedTest
    @MethodSource("passfileInfoProvider")
    void passfileInfo(final Context context,
                      final EnumMap<PqConninfoOption, String> sourceOpts,
                      final EnumMap<PqConninfoOption, String> expected) {
        EnumMap<PqConninfoOption, String> opts = EnumMapConninfoReader.passfileInfo(context, sourceOpts);
        assertEquals(expected, opts);
    }

}