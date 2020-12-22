package com.grzm.pqconninfo.alpha;

/**
 * Helper to inspect the given system and create the appropriate Windows or
 * Unix Context.
 */
public final class SystemContextFactory {
    /**
     * Private default utility constructor.
     */
    private SystemContextFactory() { /* don't construct me! */ }

    /**
     * @return {@code true} if the system is Windows and @{code false}
     * otherwise.
     */
    static boolean isWindows() {
        return isWindows(System.getProperty("os.name"));
    }

    /**
     * @param osName "os.name" System property value
     * @return true if the system is Windows and false otherwise.
     */
    static boolean isWindows(final String osName) {
        return osName.startsWith("Windows");
    }

    /**
     * @return Context for the underlying system.
     */
    public static Context create() {
        EnvVars envReader = new SystemEnvVars();
        if (isWindows()) {
            return new WindowsContext(envReader);
        }
        return new UnixContext(envReader);
    }

}
