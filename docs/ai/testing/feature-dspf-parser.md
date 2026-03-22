---
phase: testing
title: Testing Strategy
description: Define testing approach, test cases, and quality assurance
---

# Testing Strategy — DSPF Parser

## Test Coverage Goals

- Unit test coverage target: 100% of `DspfIrBuilder` parsing logic
- Integration tests: all 3 sample DSPF files parsed successfully
- Alignment with all success criteria in requirements doc

## Unit Tests

### DspfIrBuilder (`DspfIrBuilderTest.java`)

- [ ] Test: File-level keywords parsed (DSPSIZ, CA03, CA12, PRINT)
- [ ] Test: Record format detected with correct name
- [ ] Test: Named field parsed with all attributes (name, type, length, usage, screen coordinates)
- [ ] Test: Constant line parsed with text and screen position
- [ ] Test: Conditioning indicators parsed from columns 8-16 (positive and negated)
- [ ] Test: Display attribute keyword (DSPATR) captured on fields
- [ ] Test: Continuation lines merged into parent keyword list
- [ ] Test: SFL record type detected from SFL keyword
- [ ] Test: SFLCTL record type detected with sflControlFor value
- [ ] Test: Comments preserved in comments array
- [ ] Test: Source lines captured in sourceLines array
- [ ] Test: System keywords (DATE, TIME) handled as constants with keywords

### Conditioning Indicator Parsing

- [ ] Test: Single indicator (e.g., `60`) parsed correctly
- [ ] Test: Negated indicator (e.g., `N60`) parsed correctly
- [ ] Test: Multiple indicators in all 3 slots
- [ ] Test: Blank indicator slots ignored

## Integration Tests

### DspfIntegrationTest (`DspfIntegrationTest.java`)

- [ ] Test: Parse MNUDSPF.dspf end-to-end — verify sourceType, record format count, field count, file keywords
- [ ] Test: Parse STUDSPF.dspf end-to-end — verify 3 record formats, conditioning indicators on fields
- [ ] Test: Parse STULSTD.dspf end-to-end — verify SFL/SFLCTL relationship, subfile keywords with indicators

## Test Data

- Sample files: `rpg3-student-mgmt/QDDSSRC/MNUDSPF.dspf`, `STUDSPF.dspf`, `STULSTD.dspf`
- Inline test strings for unit tests

## Test Commands

```bash
# Run all DSPF tests
./gradlew test --tests "com.as400parser.dspf.*"

# Run specific test class
./gradlew test --tests "com.as400parser.dspf.DspfIrBuilderTest"
./gradlew test --tests "com.as400parser.dspf.DspfIntegrationTest"

# Run all tests (including existing DDS/RPG3 regression)
./gradlew test
```

## Manual Testing

- Run CLI against each sample file and inspect JSON output:
  ```bash
  ./gradlew :parser-core:run --args="--source rpg3-student-mgmt/QDDSSRC/MNUDSPF.dspf"
  ```
- Verify JSON structure matches design doc examples
- Verify CJK text preserved correctly in keywords and constants
