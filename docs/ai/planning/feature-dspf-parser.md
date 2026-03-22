---
phase: planning
title: "Project Planning & Task Breakdown"
description: Phased task list for DSPF parser, aligned with implementation guide
---

# Project Planning & Task Breakdown — DSPF Parser

## Milestones

- [x] Milestone 1: Model classes complete (6 POJOs)
- [x] Milestone 2: IR builder + parser facade functional
- [x] Milestone 3: CLI integration + all 3 sample files parse correctly

## Task Breakdown

### Phase 1: Model Classes (`dspf/model/`)

> Ref: [Implementation §Phase 1](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-dspf-parser.md)

- [x] Task 1.1: `ConditioningIndicator.java` — `{not, indicator}` from cols 8–16
- [x] Task 1.2: `ConditionedKeyword.java` — `{keyword (DdsKeyword), conditioningIndicators}` wrapper
- [x] Task 1.3: `DspfConstant.java` — `{location, rawSourceLines, conditioningIndicators, screenLine, screenPosition, text, systemKeyword, keywords}` — text/systemKeyword mutually exclusive
- [x] Task 1.4: `DspfFieldDefinition.java` — `{location, rawSourceLines, conditioningIndicators, name, reference, length, dataType, decimalPositions, usage, screenLine, screenPosition, source, referenceField, referenceFile, referenceRecordFormat, keywords}`
- [x] Task 1.5: `DspfRecordFormat.java` — `{location, rawSourceLine, conditioningIndicators, name, recordType, sflControlFor, text, keywords, fields, constants, keys}`
- [x] Task 1.6: `DspfContent.java` — `{sourceLines, fileKeywords, recordFormats, comments, parseErrors}`

### Phase 2: Parser Logic

> Ref: [Implementation §Phase 2](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-dspf-parser.md)

- [x] Task 2.1: `DspfIrBuilder.java` — 12-step processing:
  - [x] 2.1a: Line classification (comment, record `R`, key `K`, field, constant, system keyword, conditioned keyword, continuation)
  - [x] 2.1b: Conditioning indicator parsing — 3 slots of 3 chars from cols 8–16, handle `N` prefix
  - [x] 2.1c: Screen coordinate extraction — parseInt cols 39–41 (line), 42–44 (position)
  - [x] 2.1d: Continuation line merging (col 80 = `+`) + multi-line text constant merging (unterminated quote lookahead)
  - [x] 2.1e: Conditioned keyword-only line merging — lines with indicators + keyword but no name/position merge into preceding field/constant
  - [x] 2.1f: System keyword constant detection — `DATE`/`TIME`/`SYSNAME`/`USER`/`PAGNBR` unquoted in cols 45–80
  - [x] 2.1g: Record type detection — `sfl` (SFL keyword), `sflctl` (SFLCTL keyword), `normal` (else)
  - [x] 2.1h: File keyword vs continuation disambiguation (seenRecord flag)
  - [x] 2.1i: Build `sourceLines`, `comments`, `parseErrors` arrays
  - [x] 2.1j: Spurious keyword prevention — `parseConstantKeywordArea` only parses keywords after closing quote
  - [x] 2.1k: `populateFieldReference` — extracts referenceField/referenceFile/referenceRecordFormat from REFFLD keyword

### Phase 3: Facade + CLI

> Ref: [Implementation §Phase 3](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-dspf-parser.md)

- [x] Task 3.1: `DspfParserFacade.java` — implements `As400Parser`
  - Pipeline: normalize → buildContent → populateMetadata → IrDocument
  - Metadata: irVersion, sourceType=`"DSPF"`, sourceMember, sourceFile, sourceLibrary, parseInfo
- [x] Task 3.2: Update `As400ParserCli.java` — add `new DspfParserFacade()` to `PARSERS` list
- [x] Task 3.3: Update `DdsRefResolver.java` — add DSPF support for REFFLD resolution (dataType/length/decimalPositions)
  - Fixed resolution ordering: PF docs resolve first, then LF/DSPF

### Phase 4: Testing

> Ref: [Implementation §Phase 4](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-dspf-parser.md)

- [x] Task 4.1: `DspfIrBuilderTest.java` — unit tests (46 tests):
  - File keywords, record types (normal/sfl/sflctl), sflControlFor
  - Field columns (name, length, type, usage, screenLine, screenPos, reference, source)
  - Quoted text constants, system keyword constants (DATE/TIME/SYSNAME/USER)
  - Conditioning indicators (basic + negated N60)
  - Conditioned keyword merging, continuation merging
  - CJK text preservation, sourceLines structure
- [x] Task 4.2: `DspfIntegrationTest.java` — end-to-end parsing:
  - `MNUDSPF.dspf` — menu (file keywords, constants, CA03/CA12)
  - `STUDSPF.dspf` — detail (DATE/TIME, conditioned DSPATR, ERRMSG)
  - `STULSTD.dspf` — subfile (SFL, SFLCTL, SFLSIZ/SFLPAG, conditioned SFLDSP/SFLCLR/SFLEND)
  - Multi-line text constant merging, spurious keyword prevention
- [x] Task 4.3: Verify JSON structure consistency with PF/LF (metadata, dependencies, errors)

## Dependencies

| Component | Status | Notes |
|---|---|---|
| `DdsKeywordParser` | ✅ Reuse | Same keyword syntax, no changes |
| `SourceNormalizer` | ✅ Reuse | 80-char padding |
| `IrJsonSerializer` | ✅ Reuse | Serializes any IrDocument |
| `As400Parser` interface | ✅ Implement | No changes to interface |
| `DdsKeyword`, `KeyDefinition`, `DdsComment` | ✅ Reuse | Shared models from `dds.model` |
| `SourceLine`, `Location`, `ParseError` | ✅ Reuse | Shared models from `common.model` |
| `DdsRefResolver` | ✅ Enhanced | Added DSPF REFFLD resolution + PF-first ordering |

## Timeline & Estimates

| Phase | Effort | Files | Description |
|---|---|---|---|
| Phase 1: Models | Small | 6 new | Simple POJOs ✅ |
| Phase 2: IR Builder | Medium | 1 new | 12-step processing ✅ |
| Phase 3: Facade + CLI | Small | 1 new + 2 modify | Pipeline + CLI + RefResolver ✅ |
| Phase 4: Testing | Medium | 1 new (need 2nd) | Integration tests ✅, unit tests pending |
| **Total** | | **9 new + 2 modify** | **Unit test file remaining** |

## Risks & Mitigation

| Risk | Impact | Mitigation |
|---|---|---|
| Conditioned keyword-only line merging | Medium | ✅ Resolved — clear distinction from continuation |
| System keyword constant detection vs regular keywords | Low | ✅ Resolved — unquoted DATE/TIME/SYSNAME/USER detection |
| Gson serialization of new model classes | Low | ✅ Resolved — follows DDS model pattern |
| Screen coordinate edge cases (optional, 0 = cursor-relative) | Low | ✅ Resolved — nullable Integer |
| Multi-line text constants (DBCS continuation) | High | ✅ Resolved — unterminated quote lookahead |
| Spurious keywords from quoted text content | Medium | ✅ Resolved — parseConstantKeywordArea |
| REFFLD resolution ordering | Medium | ✅ Resolved — PF resolves first, then LF/DSPF |
