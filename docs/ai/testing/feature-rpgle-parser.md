---
phase: testing
title: Testing Strategy & Results
description: Define test strategy and track test results
---

# Testing Strategy & Results - RPGLE Parser

## Test Coverage Targets

| Component | Target | Current |
|-----------|--------|---------|
| RpgleFixedParser | 100% | Targeted unit tests in place |
| RpgleFreeParser | 100% | Targeted unit tests in place |
| RpgleIrBuilder | 100% | Targeted unit tests in place |
| RpgleParserFacade | 100% | Integration coverage in place |
| Integration | 100% | Fixture-based coverage in place |

## Test Plan

### Unit Tests
- `RpgleFixedParserTest` - Each spec type (H, F, D, C, I, O, P)
- `RpgleFreeParserTest` - Free-format constructs parsed via ANTLR/text extraction
- `RpgleIrBuilderTest` - Format detection, source-line assembly, dependency extraction, `/COPY`, and subroutine cross-reference

### Integration Tests
- `RpgleParserIntegrationTest` - End-to-end with real source files

### Test Fixtures
Located at `parser-core/src/test/resources/rpgle/`:
- `simple_fixed.rpgle` - Fixed-format only
- `fully_free.rpgle` - Free-format only
- `mixed_format.rpgle` - Mixed fixed + free

## Test Results

- Added `parser-core/src/test/java/com/as400parser/rpgle/RpgleIrBuilderTest.java`
- Verified fully free, mixed-format, and fixed-format IR assembly paths
- Verified `/COPY` dependency extraction in mixed-format input
- Verified nested `EXSR` calls populate subroutine `calledFrom`
- Test command: `./gradlew.bat :parser-core:test --tests "com.as400parser.rpgle.*"`
- Result on 2026-04-12: `BUILD SUCCESSFUL`
