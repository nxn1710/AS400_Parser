# AS400 Parser

A production-grade parser for IBM AS/400 source code. Converts fixed-format RPG III and DDS (PF/LF) source into structured IR (Intermediate Representation) JSON for modernization, migration, and analysis tools.

## Supported Source Types

| Source Type | Description | Extensions |
|-------------|-------------|------------|
| **RPG III** | Fixed-format RPG3 programs | `.rpg`, `.rpg3`, `.rpg38`, `.sqlrpg`, `.mbr`, `.cpy`, `.cpysrc` |
| **DDS PF** | Physical File definitions | `.pf` |
| **DDS LF** | Logical File definitions (simple, join, multi-format) | `.lf` |

## Features

### RPG III Parser
- **All 7 spec types**: H, F, E, L, I, C, O specs fully parsed
- **Expression AST**: 8 node types — identifier, literal, array element, binary op, unary op, indicator, figurative constant, special value
- **Control flow**: IF/ELSE, DOW, DOU, DO, CAS, BEGSR/ENDSR, TAG/GOTO, EXSR, ANDxx/ORxx compound conditions
- **Symbol table**: Cross-referenced from I-spec, E-spec, C-spec result fields, and data structures
- **Copy member resolution**: `/COPY` directive handling with configurable search paths
- **Compile-time data**: `**` separator and raw data block parsing

### DDS PF/LF Parser
- **Physical Files**: Record formats, field definitions (name, type, length, decimal), key definitions
- **Logical Files**: Simple, join, and multi-format LF types with auto-detection
- **Keywords**: TEXT, COLHDG, VALUES, DFT, COMP/CMP, RANGE, PFILE, JFILE, JOIN, JFLD, CONCAT, REFFLD, UNIQUE, DESCEND, and more
- **Select/Omit**: Comparison-based record selection and omission
- **Continuation lines**: Multi-line keyword areas with `+` continuation handling
- **Error recovery**: Partial parsing with per-line error collection

### Common
- **Japanese/CJK support**: DBCS-aware normalizer (EBCDIC CCSID 930/5035, Shift-JIS, EUC-JP, UTF-8)
- **Auto-detection**: Parser selection based on file extension
- **Unified CLI**: Single JAR handles both RPG3 and DDS files

## Quick Start

### Prerequisites

- Java 17+ JDK

### Build

```bash
# Build the fat JAR
./gradlew :parser-core:shadowJar

# The JAR is at:
# parser-core/build/libs/as400-parser-core-1.0.0-SNAPSHOT-all.jar
```

### Run Tests

```bash
# All tests
./gradlew :parser-core:test

# DDS tests only
./gradlew test --tests "com.as400parser.dds.*"

# RPG3 tests only
./gradlew test --tests "com.as400parser.rpg3.*"
```

## CLI Usage

The CLI auto-detects the parser based on file extension (`.rpg`/`.cpy` → RPG3, `.pf`/`.lf` → DDS).

```bash
# Show help
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --help
```

### Parse a Single File

```bash
# RPG3 source
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --source CUSTINQ.rpg -o CUSTINQ_ir.json

# DDS Physical File
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --source STUDNTPF.pf -o STUDNTPF_ir.json

# DDS Logical File
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --source STUDNTL2.lf -o STUDNTL2_ir.json
```

### Parse a Directory (Batch)

```bash
# Parse all source files recursively, preserving directory structure
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --source-dir ./as400-project --output-dir ./output
```

Output structure mirrors the source:
```
output/
  QRPGSRC/
    STUPRG.rpg.json
    STULST.rpg.json
  QCPYSRC/
    STUDNTCPY.cpy.json
  QDDSSRC/
    STUDNTPF.pf.json
    STUDNTL2.lf.json
```

### Options

| Option | Description |
|--------|-------------|
| `--source FILE` | Parse a single source file |
| `--source-dir DIR` | Parse all source files in a directory |
| `--output FILE` / `-o` | Write output to file (single mode, default: stdout) |
| `--output-dir DIR` | Write output files to directory (batch mode) |
| `--charset CHARSET` | Source encoding (default: `UTF-8`) |
| `--copy-path PATHS` | Semicolon-separated `/COPY` search paths (RPG3 only) |
| `--help` / `-h` | Show help |

**Supported extensions:** `.rpg`, `.rpg3`, `.rpg38`, `.sqlrpg`, `.mbr`, `.cpy`, `.cpysrc`, `.pf`, `.lf`

> **Auto-detection:** Parser is selected by file extension. For RPG3 files in a standard AS400 directory structure (e.g., `QRPGSRC/`), sibling `QCPYSRC` directories are automatically detected for `/COPY` member resolution.

## Python CLI (Wrapper)

The Python CLI wraps the Java JAR with extra features like parallel batch processing and IR JSON validation.

```bash
# Parse a single file (RPG3 or DDS)
python cli/as400_parser_cli.py parse STUDNTPF.pf -o output.json

# Batch parse a project directory (recursive, preserves structure)
python cli/as400_parser_cli.py batch ./as400-project -o ./output --parallel 4

# Validate an IR JSON file
python cli/as400_parser_cli.py validate output.json
```

### Python CLI Commands

| Command | Description |
|---------|-------------|
| `parse SOURCE [-o OUTPUT]` | Parse a single file |
| `batch SOURCE_DIR [-o DIR] [--parallel N]` | Batch parse with parallelism |
| `validate JSON_FILE` | Validate IR JSON structure |

> **Note:** Requires Python 3.8+ and `java` on PATH. Build the JAR first with `./gradlew :parser-core:shadowJar`.

## Java API Usage

### RPG III

```java
import com.as400parser.rpg3.Rpg3ParserFacade;
import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.ParseOptions;

Rpg3ParserFacade parser = new Rpg3ParserFacade();
IrDocument doc = parser.parse(Path.of("CUSTINQ.rpg"), ParseOptions.defaults());
```

### DDS (PF/LF)

```java
import com.as400parser.dds.DdsParserFacade;
import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.ParseOptions;

DdsParserFacade parser = new DdsParserFacade();
IrDocument doc = parser.parse(Path.of("STUDNTPF.pf"), ParseOptions.defaults());
```

### Auto-detect Parser

```java
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.cli.As400ParserCli;

// As400ParserCli internally selects the right parser by extension
// For programmatic auto-detection:
Path source = Path.of("STUDNTPF.pf");
String ext = source.toString().toLowerCase();
As400Parser parser;
if (ext.endsWith(".pf") || ext.endsWith(".lf")) {
    parser = new DdsParserFacade();
} else {
    parser = new Rpg3ParserFacade();
}
IrDocument doc = parser.parse(source, ParseOptions.defaults());
```

### Serialize to JSON

```java
import com.as400parser.common.serializer.IrJsonSerializer;

IrJsonSerializer serializer = new IrJsonSerializer();
String json = serializer.serialize(doc);
```

## IR JSON Output Structure

### RPG3

```json
{
  "metadata": {
    "irVersion": "1.0.0",
    "sourceType": "RPG3",
    "sourceMember": "CUSTINQ",
    "parseInfo": { "parseStatus": "complete", "totalLines": 42 }
  },
  "content": {
    "sourceLines": [...],
    "headerSpecs": [...],
    "fileSpecs": [...],
    "extensionSpecs": [...],
    "inputSpecs": [...],
    "dataStructures": [...],
    "calculationSpecs": [...],
    "outputSpecs": [...],
    "subroutines": [...],
    "symbolTable": [...],
    "comments": [...]
  },
  "dependencies": {
    "referencedFiles": [...],
    "calledPrograms": [...],
    "copyMembers": [...]
  }
}
```

### DDS PF

```json
{
  "metadata": {
    "irVersion": "1.0.0",
    "sourceType": "DDS_PF",
    "sourceMember": "STUDNTPF"
  },
  "content": {
    "sourceLines": [...],
    "fileKeywords": [{ "name": "UNIQUE" }],
    "recordFormats": [{
      "name": "STUREC",
      "text": "学生マスターレコード",
      "fields": [{
        "name": "STUID", "length": 6, "dataType": "A",
        "keywords": [
          { "name": "TEXT", "value": "学生ID" },
          { "name": "COLHDG", "values": ["学生", "ID"] }
        ]
      }],
      "keys": [{ "fieldName": "STUSCL" }, { "fieldName": "STUID" }]
    }],
    "comments": [...]
  },
  "dependencies": { "referencedFiles": [] }
}
```

### DDS LF

```json
{
  "metadata": {
    "sourceType": "DDS_LF",
    "sourceMember": "STUDNTL2"
  },
  "content": {
    "fileKeywords": [],
    "recordFormats": [{
      "name": "STUREC",
      "pfile": "STUDNTPF",
      "lfType": "simple",
      "selectOmit": [{
        "type": "select", "fieldName": "STUSTS",
        "keywords": [{ "name": "COMP", "comparisonOperator": "EQ", "comparisonValue": "A" }]
      }],
      "keys": [{ "fieldName": "STUSCL" }, { "fieldName": "STUID" }]
    }],
    "comments": [...]
  },
  "dependencies": {
    "referencedFiles": [{ "name": "STUDNTPF", "type": "PFILE" }]
  }
}
```

## Project Structure

```
AS400_Parser/
├── parser-core/                   # Java parser library
│   ├── build.gradle
│   └── src/
│       ├── main/java/com/as400parser/
│       │   ├── common/            # Shared framework
│       │   │   ├── cli/           # As400ParserCli (unified entry point)
│       │   │   ├── normalizer/    # Source text normalization
│       │   │   ├── model/         # IR data model (IrDocument, SourceLine, etc.)
│       │   │   ├── parser/        # Base parser interface (As400Parser)
│       │   │   └── serializer/    # JSON serialization
│       │   ├── rpg3/              # RPG III parser
│       │   │   ├── Rpg3ParserFacade.java
│       │   │   ├── Rpg3IrBuilder.java
│       │   │   ├── Rpg3SymbolTableBuilder.java
│       │   │   ├── Rpg3CopyResolver.java
│       │   │   ├── ExpressionBuilder.java
│       │   │   └── model/         # RPG3 IR models
│       │   └── dds/               # DDS PF/LF parser
│       │       ├── DdsParserFacade.java
│       │       ├── DdsIrBuilder.java
│       │       ├── DdsLineClassifier.java
│       │       ├── DdsKeywordParser.java
│       │       └── model/         # DDS IR models (11 classes)
│       └── test/java/             # Unit + integration tests
├── grammar/rpg3/                  # ANTLR grammar (reference)
├── cli/                           # Python CLI wrapper (as400_parser_cli.py)
├── rpg3-student-mgmt/             # Sample source files
│   ├── QRPGSRC/                   # RPG3 programs
│   ├── QDDSSRC/                   # DDS PF/LF definitions
│   └── QCPYSRC/                   # Copy members
├── output/example-ir/             # Example IR JSON outputs
└── docs/ai/                       # AI-assisted development docs
```

## Documentation

| Document | RPG3 | DDS PF/LF |
|----------|------|-----------|
| Requirements | `docs/ai/requirements/feature-rpg3-parser.md` | `docs/ai/requirements/feature-pf-lf-parser.md` |
| Design | `docs/ai/design/feature-rpg3-parser.md` | `docs/ai/design/feature-pf-lf-parser.md` |
| Planning | `docs/ai/planning/feature-rpg3-parser.md` | `docs/ai/planning/feature-pf-lf-parser.md` |
| Implementation | `docs/ai/implementation/feature-rpg3-parser.md` | `docs/ai/implementation/feature-pf-lf-parser.md` |
| Testing | `docs/ai/testing/feature-rpg3-parser.md` | `docs/ai/testing/feature-pf-lf-parser.md` |
| IR JSON Template | `docs/ai/design/feature-ir-json-template.md` | — |

## License

TBD
