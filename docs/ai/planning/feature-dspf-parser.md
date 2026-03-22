---
phase: planning
title: "Project Planning & Task Breakdown"
description: Phased task list for DSPF parser, aligned with implementation guide
---

# Project Planning & Task Breakdown — DSPF Parser

## Milestones

- [ ] Milestone 1: Model classes complete (6 POJOs)
- [ ] Milestone 2: IR builder + parser facade functional
- [ ] Milestone 3: CLI integration + all 3 sample files parse correctly

## Task Breakdown

### Phase 1: Model Classes (`dspf/model/`)

> Ref: [Implementation §Phase 1](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-dspf-parser.md)

- [ ] Task 1.1: `ConditioningIndicator.java` — `{not, indicator}` from cols 8–16
- [ ] Task 1.2: `ConditionedKeyword.java` — `{keyword (DdsKeyword), conditioningIndicators}` wrapper
- [ ] Task 1.3: `DspfConstant.java` — `{location, rawSourceLine, conditioningIndicators, screenLine, screenPosition, text, systemKeyword, keywords}` — text/systemKeyword mutually exclusive
- [ ] Task 1.4: `DspfFieldDefinition.java` — `{location, rawSourceLines, conditioningIndicators, name, reference, length, dataType, decimalPositions, usage, screenLine, screenPosition, source, keywords}`
- [ ] Task 1.5: `DspfRecordFormat.java` — `{location, rawSourceLine, conditioningIndicators, name, recordType, sflControlFor, text, keywords, fields, constants, keys}`
- [ ] Task 1.6: `DspfContent.java` — `{sourceLines, fileKeywords, recordFormats, comments, parseErrors}`

### Phase 2: Parser Logic

> Ref: [Implementation §Phase 2](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-dspf-parser.md)

- [ ] Task 2.1: `DspfIrBuilder.java` — 12-step processing:
  - [ ] 2.1a: Line classification (comment, record `R`, key `K`, field, constant, system keyword, conditioned keyword, continuation)
  - [ ] 2.1b: Conditioning indicator parsing — 3 slots of 3 chars from cols 8–16, handle `N` prefix
  - [ ] 2.1c: Screen coordinate extraction — parseInt cols 39–41 (line), 42–44 (position)
  - [ ] 2.1d: Continuation line merging (col 80 = `+`)
  - [ ] 2.1e: Conditioned keyword-only line merging — lines with indicators + keyword but no name/position merge into preceding field/constant
  - [ ] 2.1f: System keyword constant detection — `DATE`/`TIME`/`SYSNAME`/`USER`/`PAGNBR` unquoted in cols 45–80
  - [ ] 2.1g: Record type detection — `sfl` (SFL keyword), `sflctl` (SFLCTL keyword), `normal` (else)
  - [ ] 2.1h: File keyword vs continuation disambiguation (seenRecord flag)
  - [ ] 2.1i: Build `sourceLines`, `comments`, `parseErrors` arrays

### Phase 3: Facade + CLI

> Ref: [Implementation §Phase 3](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-dspf-parser.md)

- [ ] Task 3.1: `DspfParserFacade.java` — implements `As400Parser`
  - Pipeline: normalize → buildContent → populateMetadata → IrDocument
  - Metadata: irVersion, sourceType=`"DSPF"`, sourceMember, sourceFile, sourceLibrary, parseInfo
- [ ] Task 3.2: Update `As400ParserCli.java` — add `new DspfParserFacade()` to `PARSERS` list

### Phase 4: Testing

> Ref: [Implementation §Phase 4](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-dspf-parser.md)

- [ ] Task 4.1: `DspfIrBuilderTest.java` — unit tests:
  - File keywords, record types (normal/sfl/sflctl), sflControlFor
  - Field columns (name, length, type, usage, screenLine, screenPos, reference, source)
  - Quoted text constants, system keyword constants (DATE/TIME/SYSNAME/USER)
  - Conditioning indicators (basic + negated N60)
  - Conditioned keyword merging, continuation merging
  - CJK text preservation, sourceLines structure
- [ ] Task 4.2: `DspfIntegrationTest.java` — end-to-end parsing:
  - `MNUDSPF.dspf` — menu (file keywords, constants, CA03/CA12)
  - `STUDSPF.dspf` — detail (DATE/TIME, conditioned DSPATR, ERRMSG)
  - `STULSTD.dspf` — subfile (SFL, SFLCTL, SFLSIZ/SFLPAG, conditioned SFLDSP/SFLCLR/SFLEND)
- [ ] Task 4.3: Verify JSON structure consistency with PF/LF (metadata, dependencies, errors)

## Dependencies

| Component | Status | Notes |
|---|---|---|
| `DdsKeywordParser` | ✅ Reuse | Same keyword syntax, no changes |
| `SourceNormalizer` | ✅ Reuse | 80-char padding |
| `IrJsonSerializer` | ✅ Reuse | Serializes any IrDocument |
| `As400Parser` interface | ✅ Implement | No changes to interface |
| `DdsKeyword`, `KeyDefinition`, `DdsComment` | ✅ Reuse | Shared models from `dds.model` |
| `SourceLine`, `Location`, `ParseError` | ✅ Reuse | Shared models from `common.model` |

## Timeline & Estimates

| Phase | Effort | Files | Description |
|---|---|---|---|
| Phase 1: Models | Small | 6 new | Simple POJOs, ~30 min |
| Phase 2: IR Builder | Medium | 1 new | 12-step processing, ~1–2 hours |
| Phase 3: Facade + CLI | Small | 1 new + 1 modify | Pipeline + CLI registration, ~30 min |
| Phase 4: Testing | Medium | 2 new | Unit + integration, ~1 hour |
| **Total** | | **10 new + 1 modify** | **~3–4 hours** |

## Risks & Mitigation

| Risk | Impact | Mitigation |
|---|---|---|
| Conditioned keyword-only line merging | Medium | Study sample files; clear distinction from continuation (indicators present vs col 80 `+`) |
| System keyword constant detection vs regular keywords | Low | Check for unquoted DATE/TIME/SYSNAME/USER when no name + screen position present |
| Gson serialization of new model classes | Low | Follow same pattern as DDS models; IrJsonSerializer handles any content |
| Screen coordinate edge cases (optional, 0 = cursor-relative) | Low | Use nullable Integer; sample files cover main patterns |
