---
phase: planning
title: Project Planning & Task Breakdown
description: Break down work into actionable tasks and estimate timeline
---

# Project Planning & Task Breakdown

## Milestones
**What are the major checkpoints?**
- [x] Milestone 1: Python CLI securely routes `.rpgle`/`.sqlrpgle` payloads to the Java `RpgleParser` interface.
- [x] Milestone 2: Fixed-Format (`RpgleFixedParser`) logic completely established via `substring()` offsets for all 7 spec types.
- [x] Milestone 3: Fully Free and Mixed Free-Format logic correctly invokes existing ANTLR grammar (`RpgParser.g4`).
- [ ] Milestone 4: IR Builder constructs output JSON matching null/empty protocols with 100% test coverage.

## Task Breakdown
**What specific work needs to be done?**

### Phase 1: Foundation & CLI Integration
- [x] Task 1.1: Update Python CLI `as400_parser_cli.py`:
  - Add `.rpgle` and `.sqlrpgle` to `SUPPORTED_EXTENSIONS`.
  - Add RPGLE count in `cmd_batch` summary output.
  - Update CLI description/help text to mention RPGLE.
- [x] Task 1.2: Establish module architecture `com.as400parser.rpgle` inside `parser-core`:
  - Create package directory structure under `parser-core/src/main/java/com/as400parser/rpgle/`.
  - Create `model/` sub-package for RPGLE-specific content models.
  - Add ANTLR4 dependency to `build.gradle` and configure grammar source set for `grammar/rpgle/`.
  - Wire existing `RpgLexer.g4` and `RpgParser.g4` into the Gradle ANTLR generation task.
- [x] Task 1.3: Create `RpgleParserFacade.java` implementing `As400Parser` (from `com.as400parser.common.parser`):
  - Implement `parse(Path sourceFile, ParseOptions options)` — use `SourceNormalizer` for encoding detection/normalization (same pattern as `ClParserFacade`).
  - Implement `parse(String sourceText, ParseOptions options)` — normalize via `SourceNormalizer.normalize(String)`.
  - Implement `getSourceType()` → return `"RPGLE"`.
  - Implement `getSupportedExtensions()` → return `[".rpgle", ".sqlrpgle"]`.
  - Implement format detection: check first non-blank line for `**FREE` header.
  - Route to `RpgleFreeParser` for fully free, or `RpgleFixedParser` for fixed/mixed.
- [x] Task 1.4: Register `RpgleParserFacade` in `As400ParserCli.java`:
  - Add `new RpgleParserFacade()` to the `PARSERS` list.
  - Update `printUsage()` help text to include `RPGLE: .rpgle, .sqlrpgle`.

### Phase 2: Core Parsing Engine
- [x] Task 2.1: Implement `RpgleFixedParser.java` — positional parsing (NO ANTLR):
  - Detect form type at column 6 (`H`, `F`, `D`, `I`, `C`, `O`, `P`).
  - Detect comments at column 7 (`*`).
  - Parse **H-Specs (Control):** columns 7-80 (keywords).
  - Parse **F-Specs (File Description):** columns 7-16 (name), 17 (type), 18 (designation), 19 (EOF), 20 (addition), 21 (sequence), 22 (format), 23-27 (record length), 28 (limits), 29-33 (key length), 34 (address type), 35 (organization), 36-42 (device), 44-80 (keywords).
  - Parse **D-Specs (Definition):** columns 7-21 (name), 22 (external desc), 23 (DS type), 24-25 (definition type), 26-32 (from), 33-39 (to/length), 40 (data type), 41-42 (decimals), 44-80 (keywords).
  - Parse **I-Specs (Input):** columns 7-16 (file name), 17-18 (sequence), 19 (number), 20 (option), 21-22 (record ID indicator), 23-46 (record ID codes); field entries columns 31-34 (attributes), 37-46 (field location), 47-48 (decimals), 49-62 (field name), 63-64 (control level), 69-74 (indicators).
  - Parse **C-Specs (Calculation):** columns 7-8 (control level), 9-11 (indicators), 12-25 (factor 1), 26-35 (operation + extender), 36-49 (factor 2) or 36-80 (extended factor 2), 50-63 (result), 64-68 (field length), 69-70 (decimals), 71-76 (resulting indicators).
  - Parse **O-Specs (Output):** columns 7-16 (file name), 17 (type), 18-20 (record add/del), 21-29 (conditioning indicators), 30-39 (EXCEPT name), 40-51 (space/skip); field entries columns 30-43 (field name), 44 (edit code), 45 (blank after), 47-51 (end position), 53-80 (constant/edit word).
  - Parse **P-Specs (Procedure):** columns 7-21 (name), 24 (begin/end), 44-80 (keywords).
  - Handle index-out-of-bounds safely by defaulting to `null`.
  - Use `SourceLine` and `Location` from `com.as400parser.common.model` for line tracking.
- [x] Task 2.2: Implement `RpgleFreeParser.java` using existing ANTLR grammar:
  - Wire `grammar/rpgle/RpgLexer.g4` and `RpgParser.g4` into the Lexer/Parser pipeline.
  - Handle ANTLR `RecognitionException` gracefully — emit `ParseError` objects (from `com.as400parser.common.model`) instead of crashing.
  - Support operations (e.g. `CHAIN`, `READ`, `EVAL`) and Built-In Functions (e.g. `%LEN`, `%SUBST`).
- [x] Task 2.3: Implement Mixed-format logic in `RpgleFixedParser`:
  - Track `/FREE` and `/END-FREE` block boundaries line-by-line.
  - Collect lines inside `/FREE` blocks and batch-delegate them to `RpgleFreeParser`.
  - Merge ANTLR results back into the fixed-format result sequence.

### Phase 3: IR Builder & Assembly
- [x] Task 3.1: Create RPGLE content model in `com.as400parser.rpgle.model`:
  - `RpgleContent.java` — top-level content object (set on `IrDocument.setContent()`).
  - Spec-specific model classes for each spec type's parsed data.
- [ ] Task 3.2: Build `RpgleIrBuilder` to produce `IrDocument` (from `com.as400parser.common.model`):
  - Populate `IrDocument.setContent()` with `RpgleContent`.
  - Populate `IrDocument.setDependencies()` using `IrDocument.Dependencies`, `DependencyRef`, `CopyMemberRef`.
  - Populate `IrDocument.setMetadata()` using `Metadata` and `Metadata.ParseInfo`.
  - Populate `IrDocument.setErrors()` using `ParseError`.
  - Map H-specs → `programMetadata`.
  - Map F-specs → `files` (extract `DependencyRef` entries for referenced files).
  - Map D-specs → `variables` (standalone fields, data structures, constants, prototypes).
  - Map I-specs → `recordIdentifications`.
  - Map C-specs → `calculations`.
  - Map O-specs → `outputs`.
  - Map P-specs → `procedures`.
  - Note: IR document assembly is implemented directly in `RpgleParserFacade` rather than a dedicated `RpgleIrBuilder` class.
- [x] Task 3.3: Extend `IrJsonSerializer` for RPGLE content:
  - Register custom Gson type adapters for RPGLE model classes if needed.
  - Ensure `null` for missing values, `""` for intentionally blank strings.
  - Result: default Gson serialization already produces working RPGLE JSON output for parsed models.

### Phase 4: Verification & Testing
- [ ] Task 4.1: Write fixed-format boundary tests covering all 7 spec types (`.substring()` edge cases).
- [ ] Task 4.2: Write free/mixed-format unit tests leveraging the existing ANTLR grammar.
- [ ] Task 4.3: Ensure 100% statement and branch coverage inside `com.as400parser.rpgle`.

## Common Framework Components (MUST REUSE)
**The RPGLE parser must integrate with the existing `com.as400parser.common` framework:**

| Component | Class | Usage |
|-----------|-------|-------|
| Parser Interface | `common.parser.As400Parser` | `RpgleParserFacade` implements this |
| Parse Options | `common.parser.ParseOptions` | Passed to `parse()` methods |
| IR Document | `common.model.IrDocument` | Top-level return type from `parse()` |
| Dependencies | `common.model.IrDocument.Dependencies` | `referencedFiles`, `calledPrograms`, `copyMembers` |
| Dependency Ref | `common.model.IrDocument.DependencyRef` | Individual file/program references |
| Copy Member Ref | `common.model.IrDocument.CopyMemberRef` | `/COPY` and `/INCLUDE` references |
| Metadata | `common.model.Metadata` | Source type, member, library, parseInfo |
| Parse Info | `common.model.Metadata.ParseInfo` | Status, line count, encoding, errors |
| Parse Error | `common.model.ParseError` | Error/warning entries |
| Location | `common.model.Location` | Line/column tracking |
| Source Line | `common.model.SourceLine` | Raw source line wrapper |
| Source Normalizer | `common.normalizer.SourceNormalizer` | Encoding detection + normalization |
| Normalized Source | `common.normalizer.NormalizedSource` | Normalized lines + sequence numbers |
| JSON Serializer | `common.serializer.IrJsonSerializer` | Serialize `IrDocument` to JSON |
| CLI Entry Point | `common.cli.As400ParserCli` | Register parser in `PARSERS` list |

## Dependencies
**What needs to happen in what order?**
- Task 1.2 (ANTLR gradle setup) must complete before Task 2.2 (free-format parser).
- Task 1.3 (facade) follows the `ClParserFacade` pattern: `SourceNormalizer` → build content → extract deps → populate metadata → return `IrDocument`.
- Task 2.1 and 2.2 must complete before Task 3.2 (IR builder needs parsed data models).
- Task 2.3 (mixed-format) depends on both Task 2.1 (fixed) and Task 2.2 (free) being functional.
- Task 3.3 (serializer) must be done before Task 4 (tests need JSON output verification).
- **External Dependencies:** Pre-existing ANTLR4 grammars in `grammar/rpgle/`. No new grammars to author.

## Timeline & Estimates
**When will things be done?**
- Phase 1 Foundation: Quick setup — CLI routing + package scaffolding + facade following CL pattern.
- Phase 2 Core Engine: Highest complexity — Task 2.1 is the largest single task (7 positional spec parsers).
- Phase 3 IR Builder: Moderate effort — property mapping, JSON assembly, serializer extension.
- Phase 4 Testing: Moderate effort — extensive regression tests across all spec types.

## Risks & Mitigation
**What could go wrong?**
- **Risk:** Performance degradation on large fixed-format sources.
  - **Mitigation:** Use `String.substring()` exclusively, no RegEx in positional parsing.
- **Risk:** Existing `RpgParser.g4` might fail to lex isolated chunks cut from `/FREE` blocks.
  - **Mitigation:** Inject hidden wrapper tokens in the parser facade before sending mixed blocks to ANTLR.
- **Risk:** Positional index-out-of-bounds on short lines.
  - **Mitigation:** All `.substring()` calls must bounds-check and default to `null` on failure.
- **Risk:** ANTLR `RecognitionException` on malformed free-format code.
  - **Mitigation:** Catch exceptions and emit `ParseError` objects instead of crashing the entire parser.
- **Risk:** `IrJsonSerializer` currently has RPG3-specific type adapters hardcoded.
  - **Mitigation:** Either extend the serializer with RPGLE adapters or use a separate serializer instance.
