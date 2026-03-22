---
phase: requirements
title: Requirements & Problem Understanding
description: Clarify the problem space, gather requirements, and define success criteria
---

# Requirements & Problem Understanding — PRTF Parser

## Problem Statement

**What problem are we solving?**

- AS/400 Printer Files (PRTF) define report layouts — column headings, detail lines, page headers/footers, barcodes, overlays, and page control. They are written in **DDS (Data Description Specifications)** using the same A-spec column layout as PF/LF/DSPF, but with print-specific features: line spacing/skipping, fonts, print quality, page segments, overlays, and bar codes.
- The project already has **working PF/LF and DSPF parsers**. What's missing is the **parser that produces PRTF IR JSON**.
- Migration developers need structured representation of print layouts to understand report structure, field positioning, page flow control, and formatting rules for modernizing AS/400 reports to PDF/web report generation.
- PRTF shares the DDS A-spec base format but introduces unique constructs not found in PF/LF or DSPF:
  - **Print coordinates** (line/position in columns 39-44) — print line and horizontal position
  - **Line spacing** (`SPACEA`, `SPACEB`) for spacing after/before printing
  - **Line skipping** (`SKIPA`, `SKIPB`) for skipping to specific lines after/before printing
  - **Font control** (`FONT`, `FONTNAME`, `FNTCHRSET`, `CDEFNT`) for typeface selection
  - **Character sizing** (`CPI`, `LPI`, `CHRSIZ`) for density control
  - **Barcodes** (`BARCODE`) for barcode generation
  - **Page overlays** (`OVERLAY`, `PAGSEG`) for pre-printed forms
  - **Drawing primitives** (`BOX`, `LINE`, `GDF`) for graphical elements
  - **AFP resources** (`AFPRSC`, `INVDTAMAP`, `INVMMAP`) for Advanced Function Printing
  - **Page control** (`ENDPAGE`, `PAGNBR`, `DUPLEX`, `DRAWER`, `OUTBIN`, `STAPLE`, `ZFOLD`)
  - **Text formatting** (`UNDERLINE`, `HIGHLIGHT`, `TXTRTT`, `PAGRTT`, `BLKFOLD`)
  - **Constants** including `DATE`, `TIME`, `PAGNBR` system keyword constants
  - **No user interaction** — unlike DSPF, PRTF has no function keys (CAxx/CFxx), no usage column for input, no subfile constructs, no display attributes (DSPATR)

## Processing Pipeline

```
Raw PRTF Source → Stage 1: Normalize → Stage 2: Classify Lines → Stage 3: Parse Keywords → Stage 4: Build IR → Stage 5: Serialize JSON
```

### Stage 1: Source Normalization (Reuse)
Reuse existing `SourceNormalizer` with standard 80-char padding. Handles tab expansion, encoding cleanup, sequence number extraction.

### Stage 2: Line Classification
Classify each line by column 17 entry type (`R`=record format, `K`=key, blank=field/constant) and column 7 (`*`=comment). **Same logic as DSPF/PF/LF**.

### Stage 3: Keyword Parsing (Reuse)
Reuse existing `DdsKeywordParser` for keyword area (columns 45-80). Continuation lines merging via `+` at column 80. Same `KEYWORD(args)` syntax.

### Stage 4: IR Building
Build `PrtfContent` from classified/parsed lines. Group fields and constants under record formats. Handle PRTF-specific constructs: print coordinates, spacing/skipping keywords, font control.

### Stage 5: JSON Serialization (Reuse)
Reuse existing `IrJsonSerializer` to produce IR JSON output.

## Goals & Objectives

**Primary Goals:**
1. Build a PRTF parser that produces IR JSON with sourceType `PRTF`, conforming to the existing IR template envelope
2. Reuse the common framework (`As400Parser`, `IrDocument`, `SourceNormalizer`, `IrJsonSerializer`, `DdsKeywordParser`)
3. Reuse DSPF model classes where applicable — PRTF fields/constants have the same structure (coordinates, conditioning indicators, keywords)
4. Parse all DDS A-spec features relevant to PRTF: record formats, named fields, constants, keys
5. Parse PRTF-specific features:
   - Print coordinates (line/position from columns 39-44)
   - Spacing keywords (`SPACEA`, `SPACEB`) for inter-line spacing
   - Skipping keywords (`SKIPA`, `SKIPB`) for page positioning
   - Font keywords (`FONT`, `FONTNAME`, `FNTCHRSET`, `CDEFNT`)
   - Character/line density (`CPI`, `LPI`, `CHRSIZ`)
   - Bar codes (`BARCODE` keyword with symbol/height/length parameters)
   - Page overlays (`OVERLAY`, `PAGSEG`)
   - Drawing (`BOX`, `LINE`, `GDF`)
   - AFP resources (`AFPRSC`, `INVDTAMAP`, `INVMMAP`)
   - Page control (`ENDPAGE`, `ENDPAGGRP`, `STRPAGGRP`, `PAGNBR`, `DUPLEX`, `DRAWER`, `OUTBIN`, `STAPLE`, `ZFOLD`)
   - Constants: quoted text and system keywords (`DATE`, `TIME`, `PAGNBR`)
   - Conditioning indicators (columns 8-16) on fields and keywords
   - Edit codes/words (`EDTCDE`, `EDTWRD`) for formatting
   - Reference fields (`REF`, `REFFLD`) for field attribute inheritance
6. Handle continuation lines (same as PF/LF/DSPF)
7. Support Japanese/CJK text in constants and keywords

**Secondary Goals:**
- Provide meaningful error/warning messages with source line locations
- Support partial parsing (produce partial IR if some lines fail)
- Performance target: < 100ms per file

**Non-Goals (out of scope):**
- AFPDS rendering / print preview
- Runtime indicator resolution
- DDS validation rules (checking field overlap, coordinate conflicts, etc.)
- DBCS-specific keywords (`IGCALTTYP`, `IGCANKCNV`, `IGCCDEFNT`, `IGCCHRRTT`) — captured generically by keyword parser
- `DFNCHR` dot-matrix character definition — captured generically

## User Stories & Use Cases

**As a migration developer**, I want to parse PRTF source and get a complete IR JSON document with record formats, field definitions (name, type, length, print position), spacing/skipping control, and font settings, so that I can generate equivalent report layouts in modern frameworks (PDF, web reports).

**As a report designer**, I want to extract print layout information (field positions, headings, page flow) from PRTF definitions, so that I can plan modern report templates based on existing AS/400 reports.

**As a migration developer**, I want to understand page control (overlays, page breaks, duplex printing) from the IR, so that I can replicate the same print behavior in modern environments.

### Key Scenarios

1. **Simple report parse** — Parse a PRTF with page header, detail lines, and footer → IR JSON with record formats, fields with print positions, SPACEA/SPACEB keywords
2. **Report with fonts/formatting** — Parse PRTF using FONT, CPI, EDTCDE → IR captures font settings and edit codes
3. **Report with overlays** — Parse PRTF using OVERLAY, PAGSEG → IR captures overlay references
4. **Constants and system keywords** — Unnamed lines with `DATE`, `TIME`, `PAGNBR` at print positions → captured as constants
5. **Conditioning indicators** — Fields/keywords conditioned on indicators → captured with conditioning info
6. **Reference fields** — Fields using `R` reference marker and `REFFLD` keyword → captured with reference info

### Edge Cases

- Constants with Japanese/CJK text
- Multiple `SPACEA`/`SPACEB`/`SKIPA`/`SKIPB` on same record format
- `BARCODE` keyword with complex parameters
- `POSITION` keyword for precise AFP positioning
- Edit codes and edit words on numeric fields
- `ENDPAGE` keyword for page break control
- System keyword constants with formatting keywords (e.g., `DATE EDTCDE(Y)`)
- Continuation lines spanning multiple keywords

## Positional Entries for Printer Files (Positions 1-44)

| Position(s) | Name | Description |
|---|---|---|
| 1-5 | Sequence number | Optional, extracted by normalizer |
| 6 | Form type | Always `A` |
| 7 | Comment | `*` = comment line |
| 8-16 | Condition | Conditioning indicators (3 slots × 3 chars) |
| 17 | Type of name | `R` = record, `K` = key, blank = field/constant |
| 18 | Reserved | Always blank |
| 19-28 | Name | Record format name or field name |
| 29 | Reference | `R` = reference field |
| 30-34 | Length | Field length (right-justified) |
| 35 | Data type | `A`, `S`, `P`, `B`, etc. |
| 36-37 | Decimal positions | For numeric fields |
| 38 | Usage | Typically blank for PRTF (output-only implied) |
| 39-41 | Line | Print line number |
| 42-44 | Position | Horizontal print position |

> [!IMPORTANT]
> **Key PRTF differences from DSPF:**
> - Column 38 (Usage) is typically **not used** — PRTF fields are inherently output-only
> - Line/position (cols 39-44) refer to **print coordinates** on paper, not screen coordinates
> - No subfile constructs (SFL/SFLCTL) — PRTF is purely output
> - No function keys (CAxx/CFxx) — no user interaction
> - Additional spacing/skipping keywords control vertical page flow

## Success Criteria

1. ✅ Parses sample PRTF files and produces valid IR JSON
2. ✅ Correct `sourceType`: `PRTF`
3. ✅ File-level keywords captured: `REF`, `INDARA`, `INDTXT`, etc.
4. ✅ Record formats capture: name, keywords, fields, constants
5. ✅ Named fields include: name, data type, length, decimal positions, print line, print position, and all keywords
6. ✅ Constants (unnamed literal text and system keywords) captured separately
7. ✅ Conditioning indicators on both fields and keywords are captured
8. ✅ Comments preserved in `comments` array
9. ✅ `sourceLines` array captures every raw source line
10. ✅ Continuation lines correctly merged into parent keyword list
11. ✅ Implements `As400Parser` interface — plugs into existing framework
12. ✅ Registered in CLI — `.prtf` extension auto-detected and dispatched
13. ✅ REFFLD resolution via `DdsRefResolver`

## Constraints & Assumptions

**Technical Constraints:**
- Same DDS A-spec column layout as PF/LF/DSPF (columns 1-80)
- Keywords at columns 45-80 with `+` continuation at column 80
- Print coordinates occupy columns 39-44 (line in 39-41, position in 42-44)
- The IR JSON envelope (`metadata`, `content`, `dependencies`) must not change
- Must reuse existing `DdsKeywordParser` for keyword parsing

**Assumptions:**
- Source files use `.prtf` extension
- One source member = one PRTF definition (may have multiple record formats)
- Normalizer uses standard 80-char padding (same as RPG/DDS/DSPF)
- Raw-line parsing approach — no ANTLR grammar needed
- PRTF model classes can reuse DSPF model classes (ConditioningIndicator, ConditionedKeyword, etc.) since the field/constant structure is nearly identical

## Questions & Open Items

1. ~~**Sample PRTF files**~~ — Need sample `.prtf` files for testing. Create sample files based on IBM documentation examples
2. **AFP-specific keywords** — `AFPRSC`, `INVDTAMAP`, `INVMMAP` are captured generically. No special handling needed
3. **Usage column (38)** — PRTF rarely uses this (all fields are output). Parser should still capture it if present
4. **BARCODE parameters** — Complex parameter syntax captured generically by `DdsKeywordParser`
