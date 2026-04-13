---
phase: implementation
feature: analyzer
title: "Implementation: Analyzer"
description: "Implementation notes for the analyzer feature"
---

# Implementation Guide: Analyzer

## Development Setup

- Worktree: `.worktrees/feature-analyzer` on branch `feature-analyzer`
- Build: `./gradlew.bat :parser-core:test --no-daemon`
- All new code in `parser-core/src/main/java/com/as400parser/common/analyzer/`

## Code Structure

```
parser-core/src/main/java/com/as400parser/common/analyzer/
├── model/
│   ├── AnalysisResult.java          # Root container
│   ├── DatabaseSchema.java          # Tables + indexes
│   ├── TableDefinition.java         # PF → Table
│   ├── ColumnDefinition.java        # Field → Column
│   ├── IndexDefinition.java         # LF → Index
│   ├── KeyFieldDef.java             # Index key field
│   ├── SelectOmitRule.java          # LF select/omit
│   ├── CrossReference.java          # Call graph + file usage
│   ├── CallTarget.java              # Call edge
│   ├── FileUsage.java               # Program-file relationship
│   └── IOOperation.java             # Enum: READ, WRITE, etc.
├── SchemaAnalyzer.java              # DDS PF/LF → schema
├── DdsToSqlTypeMapper.java          # DDS type → SQL type
├── CrossReferenceAnalyzer.java      # Call graph builder
├── FileUsageAnalyzer.java           # File I/O tracking
├── IrDocumentLoader.java            # Batch IR loader
└── AnalyzerFacade.java              # Orchestrator
```

## Implementation Notes

### Pending — implementation starts after design approval.
