package com.grzm.pqconninfo.alpha;

import com.grzm.pqconninfo.alpha.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Functions for finding a password in a given passfile input stream.
 */
public final class Passfile {
    public static final String WILDCARD = "*";
    public static final int PASSWORD_FIELD_OFFSET = 4;
    public static final int MIN_FIELD_COUNT = 4;
    public static final int MAX_FIELD_COUNT = 5;
    /*
      DefaultHost is defined in src/interfaces/libpq/fe-connect.c
      We're spelling it DEFAULT_HOST to follow the Java convention.
     */
    public static final String DEFAULT_HOST = "localhost";
    // See note below regarding the decision to set DEFAULT_PORT_STR
    public static final String DEFAULT_PORT_STR = "5432";
    // DEFAULT_PGSOCKET_DIR is defined in src/include/pg_config_manual.h
    public static final String DEFAULT_PGSOCKET_DIR = "/tmp";

    /**
     * Privatize utility constructor.
     */
    private Passfile() { /* don't construct me! */ }

    /**
     * Parses the given passfile line, returning the password value if it
     * matches (taking into account wildcards) the given host, port, dbname,
     * and user values.
     * <p>
     * Returns null if the line is malformed or doesn't match the given values.
     *
     * @param host   host to match
     * @param port   port to match
     * @param dbname dbname to match
     * @param user   user to match
     * @param line   line to parse and match
     * @return password if matched and null otherwise
     */
    public static String parsePgpassLine(
            final String host,
            final String port,
            final String dbname,
            final String user,
            final String line) {
        String[] fields = splitPgpassLine(line);
        if (fields.length < MIN_FIELD_COUNT
                || MAX_FIELD_COUNT < fields.length) {
            return null;
        }
        Iterator<String> optsIter
                = Arrays.asList(host, port, dbname, user).iterator();
        Iterator<String> fieldsIter = Arrays.asList(fields).iterator();
        while (optsIter.hasNext() && fieldsIter.hasNext()) {
            if (!optMatchesField(optsIter.next(), fieldsIter.next())) {
                return null;
            }
        }

        /*
          TODO How do we distinguish between malformed entry, no match, and
           no password?
          Does it matter? In any case, we don't have a password, either because
           we weren't able to find one in the file, or we found a matching
           entry and (assuming the pgpass file is correct), we don't need one.
         */
        if (fields.length == MIN_FIELD_COUNT) {
            return null;
        }

        return unescapePassword(fields[PASSWORD_FIELD_OFFSET]);
    }

    /**
     * Helper to split line into entry fields.
     *
     * @param line the line to split
     * @return an array of entry fields
     */
    static String[] splitPgpassLine(final String line) {
        return line.split("(?<!\\\\):");
    }

    /**
     * Helper to remove escaping from a passfile password field value.
     *
     * @param s passfile password field value
     * @return the unescaped password value
     */
    static String unescapePassword(final String s) {
        return s.replaceAll("\\\\([\\\\:])", "$1");
    }

    /**
     * Helper to match conninfo option with passfile entry field
     * (taking into account wildcard field value).
     *
     * @param opt   the conninfo option
     * @param field the passfile entry field value
     * @return true if the option matches the field value and false otherwise
     */
    static boolean optMatchesField(final String opt, final String field) {
        return field.equals(WILDCARD) || field.equals(opt);
    }

    /**
     * Searches the given passfile input stream for a password that matches
     * the options in the given conninfo.
     *
     * @param conninfo The conninfo to find a password for
     * @param pgpass   The inputstream for the passfile contents
     * @return the password found in the input stream, or null if none found
     */
    public static String getPassword(
            final Map<PqConninfoOption, String> conninfo,
            final InputStream pgpass) {
        String host = conninfo.get(PqConninfoOption.HOST);
        String port = conninfo.get(PqConninfoOption.PORT);
        String dbname = conninfo.get(PqConninfoOption.DBNAME);
        String user = conninfo.get(PqConninfoOption.USER);

        return getPassword(host, port, dbname, user, pgpass);
    }

    /**
     * Searches the given passfile input tream for a password that matches
     * the given options
     *
     * This corresponds roughly to passwordFromFile in fe-connect.c
     *   static char *
     *   passwordFromFile(const char *hostname, const char *port,
     *                    const char *dbname, const char *username,
     *                    const char *pgpassfile)
     *
     * This java implementation takes an InputStream instead of a file name for
     * abstraction. This makes testing easier by separating IO from application
     * logic. It also separates the concerns of determining which file to use,
     * or whether a file is available at all.
     *
     * The PostgreSQL documentation mentions obliquely that lines beginning
     * with a "#" are ignored. AFAICT, there isn't explicit code to handle
     * this. It falls out of the fact that host names can't begin with a "#",
     * and therefore we won't match any pgpassfile entry that is prefixed
     * with a "#".
     *
     * @param hostOption the host value to match
     * @param portOption the port value to match
     * @param dbname     the dbname value to match
     * @param user       the user value to match
     * @param pgpass     the passfile input stream to search
     * @return the password value if found, and null otherwise
     */
    public static String getPassword(final String hostOption,
                                     final String portOption,
                                     final String dbname,
                                     final String user,
                                     final InputStream pgpass) {
        /*

          The naming is a bit inconsistent:
          - fe-connect's parameter naming (hostname, port, dbname, username)
          - password file docs (hostname, port, database, username, password),
          - conninfo struct fields (host/hostaddr, port, dbname, user)

          We're following conninfo naming (host, port, dbname, user) across
          the board for consistency.
        */

        /*
          TODO libpq's passwordFromFile doesn't even check the file if dbname
            and user aren't set.

          This should be extracted into the code we check to see if a file
          is available and convert it to an InputStream.
         */
        if (Util.isNullOrEmpty(dbname) || Util.isNullOrEmpty(user)) {
            return null;
        }

        /*
          TODO libpq has code to set the host to "localhost" if the host is
           equal to the DEFAULT_PGSOCKET_DIR.

          This value can be overridden by modifying the source and recompiling
          (there isn't a configure option for this like there is for default
          port) or by passing a flag at server startup, so this might not do
          the right thing either. This should be documented.
         */
        String host = (Util.isNullOrEmpty(hostOption)
                || DEFAULT_PGSOCKET_DIR.equals(hostOption))
                ? DEFAULT_HOST : hostOption;

        /*
          The default port is a compile time constant. It's generally going to
          be 5432, but they could have set it to something else when compiling
          the libpq library. For convenience, it would be nice to use 5432 as
          the default here, but is that an assumption worth making? If we *do*
          make this assumption, we'll need to call it out in the docs. TODO
        */
        String port = (Util.isNullOrEmpty(portOption))
                ? DEFAULT_PORT_STR : portOption;

        /*
          From https://www.postgresql.org/docs/current/libpq-pgpass.html
          (You can add a reminder comment to the file by copying the line above
          and preceding it with #.) Each of the first four fields can be a
          literal value, or *, which matches anything. The password field from
          the first line that matches the current connection parameters will be
          used. (Therefore, put more-specific entries first when you are using
          wildcards.) If an entry needs to contain : or \, escape this
          character with \. The host name field is matched to the host
          connection parameter if that is specified, otherwise to the hostaddr
          parameter if that is specified; if neither are given then the host
          name localhost is searched for. The host name localhost is also
          searched for when the connection is a Unix-domain socket connection
          and the host parameter matches libpq's default socket directory path.

          In a standby server, a database field of replication matches
          streaming replication connections made to the master server.
          The database field is of limited usefulness otherwise, because users
          have the same password for all databases in the same cluster.
        */

        String password = null;

        try (BufferedReader rdr
                     = new BufferedReader(new InputStreamReader(pgpass))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                password = parsePgpassLine(host, port, dbname, user, line);
                if (password != null) {
                    break;
                }
            }
        } catch (IOException e) {
            // do nothing
        }

        return password;
    }

}
