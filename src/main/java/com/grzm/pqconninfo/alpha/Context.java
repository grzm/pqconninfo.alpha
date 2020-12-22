package com.grzm.pqconninfo.alpha;

import java.io.InputStream;

/**
 * Interface representing access to the underlying system context.
 * This allows us to test the logic of conninfo collection based on the
 * context we give it, independent of the actual system.
 */
public interface Context {
    /**
     * Fetches the value of the given environment variable from the context.
     *
     * @param var the environment variable name
     * @return the value of the environment variable
     */
    String getenv(String var);

    /**
     * Returns an input stream of the contents of the service file specified
     * by the PGSERVICEFILE environment variable.
     *
     * Returns null if the environment variable isn't set, or the file doesn't
     * exit, or if there's an error creating the input stream.
     *
     * @return The input stream of the service file contents, or null
     */
    InputStream getEnvServiceFileInputStream();

    /**
     * Returns the name of the per-user service file.
     *
     * @return The name of the service file.
     */
    String getUserServiceFilename();

    /**
     * Returns an input stream of the contents of the per-user service file.
     *
     * Returns null if the file doesn't exist or if there's an error creating
     * the input stream.
     *
     * @return The input stream of the service file contents, or null
     */
    InputStream getUserServiceFileInputStream();

    /**
     * Returns the name of the service file in the sysconfir specified by
     * the PGSYSCONFDIR environment variable.
     *
     * @return The name of the service file
     */
    String getEnvSysconfdirServiceFilename();

    /**
     * Returns the input stream of the contents of the service file in the
     * directory specified by the PGSYSCONFDIR environment variable.
     *
     * If the environment variable is not set, or if the file doesn't exist or
     * the input stream can't be created, it returns null.
     *
     * @return the service file input stream
     */
    InputStream getEnvSysconfdirServiceFileInputStream();

    /**
     * Returns the input stream of the contents of the service file in the
     * sysconfdir specified by pg_config.
     *
     * If the input stream can't be created for any reason (such as
     * pg_config isn't found, or the file doesn't exist) null is returned.
     *
     * @return the service file input stream
     */
    String getConfigSysconfdirServiceFilename();

    /**
     * Returns the input stream of the contents of the service file in the
     * sysconfdir specified by pg_config.
     *
     * Returns null if there's an error calling pg_config, or the service file
     * doesn't exist, or if there's an error creating the input stream.
     *
     * @return the input stream of the service file contents, or null
     */
    InputStream getConfigSysconfdirServiceFileInputStream();

    /**
     * Returns an input stream of the contents of the service file in the
     * sysconfdir.
     *
     * Returns null if the file doesn't exist or if there's an error creating
     * the input stream.
     *
     * @return The input stream of the service file contents, or null
     */
    InputStream getSysconfdirServiceFileInputStream();

    /**
     * Returns the input stream of the contents of the passfile specified
     * by the PGPASSFILE environment variable.
     *
     * Returns null if the environment variable isn't set or the file doesn't
     * exist, or if there's an error creating the input stream.
     *
     * @param envPassfile The filename specified by the PGPASSFILE
     * @return the input stream of the contents of the passfile, or null
     */
    InputStream getEnvPassfileInputStream(String envPassfile);

    /**
     * Returns the input stream of the contents of the per-user passfile.
     *
     * Returns null if the file doesn't exist or if there's an error creating
     * the input stream.
     *
     * @return the input stream of the contents of the passfile, or null
     */
    InputStream getUserPassfileInputStream();

    /**
     * Returns the system user (corresponding to the "user.home" System
     * property value on the JVM).
     *
     * @return the system user
     */
    String getSystemUser();

}
