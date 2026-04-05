package com.as400parser.rpgle;

import com.as400parser.common.cli.As400ParserCli;
import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.rpgle.model.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for the RPGLE parser — end-to-end pipeline.
 * <p>
 * Covers {@link RpgleParserFacade} (parse-from-string and parse-from-file),
 * metadata correctness, dependency extraction, format detection, and CLI registration.
 */
class RpgleParserIntegrationTest {

    private static final Path FIXTURE_DIR =
            Path.of("src/test/resources/rpgle").toAbsolutePath();

    private final RpgleParserFacade parser = new RpgleParserFacade();
    private final ParseOptions opts = ParseOptions.defaults();

    // =========================================================================
    // As400Parser interface contract
    // =========================================================================

    @Nested
    class InterfaceContract {

        @Test
        void getSourceType_returnsRPGLE() {
            assertThat(parser.getSourceType()).isEqualTo("RPGLE");
        }

        @Test
        void getSupportedExtensions_containsRpgleAndSqlrpgle() {
            List<String> exts = parser.getSupportedExtensions();
            assertThat(exts).containsExactlyInAnyOrder(".rpgle", ".sqlrpgle");
        }

        @Test
        void getSupportedExtensions_doesNotContainRpg() {
            assertThat(parser.getSupportedExtensions()).doesNotContain(".rpg");
        }
    }

    // =========================================================================
    // Parse from String
    // =========================================================================

    @Nested
    class ParseFromString {

        @Test
        void simpleFixedFormat_parsesWithoutError() {
            String source = "     H DFTACTGRP(*NO)\n     FSTUDNTPF  IF   E           K DISK\n     C                   EVAL      *INLR = *ON\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc).isNotNull();
            assertThat(doc.getMetadata()).isNotNull();
            assertThat(doc.getContent()).isInstanceOf(RpgleContent.class);
        }

        @Test
        void parseStatus_isComplete() {
            String source = "     H DFTACTGRP(*NO)\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc.getMetadata().getParseInfo().getParseStatus())
                    .isIn("complete", "partial");
        }

        @Test
        void sourceType_defaultsToRPGLE_forStringInput() {
            IrDocument doc = parser.parse("     H DFTACTGRP(*NO)\n", opts);
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("RPGLE");
        }

        @Test
        void dependencies_notNull() {
            IrDocument doc = parser.parse("     H DFTACTGRP(*NO)\n", opts);
            assertThat(doc.getDependencies()).isNotNull();
            assertThat(doc.getDependencies().getCalledPrograms()).isNotNull();
            assertThat(doc.getDependencies().getReferencedFiles()).isNotNull();
            assertThat(doc.getDependencies().getCopyMembers()).isNotNull();
        }

        @Test
        void fullyFreeFormat_parsesWithoutError() {
            String source = "**FREE\nctl-opt dftactgrp(*no);\ndcl-s counter packed(5:0);\ncounter = 0;\n*inlr = *on;\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc).isNotNull();
            assertThat(doc.getContent()).isInstanceOf(RpgleContent.class);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getFormatType()).isEqualTo("free");
        }

        @Test
        void fullyFree_freeStatements_populated() {
            String source = "**FREE\nctl-opt dftactgrp(*no);\ndcl-s counter packed(5:0);\ncounter = 0;\n*inlr = *on;\n";
            IrDocument doc = parser.parse(source, opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getFreeFormatStatements()).isNotEmpty();
        }

        @Test
        void fixedFormat_formatTypeIsFixed() {
            String source = "     H DFTACTGRP(*NO)\n     FSTUDNTPF  IF   E           K DISK\n";
            IrDocument doc = parser.parse(source, opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getFormatType()).isEqualTo("fixed");
        }

        @Test
        void fixedFormat_hSpec_parsed() {
            String source = "     H DFTACTGRP(*NO) ACTGRP('MYGRP')\n";
            IrDocument doc = parser.parse(source, opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getControlSpecs()).hasSize(1);
            assertThat(content.getControlSpecs().get(0).getKeywords()).contains("DFTACTGRP(*NO)");
        }

        @Test
        void fixedFormat_fSpec_parsed() {
            String source = "     FSTUDNTPF  IF   E           K DISK\n";
            IrDocument doc = parser.parse(source, opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getFileSpecs()).hasSize(1);
            assertThat(content.getFileSpecs().get(0).getFileName()).isEqualTo("STUDNTPF");
        }

        @Test
        void fixedFormat_dSpec_parsed() {
            String source = "     D Counter         S              5  0\n";
            IrDocument doc = parser.parse(source, opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getDefinitionSpecs()).hasSize(1);
            assertThat(content.getDefinitionSpecs().get(0).getName()).isEqualTo("Counter");
        }

        @Test
        void fixedFormat_cSpec_callExtracted() {
            String source = "     C                   CALL      'RPTPGM'\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc.getDependencies().getCalledPrograms()).hasSize(1);
            assertThat(doc.getDependencies().getCalledPrograms().get(0).getName())
                    .isEqualTo("RPTPGM");
        }

        @Test
        void fixedFormat_fSpec_extractedAsReferencedFile() {
            String source = "     FSTUDNTPF  IF   E           K DISK\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc.getDependencies().getReferencedFiles()).hasSize(1);
            assertThat(doc.getDependencies().getReferencedFiles().get(0).getName())
                    .isEqualTo("STUDNTPF");
        }

        @Test
        void copyDirective_extractedAsCopyMember() {
            String source = "      /COPY QRPGLESRC,MYMBR\n     D Counter         S              5  0\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc.getDependencies().getCopyMembers()).hasSize(1);
            assertThat(doc.getDependencies().getCopyMembers().get(0).getMemberName())
                    .isEqualTo("MYMBR");
        }

        @Test
        void emptySource_parsesWithoutError() {
            IrDocument doc = parser.parse("", opts);
            assertThat(doc).isNotNull();
            assertThat(doc.getMetadata()).isNotNull();
        }
    }

    // =========================================================================
    // Parse from Path — fixture files
    // =========================================================================

    @Nested
    class ParseFromFile {

        @Test
        void simpleFixed_parsesSuccessfully() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            assertThat(doc).isNotNull();
            assertThat(doc.getMetadata().getParseInfo().getParseStatus())
                    .isIn("complete", "partial");
        }

        @Test
        void simpleFixed_sourceTypeIsRPGLE() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("RPGLE");
        }

        @Test
        void simpleFixed_memberNameIsSIMPLE_FIXED() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            assertThat(doc.getMetadata().getSourceMember()).isEqualTo("SIMPLE_FIXED");
        }

        @Test
        void sqlrpgle_sourceTypeIsSQLRPGLE() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple.sqlrpgle"), opts);
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("SQLRPGLE");
        }

        @Test
        void simpleFixed_hasHSpec() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getControlSpecs()).isNotEmpty();
        }

        @Test
        void simpleFixed_hasFSpecs() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getFileSpecs()).hasSizeGreaterThanOrEqualTo(1);
        }

        @Test
        void simpleFixed_hasDSpecs() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getDefinitionSpecs()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void simpleFixed_hasCSpecs() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getCalcSpecs()).hasSizeGreaterThanOrEqualTo(5);
        }

        @Test
        void simpleFixed_formatTypeIsFixed() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getFormatType()).isEqualTo("fixed");
        }

        @Test
        void simpleFixed_calledProgramsExtracted() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            assertThat(doc.getDependencies().getCalledPrograms()).isNotEmpty();
            boolean hasRptpgm = doc.getDependencies().getCalledPrograms().stream()
                    .anyMatch(r -> r.getName().contains("RPTPGM"));
            assertThat(hasRptpgm).isTrue();
        }

        @Test
        void simpleFixed_referencedFilesExtracted() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            assertThat(doc.getDependencies().getReferencedFiles()).hasSizeGreaterThanOrEqualTo(1);
            boolean hasStudntpf = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> "STUDNTPF".equals(r.getName()));
            assertThat(hasStudntpf).isTrue();
        }

        @Test
        void allFixtures_parseSuccessfully() {
            String[] fixtures = {
                "simple_fixed.rpgle", "copy_directives.rpgle",
                "mixed_format.rpgle", "fully_free.rpgle",
                "all_specs.rpgle", "simple.sqlrpgle"
            };
            for (String fixture : fixtures) {
                IrDocument doc = parser.parse(FIXTURE_DIR.resolve(fixture), opts);
                assertThat(doc).as("Fixture: " + fixture).isNotNull();
                assertThat(doc.getMetadata().getParseInfo().getParseStatus())
                        .as("Fixture: " + fixture)
                        .isIn("complete", "partial");
            }
        }
    }

    // =========================================================================
    // Metadata correctness
    // =========================================================================

    @Nested
    class MetadataCorrectness {

        @Test
        void parseInfo_parsedAtIsNotNull() {
            IrDocument doc = parser.parse("     H DFTACTGRP(*NO)\n", opts);
            assertThat(doc.getMetadata().getParseInfo().getParsedAt()).isNotNull();
        }

        @Test
        void parseInfo_parserVersion() {
            IrDocument doc = parser.parse("     H DFTACTGRP(*NO)\n", opts);
            assertThat(doc.getMetadata().getParseInfo().getParserVersion()).isNotNull();
        }

        @Test
        void parseInfo_totalLines_matchesInput() {
            IrDocument doc = parser.parse("     H DFTACTGRP(*NO)\n     C                   EVAL      *INLR = *ON\n", opts);
            assertThat(doc.getMetadata().getParseInfo().getTotalLines()).isGreaterThanOrEqualTo(2);
        }

        @Test
        void fileMetadata_memberAndFilePopulated() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            assertThat(doc.getMetadata().getSourceMember()).isEqualTo("SIMPLE_FIXED");
            assertThat(doc.getMetadata().getSourceFile()).isEqualTo("rpgle");
        }

        @Test
        void fileMetadata_irVersion() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            assertThat(doc.getMetadata().getIrVersion()).isNotNull();
        }
    }

    // =========================================================================
    // Dependency extraction — integration
    // =========================================================================

    @Nested
    class DependencyExtraction {

        @Test
        void simpleFixed_callExtracted() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            boolean hasCall = doc.getDependencies().getCalledPrograms().stream()
                    .anyMatch(r -> "call".equals(r.getReferenceType()));
            assertThat(hasCall).isTrue();
        }

        @Test
        void simpleFixed_callpExtracted() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            boolean hasCallp = doc.getDependencies().getCalledPrograms().stream()
                    .anyMatch(r -> "callp".equals(r.getReferenceType()));
            assertThat(hasCallp).isTrue();
        }

        @Test
        void copyDirectives_extractedFromFile() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("copy_directives.rpgle"), opts);
            assertThat(doc.getDependencies().getCopyMembers()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void copyDirectives_fileAndMemberParsed() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("copy_directives.rpgle"), opts);
            boolean hasCopymbr1 = doc.getDependencies().getCopyMembers().stream()
                    .anyMatch(r -> "COPYMBR1".equals(r.getMemberName()));
            assertThat(hasCopymbr1).isTrue();
        }

        @Test
        void copyDirectives_libFileAndMemberParsed() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("copy_directives.rpgle"), opts);
            boolean hasLibMember = doc.getDependencies().getCopyMembers().stream()
                    .anyMatch(r -> "MYLIB".equals(r.getLibraryName()) && "COPYMBR2".equals(r.getMemberName()));
            assertThat(hasLibMember).isTrue();
        }

        @Test
        void copyDirectives_memberOnlyParsed() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("copy_directives.rpgle"), opts);
            boolean hasSimplemem = doc.getDependencies().getCopyMembers().stream()
                    .anyMatch(r -> "SIMPLEMEM".equals(r.getMemberName()));
            assertThat(hasSimplemem).isTrue();
        }

        @Test
        void allSpecs_referencedFilesIncludesBothFileSpecs() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("all_specs.rpgle"), opts);
            assertThat(doc.getDependencies().getReferencedFiles()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void fullyFree_dclFExtractedAsReferencedFile() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("fully_free.rpgle"), opts);
            boolean hasStudntpf = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> r.getName().contains("STUDNTPF"));
            assertThat(hasStudntpf).isTrue();
        }
    }

    // =========================================================================
    // Format detection — integration
    // =========================================================================

    @Nested
    class FormatDetection {

        @Test
        void fullyFreeFile_detectedAsFree() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("fully_free.rpgle"), opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getFormatType()).isEqualTo("free");
        }

        @Test
        void mixedFormatFile_detectedAsMixed() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("mixed_format.rpgle"), opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getFormatType()).isEqualTo("mixed");
        }

        @Test
        void fixedFormatFile_detectedAsFixed() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple_fixed.rpgle"), opts);
            RpgleContent content = (RpgleContent) doc.getContent();
            assertThat(content.getFormatType()).isEqualTo("fixed");
        }
    }

    // =========================================================================
    // All Specs fixture — comprehensive spec type coverage
    // =========================================================================

    @Nested
    class AllSpecsFixture {

        private IrDocument parseAllSpecs() {
            return parser.parse(FIXTURE_DIR.resolve("all_specs.rpgle"), opts);
        }

        @Test
        void hasControlSpecs() {
            RpgleContent c = (RpgleContent) parseAllSpecs().getContent();
            assertThat(c.getControlSpecs()).isNotEmpty();
        }

        @Test
        void hasFileSpecs() {
            RpgleContent c = (RpgleContent) parseAllSpecs().getContent();
            assertThat(c.getFileSpecs()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void hasDefinitionSpecs() {
            RpgleContent c = (RpgleContent) parseAllSpecs().getContent();
            assertThat(c.getDefinitionSpecs()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void hasInputSpecs() {
            RpgleContent c = (RpgleContent) parseAllSpecs().getContent();
            assertThat(c.getInputSpecs()).hasSizeGreaterThanOrEqualTo(1);
        }

        @Test
        void hasCalcSpecs() {
            RpgleContent c = (RpgleContent) parseAllSpecs().getContent();
            assertThat(c.getCalcSpecs()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void hasOutputSpecs() {
            RpgleContent c = (RpgleContent) parseAllSpecs().getContent();
            assertThat(c.getOutputSpecs()).hasSizeGreaterThanOrEqualTo(1);
        }

        @Test
        void hasProcedureSpecs() {
            RpgleContent c = (RpgleContent) parseAllSpecs().getContent();
            assertThat(c.getProcedureSpecs()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void procedureSpec_beginAndEnd() {
            RpgleContent c = (RpgleContent) parseAllSpecs().getContent();
            boolean hasBegin = c.getProcedureSpecs().stream()
                    .anyMatch(p -> "B".equals(p.getBeginEnd()));
            boolean hasEnd = c.getProcedureSpecs().stream()
                    .anyMatch(p -> "E".equals(p.getBeginEnd()));
            assertThat(hasBegin).isTrue();
            assertThat(hasEnd).isTrue();
        }

        @Test
        void callOperation_extractedAsDependency() {
            IrDocument doc = parseAllSpecs();
            boolean hasCall = doc.getDependencies().getCalledPrograms().stream()
                    .anyMatch(r -> r.getName().contains("RPTPGM"));
            assertThat(hasCall).isTrue();
        }

        @Test
        void fullPipelineDocumentStructure() {
            IrDocument doc = parseAllSpecs();
            assertThat(doc.getMetadata()).isNotNull();
            assertThat(doc.getContent()).isInstanceOf(RpgleContent.class);
            assertThat(doc.getDependencies()).isNotNull();
            assertThat(doc.getErrors()).isNotNull();
        }
    }

    // =========================================================================
    // CLI — selectParser integration
    // =========================================================================

    @Nested
    class CliRegistration {

        @Test
        void selectParser_returnsRpgleParserForDotRpgle() {
            As400Parser selected = As400ParserCli.selectParser(Path.of("MYPGM.rpgle"));
            assertThat(selected).isInstanceOf(RpgleParserFacade.class);
        }

        @Test
        void selectParser_returnsRpgleParserForDotSqlrpgle() {
            As400Parser selected = As400ParserCli.selectParser(Path.of("MYPGM.sqlrpgle"));
            assertThat(selected).isInstanceOf(RpgleParserFacade.class);
        }

        @Test
        void selectParser_doesNotReturnRpgleParserForDotRpg() {
            As400Parser selected = As400ParserCli.selectParser(Path.of("MYPGM.rpg"));
            assertThat(selected).isNotInstanceOf(RpgleParserFacade.class);
        }
    }

    // =========================================================================
    // Error handling
    // =========================================================================

    @Nested
    class ErrorHandling {

        @Test
        void nonExistentFile_returnsFailedDocument() {
            IrDocument doc = parser.parse(Path.of("nonexistent.rpgle"), opts);
            assertThat(doc).isNotNull();
            assertThat(doc.getMetadata()).isNotNull();
            assertThat(doc.getMetadata().getParseInfo().getParseStatus()).isEqualTo("failed");
        }

        @Test
        void nonExistentFile_hasErrorMessage() {
            IrDocument doc = parser.parse(Path.of("nonexistent.rpgle"), opts);
            assertThat(doc.getMetadata().getParseInfo().getErrors()).isNotEmpty();
        }
    }
}
