package com.euph28.tson.filereader;

import com.euph28.tson.interpreter.provider.ContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * File reader capable of reading file from disk and returns as String
 */
public class FileReader implements ContentProvider {

    /* ----- VARIABLES ------------------------------ */

    final Logger logger = LoggerFactory.getLogger(FileReader.class);

    /**
     * Prefix to the file path, used for specifying a root directory to add before the filename
     */
    String filePrefix = "";

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a file reader capable of reading file content to String
     */
    public FileReader() {
    }

    /**
     * Create a file reader capable of reading file content to String
     *
     * @param rootDirectory Root directory to add to the front of the filename
     */
    public FileReader(String rootDirectory) {
        this.filePrefix = rootDirectory;
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Read a file and return String of all the content
     *
     * @param filename Name of the file to read
     * @return String with all the content within {@code filename}. Returns an empty {@code String} if reading failed
     */
    String readFile(String filename) {
        filename = filePrefix + filename;
        logger.trace("Reading file: " + filename);

        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            logger.warn("Failed to read file: " + filename, e);
        }

        return "";
    }

    /* ----- OVERRIDE: ContentProvider ------------------------------ */

    @Override
    public String getContent(String sourceName) {
        return readFile(sourceName);
    }
}