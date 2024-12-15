package io.smallrye.config.source.yaml;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import io.smallrye.config.ConfigValidationException;
import io.smallrye.config.ConfigValuePropertiesUtils;

/**
 * This classe handle some configuration options for yaml config loading.
 *
 * @see {@link ConfigValuePropertiesUtils} for details.
 */
public class YamlConfigSourceUtils {

    /**
     * If set to true, a ConfigValidationException will be raised if a duplicate key is found in the same config source.
     */
    public static final String SYS_PROP_FORBID_DUPLICATES = ConfigValuePropertiesUtils.SYS_PROP_FORBID_DUPLICATES;

    private YamlConfigSourceUtils() {
    }

    /**
     * Creates a LoaderOptions based on System Properties
     *
     * @return Yaml loader options.
     */
    public static LoaderOptions newYamlLoaderOptions() {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(
                !Boolean.parseBoolean(System.getProperty(SYS_PROP_FORBID_DUPLICATES, Boolean.FALSE.toString())));
        return options;
    }

    /**
     * Convert a DuplicateKeyException to ConfigValidationException.
     *
     * @param dke the duplicate key exception
     * @return the converted config validation exception
     */
    public static ConfigValidationException convertException(DuplicateKeyException dke) {
        return ConfigValuePropertiesUtils.createConfigValidationException(String.format(
                "%s (error raised because %s system property set)",
                dke.getProblem(), SYS_PROP_FORBID_DUPLICATES));
    }

}
