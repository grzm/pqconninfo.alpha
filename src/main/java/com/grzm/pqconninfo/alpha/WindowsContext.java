package com.grzm.pqconninfo.alpha;

import com.grzm.pqconninfo.alpha.util.Util;

import java.io.File;
import java.io.InputStream;

/**
 * Windows-specific Context implementation.
 */
public class WindowsContext extends BaseContext {


    /**
     * Filename of the per-user passfile on Windows systems.
     */
    static final String USER_PASSFILE
            = "postgresql" + File.separator + "pgpass.conf";

    /**
     * Reference to the EnvVars provider for this context instance.
     */
    private final EnvVars envVars;

    /**
     * Cached value of the appData location.
     */
    private final String appData;

    /**
     * Per-user service file.
     */
    private final File userServiceFile;

    /**
     * Constructor for a Windows-system Context, given an EnvVars instance.
     *
     * @param envReader provider of environment variables
     */
    public WindowsContext(final EnvVars envReader) {
        this.appData = System.getenv("APPDATA");
        this.envVars = envReader;
        this.userServiceFile = new File(this.appData, USER_SERVICE_FILE);
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
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getUserPassfileInputStream() {
        File passfile = new File(this.appData, USER_PASSFILE);
        return Util.getFileInputStream(passfile);
    }

}
