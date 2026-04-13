---
phase: requirements
feature: analyzer
title: "Phase 2: Analyzer — Cross-Reference & Schema Extraction"
description: "Build analysis layer on top of parsed IR JSON to extract cross-references, database schemas, and business logic"
---

# Requirements: Analyzer (Cross-Reference & Schema Extraction)

## Problem Statement

The AS400_Parser has successfully parsed all source types (RPG3, RPGLE, DDS, DSPF, PRTF, CL) into IR JSON. However, these IR documents exist **in isolation** — there is no analysis that connects them to understand:
- Which programs call which other programs
- Which programs use which database files and how (read/write/update/delete)
- How DDS physical/logical files map to relational database schemas
- What business logic and validation rules are encoded in the programs

Without this analysis layer, the migration pipeline cannot generate target code (Java, SQL, Angular) because it lacks understanding of the **relationships** between components.

## Goals & Objectives

### Primary Goals
1. **Cross-Reference Index** — Build a unified index connecting programs ↔ files ↔ calls
2. **Database Schema Extraction** — Convert DDS PF/LF IR into relational schema model (tables, columns, indexes, constraints)
3. **Program-File Usage Map** — Track which I/O operations each program performs on each file

### Secondary Goals
4. **Indicator Flow Analysis** — Track where indicators are set and checked across specs
5. **Copy Member Resolution Tracking** — Map /COPY references to resolved content

### Non-Goals
- Code generation (Phase 3)
- Business logic transformation to target language
- UI migration (DSPF → Angular)

## User Stories & Use Cases

### US-1: Database Schema Extraction
As a migration engineer, I want to convert DDS PF IR into a relational table model so that I can generate PostgreSQL DDL.

**Acceptance:** Given STUDNTPF.pf.json, produces a `TableDefinition` with:
- Table name: STUDNTPF
- 14 columns with correct SQL types (A→VARCHAR, S→NUMERIC, etc.)
- Primary key: (STUSCL, STUID)
- UNIQUE constraint
- Column comments from TEXT keywords

### US-2: Logical File → Index/View
As a migration engineer, I want to convert DDS LF IR into index/view models so that I can generate CREATE INDEX or CREATE VIEW statements.

**Acceptance:** Given STUDNTL1.lf.json, produces an `IndexDefinition` with:
- Associated table: STUDNTPF (from PFILE keyword)
- Key fields: STUNAM ASC, STUID ASC
- Select/Omit conditions (if any)

### US-3: Cross-Reference Index
As a migration engineer, I want a unified index showing which programs call which programs, and which programs use which files, so that I understand the application dependency graph.

**Acceptance:** Given all IR JSON files from student-mgmt app, produces:
- Call graph: MNUCL → MNUPRG, STUPRG, STULST, STURPT
- File usage: STUPRG uses STUDNTPF (READ, WRITE, UPDATE), STUDSPF (DSPF)
- Copy member usage: STUPRG includes STUDNTCPY, SCHOOLCPY

### US-4: Program-File Usage Map
As a migration engineer, I want to know what I/O operations each program performs on each file so that I can generate the correct data access layer.

**Acceptance:** For each program, lists:
- FILES: [{name, operations: [CHAIN, READ, READE, WRITE, UPDATE, DELETE]}]

## Success Criteria

1. **Schema Extraction**: All 7 PF files → correct table definitions, all 6 LF files → correct index definitions
2. **Cross-Reference**: Correctly identifies all CALL/EXSR targets in the 4 RPG programs + 1 CL program
3. **File Usage**: Correctly maps all file I/O operations for each program
4. **Test Coverage**: ≥90% unit test coverage on new analyzer code
5. **Output Format**: Export a *single* `AnalysisResult` JSON file per analysis run that encompasses the entire application context, so the dependency graph is unified and ready for downstream tools.

## Constraints & Assumptions

### Technical Constraints
- Must work with existing IR JSON format — no changes to parser output
- Must be pure Java (same Gradle project, `parser-core` module)
- Must handle both RPG3 and RPGLE IR formats
- Must handle CL IR format for call extraction

### Assumptions
- DDS REF/REFFLD cross-references are already resolved by DdsRefResolver
- All IR JSON files for a given application are available at analysis time
- PF files define the canonical field definitions; LF files reference PF via PFILE keyword
- **Error Handling**: Missing dependencies (like unparsed CALL targets) or unknown DDS type mappings will be gracefully bypassed via console warnings, without throwing exceptions or halting the analysis process.

## Questions & Open Items

- ~~Should the analyzer output be a new JSON format or extend IrDocument?~~ → New standalone JSON format (AnalysisResult)
- ~~Should we support JOIN logical files in the initial version?~~ → Yes, basic support (Inner Joins via `JFILE` keyword, without complex conditions/outer joins initially).
