package com.as400parser.rpg3;

import com.as400parser.common.model.Location;
import com.as400parser.rpg3.model.*;
import com.as400parser.rpg3.model.CalcSpec.*;
import com.as400parser.rpg3.model.Rpg3Content.*;

import java.util.*;

/**
 * Second-pass symbol table builder for RPG3 IR documents.
 * <p>
 * Scans 4 sources in priority order (highest → lowest):
 * <ol>
 *   <li>{@code inputSpecs[]} field definitions — has explicit type info</li>
 *   <li>{@code dataStructures[].subfields[]} — DS subfield definitions</li>
 *   <li>{@code extensionSpecs[]} — array/table definitions</li>
 *   <li>{@code calculationSpecs} result fields — only if not already defined</li>
 * </ol>
 * <p>
 * After building the symbol table, back-propagates resolved types onto
 * {@link IdentifierNode} expression nodes in calculationSpecs.
 */
public class Rpg3SymbolTableBuilder {

    private final Rpg3Content content;
    private final Map<String, SymbolEntry> symbolMap = new LinkedHashMap<>();

    public Rpg3SymbolTableBuilder(Rpg3Content content) {
        this.content = content;
    }

    /**
     * Build the symbol table and back-propagate types.
     * Call this after the IR Builder has fully populated the content.
     */
    public void build() {
        // Phase 1: Scan sources in priority order (first definition wins)
        scanInputSpecs();
        scanDataStructureSubfields();
        scanExtensionSpecs();
        scanCalcSpecResultFields();

        // Phase 2: Populate the content's symbolTable list
        content.setSymbolTable(new ArrayList<>(symbolMap.values()));

        // Phase 3: Back-propagate resolved types onto IdentifierNode expressions
        backPropagateTypes();
    }

    /** Get the built symbol map (for testing). */
    public Map<String, SymbolEntry> getSymbolMap() {
        return Collections.unmodifiableMap(symbolMap);
    }

    // =========================================================================
    // Source 1 (Highest priority): I-spec field definitions
    // =========================================================================

    private void scanInputSpecs() {
        for (InputSpec ispec : content.getInputSpecs()) {
            if (!"fieldDefinition".equals(ispec.getSpecLevel())) continue;
            String fieldName = ispec.getFieldName();
            if (fieldName == null || fieldName.isBlank()) continue;

            String name = fieldName.trim().toUpperCase();
            if (symbolMap.containsKey(name)) continue; // first definition wins

            SymbolEntry entry = new SymbolEntry();
            entry.setName(name);
            entry.setDefinedIn("I-spec");
            entry.setDefinitionLocation(ispec.getLocation());

            // Derive data type from data format or decimal positions
            String dataFormat = ispec.getDataFormat();
            if (dataFormat != null && !dataFormat.isBlank()) {
                entry.setDataType(mapDataFormat(dataFormat.trim()));
            } else if (ispec.getDecimalPositions() != null) {
                entry.setDataType("S"); // Zoned decimal
            } else {
                entry.setDataType("A"); // Character
            }

            // Calculate length from positions
            if (ispec.getFromPosition() != null && ispec.getToPosition() != null) {
                entry.setLength(ispec.getToPosition() - ispec.getFromPosition() + 1);
            }
            entry.setDecimalPositions(ispec.getDecimalPositions());

            symbolMap.put(name, entry);
        }
    }

    // =========================================================================
    // Source 2: Data Structure subfields
    // =========================================================================

    private void scanDataStructureSubfields() {
        for (DataStructure ds : content.getDataStructures()) {
            for (DataStructureSubfield sub : ds.getSubfields()) {
                String subName = sub.getName();
                if (subName == null || subName.isBlank()) continue;

                String name = subName.trim().toUpperCase();
                if (symbolMap.containsKey(name)) continue;

                SymbolEntry entry = new SymbolEntry();
                entry.setName(name);
                entry.setDefinedIn("DS");
                entry.setDefinitionLocation(ds.getLocation());
                entry.setDataStructure(true);
                entry.setDataStructureName(ds.getName());

                if (sub.getDataType() != null && !sub.getDataType().isBlank()) {
                    entry.setDataType(sub.getDataType().trim());
                } else if (sub.getDecimalPositions() != null) {
                    entry.setDataType("S");
                } else {
                    entry.setDataType("A");
                }

                if (sub.getFromPosition() != null && sub.getToPosition() != null) {
                    entry.setLength(sub.getToPosition() - sub.getFromPosition() + 1);
                }
                entry.setDecimalPositions(sub.getDecimalPositions());

                symbolMap.put(name, entry);
            }
        }
    }

    // =========================================================================
    // Source 3: Extension specs (arrays/tables)
    // =========================================================================

    private void scanExtensionSpecs() {
        for (ExtensionSpec espec : content.getExtensionSpecs()) {
            String tableName = espec.getArrayOrTableName();
            if (tableName == null || tableName.isBlank()) continue;

            String name = tableName.trim().toUpperCase();
            if (symbolMap.containsKey(name)) continue;

            SymbolEntry entry = new SymbolEntry();
            entry.setName(name);
            entry.setDefinedIn("E-spec");
            entry.setDefinitionLocation(espec.getLocation());

            String dataFormat = espec.getDataFormat();
            if (dataFormat != null && !dataFormat.isBlank()) {
                entry.setDataType(mapDataFormat(dataFormat.trim()));
            } else if (espec.getDecimalPositions() != null) {
                entry.setDataType("S");
            } else {
                entry.setDataType("A");
            }

            entry.setLength(espec.getEntryLength());
            entry.setDecimalPositions(espec.getDecimalPositions());

            symbolMap.put(name, entry);

            // Also add alternating array if present
            String altName = espec.getAlternateArrayName();
            if (altName != null && !altName.isBlank()) {
                String alt = altName.trim().toUpperCase();
                if (!symbolMap.containsKey(alt)) {
                    SymbolEntry altEntry = new SymbolEntry();
                    altEntry.setName(alt);
                    altEntry.setDefinedIn("E-spec");
                    altEntry.setDefinitionLocation(espec.getLocation());

                    String altFormat = espec.getAlternateDataFormat();
                    if (altFormat != null && !altFormat.isBlank()) {
                        altEntry.setDataType(mapDataFormat(altFormat.trim()));
                    } else if (espec.getAlternateDecimalPositions() != null) {
                        altEntry.setDataType("S");
                    } else {
                        altEntry.setDataType("A");
                    }

                    altEntry.setLength(espec.getAlternateEntryLength());
                    altEntry.setDecimalPositions(espec.getAlternateDecimalPositions());

                    symbolMap.put(alt, altEntry);
                }
            }
        }
    }

    // =========================================================================
    // Source 4 (Lowest priority): C-spec result fields
    // =========================================================================

    private void scanCalcSpecResultFields() {
        for (Object calcObj : content.getCalculationSpecs()) {
            scanCalcNode(calcObj);
        }
    }

    private void scanCalcNode(Object node) {
        if (node instanceof Operation) {
            Operation op = (Operation) node;
            addResultFieldSymbol(op.getResultField(), op.getFieldLength(),
                    op.getDecimalPositions(), op.getLocation());
        } else if (node instanceof ConditionalBlock) {
            ConditionalBlock cb = (ConditionalBlock) node;
            for (Object child : cb.getThenOps()) scanCalcNode(child);
            for (Object child : cb.getElseOps()) scanCalcNode(child);
        } else if (node instanceof DoWhileBlock) {
            for (Object child : ((DoWhileBlock) node).getBodyOps()) scanCalcNode(child);
        } else if (node instanceof DoUntilBlock) {
            for (Object child : ((DoUntilBlock) node).getBodyOps()) scanCalcNode(child);
        } else if (node instanceof DoBlock) {
            for (Object child : ((DoBlock) node).getBodyOps()) scanCalcNode(child);
        } else if (node instanceof SubroutineBlock) {
            for (Object child : ((SubroutineBlock) node).getOperations()) scanCalcNode(child);
        } else if (node instanceof CaseBlock) {
            for (CaseEntry ce : ((CaseBlock) node).getEntries()) {
                if (ce.getSubroutineName() != null) {
                    // CASxx subroutine calls don't have result fields
                }
            }
        }
    }

    private void addResultFieldSymbol(ExpressionNode resultExpr, Integer fieldLength,
                                       Integer decimalPositions, Location location) {
        if (resultExpr == null) return;
        String resultName = extractIdentifierName(resultExpr);
        if (resultName == null || resultName.isBlank()) return;

        String name = resultName.trim().toUpperCase();
        if (symbolMap.containsKey(name)) return; // higher-priority source already defined it

        SymbolEntry entry = new SymbolEntry();
        entry.setName(name);
        entry.setDefinedIn("C-spec");
        entry.setDefinitionLocation(location);

        // Task 4.2: Data type inference
        if (decimalPositions != null) {
            entry.setDataType("S"); // Zoned decimal
        } else {
            entry.setDataType("A"); // Character
        }

        entry.setLength(fieldLength);
        entry.setDecimalPositions(decimalPositions);

        symbolMap.put(name, entry);
    }

    // =========================================================================
    // Task 4.3: Back-propagate resolved types onto IdentifierNode expressions
    // =========================================================================

    private void backPropagateTypes() {
        for (Object calcObj : content.getCalculationSpecs()) {
            backPropagateInNode(calcObj);
        }
    }

    private void backPropagateInNode(Object node) {
        if (node instanceof Operation) {
            Operation op = (Operation) node;
            resolveExprType(op.getFactor1());
            resolveExprType(op.getFactor2());
            resolveExprType(op.getResultField());
        } else if (node instanceof ConditionalBlock) {
            ConditionalBlock cb = (ConditionalBlock) node;
            resolveExprType(cb.getCondition());
            resolveExprType(cb.getComparisonValue());
            for (Object child : cb.getThenOps()) backPropagateInNode(child);
            for (Object child : cb.getElseOps()) backPropagateInNode(child);
        } else if (node instanceof DoWhileBlock) {
            DoWhileBlock dw = (DoWhileBlock) node;
            resolveExprType(dw.getCondition());
            resolveExprType(dw.getComparisonValue());
            for (Object child : dw.getBodyOps()) backPropagateInNode(child);
        } else if (node instanceof DoUntilBlock) {
            DoUntilBlock du = (DoUntilBlock) node;
            resolveExprType(du.getCondition());
            resolveExprType(du.getComparisonValue());
            for (Object child : du.getBodyOps()) backPropagateInNode(child);
        } else if (node instanceof DoBlock) {
            DoBlock db = (DoBlock) node;
            resolveExprType(db.getStartValue());
            resolveExprType(db.getEndValue());
            resolveExprType(db.getIndexVariable());
            for (Object child : db.getBodyOps()) backPropagateInNode(child);
        } else if (node instanceof SubroutineBlock) {
            for (Object child : ((SubroutineBlock) node).getOperations()) {
                backPropagateInNode(child);
            }
        } else if (node instanceof CallSubroutine) {
            resolveExprType(((CallSubroutine) node).getSubroutineName() != null
                    ? null : null); // no expression to resolve
        }
    }

    private void resolveExprType(ExpressionNode expr) {
        if (expr == null) return;

        if (expr instanceof IdentifierNode) {
            IdentifierNode id = (IdentifierNode) expr;
            SymbolEntry sym = symbolMap.get(id.getName() != null
                    ? id.getName().trim().toUpperCase() : "");
            if (sym != null) {
                if (id.getDataType() == null) id.setDataType(sym.getDataType());
                if (id.getLength() == null) id.setLength(sym.getLength());
                if (id.getDecimalPositions() == null) id.setDecimalPositions(sym.getDecimalPositions());
            }
        } else if (expr instanceof ArrayElementNode) {
            ArrayElementNode ae = (ArrayElementNode) expr;
            // Resolve the index expression's type (ArrayElementNode itself has no dataType field)
            resolveExprType(ae.getIndex());
        } else if (expr instanceof BinaryOpNode) {
            BinaryOpNode bin = (BinaryOpNode) expr;
            resolveExprType(bin.getLeft());
            resolveExprType(bin.getRight());
        } else if (expr instanceof UnaryOpNode) {
            resolveExprType(((UnaryOpNode) expr).getOperand());
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** Extract the identifier name from an expression node (if it's an IdentifierNode). */
    private String extractIdentifierName(ExpressionNode expr) {
        if (expr instanceof IdentifierNode) {
            return ((IdentifierNode) expr).getName();
        }
        return null;
    }

    /**
     * Map RPG3 data format codes to AS400 data type codes.
     * <ul>
     *   <li>P → P (Packed decimal)</li>
     *   <li>B → B (Binary)</li>
     *   <li>L → A (Left-adjusted character)</li>
     *   <li>R → A (Right-adjusted character)</li>
     * </ul>
     */
    private String mapDataFormat(String format) {
        if (format == null) return "A";
        switch (format.toUpperCase()) {
            case "P": return "P";
            case "B": return "B";
            case "L": return "A";
            case "R": return "A";
            default: return "A";
        }
    }
}
