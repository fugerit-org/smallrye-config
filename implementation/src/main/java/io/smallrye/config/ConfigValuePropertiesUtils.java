package io.smallrye.config;

import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;

import org.jboss.logging.Logger;

import io.smallrye.config._private.ConfigLogging;

/**
 * Class responsible for adding some configuration features to ConfigValueProperties
 *
 * Setting system property :
 * smallrye.config.forbidDuplicateKeys=true (default false)
 * A ConfigValidationException will be raised if a duplicate key is found in the same config source.
 *
 * https://github.com/smallrye/smallrye-config/issues/1269
 *
 * NOTE: For backward compatibility, the default behaviour it to avoid duplicate keys check.
 */
public class ConfigValuePropertiesUtils {

    private static ConfigLogging log = Logger.getMessageLogger(MethodHandles.lookup(), ConfigLogging.class,
            "io.smallrye.config");

    private ConfigValuePropertiesUtils() {
    }

    /**
     * If set to true, a ConfigValidationException will be raised if a duplicate key is found in the same config source.
     */
    public static final String SYS_PROP_FORBID_DUPLICATES = "smallrye.config.forbidDuplicateKeys";

    /**
     * Converts a single message to a {@link ConfigValidationException}
     *
     * @param message the message.
     * @return the ConfigValidationException
     */
    public static ConfigValidationException createConfigValidationException(final String message) {
        ConfigValidationException.Problem[] problems = {
                new ConfigValidationException.Problem(message)
        };
        return new ConfigValidationException(problems);
    }

    /**
     * Creates a consumer to handle duplicate keys
     *
     * @return the consumer checking duplicate keys
     */
    public static Consumer<ConfigValue> newConfigValueCheck() {
        if (Boolean.parseBoolean(System.getProperty(SYS_PROP_FORBID_DUPLICATES, Boolean.FALSE.toString()))) {
            return configValue -> {
                if (configValue != null) {
                    throw createConfigValidationException(String.format(
                            "duplicate keys found for : %s, source name : %s (error raised because %s system property set)",
                            configValue.getName(), configValue.getSourceName(), SYS_PROP_FORBID_DUPLICATES));
                }
            };
        } else {
            return configValue -> {
                if (configValue != null) {
                    log.warnv("duplicate keys found for : {0}, source name : {1}", configValue.getName(),
                            configValue.getConfigSourceName());
                }
            };
        }
    }

}
