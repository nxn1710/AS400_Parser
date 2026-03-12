---
phase: requirements
title: "Requirements: IR JSON Template for AS400 Legacy System"
description: Design an Intermediate Representation (IR) JSON template that supports multiple AS400 source types with initial focus on RPG3
---

# Requirements & Problem Understanding

## Problem Statement

**What problem are we solving?**

- AS400 legacy systems contain multiple interdependent source types (RPG3, RPG4, CL, DDS PF/LF, DSPF, PRTF) that currently lack a unified structured representation
- There is no standard intermediate format to bridge the gap between raw AS400 source code parsing and downstream processing stages (analysis, dependency mapping, code generation)
- Without a common IR, each source type requires bespoke handling in every downstream tool, creating duplicated effort and inconsistent results
- Developers building modernization, analysis, or code generation tools need a stable contract to program against

**Who is affected?**

- Developers building AS400 legacy modernization tools
- Developers building parsers for AS400 source types
- Developers performing dependency analysis across AS400 systems
- Developers building code generation tools to transform legacy logic into modern languages

**Current situation:**

- No unified IR exists; each processing stage must understand raw source formats directly
- Analysis and transformation tools are tightly coupled to specific source type formats

## Goals & Objectives

**Primary goals:**

1. Define a unified IR JSON template structure that can represent any AS400 source type
2. Design the IR to capture every part of every source line — structural, logical, and raw content
3. Prioritize RPG3 support in the initial detailed design
4. Ensure the IR serves as a stable contract between parsing and downstream stages

**Secondary goals:**

1. Support future dependency analysis by representing inter-source relationships
2. Support future semantic analysis and code generation by capturing program logic as full AST expression trees
3. Provide extensibility for all AS400 source types (RPG4, CL, DDS, DSPF, PRTF)

**Non-goals (explicitly out of scope):**

- Implementing any parser or code generator
- Writing parser algorithms or programming language-specific logic
- Producing example AS400 source code or example IR JSON instances
- Defining runtime behavior or execution semantics
- Building tooling that consumes the IR

## User Stories & Use Cases

### User Story 1: Unified Processing
As a developer building a legacy modernization tool,
I want a unified IR structure for AS400 sources
so that different languages can be processed consistently.

### User Story 2: Parser Contract
As a developer building a parser,
I want a clearly defined IR template
so that the parser knows exactly how to structure parsed data.

### User Story 3: Dependency Analysis
As a developer performing dependency analysis,
I want the IR to represent relationships between programs, files, and other resources
so that system dependencies can be analyzed.

### User Story 4: Code Generation
As a developer building code generation tools,
I want the IR to capture program logic as full expression ASTs and structural information
so that the logic can later be transformed into modern languages.

### User Story 5: System-Wide Representation
As a developer working with large AS400 systems,
I want the IR to support multiple source types in a consistent format
so that the entire system can be represented by aggregating individual IR documents.

**Key workflows:**

1. **Parse → IR → Analyze**: A parser reads raw AS400 source and outputs IR JSON; analysis tools consume the IR
2. **Parse → IR → Generate**: A parser outputs IR with full AST expressions; a code generator reads the IR and produces modern code
3. **Multi-source IR → Dependency Graph**: Multiple IR documents (one per source file) are aggregated to build a system-wide dependency graph

**Edge cases to consider:**

- Source files with non-standard formatting or mixed content
- Sources referencing external objects not present in the analyzed set
- Very large programs with deeply nested logic
- Conditional compilation or copy-member inclusions (copy members can be inlined or referenced)
- Source types with positional/column-based syntax (RPG3, DDS) where every column position must be captured

## Success Criteria

**How will we know when we're done?**

1. The IR template defines a per-source-file document structure with a consistent envelope (metadata, content, dependencies, indicators)
2. The IR template defines content structures for RPG3 constructs covering all specification types (H, F, E, L, I, C, O), subroutines, comments, and copy members
3. Every part of every source line is captured as discrete fields in the IR, including raw source text, inline comments, and all column positions
4. Calculation spec factors are represented as full AST expression trees (not raw strings)
5. Indicators are captured both in a global program-wide registry and in-context on each spec node
6. Copy members support both inlined (embedded parsed content) and referenced (symbolic) modes
7. The IR template defines a dependencies section for representing inter-source relationships
8. The IR template is language-neutral at the envelope level, with type-specific content sections
9. The design is documented with clear descriptions of every node type, field, and their purposes
10. The template is extensible — adding support for a new source type does not require structural changes to the envelope

## Constraints & Assumptions

**Technical constraints:**

- The IR must be expressed as a JSON structure (no binary formats, no XML)
- The IR is a template/schema definition, not a runtime data instance
- The design must not embed programming language-specific implementation details
- Every specification node must include the raw source line (`rawSourceLine`) alongside its structured representation
- Calculation spec factors and conditions must be represented as full AST expression trees
- Indicators must be comprehensively represented — both as a global map and in-context on each spec node
- Every discrete column position from each RPG3 specification line must be captured as a separate field

**Business constraints:**

- RPG3 must be the first fully detailed source type
- Other source types need structural placeholders but not full detail in this iteration

**Assumptions:**

- Each source file produces exactly one IR document (no multi-file containers)
- System-wide views (dependency graphs) are built by aggregating individual IR documents downstream
- Source ordering within files is semantically meaningful and must be preserved
- The IR will be consumed by automated tools, not primarily by humans
- External references (e.g., files, programs, data areas) will be captured as symbolic references, not resolved inline
- Source metadata (member name, library, source type, dates) is available at parse time
- The existing AS400_Parser project will eventually produce parsers that emit this IR
- Indicators are captured both in a global registry and in-context on each spec node

## Questions & Open Items (Resolved)

All open questions have been resolved:

1. **Raw source lines:** ✅ Include raw source line content (`rawSourceLine`) alongside structured representations on every spec node, plus both standalone line comments and inline comments
2. **Expression detail level:** ✅ Use full AST expression trees for factor1, factor2, and result fields in calculation specifications — not just raw string values
3. **Copy member handling:** ✅ Support both inlined and referenced modes — the `inlined` flag controls whether parsed content is embedded in `inlinedContent`
4. **Indicator representation:** ✅ Include comprehensive indicator representation — a global indicator map plus in-context indicators on every spec node. Every part of every line must be captured in the IR structure
