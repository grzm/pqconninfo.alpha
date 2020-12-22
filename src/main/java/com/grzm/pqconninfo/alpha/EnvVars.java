package com.grzm.pqconninfo.alpha;

/**
 * Abstraction for fetching environment variable values allowing us
 * to substitute our own environment variable source for testing.
 */
public interface EnvVars {
    /**
     * The environment variable specifying the location of the service file.
     */
    String PGSERVICEFILE = "PGSERVICEFILE";

    /**
     * The environment variable  specifying the location of the sysconfdir.
     */
    String PGSYSCONFDIR = "PGSYSCONFDIR";

    /**
     * The value of the corresponding environment variable, or null if missing.
     *
     * @param var The name of the environment variable to fetch
     * @return environment variable value, or null
     */
    String getenv(String var);
}
