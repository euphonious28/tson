package com.euph28.tson.filereader;

import com.euph28.tson.core.provider.ContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File reader capable of reading file from disk and returns as String
 */
public class FileReader implements ContentProvider {

    /* ----- VARIABLES ------------------------------ */

    final Logger logger = LoggerFactory.getLogger(FileReader.class);

    /**
     * Root directory to resolve files from
     */
    Path rootDirectory;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a file reader capable of reading file content to String
     */
    public FileReader() {
        this(Paths.get(""));
    }

    /**
     * Create a file reader capable of reading file content to String
     *
     * @param rootDirectory Root directory to resolve files from
     */
    public FileReader(String rootDirectory) {
        this(Paths.get(rootDirectory));
    }

    /**
     * Create a file reader capable of reading file content to String
     *
     * @param rootDirectory Root directory to resolve files from
     */
    public FileReader(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Read a file and return String of all the content
     *
     * @param filename Path of the file to read
     * @return String with all the content within {@code filename}. Returns an empty {@code String} if reading failed
     */
    String readFile(Path filename) {
        try {
            logger.trace("Reading file: " + filename);
            return new String(Files.readAllBytes(filename));
        } catch (IOException e) {
            logger.warn("Failed to read file: " + filename, e);
        }

        return "";
    }

    /* ----- OVERRIDE: ContentProvider ------------------------------ */

    @Override
    public String getContent(String sourceName) {
        // Retrieve file as a path
        Path filepath;

        // Path sanity check (ensure sourceName makes sense) as this method is open to calls
        try {
            filepath = rootDirectory.resolve(sourceName);
        } catch (InvalidPathException e) {
            logger.info("Invalid path provided (only first line is shown): " + sourceName.split("\\R+", 1)[0]);
            return "";
        }

        return readFile(filepath);
    }
}