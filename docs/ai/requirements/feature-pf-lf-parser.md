---
phase: requirements
title: Requirements & Problem Understanding
description: Clarify the problem space, gather requirements, and define success criteria
---

# Requirements & Problem Understanding — PF/LF DDS Parser

## Problem Statement

**What problem are we solving?**

- AS/400 Physical File (PF) and Logical File (LF) definitions are written in **DDS (Data Description Specifications)** — a fixed-format, column-positional source language. Migration developers need a structured, machine-readable representation of these definitions to understand database schemas and data access paths.
- The project already has a **working RPG3 parser** that produces IR JSON output using the `As400Parser` framework. The IR JSON template already defines placeholders for `DDS_PF` and `DDS_LF` source types. What's missing is the **parser that produces them**.
- PF/LF DDS uses the **A-spec** (indicated by `A` in column 6) with a fixed column layout for record formats, field definitions, key specifications, and select/omit criteria.
- Unlike RPG3 which has 7 spec types, DDS has **one spec type (A-spec)** but with multiple line types distinguished by positions 17 (name type indicator) and 38 (keyword area).

## Processing Pipeline

```
Raw DDS Source → Stage 1: Normalize → Stage 2: Classify Lines → Stage 3: Parse Keywords → Stage 4: Build IR → Stage 5: Serialize JSON
```

### Stage 1: Source Normalization (Reuse)
Reuse existing `SourceNormalizer` with standard 80-char padding. Handles tab expansion, encoding cleanup, sequence number extraction.

### Stage 2: Line Classification
Classify each line by column 17 entry type (`R`=record, `K`=key, `S`=select, `O`=omit, `J`=join, blank=field) and column 7 (`*`=comment).

### Stage 3: Keyword Parsing
Parse keyword area (columns 45-80). Merge continuation lines (`+` at column 80). Handle `KEYWORD(args)` syntax.

### Stage 4: IR Building
Build `DdsPfContent` or `DdsLfContent` from classified/parsed lines. Group fields, keys, select/omit, and join specs under their record formats.

### Stage 5: JSON Serialization (Reuse)
Reuse existing `IrJsonSerializer` to produce IR JSON output.

## Goals & Objectives

**Primary Goals:**
1. Build a PF/LF DDS parser that produces IR JSON conforming to the existing IR template (`DDS_PF` and `DDS_LF` source types)
2. Reuse the existing common framework (`As400Parser`, `IrDocument`, `SourceNormalizer`, `IrJsonSerializer`)
3. Parse all DDS A-spec line types: file-level keywords, record format, field definitions, key specifications, and select/omit criteria
4. Handle continuation lines (lines with `+` at end of keyword area)
5. Parse keyword syntax: `KEYWORD`, `KEYWORD(arg)`, `KEYWORD(arg1 arg2)`, `KEYWORD('text')`, `COLHDG('val1' 'val2')`
6. Support Japanese/CJK text in `TEXT()` and `COLHDG()` keywords

**Secondary Goals:**
- Provide meaningful error/warning messages with source line locations
- Support partial parsing (produce partial IR if some lines fail to parse)
- Performance target: < 100ms per file

**Non-Goals (out of scope):**
- DSPF (Display File) or PRTF (Printer File) parsing — separate features
- Semantic validation (e.g., checking that LF field names match PF field names, REF resolution)
- Code generation from IR — downstream concern

## User Stories & Use Cases

**As a migration developer**, I want to parse PF DDS source and get a complete IR JSON document with record formats, field definitions (name, type, length, decimal positions), and key specifications, so that I can generate DDL (CREATE TABLE) statements for target databases.

**As a migration developer**, I want to parse LF DDS source and get an IR JSON document with the based-on physical file reference (PFILE), key definitions, and select/omit criteria, so that I can generate CREATE INDEX / CREATE VIEW statements.

**As a data analyst**, I want to extract field-level keywords (TEXT, COLHDG, VALUES, DFT, EDTCDE, EDTWRD) from PF definitions to understand column semantics without reading raw DDS source.

**As a migration developer**, I want to parse Join Logical Files with JFILE/JOIN/JFLD definitions, so that I can understand multi-table relationships and generate equivalent JOIN queries in modern SQL.

### Key Scenarios

1. **Physical File parse** — Parse `.pf` source → produce IR JSON with `sourceType: "DDS_PF"`, record formats, fields, keys, file-level keywords
2. **Simple Logical File parse** — Parse `.lf` source with `PFILE` → produce IR JSON with re-ordered keys, select/omit criteria
3. **Multiple-format LF parse** — Parse `.lf` source with multiple record formats from different PFs
4. **Join Logical File parse** — Parse `.lf` source with `JFILE`/`JOIN`/`JFLD` → produce IR JSON with join specifications
5. **Continuation lines** — Keywords spanning multiple lines via `+` continuation are merged into parent field/record
6. **Comment lines** — Lines with `*` in column 7 (after `A` in column 6) are preserved as comments
7. **Batch parse** — Parse a directory of `.pf` and `.lf` files via the existing Python CLI batch mechanism

### Edge Cases

- Continuation lines with `+` in column 80 for multi-line keywords
- `COLHDG()` with multiple quoted arguments: `COLHDG('学校' 'コード')`
- Japanese text in `TEXT()` values
- `VALUES()` keyword with multiple values: `VALUES('A' 'B' 'C' 'D' 'F')`
- Blank lines between field definitions
- File-level keywords (e.g., `UNIQUE`) before the record format line
- Multiple record formats in a single PF (rare but valid)
- Derived fields in LFs: `CONCAT`, `SST`, `RENAME` keywords
- `REF`/`REFFLD` references (stored symbolically, not resolved)
- Join LFs with multiple join pairs (`JFLD` per pair)
- `ALL` keyword as catch-all in select/omit rules
- `RANGE` keyword with two quoted values

## Success Criteria

1. ✅ Parses all sample PF files (STUDNTPF, CLASSPF, SCHOOLPF, STUCLSPF) and produces valid IR JSON
2. ✅ Parses all sample LF files (STUDNTL1, STUDNTL2, CLASSL1, SCHOOLL1, STUCLSL1) and produces valid IR JSON
3. ✅ Correct `sourceType`: `DDS_PF` for physical files, `DDS_LF` for logical files
4. ✅ Field definitions include: name, data type (A/S/P/B/F/H/L/T/Z/G), length, decimal positions, usage (B/I/N), and all keywords
5. ✅ Key specifications capture field name, sort order, and key keywords (DESCEND/SIGNED/UNSIGNED/ABSVAL)
6. ✅ Select/omit criteria (S/O indicators, COMP/RANGE/VALUES/ALL keywords) are captured for logical files
7. ✅ Join LF specifications (J entry type, JFILE/JOIN/JFLD) are captured
8. ✅ Derived field keywords (CONCAT/SST/RENAME) are captured for LF fields
9. ✅ File-level keywords (UNIQUE, REF, FIFO, LIFO, FCFO, ALTSEQ) are captured
10. ✅ Record-level keywords (TEXT, PFILE, JFILE, FORMAT) are captured
11. ✅ Continuation lines are correctly merged into parent keyword list
12. ✅ Comments are preserved in the `comments` array
13. ✅ `sourceLines` array captures every raw source line
14. ✅ Implements `As400Parser` interface — plugs into existing framework

## Field Usage (Column 38)

Usage field semantics differ by file type:

| Value | Meaning | Default For | Description |
|---|---|---|---|
| `B` | Both | **PF** (implicit) | Field can be read (input) and written (output) |
| `I` | Input-only | **Join LF** (implicit) | Read-only, cannot update/write |
| `N` | Neither | — | Field not exposed to programs; used for internal join keys or intermediate fields |
| `O` | Output-only | — | Write-only (rare in PF/LF) |
| `H` | Hidden | — | Hidden field (DSPF only, captured for future use) |
| `M` | Message | — | Message field (DSPF only, captured for future use) |

> **Note:** PF fields default to `B` (Both) when usage is blank. Join LF fields default to `I` (Input-only) when usage is blank. The parser captures the explicit value from column 38; default inference is a downstream concern.

## Constraints & Assumptions

**Technical Constraints:**
- DDS A-spec keywords occupy columns 45–80 with `+` continuation at column 80
- Column positions have strict, fixed meanings (see DDS reference)
- The existing `SourceNormalizer` pads to 80 characters — DDS may need configurable padding
- The IR JSON schema envelope (`metadata`, `content`, `dependencies`) must not change

**Assumptions:**
- Source files use `.pf` extension for physical files and `.lf` extension for logical files
- One source member = one PF or LF definition
- Normalizer uses standard 80-char padding (same as RPG)
- Raw-line parsing approach (same as RPG3 refactored parser) — no ANTLR grammar needed for DDS

## Questions & Open Items

1. ~~**Join Logical Files**~~ — **RESOLVED:** Include join LF support (J entry type, JFILE/JOIN/JFLD) from the start
2. ~~**DDS line width**~~ — **RESOLVED:** Standard 80-column width. Keywords at cols 45-80, continuation via `+` at col 80. Same padding as RPG
3. **REF keyword** — Should the parser resolve `REF(filename)` cross-references to pull field attributes from another PF? → *No, store as symbolic reference only*
