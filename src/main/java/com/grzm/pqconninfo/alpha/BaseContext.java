package com.grzm.pqconninfo.alpha;

import com.grzm.pqconninfo.alpha.util.Util;

import java.io.File;
import java.io.InputStream;

/**
 * Implementations of Context methods common to all implementations.
 */
public abstract class BaseContext implements Context {

    /**
     * The name of the per-user service file.
     */
    static final String USER_SERVICE_FILE = ".pg_service.conf";

    /**
     * The name of the sysconfdir service file.
     */
    static final String SYSCONFDIR_SERVICE_FILE = "pg_service.conf";

    /** {@inheritDoc} */
    @Override
    public InputStream getEnvServiceFileInputStream() {
        String serviceFile = this.getenv(EnvVars.PGSERVICEFILE);
        return Util.getFileInputStream(serviceFile);
    }

    /** {@inheritDoc} */
    @Override
    public String getUserServiceFilename() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public abstract InputStream getUserServiceFileInputStream();

    /** {@inheritDoc} */
    @Override
    public String getEnvSysconfdirServiceFilename() {
        String sysconfdir = this.getenv(EnvVars.PGSYSCONFDIR);
        if (sysconfdir == null) {
            return null;
        }
        return new File(sysconfdir, SYSCONFDIR_SERVICE_FILE).toString();
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getEnvSysconfdirServiceFileInputStream() {
        String envSysconfdirServiceFilename
                = this.getEnvSysconfdirServiceFilename();
        return Util.getFileInputStream(envSysconfdirServiceFilename);
    }

    /** {@inheritDoc} */
    @Override
    public String getConfigSysconfdirServiceFilename() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getConfigSysconfdirServiceFileInputStream() {
        String configSysconfdirServiceFileName
                = this.getConfigSysconfdirServiceFilename();
        return Util.getFileInputStream(configSysconfdirServiceFileName);
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getSysconfdirServiceFileInputStream() {
        InputStream is;

        String envSysconfdir = this.getenv(EnvVars.PGSYSCONFDIR);

        if (envSysconfdir != null) {
            is = this.getEnvSysconfdirServiceFileInputStream(envSysconfdir);
        } else {
            is = this.getConfigSysconfdirServiceFileInputStream();
        }

        return is;
    }

    /**
     * Returns the input stream of the contents of the service file in the
     * sysconfdir specified by the PGSYSCONFDIR environment variable.
     *
     * Returns null if the PGSYSCONFDIR environment variable is not set,
     * or the service file doesn't exist, or if there's an error creating
     * the input stream.
     *
     * @param envSysconfdir the value of the PGSYSCONFDIR environment variable
     * @return the input stream of the service file contents, or null
     */
    InputStream getEnvSysconfdirServiceFileInputStream(
            final String envSysconfdir) {
        String sysconfdir = this.getenv(EnvVars.PGSYSCONFDIR);

        if (sysconfdir == null) {
            return null;
        }

        File serviceFile = new File(sysconfdir,
                BaseContext.SYSCONFDIR_SERVICE_FILE);
        return Util.getFileInputStream(serviceFile);
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getEnvPassfileInputStream(final String envPassfile) {
        return Util.getFileInputStream(envPassfile);
    }

    /** {@inheritDoc} */
    @Override
    public String getSystemUser() {
        return System.getProperty("user.name");
    }
}
