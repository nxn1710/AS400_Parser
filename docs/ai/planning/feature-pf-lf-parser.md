---
phase: planning
title: Project Planning & Task Breakdown
description: Break down work into actionable tasks and estimate timeline for PF/LF DDS parser
---

# Project Planning & Task Breakdown — PF/LF DDS Parser

## Document References

| Document | Path |
|---|---|
| Requirements | [feature-pf-lf-parser.md](file:///d:/Code/AS400_Parser/docs/ai/requirements/feature-pf-lf-parser.md) |
| Design | [feature-pf-lf-parser.md](file:///d:/Code/AS400_Parser/docs/ai/design/feature-pf-lf-parser.md) |
| Implementation | [feature-pf-lf-parser.md](file:///d:/Code/AS400_Parser/docs/ai/implementation/feature-pf-lf-parser.md) |
| Knowledge | [pflf-knowledge.md](file:///d:/Code/AS400_Parser/docs/ai/knowledge/pflf-knowledge.md) |

---

## Milestones

- [x] Milestone 1: Design approval — requirements + design docs approved
- [ ] Milestone 2: Model classes — all 11 DDS model classes created
- [ ] Milestone 3: Core parser — keyword parser (50 keywords) + line classifier + IR builder
- [ ] Milestone 4: Integration — facade + CLI update (Java + Python)
- [ ] Milestone 5: Verification — all 9 sample files parse correctly

---

## Task Breakdown

### Phase 1: Model Classes (`dds/model/`) ✅

**Create package:** `parser-core/src/main/java/com/as400parser/dds/model/`

- [x] Task 1.1: `DdsKeyword.java` — unified keyword model (name/value/values/rawText/comparisonOperator/comparisonValue/rangeFrom/rangeTo)
- [x] Task 1.2: `FieldDefinition.java` — all column fields:
  - `conditioningIndicators` (cols 8-16), `name` (19-28), `reference` (29), `length` (30-34), `dataType` (35), `decimalPositions` (36-37), `usage` (38), `fieldLocation` (39-44)
  - Computed: `source` (direct/reference/derived from keyword analysis)
  - `keywords[]`, `rawSourceLines[]`, `location`
- [x] Task 1.3: `KeyDefinition.java` — fieldName, sortOrder (ascending/descending), conditioningIndicators, keywords
- [x] Task 1.4: `SelectOmitSpec.java` — type (select/omit), fieldName, rule, conditioningIndicators, keywords
- [x] Task 1.5: `JoinSpec.java` + `JoinFieldPair.java` — fromFile, toFile, joinFields, conditioningIndicators, keywords
- [x] Task 1.6: `RecordFormat.java` (PF) — name, text, conditioningIndicators, keywords, fields[], keys[]
- [x] Task 1.7: `LfRecordFormat.java` (LF) — name, lfType (simple/multipleFormat/join), pfile, jfile[], conditioningIndicators, keywords, fields[], keys[], selectOmit[], joinSpecs[]
- [x] Task 1.8: `DdsPfContent.java` + `DdsLfContent.java` — sourceLines, fileKeywords, recordFormats, comments
- [x] Task 1.9: `DdsComment.java` — lineNumber, text

### Phase 2: Core Parser Components (`dds/`) ✅

- [x] Task 2.1: `DdsLineClassifier.java`
  - Classify lines: COMMENT / BLANK / FILE_KEYWORD / RECORD_FORMAT / FIELD_DEFINITION / KEY_FIELD / SELECT_FIELD / OMIT_FIELD / JOIN_SPEC / CONTINUATION
  - Use col 6 (form type), col 7 (comment), col 17 (entry type), cols 19-28 (name)
  - State: `seenRecord` flag to distinguish FILE_KEYWORD vs CONTINUATION
- [x] Task 2.2: `DdsKeywordParser.java`
  - Parse keyword area (cols 45-80) into `DdsKeyword` objects
  - 16 parsing patterns for **50 keywords**: no-arg, single-quoted, single-unquoted, qualified-name, multi-quoted, multi-unquoted, COMP/CMP, RANGE, SST, REFFLD, CCSID, JDUPSEQ, JOIN/JFLD, hex-literal, VARLEN
  - Continuation line merging ('+' at col 80)
  - CMP → COMP normalization
  - Handle: Japanese text, hex `X'F1F2'`, special values `*NULL`/`*DESCEND`, qualified `LIB/FILE`
- [x] Task 2.3: `DdsIrBuilder.java`
  - Single-pass: classify → extract columns → parse keywords → build content model
  - State machine: currentRecordFormat, seenRecord
  - LF subtype detection: JFILE→join, multiple R→multipleFormat, else→simple
  - Compute `FieldDefinition.source` (direct/reference/derived)
  - Error recovery: create ParseError, continue processing
  - Populate `dependencies.referencedFiles` from PFILE/JFILE/REF

### Phase 3: Facade + CLI Integration ✅

- [x] Task 3.1: `DdsParserFacade.java` — implements `As400Parser`
  - Pipeline: SourceNormalizer (reuse, LINE_WIDTH=80) → classify → parse → build → serialize
  - Both `parse(Path, ParseOptions)` and `parse(String, ParseOptions)` overloads
  - Metadata: sourceType (DDS_PF/DDS_LF), sourceMember, sourceFile, sourceLibrary, parseInfo
  - Auto-detect: `.pf` → DDS_PF, `.lf` → DDS_LF
- [x] Task 3.2: CLI entry point integration
  - Extended `Rpg3ParserFacade.main()` with `selectParser()` auto-detect by extension
  - Added `.pf`/`.lf` to `validExts` in batch processing
- [x] Task 3.3: Update `cli/rpg3_parser_cli.py`
  - Added `.pf`, `.lf` to extensions set

### Phase 4: Testing & Verification

- [ ] Task 4.1: `DdsKeywordParserTest.java` — test all 16 keyword patterns + CMP normalization
- [ ] Task 4.2: `DdsLineClassifierTest.java` — test all 10 line types (R/K/S/O/J/blank + comment/blank/file-kw/continuation)
- [ ] Task 4.3: `DdsIntegrationTest.java` — parse all 9 sample files:
  - PF: STUDNTPF, CLASSPF, SCHOOLPF, STUCLSPF
  - LF: STUDNTL1, STUDNTL2 (select/omit), CLASSL1, SCHOOLL1, STUCLSL1
- [ ] Task 4.4: `DdsIrBuilderTest.java` — unit tests for IR building logic
- [ ] Task 4.5: Create example IR output files: `dds_pf.json`, `dds_lf.json`, `dds_lf_join.json`

---

## Dependencies

| Dependency | Status | Notes |
|---|---|---|
| `SourceNormalizer` | ✅ Ready | Reuse as-is, LINE_WIDTH=80 hardcoded — no changes needed |
| `IrDocument` / `Metadata` | ✅ Ready | Shared IR envelope |
| `IrJsonSerializer` | ✅ Ready | Reuse for JSON output |
| `As400Parser` interface | ✅ Ready | 4 methods: parse×2, getSourceType, getSupportedExtensions |
| `ParseOptions` | ✅ Ready | Charset configuration |
| No external libraries | ✅ | Uses existing Gson 2.11, JUnit 5.11, AssertJ 3.26 |

---

## Timeline & Estimates

| Phase | Tasks | Estimated Effort | Complexity |
|---|---|---|---|
| Phase 1: Model Classes | 9 tasks, 11 classes | ~2 hours | Low — POJOs with getters/setters |
| Phase 2: Core Parser | 3 tasks, 3 classes | ~5 hours | High — keyword parser (50 keywords, 16 patterns) |
| Phase 3: Integration | 3 tasks | ~2 hours | Medium — follow Rpg3ParserFacade pattern |
| Phase 4: Testing | 5 tasks | ~3 hours | Medium — 9 sample files + unit tests |
| **Total** | **20 tasks** | **~12 hours** | |

---

## Risks & Mitigation

| Risk | Impact | Mitigation |
|---|---|---|
| Keyword syntax edge cases (nested parens, continuation mid-keyword) | Medium | Start with 9 sample files; add patterns incrementally |
| Continuation line merging across multi-line keywords | Medium | Test with real STUDNTPF.pf (has TEXT with Japanese) |
| CMP vs COMP inconsistency in source files | Low | Normalize CMP → COMP during parsing |
| Hex literal `X'F1F2'` parsing | Low | Detect `X'` prefix, read until closing `'` |
| CLI integration affects existing RPG3 batch | Low | Extension-based detection — RPG3 extensions unchanged |
| DBCS/Japanese text in keyword values | Medium | Use String methods, not char counting; test with real samples |

---

## Resources

- **Sample DDS files:** `rpg3-student-mgmt/QDDSSRC/*.pf` and `*.lf` (9 files)
- **RPG3 parser reference:** `Rpg3ParserFacade.java` (454 lines) + `Rpg3IrBuilder.java` (65KB)
- **DDS A-spec reference:** IBM Knowledge Center + `pflf-knowledge.md`
- **Build:** `gradlew shadowJar` → `as400-parser-core-1.0.0-SNAPSHOT-all.jar`
- **Test:** `gradlew test --tests "com.as400parser.dds.*"`
