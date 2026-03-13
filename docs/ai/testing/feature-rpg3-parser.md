---
phase: testing
title: "Testing Strategy: RPG3 Parser"
description: Test plan, test cases, coverage targets, and quality assurance for the RPG3 parser
---

# Testing Strategy — RPG3 Parser

## Test Coverage Goals

- **Unit test coverage target:** 100% of new/changed code (all Java classes)
- **Integration test scope:** End-to-end pipeline from source file → IR JSON
- **Compliance test:** IR output matches `example/ir/rpg3.json` field-by-field
- **Alignment:** All test cases trace back to requirements success criteria or design edge cases

---

## Unit Tests

### SourceNormalizer (`common/normalizer/`)
- [ ] Trim trailing whitespace and pad to exactly 80 chars
- [ ] Tab expansion at 8-column stops (default)
- [ ] Tab expansion at SEU-style stops (configurable)
- [ ] Control character stripping (0x00–0x1F removed)
- [ ] DBCS preservation (SO 0x0E / SI 0x0F retained)
- [ ] CRLF → LF, CR → LF standardization
- [ ] Sequence number extraction (cols 1–5 → metadata, replaced with spaces)
- [ ] Original line number mapping (1-based, preserved after normalization)
- [ ] Lines > 80 chars → truncation warning in `NormalizationWarning`
- [ ] DBCS-aware padding (SO/SI delimited content occupies 2 column positions)
- [ ] Multi-encoding input: UTF-8, Shift-JIS, EUC-JP
- [ ] Empty source → empty `NormalizedSource` (no crash)

### ANTLR Grammar (`grammar/rpg3/`)
- [ ] Grammar compiles without ANTLR errors (`gradle generateGrammarSource`)
- [ ] H-spec only source → valid parse tree
- [ ] F-spec with continuation lines → correct tree
- [ ] E-spec → correct token extraction
- [ ] L-spec → correct token extraction
- [ ] I-spec (record identification + field definition) → correct tree
- [ ] I-spec (Data Structure: `DS` in cols 19-20) → correct tree
- [ ] C-spec with all column positions → correct tokens
- [ ] O-spec (record-level + field-level) → correct tree
- [ ] Comment lines (col 7 = `*`) → recognized
- [ ] Directive lines (`/COPY`, `/EJECT`) → recognized
- [ ] Compile-time data (`**` separator + data blocks) → correct tree
- [ ] Mixed-case opcodes (case-insensitive) → recognized

### Expression AST Builder (`rpg3/Rpg3IrBuilder`)
- [ ] Blank/empty factor → `null`
- [ ] `*BLANKS` → `FigurativeConstantNode(constant: "*BLANKS")`
- [ ] `*ZEROS` → `FigurativeConstantNode(constant: "*ZEROS")`
- [ ] `*HIVAL` → `FigurativeConstantNode(constant: "*HIVAL")`
- [ ] `*LOVAL` → `FigurativeConstantNode(constant: "*LOVAL")`
- [ ] `*ALL` → `FigurativeConstantNode(constant: "*ALL")`
- [ ] `*ON` → `FigurativeConstantNode(constant: "*ON")`
- [ ] `*OFF` → `FigurativeConstantNode(constant: "*OFF")`
- [ ] `*IN03` → `IndicatorNode(indicator: "03", category: "numeric")`
- [ ] `*INLR` → `IndicatorNode(indicator: "LR", category: "control")`
- [ ] `*INH1` → `IndicatorNode(indicator: "H1", category: "halt")`
- [ ] `*IN` (exactly) → `SpecialValueNode(value: "*IN")` (**NOT** IndicatorNode)
- [ ] `UDATE` → `SpecialValueNode(value: "UDATE")`
- [ ] `PAGE` → `SpecialValueNode(value: "PAGE")`
- [ ] `'ABC'` (quoted) → `LiteralNode(value: "ABC", dataType: "A")`
- [ ] `100` (numeric) → `LiteralNode(value: 100, dataType: "S")`
- [ ] `ARR,X` → `ArrayElementNode(arrayName: "ARR", index: IdentifierNode("X"))`
- [ ] `CUSTNO` → `IdentifierNode(name: "CUSTNO")`

### C-Spec Column Extraction
- [ ] Control level extraction (cols 7-8): blank, L0-L9, LR, SR, AN, OR
- [ ] Conditioning indicator extraction (cols 9-17): 3 positions with N-prefix
  - `N50` → `{not: true, indicator: "50", position: "first"}`
  - `  50` → `{not: false, indicator: "50", position: "first"}`
  - Multiple indicators in positions first/second/third
- [ ] Factor 1 extraction (cols 18-27)
- [ ] Opcode extraction (cols 28-32)
- [ ] Factor 2 extraction (cols 33-42)
- [ ] Result field extraction (cols 43-48)
- [ ] Field length extraction (cols 49-51): numeric or null
- [ ] Decimal positions extraction (col 52): numeric or null
- [ ] Extended opcode extraction (col 53): `H`, `N`, `P`, or blank
- [ ] Resulting indicators (cols 54-59): high/low/equal
- [ ] Inline comment extraction (cols 60-74)

### Control Flow Block Builder
- [ ] `IFGT` → `conditionalBlock` with `comparisonType: "GT"`, `condition`, `comparisonValue`
- [ ] `IFGT` + `ELSE` + `END` → `thenOps` and `elseOps` correctly populated
- [ ] `DOWEQ` → `doWhileBlock` with correct condition/bodyOps
- [ ] `DOUEQ` → `doUntilBlock` with correct condition/bodyOps
- [ ] `DO` → `doBlock` with startValue/endValue/indexField/bodyOps
- [ ] `CASxx` + multiple cases + `END` → `caseBlock` with cases array
- [ ] `BEGSR` + `ENDSR` → `subroutineBlock` with name and operations
- [ ] `TAG` → `labelNode` with name
- [ ] `GOTO` → `gotoNode` with targetLabel
- [ ] `EXSR` → `callSubroutine` with subroutineName
- [ ] **Nested blocks:** IF inside DOW inside BEGSR (3-level nesting)
- [ ] **ANDxx:** `IFGT` + `ANDLT` → compound `BinaryOpNode("AND", BinaryOpNode("GT",...), BinaryOpNode("LT",...))`
- [ ] **ORxx:** `IFGT` + `OREQ` → compound `BinaryOpNode("OR", ...)`
- [ ] **Mixed ANDxx/ORxx:** `IFGT` + `ANDLT` + `OREQ` → correct nesting
- [ ] `END` matches correct opening block (stack validation)

### Symbol Table Builder
- [ ] Fields from I-spec → symbol entries with explicit type info
- [ ] Subfields from data structures → symbol entries
- [ ] Arrays from E-spec → symbol entries with length/decimals
- [ ] Result fields from C-spec → symbol entries (inferred type)
- [ ] **Conflict resolution:** I-spec wins over C-spec for same field name
- [ ] **Type inference:** decimals specified → `S`, null → `A`
- [ ] **Back-propagation:** IdentifierNode expressions get resolved types
- [ ] Subroutine `calledFrom` populated from EXSR locations

### Copy Resolver
- [ ] Parse `FILE,MEMBER` directive format
- [ ] Parse `LIB/FILE,MEMBER` directive format
- [ ] Search order: `.rpg` → `.rpg3` → `.mbr` → no extension
- [ ] First match wins (left-to-right in copyPaths)
- [ ] Missing member → `ResolvedCopy(found=false, warning=...)`
- [ ] `LIB/FILE,MEMBER` → resolves as `sourceRoot/LIB/FILE/MEMBER`

### JSON Serializer
- [ ] `null` fields serialized as JSON null
- [ ] Empty string `""` preserved (not omitted)
- [ ] Empty arrays `[]` serialized (not null)
- [ ] Expression node polymorphism: correct `nodeType` field on each subclass
- [ ] camelCase field names throughout
- [ ] Pretty-printed output

### Zero-Loss Error Handling
- [ ] Tier 1: Normal parse → `parseQuality: "full"`, node in spec array
- [ ] Tier 2: ANTLR fails, form type recognized → `parseQuality: "columnOnly"`, raw column values extracted, node in spec array
- [ ] Tier 3: Unknown form type → `parseQuality: "raw"`, `UnparsedSpec` with column map in spec array
- [ ] **No line ever skipped** — every source line appears in both `sourceLines[]` AND relevant spec array
- [ ] Partial parse → `parseStatus: "partial"`, errors populated
- [ ] Tier 2 nodes have `rawText`-only expression nodes (no resolved types)

---

## Integration Tests

### End-to-End Pipeline (CUSTINQ Sample)
- [ ] Parse `CUSTINQ.rpg` → produce IR JSON
- [ ] Compare against `example/ir/rpg3.json` field-by-field
- [ ] All 7 spec types present and correct
- [ ] Expression AST nodes have correct types and values
- [ ] Symbol table contains all expected entries
- [ ] Subroutines have `calledFrom` populated
- [ ] Comments have correct `specContext`
- [ ] `sourceLines[]` contains all lines in order
- [ ] `dependencies` section populated (files, programs)

### Edge Case Integration Tests
- [ ] F-spec continuation lines → multi-line file declaration merged
- [ ] O-spec record-level + field-level → correct hierarchy
- [ ] Nested control flow: IF inside DOW inside BEGSR → 3-level AST
- [ ] Mixed-case source → case-insensitive opcode matching
- [ ] 5000+ line source → completes < 1 second
- [ ] Source with compile-time data (`**`) → CTDATA blocks associated with E-spec arrays
- [ ] Blank lines and comments interspersed → non-spec lines handled
- [ ] Missing /COPY member → warning in errors, parsing continues
- [ ] Intentionally malformed source → all lines captured (zero-loss validation)

### CLI Integration Tests
- [ ] `rpg3-parser parse source/CUSTINQ.rpg -o output.json` → valid JSON file created
- [ ] `rpg3-parser batch source/ -o output/` → one JSON per source file
- [ ] `rpg3-parser validate output.json` → validation passes on valid JSON
- [ ] `rpg3-parser validate bad.json` → validation fails with meaningful error
- [ ] `--charset shift-jis` option → processes Japanese source correctly
- [ ] `--copy-path` option → resolves copy members from specified paths

---

## Test Data

### Fixtures
- `example/source/CUSTINQ.rpg` — primary sample RPG3 source (all 7 spec types)
- `example/ir/rpg3.json` — golden reference IR JSON output

### Additional Test Fixtures Needed
- [ ] `test/fixtures/simple_h_spec.rpg` — H-spec only (grammar smoke test)
- [ ] `test/fixtures/all_opcodes.rpg` — Source covering ~120 RPG3 opcodes
- [ ] `test/fixtures/nested_control_flow.rpg` — 3+ levels of IF/DOW/BEGSR nesting
- [ ] `test/fixtures/compound_conditions.rpg` — ANDxx/ORxx test cases
- [ ] `test/fixtures/copy_members/` — Directory with copy member fixtures
- [ ] `test/fixtures/ctdata_source.rpg` — Source with `**` compile-time data
- [ ] `test/fixtures/malformed.rpg` — Intentionally broken source for zero-loss testing
- [ ] `test/fixtures/large_program.rpg` — 5000+ lines for performance testing
- [ ] `test/fixtures/japanese_dbcs.rpg` — DBCS/Japanese content for encoding tests

---

## Test Reporting & Coverage

**Coverage commands:**
```bash
# Run tests with coverage
gradle test jacocoTestReport

# Check coverage threshold (100% target)
gradle jacocoTestCoverageVerification
```

**JaCoCo configuration in `build.gradle`:**
```groovy
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 1.0  // 100% line coverage
            }
        }
    }
}
```

**Coverage gaps to monitor:**
- Expression AST builder: all 9 detection paths must be covered
- Control flow builder: all block types + nesting combinations
- Zero-loss fallback: all 3 tiers must be exercised
- Copy resolver: all 4 file extensions + not-found path

---

## Performance Testing

| Test | Target | Method |
|---|---|---|
| 1000-line source | < 1 second | `System.nanoTime()` before/after |
| 5000-line source | < 3 seconds | Same |
| Batch 100 files | < 30 seconds | CLI `--parallel 4` |
| JVM cold start | < 3 seconds first file | Single file parse from CLI |

---

## Bug Tracking

**Severity levels:**
| Level | Definition | Example |
|---|---|---|
| **Critical** | Data loss — source line silently skipped | Missing spec node in IR |
| **High** | Wrong data — column extracted incorrectly | Off-by-one in factor1 extraction |
| **Medium** | Incomplete — field missing or null when it shouldn't be | Missing `extendedOpcode` on operation |
| **Low** | Cosmetic — formatting or warning message quality | Warning message unclear |

**Regression testing:** Run full CUSTINQ golden test after every change to catch regressions.
