package com.as400parser.rpg3;

import com.as400parser.common.model.ResolvedCopy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Resolves /COPY and /INCLUDE directives by searching copy member files.
 * <p>
 * Search algorithm:
 * <ol>
 *   <li>Parse directive: {@code /COPY FILE,MEMBER} or {@code /COPY LIB/FILE,MEMBER}</li>
 *   <li>For each path in {@code copyPaths} (left-to-right), try extensions:
 *       {@code .rpg}, {@code .rpg3}, {@code .mbr}, then no extension</li>
 *   <li>First match wins → read content, return {@code ResolvedCopy.found(...)}</li>
 *   <li>No match → return {@code ResolvedCopy.notFound(...)}</li>
 * </ol>
 * <p>
 * For {@code LIB/FILE,MEMBER} form: resolves as {@code sourceRoot/LIB/FILE/MEMBER}.
 */
public class Rpg3CopyResolver {

    private static final String[] EXTENSIONS = {".rpg", ".rpg3", ".mbr", ""};

    private final List<Path> copyPaths;
    private final Path sourceRoot;
    private final Charset charset;

    public Rpg3CopyResolver(List<Path> copyPaths, Path sourceRoot) {
        this(copyPaths, sourceRoot, StandardCharsets.UTF_8);
    }

    public Rpg3CopyResolver(List<Path> copyPaths, Path sourceRoot, Charset charset) {
        this.copyPaths = copyPaths != null ? copyPaths : List.of();
        this.sourceRoot = sourceRoot;
        this.charset = charset != null ? charset : StandardCharsets.UTF_8;
    }

    /**
     * Resolve a /COPY or /INCLUDE directive.
     *
     * @param directive The raw directive text, e.g., "/COPY QCPYSRC,COPYMBR"
     *                  or "/COPY MYLIB/QCPYSRC,COPYMBR"
     * @return A {@link ResolvedCopy} with the result
     */
    public ResolvedCopy resolve(String directive) {
        if (directive == null || directive.isBlank()) {
            return ResolvedCopy.notFound("", "Empty directive");
        }

        // Parse the directive to extract library, file, and member
        ParsedDirective parsed = parseDirective(directive);
        if (parsed.member == null || parsed.member.isBlank()) {
            return ResolvedCopy.notFound("", "Could not parse member name from: " + directive);
        }

        // Try LIB/FILE,MEMBER form first (resolved from sourceRoot)
        if (parsed.library != null && sourceRoot != null) {
            Path qualified = sourceRoot.resolve(parsed.library)
                    .resolve(parsed.file != null ? parsed.file : "QRPGSRC")
                    .resolve(parsed.member);
            ResolvedCopy result = tryResolve(qualified.getParent(), parsed.member,
                    parsed.file, parsed.library);
            if (result != null) return result;
        }

        // Try each copy path (left-to-right)
        for (Path copyPath : copyPaths) {
            Path searchDir = copyPath;
            if (parsed.file != null) {
                // If file is specified, look in copyPath/FILE/
                searchDir = copyPath.resolve(parsed.file);
            }
            ResolvedCopy result = tryResolve(searchDir, parsed.member,
                    parsed.file, parsed.library);
            if (result != null) return result;
        }

        // Not found
        return ResolvedCopy.notFound(parsed.member,
                "Copy member not found: " + parsed.member
                        + (parsed.file != null ? " in file " + parsed.file : "")
                        + (parsed.library != null ? " library " + parsed.library : ""));
    }

    /**
     * Try to resolve a member in a directory with each extension.
     */
    private ResolvedCopy tryResolve(Path directory, String member, String file, String library) {
        if (directory == null || !Files.isDirectory(directory)) return null;

        for (String ext : EXTENSIONS) {
            Path candidate = directory.resolve(member + ext);
            if (Files.isRegularFile(candidate)) {
                try {
                    String content = Files.readString(candidate, charset);
                    return ResolvedCopy.found(
                            candidate.toAbsolutePath().toString(),
                            content,
                            member,
                            file,
                            library
                    );
                } catch (IOException e) {
                    return ResolvedCopy.notFound(member,
                            "Found but could not read: " + candidate + " — " + e.getMessage());
                }
            }
        }
        return null;
    }

    // =========================================================================
    // Directive parser
    // =========================================================================

    /**
     * Parse a /COPY or /INCLUDE directive into its components.
     * <p>
     * Formats:
     * <ul>
     *   <li>{@code /COPY FILE,MEMBER}</li>
     *   <li>{@code /COPY LIB/FILE,MEMBER}</li>
     *   <li>{@code /COPY MEMBER} (member only)</li>
     * </ul>
     */
    static ParsedDirective parseDirective(String directive) {
        ParsedDirective result = new ParsedDirective();

        // Strip leading /COPY or /INCLUDE (case-insensitive)
        String text = directive.trim();
        if (text.toUpperCase().startsWith("/COPY")) {
            text = text.substring(5).trim();
        } else if (text.toUpperCase().startsWith("/INCLUDE")) {
            text = text.substring(8).trim();
        }

        // Check for comma separator: FILE,MEMBER or LIB/FILE,MEMBER
        int commaIdx = text.indexOf(',');
        if (commaIdx >= 0) {
            String beforeComma = text.substring(0, commaIdx).trim();
            result.member = text.substring(commaIdx + 1).trim().toUpperCase();

            // Check for slash in before-comma part: LIB/FILE
            int slashIdx = beforeComma.indexOf('/');
            if (slashIdx >= 0) {
                result.library = beforeComma.substring(0, slashIdx).trim().toUpperCase();
                result.file = beforeComma.substring(slashIdx + 1).trim().toUpperCase();
            } else {
                result.file = beforeComma.trim().toUpperCase();
            }
        } else {
            // Member only
            result.member = text.trim().toUpperCase();
        }

        return result;
    }

    /** Parsed /COPY directive components. */
    static class ParsedDirective {
        String library;
        String file;
        String member;
    }
}
