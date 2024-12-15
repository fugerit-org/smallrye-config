package io.smallrye.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.smallrye.config.ConfigValueConfigSource.ConfigValueProperties;

class ConfigValuePropertiesTest {
    @Test
    void singleLine() throws Exception {
        ConfigValueProperties map = new ConfigValueProperties("config", 1);
        map.load(new StringReader("key=value"));

        assertEquals(1, map.get("key").getLineNumber());
    }

    @Test
    void multipleLines() throws Exception {
        ConfigValueProperties map = new ConfigValueProperties("config", 1);
        map.load(new StringReader(
                "key=value\n" +
                        "key2=value\n" +
                        "key3=value\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "key20=value\n"));

        assertEquals(1, map.get("key").getLineNumber());
        assertEquals(2, map.get("key2").getLineNumber());
        assertEquals(3, map.get("key3").getLineNumber());
        assertEquals(20, map.get("key20").getLineNumber());
    }

    @Test
    void comments() throws Exception {
        ConfigValueProperties map = new ConfigValueProperties("config", 1);
        map.load(new StringReader(
                "key=value\n" +
                        "key2=value\n" +
                        "#comment\n" +
                        "#comment\n" +
                        "#comment\n" +
                        "key3=value\n"));

        assertEquals(1, map.get("key").getLineNumber());
        assertEquals(2, map.get("key2").getLineNumber());
        assertEquals(6, map.get("key3").getLineNumber());
    }

    @Test
    void wrapValue() throws Exception {
        ConfigValueProperties map = new ConfigValueProperties("config", 1);
        map.load(new StringReader(
                "key=value\\wrap\n" +
                        "key2=value\\\nwrap\n" +
                        "#comment\f\t\n" +
                        "#comment\n" +
                        "\\key3=value\n"));

        assertEquals(1, map.get("key").getLineNumber());
        assertEquals("valuewrap", map.get("key2").getValue());
        assertEquals(2, map.get("key2").getLineNumber());
        assertEquals(6, map.get("key3").getLineNumber());
    }

    @Test
    void forbidDuplicateKeys() throws Exception {
        ConfigValueProperties map = new ConfigValueProperties("config", 1);
        String config = "key=value\n" +
                "key2=value\n" +
                "key2=value2\n";
        map.load(new StringReader(config));

        // property expected with the last value found
        assertEquals("value2", map.get("key2").getValue());

        // test with duplicate keys disabled
        System.setProperty(ConfigValuePropertiesUtils.SYS_PROP_FORBID_DUPLICATES, Boolean.TRUE.toString());

        // expected ConfigValidationException on ConfigSource loading
        try (StringReader reader = new StringReader(config)) {
            ConfigValueProperties mapForbid = new ConfigValueProperties("config", 1);
            Assertions.assertThrows(ConfigValidationException.class, () -> mapForbid.load(reader));
        }
    }

}
