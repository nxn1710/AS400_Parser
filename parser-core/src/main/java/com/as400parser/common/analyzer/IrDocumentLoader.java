package com.as400parser.common.analyzer;

import com.as400parser.common.model.IrDocument;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility to batch load parsing Intermediate Representation (IR) JSON documents
 * from a directory and group them by their source type for subsequent analysis.
 */
public class IrDocumentLoader {

    private final Gson gson;

    public IrDocumentLoader() {
        this.gson = new Gson();
    }

    public IrDocumentLoader(Gson customGson) {
        this.gson = customGson;
    }

    /**
     * Traverses the specified directory recursively, loading all `.json` files 
     * and deserializing them into IrDocument objects, grouped by the SourceType.
     *
     * @param directory The root directory path containing IR JSON files.
     * @return A map where the key is the IR source type (e.g. DDS_PF, DDS_LF, RPG3, CL)
     *         and the value is a list of IrDocuments.
     * @throws IOException If the directory cannot be read.
     */
    public Map<String, List<IrDocument>> loadFromDirectory(Path directory) throws IOException {
        Map<String, List<IrDocument>> groupedDocs = new HashMap<>();

        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Path must be an existing directory: " + directory);
        }

        try (Stream<Path> paths = Files.walk(directory)) {
            List<Path> jsonFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".json"))
                    .collect(Collectors.toList());

            for (Path jsonFile : jsonFiles) {
                IrDocument doc = loadSingleFile(jsonFile);
                if (doc != null && doc.getMetadata() != null && doc.getMetadata().getSourceType() != null) {
                    String sourceType = doc.getMetadata().getSourceType();
                    groupedDocs.computeIfAbsent(sourceType, k -> new ArrayList<>()).add(doc);
                } else {
                    // Log a warning for gracefully bypassing invalid IR, per design decisions
                    System.err.println("[WARNING] Skipping file, invalid IR format or missing sourceType: " + jsonFile);
                }
            }
        }

        return groupedDocs;
    }

    /**
     * Groups a list of IrDocument objects by their source type metadata.
     *
     * @param irDocs List of loaded IrDocument objects.
     * @return A map grouped by source type.
     */
    public Map<String, List<IrDocument>> groupBySourceType(List<IrDocument> irDocs) {
        Map<String, List<IrDocument>> groupedDocs = new HashMap<>();
        if (irDocs == null) return groupedDocs;

        for (IrDocument doc : irDocs) {
            String sourceType = (doc.getMetadata() != null) ? doc.getMetadata().getSourceType() : null;
            if (sourceType != null) {
                groupedDocs.computeIfAbsent(sourceType, k -> new ArrayList<>()).add(doc);
            }
        }
        return groupedDocs;
    }

    /**
     * Loads and parses a single IR JSON file.
     *
     * @param jsonFile The file path to load.
     * @return The parsed IrDocument, or null if parsing fails.
     */
    private IrDocument loadSingleFile(Path jsonFile) {
        try (Reader reader = Files.newBufferedReader(jsonFile)) {
            return gson.fromJson(reader, IrDocument.class);
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("[WARNING] Error parsing JSON file (" + jsonFile + "): " + e.getMessage());
            return null; // Graceful degradation
        }
    }
}
