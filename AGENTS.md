# AI DevKit Rules

## Project Context
This project uses ai-devkit for structured AI-assisted development. Phase documentation is located in `docs/ai/`.

## Project Structure
```
AS400_Parser/
├── AGENTS.md                          # AI agent rules & project conventions
├── README.md                          # Project overview & usage guide
├── LICENSE
├── settings.gradle                    # Gradle multi-project settings
├── gradle.properties                  # Gradle build properties
├── gradlew.bat                        # Gradle wrapper (Windows)
├── .ai-devkit.json                    # AI DevKit configuration
├── .gitignore
│
├── parser-core/                       # ★ Main Java parser module (Gradle subproject)
│   ├── build.gradle                   # Dependencies: ANTLR, Gson, JUnit 5
│   ├── src/main/java/com/as400parser/
│   │   ├── common/                    # Shared framework & IR model
│   │   │   ├── cli/
│   │   │   │   └── As400ParserCli.java        # Unified CLI entry point
│   │   │   ├── model/
│   │   │   │   ├── IrDocument.java            # Root IR document model
│   │   │   │   ├── Metadata.java              # File/parse metadata
│   │   │   │   ├── Location.java              # Source location tracking
│   │   │   │   ├── ParseError.java            # Parse error representation
│   │   │   │   ├── ResolvedCopy.java          # /COPY resolution info
│   │   │   │   └── SourceLine.java            # Normalized source line
│   │   │   ├── normalizer/
│   │   │   │   ├── SourceNormalizer.java       # Encoding & line normalization
│   │   │   │   ├── NormalizedSource.java       # Normalized source container
│   │   │   │   ├── NormalizationWarning.java   # Normalization warnings
│   │   │   │   └── EncodingDetector.java       # EBCDIC/UTF-8 detection
│   │   │   ├── parser/
│   │   │   │   ├── As400Parser.java           # Parser interface (SPI)
│   │   │   │   └── ParseOptions.java          # Parse configuration options
│   │   │   ├── serializer/
│   │   │   │   └── IrJsonSerializer.java      # IR → JSON serialization
│   │   │   └── analyzer/
│   │   │       └── model/                     # Analyzer models (future)
│   │   │
│   │   ├── rpgle/                     # RPGLE parser (fixed + free format)
│   │   │   ├── RpgleParserFacade.java         # RPGLE parser facade
│   │   │   ├── RpgleFixedParser.java          # Fixed-format spec parser
│   │   │   ├── RpgleFreeParser.java           # Free-format statement parser
│   │   │   ├── RpgleIrBuilder.java            # IR assembly from parsed specs
│   │   │   └── model/
│   │   │       ├── RpgleContent.java          # RPGLE IR content
│   │   │       ├── CalcSpec.java              # C-spec (calc) model
│   │   │       ├── ControlSpec.java           # H-spec (control) model
│   │   │       ├── DefinitionSpec.java        # D-spec (definition) model
│   │   │       ├── FileSpec.java              # F-spec (file) model
│   │   │       ├── InputSpec.java             # I-spec (input) model
│   │   │       ├── OutputSpec.java            # O-spec (output) model
│   │   │       ├── ProcedureSpec.java         # P-spec (procedure) model
│   │   │       └── FreeFormatStatement.java   # Free-format statement model
│   │   │
│   │   ├── rpg3/                      # RPG III parser (columnar format)
│   │   │   ├── Rpg3ParserFacade.java          # RPG3 parser facade
│   │   │   ├── Rpg3IrBuilder.java             # RPG3 IR builder
│   │   │   ├── Rpg3CopyResolver.java          # /COPY member resolver
│   │   │   ├── Rpg3ErrorListener.java         # ANTLR error listener
│   │   │   ├── Rpg3SymbolTableBuilder.java    # Symbol table construction
│   │   │   ├── ExpressionBuilder.java         # Expression AST builder
│   │   │   └── model/
│   │   │       ├── Rpg3Content.java           # RPG3 IR content
│   │   │       ├── CalcSpec.java              # C-spec model
│   │   │       ├── UnparsedSpec.java          # H/F/I/O/E/L spec model
│   │   │       ├── ExpressionNode.java        # Expression AST base
│   │   │       ├── BinaryOpNode.java          # Binary operation node
│   │   │       ├── UnaryOpNode.java           # Unary operation node
│   │   │       ├── IdentifierNode.java        # Variable reference node
│   │   │       ├── LiteralNode.java           # Literal value node
│   │   │       ├── ArrayElementNode.java      # Array access node
│   │   │       ├── IndicatorNode.java         # Indicator reference node
│   │   │       ├── FigurativeConstantNode.java # *BLANKS, *ZEROS, etc.
│   │   │       └── SpecialValueNode.java      # *IN, *INLR, etc.
│   │   │
│   │   ├── dds/                       # DDS parser (PF, LF)
│   │   │   ├── DdsParserFacade.java           # DDS parser facade
│   │   │   ├── DdsIrBuilder.java              # DDS IR builder
│   │   │   ├── DdsLineClassifier.java         # Line type classification
│   │   │   ├── DdsKeywordParser.java          # Keyword extraction
│   │   │   ├── DdsRefResolver.java            # REF/REFFLD resolver
│   │   │   ├── DdsLineType.java               # Line type enum
│   │   │   └── model/
│   │   │       ├── DdsPfContent.java          # Physical file IR content
│   │   │       ├── DdsLfContent.java          # Logical file IR content
│   │   │       ├── RecordFormat.java          # Record format (PF)
│   │   │       ├── LfRecordFormat.java        # Record format (LF)
│   │   │       ├── FieldDefinition.java       # Field definition
│   │   │       ├── KeyDefinition.java         # Key field definition
│   │   │       ├── DdsKeyword.java            # DDS keyword model
│   │   │       ├── DdsComment.java            # Comment model
│   │   │       ├── SelectOmitSpec.java        # Select/Omit specification
│   │   │       ├── JoinSpec.java              # Join specification
│   │   │       └── JoinFieldPair.java         # Join field pair
│   │   │
│   │   ├── dspf/                      # DSPF parser (Display files)
│   │   │   ├── DspfParserFacade.java          # DSPF parser facade
│   │   │   ├── DspfIrBuilder.java             # DSPF IR builder
│   │   │   └── model/
│   │   │       ├── DspfContent.java           # DSPF IR content
│   │   │       ├── DspfRecordFormat.java      # Display record format
│   │   │       ├── DspfFieldDefinition.java   # Display field definition
│   │   │       ├── DspfConstant.java          # Display constant
│   │   │       ├── ConditionedKeyword.java    # Conditioned keyword
│   │   │       └── ConditioningIndicator.java # Conditioning indicator
│   │   │
│   │   ├── prtf/                      # PRTF parser (Printer files)
│   │   │   ├── PrtfParserFacade.java          # PRTF parser facade
│   │   │   ├── PrtfIrBuilder.java             # PRTF IR builder
│   │   │   └── model/
│   │   │       ├── PrtfContent.java           # PRTF IR content
│   │   │       ├── PrtfRecordFormat.java      # Printer record format
│   │   │       ├── PrtfFieldDefinition.java   # Printer field definition
│   │   │       └── PrtfConstant.java          # Printer constant
│   │   │
│   │   └── cl/                        # CL/CLLE parser (Control Language)
│   │       ├── ClParserFacade.java            # CL parser facade
│   │       ├── ClIrBuilder.java               # CL IR builder
│   │       └── model/
│   │           ├── ClContent.java             # CL IR content
│   │           ├── ClCommand.java             # CL command model
│   │           ├── ClVariable.java            # CL variable declaration
│   │           ├── ClParameter.java           # Command parameter model
│   │           ├── ClLabel.java               # Label model
│   │           ├── ClSubroutine.java          # Subroutine model
│   │           ├── ClMonitorMessage.java      # MONMSG model
│   │           ├── ClComment.java             # Comment model
│   │           └── ClFileDeclaration.java     # File declaration model
│   │
│   └── src/test/
│       ├── java/com/as400parser/
│       │   ├── common/
│       │   │   ├── normalizer/SourceNormalizerTest.java
│       │   │   └── serializer/IrJsonSerializerTest.java
│       │   ├── rpgle/
│       │   │   ├── RpgleFixedParserTest.java
│       │   │   ├── RpgleFreeParserTest.java
│       │   │   ├── RpgleIrBuilderTest.java
│       │   │   └── RpgleParserIntegrationTest.java
│       │   ├── rpg3/
│       │   │   ├── Rpg3ParserFacadeTest.java
│       │   │   ├── Rpg3IrBuilderTest.java
│       │   │   ├── Rpg3CopyResolverTest.java
│       │   │   ├── Rpg3SymbolTableBuilderTest.java
│       │   │   ├── ExpressionBuilderTest.java
│       │   │   ├── Rpg3IntegrationTest.java
│       │   │   └── StudentMgmtParserTest.java
│       │   ├── dds/
│       │   │   ├── DdsIrBuilderTest.java
│       │   │   ├── DdsLineClassifierTest.java
│       │   │   ├── DdsKeywordParserTest.java
│       │   │   ├── DdsRefResolverTest.java
│       │   │   └── DdsIntegrationTest.java
│       │   ├── dspf/
│       │   │   ├── DspfIrBuilderTest.java
│       │   │   └── DspfIntegrationTest.java
│       │   ├── prtf/
│       │   │   └── PrtfIrBuilderTest.java
│       │   └── cl/
│       │       ├── ClIrBuilderTest.java
│       │       └── ClParserIntegrationTest.java
│       └── resources/
│           ├── rpgle/                         # RPGLE test fixtures
│           │   ├── simple_fixed.rpgle
│           │   ├── fully_free.rpgle
│           │   ├── mixed_format.rpgle
│           │   ├── all_specs.rpgle
│           │   ├── copy_directives.rpgle
│           │   └── simple.sqlrpgle
│           └── cl/                            # CL test fixtures
│               ├── simple.cl
│               ├── declarations.clle
│               ├── controlflow.clle
│               ├── continuation.clle
│               ├── subroutines.clle
│               └── monmsg.clp
│
├── grammar/                           # ANTLR 4 grammar definitions
│   ├── rpg3/
│   │   ├── Rpg3Lexer.g4                      # RPG III lexer grammar
│   │   └── Rpg3Parser.g4                     # RPG III parser grammar
│   └── rpgle/
│       ├── RpgLexer.g4                       # RPGLE lexer grammar
│       └── RpgParser.g4                      # RPGLE parser grammar
│
├── cli/                               # Python CLI wrappers
│   ├── as400_parser_cli.py                   # Unified CLI (all source types)
│   └── rpg3_parser_cli.py                    # RPG3-specific CLI
│
├── rpg3-student-mgmt/                 # ★ Sample AS400 application source
│   ├── QCLSRC/                                # CL source members
│   │   └── MNUCL.clle
│   ├── QCPYSRC/                               # Copy source members
│   │   ├── SCHOOLCPY.cpy
│   │   └── STUDNTCPY.cpy
│   ├── QDDSSRC/                               # DDS source members (PF, LF, DSPF, PRTF)
│   │   ├── CLASSPF.pf, CLASSL1.lf
│   │   ├── SCHOOLPF.pf, SCHOOLL1.lf
│   │   ├── STUDNTPF.pf, STUDNTL1.lf, STUDNTL2.lf
│   │   ├── STUCLSPF.pf, STUCLSL1.lf
│   │   ├── TEACHPF.pf, FLDREFPF.pf, REFSAMPF.pf
│   │   ├── MNUDSPF.dspf, STUDSPF.dspf, STULSTD.dspf
│   │   └── STURPTPF.prtf
│   └── QRPGSRC/                               # RPG III source members
│       ├── MNUPRG.RPG
│       ├── STUPRG.rpg
│       ├── STULST.rpg
│       └── STURPT.rpg
│
├── output/                            # Parsed IR JSON output
│   ├── QCLSRC/                                # CL IR output
│   ├── QCPYSRC/                               # Copy member IR output
│   ├── QDDSSRC/                               # DDS/DSPF/PRTF IR output
│   ├── QRPGSRC/                               # RPG3 IR output
│   └── example-ir/                            # Example IR samples
│
├── example/                           # Example files & IR samples
│   ├── ir/                                    # Example IR JSON
│   ├── rpg3/                                  # Example RPG3 source
│   ├── test_fixed.rpgle                       # Example RPGLE (fixed)
│   └── test_free.rpgle                        # Example RPGLE (free)
│
├── docs/ai/                           # AI-assisted development docs
│   ├── requirements/                          # Feature requirements
│   ├── design/                                # Architecture & design
│   ├── planning/                              # Task breakdown & planning
│   ├── implementation/                        # Implementation guides
│   ├── testing/                               # Testing strategy & results
│   ├── knowledge/                             # Domain knowledge docs
│   ├── deployment/                            # Deployment docs
│   └── monitoring/                            # Monitoring docs
│
├── .agent/                            # AI agent configuration
│   ├── skills/                                # AI agent skills
│   │   ├── capture-knowledge/                 # Knowledge capture skill
│   │   └── dev-lifecycle/                     # Development lifecycle skill
│   └── workflows/                             # Slash-command workflows
│       ├── capture-knowledge.md
│       ├── check-implementation.md
│       ├── code-review.md
│       ├── debug.md
│       ├── execute-plan.md
│       ├── new-requirement.md
│       ├── remember.md
│       ├── review-design.md
│       ├── review-requirements.md
│       ├── simplify-implementation.md
│       ├── update-planning.md
│       └── writing-test.md
│
├── gradle/                            # Gradle wrapper files
└── java-output/                       # Java code generation output (empty)
```

## Documentation Structure
- `docs/ai/requirements/` - Problem understanding and requirements
- `docs/ai/design/` - System architecture and design decisions (include mermaid diagrams)
- `docs/ai/planning/` - Task breakdown and project planning
- `docs/ai/implementation/` - Implementation guides and notes
- `docs/ai/testing/` - Testing strategy and test cases
- `docs/ai/deployment/` - Deployment and infrastructure docs
- `docs/ai/monitoring/` - Monitoring and observability setup

## Code Style & Standards
- Follow the project's established code style and conventions
- Write clear, self-documenting code with meaningful variable names
- Add comments for complex logic or non-obvious decisions

## Development Workflow
- Review phase documentation in `docs/ai/` before implementing features
- Keep requirements, design, and implementation docs updated as the project evolves
- Reference the planning doc for task breakdown and priorities
- Copy the testing template (`docs/ai/testing/README.md`) before creating feature-specific testing docs

## AI Interaction Guidelines
- When implementing features, first check relevant phase documentation
- For new features, start with requirements clarification
- Update phase docs when significant changes or decisions are made

## Skills (Extend Your Capabilities)
Skills are packaged capabilities that teach you new competencies, patterns, and best practices. Check for installed skills in the project's skill directory and use them to enhance your work.

### Using Installed Skills
1. **Check for skills**: Look for `SKILL.md` files in the project's skill directory
2. **Read skill instructions**: Each skill contains detailed guidance on when and how to use it
3. **Apply skill knowledge**: Follow the patterns, commands, and best practices defined in the skill

### Key Installed Skills
- **memory**: Use AI DevKit's memory service via CLI commands when MCP is unavailable. Read the skill for detailed `memory store` and `memory search` command usage.

### When to Reference Skills
- Before implementing features that match a skill's domain
- When MCP tools are unavailable but skill provides CLI alternatives
- To follow established patterns and conventions defined in skills

## Knowledge Memory (Always Use When Helpful)
The AI assistant should proactively use knowledge memory throughout all interactions.

> **Tip**: If MCP is unavailable, use the **memory skill** for detailed CLI command reference.

### When to Search Memory
- Before starting any task, search for relevant project conventions, patterns, or decisions
- When you need clarification on how something was done before
- To check for existing solutions to similar problems
- To understand project-specific terminology or standards

**How to search**:
- Use `memory.searchKnowledge` MCP tool with relevant keywords, tags, and scope
- If MCP tools are unavailable, use `npx ai-devkit memory search` CLI command (see memory skill for details)
- Example: Search for "authentication patterns" when implementing auth features

### When to Store Memory
- After making important architectural or design decisions
- When discovering useful patterns or solutions worth reusing
- If the user explicitly asks to "remember this" or save guidance
- When you establish new conventions or standards for the project

**How to store**:
- Use `memory.storeKnowledge` MCP tool
- If MCP tools are unavailable, use `npx ai-devkit memory store` CLI command (see memory skill for details)
- Include clear title, detailed content, relevant tags, and appropriate scope
- Make knowledge specific and actionable, not generic advice

### Memory Best Practices
- **Be Proactive**: Search memory before asking the user repetitive questions
- **Be Specific**: Store knowledge that's actionable and reusable
- **Use Tags**: Tag knowledge appropriately for easy discovery (e.g., "api", "testing", "architecture")
- **Scope Appropriately**: Use `global` for general patterns, `project:<name>` for project-specific knowledge

## Testing & Quality
- Write tests alongside implementation
- Follow the testing strategy defined in `docs/ai/testing/`
- Use `/writing-test` to generate unit and integration tests targeting 100% coverage
- Ensure code passes all tests before considering it complete

## Documentation
- Update phase documentation when requirements or design changes
- Keep inline code comments focused and relevant
- Document architectural decisions and their rationale
- Use mermaid diagrams for any architectural or data-flow visuals (update existing diagrams if needed)
- Record test coverage results and outstanding gaps in `docs/ai/testing/`

## Key Commands
When working on this project, you can run commands to:
- Understand project requirements and goals (`review-requirements`)
- Review architectural decisions (`review-design`)
- Plan and execute tasks (`execute-plan`)
- Verify implementation against design (`check-implementation`)
- Writing tests (`writing-test`)
- Perform structured code reviews (`code-review`)
