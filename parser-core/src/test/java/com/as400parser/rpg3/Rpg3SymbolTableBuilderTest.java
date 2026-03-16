package com.as400parser.rpg3;

import com.as400parser.common.model.Location;
import com.as400parser.rpg3.model.*;
import com.as400parser.rpg3.model.CalcSpec.*;
import com.as400parser.rpg3.model.Rpg3Content.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

class Rpg3SymbolTableBuilderTest {

    private static final Location LOC = new Location(1, 1, 1, 80);

    // =========================================================================
    // Source 1: I-spec field definitions
    // =========================================================================

    @Test
    void ispecFieldDefinitionAddsSymbol() {
        Rpg3Content content = new Rpg3Content();
        InputSpec ispec = new InputSpec();
        ispec.setSpecLevel("fieldDefinition");
        ispec.setFieldName("CUSTNO");
        ispec.setFromPosition(1);
        ispec.setToPosition(5);
        ispec.setDecimalPositions(0);
        ispec.setLocation(LOC);
        content.getInputSpecs().add(ispec);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        assertThat(content.getSymbolTable()).hasSize(1);
        SymbolEntry entry = content.getSymbolTable().get(0);
        assertThat(entry.getName()).isEqualTo("CUSTNO");
        assertThat(entry.getDataType()).isEqualTo("S"); // decimal specified
        assertThat(entry.getLength()).isEqualTo(5);
        assertThat(entry.getDecimalPositions()).isEqualTo(0);
        assertThat(entry.getDefinedIn()).isEqualTo("I-spec");
        assertThat(entry.isDataStructure()).isFalse();
        assertThat(entry.getDataStructureName()).isNull();
    }

    @Test
    void ispecCharacterFieldNoDecimals() {
        Rpg3Content content = new Rpg3Content();
        InputSpec ispec = new InputSpec();
        ispec.setSpecLevel("fieldDefinition");
        ispec.setFieldName("NAME");
        ispec.setFromPosition(1);
        ispec.setToPosition(20);
        // no decimalPositions → Character type
        ispec.setLocation(LOC);
        content.getInputSpecs().add(ispec);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        assertThat(content.getSymbolTable()).hasSize(1);
        assertThat(content.getSymbolTable().get(0).getDataType()).isEqualTo("A");
    }

    @Test
    void ispecWithPackedDataFormat() {
        Rpg3Content content = new Rpg3Content();
        InputSpec ispec = new InputSpec();
        ispec.setSpecLevel("fieldDefinition");
        ispec.setFieldName("AMOUNT");
        ispec.setFromPosition(1);
        ispec.setToPosition(7);
        ispec.setDataFormat("P");
        ispec.setDecimalPositions(2);
        ispec.setLocation(LOC);
        content.getInputSpecs().add(ispec);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        assertThat(content.getSymbolTable().get(0).getDataType()).isEqualTo("P");
    }

    // =========================================================================
    // Source 2: DS subfields
    // =========================================================================

    @Test
    void dsSubfieldAddsSymbol() {
        Rpg3Content content = new Rpg3Content();
        DataStructure ds = new DataStructure();
        ds.setName("MYDS");
        ds.setLocation(LOC);
        DataStructureSubfield sub = new DataStructureSubfield();
        sub.setFieldName("DSFLD1");
        sub.setFromPosition(1);
        sub.setToPosition(10);
        ds.getSubfields().add(sub);
        content.getDataStructures().add(ds);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        assertThat(content.getSymbolTable()).hasSize(1);
        SymbolEntry entry = content.getSymbolTable().get(0);
        assertThat(entry.getName()).isEqualTo("DSFLD1");
        assertThat(entry.getDefinedIn()).isEqualTo("DS");
        assertThat(entry.getDataType()).isEqualTo("A"); // no decimal
        assertThat(entry.getLength()).isEqualTo(10);
        assertThat(entry.isDataStructure()).isTrue();
        assertThat(entry.getDataStructureName()).isEqualTo("MYDS");
    }

    // =========================================================================
    // Source 3: E-spec arrays
    // =========================================================================

    @Test
    void extensionSpecAddsSymbol() {
        Rpg3Content content = new Rpg3Content();
        ExtensionSpec espec = new ExtensionSpec();
        espec.setArrayOrTableName("NAMES");
        espec.setEntryLength(20);
        espec.setDecimalPositions(null);
        espec.setLocation(LOC);
        content.getExtensionSpecs().add(espec);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        assertThat(content.getSymbolTable()).hasSize(1);
        SymbolEntry entry = content.getSymbolTable().get(0);
        assertThat(entry.getName()).isEqualTo("NAMES");
        assertThat(entry.getDefinedIn()).isEqualTo("E-spec");
        assertThat(entry.getDataType()).isEqualTo("A");
        assertThat(entry.getLength()).isEqualTo(20);
    }

    @Test
    void extensionSpecWithAlternatingArray() {
        Rpg3Content content = new Rpg3Content();
        ExtensionSpec espec = new ExtensionSpec();
        espec.setArrayOrTableName("TAB1");
        espec.setEntryLength(5);
        espec.setAlternateArrayName("TAB2");
        espec.setAlternateEntryLength(10);
        espec.setAlternateDecimalPositions(2);
        espec.setLocation(LOC);
        content.getExtensionSpecs().add(espec);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        assertThat(content.getSymbolTable()).hasSize(2);
        Map<String, SymbolEntry> map = builder.getSymbolMap();
        assertThat(map.get("TAB1").getDataType()).isEqualTo("A");
        assertThat(map.get("TAB2").getDataType()).isEqualTo("S"); // has decimal
        assertThat(map.get("TAB2").getLength()).isEqualTo(10);
    }

    // =========================================================================
    // Source 4: C-spec result fields
    // =========================================================================

    @Test
    void cspecResultFieldAddsSymbol() {
        Rpg3Content content = new Rpg3Content();
        Operation op = new Operation();
        op.setOpcode("MOVE");
        op.setResultField(new IdentifierNode("RESULT"));
        op.setFieldLength(10);
        op.setDecimalPositions(null);
        op.setLocation(LOC);
        content.getCalculationSpecs().add(op);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        assertThat(content.getSymbolTable()).hasSize(1);
        SymbolEntry entry = content.getSymbolTable().get(0);
        assertThat(entry.getName()).isEqualTo("RESULT");
        assertThat(entry.getDefinedIn()).isEqualTo("C-spec");
        assertThat(entry.getDataType()).isEqualTo("A"); // no decimal → Character
        assertThat(entry.getLength()).isEqualTo(10);
    }

    @Test
    void cspecResultFieldWithDecimalIsZoned() {
        Rpg3Content content = new Rpg3Content();
        Operation op = new Operation();
        op.setOpcode("Z-ADD");
        op.setResultField(new IdentifierNode("TOTAL"));
        op.setFieldLength(7);
        op.setDecimalPositions(2);
        op.setLocation(LOC);
        content.getCalculationSpecs().add(op);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        assertThat(content.getSymbolTable().get(0).getDataType()).isEqualTo("S"); // Zoned
    }

    // =========================================================================
    // Conflict resolution: I-spec wins over C-spec
    // =========================================================================

    @Test
    void ispecWinsOverCspec() {
        Rpg3Content content = new Rpg3Content();

        // I-spec defines CUSTNO as Packed
        InputSpec ispec = new InputSpec();
        ispec.setSpecLevel("fieldDefinition");
        ispec.setFieldName("CUSTNO");
        ispec.setFromPosition(1);
        ispec.setToPosition(5);
        ispec.setDataFormat("P");
        ispec.setDecimalPositions(0);
        ispec.setLocation(LOC);
        content.getInputSpecs().add(ispec);

        // C-spec also uses CUSTNO as result → should NOT override
        Operation op = new Operation();
        op.setOpcode("Z-ADD");
        op.setResultField(new IdentifierNode("CUSTNO"));
        op.setFieldLength(7);
        op.setDecimalPositions(2);
        op.setLocation(LOC);
        content.getCalculationSpecs().add(op);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        assertThat(content.getSymbolTable()).hasSize(1);
        SymbolEntry entry = content.getSymbolTable().get(0);
        assertThat(entry.getDefinedIn()).isEqualTo("I-spec"); // I-spec wins
        assertThat(entry.getDataType()).isEqualTo("P"); // Packed from I-spec, not S from C-spec
        assertThat(entry.getLength()).isEqualTo(5); // from I-spec positions
    }

    // =========================================================================
    // Task 4.3: Back-propagation
    // =========================================================================

    @Test
    void backPropagatesTypeOntoIdentifierNode() {
        Rpg3Content content = new Rpg3Content();

        // Define field in I-spec
        InputSpec ispec = new InputSpec();
        ispec.setSpecLevel("fieldDefinition");
        ispec.setFieldName("AMT");
        ispec.setFromPosition(1);
        ispec.setToPosition(7);
        ispec.setDecimalPositions(2);
        ispec.setLocation(LOC);
        content.getInputSpecs().add(ispec);

        // Use AMT in a C-spec factor (IdentifierNode with no type yet)
        IdentifierNode idNode = new IdentifierNode("AMT");
        Operation op = new Operation();
        op.setOpcode("ADD");
        op.setFactor1(idNode);
        op.setLocation(LOC);
        content.getCalculationSpecs().add(op);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        // After build, the IdentifierNode should have resolved types
        assertThat(idNode.getDataType()).isEqualTo("S"); // Zoned (decimal specified)
        assertThat(idNode.getLength()).isEqualTo(7);
        assertThat(idNode.getDecimalPositions()).isEqualTo(2);
    }

    @Test
    void backPropagateSkipsAlreadyResolvedFields() {
        Rpg3Content content = new Rpg3Content();

        InputSpec ispec = new InputSpec();
        ispec.setSpecLevel("fieldDefinition");
        ispec.setFieldName("FLD");
        ispec.setFromPosition(1);
        ispec.setToPosition(5);
        ispec.setLocation(LOC);
        content.getInputSpecs().add(ispec);

        // Identifier already has a type set (from earlier resolution)
        IdentifierNode idNode = new IdentifierNode("FLD");
        idNode.setDataType("P"); // already set
        Operation op = new Operation();
        op.setOpcode("MOVE");
        op.setFactor2(idNode);
        op.setLocation(LOC);
        content.getCalculationSpecs().add(op);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        // Should NOT overwrite existing type
        assertThat(idNode.getDataType()).isEqualTo("P");
    }

    // =========================================================================
    // Edge cases
    // =========================================================================

    @Test
    void emptyContentProducesEmptySymbolTable() {
        Rpg3Content content = new Rpg3Content();
        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();
        assertThat(content.getSymbolTable()).isEmpty();
    }

    @Test
    void caseInsensitiveFieldNames() {
        Rpg3Content content = new Rpg3Content();

        InputSpec ispec = new InputSpec();
        ispec.setSpecLevel("fieldDefinition");
        ispec.setFieldName("custno"); // lowercase
        ispec.setFromPosition(1);
        ispec.setToPosition(5);
        ispec.setLocation(LOC);
        content.getInputSpecs().add(ispec);

        Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
        builder.build();

        // Should be stored uppercase
        assertThat(content.getSymbolTable().get(0).getName()).isEqualTo("CUSTNO");
    }

    @Nested
    class NestedCalcSpecs {

        @Test
        void scansInsideSubroutineBlock() {
            Rpg3Content content = new Rpg3Content();
            SubroutineBlock sr = new SubroutineBlock();
            sr.setSubroutineName("CALC");
            sr.setLocation(LOC);

            Operation op = new Operation();
            op.setOpcode("Z-ADD");
            op.setResultField(new IdentifierNode("SRFLD"));
            op.setFieldLength(5);
            op.setDecimalPositions(2);
            op.setLocation(LOC);
            sr.getOperations().add(op);

            content.getCalculationSpecs().add(sr);

            Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
            builder.build();

            assertThat(content.getSymbolTable()).hasSize(1);
            assertThat(content.getSymbolTable().get(0).getName()).isEqualTo("SRFLD");
        }

        @Test
        void scansInsideConditionalBlock() {
            Rpg3Content content = new Rpg3Content();
            ConditionalBlock cb = new ConditionalBlock();
            cb.setLocation(LOC);

            Operation thenOp = new Operation();
            thenOp.setOpcode("MOVE");
            thenOp.setResultField(new IdentifierNode("THENFLD"));
            thenOp.setFieldLength(8);
            thenOp.setLocation(LOC);
            cb.getThenOps().add(thenOp);

            Operation elseOp = new Operation();
            elseOp.setOpcode("MOVE");
            elseOp.setResultField(new IdentifierNode("ELSEFLD"));
            elseOp.setFieldLength(8);
            elseOp.setLocation(LOC);
            cb.getElseOps().add(elseOp);

            content.getCalculationSpecs().add(cb);

            Rpg3SymbolTableBuilder builder = new Rpg3SymbolTableBuilder(content);
            builder.build();

            assertThat(content.getSymbolTable()).hasSize(2);
        }
    }
}
