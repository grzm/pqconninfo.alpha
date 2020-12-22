package com.grzm.pqconninfo.alpha;

import com.grzm.pqconninfo.alpha.impl.EnumMapConninfoReader;

import java.util.Properties;

/**
 * Utility class to serve as a convenient API for creating PqConninfo
 * instances.
 */
public final class PqConninfoReader {

    /**
     * Private constructor for the utility class.
     */
    private PqConninfoReader() { }

    /**
     * Reads libpq conninfo values from the host system, assuming no initial
     * properties.
     *
     * @return the libpq conninfo values
     */
    public static PqConninfo read() {
        return read(SystemContextFactory.create(), new Properties());
    }

    /**
     * Reads libpq conninfo values from the host system with the given initial
     * properties.
     *
     * @param props the initial properties
     * @return the libpq conninfo values
     */
    public static PqConninfo read(final Properties props) {
        return read(SystemContextFactory.create(), props);
    }

    /**
     * Reads the given context augmented by the given initial props to create a
     * new PqConninfo instance.
     *
     * @param context the system context to read
     * @param props the initial props
     * @return the libpq conninfo values read from the system
     */
    public static PqConninfo read(final Context context,
                                  final Properties props) {
        PqConninfoOptionsReader reader = new EnumMapConninfoReader();
        return reader.read(context, props);
    }

}
