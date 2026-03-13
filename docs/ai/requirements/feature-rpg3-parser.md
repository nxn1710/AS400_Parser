---
phase: requirements
title: Requirements & Problem Understanding
description: Clarify the problem space, gather requirements, and define success criteria
---

# Requirements & Problem Understanding — RPG3 Parser

## Problem Statement

**What problem are we solving?**

- IBM AS/400 RPG III programs are maintained in **fixed-format, column-positional source** that is inscrutable to modern development tools. Organizations seeking to **modernize, migrate, or analyze** these legacy systems need a structured, machine-readable representation of the source code.
- There is **no open-source, production-grade parser** that converts RPG3 source into a modern IR JSON format suitable for code generation, dependency analysis, and semantic understanding.
- The IR JSON schema has already been designed (see [feature-ir-json-template.md](file:///d:/Code/AS400_Parser/docs/ai/design/feature-ir-json-template.md)). What's missing is the **parser that produces it**.
- The parser must be part of a **common framework** that will also support DDS, DSPF, CL, and RPG4 parsers in the future.

## Processing Pipeline

```
Raw Source → Stage 1: Normalize → Stage 2: ANTLR Parse → Stage 3: AST Walk → Stage 4: IR JSON
```

### Stage 1: Source Normalization (Pre-Parser)

Before ANTLR parsing, raw RPG3 source must be **normalized** with basic text cleanup. This step is **language-agnostic** and reusable for DDS, CL, DSPF, and RPG4. The normalizer only handles raw text issues — all semantic processing (comments, control flow, `/COPY` resolution, compile-time data splitting) is done by the parser.

| Task | Description |
|---|---|
| **Trim** | Remove trailing whitespace from each line |
| **Pad** | Pad all lines to exactly 80 characters (RPG3 fixed-width) |
| **Tab expansion** | Expand tab characters to spaces at correct column stop positions |
| **Line ending** | Standardize to LF (strip CR) |
| **Encoding cleanup** | Strip control characters but preserve DBCS (Japanese/CJK) — support EBCDIC CCSID 930/5035, Shift-JIS, EUC-JP, UTF-8 |
| **Sequence numbers** | Extract cols 1–5 (preserve as metadata), normalize to spaces |

**Output:** A normalized source object containing:
- Array of 80-char normalized lines with original line number mapping
- Extracted sequence numbers per line
- Normalization warnings (e.g., truncated lines > 80 chars, encoding issues)

> [!IMPORTANT]
> The normalizer must preserve the **original line number mapping** so that parse errors and IR locations reference the original source, not the normalized version.

### Stage 2: ANTLR Parse
Feed normalized source into the RPG3 ANTLR grammar → produce parse tree.

### Stage 3: AST Walk (Visitor/Listener)
Walk the parse tree to build the IR data model — extract specs, build AST for expressions, resolve types.

### Stage 4: IR JSON Serialization
Serialize the IR data model to JSON conforming to the IR schema.

## Goals & Objectives

**Primary Goals:**
1. Build a production-grade RPG3 source parser that produces compliant IR JSON output
2. Establish a **shared parser framework** ("common project") reusable across all AS400 source types (DDS, DSPF, CL, RPG4)
3. Implement a **source normalization** stage as the first pipeline step (reusable across all source types)
4. Handle real-world RPG3 codebases — not just toy examples
5. Parse all 7 RPG3 specification types (H, F, E, L, I, C, O)
6. Produce full AST for calculation specs (expressions, control flow, subroutines)
7. Generate `symbolTable`, `dataStructures`, and `dependencies` sections
8. Support `/COPY` member resolution (both inlined and referenced modes)

**Secondary Goals:**
- Provide meaningful error/warning messages with source line locations
- Support partial parsing (produce partial IR if some lines fail to parse)
- Achieve fast performance (< 1 second for a 1000-line source)

**Non-Goals (explicitly out of scope for this feature):**
- Implementing parsers for DDS, DSPF, CL, or RPG4 (future features)
- Semantic analysis beyond what the IR captures (e.g., cross-program dependency resolution)
- Code generation from IR to modern languages
- UI or API layer — this is a library/CLI tool

## User Stories & Use Cases

**As a modernization engineer**, I want to parse an RPG3 source file and get a complete IR JSON document, so that I can feed it into code generation and analysis tools.

**As a legacy system analyst**, I want to parse an entire RPG3 codebase and extract dependency information (files, programs, data areas, copy members) from every program.

**As a tool builder**, I want a library I can import and call programmatically to parse RPG3 source into IR JSON, so I can integrate it into my modernization pipeline.

**As a developer extending the parser framework**, I want a clear, well-documented common framework so I can implement a DDS or CL parser following the same patterns.

### Key Scenarios

1. **Single file parse** — Parse one `.rpg` source member → produce one IR JSON file
2. **Batch parse** — Parse a directory of source files → produce IR JSON per file
3. **Copy member resolution** — Resolve `/COPY` directives by inlining referenced members
4. **Error recovery** — Source with syntax oddities or unsupported constructs → partial IR with errors array populated
5. **Extension spec arrays** — Compile-time data at end of source is correctly associated with E-spec array definitions

### Edge Cases

- Continuation lines in F-specs and O-specs
- Blank lines and comment lines interspersed
- Non-standard comment forms (conditional compilation, compiler directives)
- Overlapping column semantics in I-specs and O-specs (dual-purpose columns)
- Very long programs (5000+ lines)
- Copy members that contain partial specifications (e.g., field definitions without record header)
- Compile-time data (**CTDATA) at end of source
- Mixed-case source (RPG3 is case-insensitive)

## Success Criteria

**Normalizer (built into Java library):**
1. ✅ Trims trailing whitespace and pads lines to exactly 80 characters
2. ✅ Expands tabs, standardizes line endings to LF
3. ✅ Cleans control characters while preserving DBCS (Japanese/CJK) content
4. ✅ Extracts and strips sequence numbers (cols 1–5)
5. ✅ Preserves original line number mapping

**Parser:**
6. ✅ Parses the sample CUSTINQ program and produces valid IR matching the schema
7. ✅ Handles all 7 spec types: H, F, E, L, I, C, O
8. ✅ Produces correct AST for all RPG3 opcodes (~120 opcodes)
9. ✅ Generates `symbolTable` with all fields, types, and definition sources
10. ✅ Expression AST with resolved types on identifier nodes
11. ✅ Native AS400 data type codes (A, S, P, B, D, T, Z, G, O)
12. ✅ Null convention compliance: `null` = N/A, `""` = blank, `0` = zero
13. ✅ `calculationSpecs` includes `subroutineBlock` nodes; `subroutines` is convenience index
14. ✅ Parse errors/warnings include original source line and column
15. ✅ CLI tool can parse a file and output IR JSON to stdout or file
16. ✅ Common framework can be reused for future parser types

## Technology Choice

### Architecture: **Java Parser Library** + **Python CLI Wrapper**

| Component | Language | Rationale |
|---|---|---|
| **Parser Library** | Java | ANTLR grammar + AST walk + IR generation + normalizer — all in one library. Produces IR JSON. |
| **CLI Wrapper** | Python | Thin wrapper calling Java library via subprocess. Handles batch processing, file I/O, and user-facing CLI. |

The **Java library** is the core product — it takes source text in and produces IR JSON out. The normalizer is built into the library (Java) as the first step of the parse pipeline. The Python CLI simply invokes the Java library and handles orchestration (batch, file discovery, output routing).

```
[Python CLI] → calls → [Java Parser Library]
                          ├── Normalizer (trim, pad, tabs, encoding, sequence)
                          ├── ANTLR Lexer/Parser
                          ├── AST Visitor (IR model builder)
                          └── JSON Serializer → IR JSON output
```

### Parser Approach: **ANTLR4 — Reuse Existing RPGLE Grammar**

The project already has a comprehensive RPGLE grammar:
- [RpgLexer.g4](file:///d:/Code/AS400_Parser/grammar/rpgle/RpgLexer.g4) — 2667 lines, handles all fixed-format and free-format lexing
- [RpgParser.g4](file:///d:/Code/AS400_Parser/grammar/rpgle/RpgParser.g4) — 2598 lines, handles all specs and opcodes

#### Reuse Strategy: Fork RPGLE Grammar → RPG3 Grammar

**What can be reused directly (RPG3 = RPG IV fixed-format subset):**
- `FIXED_CalcSpec` lexer mode — column-position C-spec tokenization (indicators, factor1/2, opcode, result)
- `FIXED_InputSpec` mode — I-spec record identification and field definitions
- `FIXED_FileSpec` mode — F-spec file declarations
- `FIXED_OutputSpec` mode — O-spec output record/field definitions
- `HeaderSpecMode` — H-spec compiler options
- All fixed-format opcodes: `csIFxx`, `csDOWxx`, `csDOUxx`, `csCASxx`, `csANDxx`, `csORxx`, etc.
- Indicator handling: `onOffIndicatorsFlag`, `cs_indicators`, `GeneralIndicator`, `ControlLevelIndicator`, etc.
- Comment handling: `FIXED_CommentMode`, `COMMENT_SPEC_FIXED`
- Directive handling: `/COPY`, `/EJECT`, `/TITLE`, `/SPACE`

**What must be removed (RPG IV / ILE only):**
- Free-format modes: `FREE_Start`, `FREE`, `FreeOpExtender`
- D-spec declarations: `DCL-S`, `DCL-DS`, `DCL-PR`, `DCL-PI`, `DCL-PROC`, `DCL-C`, `DCL-F`
- P-spec procedures: `FIXED_ProcedureSpec`
- CTL-OPT (free-format H-spec)
- Built-in functions (`%TRIM`, `%SUBST`, etc.) — RPG3 doesn't have BIFs
- SQL embedding (`EXEC SQL`)
- RPG IV-only opcodes: `EVAL`, `EVALR`, `FOR`, `MONITOR`, `ON-ERROR`, `SELECT/WHEN/OTHER`, `CALLP`

**What must be added (RPG3-specific):**
- E-spec (Extension specs) — arrays/tables, not in RPGLE grammar
- L-spec (Line Counter specs) — printer overflow, not in RPGLE grammar
- Compile-time data (`**CTDATA` / `**` at end of source) — partially in lexer as `END_SOURCE`
- Data Structure definitions on I-specs (RPG3 style, different from D-spec `DCL-DS`)

> [!NOTE]
> The RPGLE grammar uses Java-specific actions (`System.out.println`). These are debug-only and can be cleaned up. The grammar is Java-targeted, which aligns with the recommended Java parser core.

## Constraints & Assumptions

**Technical Constraints:**
- RPG3 source is 80 columns wide (columns 1–80, with 1–5 as sequence, 6 as form type, 7–74/80 as spec-dependent)
- Column positions have strict, fixed meanings per spec type
- Indicators are 2-character codes with specific valid ranges per context
- The IR JSON schema is already defined and **must not change** without explicit design approval

**Assumptions:**
- Source files use EBCDIC-to-ASCII converted text (standard file encoding)
- One source member = one RPG3 program (no multi-program source files)
- Copy member resolution is optional (parser can run without resolving them)
- The project targets Java 17+ (parser core) and Python 3.10+ (IR generation/tooling)
- ANTLR4 version 4.13+ for grammar compilation
- Existing RPGLE grammar (`grammar/rpgle/`) is the starting point for the RPG3 grammar fork

## Questions & Open Items

1. ~~**Copy member file system**~~ — **RESOLVED:**

   **Disk layout** mirrors AS400 `library/file/member` structure:
   ```
   source_root/
   ├── MYLIB/QRPGSRC/CUSTINQ.rpg      ← main programs
   ├── MYLIB/QCPYSRC/COPYMBR.rpg      ← copy members
   └── PRODLIB/QRPGSRC/...
   ```
   **Search order** via `--copy-path` (like `*LIBL`):
   - `/COPY FILE,MEMBER` → search each path for `MEMBER.{rpg,rpg3,mbr,""}`
   - `/COPY LIB/FILE,MEMBER` → resolve as `source_root/LIB/FILE/MEMBER`
   - First match wins (left-to-right). Not found → warning in IR `errors`, continue parsing.
2. ~~**Compile-time data**~~ — **RESOLVED:** Compile-time data after `**` is preserved as **raw text blocks** keyed by associated E-spec array/table name. The parser does NOT parse the data into structured arrays — that is a downstream concern. Blocks are associated with E-spec arrays in source order (first block → first E-spec without `fromFileName`, etc.). See design doc §3.1.
3. **RPG3 vs RPG/400**: Should the parser handle RPG/400 extensions (e.g., `/FREE` blocks that some compilers accept)? — *Initially: no*
4. **Output format**: JSON only, or also support YAML/CBOR/MessagePack?  — *Initially: JSON only*
5. **Licensing**: What license for the open-source project?
