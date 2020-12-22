package com.grzm.pqconninfo.alpha.jdbc;

import com.grzm.pqconninfo.alpha.PqConninfo;
import com.grzm.pqconninfo.alpha.PqConninfoReader;
import com.grzm.pqconninfo.alpha.impl.EnumMapParamsReader;

import java.util.Properties;

/**
 * Utility class to provide a convenient entry point for
 * reading JdbcConnectionParameters from the given system.
 */
public final class JdbcConnectionParametersReader {

    /**
     * Private constructor for the utility class.
     */
    private JdbcConnectionParametersReader() { }

    /**
     * Reads JdbcConnectionParameters from the environment assuming no initial
     * properties.
     *
     * @return the JDBC connection parameters
     */
    public static JdbcConnectionParameters read() {
        return read(new Properties());
    }

    /**
     * Reads JDBC connection parameters from the environment with the given
     * initial properties.
     *
     * @param props the initial properties with which to seed the reader
     * @return the JDBC connection parameters
     */
    public static JdbcConnectionParameters read(final Properties props) {
        PqConninfo conninfo = PqConninfoReader.read(props);
        EnumMapParamsReader reader = new EnumMapParamsReader();
        return reader.read(conninfo);
    }

}
