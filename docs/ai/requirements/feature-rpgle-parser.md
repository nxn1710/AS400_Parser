---
phase: requirements
title: Requirements & Problem Understanding
description: Clarify the problem space, gather requirements, and define success criteria
---

# Requirements & Problem Understanding — RPGLE Parser

## Problem Statement

**What problem are we solving?**

- The AS400 Parser currently handles RPG3 (fixed-format RPG III), DDS, DSPF, PRTF, and CL sources — but **not RPGLE** (RPG IV / ILE RPG), the most widely used RPG variant in modern AS/400 systems.
- RPGLE supports two coding styles: **fixed-format** (column-positional, like RPG3 but with D-specs and P-specs) and **free-format** (keyword-driven, no column restrictions), often mixed in the same source file.
- Organizations migrating from AS/400 need structured parsing of RPGLE source code into the same IR JSON format used by other parsers in the project, enabling code generation, dependency analysis, and modernization pipelines.

## Processing Pipeline

```
Raw Source → Stage 1: Normalize → Stage 2A: Fixed-Format Position Parse
                                → Stage 2B: Free-Format ANTLR Parse
              → Stage 3: IR Assembly → Stage 4: IR JSON
```

### Stage 1: Source Normalization (Pre-Parser)
Reuse existing `SourceNormalizer` — same as RPG3. Trim, pad, tab-expand, encoding. The normalizer is shared across all parsers.

### Stage 2A: Fixed-Format Position Parse (RpgleFixedParser)
RPGLE fixed-format lines use column positions just like RPG3, with additions:
- **H-spec** (col 6 = 'H'): Control options with keyword area (cols 44–80)
- **F-spec** (col 6 = 'F'): File declarations with keyword continuation
- **D-spec** (col 6 = 'D'): Definition specs — NEW vs RPG3 (standalone fields, data structures, prototypes, procedure interfaces, constants)
- **C-spec** (col 6 = 'C'): Calculation specs — superset of RPG3 C-specs plus EVAL, EVALR, CALLP, etc.
- **I-spec** (col 6 = 'I'): Input specs — same as RPG3
- **O-spec** (col 6 = 'O'): Output specs — same as RPG3
- **P-spec** (col 6 = 'P'): Procedure specs — NEW (begin/end procedure boundary)

Column positions are defined in `docs/ai/knowledge/rpgle-fixed-doc.json`.

### Stage 2B: Free-Format ANTLR Parse (RpgleFreeParser)
RPGLE free-format lines use keyword-driven syntax. An ANTLR4 grammar already exists at `grammar/RpgleLexer.g4` and `grammar/RpgleParser.g4`. The free-format parser will:
1. Feed normalized source through the ANTLR lexer/parser
2. Walk the parse tree with a visitor pattern
3. Extract free-format statements (DCL-S, DCL-DS, DCL-PR, DCL-PI, DCL-PROC, CTL-OPT, IF/ELSE/ENDIF, DOW/DOU, SELECT/WHEN/OTHER, EVAL, etc.)

### Stage 3: IR Assembly (RpgleIrBuilder)
Combine results from fixed-format and free-format parsing into a unified IR document:
- Merge specs from both sources
- Build symbol table
- Extract dependencies (/COPY, /INCLUDE, CALL, CALLP)
- Handle mixed-format source files

### Stage 4: IR JSON Serialization
Reuse existing `IrJsonSerializer` — shared across all parsers.

## Goals & Objectives

**Primary Goals:**
1. Parse 100% of RPGLE source code (both fixed and free format) into IR JSON
2. Output IR JSON with **same structure** as RPG3Parser output
3. Fixed-format parsing uses **position-based extraction** (no grammar needed)
4. Free-format parsing uses **ANTLR4** with existing grammar files
5. Handle mixed-format source files (fixed + free in same file)
6. Parse all RPGLE spec types: H, F, D, C, I, O, P
7. Support `/COPY` and `/INCLUDE` directive resolution
8. Generate `symbolTable`, `dataStructures`, `dependencies`, `subroutines` sections

**Secondary Goals:**
- Meaningful error/warning messages with source line locations
- Support partial parsing (produce partial IR if some lines fail)
- Fast performance (< 1 second for 1000-line source)

**Non-Goals (out of scope):**
- Implementing SQL/RPG hybrid parsing (SQLRPGLE) — future feature
- Semantic analysis beyond IR (cross-program dependency resolution)
- Code generation from IR
- Modifying the existing ANTLR grammar

## User Stories & Use Cases

**As a modernization engineer**, I want to parse an RPGLE source file (fixed, free, or mixed) and get a complete IR JSON document matching the RPG3 output structure.

**As a legacy system analyst**, I want to parse an RPGLE codebase and extract dependency information (files, programs, copy members, procedure calls) from every program.

**As a tool builder**, I want to call the RPGLE parser programmatically via the `As400Parser` interface, same as I do for RPG3/DDS/CL.

### Key Scenarios
1. **Fully fixed-format** — Parse a source file using only fixed-format specs
2. **Fully free-format** — Parse a source file using only `**free` / `ctl-opt` style
3. **Mixed format** — Parse a source file with both fixed and free sections
4. **Copy member resolution** — Resolve `/COPY` and `/INCLUDE` directives
5. **Procedure parsing** — Parse procedure-scoped code with P-specs and PR/PI declarations
6. **Error recovery** — Source with syntax oddities → partial IR with errors populated

## Success Criteria

**Parser:**
1. ✅ Parses fixed-format RPGLE using position-based extraction per `rpgle-fixed-doc.json`
2. ✅ Parses free-format RPGLE using the existing ANTLR4 grammar
3. ✅ Handles mixed-format source files correctly
4. ✅ Outputs IR JSON in same structure as RPG3Parser
5. ✅ All 7 spec types parsed: H, F, D, C, I, O, P
6. ✅ D-spec parsing covers: DS, S, C, PR, PI, subfields
7. ✅ P-spec parsing covers: procedure begin/end boundaries
8. ✅ Free-format operations: EVAL, IF/ELSE/ENDIF, DOW, DOU, FOR, SELECT/WHEN, MONITOR, etc.
9. ✅ Symbol table generated from D-specs, I-specs, C-spec results
10. ✅ Dependencies extracted: `/COPY`, `/INCLUDE`, CALL, CALLP, CALLB
11. ✅ CLI integration via `As400ParserCli` with `.rpgle` extension support
12. ✅ Parse errors/warnings include original source line and column

## Technology Choice

| Component | Technology | Rationale |
|---|---|---|
| Fixed Parser | Java position-based | Same approach as RPG3 `Rpg3IrBuilder` — column extraction from normalized lines |
| Free Parser | Java + ANTLR4 | Existing grammar at `grammar/RpgleLexer.g4` + `grammar/RpgleParser.g4` |
| IR Assembly | Java | Combines fixed + free parse results into unified `IrDocument` |
| JSON Serializer | Gson (shared) | Reuse `IrJsonSerializer` |
| Build | Gradle | ANTLR4 plugin for grammar compilation |

## Constraints & Assumptions

**Technical Constraints:**
- RPGLE fixed-format: 80 columns (cols 1–5 sequence, 6 form type, 7–80 spec-dependent)
- RPGLE free-format: No column restrictions, semicolon-terminated statements
- `**FREE` at col 1 indicates fully free-format source
- Mixed format: fixed specs can appear alongside free-format code in the same file
- Position descriptions from `rpgle-fixed-doc.json` are 1-based
- IR JSON schema must match RPG3Parser output structure exactly
- Existing ANTLR grammar (RpgleLexer.g4 / RpgleParser.g4) must not be modified

**Assumptions:**
- Source files use standard text encoding (EBCDIC-to-ASCII converted)
- One source member = one RPGLE program
- Copy member resolution is optional
- The project targets Java 17+, ANTLR 4.13+
- Test fixtures available at `parser-core/src/test/resources/rpgle/`
