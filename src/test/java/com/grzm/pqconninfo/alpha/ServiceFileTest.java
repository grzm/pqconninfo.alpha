package com.grzm.pqconninfo.alpha;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ServiceFileTest {

    static Stream<Arguments> pgpassEntryProvider() {
        return Stream.of(
                arguments("my-password",
                        "some-host", "some-port", "some-database", "some-username",
                        "some-host:some-port:some-database:some-username:my-password"),
                arguments("my-password",
                        "some-host", "some-port", "some-database", "some-username",
                        "*:some-port:some-database:some-username:my-password"),
                arguments(null,
                        "some-host", "some-port", "some-database", "some-username",
                        "*:some-port:other-database:some-username:my-password"),
                arguments("my-pass:word",
                        "some-host", "some-port", "some-database", "some-username",
                        "*:some-port:some-database:some-username:my-pass\\:word"),
                arguments("my-pass\\:word",
                        "some-host", "some-port", "some-database", "some-username",
                        "*:some-port:some-database:some-username:my-pass\\\\\\:word")
        );
    }

    @ParameterizedTest
    @MethodSource("pgpassEntryProvider")
    void parsePgpassLine(String expected, String host, String port, String dbname, String user,
                         String entry) {
        assertEquals(expected, Passfile.parsePgpassLine(host, port, dbname, user, entry));
    }

    static Stream<Arguments> pgpassContentsProvider() {
        return Stream.of(
                arguments("my-password",
                        "some-host", "some-port", "some-database", "some-username",
                        "some-host:some-port:some-database:some-username:my-password")
        );
    }

    @ParameterizedTest
    @MethodSource("pgpassContentsProvider")
    void getPgpassPassword(String expected,
                           String host, String port, String dbname, String user,
                           String contents) {
        EnumMap<PqConninfoOption, String> conninfo = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.HOST, host);
            put(PqConninfoOption.PORT, port);
            put(PqConninfoOption.DBNAME, dbname);
            put(PqConninfoOption.USER, user);
        }};
        ByteArrayInputStream is = new ByteArrayInputStream(contents.getBytes());
        assertEquals(expected, Passfile.getPassword(conninfo, is));
    }

    static Stream<Arguments> hasValidPermissionsProvider() {
        return Stream.of(
                arguments(false, new HashSet<PosixFilePermission>()),
                arguments(true, new HashSet<>(Collections.singletonList(PosixFilePermission.OWNER_READ))),
                arguments(true, new HashSet<>(Arrays.asList(PosixFilePermission.OWNER_READ,
                        PosixFilePermission.OWNER_WRITE))),
                arguments(false, new HashSet<>(Arrays.asList(PosixFilePermission.OWNER_READ,
                        PosixFilePermission.OWNER_WRITE,
                        PosixFilePermission.OTHERS_READ))),
                arguments(false, new HashSet<>(Arrays.asList(PosixFilePermission.OWNER_READ,
                        PosixFilePermission.OTHERS_READ)))
        );
    }

    @ParameterizedTest
    @MethodSource("hasValidPermissionsProvider")
    void hasValidPermissions(boolean expected, Set<PosixFilePermission> permissions) {
        assertEquals(expected, PosixPassfile.hasValidPermissions(permissions));
    }

    @Test
    void enumEx() {
        EnumMap<PqConninfoOption, String> expected = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.USER, "my_user");
            put(PqConninfoOption.PORT, "6543");
        }};
        EnumMap<PqConninfoOption, String> actual = new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.PORT, "6543");
            put(PqConninfoOption.USER, "my_user");
        }};
        assertEquals(expected, actual);
    }

    static Stream<Arguments> serviceFileProvider() {
        return Stream.of(
                arguments(null,
                        "my-service",
                        ""),
                arguments(new EnumMap<PqConninfoOption, String>(PqConninfoOption.class),
                        "my-service",
                        "[my-service]"),
                arguments(new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                              put(PqConninfoOption.USER, "my_user");
                              put(PqConninfoOption.PORT, "6543");
                          }},
                        "my-service",
                        "[my-service]\n"
                                + "user=my_user\n"
                                + "port=6543\n"),
                arguments(new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                              put(PqConninfoOption.USER, "my_user");
                              put(PqConninfoOption.PORT, "6543");
                          }},
                        "my-service",
                        "[some-other-service]\n"
                                + "user=other_user\n"
                                + "[my-service]\n"
                                + "user=my_user\n"
                                + "port=6543\n"
                                + "[yet-another-service]\n"
                                + "user=another_user"),
                /*
                 TODO I don't know if this is accepted behavior, but
                 we could potentially set a keyword to an empty string.
                 */
                arguments(new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
                              put(PqConninfoOption.USER, "");
                              put(PqConninfoOption.PORT, "6543");
                          }},
                        "my-service",
                        "[my-service]\n"
                                + "user=\n"
                                + "port=6543\n")
        );
    }

    static Stream<Arguments> malformedServiceFileProvider() {
        return Stream.of(
                arguments(null,
                        "my-service",
                        "[my-service]\n"
                                + "user=my_user\n"
                                + "bad_port=6543\n")
        );
    }

    @ParameterizedTest
    @MethodSource("serviceFileProvider")
    void parseServiceFile(EnumMap<PqConninfoOption, String> expected,
                          String service,
                          String contents) {
        ByteArrayInputStream is = new ByteArrayInputStream(contents.getBytes());

        try {
            assertEquals(expected, ServiceFile.getServiceConninfo(service, is),
                    String.format("contents:\n%s\n", contents));
        } catch (MalformedServiceFileException | IOException e) {
            fail(String.format("unexpected exception. Contents:\n%s\n", contents), e);
        }
    }

    @ParameterizedTest
    @MethodSource("malformedServiceFileProvider")
    void parseMalformedServiceFile(EnumMap<PqConninfoOption, String> expected,
                          String service,
                          String contents) {
        ByteArrayInputStream is = new ByteArrayInputStream(contents.getBytes());

        assertThrows(MalformedServiceFileException.class,
                () ->ServiceFile.getServiceConninfo(service, is),
                String.format("contents:\n%s\n", contents));
    }


}

