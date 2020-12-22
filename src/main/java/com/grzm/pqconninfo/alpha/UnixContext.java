package com.grzm.pqconninfo.alpha;

import com.grzm.pqconninfo.alpha.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Unix (or rather, non-Windows) specific Context implementation.
 */
public class UnixContext extends BaseContext {

    /**
     * The name of the per-user passfile on Unix systems.
     */
    static final String USER_PASSFILE = ".pgpass";

    /**
     * Reference to the EnvVars provider for this context instance.
     */
    private final EnvVars envVars;

    /**
     * Cached value of userHome.
     */
    private final String userHome;

    /**
     * Per-user service file.
     */
    private final File userServiceFile;

    /**
     * Constructor for a Unix system Context, given an EnvVars instance.
     *
     * @param envReader provider of environment variables
     */
    public UnixContext(final EnvVars envReader) {
        this.envVars = envReader;
        this.userHome = System.getProperty("user.home");
        this.userServiceFile = new File(this.userHome, USER_SERVICE_FILE);
    }

    /** {@inheritDoc} */
    @Override
    public String getenv(final String var) {
        return this.envVars.getenv(var);
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getUserServiceFileInputStream() {
        return Util.getFileInputStream(this.userServiceFile);
    }

    /** {@inheritDoc} */
    @Override
    public String getUserServiceFilename() {
        return this.userServiceFile.toString();
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getConfigSysconfdirServiceFileInputStream() {
        String sysconfdir = getConfigSysconfdir();

        if (Util.isNullOrEmpty(sysconfdir)) {
            return null;
        }

        File serviceFile = new File(sysconfdir, SYSCONFDIR_SERVICE_FILE);
        return Util.getFileInputStream(serviceFile);
    }

    /** {@inheritDoc} */
    @Override
    public String getConfigSysconfdirServiceFilename() {
        String sysconfdir = getConfigSysconfdir();

        if (Util.isNullOrEmpty(sysconfdir)) {
            return null;
        }

        return new File(sysconfdir, SYSCONFDIR_SERVICE_FILE).toString();
    }


    /** {@inheritDoc} */
    @Override
    public InputStream getUserPassfileInputStream() {
        final Logger logger = LoggerFactory.getLogger(UnixContext.class);

        File passfile = new File(this.userHome, USER_PASSFILE);

        if (!passfile.exists()) {
            return null;
        }

        if (PosixPassfile.hasValidPermissions(passfile)) {
            return Util.getFileInputStream(passfile);
        }

        /*
         If the file has weak permissions, libpq warns and skips.
         libpq_gettext("WARNING: password file \"%s\" has group or world access;
                        permissions should be u=rw (0600) or less\n"),
        */
        logger.warn("Password file \"{}\" has group or world access;"
                + " permissions should be u=rw (0600) or less", passfile);
        return null;
    }

    /**
     * Returns the value of the sysconfdir provided by the pg_config tool.
     * This is a best-effort attempt to replicate the behavior of libpq,
     * where there's value of sysconfdir compiled in.
     *
     * Returns null if there's an error calling pg_config.
     *
     * @return the sysconfdir path, or null
     */
    public static String getConfigSysconfdir() {
        // TODO Determine whether we need to do this differently on Windows
        try {
            Process p = new ProcessBuilder("pg_config", "--sysconfdir").start();
            p.waitFor();
            if (p.exitValue() == 0) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream()))) {
                    StringBuilder builder = new StringBuilder();
                    String line = reader.readLine();
                    if (line != null) {
                        builder.append(line);
                    }
                    return builder.toString();
                }
            }
            return null;
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

}
