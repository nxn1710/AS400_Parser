package com.as400parser.rpgle;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.rpgle.model.*;
import com.as400parser.rpg3.model.ExpressionNode;
import com.as400parser.rpg3.model.IdentifierNode;

import java.util.*;

/**
 * Builds the RPGLE IR model from normalized source lines.
 * <p>
 * Classifies lines as fixed or free format, routes to appropriate parser,
 * and merges results into a unified IrDocument.
 */
public class RpgleIrBuilder {

    private final NormalizedSource normalizedSource;
    private final IrDocument document;
    private final RpgleContent content;
    private final IrDocument.Dependencies dependencies;

    // Track EXSR calls for subroutine cross-reference
    private final Map<String, List<Location>> exsrCalls = new LinkedHashMap<>();

    public RpgleIrBuilder(NormalizedSource normalizedSource) {
        this.normalizedSource = normalizedSource;
        this.document = new IrDocument();
        this.content = new RpgleContent();
        this.dependencies = new IrDocument.Dependencies();
        document.setContent(content);
        document.setDependencies(dependencies);
    }

    /**
     * Build the IR from normalized source.
     */
    public IrDocument build() {
        boolean isFullyFree = detectFullyFree();

        if (isFullyFree) {
            // Fully free-format: entire source goes to ANTLR
            buildFullyFree();
        } else {
            // Fixed or mixed format: use position-based parser
            buildFixedFormat();
        }

        // Build source lines
        buildSourceLines();

        // Build symbol table
        buildSymbolTable();

        // Extract dependencies
        extractDependencies();

        // Populate subroutine calledFrom
        populateSubroutineCalledFrom();

        return document;
    }

    // =========================================================================
    // Format detection
    // =========================================================================

    /**
     * Detect if source is fully free-format (**FREE at line 1).
     */
    private boolean detectFullyFree() {
        String[] lines = normalizedSource.getLines();
        if (lines.length == 0) return false;

        // Check first non-blank line for **FREE
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            return trimmed.toUpperCase().startsWith("**FREE");
        }
        return false;
    }

    // =========================================================================
    // Fully free-format parsing
    // =========================================================================

    private void buildFullyFree() {
        // Reconstruct full source text for free-format parser
        StringBuilder sb = new StringBuilder();
        String[] lines = normalizedSource.getLines();
        for (String line : lines) {
            sb.append(line).append("\n");
        }

        RpgleFreeParser freeParser = new RpgleFreeParser(content);
        freeParser.parse(sb.toString(), 1);

        // Collect errors
        for (String error : freeParser.getErrors()) {
            ParseError pe = new ParseError();
            pe.setMessage(error);
            pe.setSeverity(ParseError.Severity.WARNING);
            document.getErrors().add(pe);
        }

        // Collect copy directives
        processCopyDirectives(freeParser.getCopyDirectives());
    }

    // =========================================================================
    // Fixed / mixed format parsing
    // =========================================================================

    private void buildFixedFormat() {
        RpgleFixedParser fixedParser = new RpgleFixedParser(normalizedSource, content);
        fixedParser.parse();

        // Process copy directives into dependencies
        for (String directive : fixedParser.getCopyDirectives()) {
            IrDocument.CopyMemberRef copyRef = new IrDocument.CopyMemberRef();
            String[] parts = directive.trim().split("\\s+", 2);
            copyRef.setDirective(parts[0]);
            String target = parts.length > 1 ? parts[1].trim() : "";

            // Parse target: LIB/FILE,MEMBER or FILE,MEMBER or MEMBER
            String lib = null;
            String file = null;
            String member = target;
            int slashPos = target.indexOf('/');
            if (slashPos >= 0) {
                lib = target.substring(0, slashPos);
                target = target.substring(slashPos + 1);
            }
            int commaPos = target.indexOf(',');
            if (commaPos >= 0) {
                file = target.substring(0, commaPos);
                member = target.substring(commaPos + 1);
            }
            copyRef.setLibraryName(lib);
            copyRef.setFileName(file);
            copyRef.setMemberName(member);
            copyRef.setResolved(false);
            dependencies.getCopyMembers().add(copyRef);
        }

        // Check for /FREE blocks within mixed-format source and parse them
        detectAndParseFreeBlocks();
    }

    /**
     * Detect /FREE blocks or inline free-format code in mixed-format source.
     * Parse free blocks separately using ANTLR.
     */
    private void detectAndParseFreeBlocks() {
        String[] lines = normalizedSource.getLines();
        int[] origLineNums = normalizedSource.getOriginalLineNumbers();
        boolean inFreeBlock = false;
        StringBuilder freeBlock = new StringBuilder();
        int freeBlockStart = 1;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.length() > 6 ? line.substring(6).trim() : "";
            String upperTrimmed = trimmed.toUpperCase();

            if (upperTrimmed.startsWith("/FREE")) {
                inFreeBlock = true;
                freeBlockStart = origLineNums[i] + 1;
                continue;
            }
            if (upperTrimmed.startsWith("/END-FREE")) {
                inFreeBlock = false;
                if (freeBlock.length() > 0) {
                    RpgleFreeParser freeParser = new RpgleFreeParser(content);
                    freeParser.parse(freeBlock.toString(), freeBlockStart);
                    processCopyDirectives(freeParser.getCopyDirectives());
                    freeBlock.setLength(0);
                }
                continue;
            }

            if (inFreeBlock) {
                freeBlock.append(trimmed).append("\n");
            }
        }
    }

    private void processCopyDirectives(List<String> directives) {
        for (String directive : directives) {
            IrDocument.CopyMemberRef copyRef = new IrDocument.CopyMemberRef();
            String[] parts = directive.trim().split("\\s+", 2);
            copyRef.setDirective(parts[0]);
            String target = parts.length > 1 ? parts[1].trim() : "";

            String lib = null;
            String file = null;
            String member = target;
            int slashPos = target.indexOf('/');
            if (slashPos >= 0) {
                lib = target.substring(0, slashPos);
                target = target.substring(slashPos + 1);
            }
            int commaPos = target.indexOf(',');
            if (commaPos >= 0) {
                file = target.substring(0, commaPos);
                member = target.substring(commaPos + 1);
            }
            copyRef.setLibraryName(lib);
            copyRef.setFileName(file);
            copyRef.setMemberName(member);
            copyRef.setResolved(false);
            dependencies.getCopyMembers().add(copyRef);
        }
    }

    // =========================================================================
    // Source lines
    // =========================================================================

    private void buildSourceLines() {
        String[] lines = normalizedSource.getLines();
        int[] lineNumbers = normalizedSource.getOriginalLineNumbers();
        String[] seqNumbers = normalizedSource.getSequenceNumbers();

        List<SourceLine> sourceLines = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            SourceLine sl = new SourceLine();
            sl.setLineNumber(lineNumbers[i]);
            sl.setRawText(lines[i]);
            sl.setSequenceNumber(i < seqNumbers.length ? seqNumbers[i] : "");

            String line = lines[i];
            if (line.length() >= 6) {
                char specChar = Character.toUpperCase(line.charAt(5));
                switch (specChar) {
                    case 'H' -> sl.setSpecType("H");
                    case 'F' -> sl.setSpecType("F");
                    case 'D' -> sl.setSpecType("D");
                    case 'C' -> sl.setSpecType("C");
                    case 'I' -> sl.setSpecType("I");
                    case 'O' -> sl.setSpecType("O");
                    case 'P' -> sl.setSpecType("P");
                    case '*' -> {
                        sl.setSpecType("comment");
                        sl.setComment(true);
                    }
                    default -> sl.setSpecType(null);
                }
            }

            sl.setBlank(line.trim().isEmpty());
            sourceLines.add(sl);
        }
        content.setSourceLines(sourceLines);
    }

    // =========================================================================
    // Symbol table
    // =========================================================================

    private void buildSymbolTable() {
        List<RpgleContent.SymbolEntry> symbols = new ArrayList<>();

        // From D-specs
        for (DefinitionSpec dspec : content.getDefinitionSpecs()) {
            if (dspec.getName() == null || dspec.getName().isEmpty()) continue;

            RpgleContent.SymbolEntry entry = new RpgleContent.SymbolEntry();
            entry.setName(dspec.getName());
            entry.setDataType(dspec.getDataType());
            entry.setDefinedIn("D-spec");
            entry.setDefinitionLocation(dspec.getLocation());

            // Parse length from toPosition
            if (dspec.getToPosition() != null && !dspec.getToPosition().isEmpty()) {
                try {
                    entry.setLength(Integer.parseInt(dspec.getToPosition().trim()));
                } catch (NumberFormatException ignored) {}
            }
            entry.setDecimalPositions(dspec.getDecimalPositions());

            String defType = dspec.getDefinitionType();
            entry.setDataStructure("DS".equals(defType));

            symbols.add(entry);
        }

        // From I-specs (field definitions)
        for (InputSpec ispec : content.getInputSpecs()) {
            if ("fieldDefinition".equals(ispec.getSpecLevel()) && ispec.getFieldName() != null) {
                RpgleContent.SymbolEntry entry = new RpgleContent.SymbolEntry();
                entry.setName(ispec.getFieldName());
                entry.setDefinedIn("I-spec");
                entry.setDefinitionLocation(ispec.getLocation());
                if (ispec.getToPosition() != null) {
                    entry.setLength(ispec.getToPosition());
                }
                entry.setDecimalPositions(ispec.getDecimalPositions());
                symbols.add(entry);
            }
        }

        content.setSymbolTable(symbols);
    }

    // =========================================================================
    // Dependencies
    // =========================================================================

    private void extractDependencies() {
        // File dependencies from F-specs
        for (FileSpec fspec : content.getFileSpecs()) {
            if (fspec.getFileName() != null && !fspec.getFileName().isEmpty()) {
                String refType = mapFileType(fspec.getFileType());
                IrDocument.DependencyRef ref = new IrDocument.DependencyRef(fspec.getFileName(), refType);
                ref.getLocations().add(fspec.getLocation());
                dependencies.getReferencedFiles().add(ref);
            }
        }

        for (Object calcObj : content.getCalculationSpecs()) {
            extractDependenciesFromCalcNode(calcObj);
        }
    }

    private void extractDependenciesFromCalcNode(Object calcObj) {
        if (calcObj instanceof CalcSpec.Operation op) {
            String opcode = op.getOpcode() != null ? op.getOpcode().toUpperCase() : "";
            if ("CALL".equals(opcode) || "CALLB".equals(opcode)) {
                String progName = extractExpressionName(op.getFactor2());
                if (progName != null && !progName.isBlank()) {
                    IrDocument.DependencyRef ref = new IrDocument.DependencyRef(progName, "call");
                    ref.getLocations().add(op.getLocation());
                    dependencies.getCalledPrograms().add(ref);
                }
            } else if ("CALLP".equals(opcode)) {
                String extF2 = op.getExtendedFactor2();
                if (extF2 != null) {
                    String progName = extF2.contains("(") ? extF2.substring(0, extF2.indexOf('(')).trim() : extF2.trim();
                    IrDocument.DependencyRef ref = new IrDocument.DependencyRef(progName, "call");
                    ref.getLocations().add(op.getLocation());
                    dependencies.getCalledPrograms().add(ref);
                }
            } else if ("EXSR".equals(opcode)) {
                String srName = extractExpressionName(op.getFactor2());
                if (srName != null && !srName.isBlank()) {
                    exsrCalls.computeIfAbsent(srName.trim().toUpperCase(), k -> new ArrayList<>()).add(op.getLocation());
                }
            }
            return;
        }

        if (calcObj instanceof CalcSpec.ConditionalBlock block) {
            block.getThenOps().forEach(this::extractDependenciesFromCalcNode);
            block.getElseOps().forEach(this::extractDependenciesFromCalcNode);
            return;
        }
        if (calcObj instanceof CalcSpec.DoWhileBlock block) {
            block.getBodyOps().forEach(this::extractDependenciesFromCalcNode);
            return;
        }
        if (calcObj instanceof CalcSpec.DoUntilBlock block) {
            block.getBodyOps().forEach(this::extractDependenciesFromCalcNode);
            return;
        }
        if (calcObj instanceof CalcSpec.ForBlock block) {
            block.getBodyOps().forEach(this::extractDependenciesFromCalcNode);
            return;
        }
        if (calcObj instanceof CalcSpec.SelectBlock block) {
            block.getWhenClauses().forEach(clause ->
                    clause.getBodyOps().forEach(this::extractDependenciesFromCalcNode));
            block.getOtherOps().forEach(this::extractDependenciesFromCalcNode);
            return;
        }
        if (calcObj instanceof CalcSpec.MonitorBlock block) {
            block.getBodyOps().forEach(this::extractDependenciesFromCalcNode);
            block.getOnErrorClauses().forEach(clause ->
                    clause.getBodyOps().forEach(this::extractDependenciesFromCalcNode));
            return;
        }
        if (calcObj instanceof CalcSpec.SubroutineBlock block) {
            block.getOperations().forEach(this::extractDependenciesFromCalcNode);
        }
    }

    private String extractExpressionName(ExpressionNode expression) {
        if (expression instanceof IdentifierNode id) {
            return id.getName();
        }
        return expression != null ? expression.getRawText() : null;
    }

    private String mapFileType(String fileType) {
        if (fileType == null) return "combined";
        return switch (fileType.toUpperCase()) {
            case "I" -> "input";
            case "O" -> "output";
            case "U" -> "update";
            case "C" -> "combined";
            default -> "combined";
        };
    }

    // =========================================================================
    // Subroutine cross-reference
    // =========================================================================

    private void populateSubroutineCalledFrom() {
        for (CalcSpec.SubroutineBlock sub : content.getSubroutines()) {
            String name = sub.getSubroutineName();
            if (name != null) {
                List<Location> calls = exsrCalls.get(name.trim().toUpperCase());
                if (calls != null) {
                    List<Integer> lineNums = new ArrayList<>();
                    for (Location loc : calls) {
                        lineNums.add(loc.getStartLine());
                    }
                    sub.setCalledFrom(lineNums);
                }
            }
        }
    }
}
