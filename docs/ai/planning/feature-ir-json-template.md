---
phase: planning
title: "Planning: IR JSON Template for AS400 Legacy System"
description: Task breakdown and implementation plan for the IR JSON template design
---

# Project Planning & Task Breakdown

## Milestones

- [x] Milestone 1: Requirements Documentation — Capture problem statement, goals, user stories, constraints, and resolved design decisions
- [x] Milestone 2: Design Documentation — Define four-section envelope (metadata, content, dependencies, indicators) with detailed RPG3 content including full AST expressions, raw source preservation, and comprehensive indicators
- [x] Milestone 3: Planning Documentation — Break down tasks and define implementation order
- [x] Milestone 4: Documentation Review — Requirements review completed, all gaps identified and fixed
- [x] Milestone 5: Document Rewrite — All phase documents rewritten with resolved decisions fully integrated

## Task Breakdown

### Phase 1: Foundation (Requirements)

- [x] Task 1.1: Define problem statement and stakeholder needs
- [x] Task 1.2: Write user stories covering all downstream consumers (parser, analyzer, generator)
- [x] Task 1.3: Establish success criteria aligned with per-source-file document model
- [x] Task 1.4: Define constraints including raw source preservation, full AST, and indicator representation
- [x] Task 1.5: Document assumptions including one-doc-per-file and downstream aggregation
- [x] Task 1.6: Identify and resolve open questions (raw lines, AST level, copy members, indicators)

### Phase 2: Core Design

- [x] Task 2.1: Design four-section IR document envelope (`metadata`, `content`, `dependencies`, `indicators`)
- [x] Task 2.2: Define `metadata` section schema with all provenance fields and `parseInfo`
- [x] Task 2.3: Define global `indicators` section with program-wide indicator registry covering all categories (01–99, L1–L9, LR, MR, OA–OV, U1–U8, H1–H9, 1P, KA–KY, RT)
- [x] Task 2.4: Design polymorphic `content` section architecture with `rawSourceLine` on every node
- [x] Task 2.5: Define complete RPG3 content structure with every column position captured:
  - [x] H-spec (headerSpecs) with all column positions
  - [x] F-spec (fileSpecs) with continuation lines
  - [x] E-spec (extensionSpecs)
  - [x] L-spec (lineCounterSpecs)
  - [x] I-spec (inputSpecs) with record identification, field definitions, data structures
  - [x] C-spec (calculationSpecs) with full AST expression trees
  - [x] O-spec (outputSpecs) with field definitions
  - [x] Subroutines with cross-reference locations
  - [x] Comments (standalone and inline)
  - [x] Copy members (both inlined and referenced modes)
  - [x] Source lines (complete ordered array for round-trip reconstruction)
- [x] Task 2.6: Define Expression AST node types (literal, identifier, arrayElement, binaryOp, unaryOp, indicator, figurativeConstant, specialValue)
- [x] Task 2.7: Define control flow AST nodes (conditionalBlock, doWhileBlock, doUntilBlock, doBlock, caseBlock, labelNode, gotoNode, subroutineBlock, callSubroutine)
- [x] Task 2.8: Define `dependencies` section with reference types
- [x] Task 2.9: Define extensibility placeholders for RPG4, CL, DDS PF, DDS LF, DSPF, PRTF
- [x] Task 2.10: Document 9 key design decisions with rationale

### Phase 3: Review & Validation

- [x] Task 3.1: Run /review-requirements workflow
- [x] Task 3.2: Identify gaps (success criterion #1, missing constraints, missing assumptions)
- [x] Task 3.3: Fix all gaps and rewrite all phase documents
- [x] Task 3.4: Verify design doc consistency with updated requirements

## Dependencies

- **Internal:** This is a documentation-only task; no code dependencies
- **Downstream:** Future parser implementation will depend on this IR template as its output contract
- **Knowledge:** Understanding of RPG3 specification types and their column-based format (positions 1–80)

## Timeline & Estimates

| Task | Status |
|---|---|
| Requirements documentation | ✅ Complete (rewritten with gaps fixed) |
| Design documentation | ✅ Complete (comprehensive RPG3 spec) |
| Planning documentation | ✅ Complete (rewritten) |
| Implementation notes | ✅ Complete (rewritten) |
| Testing strategy | ✅ Complete (rewritten) |
| Requirements review | ✅ Complete |
| All documents rewritten | ✅ Complete |

## Risks & Mitigation

| Risk | Impact | Mitigation |
|---|---|---|
| RPG3 spec coverage gaps | Medium | Cross-reference IBM RPG III reference manual; every column position captured |
| IR too tightly coupled to RPG3 | High | Four-section envelope is generic; only content section is type-specific |
| Missing dependency types | Medium | `customReferences` array for unforeseen reference types |
| IR too verbose for large programs | Low | Design supports streaming; each document is independent |
| Expression AST complexity | Medium | Well-defined node types with clear hierarchy; `rawText` preserved on each node for fallback |
| Indicator tracking overhead | Low | Global map is optional enrichment; in-context indicators are the primary representation |

## Resources Needed

- IBM RPG III reference documentation for spec validation during parser implementation
- Existing AS400_Parser project codebase for alignment
- Knowledge of downstream tool requirements (dependency analyzer, code generator)
