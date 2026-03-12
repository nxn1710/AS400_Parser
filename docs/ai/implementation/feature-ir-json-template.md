---
phase: implementation
title: "Implementation Notes: IR JSON Template for AS400 Legacy System"
description: Technical implementation guidance and notes for the IR JSON template
---

# Implementation Guide

## Development Setup

**Prerequisites:**

- No code implementation is required for this feature — this is a design-only deliverable
- Future parser implementation will reference these documents as the output contract

## Code Structure

**Design artifact locations:**

| Document | Path |
|---|---|
| Requirements | `docs/ai/requirements/feature-ir-json-template.md` |
| Design | `docs/ai/design/feature-ir-json-template.md` |
| Planning | `docs/ai/planning/feature-ir-json-template.md` |
| Implementation | `docs/ai/implementation/feature-ir-json-template.md` |
| Testing | `docs/ai/testing/feature-ir-json-template.md` |

## Implementation Notes

### Key Technical Details

1. **Four-section envelope**: Every IR document uses `metadata`, `content`, `dependencies`, `indicators`. The envelope is fixed across all source types.
2. **Polymorphic content**: The `content` section schema varies by `metadata.sourceType`. RPG3 is fully defined; other types have placeholders.
3. **Raw source on every node**: Every specification node includes `rawSourceLine` with the original source text, plus `inlineComment` for inline comments.
4. **Full AST expressions**: Factor1, factor2, and result fields in C-specs are represented as expression AST trees with typed nodes (`literal`, `identifier`, `arrayElement`, `binaryOp`, `unaryOp`, `indicator`, `figurativeConstant`, `specialValue`).
5. **Comprehensive indicators**: A global `indicators` section provides a program-wide registry with set/test locations. In-context indicators are also preserved on each spec node.
6. **Copy members — dual mode**: Copy members support both inlined (parsed content embedded in `inlinedContent`) and referenced (symbolic reference) modes, controlled by the `inlined` flag.
7. **Every column captured**: All RPG3 spec nodes have discrete fields for every column position in the source line.
8. **Source ordering preserved**: All arrays in the IR are ordered as items appear in the source.
9. **Source lines array**: A `sourceLines` array provides complete ordered raw text for round-trip reconstruction.

### Future Implementation Steps

When implementing parsers that emit this IR:

1. **JSON Schema first**: Create a formal JSON Schema from the design document to enable automated validation of parser output
2. **Shared envelope module**: Implement the metadata envelope and dependencies section as a shared base — these are identical across all source types
3. **Global indicator collector**: Implement a post-parse pass that scans all spec nodes to build the global `indicators` map with set/test locations
4. **RPG3 content builder**: Implement first, aligned with the detailed RPG3 content spec:
   - Parse each spec type (H/F/E/L/I/C/O) into its dedicated array
   - Build Expression AST nodes for C-spec factors (factor1, factor2, resultField)
   - Detect control flow blocks (IFxx/ELSE/END, DOWxx/DOUxx/END, CASxx/END, BEGSR/ENDSR) and produce nested AST structures
   - Capture raw source on every node (`rawSourceLine`, `inlineComment`)
   - Extract subroutines into the top-level `subroutines` array with cross-reference locations
   - Build `sourceLines` array for round-trip capability
5. **Copy member resolution**: Implement as configurable — parsers should support both modes:
   - Referenced (default): emit symbolic reference only
   - Inlined: resolve the copy member source, parse it, and embed the result in `inlinedContent`
6. **Dependency extraction**: Implement as a post-parse pass that scans F-specs (file references), C-specs (CALL operations, data area operations), and copy directives
7. **Incremental source types**: Add RPG4, CL, DDS, DSPF, PRTF content schemas incrementally — each only requires a new content schema definition

### Expression AST Node Type Reference

| Node Type | When to Emit |
|---|---|
| `literal` | Numeric or string constants in factors |
| `identifier` | Field/variable names |
| `arrayElement` | Array references with index (e.g., `ARR,X`) |
| `binaryOp` | Compound expressions (rare in RPG3 column-based format) |
| `unaryOp` | Unary operations |
| `indicator` | Indicator references (`*IN01`, `*INLR`, etc.) |
| `figurativeConstant` | `*BLANKS`, `*ZEROS`, `*HIVAL`, `*LOVAL`, `*ALL`, `*ON`, `*OFF` |
| `specialValue` | `*IN`, `*INxx`, `UDATE`, `UMONTH`, `UDAY`, `UYEAR`, `PAGE`, etc. |

### Control Flow AST Node Reference

| Source Construct | IR Node Type | Key Fields |
|---|---|---|
| `IFxx` / `ELSE` / `END` | `conditionalBlock` | `condition`, `comparisonType`, `thenOps`, `elseOps` |
| `DOWxx` / `END` | `doWhileBlock` | `condition`, `comparisonType`, `bodyOps` |
| `DOUxx` / `END` | `doUntilBlock` | `condition`, `comparisonType`, `bodyOps` |
| `DO` / `END` | `doBlock` | `startValue`, `endValue`, `indexField`, `bodyOps` |
| `CASxx` / `END` | `caseBlock` | `cases`, `defaultSubroutine` |
| `BEGSR` / `ENDSR` | `subroutineBlock` | `name`, `operations` |
| `TAG` | `labelNode` | `name` |
| `GOTO` | `gotoNode` | `targetLabel` |
| `EXSR` | `callSubroutine` | `subroutineName` |

### Patterns & Best Practices

- **Ordered arrays**: All arrays must preserve source order — parsers must emit items in order of appearance
- **Optional fields**: Fields that may not apply to all instances should be omitted (not set to `null`) in the JSON output
- **Naming**: All field names use camelCase
- **Versioning**: The `irVersion` field in metadata enables schema evolution; use semver
- **Raw text fallback**: Every expression AST node includes `rawText` so downstream tools can fall back to raw parsing if the AST is insufficient
- **Location on every node**: Every significant node carries a `location` object for source-level traceability

## Integration Points

- **Parser output**: The IR JSON is the output contract for all language-specific parsers
- **Dependency analyzer**: Consumes the `dependencies` section from multiple IR documents to build system-wide graphs
- **Code generator**: Consumes the `content` section (especially the expression AST and control flow nodes in C-specs) to produce modern code
- **Visualization tools**: Consume both `content` and `dependencies` for display

## Error Handling

- **Parse errors**: Captured in `metadata.parseInfo.errors` with line, column, message, severity
- **Parse warnings**: Captured in `metadata.parseInfo.warnings`
- **Parse status**: `complete`, `partial`, `failed` — enables downstream tools to decide whether to process partial results
- **Unresolved copy members**: `copyMembers[].resolved = false` indicates the member could not be found

## Performance Considerations

- **Streaming**: Each IR document is independent — large systems can be processed in parallel
- **No circular references**: The IR structure is a tree, never a graph
- **Proportional sizing**: IR document size is proportional to source program size
- **Global indicator map**: Built as a post-parse pass to avoid runtime overhead during parsing
