/**
 * RPG III Parser Grammar
 *
 * A purpose-built ANTLR4 parser for IBM RPG III fixed-format source code.
 * Works with Rpg3Lexer.g4. Follows the RPG III specification ordering:
 *   H-spec -> F-spec -> E-spec -> I-spec -> C-spec -> O-spec -> End Source
 *
 * IMPORTANT: Input must be space-padded to 80 columns before parsing.
 *
 * The parser handles nested control structures (IF/DO/CAS) and
 * subroutine boundaries (BEGSR/ENDSR) within C-specs.
 *
 * RPG IV-only constructs NOT supported:
 *   - D-spec, P-spec (use E-spec and I-spec DS instead)
 *   - Free-format expressions
 *   - WHENxx/SELECT/OTHER/ENDSL (use CASxx/CAS/ENDCS)
 *   - CHECKR, LEAVESR (6-char opcodes don't fit 5-char RPG III op field)
 *   - Built-in functions (%SUBST, etc.)
 */

parser grammar Rpg3Parser;

options { tokenVocab = Rpg3Lexer; }

// =================================================================
// Top-level Program Structure
// RPG III requires specifications in order: H, F, E, I, C, O
// =================================================================

rpg3Program
    : headerSpec*
      fileSpec*
      extensionSpec*
      inputSpec*
      calculationSpec*
      outputSpec*
      endSource?
      EOF
    ;

// =================================================================
// H-spec (Header Specification)
// =================================================================

headerSpec
    : HS_FIXED HS_Content? EOL
    ;

// =================================================================
// F-spec (File Specification)
// =================================================================

fileSpec
    : FS_FIXED
      FS_RecordName          // File name (cols 7-14)
      FS_Type                // I/O/U/C/D (col 15)
      FS_Designation         // blank/P/S/R/T/F (col 16)
      FS_EndOfFile           // E/blank (col 17)
      FS_Sequence            // A/D/blank (col 18)
      FS_Format              // F/E (col 19)
      FS_RecordLength        // Record length (cols 20-23)
      FS_Mode                // Mode of processing (col 24)
      FS_LengthOfKeyArea     // Length of key area (cols 25-27)
      FS_KeyLength           // Key length (cols 28-29)
      FS_RecordAddressType   // Record address type (col 31)
      FS_Organization        // File organization (col 32)
      FS_OverflowIndicator   // Overflow indicator (cols 33-34)
      FS_KeyFieldStart       // Key field start (cols 35-38)
      FS_ExtensionCode       // Extension code (col 39)
      FS_Device              // Device (cols 40-46)
      FS_Continuation?       // Keywords like SFILE(...) (cols 53-80)
      EOL
    ;

// =================================================================
// E-spec (Extension Specification) — RPG III Only
// Defines arrays and tables
// =================================================================

extensionSpec
    : ES_FIXED
      ES_FromFileName        // From file name (cols 11-18)
      ES_ToFileName          // To file name (cols 19-26)
      ES_ArrayName           // Array/table name (cols 27-32)
      ES_EntriesPerRecord    // Entries per record (cols 33-35)
      ES_EntriesPerArray     // Total entries (cols 36-39)
      ES_Length              // Length of entry (cols 40-42)
      ES_DataFormat          // Data format P/B/L/R (col 43)
      ES_DecimalPositions    // Decimal positions (col 44)
      ES_Sequence            // Sequence A/D (col 45)
      alternatingArray?
      EOL
    ;

alternatingArray
    : ES_AltArrayName        // Alt array name (cols 46-51)
      ES_AltLength           // Alt length (cols 52-54)
      ES_AltDataFormat       // Alt data format (col 55)
      ES_AltDecimalPositions // Alt decimal positions (col 56)
      ES_AltSequence         // Alt sequence (col 57)
    ;

// =================================================================
// I-spec (Input Specification)
// The IS_Identifier token (cols 7-20) determines the sub-format:
//   - Contains file name -> record identification
//   - Contains 'DS'/'SDS' -> data structure header
//   - All blank -> field description
// =================================================================

inputSpec
    : IS_FIXED
      IS_Identifier          // Cols 7-20: file name, DS/SDS, or blank
      IS_RecordIdArea        // Cols 21-42: record ID codes or blank
      inputFieldDetail?      // Cols 43-70: field detail (if field desc line)
      EOL
    ;

inputFieldDetail
    : IS_DataFormat          // Col 43: P/B/L/R/blank
      IS_FromPosition        // Cols 44-47: from position
      IS_ToPosition          // Cols 48-51: to position
      IS_DecimalPositions    // Col 52: decimal positions
      IS_FieldName           // Cols 53-58: field name (6 chars)
      IS_ControlLevel        // Cols 59-60: control level
      IS_MatchingFields      // Cols 61-62: matching fields
      IS_FieldRelation       // Cols 63-64: field record relation
      IS_FieldIndPlus        // Cols 65-66: plus indicator
      IS_FieldIndMinus       // Cols 67-68: minus indicator
      IS_FieldIndZero        // Cols 69-70: zero/blank indicator
    ;

// =================================================================
// C-spec (Calculation Specification)
// All business logic. Supports nested IF/DO/CAS and subroutines.
// =================================================================

calculationSpec
    : subroutine
    | calcStatement
    ;

// ----- Subroutine -----
subroutine
    : cspecBEGSR
      calcStatement*
      cspecENDSR
    ;

// ----- Single calculation statement or block -----
calcStatement
    : ifBlock
    | doBlock
    | douBlock
    | dowBlock
    | caseBlock
    | calcLine
    | commentLine
    ;

commentLine
    : COMMENT_SPEC
    | COMMENT_SPEC_STAR
    ;

// ----- IF block -----
ifBlock
    : cspecIFxx
      calcStatement*
      (cspecELSE calcStatement*)?
      cspecENDIF
    ;

// ----- DO block -----
doBlock
    : cspecDO
      calcStatement*
      cspecENDDO
    ;

// ----- DOWxx block -----
dowBlock
    : cspecDOWxx
      calcStatement*
      cspecENDDO
    ;

// ----- DOUxx block -----
douBlock
    : cspecDOUxx
      calcStatement*
      cspecENDDO
    ;

// ----- CASxx block (RPG III multi-way branch) -----
caseBlock
    : cspecCASxx+
      cspecCAS?
      cspecENDCS
    ;

// =================================================================
// Individual C-spec line helpers
// =================================================================

// Common C-spec line prefix: spec type + control level + indicator
cspecPrefix
    : CS_FIXED CS_ControlLevel onOffFlag csIndicator CS_Factor1 CS_OpExtender
    ;

// Common C-spec line suffix: after operation code
cspecSuffix
    : CS_Factor2 CS_Result CS_FieldLength CS_DecimalPositions
      CS_HalfAdjust csResultIndicators EOL
    ;

// =================================================================
// Individual C-spec line patterns
// =================================================================

calcLine
    : cspecPrefix csOperation cspecSuffix
    ;

// ----- Control structure entry lines -----
cspecIFxx
    : cspecPrefix
      ( CS_Operation_IFEQ | CS_Operation_IFNE | CS_Operation_IFLE
      | CS_Operation_IFLT | CS_Operation_IFGE | CS_Operation_IFGT )
      cspecSuffix
      (cspecANDxx | cspecORxx)*
    ;

cspecELSE
    : cspecPrefix CS_Operation_ELSE cspecSuffix
    ;

cspecENDIF
    : cspecPrefix ( CS_Operation_ENDIF | CS_Operation_END ) cspecSuffix
    ;

cspecDO
    : cspecPrefix CS_Operation_DO cspecSuffix
    ;

cspecDOWxx
    : cspecPrefix
      ( CS_Operation_DOWEQ | CS_Operation_DOWNE | CS_Operation_DOWLE
      | CS_Operation_DOWLT | CS_Operation_DOWGE | CS_Operation_DOWGT )
      cspecSuffix
      (cspecANDxx | cspecORxx)*
    ;

cspecDOUxx
    : cspecPrefix
      ( CS_Operation_DOUEQ | CS_Operation_DOUNE | CS_Operation_DOULE
      | CS_Operation_DOULT | CS_Operation_DOUGE | CS_Operation_DOUGT )
      cspecSuffix
      (cspecANDxx | cspecORxx)*
    ;

cspecENDDO
    : cspecPrefix ( CS_Operation_ENDDO | CS_Operation_END ) cspecSuffix
    ;

// ----- CASxx -----
cspecCASxx
    : cspecPrefix
      ( CS_Operation_CASEQ | CS_Operation_CASNE | CS_Operation_CASLE
      | CS_Operation_CASLT | CS_Operation_CASGE | CS_Operation_CASGT )
      cspecSuffix
    ;

cspecCAS
    : cspecPrefix CS_Operation_CAS cspecSuffix
    ;

cspecENDCS
    : cspecPrefix ( CS_Operation_ENDCS | CS_Operation_END ) cspecSuffix
    ;

// ----- ANDxx / ORxx continuation -----
cspecANDxx
    : cspecPrefix
      ( CS_Operation_ANDEQ | CS_Operation_ANDNE | CS_Operation_ANDLE
      | CS_Operation_ANDLT | CS_Operation_ANDGE | CS_Operation_ANDGT )
      cspecSuffix
    ;

cspecORxx
    : cspecPrefix
      ( CS_Operation_OREQ | CS_Operation_ORNE | CS_Operation_ORLE
      | CS_Operation_ORLT | CS_Operation_ORGE | CS_Operation_ORGT )
      cspecSuffix
    ;

// ----- Subroutine boundaries -----
cspecBEGSR
    : cspecPrefix CS_Operation_BEGSR cspecSuffix
    ;

cspecENDSR
    : cspecPrefix CS_Operation_ENDSR cspecSuffix
    ;

// =================================================================
// C-spec operation code (all valid RPG III opcodes)
// =================================================================
csOperation
    // Conditional
    : CS_Operation_IFEQ | CS_Operation_IFNE | CS_Operation_IFLE
    | CS_Operation_IFLT | CS_Operation_IFGE | CS_Operation_IFGT
    // DOWxx
    | CS_Operation_DOWEQ | CS_Operation_DOWNE | CS_Operation_DOWLE
    | CS_Operation_DOWLT | CS_Operation_DOWGE | CS_Operation_DOWGT
    // DOUxx
    | CS_Operation_DOUEQ | CS_Operation_DOUNE | CS_Operation_DOULE
    | CS_Operation_DOULT | CS_Operation_DOUGE | CS_Operation_DOUGT
    // ANDxx / ORxx
    | CS_Operation_ANDEQ | CS_Operation_ANDNE | CS_Operation_ANDLE
    | CS_Operation_ANDLT | CS_Operation_ANDGE | CS_Operation_ANDGT
    | CS_Operation_OREQ | CS_Operation_ORNE | CS_Operation_ORLE
    | CS_Operation_ORLT | CS_Operation_ORGE | CS_Operation_ORGT
    // CASxx (multi-way branch)
    | CS_Operation_CASEQ | CS_Operation_CASNE | CS_Operation_CASLE
    | CS_Operation_CASLT | CS_Operation_CASGE | CS_Operation_CASGT
    | CS_Operation_CAS
    // CABxx
    | CS_Operation_CABEQ | CS_Operation_CABNE | CS_Operation_CABLE
    | CS_Operation_CABLT | CS_Operation_CABGE | CS_Operation_CABGT
    // Move
    | CS_Operation_MOVEL | CS_Operation_MOVEA | CS_Operation_MOVE
    // Arithmetic
    | CS_Operation_Z_ADD | CS_Operation_Z_SUB
    | CS_Operation_ADD | CS_Operation_SUB | CS_Operation_MULT
    | CS_Operation_DIV | CS_Operation_MVR | CS_Operation_SQRT
    | CS_Operation_XFOOT
    // File I/O
    | CS_Operation_CHAIN | CS_Operation_READ | CS_Operation_READC
    | CS_Operation_READE | CS_Operation_READP | CS_Operation_REDPE
    | CS_Operation_WRITE | CS_Operation_UPDAT | CS_Operation_DELET
    | CS_Operation_SETLL | CS_Operation_SETGT
    | CS_Operation_OPEN | CS_Operation_CLOSE | CS_Operation_FORCE
    | CS_Operation_FEOD | CS_Operation_NEXT | CS_Operation_UNLCK
    | CS_Operation_ACQ | CS_Operation_REL | CS_Operation_POST
    | CS_Operation_COMIT | CS_Operation_ROLBK
    // Control flow
    | CS_Operation_GOTO | CS_Operation_TAG
    | CS_Operation_BEGSR | CS_Operation_ENDSR | CS_Operation_EXSR
    | CS_Operation_ENDIF | CS_Operation_ENDDO | CS_Operation_ENDCS
    | CS_Operation_END | CS_Operation_ELSE
    | CS_Operation_DO | CS_Operation_LEAVE | CS_Operation_ITER
    // CALL / PARM / PLIST
    | CS_Operation_CALL | CS_Operation_PARM | CS_Operation_PLIST
    // KLIST / KFLD
    | CS_Operation_KLIST | CS_Operation_KFLD
    // Screen / Output
    | CS_Operation_EXCPT | CS_Operation_EXFMT | CS_Operation_DSPLY
    // Indicator
    | CS_Operation_SETON | CS_Operation_SETOF
    // String
    | CS_Operation_CAT | CS_Operation_SCAN | CS_Operation_SUBST
    | CS_Operation_XLATE | CS_Operation_CHECK
    // Compare / Lookup / Sort
    | CS_Operation_COMP | CS_Operation_LOKUP | CS_Operation_SORTA
    // Time / Occur
    | CS_Operation_TIME | CS_Operation_OCCUR
    // Testing
    | CS_Operation_TESTB | CS_Operation_TESTN | CS_Operation_TESTZ
    // Bit manipulation
    | CS_Operation_BITON | CS_Operation_BITOF
    // Zone manipulation
    | CS_Operation_MHHZO | CS_Operation_MHLZO
    | CS_Operation_MLHZO | CS_Operation_MLLZO
    // Misc
    | CS_Operation_CLEAR | CS_Operation_DUMP | CS_Operation_SHTDN
    | CS_Operation_DEFN | CS_Operation_IN | CS_Operation_OUT
    | CS_Operation_RETRN | CS_Operation_RESET
    // Catch-all
    | CS_Operation_Other
    ;

// =================================================================
// Indicator helper rules
// =================================================================

onOffFlag
    : BlankFlag
    | NoFlag
    ;

csIndicator
    : BlankIndicator
    | GeneralIndicator
    | FunctionKeyIndicator
    | ControlLevelIndicator
    | ControlLevel0Indicator
    | LastRecordIndicator
    | MatchingRecordIndicator
    | HaltIndicator
    | ReturnIndicator
    | ExternalIndicator
    | OverflowIndicator
    | SubroutineIndicator
    | AndIndicator
    | OrIndicator
    | FirstPageIndicator
    | OtherIndicator
    ;

csResultIndicators
    : CS_ResultInd1 CS_ResultInd2 CS_ResultInd3
    ;

// =================================================================
// O-spec (Output Specification)
// =================================================================

outputSpec
    : outputRecordSpec
    | outputFieldSpec
    ;

outputRecordSpec
    : OS_FIXED
      OS_RecordName
      OS_Type
      OS_AddDelete
      OS_SpaceBefore
      OS_SpaceAfter
      OS_SkipBefore
      OS_SkipAfter
      outputCondIndicators
      OS_ExceptName
      OS_Remaining?
      EOL
    ;

outputFieldSpec
    : OS_FIXED
      OS_RecordName
      OS_Type?
      OS_AddDelete?
      OS_SpaceBefore?
      OS_SpaceAfter?
      OS_SkipBefore?
      OS_SkipAfter?
      outputCondIndicators?
      OS_ExceptName?
      OS_Remaining?
      EOL
    ;

outputCondIndicators
    : OS_CondInd1_Flag OS_CondInd1
      OS_CondInd2_Flag OS_CondInd2
      OS_CondInd3_Flag OS_CondInd3
    ;

// =================================================================
// End of Source
// =================================================================

endSource
    : END_SOURCE endSourceLine*
    ;

endSourceLine
    : EOS_Text? EOL
    ;
