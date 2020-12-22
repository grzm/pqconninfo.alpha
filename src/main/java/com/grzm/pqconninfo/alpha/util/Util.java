package com.grzm.pqconninfo.alpha.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Miscellaneous, catch-all functions for which I haven't found a more
 * appropriate home.
 */
public final class Util {

    /**
     * Privatize default constructor.
     */
    private Util() { /* no constructor */ }

    /**
     * The input stream of the corresponding file, or null if the file can't be
     * opened for whatever reason.
     *
     * @param filename The filename of the file to open as an InputStream
     * @return The input stream of the corresponding file, or null
     */
    public static InputStream getFileInputStream(final String filename) {
        if (filename == null) {
            return null;
        }

        try {
            return new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * The input stream of the corresponding file, or null if the file can't be
     * opened for whatever reason.
     *
     * @param file The file to open as an InputStream
     * @return The input stream of the corresponding file, or null
     */
    public static InputStream getFileInputStream(final File file) {
        if (file == null) {
            return null;
        }

        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Null-safe check to test if a String is empty.
     * TODO Refactor this as isBlank ?
     *
     * @param s The string to check
     * @return True if the string is empty or null, and false otherwise.
     */
    public static boolean isNullOrEmpty(final String s) {
        return (s == null) || s.isEmpty();
    }
}
