package io.p13i.ra.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.stream.Collectors;

public class Version {
    /**
     * Source: https://stackoverflow.com/a/38204232
     * @return the version number set in build.gradle
     */
    public static String getVersion() {
        Properties p = new Properties();

        try {
            ByteArrayInputStream s = new ByteArrayInputStream(getResourceFileAsString("version.properties").getBytes(StandardCharsets.UTF_8));
            p.load(s);
            return p.getProperty("version").replaceFirst("v", "");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads given resource file as a string.
     * Source: https://stackoverflow.com/a/46613809
     *
     * @param fileName path to the resource file
     * @return the file's contents
     * @throws IOException if read fails for any reason
     */
    private static String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}
