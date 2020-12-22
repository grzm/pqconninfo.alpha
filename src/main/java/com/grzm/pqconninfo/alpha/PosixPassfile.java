package com.grzm.pqconninfo.alpha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * libpq checks the permissions of the passfile on Unix systems. Java provides
 * options to check Posix file permissions, so we'll use those to check
 * passfile permissions on Unix (or at least non-Windows) systems.
 */
public final class PosixPassfile {

    /**
     * Private utility class constructor.
     */
    private PosixPassfile() { }

    /**
     * Set of valid permissions.
     */
    static final HashSet<PosixFilePermission> VALID_PGPASS_PERMISSIONS =
            new HashSet<>(Arrays.asList(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE));

    /**
     * Tests the given set of file permissions with the valid file permissions
     * for a passfile.
     *
     * @param perms The given permissions
     * @return true if the given permissions set is valid and false otherwise
     */
    static boolean hasValidPermissions(final Set<PosixFilePermission> perms) {
        HashSet<PosixFilePermission> p = new HashSet<>(perms);
        p.removeAll(VALID_PGPASS_PERMISSIONS);
        return perms.contains(PosixFilePermission.OWNER_READ)
                && p.isEmpty();
    }

    /*
      libpq trusts that pgpass has valid permissions because the directory
      it's in is protected.
      Should we do that, too?
      Actually, that's likely true only if we're looking for the default
      pgpass.conf, not one that's set via PGPASSFILE.
      TODO figure out if this is a potential security hole for Windows
     */

    /**
     * Tests whether the given file has valid passfile permissions.
     *
     * @param file The file to test
     * @return true if the file has valid permissions and false otherwise
     */
    static boolean hasValidPermissions(final File file) {
        final Logger logger = LoggerFactory.getLogger(PosixPassfile.class);
        try {
            return hasValidPermissions(Files.getPosixFilePermissions(
                    file.toPath(), LinkOption.values()));
        } catch (IOException e) {
            logger.warn("Failed to validate permissions on passfile {}. {} {}",
                    file, e.getClass().getName(), e.getMessage());
            return false;
        }
    }
}
