---
phase: planning
title: Planning — CL Parser
description: Task breakdown and status for the CL/CLP/CLLE parser feature
---

# Feature: CL Parser

## Overview

Implement a parser for AS/400 CL (Control Language) source files (`.cl`, `.clp`, `.clle`) following the established `As400Parser` / `IrDocument` pipeline pattern.

## Milestones

### Milestone 1: Foundation ✅ Complete
- [x] Create `com.as400parser.cl` package structure
- [x] Define `ClContent` and all model classes (`ClCommand`, `ClVariable`, `ClFileDeclaration`, `ClLabel`, `ClSubroutine`, `ClMonitorMessage`, `ClParameter`, `ClComment`)
- [x] Implement `ClIrBuilder.buildContent()` — two-pass parser (tokenization + command parsing)
- [x] Implement `ClParserFacade` implementing `As400Parser`
- [x] Register `ClParserFacade` in `As400ParserCli.PARSERS`

### Milestone 2: Core Parsing ✅ Complete
- [x] Block comment extraction (`/* ... */`, single and multi-line)
- [x] Line continuation (`+` skip whitespace, `-` no-skip)
- [x] Quoted string protection (syntax chars inside `'...'` ignored)
- [x] DCL variable declaration parsing (name, type, length, decimals, value)
- [x] DCLF file declaration parsing (fileName, openId)
- [x] MONMSG parsing (msgId, cmpData, EXEC nested command)
- [x] SUBR / ENDSUBR subroutine scope management
- [x] Label extraction (`LABEL:` prefix)
- [x] Source line specType tagging (COMMAND, CONTINUATION, COMMENT, LABEL)

### Milestone 3: Dependency Extraction ✅ Complete
- [x] CALL PGM → calledPrograms
- [x] DCLF FILE → referencedFiles (file-declaration)
- [x] OVRxxx → referencedFiles (file-override; TOFILE priority; 6 override types merged into single case)
- [x] DLTOVR FILE (filter *ALL)
- [x] OPNDBF, OPNQRYF, CLOF, RCVF, SNDF, SNDRCVF
- [x] SNDPGMMSG, SNDINQMSG, RCVMSG, RMVMSG, RTVMSG, ADDMSGD, OVRMSGF
- [x] ADDLIBLE, RMVLIBLE, CHGLIBL, CRTLIB, DLTLIB
- [x] SBMJOB (JOBQ)
- [x] CRTDTAQ, DLTDTAQ, CRTDTAARA, CHGDTAARA, RTVDTAARA, DLTDTAARA
- [x] RTVSYSVAL, RTVNETA, RTVJOBA, RTVUSRPRF, RTVMBRD, RTVOBJD
- [x] CRTPF, CRTSRCPF, DLTF, CPYF, CPYSRCF, RNMOBJ, MOVOBJ, DLTPGM
- [x] CRTCLPGM, CRTBNDCL, CRTCLMOD
- [x] TFRCTL, SAVOBJ, RSTOBJ

### Milestone 4: Testing ✅ Complete
- [x] `ClIrBuilderTest` — unit tests (~65 tests)
- [x] `ClParserIntegrationTest` — integration tests (~55 tests)
- [x] Fixture files: `simple.cl`, `declarations.clle`, `monmsg.clp`, `continuation.clle`, `subroutines.clle`, `controlflow.clle`
- [x] Real-world test: `MNUCL.clle`
- [x] Python CLI extension routing tests (`.cl`, `.clp`, `.clle`)

### Milestone 5: Post-Review Fixes ✅ Complete
- [x] Fix: doubled-quote (`''`) inside quoted strings now correctly treated as escaped quote
- [x] Fix: `parseInfo.errors`/`parseInfo.warnings` now populated from `ClContent.getParseErrors()` (was hardcoded empty)
- [x] Fix: Merged 4 identical `OVR*` switch cases into one combined `case` (DRY)
- [x] Fix: `SUBR` name resolution now exclusively uses label prefix (removed dead `getParamValue("SUBR")` call)
- [x] Fix: Corrected misleading DCLF comment in `extractReferencedFiles`
- [x] Add: Tests for doubled-quote edge cases (`QuotedStringEdgeCases` nested class)
- [x] Add: Test for `DLTOVR FILE(*ALL)` → no dependency ref created
- [x] Add: Test for `unterminatedComment_warningInParseInfo`
- [x] Add: Design, requirements, testing, and planning documentation

## Known Remaining Gaps

- [ ] `-` (no-skip) continuation type has no direct unit test
- [ ] `MONMSG EXEC(CALL PGM(...))` nested call not verified in integration tests
- [ ] `getParamValue` duplicated between `ClIrBuilder` and `ClParserFacade` — extract to shared utility
- [ ] `SBMJOB CMD(...)` inner command string not parsed for program references (by design)
