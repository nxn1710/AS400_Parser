---
title: RPG3 Parser Key Conventions & Knowledge
description: Critical project conventions and patterns for the RPG3 parser. Reference this doc before implementing any parser component.
tags: rpg3, parser, conventions, migration, ir-json
scope: project:AS400_Parser
---

# RPG3 Parser — Key Conventions & Knowledge

## 1. Zero-Loss Parse Strategy
**Tags:** error-handling, migration, parseQuality

Every column in every source line MUST be captured in the IR. A silently skipped line means missing business logic → bugs during migration.

**Three-tier fallback:**
- **Tier 1** (`parseQuality: "full"`): Normal ANTLR parse → full expression AST
- **Tier 2** (`parseQuality: "columnOnly"`): ANTLR fails, form type recognized → raw column values extracted by position
- **Tier 3** (`parseQuality: "raw"`): Even column extraction fails → `unparsedSpec` node with raw text + column map

**Rule:** Downstream migration tools MUST check `parseQuality`. Flag non-`"full"` nodes for human review.

---

## 2. Expression Detection Order
**Tags:** expression-ast, parsing, bug-prevention

Order matters. Checking `*IN` before figurative constants causes `*IN` (indicator array) to be misclassified.

**Correct order:**
1. blank/empty → `null`
2. `*BLANKS/*ZEROS/*HIVAL/*LOVAL/*ALL/*ON/*OFF` → `FigurativeConstantNode`
3. `*INxx` (length ≥ 4: `*IN01`, `*INLR`, `*INH1`) → `IndicatorNode`
4. `*IN` (exactly 3 chars) → `SpecialValueNode` (the indicator array)
5. `UDATE/UMONTH/UDAY/UYEAR/PAGE` → `SpecialValueNode`
6. Quoted string → `LiteralNode`
7. Numeric → `LiteralNode`
8. Contains `,` (like `ARR,X`) → `ArrayElementNode`
9. Otherwise → `IdentifierNode`

---

## 3. C-Spec Column Layout
**Tags:** column-positions, c-spec, extraction

| Cols | Field | IR Field |
|---|---|---|
| 6 | Form Type | `formType` |
| 7–8 | Control Level | `controlLevel` |
| 9–11 | Conditioning Indicator 1 | `conditioningIndicators[0]` |
| 12–14 | Conditioning Indicator 2 | `conditioningIndicators[1]` |
| 15–17 | Conditioning Indicator 3 | `conditioningIndicators[2]` |
| 18–27 | Factor 1 | `factor1` |
| 28–32 | Operation Code | `opcode` |
| 33–42 | Factor 2 | `factor2` |
| 43–48 | Result Field | `resultField` |
| 49–51 | Field Length | `fieldLength` |
| 52 | Decimal Positions | `decimalPositions` |
| 53 | Half Adjust / Op Extender | `extendedOpcode` (H/N/P) |
| 54–55 | Resulting Indicator High | `resultingIndicators.high` |
| 56–57 | Resulting Indicator Low | `resultingIndicators.low` |
| 58–59 | Resulting Indicator Equal | `resultingIndicators.equal` |
| 60–74 | Comments | `inlineComment` |

---

## 4. ANDxx/ORxx Compound Conditions
**Tags:** control-flow, conditions, antlr

`ANDxx`/`ORxx` extend the condition of a preceding `IFxx`/`DOWxx`/`DOUxx`.

**Algorithm:**
1. `IFxx` → create `BinaryOpNode(operator=xx, left=factor1, right=factor2)`
2. `ANDxx` follows → wrap: `BinaryOpNode("AND", prevCondition, BinaryOpNode(xx, f1, f2))`
3. `ORxx` follows → wrap: `BinaryOpNode("OR", prevCondition, BinaryOpNode(xx, f1, f2))`
4. Non-AND/OR line → condition finalized

When compound conditions are used, `comparisonType` and `comparisonValue` are set to `null` — the full condition lives in the `condition` expression tree.

---

## 5. IR JSON Null Conventions
**Tags:** ir-json, serialization, contract

| Value | Meaning |
|---|---|
| `null` | Field not applicable / not present in source |
| `""` (empty string) | Field exists but is blank |
| `0` | Numeric zero (meaningful value) |
| `[]` | Empty array (never use null for arrays) |

Gson config: `serializeNulls()`, `disableHtmlEscaping()`, `setPrettyPrinting()`

---

## 6. Document Locations
**Tags:** project-structure, docs

| Document | Path |
|---|---|
| Requirements | `docs/ai/requirements/feature-rpg3-parser.md` |
| Design | `docs/ai/design/feature-rpg3-parser.md` |
| IR JSON Template | `docs/ai/design/feature-ir-json-template.md` |
| Implementation | `docs/ai/implementation/feature-rpg3-parser.md` |
| Planning | `docs/ai/planning/feature-rpg3-parser.md` |
| Testing | `docs/ai/testing/feature-rpg3-parser.md` |
| Sample IR | `example/ir/rpg3.json` |
| RPGLE Grammar | `grammar/rpgle/RpgLexer.g4`, `grammar/rpgle/RpgParser.g4` |

**IR JSON Template is the strict contract.** Any changes to the IR output schema require updating the template doc first.
