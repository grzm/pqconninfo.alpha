package com.grzm.pqconninfo.alpha.jdbc;

import com.grzm.pqconninfo.alpha.PqConninfo;

/**
 * Interface to populate a new JdbcConnectionParameters from a PqConninfo
 * instance.
 */
public interface JdbcConninfoReader {
    /**
     * @param conninfo the source libpq conninfo
     * @return the JDBC connection parameters
     */
    JdbcConnectionParameters read(PqConninfo conninfo);
}
