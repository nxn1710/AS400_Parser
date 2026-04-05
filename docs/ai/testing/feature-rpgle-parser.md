---
phase: testing
title: Testing Strategy — RPGLE Parser
description: Test cases, coverage, and quality assurance for the RPGLE parser feature
---

# Testing Strategy — RPGLE Parser

## Test Coverage Goals

- **Unit test coverage target:** 100% of new/changed code for `RpgleFixedParser`, `RpgleIrBuilder`
- **Integration test scope:** End-to-end pipeline via `RpgleParserFacade` for all format types (fixed, free, mixed)
- **Edge case coverage:** Null/empty inputs, short lines, unknown spec types, unterminated blocks

## Unit Tests

### RpgleFixedParser — [RpgleFixedParserTest.java](file:///d:/Code/AS400_Parser/parser-core/src/test/java/com/as400parser/rpgle/RpgleFixedParserTest.java)

- [x] H-spec: Single spec with keywords, location, raw source line, multiple specs
- [x] F-spec: File name, file types (I/U/O), device, continuation lines, end line update
- [x] D-spec: Standalone name, definition type (S/C/DS), data type, continuations
- [x] I-spec: Record entry (file name), field entry (field name), multi-entry
- [x] C-spec: EVAL/CALL/CALLP/PLIST/PARM/READ, extended factor 2, continuation, indicators
- [x] O-spec: Record entry (file name, type), field entry
- [x] P-spec: Begin/end, name, keywords, continuation lines
- [x] Comments: Capture, multiple comments
- [x] /COPY and /INCLUDE: Directive extraction, all path formats, location
- [x] Mixed-format: /FREE.../END-FREE block, format type detection, unterminated blocks
- [x] Edge cases: Empty input, null lines, short lines, unknown form types, non-COPY directives
- [x] safeSubstring: Normal range, out-of-bounds, nulls, blanks

### RpgleIrBuilder — [RpgleIrBuilderTest.java](file:///d:/Code/AS400_Parser/parser-core/src/test/java/com/as400parser/rpgle/RpgleIrBuilderTest.java)

- [x] Document structure: Non-null doc, content, dependencies, metadata, errors
- [x] Referenced files: F-spec all type mappings (I→read, O→write, U→update, C→combined), null→default, blank skip, locations, free-format dcl-f
- [x] Called programs: CALL (quoted/unquoted), CALLP (with/without parens), null/blank factor 2, location, free-format callp, multiple calls
- [x] Copy members: FILE,MEMBER; LIB/FILE,MEMBER; FILE/MEMBER; MEMBER-only; blank path; location; multiple
- [x] Metadata: IR version, source types, parsed at, total lines, parser version
- [x] Parse status: Complete (no errors), partial (has errors), complete (warnings only)
- [x] Error propagation: Empty, single, multiple errors

### RpgleFreeParser — [RpgleFreeParserTest.java](file:///d:/Code/AS400_Parser/parser-core/src/test/java/com/as400parser/rpgle/RpgleFreeParserTest.java)

- [x] Statement-level parsing: single/multiple statements, locations, types, multi-line joining, inline comments
- [x] Null/blank/empty input handling: null lines, blank lines, empty input, unterminated statements
- [x] Comment handling: full-line comments, inline comments, comments inside strings (isInsideString)
- [x] Fallback type detection: All 38 statement types (DCL-S/DS/F/PR/PI/C/PROC, END-*, CTL-OPT, IF/ELSE/ENDIF, DOW/DOU/ENDDO, FOR/ENDFOR, SELECT/WHEN/OTHER/ENDSL, MONITOR/ON-ERROR/ENDMON, RETURN, BEGSR/ENDSR/EXSR, READ/CHAIN/WRITE/UPDATE/DELETE, EVAL, CALLP, CLEAR, RESET, assignment, unknown)
- [x] ANTLR parsing: parseFullyFree with statements/locations/text/types, procedures, subroutines, empty source
- [x] parseFullSource: valid source, empty source
- [x] Error handling: initial state, post-parse state

## Integration Tests

### RpgleParserFacade — [RpgleParserIntegrationTest.java](file:///d:/Code/AS400_Parser/parser-core/src/test/java/com/as400parser/rpgle/RpgleParserIntegrationTest.java)

- [x] Interface contract: getSourceType, getSupportedExtensions
- [x] Parse from string: Fixed/free format, all spec types, dependencies, empty source
- [x] Parse from file: All 6 fixture files, source type detection (.rpgle vs .sqlrpgle)
- [x] Metadata: parsedAt, parserVersion, totalLines, memberName, sourceFile, irVersion
- [x] Dependency extraction: CALL/CALLP, /COPY members (all path formats), F-spec files, free-format dcl-f
- [x] Format detection: Fixed, free (**FREE), mixed (/FREE blocks)
- [x] All specs fixture: Coverage of all 7 spec types (H/F/D/I/C/O/P) in single file
- [x] CLI registration: selectParser for .rpgle, .sqlrpgle, not .rpg
- [x] Error handling: Nonexistent file → failed document with error

## Test Data

### Fixture Files (`src/test/resources/rpgle/`)

| Fixture | Purpose |
|---------|---------|
| `simple_fixed.rpgle` | Basic H/F/D/C specs, CALL, CALLP |
| `copy_directives.rpgle` | /COPY and /INCLUDE in all path formats |
| `mixed_format.rpgle` | Fixed H/F/D specs with /FREE.../END-FREE block |
| `fully_free.rpgle` | **FREE with dcl-f, dcl-s, dcl-c, dcl-pr, callp |
| `all_specs.rpgle` | All 7 spec types (H/F/D/I/C/O/P) |
| `simple.sqlrpgle` | Source type detection for SQLRPGLE |

## Test Reporting & Coverage

### Commands

```bash
# Run RPGLE tests only
gradlew test --tests "com.as400parser.rpgle.*"

# Run with verbose output
gradlew test --tests "com.as400parser.rpgle.*" --info
```

### Results

- **~290 tests total**: All passing ✅
  - `RpgleFixedParserTest`: ~100 unit tests (includes boundary tests for all 7 spec types)
  - `RpgleFreeParserTest`: ~65 unit tests (statement-level, fallback type detection, ANTLR, error handling)
  - `RpgleIrBuilderTest`: ~50 unit tests
  - `RpgleParserIntegrationTest`: ~49 integration tests

### Coverage (JaCoCo)

| Class | Instructions | Branches | Lines | Methods |
|-------|-------------|----------|-------|---------|
| `RpgleFixedParser` | 97% | 78% | 98% | 100% |
| `RpgleFreeParser` | 62% | 69% | 68% | 71% |
| `RpgleIrBuilder` | 98% | 73% | 99% | 100% |
| `RpgleParserFacade` | 96% | 57% | 97% | 100% |
| **Package total** | **88.8%** | **73%** | **90%** | **90%** |

### Remaining Gaps

- `RpgleFreeParser` ANTLR tree-walking methods (addStatement, addContextAsStatement, detectStatementType, getOriginalText) are tested indirectly through integration tests
- The ANTLR grammar produces different parse tree structures depending on source, so some branches in the tree-walking code are grammar-dependent
- `RpgleParserFacade` charset branch (57% branches) — the explicit charset path is rarely exercised in test

## Manual Testing

- [ ] Parse real-world RPGLE source files from `rpg3-student-mgmt/` directory (if RPGLE samples exist)
- [ ] Verify JSON output matches IR standard using `IrJsonSerializer`
- [ ] Cross-check dependency extraction against known program call chains
