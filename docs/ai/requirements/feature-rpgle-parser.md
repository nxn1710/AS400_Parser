---
phase: requirements
title: Requirements & Problem Understanding
description: Clarify the problem space, gather requirements, and define success criteria
---

# Requirements & Problem Understanding

## Problem Statement
**What problem are we solving?**
- The `as400_parser` framework lacks support for RPGLE (RPG IV) source code.
- Developers need to analyze and migrate RPGLE files, which requires extracting and structuring their code into an Intermediate Representation (IR).
- RPGLE code can exist in multiple formats: fixed-format, free-format (`**FREE`), and a mix of both (`/FREE` and `/END-FREE` within fixed-format code). 

## Goals & Objectives
**What do we want to achieve?**
- Primary Goals:
  - Add an RPGLE parser capable of handling `.rpgle` and `.sqlrpgle` file extensions.
  - Parse completely free-format RPGLE (`**FREE`) entirely using an ANTLR grammar.
  - Parse fixed-format RPGLE specifications (Control, File Description, Definition, Input, Calculation, Output, and Procedure Specs) specifically via position-based line parsing, explicitly avoiding ANTLR for these components just like the existing RPG3 parser.
  - Parse mixed-format RPGLE blocks line-by-line: lines falling inside free-format zones use ANTLR, and standard lines fall back to position-based parsing.
  - Capture supported Built-in Functions (e.g. `%LEN`, `%SUBST`) and Operation Codes (e.g. `CHAIN`, `READ`, `EVAL`) as documented in the ILE RPG Reference (`rpgle-knowledge.md`).
  - Output standardized IR JSON matching other supported AS400 languages.
- Non-goals:
  - Runtime execution or evaluation of the RPGLE logic.
  - Legacy RPG II or RPG III handling (already supported by an existing RPG3 parser module).

## User Stories & Use Cases
**How will users interact with the solution?**
- As a tooling engineer, I want to run `python as400_parser_cli.py parse source.rpgle` and get a structured JSON AST/IR output representing the logic.
- As a developer, I want all fixed-format definitions (File, Data) to be extracted into clear properties.
- As a developer, I want `**FREE` operation codes and syntax to be correctly parsed and tokenized.

## Success Criteria
**How will we know when we're done?**
- Python CLI correctly targets `.rpgle` and `.sqlrpgle` files and invokes the Java parser.
- The Java core contains a new `RpgleParser` adhering strictly to the `As400Parser` interface.
- Fixed format, `**FREE` format, and mixed-format source test files are accurately parsed into IR.
- Code coverage is 100% for the new Java implementation.

## Constraints & Assumptions
**What limitations do we need to work within?**
- Must support Java 17 and Python 3.10+ as per existing architectures.
- The JSON output must strictly conform to existing null/empty string design principles established in `.agent/workflows`.

## Questions & Open Items
**What do we still need to clarify?**
- **Resolved**: We will use ANTLR for the `**FREE` format parsing and free-format statements rather than manual string tokenization.
- **Resolved**: Compiler directives (`/IF`, `/EJECT`, `/COPY`, etc.) will be largely ignored or parsed as 'NoOp' tokens in this initial stage, prioritizing extraction of variables and free-format logic.
