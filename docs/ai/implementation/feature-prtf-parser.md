---
phase: implementation
title: "Implementation Guide: PRTF Parser"
description: Technical implementation guidance, phased task breakdown, and coding notes for the Printer File DDS parser
---

# Implementation Guide — PRTF Parser

## Document References

| Document | Path |
|---|---|
| Requirements | [feature-prtf-parser.md](file:///d:/Code/AS400_Parser/docs/ai/requirements/feature-prtf-parser.md) |
| Design | [feature-prtf-parser.md](file:///d:/Code/AS400_Parser/docs/ai/design/feature-prtf-parser.md) |
| Planning | [feature-prtf-parser.md](file:///d:/Code/AS400_Parser/docs/ai/planning/feature-prtf-parser.md) |
| PF/LF Parser (reference) | [DdsParserFacade.java](file:///d:/Code/AS400_Parser/parser-core/src/main/java/com/as400parser/dds/DdsParserFacade.java) |
| DSPF Parser (primary reference) | [DspfIrBuilder.java](file:///d:/Code/AS400_Parser/parser-core/src/main/java/com/as400parser/dspf/DspfIrBuilder.java) |
| Implementation | `docs/ai/implementation/feature-prtf-parser.md` ← this file |

---

## Development Setup

**Prerequisites:**
- Java 17+ JDK (matches existing project)
- Gradle 8+ (existing build)
- No new dependencies required

**New package:**
```
parser-core/src/main/java/com/as400parser/prtf/
```

---

## Source Layout

**New PRTF package to create:**

```
parser-core/src/main/java/com/as400parser/prtf/
├── PrtfParserFacade.java        # Phase 3 — implements As400Parser
├── PrtfIrBuilder.java           # Phase 2 — build PRTF IR from normalized lines
└── model/
    ├── PrtfContent.java          # Phase 1 — top-level content
    ├── PrtfRecordFormat.java     # Phase 1 — record format
    ├── PrtfFieldDefinition.java  # Phase 1 — field with print coords
    └── PrtfConstant.java         # Phase 1 — print constant/label or system keyword

parser-core/src/test/java/com/as400parser/prtf/
├── PrtfIrBuilderTest.java        # Phase 4
└── PrtfIntegrationTest.java      # Phase 4
```

**Reused from existing packages:**

| Component | Package | Notes |
|---|---|---|
| `SourceNormalizer` | `common.normalizer` | 80-char padding |
| `IrDocument`, `Metadata`, `Location` | `common.model` | Same envelope |
| `IrJsonSerializer` | `common.serializer` | Serializes any IrDocument |
| `As400Parser`, `ParseOptions` | `common.parser` | Interface to implement |
| `DdsKeywordParser` | `dds` | Same keyword syntax |
| `DdsKeyword` | `dds.model` | Same keyword representation |
| `KeyDefinition` | `dds.model` | For key specs |
| `DdsComment` | `dds.model` | `{lineNumber, text}` structure |
| `SourceLine` | `common.model` | Raw source line model |
| `ConditioningIndicator` | `dspf.model` | Conditioning from cols 8-16 |
| `ConditionedKeyword` | `dspf.model` | Keyword with conditioning |

---

## Phase Implementation Notes

### Phase 1: Model Classes

Mirror DSPF model structure with these differences:
- **`PrtfFieldDefinition`**: Use `printLine`/`printPosition` instead of `screenLine`/`screenPosition`
- **`PrtfRecordFormat`**: No `recordType`/`sflControlFor` (no subfile concept)
- **`PrtfConstant`**: Use `printLine`/`printPosition` instead of screen coords

### Phase 2: IR Builder

Follow `DspfIrBuilder` as primary template with simplifications:
- **No subfile detection** — all records are normal type
- **No conditioned keyword merging complexity** — PRTF uses conditioning less than DSPF, but still support it
- **No usage column logic** — col 38 typically blank, capture if present
- **Same `extractColumn` utility** approach for positional parsing
- **Same continuation handling** (`+` at col 80)
- **Same constant detection** (unnamed lines with coordinates)
- **Same `populateFieldReference`** for REFFLD handling

### Phase 3: Facade & CLI Integration

Follow `DspfParserFacade` pattern exactly:
1. Normalize source via `SourceNormalizer`
2. Build content via `PrtfIrBuilder.buildContent(lines, sequenceNumbers)`
3. Create `IrDocument` with content, dependencies, metadata
4. Register `.prtf` extension in `As400ParserCli`

### Phase 4: Testing

**Sample PRTF file creation:**
Create representative `.prtf` files with:
- Page header record with `SKIPB(1)` / `SPACEA(2)`
- Detail record with fields at various print positions
- Page footer with `DATE`, `TIME`, `PAGNBR` system keywords
- Fields with `EDTCDE(Y)`, `FONT`, `UNDERLINE`
- Constants with quoted text
- Conditioning indicators
- Continuation lines
- REFFLD references

---

## Error Handling

- Same approach as DSPF: collect `ParseError` objects, continue parsing
- No fatal errors — best-effort parsing for all lines
- Line/column locations on all error messages

## Performance Considerations

- Same single-pass approach as DSPF — O(n) in lines
- Target: < 100ms per file
- No additional memory overhead vs DSPF
