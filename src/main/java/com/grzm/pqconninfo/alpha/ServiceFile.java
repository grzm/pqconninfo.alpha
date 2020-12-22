package com.grzm.pqconninfo.alpha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;

/**
 * ServiceFile provides a static method for parsing service file input streams.
 */
public final class ServiceFile {
    /**
     * Private default constructor for utility class.
     */
    private ServiceFile() { /* no constructor */ }

    /**
     * Returns the PqConninfoOption corresponding to the given keyword,
     * or null if not found.
     *
     * TODO Extract this as it's not specific to parsing service files
     *
     * @param keyword The keyword value to search for
     * @return The corresponding PqConninfoOption
     */
    static PqConninfoOption optionForKeyword(final String keyword) {
        for (PqConninfoOption opt : PqConninfoOption.values()) {
            if (opt.keyword.equals(keyword)) {
                return opt;
            }
        }
        return null;
    }

    /**
     * Searches the provided input stream for options for the given service.
     * If the service is found, an EnumMap with PqConninfoOption keys is
     * returned (empty, if the service is found and for some reason there are
     * no options provided), or null if the service is not found.
     *
     * This implementation roughly corresponds to the parsing method in
     * libpq fe-connect.c parseInfoFile
     *
     * TODO Extract getServiceConninfo into an interface
     *
     * @param service The service to search for
     * @param is      The input stream to search through
     * @return PqConninfoOption EnumMap (possibly empty) when the
     * service is found, or null if it wasn't found.
     * @throws IOException                   Error reading the input stream.
     * @throws MalformedServiceFileException Error parsing service file.
     */
    public static EnumMap<PqConninfoOption, String> getServiceConninfo(
            final String service,
            final InputStream is)
            throws IOException, MalformedServiceFileException {

        if (is == null) {
            return null;
        }

        EnumMap<PqConninfoOption, String> conninfo = null;

        try (BufferedReader rdr = new BufferedReader(
                new InputStreamReader(is))) {

            boolean foundGroup = false;
            String line;
            int lineNumber = 0;

            while ((line = rdr.readLine()) != null) {
            /*
              fe-connect.c checks that service file lines are <= 256 bytes long
              It returns an error if it finds one longer.
                libpq_gettext("line %d too long in service file \"%s\"\n"),

              I'm choosing not to replicate this behavior, as it's more of a
              buffer-overflow issue for c.
              We might have an OOM error, but we would have seen it already.
             */
                String trimmedLine = line.trim();
                lineNumber++;

                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                if (trimmedLine.startsWith("[")) {
                    if (foundGroup) {
                        // group info already read.
                        // This is the start of the next group.
                        break;
                    }
                    foundGroup = trimmedLine.equals("[" + service + "]");
                    if (foundGroup) {
                        conninfo = new EnumMap<>(PqConninfoOption.class);
                    }
                } else {
                    if (foundGroup) {
                        int i = trimmedLine.indexOf('=');
                        if (i == -1) {
                            throw new MalformedServiceFileException(
                                    "syntax error in service file",
                                    lineNumber, line);
                        } else {
                            String keyword = trimmedLine.substring(0, i);

                            if (PqConninfoOption.SERVICE.keyword.equals(keyword)) {
                                // libpq doesn't support nested service
                                throw new MalformedServiceFileException(
                                        "nested service specifications not supported in service file",
                                        lineNumber, line);
                            }

                            PqConninfoOption option = optionForKeyword(keyword);
                            if (option == null) {
                                // Unknown keyword.
                                throw new MalformedServiceFileException(
                                        "syntax error in service file",
                                        lineNumber, line);
                            }
                            i++;
                            String val = trimmedLine.substring(i);
                            conninfo.put(option, val);
                        }
                    }
                }
            }
        }

        return conninfo;
    }
}
