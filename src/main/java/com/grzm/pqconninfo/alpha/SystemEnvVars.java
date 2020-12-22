package com.grzm.pqconninfo.alpha;

/**
 * EnvVars implementation that fetches environment variable values
 * from the underlying system using System.getenv(String).
 */
public class SystemEnvVars implements EnvVars {
    /** {@inheritDoc} */
    @Override
    public String getenv(final String var) {
        return System.getenv(var);
    }
}
