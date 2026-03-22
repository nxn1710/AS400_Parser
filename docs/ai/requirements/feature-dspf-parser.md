---
phase: requirements
title: Requirements & Problem Understanding
description: Clarify the problem space, gather requirements, and define success criteria
---

# Requirements & Problem Understanding — DSPF Parser

## Problem Statement

**What problem are we solving?**

- AS/400 Display Files (DSPF) define interactive terminal screens — menus, data entry forms, subfile lists. They are written in **DDS (Data Description Specifications)** using the same A-spec column layout as PF/LF, but with additional DSPF-specific features: screen coordinates, display attributes, conditioning indicators, function keys, constants, and subfile constructs.
- The project already has a **working PF/LF DDS parser** (`DdsParserFacade`) and the IR JSON template defines a `DSPF` placeholder (sourceType `DSPF`). What's missing is the **parser that produces DSPF IR JSON**.
- Migration developers need structured representation of screen layouts to understand UI logic, field positioning, indicator-driven display attributes, and subfile configurations for modernizing terminal-based UIs to web/mobile interfaces.
- DSPF shares the DDS A-spec base format but introduces unique constructs not found in PF/LF:
  - **Screen coordinates** (line/position in columns 39-44)
  - **Constants** (unnamed fields with literal text for screen labels/titles)
  - **Display attributes** (`DSPATR`) for visual styling
  - **Function key keywords** (`CAxx`, `CFxx`) for user interaction
  - **Conditioning indicators** (columns 8-16) controlling field visibility/protection
  - **Subfile constructs** (`SFL`, `SFLCTL`, `SFLSIZ`, `SFLPAG`, `SFLDSP`, etc.)
  - **Display size** (`DSPSIZ`) for terminal dimensions

## Processing Pipeline

```
Raw DSPF Source → Stage 1: Normalize → Stage 2: Classify Lines → Stage 3: Parse Keywords → Stage 4: Build IR → Stage 5: Serialize JSON
```

### Stage 1: Source Normalization (Reuse)
Reuse existing `SourceNormalizer` with standard 80-char padding. Handles tab expansion, encoding cleanup, sequence number extraction.

### Stage 2: Line Classification
Classify each line by column 17 entry type (`R`=record format, `K`=key, `H`=help, blank=field/constant) and column 7 (`*`=comment). **Same logic as PF/LF** but DSPF doesn't use `S`, `O`, `J` entry types.

### Stage 3: Keyword Parsing (Reuse)
Reuse existing `DdsKeywordParser` for keyword area (columns 45-80). Continuation lines merging via `+` at column 80. Same `KEYWORD(args)` syntax.

### Stage 4: IR Building
Build `DspfContent` from classified/parsed lines. Group fields and constants under record formats. Handle DSPF-specific constructs: screen coordinates, conditioning indicators, display attributes, subfile relationships.

### Stage 5: JSON Serialization (Reuse)
Reuse existing `IrJsonSerializer` to produce IR JSON output.

## Goals & Objectives

**Primary Goals:**
1. Build a DSPF parser that produces IR JSON with sourceType `DSPF`, conforming to the existing IR template envelope
2. Reuse the common framework (`As400Parser`, `IrDocument`, `SourceNormalizer`, `IrJsonSerializer`, `DdsKeywordParser`)
3. Parse all DDS A-spec features relevant to DSPF: record formats, named fields, constants (unnamed literals), keys, help specs
4. Parse DSPF-specific features:
   - Screen coordinates (line/position from columns 39-44)
   - Display attributes (`DSPATR` keyword with values: HI, RI, UL, BL, CS, PR, ND, PC, etc.)
   - Function key definitions (`CAxx`, `CFxx` at file or record level)
   - Conditioning indicators (columns 8-16) on fields and keywords
   - Subfile constructs (`SFL`, `SFLCTL`, `SFLSIZ`, `SFLPAG`, `SFLDSP`, `SFLDSPCTL`, `SFLCLR`, `SFLEND`)
   - Display size (`DSPSIZ`) file-level keyword
   - Constants (unnamed field lines with quoted literal text and screen position)
   - System keyword constants (`DATE`, `TIME`, `SYSNAME`, `USER`, `PAGNBR`) — unnamed lines with screen position and system keyword (no quotes, no field name)
   - `PRINT` keyword
   - Conditioned keyword lines — keyword-only lines with conditioning indicators (e.g., `A N60  DSPATR(PR)`) merge into the preceding field's keyword list
5. Handle continuation lines (same as PF/LF)
6. Support Japanese/CJK text in constants and keywords

**Secondary Goals:**
- Provide meaningful error/warning messages with source line locations
- Support partial parsing (produce partial IR if some lines fail)
- Performance target: < 100ms per file

**Non-Goals (out of scope):**
- PRTF (Printer File) parsing — separate feature
- Runtime indicator resolution (determining which indicators are set at runtime)
- Screen rendering / visual preview
- Message subfile constructs (`MSGSFL`, `SFLMSGRCD`, `SFLPGMQ`) — can be added as enhancement later
- Window-specific IR modeling (`WINDOW` keyword is captured generically but no special `window` field on the record format model)
- DDS validation rules (checking field coordinate overlap, etc.)

## User Stories & Use Cases

**As a migration developer**, I want to parse DSPF source and get a complete IR JSON document with record formats, field definitions (name, type, length, screen position), display attributes, and function key definitions, so that I can generate web UI components from terminal screens.

**As a migration developer**, I want to understand subfile (SFL/SFLCTL) relationships from the IR, so that I can generate equivalent list/table/grid components for web applications.

**As a UI designer**, I want to extract screen layout information (field positions, labels, constants) from DSPF definitions, so that I can plan modern UI layouts based on existing terminal designs.

**As a migration developer**, I want to understand conditioning indicator usage on fields and display attributes, so that I can generate conditional visibility/protection logic in modern code.

### Key Scenarios

1. **Simple menu screen parse** — Parse `MNUDSPF.dspf` → produce IR JSON with file-level keywords (`DSPSIZ`, `CA03`, `CA12`), one record format with constants (menu text), one input field, one output field
2. **Data entry screen parse** — Parse `STUDSPF.dspf` → produce IR JSON with multiple record formats (`STUSRCH`, `STUDETL`, `STUDEL`), input/output/both-usage fields, conditioning indicators for protection toggle
3. **Subfile list screen parse** — Parse `STULSTD.dspf` → produce IR JSON with SFL record format, SFLCTL record format, subfile keywords (`SFLSIZ`, `SFLPAG`, `SFLDSP`, `SFLDSPCTL`, `SFLCLR`, `SFLEND`), and conditioning indicators on subfile keywords
4. **Constants (screen labels)** — Unnamed lines with literal text and screen positions are captured as constants
5. **Conditioning indicators** — Fields with indicator conditioning (`60`/`N60` patterns) for protection/display attribute toggling
6. **Continuation lines** — Keywords spanning multiple lines via `+` continuation

### Edge Cases

- Constants with Japanese/CJK text (e.g., `'学 生 管 理 シ ス テ ム'`)
- **Multiple `DSPATR` keywords on same field with different conditions** — e.g., `DSPATR(PR)` conditioned on `N60` AND `DSPATR(UL)` conditioned on `60` (field protected when indicator 60 off, underlined when on)
- **Conditioned keyword-only lines** — lines with conditioning indicators + keyword but no field name/position (e.g., `A N60  DSPATR(PR)`) must be merged into the preceding field's keyword list as separate `ConditionedKeyword` entries
- **System keyword constants** — `DATE`, `TIME`, `SYSNAME`, `USER` appear as unnamed lines with screen position and the keyword in cols 45-80 (no quotes). E.g., `A                                  2 70DATE`. These are system-provided values, not quoted text constants
- **`EDTCDE(Y)` on system keyword lines** — continuation keyword on `DATE` line (e.g., `DATE` then `EDTCDE(Y)` on next line) — the edit code must be associated with the system keyword constant
- `EDTCDE(Y)` and `EDTCDE(Z)` on DSPF fields for date/number formatting
- `VALUES()` keyword on input fields for validation
- Subfile with `SFLEND(*MORE)` — keyword with special-value argument
- Multiple record formats in single DSPF (common — one DSPF may have menu, detail, list screens)
- `CA` vs `CF` function key difference (CA does not return changed data, CF does)
- `DSPSIZ(24 80 *DS3)` — display size with multiple arguments
- Screen coordinate `0` (line 0) used for special positioning

## Field Usage in DSPF (Column 38)

| Value | Meaning | Description |
|---|---|---|
| `B` | Both | Input/output field — user can type and program can write |
| `I` | Input-only | User input field (less common in DSPF; typically B is used) |
| `O` | Output-only | Program writes, user cannot modify |
| `H` | Hidden | Field not visible on screen but part of the record |
| `M` | Message | Message field (used in message subfiles) |
| `P` | Program-to-system | Used for certain system operations |

## Success Criteria

1. ✅ Parses all 3 sample DSPF files (`MNUDSPF.dspf`, `STUDSPF.dspf`, `STULSTD.dspf`) and produces valid IR JSON
2. ✅ Correct `sourceType`: `DSPF`
3. ✅ File-level keywords captured: `DSPSIZ`, `CAxx`/`CFxx` (function keys), `PRINT`, etc.
4. ✅ Record formats capture: name, type (normal, SFL, SFLCTL), keywords, fields, constants
5. ✅ Named fields include: name, data type, length, decimal positions, usage, screen line, screen position, display attributes, conditioning indicators, and all keywords
6. ✅ Constants (unnamed literal text with screen coordinates) are captured separately
7. ✅ Subfile constructs: SFL record linked to SFLCTL record; subfile keywords (`SFLSIZ`, `SFLPAG`, `SFLDSP`, `SFLDSPCTL`, `SFLCLR`, `SFLEND`) captured
8. ✅ Conditioning indicators on both fields and keywords are captured (e.g., `60` / `N60`)
9. ✅ Function key definitions (file-level `CAxx`/`CFxx`) captured with indicator number and description
10. ✅ Display attributes (`DSPATR`) with all values (HI, RI, UL, BL, CS, PR, ND, PC) captured
11. ✅ Comments preserved in `comments` array
12. ✅ `sourceLines` array captures every raw source line
13. ✅ Continuation lines correctly merged into parent keyword list
14. ✅ Implements `As400Parser` interface — plugs into existing framework
15. ✅ Registered in CLI — `.dspf` extension auto-detected and dispatched

## Constraints & Assumptions

**Technical Constraints:**
- Same DDS A-spec column layout as PF/LF (columns 1-80)
- Keywords at columns 45-80 with `+` continuation at column 80
- Screen coordinates occupy columns 39-44 (line in 39-41, position in 42-44)
- The IR JSON envelope (`metadata`, `content`, `dependencies`) must not change
- Must reuse existing `DdsKeywordParser` for keyword parsing

**Assumptions:**
- Source files use `.dspf` extension
- One source member = one DSPF definition (may have multiple record formats)
- Normalizer uses standard 80-char padding (same as RPG and PF/LF)
- Raw-line parsing approach (same as PF/LF parser) — no ANTLR grammar needed
- The 3 sample files (`MNUDSPF`, `STUDSPF`, `STULSTD`) are representative of typical DSPF constructs

## Questions & Open Items

1. ~~**Help specifications**~~ — **RESOLVED:** Defer `H` (help) entry type to enhancement; not found in sample files
2. ~~**Message subfiles**~~ — **RESOLVED:** Defer `MSGSFL` / message subfile constructs to enhancement
3. ~~**Window files**~~ — **RESOLVED:** `WINDOW` keyword captured generically by `DdsKeywordParser` but no special IR model field. Window support deferred to enhancement
4. ~~**ERRMSG/ERRMSGID**~~ — **RESOLVED:** Captured as standard `DdsKeyword` entries, no special handling
5. ~~**OVERLAY/ASSUME**~~ — **RESOLVED:** Captured as standard `DdsKeyword` entries, no special handling
6. ~~**System keyword constants (DATE/TIME/SYSNAME/USER)**~~ — **RESOLVED:** Classified as `DspfConstant` with `systemKeyword` field instead of `text` field. `EDTCDE` continuation keywords merge into the constant's keyword list
7. ~~**Conditioned keyword-only lines**~~ — **RESOLVED:** Lines with indicator + keyword but no field name (e.g., `A N60 DSPATR(PR)`) merge into the preceding field's keyword list as `ConditionedKeyword` entries with their own indicators
8. ~~**Multiple DSPATR per field**~~ — **RESOLVED:** A single field can have multiple `DSPATR` keywords with conflicting values under different conditions. Each stored as a separate `ConditionedKeyword`
