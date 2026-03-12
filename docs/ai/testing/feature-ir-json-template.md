---
phase: testing
title: "Testing Strategy: IR JSON Template for AS400 Legacy System"
description: Validation approach for the IR JSON template design
---

# Testing Strategy

## Test Coverage Goals

Since this is a design-only deliverable (no code), testing focuses on **design validation**. When parsers are implemented, this document also defines the automated validation approach.

- ✅ Requirements completeness and consistency
- ✅ Design alignment with requirements (all constraints and decisions reflected)
- ✅ RPG3 spec coverage across all specification types
- ✅ Extensibility validation (other source types fit the envelope)
- ✅ User story satisfaction
- ✅ Full fidelity validation (every column position, raw source, AST, indicators)

## Design Validation Tests

### Requirements Alignment

- [x] Test 1: All five user stories are addressed by the design
- [x] Test 2: All constraints are respected (JSON only, no code, template only)
- [x] Test 3: Non-goals are not introduced in the design
- [x] Test 4: Success criteria are individually verifiable against the design
- [x] Test 5: Success criterion #1 correctly describes per-source-file model (not multi-file container)
- [x] Test 6: Raw source preservation constraint is reflected in every spec node
- [x] Test 7: Full AST expression constraint is reflected in C-spec factor definitions
- [x] Test 8: Indicator representation constraint is reflected in global map and in-context fields

### Envelope & Metadata

- [x] Test 9: Four-section envelope (metadata, content, dependencies, indicators) defined
- [x] Test 10: Metadata section covers all provenance fields (member, file, library, dates, record length, line count)
- [x] Test 11: `parseInfo` sub-object captures parser version, timestamp, status, errors, warnings
- [x] Test 12: `irVersion` field enables schema evolution

### Global Indicators Section

- [x] Test 13: Numeric indicators `01`–`99` covered
- [x] Test 14: Control level indicators `L1`–`L9` covered
- [x] Test 15: Last record (`LR`), matching record (`MR`), return (`RT`) indicators covered
- [x] Test 16: Overflow indicators `OA`–`OG`, `OV` covered
- [x] Test 17: External indicators `U1`–`U8` covered
- [x] Test 18: Halt indicators `H1`–`H9` covered
- [x] Test 19: First page indicator `1P` covered
- [x] Test 20: Function key indicators `KA`–`KN`, `KP`–`KY` covered
- [x] Test 21: Each indicator has `setAt`, `testedAt`, `description`, `category` fields

### RPG3 Content Coverage

- [x] Test 22: H-spec captures all column positions (debug option, currency, date format, etc.)
- [x] Test 23: F-spec captures all column positions (type, designation, EOF, sequence, format, record length, key length, device, etc.)
- [x] Test 24: F-spec continuation lines support keywords and values
- [x] Test 25: E-spec captures all fields (from/to file, table/array, entries, length, decimals, format, sequence, alternate)
- [x] Test 26: L-spec captures file name, lines per page, form length, overflow line
- [x] Test 27: I-spec record identification captures position, not, comparison type, character
- [x] Test 28: I-spec field definitions capture from/to position, decimals, data type, control level, matching fields, field indicators (plus/minus/zero)
- [x] Test 29: I-spec data structures capture type, external name, occurrences, subfields
- [x] Test 30: C-spec captures all column positions (control level, conditioning indicators, factor1, opcode, factor2, result, length, decimals, resulting indicators, inline comment)
- [x] Test 31: C-spec conditioning indicators capture not/indicator/position for all three slots
- [x] Test 32: C-spec resulting indicators capture high/low/equal
- [x] Test 33: O-spec captures file name, record type, add/delete, spacing, skipping, output indicators, fields
- [x] Test 34: O-spec field definitions capture field name, edit code, blank after, end position, data format, edit word
- [x] Test 35: Subroutines extract name, location, operations, and calledFrom references
- [x] Test 36: Comments capture standalone (full-line) and inline comments with specContext

### Raw Source Preservation

- [x] Test 37: `rawSourceLine` present on every spec node (H, F, E, L, I, C, O)
- [x] Test 38: `inlineComment` present on every spec node
- [x] Test 39: `sourceLines` array provides complete ordered raw text for round-trip reconstruction
- [x] Test 40: Each source line entry has `lineNumber`, `rawText`, `specType`, `isComment`, `isBlank`

### Expression AST

- [x] Test 41: Factor1 is an expression AST object (not a raw string)
- [x] Test 42: Factor2 is an expression AST object (not a raw string)
- [x] Test 43: Result field is an expression AST object (not a raw string)
- [x] Test 44: Node type `literal` defined with `value`, `dataType`
- [x] Test 45: Node type `identifier` defined with `name`, `isArray`, `arrayIndex`
- [x] Test 46: Node type `arrayElement` defined with `arrayName`, `index`
- [x] Test 47: Node type `binaryOp` defined with `operator`, `left`, `right`
- [x] Test 48: Node type `unaryOp` defined with `operator`, `operand`
- [x] Test 49: Node type `indicator` defined with `indicator`, `category`
- [x] Test 50: Node type `figurativeConstant` defined with `constant`
- [x] Test 51: Node type `specialValue` defined with `value`
- [x] Test 52: All expression nodes include `nodeType`, `rawText`, `location`

### Control Flow AST

- [x] Test 53: `conditionalBlock` (IFxx/ELSE/END) with condition, comparisonType, thenOps, elseOps
- [x] Test 54: `doWhileBlock` (DOWxx/END) with condition, comparisonType, bodyOps
- [x] Test 55: `doUntilBlock` (DOUxx/END) with condition, comparisonType, bodyOps
- [x] Test 56: `doBlock` (DO/END) with startValue, endValue, indexField, bodyOps
- [x] Test 57: `caseBlock` (CASxx/END) with cases array and defaultSubroutine
- [x] Test 58: `labelNode` (TAG) with name
- [x] Test 59: `gotoNode` (GOTO) with targetLabel
- [x] Test 60: `subroutineBlock` (BEGSR/ENDSR) with name and operations
- [x] Test 61: `callSubroutine` (EXSR) with subroutineName

### Copy Members

- [x] Test 62: Copy members capture memberName, fileName, libraryName, qualifiedPath
- [x] Test 63: `inlined` boolean flag controls mode
- [x] Test 64: When `inlined = true`, `inlinedContent` contains nested parsed IR
- [x] Test 65: When `inlined = true`, `inlinedSourceLines` contains raw source lines
- [x] Test 66: When `inlined = false`, symbolic reference is sufficient
- [x] Test 67: `resolved` boolean indicates whether the member was found

### Dependencies Section

- [x] Test 68: `referencedFiles` with name, library, referenceType, locations, qualifiedName
- [x] Test 69: `calledPrograms` with same structure
- [x] Test 70: `referencedDataAreas` with same structure
- [x] Test 71: `referencedDataQueues` with same structure
- [x] Test 72: `copyMembers` with same structure
- [x] Test 73: `referencedAPIs` with same structure
- [x] Test 74: `customReferences` for extensibility

### Extensibility

- [x] Test 75: Adding RPG4 content schema does not require envelope changes
- [x] Test 76: Adding CL content schema does not require envelope changes
- [x] Test 77: Adding DDS (PF/LF) content schema does not require envelope changes
- [x] Test 78: Adding DSPF content schema does not require envelope changes
- [x] Test 79: Adding PRTF content schema does not require envelope changes
- [x] Test 80: `customReferences` in dependencies supports unforeseen reference types

### Consistency

- [x] Test 81: All field names follow camelCase convention
- [x] Test 82: All nodes that need source tracking include a `location` object
- [x] Test 83: All arrays are documented as preserving source order
- [x] Test 84: `formType` field present on every spec node

## Manual Verification

**Steps for manual review:**

1. Read the design document section by section
2. For each RPG3 content node, verify it maps to a known RPG III specification type and captures all column positions
3. For each design decision, verify the rationale is sound and consistent with requirements constraints
4. Verify the dependency section can represent: file references, program calls, data area access, copy members, API calls
5. Pick a known RPG3 program and mentally map its structure to the IR template — identify any structural gaps
6. Verify that raw source lines, inline comments, and indicators are present on every relevant node
7. Verify the Expression AST can represent all RPG3 factor patterns (literals, fields, arrays, indicators, figurative constants)

## Future Automated Testing (Post-Implementation)

When parsers are implemented:

- **JSON Schema validation**: Create a JSON Schema from the design and validate all parser output against it
- **Round-trip testing**: Parse → IR → reconstruct source from `sourceLines` → compare with original
- **Expression AST validation**: Verify each factor produces the correct AST node type
- **Indicator consistency**: Verify global indicator map matches the sum of all in-context indicator references
- **Copy member inlining**: Test both modes (inlined vs. referenced) produce valid IR
- **Cross-source dependency testing**: Generate IR for multiple related sources and verify dependency links resolve
- **Coverage checking**: Verify every RPG3 opcode produces valid C-spec IR nodes
