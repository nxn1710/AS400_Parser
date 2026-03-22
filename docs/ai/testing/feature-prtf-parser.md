---
phase: testing
title: Testing Strategy
description: Define testing approach, test cases, and quality assurance
---

# Testing Strategy — PRTF Parser

## Test Coverage Goals

- Unit test coverage target: 100% of `PrtfIrBuilder` parsing logic
- Integration tests: all sample PRTF files parsed successfully
- Alignment with all success criteria in requirements doc

## Unit Tests

### PrtfIrBuilder (`PrtfIrBuilderTest.java`)

- [ ] Test: Blank/non-A lines classified as BLANK
- [ ] Test: Comment lines (col 7 = `*`) classified correctly
- [ ] Test: File-level keyword lines parsed (REF, INDARA)
- [ ] Test: Record format detected (col 17 = `R`, name from cols 19-28)
- [ ] Test: Record format TEXT keyword extracted
- [ ] Test: Named field parsed with all columns (name, length, type, decimals, printLine, printPosition)
- [ ] Test: Field with reference indicator (`R` at col 29) captured
- [ ] Test: Field with REFFLD keyword → referenceField/referenceFile populated
- [ ] Test: Constant with quoted text and print position
- [ ] Test: System keyword constant (DATE, TIME, PAGNBR) with print position
- [ ] Test: System keyword constant with EDTCDE keyword
- [ ] Test: Constant keywords — no spurious keyword from quoted text
- [ ] Test: Conditioning indicators — single positive (e.g., `60`)
- [ ] Test: Conditioning indicators — negated (e.g., `N60`)
- [ ] Test: Conditioning indicators — multiple slots
- [ ] Test: Conditioned keyword-only lines merge into preceding field
- [ ] Test: Continuation lines (col 80 = `+`) merge keywords
- [ ] Test: Continuation lines — rawSourceLines accumulation
- [ ] Test: Key definition (col 17 = `K`) with ascending/descending
- [ ] Test: Source lines — line numbers, specTypes, comments
- [ ] Test: Parse errors — none on valid input
- [ ] Test: Edge case — no print position (hidden field)
- [ ] Test: Edge case — field grouping under correct record format
- [ ] Test: Edge case — multiple record formats

## Integration Tests

### PrtfIntegrationTest (`PrtfIntegrationTest.java`)

- [ ] Test: Parse sample PRTF file end-to-end — verify sourceType `PRTF`
- [ ] Test: Verify record format count, field count, constants
- [ ] Test: Verify SPACEA/SPACEB/SKIPA/SKIPB keywords captured
- [ ] Test: Verify system keyword constants (DATE, TIME, PAGNBR)
- [ ] Test: Verify conditioning indicators on fields/keywords
- [ ] Test: Verify REFFLD resolution (if applicable)
- [ ] Test: JSON output round-trip consistency

## Test Data

- Sample files: to be created in `rpg3-student-mgmt/QDDSSRC/` (e.g., `STURPT.prtf`, `STULIST.prtf`)
- Inline test strings for unit tests (synthetic DDS lines with precise column alignment)

## Test Commands

```bash
# Run all PRTF tests
./gradlew test --tests "com.as400parser.prtf.*"

# Run specific test class
./gradlew test --tests "com.as400parser.prtf.PrtfIrBuilderTest"
./gradlew test --tests "com.as400parser.prtf.PrtfIntegrationTest"

# Run all tests (including existing DDS/DSPF/RPG3 regression)
./gradlew test
```

## Manual Testing

- Run CLI against sample PRTF files and inspect JSON output:
  ```bash
  python cli/as400_parser_cli.py batch rpg3-student-mgmt -o ./output --parallel 4
  ```
- Verify JSON structure matches design doc
- Verify CJK text preserved correctly in keywords and constants
