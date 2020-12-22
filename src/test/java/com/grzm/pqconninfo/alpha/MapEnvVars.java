package com.grzm.pqconninfo.alpha;

import java.util.Map;

public class MapEnvVars implements EnvVars {
    private final Map<String, String> env;

    public MapEnvVars(Map<String, String> env) {
        this.env = env;
    }

    @Override
    public String getenv(String var) {
        return this.env.get(var);
    }

}
