package com.grzm.pqconninfo.alpha;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PosixPassfileTest {

    static boolean HAS_VALID_PERMS = true;
    static boolean HAS_INVALID_PERMS = false;

    static Stream<Arguments> passfileProvider() {
        return Stream.of(
                arguments("pgpass-world-readable", "r--r--r--", HAS_INVALID_PERMS),
                arguments("pgpass-read-only", "r--------", HAS_VALID_PERMS),
                arguments("pgpass-read-write", "rw-------", HAS_VALID_PERMS)
        );
    }

    @TempDir
    static Path tempDir;

    @ParameterizedTest
    @MethodSource("passfileProvider")
    void passfilePermissions(String passfileName, String permissions, boolean hasValidPermissions) {

        try {
            final Path passfilePath = tempDir.resolve(passfileName);
            final byte[] contents = "host:port:database:user:pass".getBytes(StandardCharsets.UTF_8);
            Files.write(passfilePath, contents);
            Files.setPosixFilePermissions(passfilePath, PosixFilePermissions.fromString(permissions));
            File passfile = passfilePath.toFile();
            assertEquals(hasValidPermissions, PosixPassfile.hasValidPermissions(passfile), permissions);
        } catch (IOException|IllegalArgumentException e) {
            fail(e.getMessage() + ": " + permissions);
        }
    }
}
