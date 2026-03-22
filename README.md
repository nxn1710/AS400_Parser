# AS400 Parser

A production-grade parser for IBM AS/400 source code. Converts fixed-format RPG III and DDS (PF/LF/DSPF/PRTF) source into structured IR (Intermediate Representation) JSON for modernization, migration, and analysis tools.

## Supported Source Types

| Source Type | Description | Extensions |
|-------------|-------------|------------|
| **RPG III** | Fixed-format RPG3 programs | `.rpg`, `.rpg3`, `.rpg38`, `.sqlrpg`, `.mbr`, `.cpy`, `.cpysrc` |
| **DDS PF** | Physical File definitions | `.pf` |
| **DDS LF** | Logical File definitions (simple, join, multi-format) | `.lf` |
| **DDS DSPF** | Display File definitions (screens/panels) | `.dspf` |
| **DDS PRTF** | Printer File definitions (reports) | `.prtf` |

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

### DSPF Parser
- **Record formats**: Normal, subfile (SFL), and subfile control (SFLCTL) with auto-detection
- **Fields**: Named fields with usage (I/O/B), screen coordinates, data type, conditioning indicators
- **Constants**: Quoted text, system keywords (DATE, TIME, SYSNAME, USER, PAGNBR)
- **Conditioned keywords**: Indicator-conditioned display attributes (DSPATR, COLOR, etc.)
- **Keywords**: DSPSIZ, CA/CF function keys, SFL, SFLCTL, SFLPAG, SFLSIZ, SFLDSP, SFLDSPCTL, OVERLAY, REFFLD, and more
- **Continuation lines**: `+` at col 80 with merged keyword parsing
- **Reference fields**: REFFLD cross-file resolution in batch mode

### PRTF Parser
- **Record formats**: Print record definitions with spacing/skipping keywords
- **Fields**: Named fields with print line/position coordinates, edit codes/words
- **Constants**: Quoted text constants with print positioning
- **Keywords**: SPACEA, SPACEB, SKIPA, SKIPB, EDTCDE, EDTWRD, UNDERLINE, DSPATR, and more
- **Continuation lines**: `+` at col 80 with merged keyword parsing

### Common
- **Japanese/CJK support**: DBCS-aware normalizer (EBCDIC CCSID 930/5035, Shift-JIS, EUC-JP, UTF-8)
- **Auto-encoding detection**: Automatic charset detection or explicit `--charset` option
- **Auto-detection**: Parser selection based on file extension
- **Unified CLI**: Single JAR handles all source types
- **REFFLD resolution**: Cross-file field reference resolution in batch mode

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

# DSPF tests only
./gradlew test --tests "com.as400parser.dspf.*"

# PRTF tests only
./gradlew test --tests "com.as400parser.prtf.*"
```

## CLI Usage

The CLI auto-detects the parser based on file extension.

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

# DSPF Display File
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --source STUDSPF.dspf -o STUDSPF_ir.json

# PRTF Printer File
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --source STURPTPF.prtf -o STURPTPF_ir.json
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
    STUDSPF.dspf.json
    STURPTPF.prtf.json
```

### Options

| Option | Description |
|--------|-------------|
| `--source FILE` | Parse a single source file |
| `--source-dir DIR` | Parse all source files in a directory |
| `--output FILE` / `-o` | Write output to file (single mode, default: stdout) |
| `--output-dir DIR` | Write output files to directory (batch mode) |
| `--charset CHARSET` | Source encoding (default: `auto` for auto-detect) |
| `--copy-path PATHS` | Semicolon-separated `/COPY` search paths (RPG3 only) |
| `--help` / `-h` | Show help |

**Supported extensions:** `.rpg`, `.rpg3`, `.rpg38`, `.sqlrpg`, `.mbr`, `.cpy`, `.cpysrc`, `.pf`, `.lf`, `.dspf`, `.prtf`

> **Auto-detection:** Parser is selected by file extension. For RPG3 files in a standard AS400 directory structure (e.g., `QRPGSRC/`), sibling `QCPYSRC` directories are automatically detected for `/COPY` member resolution.

## Python CLI (Wrapper)

The Python CLI wraps the Java JAR with extra features like parallel batch processing and IR JSON validation.

```bash
# Parse a single file
python cli/as400_parser_cli.py parse STUDSPF.dspf -o output.json

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

### DSPF

```java
import com.as400parser.dspf.DspfParserFacade;
import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.ParseOptions;

DspfParserFacade parser = new DspfParserFacade();
IrDocument doc = parser.parse(Path.of("STUDSPF.dspf"), ParseOptions.defaults());
```

### PRTF

```java
import com.as400parser.prtf.PrtfParserFacade;
import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.ParseOptions;

PrtfParserFacade parser = new PrtfParserFacade();
IrDocument doc = parser.parse(Path.of("STURPTPF.prtf"), ParseOptions.defaults());
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

### DSPF

```json
{
  "metadata": {
    "sourceType": "DSPF",
    "sourceMember": "STUDSPF"
  },
  "content": {
    "fileKeywords": [
      { "name": "DSPSIZ", "values": ["24", "80", "*DS3"] },
      { "name": "CA03", "value": "03 '終了'" }
    ],
    "recordFormats": [{
      "name": "STUSRCH",
      "recordType": "normal",
      "fields": [{
        "name": "SCSCL", "length": 4, "dataType": "A", "usage": "B",
        "screenLine": 6, "screenPosition": 20,
        "keywords": [{ "keyword": { "name": "DSPATR", "value": "UL" } }]
      }],
      "constants": [{
        "screenLine": 1, "screenPosition": 25,
        "text": "学 生 メ ン テ ナ ン ス",
        "keywords": [{ "keyword": { "name": "DSPATR", "value": "HI" } }]
      }]
    }]
  },
  "dependencies": { "referencedFiles": [] }
}
```

### PRTF

```json
{
  "metadata": {
    "sourceType": "PRTF",
    "sourceMember": "STURPTPF"
  },
  "content": {
    "fileKeywords": [],
    "recordFormats": [{
      "name": "TITLE",
      "keywords": [{ "name": "SPACEB", "value": "3" }],
      "fields": [{
        "name": "RPDATE", "length": 8, "dataType": "S", "decimalPositions": 0,
        "printLine": 1, "printPosition": 1,
        "keywords": [{ "keyword": { "name": "EDTCDE", "value": "Y" } }]
      }],
      "constants": [{
        "printLine": 1, "printPosition": 30,
        "text": "学 生 名 簿 一 覧 表",
        "keywords": [{ "keyword": { "name": "SPACEA", "value": "1" } }]
      }]
    }]
  },
  "dependencies": { "referencedFiles": [] }
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
│       │   │   ├── normalizer/    # Source text normalization (DBCS-aware)
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
│       │   ├── dds/               # DDS PF/LF parser
│       │   │   ├── DdsParserFacade.java
│       │   │   ├── DdsIrBuilder.java
│       │   │   ├── DdsLineClassifier.java
│       │   │   ├── DdsKeywordParser.java
│       │   │   └── model/         # DDS IR models
│       │   ├── dspf/              # DSPF parser
│       │   │   ├── DspfParserFacade.java
│       │   │   ├── DspfIrBuilder.java
│       │   │   └── model/         # DSPF IR models (6 classes)
│       │   └── prtf/              # PRTF parser
│       │       ├── PrtfParserFacade.java
│       │       ├── PrtfIrBuilder.java
│       │       └── model/         # PRTF IR models (4 classes)
│       └── test/java/             # Unit + integration tests
├── grammar/rpg3/                  # ANTLR grammar (reference)
├── cli/                           # Python CLI wrapper (as400_parser_cli.py)
├── rpg3-student-mgmt/             # Sample source files
│   ├── QRPGSRC/                   # RPG3 programs
│   ├── QDDSSRC/                   # DDS PF/LF/DSPF/PRTF definitions
│   └── QCPYSRC/                   # Copy members
├── output/                        # Parsed IR JSON outputs
└── docs/ai/                       # AI-assisted development docs
```

## Documentation

| Document | RPG3 | DDS PF/LF | DSPF | PRTF |
|----------|------|-----------|------|------|
| Requirements | `feature-rpg3-parser.md` | `feature-pf-lf-parser.md` | `feature-dspf-parser.md` | `feature-prtf-parser.md` |
| Design | `feature-rpg3-parser.md` | `feature-pf-lf-parser.md` | `feature-dspf-parser.md` | `feature-prtf-parser.md` |
| Planning | `feature-rpg3-parser.md` | `feature-pf-lf-parser.md` | `feature-dspf-parser.md` | `feature-prtf-parser.md` |
| Implementation | `feature-rpg3-parser.md` | `feature-pf-lf-parser.md` | `feature-dspf-parser.md` | `feature-prtf-parser.md` |
| Testing | `feature-rpg3-parser.md` | `feature-pf-lf-parser.md` | `feature-dspf-parser.md` | `feature-prtf-parser.md` |

All docs are in `docs/ai/{phase}/` directories.

## License

This project is licensed under the [MIT License](LICENSE).
