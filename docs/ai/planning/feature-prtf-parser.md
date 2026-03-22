---
phase: planning
title: "Planning: PRTF Parser"
description: Task breakdown and implementation plan for Printer File DDS parser
---

# Project Planning & Task Breakdown — PRTF Parser

## Milestones

- [x] Milestone 1: Model classes created and compiling (4 POJOs)
- [x] Milestone 2: IR Builder produces correct PRTF content from sample files
- [x] Milestone 3: Facade + CLI integration complete
- [x] Milestone 4: All tests passing (unit + integration)

## Task Breakdown

### Phase 1: Model Classes (`prtf/model/`)

> Ref: [Implementation §Phase 1](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-prtf-parser.md)

- [x] Task 1.1: `PrtfConstant.java` — `{location, rawSourceLines, conditioningIndicators, printLine, printPosition, text, systemKeyword, keywords}` — text/systemKeyword mutually exclusive; systemKeyword includes DATE/TIME/PAGNBR/MSGCON
- [x] Task 1.2: `PrtfFieldDefinition.java` — `{location, rawSourceLines, conditioningIndicators, name, reference, referenceField, referenceFile, referenceRecordFormat, length, dataType, decimalPositions, usage, printLine, printPosition, source, keywords}`
  - `dataType`: S/A/F/L/T/Z/O/G only — **no P(packed)/B(binary)**
  - `usage`: blank/O = output-only, P = program-to-system (not printed)
  - `printLine`/`printPosition`: nullable Integer (P-fields have no position)
- [x] Task 1.3: `PrtfRecordFormat.java` — `{location, rawSourceLine, conditioningIndicators, name, text, keywords, fields, constants}`
  - **No `recordType`/`sflControlFor`** — no subfile concept
  - **No `keys`** — PRTF does not support key fields (K in col 17)
- [x] Task 1.4: `PrtfContent.java` — `{sourceLines, fileKeywords, recordFormats, comments, parseErrors}`
- [x] Task 1.5: Verify compilation — all models compile, reuse `ConditioningIndicator`/`ConditionedKeyword` from `dspf.model`

### Phase 2: IR Builder

> Ref: [Implementation §Phase 2](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-prtf-parser.md)

- [x] Task 2.1: Create `PrtfIrBuilder.java` — 11-step single-pass (simplified vs DSPF 12-step):
  - [x] 2.1a: Line classification (comment, record `R`, field, constant, system keyword constant, conditioned keyword, continuation)
    - **No key field** (`K`) classification — not valid for PRTF
  - [x] 2.1b: Conditioning indicator parsing — 3 slots of 3 chars from cols 8–16, handle `N` prefix (reuse logic from DSPF)
  - [x] 2.1c: Print coordinate extraction — parseInt cols 39–41 (printLine), 42–44 (printPosition), max 255
  - [x] 2.1d: Continuation line merging (col 80 = `+`) + multi-line text constant merging (unterminated quote lookahead)
  - [x] 2.1e: Conditioned keyword-only line merging — lines with indicators + keyword but no name/position merge into preceding field/constant
  - [x] 2.1f: System keyword constant detection — `DATE`/`TIME`/`PAGNBR`/`MSGCON` unquoted in cols 45–80 (different from DSPF which has SYSNAME/USER)
  - [x] 2.1g: File keyword vs continuation disambiguation (seenRecord flag)
  - [x] 2.1h: Build `sourceLines`, `comments`, `parseErrors` arrays
  - [x] 2.1i: Spurious keyword prevention — `parseConstantKeywordArea` only parses keywords after closing quote
  - [x] 2.1j: `populateFieldReference` — extracts referenceField/referenceFile/referenceRecordFormat from REFFLD keyword
  - [x] 2.1k: Usage validation — only allow blank/O/P (not B/I/H/M as in DSPF)
- [x] Task 2.2: Implement `buildContent(List<String>, String[])` overload for sequence number pass-through

### Phase 3: Facade + CLI

> Ref: [Implementation §Phase 3](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-prtf-parser.md)

- [x] Task 3.1: `PrtfParserFacade.java` — implements `As400Parser`
  - Pipeline: normalize → buildContent(lines, sequenceNumbers) → populateMetadata → IrDocument
  - Metadata: irVersion, sourceType=`"PRTF"`, sourceMember, sourceFile, sourceLibrary, parseInfo
- [x] Task 3.2: Update `As400ParserCli.java` — add `new PrtfParserFacade()` to `PARSERS` list
- [x] Task 3.3: Extend `DdsRefResolver.java` for PRTF
  - Add `PrtfContent` case in `resolve()` method
  - Add `resolvePrtfFields()` helper

### Phase 4: Testing

> Ref: [Implementation §Phase 4](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-prtf-parser.md)

- [x] Task 4.1: Create sample `.prtf` test file(s) — synthetic lines in unit tests covering all scenarios
- [x] Task 4.2: `PrtfIrBuilderTest.java` — **41 unit tests** covering:
  - File keywords, record formats
  - Field columns (name, length, type, usage O/P, printLine, printPos, reference, source)
  - Text constants, system keyword constants (DATE/TIME/PAGNBR/MSGCON)
  - Conditioning indicators (basic + negated + merging)
  - Continuation line merging
  - Program-to-system fields (no position)
  - K-lines treated as unrecognized with warning
- [x] Task 4.3: `PrtfIntegrationTest.java` — covered by regression + facade tests
- [x] Task 4.4: Verify all existing tests still pass (regression) — ✅ all green

## Dependencies

| Component | Status | Notes |
|---|---|---|
| `DdsKeywordParser` | ✅ Reuse | Same keyword syntax, no changes |
| `SourceNormalizer` | ✅ Reuse | 80-char padding |
| `IrJsonSerializer` | ✅ Reuse | Serializes any IrDocument |
| `As400Parser` interface | ✅ Implement | No changes to interface |
| `DdsKeyword`, `DdsComment` | ✅ Reuse | Shared models from `dds.model` |
| `ConditioningIndicator`, `ConditionedKeyword` | ✅ Reuse | From `dspf.model` |
| `SourceLine`, `Location`, `ParseError` | ✅ Reuse | Shared models from `common.model` |
| `DdsRefResolver` | ✅ Extend | Add PRTF REFFLD resolution |
| ~~`KeyDefinition`~~ | ❌ Not used | PRTF has no key fields |

## Timeline & Estimates

| Phase | Effort | Files | Description |
|---|---|---|---|
| Phase 1: Models | Small | 4 new | Simple POJOs (fewer than DSPF — no subfile types, no keys) |
| Phase 2: IR Builder | Medium | 1 new | 11-step processing (simplified vs DSPF 12-step) |
| Phase 3: Facade + CLI | Small | 1 new + 2 modify | Pipeline + CLI + RefResolver |
| Phase 4: Testing | Medium | 2 new + test data | Unit + integration tests |
| **Total** | | **8 new + 2 modify** | |

## Risks & Mitigation

| Risk | Impact | Mitigation |
|---|---|---|
| No sample `.prtf` files available | Low | Create synthetic test files from IBM doc examples |
| PRTF has unknown AFP keywords | None | `DdsKeywordParser` captures all keywords generically |
| P (program-to-system) field handling | Medium | P-fields have no position — skip coordinate extraction when usage=P |
| Conditioned keyword-only line merging | Medium | Reuse proven pattern from DSPF |
| Multi-line text constants | Medium | Reuse unterminated quote lookahead from DSPF |
| Spurious keywords from quoted text | Low | Reuse `parseConstantKeywordArea` from DSPF |

## Resources Needed

- IBM i DDS for Printer Files reference → [prtf-knowledge.md](file:///d:/Code/AS400_Parser/docs/ai/knowledge/prtf-knowledge.md)
- Existing DSPF parser implementation → [feature-dspf-parser.md](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-dspf-parser.md)
- Sample PRTF source files (to be created as test data)
