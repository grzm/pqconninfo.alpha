package com.grzm.pqconninfo.alpha;

import java.util.Properties;

/**
 * A PqConninfoOptionsReader provides the read method to return a PqConninfo
 * instance from a given system context.
 */
public interface PqConninfoOptionsReader {
    /**
     * Reads the given system context for libpq conninfo options using the
     * given initial Properties.
     *
     * Common initial properties are host, port, dbname, and user that can be
     * used to supply or override other system values to look up a password
     * from a passfile.
     *
     * @param context the context of the system to read
     * @param props initial props
     * @return the PqConninfo instance to return
     */
    PqConninfo read(Context context, Properties props);
}
