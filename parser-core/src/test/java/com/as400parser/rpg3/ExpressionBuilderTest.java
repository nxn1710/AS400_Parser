package com.as400parser.rpg3;

import com.as400parser.common.model.Location;
import com.as400parser.rpg3.model.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ExpressionBuilder} — validates the 9-step detection order.
 */
class ExpressionBuilderTest {

    private static final Location LOC = new Location(1, 1, 1, 80);

    // =========================================================================
    // Step 1: blank/empty → null
    // =========================================================================
    @Test
    void blankReturnsNull() {
        assertThat(ExpressionBuilder.build("", LOC)).isNull();
        assertThat(ExpressionBuilder.build("   ", LOC)).isNull();
        assertThat(ExpressionBuilder.build(null, LOC)).isNull();
    }

    // =========================================================================
    // Step 2: Figurative constants
    // =========================================================================
    @Nested
    class FigurativeConstants {
        @Test
        void blanks() {
            ExpressionNode node = ExpressionBuilder.build("*BLANKS", LOC);
            assertThat(node).isInstanceOf(FigurativeConstantNode.class);
            assertThat(((FigurativeConstantNode) node).getConstant()).isEqualTo("*BLANKS");
        }

        @Test
        void zeros() {
            ExpressionNode node = ExpressionBuilder.build("*ZEROS", LOC);
            assertThat(node).isInstanceOf(FigurativeConstantNode.class);
            assertThat(((FigurativeConstantNode) node).getConstant()).isEqualTo("*ZEROS");
        }

        @Test
        void hival() {
            ExpressionNode node = ExpressionBuilder.build("*HIVAL", LOC);
            assertThat(node).isInstanceOf(FigurativeConstantNode.class);
        }

        @Test
        void loval() {
            ExpressionNode node = ExpressionBuilder.build("*LOVAL", LOC);
            assertThat(node).isInstanceOf(FigurativeConstantNode.class);
        }

        @Test
        void all() {
            ExpressionNode node = ExpressionBuilder.build("*ALL", LOC);
            assertThat(node).isInstanceOf(FigurativeConstantNode.class);
        }

        @Test
        void on() {
            ExpressionNode node = ExpressionBuilder.build("*ON", LOC);
            assertThat(node).isInstanceOf(FigurativeConstantNode.class);
        }

        @Test
        void off() {
            ExpressionNode node = ExpressionBuilder.build("*OFF", LOC);
            assertThat(node).isInstanceOf(FigurativeConstantNode.class);
        }

        @Test
        void caseInsensitive() {
            assertThat(ExpressionBuilder.build("*blanks", LOC)).isInstanceOf(FigurativeConstantNode.class);
            assertThat(ExpressionBuilder.build("*Zeros", LOC)).isInstanceOf(FigurativeConstantNode.class);
        }
    }

    // =========================================================================
    // Step 3: *INxx → IndicatorNode
    // =========================================================================
    @Nested
    class Indicators {
        @Test
        void numericIndicator() {
            ExpressionNode node = ExpressionBuilder.build("*IN01", LOC);
            assertThat(node).isInstanceOf(IndicatorNode.class);
            assertThat(((IndicatorNode) node).getIndicator()).isEqualTo("01");
            assertThat(((IndicatorNode) node).getCategory()).isEqualTo("numeric");
        }

        @Test
        void lastRecordIndicator() {
            ExpressionNode node = ExpressionBuilder.build("*INLR", LOC);
            assertThat(node).isInstanceOf(IndicatorNode.class);
            assertThat(((IndicatorNode) node).getIndicator()).isEqualTo("LR");
        }

        @Test
        void haltIndicator() {
            ExpressionNode node = ExpressionBuilder.build("*INH1", LOC);
            assertThat(node).isInstanceOf(IndicatorNode.class);
            assertThat(((IndicatorNode) node).getCategory()).isEqualTo("halt");
        }

        @Test
        void overflowIndicator() {
            ExpressionNode node = ExpressionBuilder.build("*INOA", LOC);
            assertThat(node).isInstanceOf(IndicatorNode.class);
            assertThat(((IndicatorNode) node).getCategory()).isEqualTo("overflow");
        }
    }

    // =========================================================================
    // Step 4: *IN exactly → SpecialValueNode
    // =========================================================================
    @Test
    void starInAsSpecialValue() {
        ExpressionNode node = ExpressionBuilder.build("*IN", LOC);
        assertThat(node).isInstanceOf(SpecialValueNode.class);
        assertThat(((SpecialValueNode) node).getValue()).isEqualTo("*IN");
    }

    // =========================================================================
    // Step 5: UDATE/UMONTH/PAGE → SpecialValueNode
    // =========================================================================
    @Test
    void udate() {
        ExpressionNode node = ExpressionBuilder.build("UDATE", LOC);
        assertThat(node).isInstanceOf(SpecialValueNode.class);
    }

    @Test
    void page() {
        ExpressionNode node = ExpressionBuilder.build("PAGE", LOC);
        assertThat(node).isInstanceOf(SpecialValueNode.class);
    }

    @Test
    void page3() {
        ExpressionNode node = ExpressionBuilder.build("PAGE3", LOC);
        assertThat(node).isInstanceOf(SpecialValueNode.class);
    }

    // =========================================================================
    // Step 6: Quoted string → LiteralNode
    // =========================================================================
    @Test
    void quotedString() {
        ExpressionNode node = ExpressionBuilder.build("'HELLO'", LOC);
        assertThat(node).isInstanceOf(LiteralNode.class);
        assertThat(((LiteralNode) node).getValue()).isEqualTo("HELLO");
        assertThat(((LiteralNode) node).getDataType()).isEqualTo("A");
    }

    // =========================================================================
    // Step 7: Numeric → LiteralNode
    // =========================================================================
    @Test
    void integerLiteral() {
        ExpressionNode node = ExpressionBuilder.build("42", LOC);
        assertThat(node).isInstanceOf(LiteralNode.class);
        assertThat(((LiteralNode) node).getValue()).isEqualTo(42);
    }

    @Test
    void decimalLiteral() {
        ExpressionNode node = ExpressionBuilder.build("3.14", LOC);
        assertThat(node).isInstanceOf(LiteralNode.class);
        assertThat(((LiteralNode) node).getDataType()).isEqualTo("S");
    }

    // =========================================================================
    // Step 8: Array element (contains comma)
    // =========================================================================
    @Test
    void arrayElementWithFieldIndex() {
        ExpressionNode node = ExpressionBuilder.build("ARR,X", LOC);
        assertThat(node).isInstanceOf(ArrayElementNode.class);
        ArrayElementNode arr = (ArrayElementNode) node;
        assertThat(arr.getArrayName()).isEqualTo("ARR");
        assertThat(arr.getIndex()).isInstanceOf(IdentifierNode.class);
        assertThat(((IdentifierNode) arr.getIndex()).getName()).isEqualTo("X");
    }

    @Test
    void arrayElementWithNumericIndex() {
        ExpressionNode node = ExpressionBuilder.build("TAB,3", LOC);
        assertThat(node).isInstanceOf(ArrayElementNode.class);
        ArrayElementNode arr = (ArrayElementNode) node;
        assertThat(arr.getArrayName()).isEqualTo("TAB");
        assertThat(arr.getIndex()).isInstanceOf(LiteralNode.class);
    }

    // =========================================================================
    // Step 9: Identifier (fallback)
    // =========================================================================
    @Test
    void simpleIdentifier() {
        ExpressionNode node = ExpressionBuilder.build("CUSTNO", LOC);
        assertThat(node).isInstanceOf(IdentifierNode.class);
        assertThat(((IdentifierNode) node).getName()).isEqualTo("CUSTNO");
    }

    // =========================================================================
    // Edge cases: detection order correctness
    // =========================================================================
    @Test
    void starOnIsFigurativeNotIndicator() {
        // *ON should be figurative constant, NOT IndicatorNode(*N)
        ExpressionNode node = ExpressionBuilder.build("*ON", LOC);
        assertThat(node).isInstanceOf(FigurativeConstantNode.class);
    }

    @Test
    void starInIsSpecialNotIdentifier() {
        // *IN should be SpecialValueNode (indicator array), not IdentifierNode
        ExpressionNode node = ExpressionBuilder.build("*IN", LOC);
        assertThat(node).isInstanceOf(SpecialValueNode.class);
    }

    @Test
    void starIn01IsIndicatorNotSpecial() {
        // *IN01 should be IndicatorNode, not SpecialValueNode
        ExpressionNode node = ExpressionBuilder.build("*IN01", LOC);
        assertThat(node).isInstanceOf(IndicatorNode.class);
    }

    @Test
    void preservesRawText() {
        ExpressionNode node = ExpressionBuilder.build("  CUSTNO  ", LOC);
        assertThat(node.getRawText()).isEqualTo("CUSTNO");
    }

    // =========================================================================
    // Step 2.5: NOT(expr) → UnaryOpNode (GAP-3)
    // =========================================================================
    @Test
    void notWithIdentifier() {
        ExpressionNode node = ExpressionBuilder.build("NOT(FLGON)", LOC);
        assertThat(node).isInstanceOf(UnaryOpNode.class);
        UnaryOpNode unary = (UnaryOpNode) node;
        assertThat(unary.getOperator()).isEqualTo("NOT");
        assertThat(unary.getOperand()).isInstanceOf(IdentifierNode.class);
        assertThat(((IdentifierNode) unary.getOperand()).getName()).isEqualTo("FLGON");
    }

    @Test
    void notWithIndicator() {
        ExpressionNode node = ExpressionBuilder.build("NOT(*IN50)", LOC);
        assertThat(node).isInstanceOf(UnaryOpNode.class);
        UnaryOpNode unary = (UnaryOpNode) node;
        assertThat(unary.getOperator()).isEqualTo("NOT");
        assertThat(unary.getOperand()).isInstanceOf(IndicatorNode.class);
    }

    @Test
    void notCaseInsensitive() {
        ExpressionNode node = ExpressionBuilder.build("not(X)", LOC);
        assertThat(node).isInstanceOf(UnaryOpNode.class);
    }

    // =========================================================================
    // Step 7: Negative numbers stay as LiteralNode (not UnaryOpNode)
    // =========================================================================
    @Test
    void negativeNumericIsLiteral() {
        ExpressionNode node = ExpressionBuilder.build("-42", LOC);
        assertThat(node).isInstanceOf(LiteralNode.class);
        assertThat(((LiteralNode) node).getValue()).isEqualTo(-42);
    }
}
