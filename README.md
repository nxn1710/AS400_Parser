# AS400 RPG3 Parser

A production-grade parser for IBM AS/400 RPG III source code. Converts fixed-format RPG3 source into structured IR (Intermediate Representation) JSON for modernization, migration, and analysis tools.

## Features

- **All 7 RPG3 spec types**: H, F, E, L, I, C, O specs fully parsed
- **Expression AST**: 8 node types — identifier, literal, array element, binary op, unary op, indicator, figurative constant, special value
- **Control flow**: IF/ELSE, DOW, DOU, DO, CAS, BEGSR/ENDSR, TAG/GOTO, EXSR, ANDxx/ORxx compound conditions
- **Symbol table**: Cross-referenced from I-spec, E-spec, C-spec result fields, and data structures
- **Copy member resolution**: `/COPY` directive handling with configurable search paths
- **Compile-time data**: `**` separator and raw data block parsing
- **Japanese/CJK support**: DBCS-aware normalizer (EBCDIC CCSID 930/5035, Shift-JIS, EUC-JP, UTF-8)
- **213 tests**, 0 failures

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
./gradlew :parser-core:test
```

## CLI Usage

```bash
# Show help
java -jar parser-core/build/libs/as400-parser-core-1.0.0-SNAPSHOT-all.jar --help
```

### Parse a Single File

```bash
# Output to stdout
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --source CUSTINQ.rpg

# Output to file
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --source CUSTINQ.rpg -o CUSTINQ_ir.json
```

### Parse a Directory (Batch)

```bash
# Parse all RPG3 files recursively, preserving directory structure
java -jar as400-parser-core-1.0.0-SNAPSHOT-all.jar --source-dir ./rpg3-project --output-dir ./output
```

Output structure mirrors the source:
```
output/
  QRPGSRC/
    STUPRG.rpg.json
    STULST.rpg.json
  QCPYSRC/
    STUDNTCPY.cpy.json
    SCHOOLCPY.cpy.json
```

### Options

| Option | Description |
|--------|-------------|
| `--source FILE` | Parse a single RPG3 source file |
| `--source-dir DIR` | Parse all RPG3 files in a directory |
| `--output FILE` / `-o` | Write output to file (single mode, default: stdout) |
| `--output-dir DIR` | Write output files to directory (batch mode) |
| `--charset CHARSET` | Source encoding (default: `UTF-8`) |
| `--copy-path PATHS` | Semicolon-separated `/COPY` search paths |
| `--help` / `-h` | Show help |

**Supported extensions:** `.rpg`, `.rpg3`, `.rpg38`, `.sqlrpg`, `.mbr`, `.cpy`, `.cpysrc`

> **Auto-detection:** When parsing a file from a standard AS400 directory structure (e.g., `QRPGSRC/`), sibling `QCPYSRC` directories are automatically detected for `/COPY` member resolution — no `--copy-path` needed.

## Python CLI (Wrapper)

The Python CLI wraps the Java JAR with extra features like parallel batch processing and IR JSON validation.

```bash
# Parse a single file
python cli/rpg3_parser_cli.py parse CUSTINQ.rpg -o output.json

# Batch parse a project directory (recursive, preserves structure)
python cli/rpg3_parser_cli.py batch ./rpg3-project -o ./output --parallel 4

# Validate an IR JSON file
python cli/rpg3_parser_cli.py validate output.json
```

### Python CLI Commands

| Command | Description |
|---------|-------------|
| `parse SOURCE [-o OUTPUT]` | Parse a single file |
| `batch SOURCE_DIR [-o DIR] [--parallel N]` | Batch parse with parallelism |
| `validate JSON_FILE` | Validate IR JSON structure |

> **Note:** Requires Python 3.8+ and `java` on PATH. Build the JAR first with `./gradlew :parser-core:shadowJar`.

## Java API Usage

```java
import com.as400parser.rpg3.Rpg3ParserFacade;
import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.common.serializer.IrJsonSerializer;

import java.nio.file.Path;

// Parse a file
Rpg3ParserFacade parser = new Rpg3ParserFacade();
IrDocument doc = parser.parse(Path.of("CUSTINQ.rpg"), ParseOptions.defaults());

// Serialize to JSON
IrJsonSerializer serializer = new IrJsonSerializer();
String json = serializer.serialize(doc);

// Parse from string
IrDocument doc2 = parser.parse(sourceText, ParseOptions.defaults());
```

### With Copy Member Resolution

```java
ParseOptions options = ParseOptions.builder()
    .resolveCopies(true)
    .copyPaths(List.of("./QCPYSRC", "./QRPGSRC"))
    .build();

IrDocument doc = parser.parse(Path.of("PROGRAM.rpg"), options);
```

## IR JSON Output Structure

```json
{
  "metadata": {
    "irVersion": "1.0.0",
    "sourceType": "RPG3",
    "sourceMember": "CUSTINQ",
    "parseInfo": {
      "parsedAt": "2026-03-15T10:00:00Z",
      "parseStatus": "complete",
      "totalLines": 42,
      "errors": [],
      "warnings": []
    }
  },
  "content": {
    "sourceLines": [...],
    "headerSpecs": [...],
    "fileSpecs": [...],
    "extensionSpecs": [...],
    "lineCounterSpecs": [...],
    "inputSpecs": [...],
    "dataStructures": [...],
    "calculationSpecs": [...],
    "outputSpecs": [...],
    "subroutines": [...],
    "symbolTable": [...],
    "comments": [...],
    "compileTimeData": { "blocks": [...] }
  },
  "dependencies": {
    "referencedFiles": [...],
    "calledPrograms": [...],
    "copyMembers": [...]
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
│       │   │   ├── normalizer/    # Source text normalization
│       │   │   ├── model/         # IR data model
│       │   │   ├── parser/        # Base parser interface
│       │   │   └── serializer/    # JSON serialization
│       │   └── rpg3/              # RPG3-specific
│       │       ├── Rpg3ParserFacade.java    # Public API + CLI
│       │       ├── Rpg3IrBuilder.java       # Raw-line IR builder
│       │       ├── Rpg3SymbolTableBuilder.java
│       │       ├── Rpg3CopyResolver.java
│       │       ├── ExpressionBuilder.java
│       │       └── model/         # RPG3 IR models
│       └── test/java/             # 213 tests
├── grammar/rpg3/                  # ANTLR grammar (reference)
├── cli/                           # Python CLI wrapper
├── example/ir/rpg3.json           # Sample IR output
└── docs/ai/                       # AI-assisted development docs
```

## Documentation

| Document | Path |
|----------|------|
| Requirements | `docs/ai/requirements/feature-rpg3-parser.md` |
| Design | `docs/ai/design/feature-rpg3-parser.md` |
| Planning | `docs/ai/planning/feature-rpg3-parser.md` |
| Implementation | `docs/ai/implementation/feature-rpg3-parser.md` |
| Testing | `docs/ai/testing/feature-rpg3-parser.md` |
| IR JSON Template | `docs/ai/design/feature-ir-json-template.md` |

## License

TBD
