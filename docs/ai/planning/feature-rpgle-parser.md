---
phase: planning
title: Task Breakdown & Planning
description: Break down the implementation into manageable tasks with dependencies and estimates
---

# Task Breakdown & Planning — RPGLE Parser

## Implementation Order

Tasks are ordered by dependency. Foundation first, then parsers, then assembly, then integration.

---

## Task 1: Model Classes
**Priority:** P0 — Foundation
**Effort:** Medium
**Dependencies:** None

Create the RPGLE IR model classes following RPG3 patterns:
- `RpgleContent.java` — Top-level content with all spec arrays
- `ControlSpec.java` — H-spec model with keyword area
- `FileSpec.java` — F-spec model with file declaration fields + keywords
- `DefinitionSpec.java` — D-spec model (DS/S/C/PR/PI types)
- `CalcSpec.java` — C-spec model with traditional + extended factor 2
- `InputSpec.java` — I-spec model (reuse RPG3 pattern)
- `OutputSpec.java` — O-spec model (reuse RPG3 pattern)
- `ProcedureSpec.java` — P-spec model (begin/end boundary)
- `FreeFormatStatement.java` — Free-format statement model

**Acceptance:** All model classes compile and match the IR schema field names.

---

## Task 2: RpgleFixedParser — Position-Based Parser
**Priority:** P0 — Core
**Effort:** Large
**Dependencies:** Task 1

Implement fixed-format parsing using column-position extraction:
- Parse H-spec (col 6='H'): keywords from cols 7-80
- Parse F-spec (col 6='F'): filename, type, format, device, keywords
- Parse D-spec (col 6='D'): name, DS/S/C/PR/PI type, from/to pos, data type, keywords
- Parse C-spec (col 6='C'): control level, indicators, factor1, opcode, factor2/extended, result, resulting indicators
- Parse I-spec (col 6='I'): record identification + field definitions
- Parse O-spec (col 6='O'): record level + field level
- Parse P-spec (col 6='P'): procedure name, begin/end
- Handle comment lines (* in col 7, or *-prefixed)
- Handle directives (/COPY, /INCLUDE, /EJECT, /TITLE, /SPACE, /FREE, /END-FREE)
- Handle continuation lines (D-spec, F-spec keyword continuation)

**Acceptance:** All fixed-format spec types extracted from test fixtures with correct field values.

---

## Task 3: RpgleFreeParser — ANTLR-Based Parser
**Priority:** P0 — Core
**Effort:** Large
**Dependencies:** Task 1

Implement free-format parsing using existing ANTLR grammar:
- Configure ANTLR4 build for RpgleLexer.g4 / RpgleParser.g4
- Implement visitor pattern on parse tree
- Map ANTLR tree nodes to IR model:
  - `ctl_opt` → ControlSpec
  - `fspec` / `fspec_fixed` → FileSpec
  - `dspec` / `dcl_ds` / `dcl_c` → DefinitionSpec
  - `dcl_pr` / `dcl_pi` → DefinitionSpec (PR/PI type)
  - `cspec_fixed` / free operations → CalcSpec / FreeFormatStatement
  - `procedure` → ProcedureSpec
  - `block` (if/dow/dou/for/select/monitor) → nested CalcSpec blocks
  - `subroutine` → SubroutineBlock
- Handle expression extraction for extended factor 2

**Acceptance:** Free-format test fixtures parsed into correct IR model nodes.

---

## Task 4: RpgleIrBuilder — IR Assembly
**Priority:** P0 — Core
**Effort:** Medium
**Dependencies:** Task 2, Task 3

Implement IR document assembly:
- Classify source lines as fixed/free
- Route lines to appropriate parser
- Merge fixed + free results in source order
- Build symbol table from D-specs, I-specs, C-spec results
- Extract dependencies (/COPY, /INCLUDE, CALL, CALLP, CALLB, file references from F-specs)
- Build subroutines index (BEGSR/ENDSR blocks with calledFrom)
- Build data structures from D-spec DS definitions
- Populate source lines with metadata
- Handle **FREE / /FREE directives

**Acceptance:** Mixed-format test fixture produces complete IR with all sections populated.

---

## Task 5: RpgleParserFacade — Public API
**Priority:** P0 — Integration
**Effort:** Small
**Dependencies:** Task 4

Implement the parser facade:
- Implement `As400Parser` interface
- Pipeline orchestration: normalize → classify → parse → build → serialize
- Copy member resolution (/COPY, /INCLUDE)
- Metadata population (source member, file, library, parse status)
- Error handling and failed document creation
- Register in `As400ParserCli` PARSERS list

**Acceptance:** Single file and batch parsing work via CLI with `.rpgle` extension.

---

## Task 6: Tests
**Priority:** P1 — Quality
**Effort:** Medium
**Dependencies:** Task 5

Write comprehensive tests:
- `RpgleFixedParserTest` — Unit tests for each spec type
- `RpgleFreeParserTest` — Unit tests for free-format constructs
- `RpgleIrBuilderTest` — Integration tests for mixed-format
- `RpgleParserIntegrationTest` — End-to-end tests with real source files
- Test fixtures: `simple_fixed.rpgle`, `fully_free.rpgle`, `mixed_format.rpgle`, `all_specs.rpgle`, `copy_directives.rpgle`

**Acceptance:** All tests pass, covering fixed/free/mixed parsing scenarios.

---

## Task 7: CLI Integration & Documentation
**Priority:** P1 — Polish
**Effort:** Small
**Dependencies:** Task 5

- Register `RpgleParserFacade` in `As400ParserCli` PARSERS list
- Update CLI usage text to show RPGLE extensions
- Update Python CLI wrapper if needed
- Update `AGENTS.md` project structure

**Acceptance:** CLI can parse `.rpgle` files end-to-end.

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| ANTLR grammar doesn't compile with current build | Medium | High | Test grammar compilation early in Task 3 |
| Grammar produces different tree structure than expected | Medium | Medium | Inspect parse tree with ANTLR test rig first |
| Mixed-format line classification edge cases | Low | Medium | Build robust classifier with fallback |
| Performance bottleneck in ANTLR parsing | Low | Low | Use SLL mode first, LL fallback |

## Estimated Timeline

| Task | Effort | Status |
|------|--------|--------|
| Task 1: Model Classes | 1-2 hours | Not started |
| Task 2: Fixed Parser | 3-4 hours | Not started |
| Task 3: Free Parser | 3-4 hours | Not started |
| Task 4: IR Builder | 2-3 hours | Not started |
| Task 5: Facade | 1 hour | Not started |
| Task 6: Tests | 2-3 hours | Not started |
| Task 7: CLI Integration | 30 min | Not started |
