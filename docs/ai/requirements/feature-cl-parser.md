---
phase: requirements
title: Requirements — CL Parser
description: Define functional and non-functional requirements for the CL/CLP/CLLE parser
---

# Requirements

## Problem Statement

AS/400 CL (Control Language) source files encode the orchestration logic for IBM i applications — calling programs, overriding files, managing libraries, sending messages, and handling errors. These files must be parsed into a structured IR (Intermediate Representation) so that:

1. Dependencies can be extracted automatically (called programs, file overrides, libraries)
2. The source can be analyzed for migration to Java/Spring Boot equivalents
3. The structured output follows the same `IrDocument` contract as all other parsers in the project

## Source File Variants

| Extension | SourceType | Description |
|-----------|------------|-------------|
| `.cl`     | `CL`       | Traditional CL program |
| `.clp`    | `CLP`      | CL program (older convention) |
| `.clle`   | `CLLE`     | ILE CL (supports subroutines, enhanced syntax) |

## Functional Requirements

### FR-1: File Registration
- The parser must register with `As400ParserCli` for extensions `.cl`, `.clp`, `.clle`.
- Unsupported extensions (e.g., `.clmod`) must NOT match.

### FR-2: Source Normalization
- Must use `SourceNormalizer` for encoding detection (EBCDIC, Shift-JIS, UTF-8).
- Must correctly handle files from IBM i source physical file members.

### FR-3: Comment Extraction
- `/* ... */` block comments (single or multi-line) must be extracted into `ClContent.comments`.
- Comment text must be preserved verbatim.
- An unterminated block comment at EOF must produce a `ParseError.WARNING`.

### FR-4: Line Continuation
- Lines ending with `+` (last non-blank char) continue with the next line; leading whitespace on the next line is skipped.
- Lines ending with `-` continue with the next line; leading whitespace is NOT skipped.
- Continuation markers inside quoted strings must NOT be treated as continuation.

### FR-5: Quoted String Handling
- Quoted strings `'...'` prevent all syntax interpretation inside them.
- `''` (two consecutive single-quotes) inside a string represents a literal single-quote (escape rule); the string does NOT end at the first `'` of a `''` pair.

### FR-6: Variable Declarations (DCL)
- `DCL VAR(&NAME) TYPE(*CHAR/*DEC/*LGL) LEN(n) [DEC(d)] [VALUE('x')]`
- Must extract: variable name, type, length, decimal positions, initial value.
- Multiple DCL statements produce multiple entries in `ClContent.variables`.

### FR-7: File Declarations (DCLF)
- `DCLF FILE(LIB/FILE) [OPNID(ID)]`
- Must extract: fileName (including library qualifier), openId.
- DCLF entries go into `ClContent.fileDeclarations` (not `ClContent.commands`).
- Each DCLF produces exactly one entry in `IrDocument.Dependencies.referencedFiles` with type `"file-declaration"`.

### FR-8: Command Parsing
- All CL commands must be captured in `ClContent.commands` (or subroutine commands if inside a SUBR block).
- Each command must record: name (uppercase), parameters (keyword+value), location (start/end line), raw source lines.
- Commands not specifically recognized by the dependency extractor must still be captured.

### FR-9: Labels and GOTO
- `LABELNAME:` (label on its own line or prefixing a command) must be captured in `ClContent.labels`.
- Labels must be tagged on their source line as `LABEL` specType.

### FR-10: Subroutines (CLLE)
- `LABEL: SUBR ... ENDSUBR` defines a named subroutine.
- The subroutine name comes from the preceding label, NOT from a SUBR parameter.
- Commands inside the SUBR block go into `ClSubroutine.commands` (not top-level `ClContent.commands`).

### FR-11: MONMSG Error Monitoring
- `MONMSG MSGID(x) [EXEC(command)]` captures the message ID and optional nested EXEC command.
- The nested EXEC command must be parseable (e.g., `GOTO CMDLBL(label)`, `CALL PGM(pgm)`).
- MONMSG EXEC(CALL PGM(...)) must still feed called programs into the dependency graph.

### FR-12: Dependency Extraction — Called Programs
- `CALL PGM(lib/pgm)` → `calledPrograms` entry with type `"call"`.
- CALL inside subroutines must also be captured.
- MONMSG EXEC(CALL PGM(x)) must also contribute to calledPrograms.

### FR-13: Dependency Extraction — Referenced Files
- File override commands (`OVRDBF`, `OVRDSPF`, `OVRPRTF`, etc.) → `"file-override"` (TOFILE takes priority over FILE when both present).
- `DLTOVR FILE(*ALL)` → must be filtered; `*ALL` is not a real file reference.
- `OPNDBF`, `OPNQRYF` → `"file-open"`
- `CLOF`, `CLRPFM` → `"file-close"`
- `RCVF`, `SNDF`, `SNDRCVF` → `"file-read"` / `"file-write"`
- `CPYF`, `CPYSRCF` → `"file-read"` (FROMFILE) + `"file-write"` (TOFILE)
- `CRTPF`, `CRTSRCPF` → `"file-create"`, `DLTF` → `"file-delete"`
- Message files (`SNDPGMMSG`, `SNDINQMSG`, `RCVMSG`, etc.) → `"message-file"` / `"message-queue"` (system message files starting with `*` excluded)
- Library operations (`ADDLIBLE` → `"library"`, `RMVLIBLE` → `"library-remove"`, `CRTLIB` → `"library-create"`, `DLTLIB` → `"library-delete"`; system libraries starting with `*` excluded)
- Data area operations (`RTVDTAARA`, `CHGDTAARA`, `DLTDTAARA`, `CRTDTAARA`)
- `copyMembers` always empty (CL has no `/COPY` mechanism).

### FR-14: Metadata
- `sourceType`: `"CL"`, `"CLP"`, or `"CLLE"` (from file extension).
- `sourceMember`: filename without extension, uppercased.
- `sourceFile`: parent directory name.
- `sourceLibrary`: grandparent directory name.
- `parseInfo.parsedAt`: ISO-8601 instant.
- `parseInfo.totalLines`: from `NormalizedSource.getLineCount()`.
- `parseInfo.parseStatus`: `"complete"` if no `ERROR`-severity errors; `"partial"` otherwise.
- `parseInfo.errors`: list of ERROR-severity `ParseError` entries in standard map format.
- `parseInfo.warnings`: list of WARNING-severity `ParseError` entries in standard map format.

### FR-15: Source Line Tagging
- Each physical source line must have a `specType` tag:
  - `COMMAND` — first line of any CL command
  - `CONTINUATION` — continuation lines of a multi-line command
  - `COMMENT` — inside a `/* ... */` block
  - `LABEL` — label-only lines

## Non-Functional Requirements

- **Performance:** Must parse a 1000-line CL program in < 500ms.
- **Robustness:** Must not throw uncaught exceptions on malformed input; report issues via `ParseError`.
- **Consistency:** `IrDocument` structure must match all other parsers (RPGLE, RPG3, DDS, DSPF, PRTF).
