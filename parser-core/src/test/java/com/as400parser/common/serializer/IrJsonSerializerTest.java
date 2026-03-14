package com.as400parser.common.serializer;

import com.as400parser.common.model.*;
import com.as400parser.rpg3.model.*;
import com.as400parser.rpg3.model.CalcSpec.*;
import com.as400parser.rpg3.model.Rpg3Content.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

class IrJsonSerializerTest {

    private final IrJsonSerializer serializer = new IrJsonSerializer();
    private static final Location LOC = new Location(1, 1, 1, 80);

    // =========================================================================
    // Basic document serialization
    // =========================================================================

    @Test
    void emptyDocumentSerializesToValidJson() {
        IrDocument doc = new IrDocument();
        String json = serializer.serialize(doc);
        assertThat(json).isNotEmpty();
        // Should be valid JSON
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        assertThat(root.has("metadata")).isTrue();
        assertThat(root.get("metadata").isJsonNull()).isTrue();  // null metadata
    }

    @Test
    void serializeNullsIncluded() {
        IrDocument doc = new IrDocument();
        Metadata meta = new Metadata();
        meta.setSourceType("RPG3");
        doc.setMetadata(meta);
        String json = serializer.serialize(doc);
        // Null fields should be present
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonObject metaObj = root.getAsJsonObject("metadata");
        assertThat(metaObj.has("sourceMember")).isTrue();
        assertThat(metaObj.get("sourceMember").isJsonNull()).isTrue();
        assertThat(metaObj.get("sourceType").getAsString()).isEqualTo("RPG3");
    }

    // =========================================================================
    // ExpressionNode polymorphism
    // =========================================================================

    @Nested
    class ExpressionNodeTests {

        @Test
        void serializesIdentifierNode() {
            IdentifierNode id = new IdentifierNode("CUSTNO");
            id.setDataType("S");
            id.setLength(5);
            id.setDecimalPositions(0);
            id.setRawText("CUSTNO");
            id.setLocation(LOC);

            String json = serializeViaOperation(id);
            JsonObject factor1 = extractFactor1(json);

            assertThat(factor1.get("nodeType").getAsString()).isEqualTo("identifier");
            assertThat(factor1.get("name").getAsString()).isEqualTo("CUSTNO");
            assertThat(factor1.get("isArray").getAsBoolean()).isFalse();
            assertThat(factor1.get("dataType").getAsString()).isEqualTo("S");
            assertThat(factor1.get("length").getAsInt()).isEqualTo(5);
            assertThat(factor1.get("decimalPositions").getAsInt()).isEqualTo(0);
        }

        @Test
        void serializesLiteralNode() {
            LiteralNode lit = new LiteralNode("NOTFND", "A");
            lit.setRawText("'NOTFND'");
            lit.setLocation(LOC);

            String json = serializeViaOperation(lit);
            JsonObject factor1 = extractFactor1(json);

            assertThat(factor1.get("nodeType").getAsString()).isEqualTo("literal");
            assertThat(factor1.get("value").getAsString()).isEqualTo("NOTFND");
            assertThat(factor1.get("dataType").getAsString()).isEqualTo("A");
        }

        @Test
        void serializesNumericLiteralNode() {
            LiteralNode lit = new LiteralNode(100, "S");
            lit.setRawText("100");
            lit.setLocation(LOC);

            String json = serializeViaOperation(lit);
            JsonObject factor1 = extractFactor1(json);

            assertThat(factor1.get("value").getAsInt()).isEqualTo(100);
            assertThat(factor1.get("dataType").getAsString()).isEqualTo("S");
        }

        @Test
        void serializesIndicatorNode() {
            IndicatorNode ind = new IndicatorNode("03", "numeric");
            ind.setRawText("*IN03");
            ind.setLocation(LOC);

            String json = serializeViaOperation(ind);
            JsonObject factor1 = extractFactor1(json);

            assertThat(factor1.get("nodeType").getAsString()).isEqualTo("indicator");
            assertThat(factor1.get("indicator").getAsString()).isEqualTo("03");
            assertThat(factor1.get("category").getAsString()).isEqualTo("numeric");
        }

        @Test
        void serializesFigurativeConstantNode() {
            FigurativeConstantNode fig = new FigurativeConstantNode("*BLANKS");
            fig.setRawText("*BLANKS");
            fig.setLocation(LOC);

            String json = serializeViaOperation(fig);
            JsonObject factor1 = extractFactor1(json);

            assertThat(factor1.get("nodeType").getAsString()).isEqualTo("figurativeConstant");
            assertThat(factor1.get("constant").getAsString()).isEqualTo("*BLANKS");
        }

        @Test
        void serializesSpecialValueNode() {
            SpecialValueNode sv = new SpecialValueNode("UDATE");
            sv.setRawText("UDATE");
            sv.setLocation(LOC);

            String json = serializeViaOperation(sv);
            JsonObject factor1 = extractFactor1(json);

            assertThat(factor1.get("nodeType").getAsString()).isEqualTo("specialValue");
            assertThat(factor1.get("value").getAsString()).isEqualTo("UDATE");
        }

        @Test
        void serializesArrayElementNode() {
            IdentifierNode idx = new IdentifierNode("X");
            idx.setRawText("X");
            ArrayElementNode arr = new ArrayElementNode("ARR", idx);
            arr.setRawText("ARR,X");
            arr.setLocation(LOC);

            String json = serializeViaOperation(arr);
            JsonObject factor1 = extractFactor1(json);

            assertThat(factor1.get("nodeType").getAsString()).isEqualTo("arrayElement");
            assertThat(factor1.get("arrayName").getAsString()).isEqualTo("ARR");
            assertThat(factor1.getAsJsonObject("index").get("nodeType").getAsString()).isEqualTo("identifier");
        }

        @Test
        void serializesBinaryOpNode() {
            IdentifierNode left = new IdentifierNode("A");
            left.setRawText("A");
            IdentifierNode right = new IdentifierNode("B");
            right.setRawText("B");
            BinaryOpNode bin = new BinaryOpNode("AND", left, right);
            bin.setRawText("A AND B");
            bin.setLocation(LOC);

            String json = serializeViaOperation(bin);
            JsonObject factor1 = extractFactor1(json);

            assertThat(factor1.get("nodeType").getAsString()).isEqualTo("binaryOp");
            assertThat(factor1.get("operator").getAsString()).isEqualTo("AND");
            assertThat(factor1.getAsJsonObject("left").get("name").getAsString()).isEqualTo("A");
            assertThat(factor1.getAsJsonObject("right").get("name").getAsString()).isEqualTo("B");
        }

        @Test
        void serializesNullExpressionAsJsonNull() {
            Operation op = new Operation();
            op.setOpcode("SETON");
            op.setLocation(LOC);
            // factor1, factor2, resultField all null

            Rpg3Content content = new Rpg3Content();
            content.getCalculationSpecs().add(op);
            IrDocument doc = new IrDocument();
            doc.setContent(content);
            String json = serializer.serialize(doc);

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject contentObj = root.getAsJsonObject("content");
            JsonObject calcNode = contentObj.getAsJsonArray("calculationSpecs").get(0).getAsJsonObject();
            assertThat(calcNode.get("factor1").isJsonNull()).isTrue();
            assertThat(calcNode.get("factor2").isJsonNull()).isTrue();
            assertThat(calcNode.get("resultField").isJsonNull()).isTrue();
        }

        // Helper: wrap expression in an Operation to serialize through IrDocument
        private String serializeViaOperation(ExpressionNode expr) {
            Operation op = new Operation();
            op.setOpcode("TEST");
            op.setFactor1(expr);
            op.setLocation(LOC);
            Rpg3Content content = new Rpg3Content();
            content.getCalculationSpecs().add(op);
            IrDocument doc = new IrDocument();
            doc.setContent(content);
            return serializer.serialize(doc);
        }

        private JsonObject extractFactor1(String json) {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject content = root.getAsJsonObject("content");
            return content.getAsJsonArray("calculationSpecs")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("factor1");
        }
    }

    // =========================================================================
    // CalcSpec control flow
    // =========================================================================

    @Nested
    class CalcSpecTests {

        @Test
        void serializesDoWhileBlock() {
            DoWhileBlock dw = new DoWhileBlock();
            dw.setComparisonType("EQ");
            IndicatorNode cond = new IndicatorNode("03", "numeric");
            cond.setRawText("*IN03");
            dw.setCondition(cond);
            FigurativeConstantNode val = new FigurativeConstantNode("*OFF");
            val.setRawText("*OFF");
            dw.setComparisonValue(val);
            dw.setLocation(LOC);
            dw.setRawSourceLine("     C           *IN03     DOWEQ*OFF");

            // Add a body operation
            Operation bodyOp = new Operation();
            bodyOp.setOpcode("READ");
            bodyOp.setLocation(LOC);
            dw.getBodyOps().add(bodyOp);

            JsonObject obj = serializeCalcNode(dw);
            assertThat(obj.get("nodeType").getAsString()).isEqualTo("doWhileBlock");
            assertThat(obj.get("comparisonType").getAsString()).isEqualTo("EQ");
            assertThat(obj.getAsJsonObject("condition").get("nodeType").getAsString())
                    .isEqualTo("indicator");
            assertThat(obj.getAsJsonArray("bodyOps")).hasSize(1);
            assertThat(obj.get("formType").getAsString()).isEqualTo("C");
        }

        @Test
        void serializesSubroutineBlock() {
            SubroutineBlock sr = new SubroutineBlock();
            sr.setSubroutineName("DSPREC");
            sr.setLocation(LOC);

            Operation op = new Operation();
            op.setOpcode("MOVEL");
            op.setLocation(LOC);
            sr.getOperations().add(op);

            JsonObject obj = serializeCalcNode(sr);
            assertThat(obj.get("nodeType").getAsString()).isEqualTo("subroutineBlock");
            assertThat(obj.get("name").getAsString()).isEqualTo("DSPREC"); // subroutineName → name
            assertThat(obj.getAsJsonArray("operations")).hasSize(1);
        }

        @Test
        void serializesConditionalBlockWithThenAndElse() {
            ConditionalBlock cb = new ConditionalBlock();
            cb.setComparisonType("GT");
            IdentifierNode cond = new IdentifierNode("COUNT");
            cond.setRawText("COUNT");
            cb.setCondition(cond);
            LiteralNode val = new LiteralNode(0, "S");
            val.setRawText("0");
            cb.setComparisonValue(val);
            cb.setLocation(LOC);

            Operation thenOp = new Operation();
            thenOp.setOpcode("WRITE");
            thenOp.setLocation(LOC);
            cb.getThenOps().add(thenOp);

            Operation elseOp = new Operation();
            elseOp.setOpcode("MOVEL");
            elseOp.setLocation(LOC);
            cb.getElseOps().add(elseOp);

            JsonObject obj = serializeCalcNode(cb);
            assertThat(obj.get("nodeType").getAsString()).isEqualTo("conditionalBlock");
            assertThat(obj.getAsJsonArray("thenOps")).hasSize(1);
            assertThat(obj.getAsJsonArray("elseOps")).hasSize(1);
        }

        @Test
        void serializesConditioningIndicatorsWithNotMapping() {
            Operation op = new Operation();
            op.setOpcode("EXSR");
            op.setLocation(LOC);
            op.getConditioningIndicators().add(
                    new ConditioningIndicator(true, "50", "first"));

            JsonObject obj = serializeCalcNode(op);
            JsonObject ci = obj.getAsJsonObject("conditioningIndicators");
            var indicators = ci.getAsJsonArray("indicators");
            assertThat(indicators).hasSize(1);
            JsonObject ind = indicators.get(0).getAsJsonObject();
            assertThat(ind.get("not").getAsBoolean()).isTrue(); // negated → not
            assertThat(ind.get("indicator").getAsString()).isEqualTo("50");
            assertThat(ind.get("position").getAsString()).isEqualTo("first");
        }

        private JsonObject serializeCalcNode(CalcNode node) {
            Rpg3Content content = new Rpg3Content();
            content.getCalculationSpecs().add(node);
            IrDocument doc = new IrDocument();
            doc.setContent(content);
            String json = serializer.serialize(doc);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            return root.getAsJsonObject("content")
                    .getAsJsonArray("calculationSpecs")
                    .get(0).getAsJsonObject();
        }
    }

    // =========================================================================
    // SymbolEntry serialization
    // =========================================================================

    @Nested
    class SymbolEntryTests {

        @Test
        void mapsDefinedInToIrNames() {
            Rpg3Content content = new Rpg3Content();

            addSymbol(content, "CUSTNO", "S", 5, 0, "I-spec", 9);
            addSymbol(content, "DTEFLD", "A", 8, null, "DS", 13);
            addSymbol(content, "TOTAL", "S", 9, 2, "C-spec", 28);
            addSymbol(content, "ARR", "S", 5, 0, "E-spec", 6);

            IrDocument doc = new IrDocument();
            doc.setContent(content);
            String json = serializer.serialize(doc);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            var symTable = root.getAsJsonObject("content").getAsJsonArray("symbolTable");

            assertThat(symTable.get(0).getAsJsonObject().get("definedIn").getAsString())
                    .isEqualTo("inputField");
            assertThat(symTable.get(1).getAsJsonObject().get("definedIn").getAsString())
                    .isEqualTo("dataStructure");
            assertThat(symTable.get(2).getAsJsonObject().get("definedIn").getAsString())
                    .isEqualTo("resultField");
            assertThat(symTable.get(3).getAsJsonObject().get("definedIn").getAsString())
                    .isEqualTo("extensionSpec");
        }

        @Test
        void extractsDefinedAtLineFromLocation() {
            Rpg3Content content = new Rpg3Content();
            addSymbol(content, "FLD1", "A", 10, null, "I-spec", 42);

            IrDocument doc = new IrDocument();
            doc.setContent(content);
            String json = serializer.serialize(doc);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            var entry = root.getAsJsonObject("content")
                    .getAsJsonArray("symbolTable")
                    .get(0).getAsJsonObject();

            assertThat(entry.get("definedAtLine").getAsInt()).isEqualTo(42);
            assertThat(entry.has("definitionLocation")).isFalse(); // not in IR JSON
        }

        @Test
        void includesIsDataStructureAndDataStructureName() {
            Rpg3Content content = new Rpg3Content();
            SymbolEntry entry = new SymbolEntry();
            entry.setName("DSFLD");
            entry.setDataType("A");
            entry.setLength(10);
            entry.setDefinedIn("DS");
            entry.setDataStructure(true);
            entry.setDataStructureName("MYDS");
            entry.setDefinitionLocation(new Location(5, 5, 1, 80));
            content.getSymbolTable().add(entry);

            IrDocument doc = new IrDocument();
            doc.setContent(content);
            String json = serializer.serialize(doc);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            var sym = root.getAsJsonObject("content")
                    .getAsJsonArray("symbolTable")
                    .get(0).getAsJsonObject();

            assertThat(sym.get("isDataStructure").getAsBoolean()).isTrue();
            assertThat(sym.get("dataStructureName").getAsString()).isEqualTo("MYDS");
        }

        @Test
        void symbolWithNullDecimalShowsNull() {
            Rpg3Content content = new Rpg3Content();
            addSymbol(content, "NAME", "A", 25, null, "I-spec", 10);

            IrDocument doc = new IrDocument();
            doc.setContent(content);
            String json = serializer.serialize(doc);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            var entry = root.getAsJsonObject("content")
                    .getAsJsonArray("symbolTable")
                    .get(0).getAsJsonObject();

            assertThat(entry.get("decimalPositions").isJsonNull()).isTrue();
        }

        private void addSymbol(Rpg3Content content, String name, String dataType,
                               int length, Integer decPos, String definedIn, int line) {
            SymbolEntry entry = new SymbolEntry();
            entry.setName(name);
            entry.setDataType(dataType);
            entry.setLength(length);
            entry.setDecimalPositions(decPos);
            entry.setDefinedIn(definedIn);
            entry.setDefinitionLocation(new Location(line, line, 1, 80));
            content.getSymbolTable().add(entry);
        }
    }
}
