package com.as400parser.common.serializer;

import com.as400parser.common.model.IrDocument;
import com.as400parser.rpg3.model.*;
import com.as400parser.rpg3.model.CalcSpec.*;
import com.as400parser.rpg3.model.Rpg3Content.SymbolEntry;

import com.google.gson.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Serializes an {@link IrDocument} to JSON matching the IR template exactly.
 * <p>
 * Uses Gson with:
 * <ul>
 *   <li>{@code prettyPrinting} — indented output for readability</li>
 *   <li>{@code disableHtmlEscaping} — preserve raw source characters</li>
 *   <li>Null fields are omitted from output</li>
 *   <li>Custom type adapters for polymorphic ExpressionNode, CalcNode, and SymbolEntry</li>
 * </ul>
 */
public class IrJsonSerializer {

    private final Gson gson;

    public IrJsonSerializer() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ExpressionNode.class, new ExpressionNodeSerializer())
                .registerTypeHierarchyAdapter(CalcNode.class, new CalcNodeSerializer())
                .registerTypeAdapter(ConditioningIndicator.class, new ConditioningIndicatorSerializer())
                .registerTypeAdapter(SymbolEntry.class, new SymbolEntrySerializer())
                .create();
    }

    /**
     * Serialize the IR document to a JSON string.
     */
    public String serialize(IrDocument document) {
        return gson.toJson(document);
    }

    /**
     * Serialize the IR document and write to a file.
     */
    public void serialize(IrDocument document, Path outputFile) throws IOException {
        String json = serialize(document);
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, json, StandardCharsets.UTF_8);
    }

    // =========================================================================
    // Type Adapter 1: ExpressionNode (8 subtypes)
    // =========================================================================

    static class ExpressionNodeSerializer implements JsonSerializer<ExpressionNode> {
        @Override
        public JsonElement serialize(ExpressionNode src, Type typeOfSrc, JsonSerializationContext ctx) {
            if (src == null) return JsonNull.INSTANCE;

            JsonObject obj = new JsonObject();
            obj.addProperty("nodeType", src.getNodeType());
            addIfNotNull(obj, "rawText", src.getRawText());

            if (src instanceof IdentifierNode id) {
                addIfNotNull(obj, "name", id.getName());
                obj.addProperty("isArray", id.isArray());
                addIfNotNull(obj, "dataType", id.getDataType());
                addIfNotNull(obj, "length", id.getLength(), ctx);
                addIfNotNull(obj, "decimalPositions", id.getDecimalPositions(), ctx);
            } else if (src instanceof LiteralNode lit) {
                addIfNotNull(obj, "value", lit.getValue(), ctx);
                addIfNotNull(obj, "dataType", lit.getDataType());
            } else if (src instanceof ArrayElementNode arr) {
                addIfNotNull(obj, "arrayName", arr.getArrayName());
                addIfNotNull(obj, "index", arr.getIndex(), ExpressionNode.class, ctx);
            } else if (src instanceof BinaryOpNode bin) {
                addIfNotNull(obj, "operator", bin.getOperator());
                addIfNotNull(obj, "left", bin.getLeft(), ExpressionNode.class, ctx);
                addIfNotNull(obj, "right", bin.getRight(), ExpressionNode.class, ctx);
            } else if (src instanceof UnaryOpNode un) {
                addIfNotNull(obj, "operator", un.getOperator());
                addIfNotNull(obj, "operand", un.getOperand(), ExpressionNode.class, ctx);
            } else if (src instanceof IndicatorNode ind) {
                addIfNotNull(obj, "indicator", ind.getIndicator());
                addIfNotNull(obj, "category", ind.getCategory());
            } else if (src instanceof FigurativeConstantNode fig) {
                addIfNotNull(obj, "constant", fig.getConstant());
            } else if (src instanceof SpecialValueNode sv) {
                addIfNotNull(obj, "value", sv.getValue());
            }

            addIfNotNull(obj, "location", src.getLocation(), ctx);
            return obj;
        }
    }

    // =========================================================================
    // Type Adapter 2: CalcNode (10 subtypes)
    // =========================================================================

    static class CalcNodeSerializer implements JsonSerializer<CalcNode> {
        @Override
        public JsonElement serialize(CalcNode src, Type typeOfSrc, JsonSerializationContext ctx) {
            if (src == null) return JsonNull.INSTANCE;

            JsonObject obj = new JsonObject();
            obj.addProperty("nodeType", src.getNodeType());

            // Add common fields
            obj.add("location", ctx.serialize(src.getLocation()));
            obj.addProperty("rawSourceLine", src.getRawSourceLine());
            obj.addProperty("inlineComment", src.getInlineComment());
            obj.addProperty("formType", "C");
            obj.addProperty("controlLevel", src.getControlLevel());

            // Conditioning indicators as nested { indicators: [...] }
            JsonObject ciObj = new JsonObject();
            ciObj.add("indicators", ctx.serialize(src.getConditioningIndicators()));
            obj.add("conditioningIndicators", ciObj);

            // Type-specific fields
            if (src instanceof Operation op) {
                serializeOperation(obj, op, ctx);
            } else if (src instanceof ConditionalBlock cb) {
                serializeConditionalBlock(obj, cb, ctx);
            } else if (src instanceof DoWhileBlock dw) {
                serializeComparisonBlock(obj, dw.getCondition(), dw.getComparisonType(),
                        dw.getComparisonValue(), ctx);
                addResultingIndicators(obj, null, ctx);
                obj.add("bodyOps", serializeCalcList(dw.getBodyOps(), ctx));
            } else if (src instanceof DoUntilBlock du) {
                serializeComparisonBlock(obj, du.getCondition(), du.getComparisonType(),
                        du.getComparisonValue(), ctx);
                addResultingIndicators(obj, null, ctx);
                obj.add("bodyOps", serializeCalcList(du.getBodyOps(), ctx));
            } else if (src instanceof DoBlock db) {
                obj.add("startValue", ctx.serialize(db.getStartValue(), ExpressionNode.class));
                obj.add("endValue", ctx.serialize(db.getEndValue(), ExpressionNode.class));
                obj.add("indexField", ctx.serialize(db.getIndexVariable(), ExpressionNode.class));
                addResultingIndicators(obj, null, ctx);
                obj.add("bodyOps", serializeCalcList(db.getBodyOps(), ctx));
            } else if (src instanceof CaseBlock cas) {
                obj.add("cases", ctx.serialize(cas.getEntries()));
                addResultingIndicators(obj, null, ctx);
            } else if (src instanceof SubroutineBlock sr) {
                obj.addProperty("name", sr.getSubroutineName());
                addResultingIndicators(obj, null, ctx);
                obj.add("operations", serializeCalcList(sr.getOperations(), ctx));
            } else if (src instanceof LabelNode lbl) {
                obj.addProperty("name", lbl.getLabelName());
                addResultingIndicators(obj, null, ctx);
            } else if (src instanceof GotoNode go) {
                obj.addProperty("targetLabel", go.getTargetLabel());
                addResultingIndicators(obj, null, ctx);
            } else if (src instanceof CallSubroutine call) {
                obj.addProperty("subroutineName", call.getSubroutineName());
                addResultingIndicators(obj, null, ctx);
            }

            return obj;
        }

        private void serializeOperation(JsonObject obj, Operation op, JsonSerializationContext ctx) {
            addIfNotNull(obj, "factor1", op.getFactor1(), ExpressionNode.class, ctx);
            addIfNotNull(obj, "opcode", op.getOpcode());
            addIfNotNull(obj, "extendedOpcode", op.getExtendedOpcode());
            addIfNotNull(obj, "factor2", op.getFactor2(), ExpressionNode.class, ctx);
            addIfNotNull(obj, "resultField", op.getResultField(), ExpressionNode.class, ctx);
            addIfNotNull(obj, "fieldLength", op.getFieldLength(), ctx);
            addIfNotNull(obj, "decimalPositions", op.getDecimalPositions(), ctx);
            addResultingIndicators(obj, op.getResultingIndicators(), ctx);
        }

        private void serializeConditionalBlock(JsonObject obj, ConditionalBlock cb,
                                               JsonSerializationContext ctx) {
            serializeComparisonBlock(obj, cb.getCondition(), cb.getComparisonType(),
                    cb.getComparisonValue(), ctx);
            addResultingIndicators(obj, null, ctx);
            obj.add("thenOps", serializeCalcList(cb.getThenOps(), ctx));
            obj.add("elseOps", serializeCalcList(cb.getElseOps(), ctx));
        }

        private void serializeComparisonBlock(JsonObject obj, ExpressionNode condition,
                                              String comparisonType, ExpressionNode comparisonValue,
                                              JsonSerializationContext ctx) {
            addIfNotNull(obj, "condition", condition, ExpressionNode.class, ctx);
            addIfNotNull(obj, "comparisonType", comparisonType);
            addIfNotNull(obj, "comparisonValue", comparisonValue, ExpressionNode.class, ctx);
        }

        private void addResultingIndicators(JsonObject obj, ResultingIndicators ri,
                                           JsonSerializationContext ctx) {
            if (ri != null) {
                obj.add("resultingIndicators", ctx.serialize(ri));
            } else {
                JsonObject riObj = new JsonObject();
                riObj.addProperty("high", "");
                riObj.addProperty("low", "");
                riObj.addProperty("equal", "");
                obj.add("resultingIndicators", riObj);
            }
        }

        private JsonArray serializeCalcList(List<Object> items, JsonSerializationContext ctx) {
            JsonArray arr = new JsonArray();
            if (items != null) {
                for (Object item : items) {
                    if (item instanceof CalcNode cn) {
                        arr.add(ctx.serialize(cn, CalcNode.class));
                    } else {
                        arr.add(ctx.serialize(item));
                    }
                }
            }
            return arr;
        }
    }

    // =========================================================================
    // Type Adapter 3: ConditioningIndicator (negated → not)
    // =========================================================================

    static class ConditioningIndicatorSerializer implements JsonSerializer<ConditioningIndicator> {
        @Override
        public JsonElement serialize(ConditioningIndicator src, Type typeOfSrc,
                                     JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            obj.addProperty("not", src.isNegated());
            obj.addProperty("indicator", src.getIndicator());
            obj.addProperty("position", src.getPosition());
            return obj;
        }
    }

    // =========================================================================
    // Type Adapter 4: SymbolEntry (definedIn mapping + definedAtLine)
    // =========================================================================

    static class SymbolEntrySerializer implements JsonSerializer<SymbolEntry> {
        @Override
        public JsonElement serialize(SymbolEntry src, Type typeOfSrc, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            addIfNotNull(obj, "name", src.getName());
            addIfNotNull(obj, "dataType", src.getDataType());
            addIfNotNull(obj, "length", src.getLength(), ctx);
            addIfNotNull(obj, "decimalPositions", src.getDecimalPositions(), ctx);
            addIfNotNull(obj, "definedIn", mapDefinedIn(src.getDefinedIn()));

            // Extract line number from Location
            Integer line = null;
            if (src.getDefinitionLocation() != null) {
                line = src.getDefinitionLocation().getStartLine();
            }
            addIfNotNull(obj, "definedAtLine", line, ctx);

            obj.addProperty("isDataStructure", src.isDataStructure());
            addIfNotNull(obj, "dataStructureName", src.getDataStructureName());
            return obj;
        }

        private String mapDefinedIn(String internal) {
            if (internal == null) return null;
            return switch (internal) {
                case "I-spec" -> "inputField";
                case "DS" -> "dataStructure";
                case "C-spec" -> "resultField";
                case "E-spec" -> "extensionSpec";
                default -> internal;
            };
        }
    }

    // =========================================================================
    // Null-safe helpers — skip null values in JSON output
    // =========================================================================

    private static void addIfNotNull(JsonObject obj, String key, String value) {
        if (value != null) obj.addProperty(key, value);
    }

    private static void addIfNotNull(JsonObject obj, String key, Object value,
                                     JsonSerializationContext ctx) {
        if (value != null) obj.add(key, ctx.serialize(value));
    }

    private static <T> void addIfNotNull(JsonObject obj, String key, T value,
                                         Class<T> type, JsonSerializationContext ctx) {
        if (value != null) obj.add(key, ctx.serialize(value, type));
    }
}
