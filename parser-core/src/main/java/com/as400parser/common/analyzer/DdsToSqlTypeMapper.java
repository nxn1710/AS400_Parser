package com.as400parser.common.analyzer;

/**
 * Utility to map AS400 DDS Data Types to relational SQL data types.
 */
public class DdsToSqlTypeMapper {

    /**
     * Maps a DDS field definition to an SQL type string.
     *
     * @param ddsType          The DDS data type (e.g. A, P, S, B, L, Z, T).
     * @param length           The field length.
     * @param decimalPositions The number of decimal positions (can be null for non-numeric/integer).
     * @return The corresponding SQL type string (e.g., VARCHAR(50), NUMERIC(9,2)).
     */
    public static String mapToSqlType(String ddsType, int length, Integer decimalPositions) {
        if (ddsType == null || ddsType.isEmpty()) {
            // Default to string if empty, usually indicates simple character field
            return createVarchar(length);
        }

        String typeUpper = ddsType.toUpperCase();
        switch (typeUpper) {
            case "A": // Alpha (Alphanumeric)
                return createVarchar(length);

            case "P": // Packed Decimal
            case "S": // Zoned Decimal
                return createNumeric(length, decimalPositions);

            case "B": // Binary
                return mapBinary(length, decimalPositions);

            case "L": // Date
                return "DATE";

            case "T": // Time
                return "TIME";

            case "Z": // Timestamp
                return "TIMESTAMP";

            case "F": // Floating Point
                return "DOUBLE PRECISION";

            case "H": // Hexadecimal
                return "BYTEA";

            default:
                // Graceful fallback for unknown types
                System.err.println("[WARNING] Unknown DDS Type '" + ddsType + "', falling back to VARCHAR.");
                return createVarchar(length);
        }
    }

    private static String createVarchar(int length) {
        int safeLength = length > 0 ? length : 1; // Safeguard against 0 length
        return "VARCHAR(" + safeLength + ")";
    }

    private static String createNumeric(int length, Integer decimalPositions) {
        int safeLength = length > 0 ? length : 1;
        int safeDecimals = (decimalPositions != null && decimalPositions >= 0) ? decimalPositions : 0;
        
        if (safeDecimals == 0) {
            return "NUMERIC(" + safeLength + ")";
        }
        return "NUMERIC(" + safeLength + ", " + safeDecimals + ")";
    }

    private static String mapBinary(int length, Integer decimalPositions) {
        // Binary field lengths typically represent the byte length, not digits.
        // E.g., 2 bytes = SMALLINT, 4 bytes = INTEGER, 8 bytes = BIGINT.
        // Some systems encode digits in length for binary; if length <= 4, smallint.
        if (decimalPositions != null && decimalPositions > 0) {
            // Unlikely in DDS, but fallback just in case
            return createNumeric(length, decimalPositions);
        }
        if (length <= 4) {
            return "SMALLINT";
        } else if (length <= 9) {
            return "INTEGER";
        } else {
            return "BIGINT";
        }
    }
}
