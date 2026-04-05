package com.as400parser.rpgle;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.rpgle.model.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link RpgleIrBuilder} — isolated IR assembly logic.
 * <p>
 * Constructs {@link RpgleContent} programmatically and verifies
 * the resulting {@link IrDocument} structure (dependencies, metadata, errors).
 */
class RpgleIrBuilderTest {

    private final RpgleIrBuilder builder = new RpgleIrBuilder();

    /**
     * Creates a minimal NormalizedSource for testing.
     */
    private NormalizedSource fakeNormalized(int lineCount) {
        String[] lines = new String[lineCount];
        int[] lineNums = new int[lineCount];
        String[] seqNums = new String[lineCount];
        for (int i = 0; i < lineCount; i++) {
            lines[i] = "line " + (i + 1);
            lineNums[i] = i + 1;
            seqNums[i] = "";
        }
        return new NormalizedSource(lines, lineNums, seqNums, List.of());
    }

    /**
     * Creates a minimal RpgleContent.
     */
    private RpgleContent emptyContent() {
        RpgleContent c = new RpgleContent();
        c.setFormatType("fixed");
        return c;
    }

    // =========================================================================
    // IrDocument structure
    // =========================================================================

    @Nested
    class DocumentStructure {

        @Test
        void build_returnsNonNullDocument() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc).isNotNull();
        }

        @Test
        void build_setsContentOnDocument() {
            RpgleContent content = emptyContent();
            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getContent()).isSameAs(content);
        }

        @Test
        void build_setsDependenciesOnDocument() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies()).isNotNull();
        }

        @Test
        void build_setsMetadataOnDocument() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getMetadata()).isNotNull();
        }

        @Test
        void build_setsErrorsListOnDocument() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getErrors()).isNotNull();
        }
    }

    // =========================================================================
    // Dependency: Referenced files from F-specs
    // =========================================================================

    @Nested
    class ReferencedFileExtraction {

        @Test
        void noFSpecs_emptyReferencedFiles() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles()).isEmpty();
        }

        @Test
        void singleFSpec_extractedAsReferencedFile() {
            RpgleContent content = emptyContent();
            FileSpec fs = new FileSpec();
            fs.setFileName("STUDNTPF");
            fs.setFileType("I");
            fs.setLocation(new Location(1, 1));
            content.setFileSpecs(List.of(fs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles()).hasSize(1);
            IrDocument.DependencyRef ref = doc.getDependencies().getReferencedFiles().get(0);
            assertThat(ref.getName()).isEqualTo("STUDNTPF");
            assertThat(ref.getReferenceType()).isEqualTo("read");
        }

        @Test
        void fSpec_inputType_mapsToRead() {
            RpgleContent content = emptyContent();
            FileSpec fs = new FileSpec();
            fs.setFileName("MYFILE");
            fs.setFileType("I");
            fs.setLocation(new Location(1, 1));
            content.setFileSpecs(List.of(fs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles().get(0).getReferenceType())
                    .isEqualTo("read");
        }

        @Test
        void fSpec_outputType_mapsToWrite() {
            RpgleContent content = emptyContent();
            FileSpec fs = new FileSpec();
            fs.setFileName("MYFILE");
            fs.setFileType("O");
            fs.setLocation(new Location(1, 1));
            content.setFileSpecs(List.of(fs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles().get(0).getReferenceType())
                    .isEqualTo("write");
        }

        @Test
        void fSpec_updateType_mapsToUpdate() {
            RpgleContent content = emptyContent();
            FileSpec fs = new FileSpec();
            fs.setFileName("MYFILE");
            fs.setFileType("U");
            fs.setLocation(new Location(1, 1));
            content.setFileSpecs(List.of(fs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles().get(0).getReferenceType())
                    .isEqualTo("update");
        }

        @Test
        void fSpec_combinedType_mapsToCombined() {
            RpgleContent content = emptyContent();
            FileSpec fs = new FileSpec();
            fs.setFileName("MYFILE");
            fs.setFileType("C");
            fs.setLocation(new Location(1, 1));
            content.setFileSpecs(List.of(fs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles().get(0).getReferenceType())
                    .isEqualTo("combined");
        }

        @Test
        void fSpec_nullType_defaultsToRead() {
            RpgleContent content = emptyContent();
            FileSpec fs = new FileSpec();
            fs.setFileName("MYFILE");
            fs.setFileType(null);
            fs.setLocation(new Location(1, 1));
            content.setFileSpecs(List.of(fs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles().get(0).getReferenceType())
                    .isEqualTo("read");
        }

        @Test
        void multipleFSpecs_allExtracted() {
            RpgleContent content = emptyContent();
            List<FileSpec> specs = new ArrayList<>();
            for (String name : List.of("FILE1", "FILE2", "FILE3")) {
                FileSpec fs = new FileSpec();
                fs.setFileName(name);
                fs.setFileType("I");
                fs.setLocation(new Location(1, 1));
                specs.add(fs);
            }
            content.setFileSpecs(specs);

            IrDocument doc = builder.build(content, fakeNormalized(3), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles()).hasSize(3);
        }

        @Test
        void fSpec_locationAttachedToDependencyRef() {
            RpgleContent content = emptyContent();
            FileSpec fs = new FileSpec();
            fs.setFileName("STUDNTPF");
            fs.setFileType("I");
            fs.setLocation(new Location(5, 5));
            content.setFileSpecs(List.of(fs));

            IrDocument doc = builder.build(content, fakeNormalized(5), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles().get(0).getLocations())
                    .hasSize(1);
            assertThat(doc.getDependencies().getReferencedFiles().get(0)
                    .getLocations().get(0).getStartLine()).isEqualTo(5);
        }

        @Test
        void freeFormat_dclF_extractedAsReferencedFile() {
            RpgleContent content = emptyContent();
            content.setFormatType("free");
            FreeFormatStatement stmt = new FreeFormatStatement();
            stmt.setStatementType("dcl-f");
            stmt.setText("dcl-f STUDNTPF disk(*ext) usage(*input) keyed");
            stmt.setLocation(new Location(3, 3));
            content.setFreeFormatStatements(List.of(stmt));

            IrDocument doc = builder.build(content, fakeNormalized(5), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles()).hasSize(1);
            assertThat(doc.getDependencies().getReferencedFiles().get(0).getName())
                    .isEqualTo("STUDNTPF");
        }

        @Test
        void fSpec_blankFileName_notExtracted() {
            RpgleContent content = emptyContent();
            FileSpec fs = new FileSpec();
            fs.setFileName("   ");
            fs.setFileType("I");
            fs.setLocation(new Location(1, 1));
            content.setFileSpecs(List.of(fs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getReferencedFiles()).isEmpty();
        }
    }

    // =========================================================================
    // Dependency: Called programs from C-spec CALL/CALLP
    // =========================================================================

    @Nested
    class CalledProgramExtraction {

        @Test
        void noCSpecs_emptyCalledPrograms() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).isEmpty();
        }

        @Test
        void call_quotedProgramName_extracted() {
            RpgleContent content = emptyContent();
            CalcSpec cs = new CalcSpec();
            cs.setOpcode("CALL");
            cs.setFactor2("'RPTPGM'");
            cs.setLocation(new Location(10, 10));
            content.setCalcSpecs(List.of(cs));

            IrDocument doc = builder.build(content, fakeNormalized(10), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).hasSize(1);
            IrDocument.DependencyRef ref = doc.getDependencies().getCalledPrograms().get(0);
            assertThat(ref.getName()).isEqualTo("RPTPGM");
            assertThat(ref.getReferenceType()).isEqualTo("call");
        }

        @Test
        void call_unquotedProgramName_extracted() {
            RpgleContent content = emptyContent();
            CalcSpec cs = new CalcSpec();
            cs.setOpcode("  CALL  ");
            cs.setFactor2("MYPGM");
            cs.setLocation(new Location(1, 1));
            content.setCalcSpecs(List.of(cs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).hasSize(1);
            assertThat(doc.getDependencies().getCalledPrograms().get(0).getName())
                    .isEqualTo("MYPGM");
        }

        @Test
        void callp_procedureName_extracted() {
            RpgleContent content = emptyContent();
            CalcSpec cs = new CalcSpec();
            cs.setOpcode("CALLP");
            cs.setFactor2("UpdateLog(Counter)");
            cs.setLocation(new Location(1, 1));
            content.setCalcSpecs(List.of(cs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).hasSize(1);
            IrDocument.DependencyRef ref = doc.getDependencies().getCalledPrograms().get(0);
            assertThat(ref.getName()).isEqualTo("UpdateLog");
            assertThat(ref.getReferenceType()).isEqualTo("callp");
        }

        @Test
        void callp_noParens_procedureName_extracted() {
            RpgleContent content = emptyContent();
            CalcSpec cs = new CalcSpec();
            cs.setOpcode("CALLP");
            cs.setFactor2("SimpleProc");
            cs.setLocation(new Location(1, 1));
            content.setCalcSpecs(List.of(cs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).hasSize(1);
            assertThat(doc.getDependencies().getCalledPrograms().get(0).getName())
                    .isEqualTo("SimpleProc");
        }

        @Test
        void evalOperation_notExtractedAsCalledProgram() {
            RpgleContent content = emptyContent();
            CalcSpec cs = new CalcSpec();
            cs.setOpcode("EVAL");
            cs.setFactor2("Counter = 0");
            cs.setLocation(new Location(1, 1));
            content.setCalcSpecs(List.of(cs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).isEmpty();
        }

        @Test
        void call_blankFactor2_notExtracted() {
            RpgleContent content = emptyContent();
            CalcSpec cs = new CalcSpec();
            cs.setOpcode("CALL");
            cs.setFactor2("   ");
            cs.setLocation(new Location(1, 1));
            content.setCalcSpecs(List.of(cs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).isEmpty();
        }

        @Test
        void call_nullFactor2_notExtracted() {
            RpgleContent content = emptyContent();
            CalcSpec cs = new CalcSpec();
            cs.setOpcode("CALL");
            cs.setFactor2(null);
            cs.setLocation(new Location(1, 1));
            content.setCalcSpecs(List.of(cs));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).isEmpty();
        }

        @Test
        void callp_locationAttachedToDependencyRef() {
            RpgleContent content = emptyContent();
            CalcSpec cs = new CalcSpec();
            cs.setOpcode("CALLP");
            cs.setFactor2("MyProc(x)");
            cs.setLocation(new Location(15, 15));
            content.setCalcSpecs(List.of(cs));

            IrDocument doc = builder.build(content, fakeNormalized(15), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms().get(0)
                    .getLocations().get(0).getStartLine()).isEqualTo(15);
        }

        @Test
        void freeFormat_callp_extractedAsCalledProgram() {
            RpgleContent content = emptyContent();
            content.setFormatType("free");
            FreeFormatStatement stmt = new FreeFormatStatement();
            stmt.setStatementType("callp");
            stmt.setText("callp UpdateLog(Counter)");
            stmt.setLocation(new Location(20, 20));
            content.setFreeFormatStatements(List.of(stmt));

            IrDocument doc = builder.build(content, fakeNormalized(20), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).hasSize(1);
            assertThat(doc.getDependencies().getCalledPrograms().get(0).getName())
                    .isEqualTo("UpdateLog");
        }

        @Test
        void multipleCallsAndCallps_allExtracted() {
            RpgleContent content = emptyContent();
            List<CalcSpec> specs = new ArrayList<>();

            CalcSpec call1 = new CalcSpec();
            call1.setOpcode("CALL");
            call1.setFactor2("'PGM1'");
            call1.setLocation(new Location(1, 1));
            specs.add(call1);

            CalcSpec call2 = new CalcSpec();
            call2.setOpcode("CALLP");
            call2.setFactor2("Proc1(x)");
            call2.setLocation(new Location(2, 2));
            specs.add(call2);

            CalcSpec call3 = new CalcSpec();
            call3.setOpcode("CALL");
            call3.setFactor2("'PGM2'");
            call3.setLocation(new Location(3, 3));
            specs.add(call3);

            content.setCalcSpecs(specs);
            IrDocument doc = builder.build(content, fakeNormalized(3), "RPGLE");
            assertThat(doc.getDependencies().getCalledPrograms()).hasSize(3);
        }
    }

    // =========================================================================
    // Dependency: /COPY and /INCLUDE directives
    // =========================================================================

    @Nested
    class CopyMemberExtraction {

        @Test
        void noCopyDirectives_emptyCopyMembers() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getCopyMembers()).isEmpty();
        }

        @Test
        void copyWithFileAndMember_parsedCorrectly() {
            RpgleContent content = emptyContent();
            content.setCopyDirectives(List.of(
                    new RpgleContent.CopyDirective("COPY", "QRPGLESRC,COPYMBR1", new Location(2, 2))
            ));

            IrDocument doc = builder.build(content, fakeNormalized(2), "RPGLE");
            assertThat(doc.getDependencies().getCopyMembers()).hasSize(1);
            IrDocument.CopyMemberRef ref = doc.getDependencies().getCopyMembers().get(0);
            assertThat(ref.getDirective()).isEqualTo("COPY");
            assertThat(ref.getFileName()).isEqualTo("QRPGLESRC");
            assertThat(ref.getMemberName()).isEqualTo("COPYMBR1");
            assertThat(ref.getLibraryName()).isNull();
        }

        @Test
        void copyWithLibFileAndMember_parsedCorrectly() {
            RpgleContent content = emptyContent();
            content.setCopyDirectives(List.of(
                    new RpgleContent.CopyDirective("INCLUDE", "MYLIB/QRPGLESRC,COPYMBR2", new Location(3, 3))
            ));

            IrDocument doc = builder.build(content, fakeNormalized(3), "RPGLE");
            IrDocument.CopyMemberRef ref = doc.getDependencies().getCopyMembers().get(0);
            assertThat(ref.getDirective()).isEqualTo("INCLUDE");
            assertThat(ref.getLibraryName()).isEqualTo("MYLIB");
            assertThat(ref.getFileName()).isEqualTo("QRPGLESRC");
            assertThat(ref.getMemberName()).isEqualTo("COPYMBR2");
        }

        @Test
        void copyWithMemberOnly_parsedCorrectly() {
            RpgleContent content = emptyContent();
            content.setCopyDirectives(List.of(
                    new RpgleContent.CopyDirective("COPY", "SIMPLEMEM", new Location(1, 1))
            ));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            IrDocument.CopyMemberRef ref = doc.getDependencies().getCopyMembers().get(0);
            assertThat(ref.getMemberName()).isEqualTo("SIMPLEMEM");
            assertThat(ref.getFileName()).isNull();
            assertThat(ref.getLibraryName()).isNull();
        }

        @Test
        void copyWithFileSlashMember_parsedCorrectly() {
            RpgleContent content = emptyContent();
            content.setCopyDirectives(List.of(
                    new RpgleContent.CopyDirective("COPY", "MYFILE/MYMBR", new Location(1, 1))
            ));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            IrDocument.CopyMemberRef ref = doc.getDependencies().getCopyMembers().get(0);
            assertThat(ref.getFileName()).isEqualTo("MYFILE");
            assertThat(ref.getMemberName()).isEqualTo("MYMBR");
        }

        @Test
        void copyMember_notResolved() {
            RpgleContent content = emptyContent();
            content.setCopyDirectives(List.of(
                    new RpgleContent.CopyDirective("COPY", "MEMBER", new Location(1, 1))
            ));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getDependencies().getCopyMembers().get(0).isResolved()).isFalse();
        }

        @Test
        void multipleCopyDirectives_allExtracted() {
            RpgleContent content = emptyContent();
            content.setCopyDirectives(List.of(
                    new RpgleContent.CopyDirective("COPY", "QRPGLESRC,MBR1", new Location(1, 1)),
                    new RpgleContent.CopyDirective("INCLUDE", "LIB/FILE,MBR2", new Location(2, 2)),
                    new RpgleContent.CopyDirective("COPY", "SINGLEMBR", new Location(3, 3))
            ));

            IrDocument doc = builder.build(content, fakeNormalized(3), "RPGLE");
            assertThat(doc.getDependencies().getCopyMembers()).hasSize(3);
        }

        @Test
        void copyDirective_locationPreserved() {
            RpgleContent content = emptyContent();
            content.setCopyDirectives(List.of(
                    new RpgleContent.CopyDirective("COPY", "MBR1", new Location(7, 7))
            ));

            IrDocument doc = builder.build(content, fakeNormalized(7), "RPGLE");
            assertThat(doc.getDependencies().getCopyMembers().get(0).getLocation()
                    .getStartLine()).isEqualTo(7);
        }

        @Test
        void copyDirective_blankPath_handledGracefully() {
            RpgleContent content = emptyContent();
            content.setCopyDirectives(List.of(
                    new RpgleContent.CopyDirective("COPY", "   ", new Location(1, 1))
            ));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            // Should still produce a CopyMemberRef but with null fields
            assertThat(doc.getDependencies().getCopyMembers()).hasSize(1);
        }
    }

    // =========================================================================
    // Metadata population
    // =========================================================================

    @Nested
    class MetadataPopulation {

        @Test
        void metadata_irVersion_isSet() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getMetadata().getIrVersion()).isEqualTo("1.0.0");
        }

        @Test
        void metadata_sourceType_RPGLEIfRpgle() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("RPGLE");
        }

        @Test
        void metadata_sourceType_SQLRPGLEIfSqlrpgle() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "SQLRPGLE");
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("SQLRPGLE");
        }

        @Test
        void parseInfo_parsedAt_isNotNull() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getParsedAt()).isNotNull();
        }

        @Test
        void parseInfo_totalLines_matchesNormalized() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(42), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getTotalLines()).isEqualTo(42);
        }

        @Test
        void parseInfo_parserVersion_isSet() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getParserVersion()).isNotNull();
        }

        @Test
        void parseStatus_complete_whenNoErrors() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getParseStatus()).isEqualTo("complete");
        }

        @Test
        void parseStatus_partial_whenHasErrors() {
            RpgleContent content = emptyContent();
            content.setParseErrors(List.of(
                    new ParseError(1, 0, "Something went wrong", ParseError.Severity.ERROR)
            ));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getParseStatus()).isEqualTo("partial");
        }

        @Test
        void parseStatus_complete_whenOnlyWarnings() {
            RpgleContent content = emptyContent();
            content.setParseErrors(List.of(
                    new ParseError(1, 0, "Minor issue", ParseError.Severity.WARNING)
            ));

            IrDocument doc = builder.build(content, fakeNormalized(1), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getParseStatus()).isEqualTo("complete");
        }
    }

    // =========================================================================
    // Parse errors propagation
    // =========================================================================

    @Nested
    class ParseErrorPropagation {

        @Test
        void noParseErrors_emptyErrorsList() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getErrors()).isEmpty();
        }

        @Test
        void parseErrors_copiedToDocument() {
            RpgleContent content = emptyContent();
            content.setParseErrors(List.of(
                    new ParseError(5, 10, "Error message", ParseError.Severity.ERROR)
            ));

            IrDocument doc = builder.build(content, fakeNormalized(5), "RPGLE");
            assertThat(doc.getErrors()).hasSize(1);
            assertThat(doc.getErrors().get(0).getMessage()).isEqualTo("Error message");
        }

        @Test
        void multipleParseErrors_allCopied() {
            RpgleContent content = emptyContent();
            content.setParseErrors(List.of(
                    new ParseError(1, 0, "Error 1", ParseError.Severity.ERROR),
                    new ParseError(5, 0, "Warning 1", ParseError.Severity.WARNING)
            ));

            IrDocument doc = builder.build(content, fakeNormalized(5), "RPGLE");
            assertThat(doc.getErrors()).hasSize(2);
        }

        @Test
        void errorSeverity_propagatesToParseInfoErrors() {
            // ERROR-severity items must appear in parseInfo.errors (not just doc.errors)
            RpgleContent content = emptyContent();
            content.setParseErrors(List.of(
                    new ParseError(10, 5, "Syntax error on line 10", ParseError.Severity.ERROR)
            ));

            IrDocument doc = builder.build(content, fakeNormalized(10), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getErrors()).isNotEmpty();
            assertThat(doc.getMetadata().getParseInfo().getWarnings()).isEmpty();
        }

        @Test
        void warningSeverity_propagatesToParseInfoWarnings() {
            // WARNING-severity items must appear in parseInfo.warnings (not errors)
            RpgleContent content = emptyContent();
            content.setParseErrors(List.of(
                    new ParseError(3, 0, "Unknown opcode", ParseError.Severity.WARNING)
            ));

            IrDocument doc = builder.build(content, fakeNormalized(5), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getWarnings()).isNotEmpty();
            assertThat(doc.getMetadata().getParseInfo().getErrors()).isEmpty();
        }

        @Test
        void mixedSeverity_separatedCorrectly() {
            // ERROR → parseInfo.errors, WARNING → parseInfo.warnings, both in same run
            RpgleContent content = emptyContent();
            content.setParseErrors(List.of(
                    new ParseError(1, 0, "Fatal error", ParseError.Severity.ERROR),
                    new ParseError(2, 0, "Minor warning", ParseError.Severity.WARNING),
                    new ParseError(3, 0, "Another error", ParseError.Severity.ERROR)
            ));

            IrDocument doc = builder.build(content, fakeNormalized(5), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getErrors()).hasSize(2);
            assertThat(doc.getMetadata().getParseInfo().getWarnings()).hasSize(1);
        }

        @Test
        void noErrors_parseInfoErrorsAndWarningsEmpty() {
            IrDocument doc = builder.build(emptyContent(), fakeNormalized(1), "RPGLE");
            assertThat(doc.getMetadata().getParseInfo().getErrors()).isEmpty();
            assertThat(doc.getMetadata().getParseInfo().getWarnings()).isEmpty();
        }
    }
}
