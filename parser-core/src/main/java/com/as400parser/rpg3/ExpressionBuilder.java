package com.as400parser.rpg3;

import com.as400parser.common.model.Location;
import com.as400parser.rpg3.model.*;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Converts raw factor text from C-spec columns into ExpressionNode AST nodes.
 * <p>
 * Detection order (from knowledge doc §2):
 * <ol>
 *   <li>blank/empty → null</li>
 *   <li>figurative constants → FigurativeConstantNode</li>
 *   <li>*INxx → IndicatorNode</li>
 *   <li>*IN → SpecialValueNode</li>
 *   <li>UDATE/UMONTH/UDAY/UYEAR/PAGE → SpecialValueNode</li>
 *   <li>quoted string → LiteralNode</li>
 *   <li>numeric → LiteralNode</li>
 *   <li>contains comma (ARR,X) → ArrayElementNode</li>
 *   <li>otherwise → IdentifierNode</li>
 * </ol>
 */
public class ExpressionBuilder {

    private static final Set<String> FIGURATIVE_CONSTANTS = Set.of(
        "*BLANKS", "*BLANK", "*ZEROS", "*ZERO",
        "*HIVAL", "*LOVAL", "*ALL", "*ON", "*OFF"
    );

    private static final Set<String> SPECIAL_VALUES = Set.of(
        "UDATE", "UMONTH", "UDAY", "UYEAR",
        "PAGE", "PAGE1", "PAGE2", "PAGE3", "PAGE4", "PAGE5", "PAGE6", "PAGE7"
    );

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^-?\\d+\\.?\\d*$");

    /**
     * Build an expression node from raw factor text.
     *
     * @param rawText  the raw text from a C-spec column (factor1, factor2, or result)
     * @param location the source location
     * @return an ExpressionNode, or null if the factor is blank
     */
    public static ExpressionNode build(String rawText, Location location) {
        if (rawText == null) return null;

        String trimmed = rawText.trim();

        // 1. blank/empty → null
        if (trimmed.isEmpty()) return null;

        String upper = trimmed.toUpperCase();

        // 2. figurative constants
        if (FIGURATIVE_CONSTANTS.contains(upper)) {
            FigurativeConstantNode node = new FigurativeConstantNode();
            node.setConstant(upper);
            node.setRawText(trimmed);
            node.setLocation(location);
            return node;
        }

        // 2.5. NOT(expr) → UnaryOpNode (e.g., NOT(*IN50), NOT(FLGON))
        if (upper.startsWith("NOT(") && upper.endsWith(")")) {
            String inner = trimmed.substring(4, trimmed.length() - 1).trim();
            ExpressionNode operand = build(inner, location);
            UnaryOpNode node = new UnaryOpNode("NOT", operand);
            node.setRawText(trimmed);
            node.setLocation(location);
            return node;
        }

        // 3. *INxx (length >= 4, starts with *IN)
        if (upper.startsWith("*IN") && upper.length() >= 4) {
            IndicatorNode node = new IndicatorNode();
            node.setIndicator(upper.substring(3)); // e.g., "01", "LR", "H1"
            node.setCategory(categorizeIndicator(upper.substring(3)));
            node.setRawText(trimmed);
            node.setLocation(location);
            return node;
        }

        // 4. *IN exactly → SpecialValueNode (the indicator array)
        if (upper.equals("*IN")) {
            SpecialValueNode node = new SpecialValueNode();
            node.setValue("*IN");
            node.setRawText(trimmed);
            node.setLocation(location);
            return node;
        }

        // 5. UDATE/UMONTH/UDAY/UYEAR/PAGE
        if (SPECIAL_VALUES.contains(upper)) {
            SpecialValueNode node = new SpecialValueNode();
            node.setValue(upper);
            node.setRawText(trimmed);
            node.setLocation(location);
            return node;
        }

        // 6. quoted string (starts with ')
        if (trimmed.startsWith("'")) {
            LiteralNode node = new LiteralNode();
            // Strip surrounding quotes
            String value = trimmed;
            if (trimmed.endsWith("'") && trimmed.length() > 1) {
                value = trimmed.substring(1, trimmed.length() - 1);
            }
            node.setValue(value);
            node.setDataType("A"); // character literal
            node.setRawText(trimmed);
            node.setLocation(location);
            return node;
        }

        // 7. numeric
        if (NUMERIC_PATTERN.matcher(trimmed).matches()) {
            LiteralNode node = new LiteralNode();
            if (trimmed.contains(".")) {
                node.setValue(Double.parseDouble(trimmed));
                node.setDataType("S");
            } else {
                node.setValue(Integer.parseInt(trimmed));
                node.setDataType("S");
            }
            node.setRawText(trimmed);
            node.setLocation(location);
            return node;
        }

        // 8. contains comma (array element: ARR,X or ARR,3)
        if (trimmed.contains(",")) {
            int commaPos = trimmed.indexOf(',');
            String arrayName = trimmed.substring(0, commaPos).trim();
            String indexText = trimmed.substring(commaPos + 1).trim();

            ArrayElementNode node = new ArrayElementNode();
            node.setArrayName(arrayName);
            // Recursively build the index expression
            node.setIndex(build(indexText, location));
            node.setRawText(trimmed);
            node.setLocation(location);
            return node;
        }

        // 9. otherwise → identifier
        IdentifierNode node = new IdentifierNode();
        node.setName(trimmed);
        node.setRawText(trimmed);
        node.setLocation(location);
        return node;
    }

    /**
     * Categorize an indicator suffix into a semantic category.
     */
    private static String categorizeIndicator(String suffix) {
        if (suffix.length() == 2) {
            char first = suffix.charAt(0);
            char second = suffix.charAt(1);

            // Numeric indicators: 01-99
            if (Character.isDigit(first) && Character.isDigit(second)) {
                return "numeric";
            }

            // Control level: L0-L9
            if ((first == 'L' || first == 'l') && Character.isDigit(second)) {
                return "control";
            }

            // Halt: H1-H9
            if ((first == 'H' || first == 'h') && Character.isDigit(second)) {
                return "halt";
            }

            // Overflow: OA-OG, OV
            if (first == 'O' || first == 'o') {
                return "overflow";
            }

            // LR, MR, KA-KN, RT
            if (suffix.equalsIgnoreCase("LR")) return "control";
            if (suffix.equalsIgnoreCase("MR")) return "matching";
            if (suffix.equalsIgnoreCase("RT")) return "return";

            // Function keys: KA-KN, KP-KY
            if (first == 'K' || first == 'k') {
                return "functionKey";
            }

            // External: U1-U8
            if ((first == 'U' || first == 'u') && Character.isDigit(second)) {
                return "external";
            }
        }

        // Fallback
        return "other";
    }
}
