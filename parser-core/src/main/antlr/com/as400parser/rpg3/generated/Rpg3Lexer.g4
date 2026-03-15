/**
 * RPG III Lexer Grammar
 * 
 * A purpose-built ANTLR4 lexer for IBM RPG III fixed-format source code.
 * All column positions follow the RPG III reference manual (SC21-9001).
 *
 * IMPORTANT: Input source lines MUST be space-padded to 80 characters.
 *            Use a pre-processor to pad short lines before parsing.
 *
 * RPG III line format (80 columns):
 *   Cols 1-5:   Sequence number (ignored)
 *   Col  6:     Form type (H, F, E, I, C, O, or * for comment)
 *   Cols 7-80:  Specification-dependent content
 *
 * Key difference from RPG IV:
 *   RPG III C-spec: Operation=28-32, Factor2=33-42, Result=43-48
 *   RPG IV  C-spec: Operation=26-35, Factor2=36-49, Result=50-63
 */

lexer grammar Rpg3Lexer;

// =================================================================
// Default Mode: Detect line type from first 6 characters
// =================================================================

// End-of-source marker: ** in positions 1-2
END_SOURCE : '**' ~[\r\n]* EOL_CHAR -> pushMode(EndOfSourceMode);

// Skip leading 5-char sequence number, then detect spec type
LEAD_WS5 : '     ' -> pushMode(SpecSelector), skip;

// Sequence number with content (non-blank first 5 chars)
LEAD_WS5_Comments : WORD5 -> pushMode(SpecSelector), channel(HIDDEN);

// Blank lines
EMPTY_NEWLINE : [ ]* '\r'? '\n' -> skip;

fragment WORD5 : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n];
fragment EOL_CHAR : '\r'? '\n';


// =================================================================
// Spec Selector Mode: Column 6 determines specification type
// =================================================================
mode SpecSelector;

// Comment line: column 6 = spec type letter, column 7 = '*'
COMMENT_SPEC : ~[*/\r\n] '*' -> popMode, pushMode(CommentMode), channel(HIDDEN);

// Standalone comment: column 6 = '*'
COMMENT_SPEC_STAR : '*' -> popMode, pushMode(CommentMode), channel(HIDDEN);

// H-spec (Header/Control)
HS_FIXED : [hH] -> popMode, pushMode(FIXED_HSpec);

// F-spec (File)
FS_FIXED : [fF] -> popMode, pushMode(FIXED_FSpec);

// E-spec (Extension) - RPG III only
ES_FIXED : [eE] -> popMode, pushMode(FIXED_ESpec);

// I-spec (Input)
IS_FIXED : [iI] -> popMode, pushMode(FIXED_ISpec);

// C-spec (Calculation) - NO indicator modes pushed here!
// CS_ControlLevel will push OnOffIndicatorMode after consuming cols 7-8.
CS_FIXED : [cC] -> popMode, pushMode(FIXED_CSpec);

// O-spec (Output)
OS_FIXED : [oO] -> popMode, pushMode(FIXED_OSpec);

// Directive (e.g. /COPY, /EJECT, /TITLE, /SPACE)
DIRECTIVE_SPEC : . [ ]*? '/' -> popMode, pushMode(DirectiveMode);

// Blank spec line (must NOT match valid spec type letters)
BLANK_SPEC_LINE : [^hHfFeEiIcCoO*/\r\n] [ ]* '\r'? '\n' -> popMode, skip;

// Newline in selector
SEL_NEWLINE : '\r'? '\n' -> popMode, skip;


// =================================================================
// Comment Mode
// =================================================================
mode CommentMode;

COMMENTS_TEXT : ~[\r\n]+ { setText(getText().trim()); } -> channel(HIDDEN);
COMMENTS_EOL : '\r'? '\n' -> popMode, skip;


// =================================================================
// Directive Mode (e.g., /COPY, /EJECT, /TITLE, /SPACE)
// =================================================================
mode DirectiveMode;

DIR_COPY    : [cC][oO][pP][yY];
DIR_EJECT   : [eE][jJ][eE][cC][tT];
DIR_TITLE   : [tT][iI][tT][lL][eE];
DIR_SPACE   : [sS][pP][aA][cC][eE];
DIR_OtherText : ~[/'\r\n \t]+;
DIR_WS      : [ \t]+ -> skip;
DIR_Slash   : '/';
DIR_EOL     : '\r'? '\n' -> type(EOL), popMode;


// =================================================================
// H-spec Mode (Header Specification)
// RPG III H-spec: col 6=H, cols 7-80 program identification/control
// =================================================================
mode FIXED_HSpec;

HS_Content : ~[\r\n]+ { setText(getText().trim()); };
HS_EOL : '\r'? '\n' -> type(EOL), popMode;


// =================================================================
// F-spec Mode (File Specification)
// RPG III F-spec layout:
//   Cols 7-14 : File name (8 chars)
//   Col  15   : File type (I/O/U/C/D)
//   Col  16   : File designation (blank/P/S/R/T/F)
//   Col  17   : End of file (E or blank)
//   Col  18   : Sequence (A/D/blank)
//   Col  19   : File format (F/E)
//   Cols 20-23: Record length (4 digits)
//   Col  24   : Mode of processing (blank/L)
//   Cols 25-27: Length of key area (3 chars)
//   Cols 28-29: Key length (2 chars)
//   Col  30   : Reserved
//   Col  31   : Record address type (blank/A/P/K)
//   Col  32   : File organization (I/T/blank)
//   Cols 33-34: Overflow indicator (OA-OG,OV)
//   Cols 35-38: Key field start position (4 chars)
//   Col  39   : Extension code (E or blank)
//   Cols 40-46: Device (7 chars: DISK, PRINTER, WORKSTN, etc.)
//   Cols 47-52: Reserved/Continuation (6 chars)
//   Cols 53-80: Continuation keywords (SFILE, RENAME, etc.)
// =================================================================
mode FIXED_FSpec;

FS_RecordName : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==14 }?;

FS_Type : [a-zA-Z ] { getCharPositionInLine()==15 }?;

FS_Designation : [a-zA-Z ] { getCharPositionInLine()==16 }?;

FS_EndOfFile : [eE ] { getCharPositionInLine()==17 }?;

FS_Sequence : [aAdD ] { getCharPositionInLine()==18 }?;

FS_Format : [eEfF ] { getCharPositionInLine()==19 }?;

FS_RecordLength : ~[\r\n]~[\r\n]~[\r\n]~[\r\n] { getCharPositionInLine()==23 }?;

FS_Mode : [lL ] { getCharPositionInLine()==24 }?;

FS_LengthOfKeyArea : ~[\r\n]~[\r\n]~[\r\n] { getCharPositionInLine()==27 }?;

FS_KeyLength : ~[\r\n]~[\r\n] { getCharPositionInLine()==29 }?;

FS_Reserved1 : [ ] { getCharPositionInLine()==30 }? -> skip;

FS_RecordAddressType : [a-zA-Z ] { getCharPositionInLine()==31 }?;

FS_Organization : [a-zA-Z ] { getCharPositionInLine()==32 }?;

FS_OverflowIndicator : ~[\r\n]~[\r\n] { getCharPositionInLine()==34 }?;

FS_KeyFieldStart : ~[\r\n]~[\r\n]~[\r\n]~[\r\n] { getCharPositionInLine()==38 }?;

FS_ExtensionCode : [eE ] { getCharPositionInLine()==39 }?;

FS_Device : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==46 }?;

FS_Reserved2 : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==52 }? -> skip;

// Continuation area (cols 53-80) - file keywords like SFILE(xxx:yyy)
FS_Continuation : ~[\r\n]+ { setText(getText().trim()); };

FS_EOL : '\r'? '\n' -> type(EOL), popMode;


// =================================================================
// E-spec Mode (Extension Specification) - RPG III Only
// Used for defining arrays and tables.
//   Cols 7-10 : Reserved (4 chars)
//   Cols 11-18: From file name (8 chars)
//   Cols 19-26: To file name (8 chars)
//   Cols 27-32: Array/table name (6 chars)
//   Cols 33-35: Entries per record (3 digits)
//   Cols 36-39: Entries per array/table (4 digits)
//   Cols 40-42: Length of entry (3 digits)
//   Col  43   : Data format (P/B/L/R/blank)
//   Col  44   : Decimal positions (0-9/blank)
//   Col  45   : Sequence (A/D/blank)
//   Cols 46-51: Alternating array/table name (6 chars)
//   Cols 52-54: Alternating length (3 digits)
//   Col  55   : Alternating data format
//   Col  56   : Alternating decimal positions
//   Col  57   : Alternating sequence
//   Cols 58-80: Comments
// =================================================================
mode FIXED_ESpec;

ES_Reserved : '    ' { getCharPositionInLine()==10 }? -> skip;

ES_FromFileName :
    ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==18 }?;

ES_ToFileName :
    ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==26 }?;

ES_ArrayName :
    ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==32 }?;

ES_EntriesPerRecord :
    ~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==35 }?;

ES_EntriesPerArray :
    ~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==39 }?;

ES_Length :
    ~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==42 }?;

ES_DataFormat : [pPbBlLrR ] { getCharPositionInLine()==43 }?;

ES_DecimalPositions : [0-9 ] { getCharPositionInLine()==44 }?;

ES_Sequence : [aAdD ] { getCharPositionInLine()==45 }?;

ES_AltArrayName :
    ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==51 }?;

ES_AltLength :
    ~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==54 }?;

ES_AltDataFormat : [pPbBlLrR ] { getCharPositionInLine()==55 }?;

ES_AltDecimalPositions : [0-9 ] { getCharPositionInLine()==56 }?;

ES_AltSequence : [aAdD ] { getCharPositionInLine()==57 }?;

ES_Comments : ~[\r\n]+ { setText(getText().trim()); } -> channel(HIDDEN);

ES_EOL : '\r'? '\n' -> type(EOL), popMode;


// =================================================================
// I-spec Mode (Input Specification)
//
// Has multiple sub-formats detected by content:
//
// 1. Record Identification (file name in cols 7-14):
//    Cols 7-14 : File name
//    Cols 15-16: Logical relationship (AND/OR) or Sequence
//    Col  17   : Number (1/N/blank)
//    Col  18   : Option (O/blank)
//    Cols 19-20: Record identifying indicator
//    Cols 21-41: Record identification codes
//
// 2. Data Structure Header (cols 19-21 contain DS or SDS):
//    Cols 7-20 : Mostly blank, with DS or SDS marker
//
// 3. Field Description (cols 7-42 blank, fields start at col 43+):
//    Col  43   : Data format (P/B/L/R/blank)
//    Cols 44-47: From position (right-justified)
//    Cols 48-51: To position (right-justified)
//    Col  52   : Decimal positions
//    Cols 53-58: Field name (6 chars)
//    Cols 59-60: Control level (L1-L9 or blank)
//    Cols 61-62: Matching fields (M1-M9 or blank)
//    Cols 63-64: Field record relation indicator
//    Cols 65-66: Field indicator (+/plus)
//    Cols 67-68: Field indicator (-/minus)
//    Cols 69-70: Field indicator (0/blank/zero)
//
// We tokenize the first 14 chars (cols 7-20) to determine sub-format,
// then tokenize remaining content. Field description gets granular tokens.
// =================================================================
mode FIXED_ISpec;

// First 14 chars after 'I' (cols 7-20): determines sub-format
IS_Identifier : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==20 }?;

// Cols 21-42: record ID codes or reserved area (22 chars)
IS_RecordIdArea : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==42 }?;

// Col 43: Data format (P/B/L/R or blank)
IS_DataFormat : ~[\r\n] { getCharPositionInLine()==43 }?;

// Cols 44-47: From position (4 chars, right-justified)
IS_FromPosition : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==47 }?;

// Cols 48-51: To position (4 chars, right-justified)
IS_ToPosition : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==51 }?;

// Col 52: Decimal positions
IS_DecimalPositions : [0-9 ] { getCharPositionInLine()==52 }?;

// Cols 53-58: Field name (6 chars)
IS_FieldName : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==58 }?;

// Cols 59-60: Control level (L1-L9 or blank)
IS_ControlLevel : ~[\r\n]~[\r\n] { getCharPositionInLine()==60 }?;

// Cols 61-62: Matching fields (M1-M9 or blank)
IS_MatchingFields : ~[\r\n]~[\r\n] { getCharPositionInLine()==62 }?;

// Cols 63-64: Field record relation indicator
IS_FieldRelation : ~[\r\n]~[\r\n] { getCharPositionInLine()==64 }?;

// Cols 65-66: Field indicator (plus/positive)
IS_FieldIndPlus : ~[\r\n]~[\r\n] { getCharPositionInLine()==66 }?;

// Cols 67-68: Field indicator (minus/negative)
IS_FieldIndMinus : ~[\r\n]~[\r\n] { getCharPositionInLine()==68 }?;

// Cols 69-70: Field indicator (zero/blank)
IS_FieldIndZero : ~[\r\n]~[\r\n] { getCharPositionInLine()==70 }?;

// Rest of line (cols 71-80): comments
IS_Comments : ~[\r\n]+ { setText(getText().trim()); } -> channel(HIDDEN);

IS_EOL : '\r'? '\n' -> type(EOL), popMode;


// =================================================================
// C-spec Mode (Calculation Specification)
// RPG III C-spec layout (CRITICAL - different from RPG IV!):
//   Col  7-8  : Control level (L0-L9, LR, SR, AN, OR, blank)
//   Col  9    : Conditioning indicator N flag
//   Col  10-11: Conditioning indicator (2-char)
//   Cols 12-25: Factor 1 (14 chars)
//   Cols 26-27: Operation extender area (2 chars, usually blank)
//   Cols 28-32: Operation code (5 chars, left-justified)
//   Cols 33-42: Factor 2 (10 chars)
//   Cols 43-48: Result field (6 chars)
//   Cols 49-51: Field length (3 digits)
//   Col  52   : Decimal positions (0-9, blank)
//   Col  53   : Half-adjust (H or blank)
//   Cols 54-55: Resulting indicator Hi
//   Cols 56-57: Resulting indicator Lo
//   Cols 58-59: Resulting indicator Eq
//   Cols 60-80: Comments
// =================================================================
mode FIXED_CSpec;

// Control Level: cols 7-8 (2 chars)
// After matching, push OnOffIndicatorMode for the conditioning indicator
CS_ControlLevel : ~[\r\n]~[\r\n] { getCharPositionInLine()==8 }?
    -> pushMode(OnOffIndicatorMode);

// Factor 1: columns 12-25 (14 characters)
CS_Factor1 : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==25 }?;

// Operation extender area: columns 26-27 (2 chars, usually blank in RPG III)
CS_OpExtender : ~[\r\n]~[\r\n] { getCharPositionInLine()==27 }?;

// =================================================================
// Operation Code: columns 28-32 (5 characters)
// RPG III opcodes are max 5 chars. Matched specifically for parser.
// =================================================================

// ----- Conditional/Comparison (IFxx) -----
CS_Operation_IFEQ   : [iI][fF][eE][qQ][ ] { getCharPositionInLine()==32 }?;
CS_Operation_IFNE   : [iI][fF][nN][eE][ ] { getCharPositionInLine()==32 }?;
CS_Operation_IFLE   : [iI][fF][lL][eE][ ] { getCharPositionInLine()==32 }?;
CS_Operation_IFLT   : [iI][fF][lL][tT][ ] { getCharPositionInLine()==32 }?;
CS_Operation_IFGE   : [iI][fF][gG][eE][ ] { getCharPositionInLine()==32 }?;
CS_Operation_IFGT   : [iI][fF][gG][tT][ ] { getCharPositionInLine()==32 }?;

// ----- DOWxx -----
CS_Operation_DOWEQ  : [dD][oO][wW][eE][qQ] { getCharPositionInLine()==32 }?;
CS_Operation_DOWNE  : [dD][oO][wW][nN][eE] { getCharPositionInLine()==32 }?;
CS_Operation_DOWLE  : [dD][oO][wW][lL][eE] { getCharPositionInLine()==32 }?;
CS_Operation_DOWLT  : [dD][oO][wW][lL][tT] { getCharPositionInLine()==32 }?;
CS_Operation_DOWGE  : [dD][oO][wW][gG][eE] { getCharPositionInLine()==32 }?;
CS_Operation_DOWGT  : [dD][oO][wW][gG][tT] { getCharPositionInLine()==32 }?;

// ----- DOUxx -----
CS_Operation_DOUEQ  : [dD][oO][uU][eE][qQ] { getCharPositionInLine()==32 }?;
CS_Operation_DOUNE  : [dD][oO][uU][nN][eE] { getCharPositionInLine()==32 }?;
CS_Operation_DOULE  : [dD][oO][uU][lL][eE] { getCharPositionInLine()==32 }?;
CS_Operation_DOULT  : [dD][oO][uU][lL][tT] { getCharPositionInLine()==32 }?;
CS_Operation_DOUGE  : [dD][oO][uU][gG][eE] { getCharPositionInLine()==32 }?;
CS_Operation_DOUGT  : [dD][oO][uU][gG][tT] { getCharPositionInLine()==32 }?;

// ----- ANDxx -----
CS_Operation_ANDEQ  : [aA][nN][dD][eE][qQ] { getCharPositionInLine()==32 }?;
CS_Operation_ANDNE  : [aA][nN][dD][nN][eE] { getCharPositionInLine()==32 }?;
CS_Operation_ANDLE  : [aA][nN][dD][lL][eE] { getCharPositionInLine()==32 }?;
CS_Operation_ANDLT  : [aA][nN][dD][lL][tT] { getCharPositionInLine()==32 }?;
CS_Operation_ANDGE  : [aA][nN][dD][gG][eE] { getCharPositionInLine()==32 }?;
CS_Operation_ANDGT  : [aA][nN][dD][gG][tT] { getCharPositionInLine()==32 }?;

// ----- ORxx -----
CS_Operation_OREQ   : [oO][rR][eE][qQ][ ] { getCharPositionInLine()==32 }?;
CS_Operation_ORNE   : [oO][rR][nN][eE][ ] { getCharPositionInLine()==32 }?;
CS_Operation_ORLE   : [oO][rR][lL][eE][ ] { getCharPositionInLine()==32 }?;
CS_Operation_ORLT   : [oO][rR][lL][tT][ ] { getCharPositionInLine()==32 }?;
CS_Operation_ORGE   : [oO][rR][gG][eE][ ] { getCharPositionInLine()==32 }?;
CS_Operation_ORGT   : [oO][rR][gG][tT][ ] { getCharPositionInLine()==32 }?;

// ----- CASxx (RPG III multi-way branch) -----
CS_Operation_CASEQ  : [cC][aA][sS][eE][qQ] { getCharPositionInLine()==32 }?;
CS_Operation_CASNE  : [cC][aA][sS][nN][eE] { getCharPositionInLine()==32 }?;
CS_Operation_CASLE  : [cC][aA][sS][lL][eE] { getCharPositionInLine()==32 }?;
CS_Operation_CASLT  : [cC][aA][sS][lL][tT] { getCharPositionInLine()==32 }?;
CS_Operation_CASGE  : [cC][aA][sS][gG][eE] { getCharPositionInLine()==32 }?;
CS_Operation_CASGT  : [cC][aA][sS][gG][tT] { getCharPositionInLine()==32 }?;
CS_Operation_CAS    : [cC][aA][sS][ ][ ]    { getCharPositionInLine()==32 }?;

// ----- CABxx -----
CS_Operation_CABEQ  : [cC][aA][bB][eE][qQ] { getCharPositionInLine()==32 }?;
CS_Operation_CABNE  : [cC][aA][bB][nN][eE] { getCharPositionInLine()==32 }?;
CS_Operation_CABLE  : [cC][aA][bB][lL][eE] { getCharPositionInLine()==32 }?;
CS_Operation_CABLT  : [cC][aA][bB][lL][tT] { getCharPositionInLine()==32 }?;
CS_Operation_CABGE  : [cC][aA][bB][gG][eE] { getCharPositionInLine()==32 }?;
CS_Operation_CABGT  : [cC][aA][bB][gG][tT] { getCharPositionInLine()==32 }?;

// ----- Move / Arithmetic -----
CS_Operation_MOVEL  : [mM][oO][vV][eE][lL] { getCharPositionInLine()==32 }?;
CS_Operation_MOVEA  : [mM][oO][vV][eE][aA] { getCharPositionInLine()==32 }?;
CS_Operation_MOVE   : [mM][oO][vV][eE][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_Z_ADD  : [zZ]'-'[aA][dD][dD]  { getCharPositionInLine()==32 }?;
CS_Operation_Z_SUB  : [zZ]'-'[sS][uU][bB]  { getCharPositionInLine()==32 }?;
CS_Operation_ADD    : [aA][dD][dD][ ][ ]    { getCharPositionInLine()==32 }?;
CS_Operation_SUB    : [sS][uU][bB][ ][ ]    { getCharPositionInLine()==32 }?;
CS_Operation_MULT   : [mM][uU][lL][tT][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_DIV    : [dD][iI][vV][ ][ ]    { getCharPositionInLine()==32 }?;
CS_Operation_MVR    : [mM][vV][rR][ ][ ]    { getCharPositionInLine()==32 }?;
CS_Operation_SQRT   : [sS][qQ][rR][tT][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_XFOOT  : [xX][fF][oO][oO][tT] { getCharPositionInLine()==32 }?;

// ----- File I/O -----
CS_Operation_CHAIN  : [cC][hH][aA][iI][nN] { getCharPositionInLine()==32 }?;
CS_Operation_READ   : [rR][eE][aA][dD][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_READC  : [rR][eE][aA][dD][cC] { getCharPositionInLine()==32 }?;
CS_Operation_READE  : [rR][eE][aA][dD][eE] { getCharPositionInLine()==32 }?;
CS_Operation_READP  : [rR][eE][aA][dD][pP] { getCharPositionInLine()==32 }?;
CS_Operation_REDPE  : [rR][eE][dD][pP][eE] { getCharPositionInLine()==32 }?;
CS_Operation_WRITE  : [wW][rR][iI][tT][eE] { getCharPositionInLine()==32 }?;
CS_Operation_UPDAT  : [uU][pP][dD][aA][tT] { getCharPositionInLine()==32 }?;
CS_Operation_DELET  : [dD][eE][lL][eE][tT] { getCharPositionInLine()==32 }?;
CS_Operation_SETLL  : [sS][eE][tT][lL][lL] { getCharPositionInLine()==32 }?;
CS_Operation_SETGT  : [sS][eE][tT][gG][tT] { getCharPositionInLine()==32 }?;
CS_Operation_OPEN   : [oO][pP][eE][nN][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_CLOSE  : [cC][lL][oO][sS][eE] { getCharPositionInLine()==32 }?;
CS_Operation_FORCE  : [fF][oO][rR][cC][eE] { getCharPositionInLine()==32 }?;
CS_Operation_FEOD   : [fF][eE][oO][dD][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_NEXT   : [nN][eE][xX][tT][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_UNLCK  : [uU][nN][lL][cC][kK] { getCharPositionInLine()==32 }?;
CS_Operation_ACQ    : [aA][cC][qQ][ ][ ]   { getCharPositionInLine()==32 }?;
CS_Operation_REL    : [rR][eE][lL][ ][ ]   { getCharPositionInLine()==32 }?;
CS_Operation_POST   : [pP][oO][sS][tT][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_COMIT  : [cC][oO][mM][iI][tT] { getCharPositionInLine()==32 }?;
CS_Operation_ROLBK  : [rR][oO][lL][bB][kK] { getCharPositionInLine()==32 }?;

// ----- Control flow -----
CS_Operation_GOTO   : [gG][oO][tT][oO][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_TAG    : [tT][aA][gG][ ][ ]    { getCharPositionInLine()==32 }?;
CS_Operation_BEGSR  : [bB][eE][gG][sS][rR] { getCharPositionInLine()==32 }?;
CS_Operation_ENDSR  : [eE][nN][dD][sS][rR] { getCharPositionInLine()==32 }?;
CS_Operation_EXSR   : [eE][xX][sS][rR][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_ENDIF  : [eE][nN][dD][iI][fF] { getCharPositionInLine()==32 }?;
CS_Operation_ENDDO  : [eE][nN][dD][dD][oO] { getCharPositionInLine()==32 }?;
CS_Operation_ENDCS  : [eE][nN][dD][cC][sS] { getCharPositionInLine()==32 }?;
CS_Operation_END    : [eE][nN][dD][ ][ ]    { getCharPositionInLine()==32 }?;
CS_Operation_ELSE   : [eE][lL][sS][eE][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_DO     : [dD][oO][ ][ ][ ]     { getCharPositionInLine()==32 }?;
CS_Operation_LEAVE  : [lL][eE][aA][vV][eE] { getCharPositionInLine()==32 }?;
CS_Operation_ITER   : [iI][tT][eE][rR][ ]  { getCharPositionInLine()==32 }?;

// ----- CALL / PARM / PLIST -----
CS_Operation_CALL   : [cC][aA][lL][lL][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_PARM   : [pP][aA][rR][mM][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_PLIST  : [pP][lL][iI][sS][tT] { getCharPositionInLine()==32 }?;

// ----- KLIST / KFLD -----
CS_Operation_KLIST  : [kK][lL][iI][sS][tT] { getCharPositionInLine()==32 }?;
CS_Operation_KFLD   : [kK][fF][lL][dD][ ]  { getCharPositionInLine()==32 }?;

// ----- Screen/Output -----
CS_Operation_EXCPT  : [eE][xX][cC][pP][tT] { getCharPositionInLine()==32 }?;
CS_Operation_EXFMT  : [eE][xX][fF][mM][tT] { getCharPositionInLine()==32 }?;
CS_Operation_DSPLY  : [dD][sS][pP][lL][yY] { getCharPositionInLine()==32 }?;

// ----- Indicator ops -----
CS_Operation_SETON  : [sS][eE][tT][oO][nN] { getCharPositionInLine()==32 }?;
CS_Operation_SETOF  : [sS][eE][tT][oO][fF] { getCharPositionInLine()==32 }?;

// ----- String ops -----
CS_Operation_CAT    : [cC][aA][tT][ ][ ]   { getCharPositionInLine()==32 }?;
CS_Operation_SCAN   : [sS][cC][aA][nN][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_SUBST  : [sS][uU][bB][sS][tT] { getCharPositionInLine()==32 }?;
CS_Operation_XLATE  : [xX][lL][aA][tT][eE] { getCharPositionInLine()==32 }?;
CS_Operation_CHECK  : [cC][hH][eE][cC][kK] { getCharPositionInLine()==32 }?;

// ----- Compare -----
CS_Operation_COMP   : [cC][oO][mM][pP][ ]  { getCharPositionInLine()==32 }?;

// ----- Lookup -----
CS_Operation_LOKUP  : [lL][oO][kK][uU][pP] { getCharPositionInLine()==32 }?;

// ----- Sort -----
CS_Operation_SORTA  : [sS][oO][rR][tT][aA] { getCharPositionInLine()==32 }?;

// ----- Time -----
CS_Operation_TIME   : [tT][iI][mM][eE][ ]  { getCharPositionInLine()==32 }?;

// ----- OCCUR -----
CS_Operation_OCCUR  : [oO][cC][cC][uU][rR] { getCharPositionInLine()==32 }?;

// ----- Testing -----
CS_Operation_TESTB  : [tT][eE][sS][tT][bB] { getCharPositionInLine()==32 }?;
CS_Operation_TESTN  : [tT][eE][sS][tT][nN] { getCharPositionInLine()==32 }?;
CS_Operation_TESTZ  : [tT][eE][sS][tT][zZ] { getCharPositionInLine()==32 }?;

// ----- Bit manipulation -----
CS_Operation_BITON  : [bB][iI][tT][oO][nN] { getCharPositionInLine()==32 }?;
CS_Operation_BITOF  : [bB][iI][tT][oO][fF] { getCharPositionInLine()==32 }?;

// ----- Zone manipulation -----
CS_Operation_MHHZO  : [mM][hH][hH][zZ][oO] { getCharPositionInLine()==32 }?;
CS_Operation_MHLZO  : [mM][hH][lL][zZ][oO] { getCharPositionInLine()==32 }?;
CS_Operation_MLHZO  : [mM][lL][hH][zZ][oO] { getCharPositionInLine()==32 }?;
CS_Operation_MLLZO  : [mM][lL][lL][zZ][oO] { getCharPositionInLine()==32 }?;

// ----- Misc -----
CS_Operation_CLEAR  : [cC][lL][eE][aA][rR] { getCharPositionInLine()==32 }?;
CS_Operation_DUMP   : [dD][uU][mM][pP][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_SHTDN  : [sS][hH][tT][dD][nN] { getCharPositionInLine()==32 }?;
CS_Operation_DEFN   : [dD][eE][fF][nN][ ]  { getCharPositionInLine()==32 }?;
CS_Operation_IN     : [iI][nN][ ][ ][ ]     { getCharPositionInLine()==32 }?;
CS_Operation_OUT    : [oO][uU][tT][ ][ ]    { getCharPositionInLine()==32 }?;
CS_Operation_RETRN  : [rR][eE][tT][rR][nN] { getCharPositionInLine()==32 }?;
CS_Operation_RESET  : [rR][eE][sS][eE][tT] { getCharPositionInLine()==32 }?;

// Catch-all for any unrecognized 5-char operation code
CS_Operation_Other : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==32 }?;

// ----- Factor 2: columns 33-42 (10 characters) -----
CS_Factor2 : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    ~[\r\n]~[\r\n]
    { getCharPositionInLine()==42 }?;

// ----- Result field: columns 43-48 (6 characters) -----
CS_Result : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==48 }?;

// ----- Field length: columns 49-51 (3 chars) -----
CS_FieldLength : ~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==51 }?;

// ----- Decimal positions: column 52 -----
CS_DecimalPositions : [0-9 ] { getCharPositionInLine()==52 }?;

// ----- Half-adjust: column 53 -----
CS_HalfAdjust : [hH ] { getCharPositionInLine()==53 }?;

// ----- Resulting indicators: cols 54-55, 56-57, 58-59 -----
CS_ResultInd1 : ~[\r\n]~[\r\n] { getCharPositionInLine()==55 }?;
CS_ResultInd2 : ~[\r\n]~[\r\n] { getCharPositionInLine()==57 }?;
CS_ResultInd3 : ~[\r\n]~[\r\n] { getCharPositionInLine()==59 }?;

// ----- Comments after col 59 -----
CS_Comments : ~[\r\n]+ { setText(getText().trim()); } -> channel(HIDDEN);

CS_EOL : '\r'? '\n' -> type(EOL), popMode;


// =================================================================
// O-spec Mode (Output Specification)
// Two sub-types distinguished in the parser:
//
// Record format:
//   Cols 7-14 : File name (8 chars)
//   Col  15   : Type (H/D/T/E)
//   Col  16   : ADD/DEL/Fetch overflow
//   Cols 17-18: Space before (2 chars)
//   Cols 19-20: Space after (2 chars)
//   Cols 21-22: Skip before (2 chars)
//   Cols 23-24: Skip after (2 chars)
//   Cols 25-31: Output conditioning indicators (3 groups: N + 2-char)
//   Cols 32-37: Except name (6 chars)
//   Cols 38-80: Reserved / continuation
//
// Field format:
//   Cols 7-22 : Usually blank (indicates field line)
//   Cols 23-31: Conditioning indicators (3 groups: N + 2-char)
//   Cols 32-43: Field name (12 chars)
//   Col  44   : Edit code
//   Col  45   : Blank after (B or blank)
//   Cols 46-50: End position (5 chars)
//   Col  51   : Data format
//   Cols 52-80: Constant or edit word
//
// Tokenize all positional fields; parser distinguishes sub-formats.
// =================================================================
mode FIXED_OSpec;

// File name: cols 7-14 (8 chars)
OS_RecordName : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==14 }?;

// Type indicator: col 15
OS_Type : [hHdDtTeE ] { getCharPositionInLine()==15 }?;

// ADD/DEL/Fetch: col 16
OS_AddDelete : ~[\r\n] { getCharPositionInLine()==16 }?;

// Space before: cols 17-18
OS_SpaceBefore : ~[\r\n]~[\r\n] { getCharPositionInLine()==18 }?;

// Space after: cols 19-20
OS_SpaceAfter : ~[\r\n]~[\r\n] { getCharPositionInLine()==20 }?;

// Skip before: cols 21-22
OS_SkipBefore : ~[\r\n]~[\r\n] { getCharPositionInLine()==22 }?;

// Skip after: cols 23-24
OS_SkipAfter : ~[\r\n]~[\r\n] { getCharPositionInLine()==24 }?;

// Output conditioning indicators: 3 groups of (N/blank + 2-char indicator)
OS_CondInd1_Flag : [nN ] { getCharPositionInLine()==25 }?;
OS_CondInd1 : ~[\r\n]~[\r\n] { getCharPositionInLine()==27 }?;
OS_CondInd2_Flag : [nN ] { getCharPositionInLine()==28 }?;
OS_CondInd2 : ~[\r\n]~[\r\n] { getCharPositionInLine()==30 }?;
OS_CondInd3_Flag : [nN ] { getCharPositionInLine()==31 }?;
OS_CondInd3 : ~[\r\n]~[\r\n] { getCharPositionInLine()==33 }?;

// Except name / field name start: cols 32-37 (6 chars)
// For record lines: except name
// For field lines: start of field name area
OS_ExceptName : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n]
    { getCharPositionInLine()==38 }?;

// Remaining content (rest of line: field name continuation, edit codes, constants)
OS_Remaining : ~[\r\n]+ { setText(getText().trim()); };

OS_EOL : '\r'? '\n' -> type(EOL), popMode;


// =================================================================
// Indicator Modes (shared across specs)
// =================================================================

// OnOff flag: N or blank, then push IndicatorMode for 2-char indicator
mode OnOffIndicatorMode;

BlankFlag : [ ] -> popMode, pushMode(IndicatorMode);
NoFlag    : [nN] -> popMode, pushMode(IndicatorMode);


// 2-character indicator code
mode IndicatorMode;

BlankIndicator         : [ ][ ] -> popMode;
GeneralIndicator       : ([0][1-9] | [1-9][0-9]) -> popMode;
FunctionKeyIndicator   : [kK][A-NP-Ya-np-y] -> popMode;
ControlLevelIndicator  : [lL][1-9] -> popMode;
ControlLevel0Indicator : [lL][0] -> popMode;
LastRecordIndicator    : [lL][rR] -> popMode;
MatchingRecordIndicator: [mM][rR] -> popMode;
HaltIndicator          : [hH][1-9] -> popMode;
ReturnIndicator        : [rR][tT] -> popMode;
ExternalIndicator      : [uU][1-8] -> popMode;
OverflowIndicator      : [oO][A-GVa-gv] -> popMode;
SubroutineIndicator    : [sS][rR] -> popMode;
AndIndicator           : [aA][nN] -> popMode;
OrIndicator            : [oO][rR] -> popMode;
FirstPageIndicator     : [1][pP] -> popMode;
OtherIndicator         : ~[\r\n]~[\r\n] -> popMode;


// =================================================================
// End of Source Mode (after ** marker)
// =================================================================
mode EndOfSourceMode;

EOS_Text : ~[\r\n]+;
EOS_EOL  : '\r'? '\n' -> type(EOL);


// =================================================================
// Shared token type
// =================================================================
EOL : '\r'? '\n';
