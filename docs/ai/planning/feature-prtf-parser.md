---
phase: planning
title: "Planning: PRTF Parser"
description: Task breakdown and implementation plan for Printer File DDS parser
---

# Project Planning & Task Breakdown — PRTF Parser

## Milestones

- [ ] Milestone 1: Model classes created and compiling
- [ ] Milestone 2: IR Builder produces correct PRTF content from sample files
- [ ] Milestone 3: Facade + CLI integration complete
- [ ] Milestone 4: All tests passing (unit + integration)

## Task Breakdown

### Phase 1: Model Classes
> Priority: High | Estimate: ~1 hour

- [ ] Task 1.1: Create `PrtfContent.java` — top-level content container
  - Fields: `sourceLines`, `fileKeywords`, `recordFormats`, `comments`, `parseErrors`
- [ ] Task 1.2: Create `PrtfRecordFormat.java`
  - Fields: `location`, `rawSourceLine`, `conditioningIndicators`, `name`, `text`, `keywords`, `fields`, `constants`, `keys`
  - No subfile types (simpler than DSPF — always "normal" record type)
- [ ] Task 1.3: Create `PrtfFieldDefinition.java`
  - Fields: `location`, `rawSourceLines`, `conditioningIndicators`, `name`, `reference`, `referenceField`, `referenceFile`, `referenceRecordFormat`, `length`, `dataType`, `decimalPositions`, `usage`, `printLine`, `printPosition`, `source`, `keywords`
- [ ] Task 1.4: Create `PrtfConstant.java`
  - Fields: `location`, `rawSourceLines`, `conditioningIndicators`, `printLine`, `printPosition`, `text`, `systemKeyword`, `keywords`
- [ ] Task 1.5: Verify compilation — all models compile, reuse `ConditioningIndicator`/`ConditionedKeyword` from DSPF

### Phase 2: IR Builder
> Priority: High | Estimate: ~3 hours

- [ ] Task 2.1: Create `PrtfIrBuilder.java` — single-pass state machine
  - Classify lines (comment, file-keyword, record format, field, constant, continuation)
  - Extract columns using same `extractColumn` utility approach as DSPF
  - Build `PrtfContent` from classified lines
- [ ] Task 2.2: Implement record format parsing
  - Detect `R` in col 17, extract name from cols 19-28
  - Parse record-level keywords (TEXT, SPACEA, SPACEB, SKIPA, SKIPB, etc.)
- [ ] Task 2.3: Implement field definition building
  - Extract: name (19-28), reference (29), length (30-34), dataType (35), decimalPositions (36-37), usage (38), printLine (39-41), printPosition (42-44)
  - Parse field keywords (EDTCDE, EDTWRD, REFFLD, etc.)
  - Handle REFFLD population (same pattern as DSPF)
- [ ] Task 2.4: Implement constant building
  - Detect unnamed lines with print coordinates
  - Parse quoted text or system keywords (DATE, TIME, PAGNBR)
  - Handle `parseConstantKeywordArea` to prevent spurious keywords from quoted text
- [ ] Task 2.5: Implement conditioning indicator parsing
  - Reuse same column 8-16 parsing logic as DSPF
  - Apply to fields, constants, and keyword-only lines
- [ ] Task 2.6: Implement continuation line handling
  - Detect `+` at column 80
  - Merge keyword areas across continuation lines
  - Track raw source lines for multi-line fields/constants
- [ ] Task 2.7: Implement conditioned keyword merging
  - Keyword-only lines (no name, no position) with indicators merge into preceding field/constant
- [ ] Task 2.8: Handle text continuation lookahead for multi-line constants

### Phase 3: Facade & CLI Integration
> Priority: High | Estimate: ~1 hour

- [ ] Task 3.1: Create `PrtfParserFacade.java` — implements `As400Parser`
  - Normalize → build → create IrDocument → populate metadata
  - Pass `sequenceNumbers` to builder (same pattern as DSPF)
  - Extract referenced files from file-level keywords
- [ ] Task 3.2: Register in `As400ParserCli.java`
  - Add `.prtf` extension mapping
  - Instantiate `PrtfParserFacade`
- [ ] Task 3.3: Extend `DdsRefResolver` for PRTF
  - Add `PrtfContent` case in `resolve()` method
  - Add `resolvePrtfFields()` helper

### Phase 4: Testing
> Priority: High | Estimate: ~2 hours

- [ ] Task 4.1: Create sample `.prtf` test file(s)
  - Include: record formats, fields with print coordinates, constants, system keywords, conditioning indicators, continuations, SPACEA/SPACEB, SKIPA/SKIPB, EDTCDE, REF/REFFLD
- [ ] Task 4.2: Create `PrtfIrBuilderTest.java` — unit tests
  - Line classification, record formats, field definitions, constants, conditioning indicators, continuations, key defs, edge cases
- [ ] Task 4.3: Create `PrtfIntegrationTest.java` — end-to-end tests
  - Parse sample files, verify full IR structure
- [ ] Task 4.4: Verify all existing tests still pass (regression)

## Dependencies

- Phase 1 has no dependencies
- Phase 2 depends on Phase 1 (model classes)
- Phase 3 depends on Phase 2 (builder must exist)
- Phase 4 depends on Phase 2 + Phase 3 (builder + facade must exist)

## Timeline & Estimates

| Phase | Estimate | Cumulative |
|---|---|---|
| Phase 1: Models | ~1 hour | 1 hour |
| Phase 2: IR Builder | ~3 hours | 4 hours |
| Phase 3: Facade + CLI | ~1 hour | 5 hours |
| Phase 4: Testing | ~2 hours | 7 hours |

## Risks & Mitigation

| Risk | Mitigation |
|---|---|
| No sample `.prtf` files available | Create synthetic test files based on IBM documentation examples |
| PRTF has keywords unknown to us | `DdsKeywordParser` captures all keywords generically — no risk |
| AFP-specific constructs complex | Capture generically — no special handling needed |

## Resources Needed

- IBM i DDS for Printer Files reference (provided by user)
- Existing DSPF parser implementation for reference patterns
- Sample PRTF source files (to be created)
