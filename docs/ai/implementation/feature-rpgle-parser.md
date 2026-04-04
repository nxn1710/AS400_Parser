---
phase: implementation
title: Implementation Guide
description: Technical implementation notes, patterns, and code guidelines
---

# Implementation Guide

## Development Setup
**How do we get started?**
- **Java Setup:** Add ANTLR4 dependency to `parser-core/build.gradle` and configure grammar source set for `grammar/rpgle/`.
- **Python Hook:** Update `as400_parser_cli.py` to add `.rpgle` and `.sqlrpgle` to `SUPPORTED_EXTENSIONS`.
- **Java CLI:** Register `RpgleParserFacade` in `As400ParserCli.PARSERS` list.

## Code Structure
**How is the code organized?**
- `com.as400parser.rpgle` (new package)
  - `RpgleParserFacade.java`: Main facade implementing `As400Parser`. Detects format (`**FREE`) and delegates.
  - `RpgleFixedParser.java`: Core processor for fixed-format specification logic.
  - `RpgleFreeParser.java`: Invokes the ANTLR pipeline using existing grammars.
  - `RpgleIrBuilder.java`: Responsible for assembling `IrDocument` from parsed specs.
- `com.as400parser.rpgle.model` (new sub-package)
  - `RpgleContent.java`: RPGLE-specific content model (set on `IrDocument.setContent()`).
  - Additional model classes for each spec type's parsed data.
- `grammar/rpgle/` (existing — DO NOT MODIFY)
  - `RpgLexer.g4` and `RpgParser.g4`: Existing ANTLR grammatical specifications.
- `com.as400parser.common` (existing — REUSE, DO NOT DUPLICATE)
  - `parser/As400Parser.java`: Interface that `RpgleParserFacade` implements.
  - `parser/ParseOptions.java`: Configuration passed to `parse()` methods.
  - `model/IrDocument.java`: Top-level return type with content, metadata, dependencies, errors.
  - `model/Metadata.java`: Source type, member, library, parse info.
  - `model/ParseError.java`: Error/warning entries with severity levels.
  - `model/Location.java`: Line/column tracking for source positions.
  - `model/SourceLine.java`: Raw source line wrapper.
  - `normalizer/SourceNormalizer.java`: Encoding detection and source normalization.
  - `normalizer/NormalizedSource.java`: Normalized lines + sequence numbers.
  - `serializer/IrJsonSerializer.java`: Serialize `IrDocument` → JSON (extend with RPGLE adapters).
  - `cli/As400ParserCli.java`: Register in `PARSERS` list for auto-dispatch.

## Implementation Notes
**Key technical details to remember:**

### Facade Pattern (follow `ClParserFacade`)
The `RpgleParserFacade` must follow the identical pipeline pattern used by `ClParserFacade`:
```java
// 1. Normalize source
SourceNormalizer normalizer = new SourceNormalizer(new int[0], false);
NormalizedSource normalized = (options.getCharset() != null)
    ? normalizer.normalize(sourceFile, options.getCharset())
    : normalizer.normalize(sourceFile);

// 2. Run parsing pipeline (detect format → parse → build content)
IrDocument doc = runPipeline(normalized, sourceType);

// 3. Populate file-level metadata
populateMetadataFromFile(doc, sourceFile, sourceType);

// 4. Record detected encoding
if (normalized.getDetectedCharset() != null) {
    doc.getMetadata().getParseInfo()
        .setDetectedEncoding(normalized.getDetectedCharset().name());
}
```

### Positional Rules "Know-How" (RPG IV / RPGLE)
When parsing fixed-format lines via `.substring()`, remember RPGLE is a 1-indexed structured format with the following core offsets:
- **Index 6**: Form Type (`H`, `F`, `D`, `I`, `C`, `O`, `P`)
- **Index 7**: Indicates comments (`*`) or Control Levels.

**Definition Specifications (D-Specs):**
  - Columns 7-21: Name
  - Column 22: External Description
  - Column 23: Type of Data Structure
  - Columns 24-25: Definition Type
  - Columns 26-32: From Position
  - Columns 33-39: To Position / Length
  - Column 40: Internal Data Type
  - Columns 41-42: Decimal Positions
  - Columns 44-80: Keywords

**Calculation Specifications (C-Specs):**
  - Columns 7-8: Control Level
  - Columns 9-11: Indicators
  - Columns 12-25: Factor 1
  - Columns 26-35: Operation Code
  - Columns 36-49 (or 36-80 in Extended Factor 2): Factor 2
  - Columns 50-63: Result Field
  - Columns 64-68: Field Length
  - Columns 69-70: Decimal Positions
  - Columns 71-76: Resulting Indicators

**File Description Specifications (F-Specs):**
  - Columns 7-16: File Name
  - Column 17: File Type (I, O, U, C)
  - Column 18: File Designation
  - Columns 23-27: Record Length
  - Columns 36-42: Device Type
  - Columns 44-80: Keywords

### Parsing Execution Logic
- **Fixed Format (`RpgleFixedParser`):** **DO NOT USE ANTLR**. Parses specs by extracting strict column character subsets using `.substring()`. This must securely evaluate line signatures for all 7 specification arrays documented in the ILE RPG reference: Control (H), File (F), Definition (D), Input (I), Calculation (C), Output (O), and Procedure (P).
- **Free Format (`RpgleFreeParser`):** Exclusively leverages the existing ANTLR grammars (`grammar/rpgle/RpgLexer.g4` and `RpgParser.g4`). Lexer rules tokenize the operations (e.g. `CHAIN`, `READ`) and Built-In Functions (e.g. `%LEN`) as dictated by the IBM knowledge documentation.
- **Mixed Format Logic:** Handled directly in `RpgleFixedParser`. It evaluates line-by-line. If a fixed line is inside a `/FREE` and `/END-FREE` wrapper block, that block of lines is diverted out to `RpgleFreeParser` (ANTLR). 

### Patterns & Best Practices
- **Strict Data Mapping:** Ensure JSON output mapping strictly uses `null` for unknown/missing states, and `""` purely for intentional empty spaces.
- **Extensibility:** Free-format statements evaluate into uniform AST calculation nodes that align seamlessly with what the positional parsing tree outputs to the `RpgleIrBuilder`.
- **Reuse common models:** Always use `Location`, `ParseError`, `IrDocument.DependencyRef` from common — never create RPGLE-specific duplicates.

## Integration Points
**How do pieces connect?**
- `As400ParserCli.selectParser()` auto-dispatches `.rpgle`/`.sqlrpgle` files to `RpgleParserFacade`.
- `IrJsonSerializer.serialize(IrDocument)` handles the final JSON output.
- Python CLI invokes the fat JAR (`as400-parser-core-all.jar`) via subprocess — no changes needed to the subprocess mechanism.

## Error Handling
**How do we handle failures?**
- ANTLR `RecognitionException` → catch and emit `ParseError` objects with `Severity.WARNING`.
- Positional index-out-of-bounds → default properties to `null`.
- IOException during file read → return failed `IrDocument` with `parseStatus = "failed"` (same pattern as `ClParserFacade.createFailedDocument()`).

## Performance Considerations
**How do we keep it fast?**
- Favor `String.substring()` over regex evaluation when executing the high-velocity positional bounds in the fixed-format parser.
- Limit ANTLR parser initialization overhead by reusing the environment paths where globally possible.
