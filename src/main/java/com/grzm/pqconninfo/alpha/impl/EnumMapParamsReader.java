package com.grzm.pqconninfo.alpha.impl;

import com.grzm.pqconninfo.alpha.PqConninfo;
import com.grzm.pqconninfo.alpha.jdbc.JdbcConnectionParameters;
import com.grzm.pqconninfo.alpha.jdbc.JdbcConninfoReader;

public final class EnumMapParamsReader implements JdbcConninfoReader {

    @Override
    public JdbcConnectionParameters read(final PqConninfo conninfo) {
        return JdbcConnectionParameters.from(conninfo);
    }
}
