package com.grzm.pqconninfo.alpha;

import java.util.EnumMap;
import java.util.Objects;

/**
 * Interface defining the getters for conninfo options.
 *
 * Immutable conninfo implementations will implement this interface,
 * while mutable implementations will implement PqConninfoOptionsBean.
 *
 * It's not the responsibility of implementations to validate option values.
 * All properties are strings, even those expected to have numeric or
 * enumerated values.
 */
public final class PqConninfo {

    /**
     * Backing store for PqConninfoOption values for the PqConninfo instance.
     */
    private final EnumMap<PqConninfoOption, String> opts;

    /**
     * Private constructor for PqConninfo instances.
     *
     * @param opts an PqConninfoOption EnumMap with which to populate
     *            the instance
     */
    private PqConninfo(final EnumMap<PqConninfoOption, String> opts) {
        this.opts = opts.clone();
    }

    /**
     * Create a PqConninfo instance from a PqConninfoOption EnumMap.
     *
     * @param map the source PqConninfoOption EnumMap
     * @return the new PqConninfo instance
     */
    public static PqConninfo from(final EnumMap<PqConninfoOption, String> map) {
        return new PqConninfo(map);
    }

    /**
     * Returns the conninfo instance value of the given PqConninfoOption.
     *
     * @param opt the PqConninfoOption value to get
     * @return the value of the PqConninfoOption of the instance
     */
    public String get(final PqConninfoOption opt) {
        return opts.get(opt);
    }

    @Override
    public String toString() {
        EnumMap<PqConninfoOption, String> redactedOpts = opts.clone();

        if (redactedOpts.containsKey(PqConninfoOption.PASSWORD)) {
            redactedOpts.put(PqConninfoOption.PASSWORD, "****");
        }

        if (redactedOpts.containsKey(PqConninfoOption.SSLPASSWORD)) {
            redactedOpts.put(PqConninfoOption.SSLPASSWORD, "****");
        }

        return "PqConninfo{" + "opts=" + redactedOpts + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PqConninfo that = (PqConninfo) o;
        return opts.equals(that.opts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opts);
    }

}
