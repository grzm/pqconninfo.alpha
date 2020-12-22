package com.grzm.pqconninfo.alpha.impl;

import com.grzm.pqconninfo.alpha.Context;
import com.grzm.pqconninfo.alpha.EnvVars;
import com.grzm.pqconninfo.alpha.MalformedServiceFileException;
import com.grzm.pqconninfo.alpha.Passfile;
import com.grzm.pqconninfo.alpha.PqConninfo;
import com.grzm.pqconninfo.alpha.PqConninfoOption;
import com.grzm.pqconninfo.alpha.PqConninfoOptionsReader;
import com.grzm.pqconninfo.alpha.ServiceFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Properties;

public final class EnumMapConninfoReader implements PqConninfoOptionsReader {

    /**
     * The constructor for the default implementation of
     * PqConninfoOptionsReader.
     */
    public EnumMapConninfoReader() {
    }

    @Override
    public PqConninfo read(final Context context, final Properties props) {
        EnumMap<PqConninfoOption, String> opts = optsFrom(props);
        putAllIfAbsent(opts, serviceOpts(context, opts));
        putAllIfAbsent(opts, environmentVariables(context, opts));
        putAllIfAbsent(opts, systemUser(context, opts));
        putAllIfAbsent(opts, passfileInfo(context, opts));
        return PqConninfo.from(opts);
    }

    /**
     * Fills in EnumMap a with all of the values in EnumMap b that aren't
     * already set.
     *
     * @param a The EnumMap that will be filled in
     * @param b The EnumMap that serves as a source of values.
     */
    static void putAllIfAbsent(final EnumMap<PqConninfoOption, String> a,
                               final EnumMap<PqConninfoOption, String> b) {
        if (b == null || b.isEmpty()) {
            return;
        }
        for (PqConninfoOption opt : PqConninfoOption.values()) {
            if (b.containsKey(opt)) {
                a.putIfAbsent(opt, b.get(opt));
            }
        }
    }

    /**
     * Helper function to create a PqConninfoOption EnumMap from
     * a Properties instance.
     *
     * @param props the source of values
     * @return the populated EnumMap
     */
    public static EnumMap<PqConninfoOption, String>
    optsFrom(final Properties props) {
        EnumMap<PqConninfoOption, String> opts
                = new EnumMap<>(PqConninfoOption.class);
        for (PqConninfoOption opt : PqConninfoOption.values()) {
            String val = props.getProperty(opt.keyword);
            if (val != null) {
                opts.put(opt, val);
            }
        }
        return opts;
    }

    /**
     * Returns conninfo option values for the given service as an EnumMap of
     * PqConninfoOption keys and option values found for the corresponding
     * service.
     *
     * @param service  The service we're looking for
     * @param filename The filename of the service file to parse.
     * @param is       The InputStream of the service file.
     * @return An EnumMap of PqConninfoOption keys and String option values
     */
    private static EnumMap<PqConninfoOption, String>
    getServiceConninfo(final String service,
                       final String filename,
                       final InputStream is) {

        final Logger logger
                = LoggerFactory.getLogger(EnumMapConninfoReader.class);

        try {
            return ServiceFile.getServiceConninfo(service, is);
        } catch (MalformedServiceFileException e) {
            logger.warn("{}, file {}, line {}",
                    e.getMessage(), filename, e.getLineNumber());
            return null;
        } catch (IOException e) {
            logger.warn("Failed to parse service file {}, {}",
                    filename, e.getMessage());
            return null;
        }
    }

    static EnumMap<PqConninfoOption, String>
    serviceOpts(final Context context,
                final EnumMap<PqConninfoOption, String> opts) {

        String service = opts.get(PqConninfoOption.SERVICE);

        if (service == null) {
            service = context.getenv(
                    PqConninfoOption.SERVICE.environmentVariable);
        }

        if (service == null) {
            return null;
        }

        EnumMap<PqConninfoOption, String> info
                = new EnumMap<>(PqConninfoOption.class);

        info.put(PqConninfoOption.SERVICE, service);

        String envServiceFile = context.getenv(EnvVars.PGSERVICEFILE);

        if (envServiceFile != null) {
            InputStream is = context.getEnvServiceFileInputStream();
            if (is != null) {
                EnumMap<PqConninfoOption, String> envServiceInfo
                        = getServiceConninfo(service, envServiceFile, is);

                if (envServiceInfo != null) {
                    putAllIfAbsent(info, envServiceInfo);
                }
            }
        } else {
            /*
             libpq falls through. It'll look in both of these locations,
             stopping if (and when) it finds settings
             for the specified service.
               1. PGSERVICEFILE if set, or ~/.pg_service.conf
               2. PGSYSCONFDIR/pg_service.conf
                  or $(pg_config --sysconfidir)/pg_service.conf
             */
            EnumMap<PqConninfoOption, String> serviceConninfo =
                    getServiceConninfo(
                            service,
                            context.getUserServiceFilename(),
                            context.getUserServiceFileInputStream());

            if (serviceConninfo == null) {
                if (context.getenv(EnvVars.PGSYSCONFDIR) != null) {
                    serviceConninfo = getServiceConninfo(service,
                            context.getEnvSysconfdirServiceFilename(),
                            context.getEnvSysconfdirServiceFileInputStream());
                } else {
                    serviceConninfo = getServiceConninfo(service,
                            context.getConfigSysconfdirServiceFilename(),
                            context.getConfigSysconfdirServiceFileInputStream());
                }
            }

            if (serviceConninfo != null) {
                putAllIfAbsent(info, serviceConninfo);
            }
        }

        return info;
    }

    static EnumMap<PqConninfoOption, String>
    environmentVariables(final Context context,
                         final EnumMap<PqConninfoOption, String> opts) {
        EnumMap<PqConninfoOption, String> envVars
                = new EnumMap<>(PqConninfoOption.class);
        for (PqConninfoOption opt : PqConninfoOption.values()) {
            if (opt.environmentVariable == null || opts.containsKey(opt)) {
                continue;
            }
            String val = context.getenv(opt.environmentVariable);

            if (val != null) {
                envVars.put(opt, val);
            }
        }
        return envVars;
    }

    /**
     * Returns an EnumMap with the PqConninfo.USER key set to
     * the system user iff
     * * the current opts does not have PqConninfo.USER set; and
     * * the system user is not null.
     * <p>
     * Otherwise, it returns null.
     *
     * @param context the system context
     * @param opts    the current PqConninfoOption values
     * @return an map with PqConninfoOption.USER set, or null
     */
    public static EnumMap<PqConninfoOption, String>
    systemUser(final Context context,
               final EnumMap<PqConninfoOption, String> opts) {
        if (opts.containsKey(PqConninfoOption.USER)) {
            return null;
        }

        String user = context.getSystemUser();
        if (user == null) {
            return null;
        }

        return new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.USER, user);
        }};
    }

    static String
    passfilePassword(final Context context,
                     final EnumMap<PqConninfoOption, String> opts) {
        String envPassfile = context.getenv(
                PqConninfoOption.PASSFILE.environmentVariable);

        InputStream inputStream;

        if (envPassfile != null) {
            inputStream = context.getEnvPassfileInputStream(envPassfile);
        } else {
            inputStream = context.getUserPassfileInputStream();
        }

        if (inputStream == null) {
            return null;
        }

        return Passfile.getPassword(
                opts.get(PqConninfoOption.HOST),
                opts.get(PqConninfoOption.PORT),
                opts.get(PqConninfoOption.DBNAME),
                opts.get(PqConninfoOption.USER),
                inputStream);
    }


    static EnumMap<PqConninfoOption, String>
    passfileInfo(final Context context,
                 final EnumMap<PqConninfoOption, String> opts) {
        if (opts.containsKey(PqConninfoOption.PASSWORD)) {
            return null;
        }

        String password = passfilePassword(context, opts);
        if (password == null) {
            return null;
        }

        return new EnumMap<PqConninfoOption, String>(PqConninfoOption.class) {{
            put(PqConninfoOption.PASSWORD, password);
        }};
    }

}
