---
phase: testing
feature: analyzer
title: "Testing: Analyzer"
description: "Testing strategy for the analyzer feature"
---

# Testing Strategy: Analyzer

## Test Coverage Goals

- Unit test coverage target: 100% of new analyzer code
- Integration test: Full student-mgmt app analysis verification
- Test data: Use existing IR JSON output from `output/` directory

## Unit Tests

### SchemaAnalyzer
- [ ] PF with all field types (A, P, S, B) → correct SQL types
- [ ] PF with composite primary key → correct key list
- [ ] PF with UNIQUE keyword → unique flag set
- [ ] PF with DFT, VALUES keywords → default and constraint extraction
- [ ] LF simple (keys only) → IndexDefinition with correct key fields
- [ ] LF with select/omit → SelectOmitRule extraction
- [ ] LF with PFILE → correct parent table reference

### DdsToSqlTypeMapper
- [ ] All 8 DDS types mapped correctly
- [ ] Edge cases: null decimal, zero length, binary length thresholds

### CrossReferenceAnalyzer
- [ ] RPG3 CALL extraction from C-specs
- [ ] RPG3 EXSR extraction
- [ ] RPGLE CALLP extraction
- [ ] CL CALL PGM extraction
- [ ] Reverse call graph (calledBy)

### FileUsageAnalyzer
- [ ] RPG3 F-spec → file access mode
- [ ] C-spec READ/CHAIN/WRITE operations → IOOperation set
- [ ] Multiple files per program

## Integration Tests

- [ ] Full student-mgmt app: parse + analyze → verify cross-reference completeness
- [ ] AnalysisResult JSON serialization/deserialization round-trip

## Test Data

- Reuse existing IR JSON from `output/QDDSSRC/`, `output/QRPGSRC/`, `output/QCLSRC/`
- Load JSON fixtures in tests via classpath resources
