package com.euph28.tson.core;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Utility class that performs actions that have no specific category
 */
public class Utility {

    /**
     * Version of TSON
     */
    static String version = "";

    public static String getVersion() {
        // Load version from manifest if its empty
        if (version.isEmpty()) {
            // Default to no-version if failed to retrieve
            version = "no-version";

            // Retrieve and read manifest file for version
            // Snippet from: https://dzone.com/articles/how-to-read-version-number-and-other-details-from
            try {
                URLClassLoader cl = (URLClassLoader) Utility.class.getClassLoader();
                URL url = cl.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                Attributes attr = manifest.getMainAttributes();
                version = manifest.getMainAttributes().getValue("Implementation-Version");
            } catch (IOException e) {
                LoggerFactory.getLogger(Utility.class).error("Failed to retrieve TSON version. Defaulting to " + version + " instead", e);
            }
        }

        // Return version
        return version;
    }
}
