---
phase: design
title: "Design: PRTF Parser"
description: Technical architecture and IR JSON design for Printer File DDS parser
---

# System Design & Architecture — PRTF Parser

## Architecture Overview

The PRTF parser plugs into the existing `As400Parser` framework exactly like the DSPF parser. It reuses the common infrastructure (normalizer, serializer, keyword parser) and **reuses DSPF model classes** for fields, constants, and conditioning indicators since the column layout is identical.

```mermaid
graph TD
    subgraph Sources["PRTF Source Files"]
        PRTF[".prtf files<br/>(Printer File DDS)"]
    end

    subgraph Pipeline["PRTF Parse Pipeline"]
        NORM["SourceNormalizer<br/>(80-char padding)"]
        CLASSIFY["Line Classifier<br/>(comment / file-level / record / field / constant)"]
        KWPARSE["DdsKeywordParser<br/>(reuse — continuation merge + keyword extraction)"]
        BUILDER["PRTF IR Builder<br/>(line → PRTF IR model)"]
        SER["IrJsonSerializer<br/>(IR → JSON)"]
    end

    subgraph Output["IR JSON Output"]
        IRPRTF["IR Document<br/>sourceType: PRTF"]
    end

    PRTF --> NORM
    NORM --> CLASSIFY
    CLASSIFY --> KWPARSE
    KWPARSE --> BUILDER
    BUILDER --> SER
    SER --> IRPRTF
```

### Reuse from DSPF / PF/LF Parsers

| Component | Reuse | Notes |
|---|---|---|
| `SourceNormalizer` | ✅ Reuse | Standard 80-char padding |
| `IrDocument` / `Metadata` / `Location` | ✅ Reuse | Same envelope, new content type |
| `IrJsonSerializer` | ✅ Reuse | Serializes any `IrDocument` |
| `As400Parser` interface | ✅ Implement | `PrtfParserFacade implements As400Parser` |
| `ParseOptions` | ✅ Reuse | No changes needed |
| `DdsKeywordParser` | ✅ Reuse | Same keyword syntax as PF/LF/DSPF |
| `DdsKeyword` model | ✅ Reuse | Same keyword representation |
| `ConditioningIndicator` model | ✅ Reuse | From `dspf.model` — same indicator columns |
| `ConditionedKeyword` model | ✅ Reuse | From `dspf.model` — keyword + conditioning |
| `KeyDefinition` model | ❌ N/A | PRTF does NOT support key fields (K in col 17) |
| `DdsComment` model | ✅ Reuse | `{lineNumber, text}` structure |
| `SourceLine` model | ✅ Reuse | Raw source line model |
| `DdsRefResolver` | ✅ Extend | Add PRTF support for REFFLD resolution |
| `PrtfContent` model | ❌ New | Top-level PRTF content container |
| `PrtfRecordFormat` model | ❌ New | PRTF record format (simpler than DSPF — no subfile types) |
| `PrtfFieldDefinition` model | ❌ New | PRTF field — similar to DSPF but usage is O/P only, renamed screen→print coords |
| `PrtfConstant` model | ❌ New | PRTF constant — text/system keyword at print position |
| `PrtfIrBuilder` | ❌ New | Builder — simpler than DSPF (no subfile detection, no conditioned keyword merging complexity) |
| `PrtfParserFacade` | ❌ New | Facade — implements `As400Parser` |

> [!TIP]
> PRTF parsing is **simpler than DSPF** — no subfile constructs, no key fields, limited usage (O or P only), no display attribute conditions. The main PRTF-specific semantics are the spacing/skipping keywords which are captured generically by `DdsKeywordParser`.

---

## DDS A-Spec Column Layout for PRTF

Same column layout as DSPF with print-specific semantics:

```
Columns  1- 5: Sequence number
Column      6: Form type (always 'A')
Column      7: Comment indicator ('*' = comment line)
Columns  8-16: Conditioning indicators (3 slots of 3 cols: 8-10, 11-13, 14-16)
Column     17: Name type:
                 blank = field/constant definition
                 'R'   = record format
                 (Note: 'K' key fields are NOT valid for PRTF)
Column     18: Reserved (always blank)
Columns 19-28: Name (field or record format name)
Column     29: Reference indicator ('R' = use REF/REFFLD)
Columns 30-34: Length (numeric, right-justified)
Column     35: Data type: S, A, F, L, T, Z (also O, G for DBCS/UTF-16)
                 Note: P (packed) and B (binary) are NOT valid for PRTF.
                 Referenced P/B fields are auto-converted to S (zoned decimal).
Columns 36-37: Decimal positions
Column     38: Usage:
                 blank/O = output-only (default)
                 P       = program-to-system (not printed; passes data to keywords
                           like AFPRSC, BOX, LINE, OVERLAY via field references)
Columns 39-41: Print line number (max 255)
Columns 42-44: Print position / horizontal column (max 255)
Columns 45-80: Keywords and comments
Column     80: Continuation indicator ('+' = line continues on next)
```

> [!IMPORTANT]
> **Key PRTF differences from DSPF:**
> - Columns 39-44 contain **print coordinates** (line/position on paper) rather than screen coordinates
> - Column 38 (Usage): blank/`O` = output-only (default), `P` = program-to-system (field not printed, used as keyword parameter)
> - **No key fields** — `K` in column 17 is not valid for printer files
> - **No subfile constructs** — SFL/SFLCTL are DSPF-specific
> - **No function keys** — CAxx/CFxx are DSPF-specific
> - **No display attributes** — DSPATR is DSPF-specific
> - **Print-specific keywords**: SPACEA/SPACEB, SKIPA/SKIPB, FONT, BARCODE, OVERLAY, etc.

---

## Data Models — IR Content Structure

### PRTF Content (`PRTF`)

```mermaid
graph TD
    Content["content (PRTF)"]
    Content --> SL["sourceLines"]
    Content --> FKW["fileKeywords"]
    Content --> RF["recordFormats"]
    Content --> CMT["comments"]
    Content --> PE["parseErrors"]

    RF --> RFDEF["PrtfRecordFormat"]
    RFDEF --> RKWDS["keywords"]
    RFDEF --> FIELDS["fields"]
    RFDEF --> CONSTS["constants"]

    FIELDS --> FDEF["PrtfFieldDefinition"]
    FDEF --> PRINT["printLine / printPosition"]
    FDEF --> COND["conditioningIndicators"]
    FDEF --> FKWDS["keywords"]
```

#### `content` for `PRTF`

| Field | Type | Description |
|---|---|---|
| `sourceLines` | `array<SourceLine>` | All raw source lines (same structure as RPG3/DDS/DSPF) |
| `fileKeywords` | `array<DdsKeyword>` | File-level keywords: `REF`, `INDARA`, etc. |
| `recordFormats` | `array<PrtfRecordFormat>` | Record format definitions |
| `comments` | `array<Comment>` | Standalone comment lines |
| `parseErrors` | `array<ParseError>` | Parse errors/warnings |

#### `PrtfRecordFormat`

| Field | Type | Description |
|---|---|---|
| `location` | `location` | Source position |
| `rawSourceLine` | `string` | Original source text |
| `conditioningIndicators` | `array<ConditioningIndicator>` | Conditioning indicators on the record format line |
| `name` | `string` | Record format name |
| `text` | `string` | Record text from `TEXT(...)` keyword |
| `keywords` | `array<DdsKeyword>` | Record-level keywords (SPACEA, SPACEB, SKIPA, SKIPB, FORCE, BOX, LINE, OVERLAY, etc.) |
| `fields` | `array<PrtfFieldDefinition>` | Named field definitions |
| `constants` | `array<PrtfConstant>` | Constant entries (text/system keywords) |

#### `PrtfFieldDefinition`

| Field | Type | Description |
|---|---|---|
| `location` | `location` | Source position (may span multiple lines) |
| `rawSourceLines` | `array<string>` | All source lines for this field (including continuations) |
| `conditioningIndicators` | `array<ConditioningIndicator>` | Conditioning indicators on field definition line |
| `name` | `string` | Field name |
| `reference` | `string` | Reference indicator: `R` if using REF/REFFLD, `null` otherwise |
| `referenceField` | `string` | Referenced field name from REFFLD |
| `referenceFile` | `string` | Referenced file from REFFLD |
| `referenceRecordFormat` | `string` | Referenced record format from REFFLD |
| `length` | `integer` | Field length. `null` if inherited via REF |
| `dataType` | `string` | DDS data type code: `A`, `S`, `F`, `L`, `T`, `Z`, `O`, `G` (no P/B) |
| `decimalPositions` | `integer` | Decimal positions (`null` for character types) |
| `usage` | `string` | Usage: `O`/blank = output-only, `P` = program-to-system |
| `printLine` | `integer` | Print line number (cols 39-41). `null` if not specified |
| `printPosition` | `integer` | Print column position (cols 42-44). `null` if not specified |
| `source` | `string` | Field source: `direct`, `reference` (REF/REFFLD) |
| `keywords` | `array<ConditionedKeyword>` | Field-level keywords with optional conditioning indicators |

#### `PrtfConstant`

Represents unnamed print elements: **quoted literal text** (column headers, labels) or **system keyword constants** (`DATE`, `TIME`, `PAGNBR`, `MSGCON`).

| Field | Type | Description |
|---|---|---|
| `location` | `location` | Source position |
| `rawSourceLines` | `array<string>` | Original source text (may span continuation lines) |
| `conditioningIndicators` | `array<ConditioningIndicator>` | Conditioning indicators controlling output |
| `printLine` | `integer` | Print line number |
| `printPosition` | `integer` | Print column position |
| `text` | `string` | Literal text content. `null` for system keyword constants |
| `systemKeyword` | `string` | System keyword (`DATE`, `TIME`, `PAGNBR`, `MSGCON`). `null` for text constants |
| `keywords` | `array<ConditionedKeyword>` | Keywords on the constant (e.g., `EDTCDE`, `UNDERLINE`) |

#### Reused Models

- **`ConditioningIndicator`** — `{not, indicator}` from cols 8-16 (reuse from `dspf.model`)
- **`ConditionedKeyword`** — `{keyword, conditioningIndicators}` wrapper (reuse from `dspf.model`)

---

### PRTF Keywords Reference (IBM DDS for Printer Files)

> [!NOTE]
> The `DdsKeywordParser` captures **all** keywords generically. This list documents known PRTF keywords from the IBM i 7.6 DDS Reference for Printer Files. No special handling is needed per keyword. Keywords marked with ★ can appear at multiple levels.

#### File-Level Keywords

| Keyword | Description |
|---|---|
| `REF(file)` | Reference file for field attributes |
| `SKIPA(n)` | Skip after — file-level (not allowed for *AFPDS) ★ |
| `SKIPB(n)` | Skip before — file-level (not allowed for *AFPDS) ★ |
| `INDARA` | Separate indicator area |
| `INDTXT(indicator 'text')` | Indicator text description |
| `CHRID` | Character identifier |
| `DFNCHR(...)` | Define custom characters (dot-matrix patterns) ★ |

#### Record-Level Keywords

| Keyword | Description |
|---|---|
| `TEXT('description')` | Record description |
| `SPACEA(n)` | Space after printing (1-255 lines) ★ field-level also |
| `SPACEB(n)` | Space before printing (1-255 lines) ★ field-level also |
| `SKIPA(n)` | Skip after printing to line n ★ field-level also |
| `SKIPB(n)` | Skip before printing to line n ★ field-level also |
| `FORCE` | Force record to print immediately |
| `CPI(n)` | Characters per inch ★ field-level also |
| `LPI(n)` | Lines per inch |
| `FONT(font-id [pointsize])` | Font identifier ★ field-level also |
| `FONTNAME(name [*POINTSIZE h w] [*CODEPAGE cp])` | Font name ★ field-level also |
| `FNTCHRSET(set codepage [library])` | Font character set ★ field-level also |
| `CDEFNT([library/]font [*POINTSIZE h w])` | Coded font name ★ field-level also |
| `PRTQLTY(*STD \| *NLQ \| *FASTDRAFT \| *DRAFT)` | Print quality |
| `DRAWER(n)` | Paper drawer |
| `OUTBIN(n)` | Output bin |
| `DUPLEX(*YES \| *NO \| *TUMBLE)` | Duplex printing |
| `PAGRTT(0 \| 90 \| 180 \| 270)` | Page rotation |
| `CHRSIZ(w h)` | Character size multiplier ★ field-level also |
| `STAPLE` | Staple output |
| `ZFOLD` | Z-fold output |
| `ENDPAGE` | End of page |
| `STRPAGGRP(name)` | Start page group |
| `ENDPAGGRP` | End page group |
| `OVERLAY(name [library] [x y])` | Page overlay |
| `BOX(d1 a1 d2 a2 line-width [color])` | Draw rectangle (corners + line width) |
| `LINE(d1 a1 d2 a2 line-width [color])` | Draw line (start/end points + line width) |
| `GDF(name library pos-down pos-across [*SIZE w h] [*ROTATION r])` | Graphic data file |
| `AFPRSC(name type pos-down pos-across [...])` | AFP resource (complex params) |
| `PAGSEG(name library pos-down pos-across [*SIZE w h] [*ROTATION r])` | Page segment |
| `INVDTAMAP(name)` | Invoke data map |
| `INVMMAP(name)` | Invoke medium map |
| `DOCIDXTAG(tag-name attribute-name value)` | Document index tag |
| `DFNCHR(...)` | Define custom characters ★ file-level also |
| `DFNLIN(...)` | Define line — DBCS line drawing |

#### Field-Level Keywords

| Keyword | Description |
|---|---|
| `EDTCDE(code)` | Edit code for numeric formatting |
| `EDTWRD('edit-word')` | Edit word for custom numeric formatting |
| `REFFLD(field [record] [file])` | Reference field |
| `ALIAS('name')` | Alternative name |
| `DATFMT(format)` | Date format |
| `DATSEP(separator)` | Date separator |
| `TIMFMT(format)` | Time format |
| `TIMSEP(separator)` | Time separator |
| `DFT('value')` | Default value |
| `DLTEDT` | Delete edit |
| `FLTFIXDEC(n)` | Float to fixed decimal |
| `FLTPCN(*SINGLE \| *DOUBLE)` | Floating-point precision |
| `BARCODE(bar-code-ID [height] [options...])` | Bar code (complex params) |
| `COLOR(color-name \| *RGB \| *CMYK \| *CIELAB \| *HIGHLIGHT)` | Color |
| `HIGHLIGHT` | Highlight text |
| `UNDERLINE` | Underline text |
| `TXTRTT(0 \| 90 \| 180 \| 270)` | Text rotation |
| `POSITION(pos-down pos-across)` | Absolute positioning (inches/cm) |
| `RELPOS(n)` | Relative positioning |
| `BLKFOLD` | Blank fold |
| `CVTDTA` | Convert data (hex conversion) |
| `TRNSPY('hex-string')` | Transparency |
| `CHRID` | Character identifier |
| `MSGCON(msg-id file [library] length)` | Message constant |
| `CCSID(n)` | Coded Character Set Identifier (UTF-16/UCS-2) |
| `UNISCRIPT` | Unicode text layout |
| `DTASTMCMD` | Data stream command (raw SCS/IPDS) |

#### DBCS-Specific Keywords

| Keyword | Level | Description |
|---|---|---|
| `IGCALTTYP` | Field | Alternative data type (DBCS) |
| `IGCANKCNV` | Field | Alphanumeric-to-DBCS conversion |
| `IGCCDEFNT([lib/]font [*POINTSIZE h w])` | Record/Field | DBCS coded font |
| `IGCCHRRTT` | Record/Field | DBCS character rotation |

#### System Keyword Constants

| Keyword | Description |
|---|---|
| `DATE` | System date (with optional `DATFMT`, `DATSEP`, `EDTCDE`) |
| `TIME` | System time (with optional `TIMFMT`, `TIMSEP`) |
| `PAGNBR` | Page number (with optional `EDTCDE`) |
| `MSGCON(msg-id file [library] length)` | Message constant — text from message description |

---

## Design Decisions

| Decision | Rationale |
|---|---|
| Create new PRTF model classes | PRTF field semantics differ (print coords vs screen coords), cleaner separation |
| Reuse `ConditioningIndicator` / `ConditionedKeyword` from DSPF | Same indicator columns 8-16, identical structure |
| Single-pass builder (same as DSPF) | Proven pattern, same column layout |
| Extend `DdsRefResolver` for PRTF | REFFLD resolution follows same PF-first pattern |
| Name coordinates `printLine`/`printPosition` | Distinguish from DSPF's `screenLine`/`screenPosition` for clarity |
| Keep `usage` field on model | PRTF supports `O` (output) and `P` (program-to-system); P-fields are not printed but pass data to record-level keywords |
| No key field support | PRTF does not support `K` in col 17 — key fields are PF/LF-only |
