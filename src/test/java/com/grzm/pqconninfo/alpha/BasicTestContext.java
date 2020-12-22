package com.grzm.pqconninfo.alpha;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class BasicTestContext extends BaseContext {
    private final EnvVars envVars;

    private String envPassfileContents;
    private String userPassfileContents;
    private String envServiceFileContents;
    private String userServiceFileContents;
    private String envSysconfdirServiceFileContents;
    private String configSysconfdirServiceFileContents;
    private String systemUser;

    public BasicTestContext() {
        this(new HashMap<String, String>());
    }
    public BasicTestContext(EnvVars envReader) {
        this.envVars = envReader;
    }

    public BasicTestContext(HashMap envVars) {
        this(new MapEnvVars(envVars));
    }

    @Override
    public String getenv(String var) {
        return this.envVars.getenv(var);
    }

    public void setEnvServiceFileContents(String envServiceFileContents) {
        this.envServiceFileContents = envServiceFileContents;
    }

    public void setUserServiceFileContents(String contents) {
        this.userServiceFileContents = contents;
    }

    public void setEnvSysconfdirServiceFileContents(String contents) {
        this.envSysconfdirServiceFileContents = contents;
    }

    public void setConfigSysconfdirServiceFileContents(String contents) {
        this.configSysconfdirServiceFileContents = contents;
    }

    public void setSystemUser(String user) {
        this.systemUser = user;
    }

    @Override
    public String getSystemUser() {
        return this.systemUser;
    }

    @Override
    public InputStream getUserServiceFileInputStream() {
        return inputStream(this.userServiceFileContents);
    }

    @Override
    public InputStream getEnvSysconfdirServiceFileInputStream() {
        return inputStream(this.envSysconfdirServiceFileContents);
    }

    @Override
    InputStream getEnvSysconfdirServiceFileInputStream(String envSysconfdir) {
        return inputStream(this.envSysconfdirServiceFileContents);
    }

    @Override
    public InputStream getConfigSysconfdirServiceFileInputStream() {
        return inputStream(this.configSysconfdirServiceFileContents);
    }

    @Override
    public InputStream getEnvServiceFileInputStream() {
        return inputStream(this.envServiceFileContents);
    }

    public void setEnvPassfileContents(String contents) {
        this.envPassfileContents = contents;
    }

    public void setUserPassfileContents(String contents) {
        this.userPassfileContents = contents;
    }

    @Override
    public InputStream getEnvPassfileInputStream(String envPassfile) {
        return inputStream(this.envPassfileContents);
    }

    @Override
    public InputStream getUserPassfileInputStream() {
        return inputStream(this.userPassfileContents);
    }

    static InputStream inputStream(String s) {
        if (s == null) {
            return null;
        }

        return new ByteArrayInputStream(s.getBytes());
    }

}
