---
phase: testing
title: Testing Strategy — CL Parser
description: Test coverage, strategies, and known gaps for the CL/CLP/CLLE parser
---

# Testing Strategy

## Test Classes

| Class | File | Tests | Scope |
|-------|------|-------|-------|
| `ClIrBuilderTest` | `src/test/java/com/as400parser/cl/ClIrBuilderTest.java` | ~65 | Unit: ClIrBuilder in isolation |
| `ClParserIntegrationTest` | `src/test/java/com/as400parser/cl/ClParserIntegrationTest.java` | ~55 | Integration: end-to-end facade pipeline |

## ClIrBuilderTest Coverage

Tests feed raw CL source lines directly to `ClIrBuilder.buildContent()` and verify the `ClContent` IR structure without any file I/O.

| Nested Class | Coverage |
|---|---|
| `CommentParsing` | Single/multi-line block comments, comment specType tagging, empty source |
| `LineContinuation` | `+` continuation, `-` continuation, 3+ continuation lines, specType on continuation lines |
| `ParameterParsing` | Keyword params, positional params, quoted strings, qualified names, multiple params |
| `PgmBoundary` | PGM/ENDPGM boundary, programName extraction |
| `DclVariables` | `*CHAR`, `*DEC` with decimals, `VALUE(...)`, multiple variables |
| `DclfDeclarations` | FILE extraction, OPNID, multiple DCLF |
| `Labels` | Single/multiple labels, specType, label + command on same line |
| `MonMsgParsing` | Simple MONMSG, MONMSG with EXEC(GOTO), execCommands list |
| `SubroutineParsing` | Single/multiple SUBR, commands inside/outside SUBR, ENDSUBR |
| `ControlFlow` | GOTO, CHGVAR, IF, DOWHILE/ENDDO, SELECT |
| `SourceLineTagging` | COMMAND specType, all lines have line numbers |
| `CommonCommands` | OVRDBF, ADDLIBLE, RMVLIBLE, DLTOVR, SNDPGMMSG, CALL with qualified name |
| `QuotedStringEdgeCases` | `''` (doubled quote) escape, `+` inside quoted string, multiple doubled quotes |

## ClParserIntegrationTest Coverage

Tests use `ClParserFacade.parse()` against in-memory strings and on-disk fixture files.

| Nested Class | Coverage |
|---|---|
| `InterfaceContract` | getSourceType, getSupportedExtensions, no .clmod |
| `ParseFromString` | Full pipeline, parseStatus, sourceType default (CLLE), no errors on valid input |
| `ParseFromFile` | .cl→CL, .clp→CLP, .clle→CLLE, memberName, all fixtures parse successfully |
| `MetadataCorrectness` | parsedAt, parserVersion, totalLines, sourceMember/sourceFile from path |
| `DependencyExtraction` | CALL, multiple CALLs, DCLF, no-dup DCLF, OVRDBF (TOFILE priority), SNDPGMMSG MSGF, ADDLIBLE, RMVLIBLE, CALL-in-subroutine, MONMSG EXEC(GOTO), copyMembers empty, DLTOVR FILE(*ALL) filtered, unterminated comment warning |
| `DeclarationsFixture` | variables count, file declarations count, &MODE type/length, &MSG initialValue |
| `ContinuationFixture` | commands joined correctly, SNDPGMMSG all params |
| `SubroutinesFixture` | two subroutines, PROCESS subr has commands, calledPrograms from subroutine |
| `CliRegistration` | selectParser for .cl, .clp, .clle, not for .clmod |
| `MnuclIntegration` | Real MNUCL.clle: parse without error, metadata, comments, labels ERROR/ENDPGM, calledPrograms includes MNUPRG, referencedFiles includes STUDNTPF, ADDLIBLE STULIB, RMVLIBLE STULIB, SNDPGMMSG QCPFMSG, command count, source lines with numbers, full document structure |

## Test Fixtures

Located in `parser-core/src/test/resources/cl/`:

| File | Purpose |
|------|---------|
| `simple.cl` | Minimal PGM + CALL + ENDPGM |
| `declarations.clle` | DCL variables + DCLF file declarations |
| `monmsg.clp` | MONMSG with EXEC |
| `continuation.clle` | Multi-line continuation (`+`) |
| `subroutines.clle` | SUBR/ENDSUBR blocks |
| `controlflow.clle` | IF, SELECT, DOWHILE, GOTO |

Real-world fixture: `rpg3-student-mgmt/QCLSRC/MNUCL.clle`

## Coverage Notes

- `ClIrBuilder.getParamValue` and `ClParserFacade.getParamValue` are duplicates; both are tested indirectly via builder/facade tests.
- The `extractLogicalLines` doubled-quote (`''`) path is covered by `QuotedStringEdgeCases`.
- Dependency extraction for rarely-used commands (`RTVDTAARA`, `RSTOBJ`, `SAVOBJ`, etc.) is not tested directly — they share the same `addRef` helper and are covered by the switch-case structure.
- `parseInfo.warnings` propagation is verified by the `unterminatedComment_warningInParseInfo` integration test.

## Known Gaps

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| No test for `-` (no-skip) continuation type | Low | Add `ClIrBuilderTest` case with `-` marker |
| No test for nested MONMSG EXEC(CALL PGM) → calledPrograms | Medium | Add integration test |
| Rare OVR commands (`OVRICFF`, `OVRTAPF`, `OVRSAVF`) untested | Low | Add at least one representative test |
| `SBMJOB CMD(CALL PGM(...))` not recursively extracted | Medium | Design decision: CMD is a string, not parsed |
