package com.as400parser.common.parser;

import com.as400parser.common.model.IrDocument;

import java.nio.file.Path;
import java.util.List;

/**
 * Base interface for all AS/400 source parsers.
 * <p>
 * Defines the contract that all source-type parsers implement.
 * This is the extensibility foundation for future DDS, CL, RPG4 parsers.
 */
public interface As400Parser {

    /**
     * Parse a source file and produce an IR document.
     *
     * @param sourceFile path to the source file
     * @param options    parse configuration options
     * @return IR document
     */
    IrDocument parse(Path sourceFile, ParseOptions options);

    /**
     * Parse raw source text and produce an IR document.
     *
     * @param sourceText raw source text
     * @param options    parse configuration options
     * @return IR document
     */
    IrDocument parse(String sourceText, ParseOptions options);

    /**
     * Returns the source type this parser handles (e.g., "rpg3", "dds", "cl").
     */
    String getSourceType();

    /**
     * Returns the file extensions this parser can process.
     */
    List<String> getSupportedExtensions();
}
