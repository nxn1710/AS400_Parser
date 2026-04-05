package com.as400parser.cl;

import com.as400parser.cl.model.*;
import com.as400parser.common.cli.As400ParserCli;
import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for the CL parser — end-to-end pipeline.
 * <p>
 * Covers {@link ClParserFacade} (parse-from-string and parse-from-file),
 * metadata correctness, dependency extraction, and CLI registration.
 */
class ClParserIntegrationTest {

    private static final Path FIXTURE_DIR =
            Path.of("src/test/resources/cl").toAbsolutePath();
    private static final Path MNUCL_PATH =
            Path.of("D:/Code/AS400_Parser/rpg3-student-mgmt/QCLSRC/MNUCL.clle");

    private final ClParserFacade parser = new ClParserFacade();
    private final ParseOptions opts = ParseOptions.defaults();

    // =========================================================================
    // As400Parser interface contract
    // =========================================================================

    @Nested
    class InterfaceContract {

        @Test
        void getSourceType_returnsCL() {
            assertThat(parser.getSourceType()).isEqualTo("CL");
        }

        @Test
        void getSupportedExtensions_containsAllThree() {
            List<String> exts = parser.getSupportedExtensions();
            assertThat(exts).containsExactlyInAnyOrder(".cl", ".clp", ".clle");
        }

        @Test
        void getSupportedExtensions_doesNotContainClmod() {
            assertThat(parser.getSupportedExtensions()).doesNotContain(".clmod");
        }
    }

    // =========================================================================
    // Parse from String
    // =========================================================================

    @Nested
    class ParseFromString {

        @Test
        void simpleProgram_parsesWithoutError() {
            String source = "             PGM\n             CALL       PGM(MYPGM)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc).isNotNull();
            assertThat(doc.getMetadata()).isNotNull();
            assertThat(doc.getContent()).isInstanceOf(ClContent.class);
        }

        @Test
        void parseStatus_isComplete() {
            IrDocument doc = parser.parse("             PGM\n             ENDPGM\n", opts);
            assertThat(doc.getMetadata().getParseInfo().getParseStatus()).isEqualTo("complete");
        }

        @Test
        void sourceType_defaultsToCLLE_forStringInput() {
            IrDocument doc = parser.parse("             PGM\n             ENDPGM\n", opts);
            // String-based parse defaults to CLLE (no extension to detect from)
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("CLLE");
        }

        @Test
        void parseErrors_emptyOnValidInput() {
            IrDocument doc = parser.parse("             PGM\n             ENDPGM\n", opts);
            assertThat(doc.getErrors()).isEmpty();
        }

        @Test
        void dependencies_notNull() {
            IrDocument doc = parser.parse("             PGM\n             ENDPGM\n", opts);
            assertThat(doc.getDependencies()).isNotNull();
            assertThat(doc.getDependencies().getCalledPrograms()).isNotNull();
            assertThat(doc.getDependencies().getReferencedFiles()).isNotNull();
            assertThat(doc.getDependencies().getCopyMembers()).isNotNull();
        }
    }

    // =========================================================================
    // Parse from Path — fixture files
    // =========================================================================

    @Nested
    class ParseFromFile {

        @Test
        void simpleClFile_sourceTypeIsCL() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple.cl"), opts);
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("CL");
        }

        @Test
        void simpleClFile_memberNameIsSimple() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple.cl"), opts);
            assertThat(doc.getMetadata().getSourceMember()).isEqualTo("SIMPLE");
        }

        @Test
        void monmsgClpFile_sourceTypeIsCLP() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("monmsg.clp"), opts);
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("CLP");
        }

        @Test
        void declarationsClleFile_sourceTypeIsCLLE() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("declarations.clle"), opts);
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("CLLE");
        }

        @Test
        void allFixtures_parseSuccessfully() {
            String[] fixtures = {
                "simple.cl", "declarations.clle", "monmsg.clp",
                "continuation.clle", "subroutines.clle", "controlflow.clle"
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
            IrDocument doc = parser.parse("             PGM\n", opts);
            assertThat(doc.getMetadata().getParseInfo().getParsedAt()).isNotNull();
        }

        @Test
        void parseInfo_parserVersion() {
            IrDocument doc = parser.parse("             PGM\n", opts);
            assertThat(doc.getMetadata().getParseInfo().getParserVersion()).isNotNull();
        }

        @Test
        void parseInfo_totalLines_matchesInput() {
            IrDocument doc = parser.parse("             PGM\n             ENDPGM\n", opts);
            assertThat(doc.getMetadata().getParseInfo().getTotalLines()).isGreaterThanOrEqualTo(2);
        }

        @Test
        void fileMetadata_memberAndFilePopulated() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("simple.cl"), opts);
            assertThat(doc.getMetadata().getSourceMember()).isEqualTo("SIMPLE");
            assertThat(doc.getMetadata().getSourceFile()).isEqualTo("cl");
        }
    }

    // =========================================================================
    // Dependency extraction
    // =========================================================================

    @Nested
    class DependencyExtraction {

        @Test
        void callPgm_extractedAsCalledProgram() {
            String source = "             PGM\n             CALL       PGM(MYLIB/MYPGM)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc.getDependencies().getCalledPrograms()).hasSize(1);
            assertThat(doc.getDependencies().getCalledPrograms().get(0).getName())
                    .isEqualTo("MYLIB/MYPGM");
            assertThat(doc.getDependencies().getCalledPrograms().get(0).getReferenceType())
                    .isEqualTo("call");
        }

        @Test
        void multipleCallPgm_allExtracted() {
            String source = "             PGM\n" +
                            "             CALL       PGM(PGM1)\n" +
                            "             CALL       PGM(PGM2)\n" +
                            "             CALL       PGM(PGM3)\n" +
                            "             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc.getDependencies().getCalledPrograms()).hasSize(3);
        }

        @Test
        void dclf_extractedAsFileDeclaration() {
            String source = "             PGM\n             DCLF       FILE(STUDNTPF)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            List<IrDocument.DependencyRef> refs = doc.getDependencies().getReferencedFiles();
            boolean hasStudntpf = refs.stream()
                    .anyMatch(r -> "STUDNTPF".equals(r.getName()) && "file-declaration".equals(r.getReferenceType()));
            assertThat(hasStudntpf).isTrue();
        }

        @Test
        void dclf_notDuplicated() {
            // Only one DCLF → exactly one "file-declaration" entry
            String source = "             PGM\n             DCLF       FILE(STUDNTPF)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            long count = doc.getDependencies().getReferencedFiles().stream()
                    .filter(r -> "file-declaration".equals(r.getReferenceType()) && "STUDNTPF".equals(r.getName()))
                    .count();
            assertThat(count).isEqualTo(1);
        }

        @Test
        void ovrdbf_extractedAsFileOverride() {
            String source = "             PGM\n             OVRDBF     FILE(STUDNTPF) TOFILE(STULIB/STUDNTPF)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            boolean hasOverride = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> "file-override".equals(r.getReferenceType()));
            assertThat(hasOverride).isTrue();
        }

        @Test
        void ovrdbf_toFileUsedAsReference() {
            String source = "             PGM\n             OVRDBF     FILE(STUDNTPF) TOFILE(STULIB/STUDNTPF)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            // TOFILE takes priority
            boolean hasToFile = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> "STULIB/STUDNTPF".equals(r.getName()));
            assertThat(hasToFile).isTrue();
        }

        @Test
        void sndpgmmsg_msgf_extractedAsMessageFile() {
            String source = "             PGM\n             SNDPGMMSG  MSGID(CPF9898) MSGF(QCPFMSG)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            boolean hasMsgFile = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> "QCPFMSG".equals(r.getName()) && "message-file".equals(r.getReferenceType()));
            assertThat(hasMsgFile).isTrue();
        }

        @Test
        void addlible_extractedAsLibrary() {
            String source = "             PGM\n             ADDLIBLE   LIB(STULIB)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            boolean hasLib = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> "STULIB".equals(r.getName()) && "library".equals(r.getReferenceType()));
            assertThat(hasLib).isTrue();
        }

        @Test
        void rmvlible_extractedAsLibraryRemove() {
            String source = "             PGM\n             RMVLIBLE   LIB(STULIB)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            boolean hasLib = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> "STULIB".equals(r.getName()) && "library-remove".equals(r.getReferenceType()));
            assertThat(hasLib).isTrue();
        }

        @Test
        void callInSubroutine_extractedAsCalledProgram() {
            String source = "             PGM\n" +
                            " SUB:       SUBR\n" +
                            "             CALL       PGM(SUBPGM)\n" +
                            "             ENDSUBR\n" +
                            "             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            boolean hasSubpgm = doc.getDependencies().getCalledPrograms().stream()
                    .anyMatch(r -> "SUBPGM".equals(r.getName()));
            assertThat(hasSubpgm).isTrue();
        }

        @Test
        void monmsgExec_callExtracted() {
            String source = "             PGM\n" +
                            "             MONMSG     MSGID(CPF0000) EXEC(GOTO CMDLBL(ERROR))\n" +
                            "             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            // GOTO is not a program call — no calledPrograms from it
            assertThat(doc.getDependencies().getCalledPrograms()).isEmpty();
        }

        @Test
        void copyMembers_alwaysEmpty() {
            IrDocument doc = parser.parse("             PGM\n             ENDPGM\n", opts);
            assertThat(doc.getDependencies().getCopyMembers()).isEmpty();
        }

        @Test
        void dltovr_withStarAll_filteredFromDependencies() {
            // *ALL is a special value (not a real file name) and must not create a dependency ref
            String source = "             PGM\n             DLTOVR     FILE(*ALL)\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            long starAllRefs = doc.getDependencies().getReferencedFiles().stream()
                    .filter(r -> "*ALL".equals(r.getName()))
                    .count();
            assertThat(starAllRefs).isEqualTo(0);
        }

        @Test
        void unterminatedComment_warningInParseInfo() {
            // An unterminated /* block comment must generate a WARNING in parseInfo.warnings
            String source = "             PGM\n/* This comment has no closing\n             ENDPGM\n";
            IrDocument doc = parser.parse(source, opts);
            assertThat(doc.getMetadata().getParseInfo().getWarnings()).isNotEmpty();
        }
    }

    // =========================================================================
    // Fixture: declarations.clle
    // =========================================================================

    @Nested
    class DeclarationsFixture {

        @Test
        void declarationsFile_hasVariables() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("declarations.clle"), opts);
            ClContent content = (ClContent) doc.getContent();
            assertThat(content.getVariables()).hasSizeGreaterThanOrEqualTo(4);
        }

        @Test
        void declarationsFile_hasFileDeclarations() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("declarations.clle"), opts);
            ClContent content = (ClContent) doc.getContent();
            assertThat(content.getFileDeclarations()).hasSize(2);
        }

        @Test
        void declarationsFile_charVariable() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("declarations.clle"), opts);
            ClContent content = (ClContent) doc.getContent();
            ClVariable mode = content.getVariables().stream()
                    .filter(v -> "&MODE".equals(v.getName()))
                    .findFirst().orElse(null);
            assertThat(mode).isNotNull();
            assertThat(mode.getType()).isEqualTo("*CHAR");
            assertThat(mode.getLength()).isEqualTo(1);
        }

        @Test
        void declarationsFile_variableWithValue() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("declarations.clle"), opts);
            ClContent content = (ClContent) doc.getContent();
            ClVariable msg = content.getVariables().stream()
                    .filter(v -> "&MSG".equals(v.getName()))
                    .findFirst().orElse(null);
            assertThat(msg).isNotNull();
            assertThat(msg.getInitialValue()).isEqualTo("'INITIAL'");
        }
    }

    // =========================================================================
    // Fixture: continuation.clle
    // =========================================================================

    @Nested
    class ContinuationFixture {

        @Test
        void continuationFile_commandsJoinedCorrectly() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("continuation.clle"), opts);
            ClContent content = (ClContent) doc.getContent();
            // SNDPGMMSG (3 lines), OVRDBF (3 lines), CALL (2 lines) = 3 commands
            assertThat(content.getCommands()).hasSize(3);
        }

        @Test
        void continuationFile_sndpgmmsg_hasAllParams() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("continuation.clle"), opts);
            ClContent content = (ClContent) doc.getContent();
            ClCommand snd = content.getCommands().stream()
                    .filter(c -> "SNDPGMMSG".equalsIgnoreCase(c.getName()))
                    .findFirst().orElse(null);
            assertThat(snd).isNotNull();
            assertThat(snd.getParameters()).hasSizeGreaterThanOrEqualTo(3);
        }
    }

    // =========================================================================
    // Fixture: subroutines.clle
    // =========================================================================

    @Nested
    class SubroutinesFixture {

        @Test
        void subroutinesFile_hasTwoSubroutines() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("subroutines.clle"), opts);
            ClContent content = (ClContent) doc.getContent();
            assertThat(content.getSubroutines()).hasSize(2);
        }

        @Test
        void subroutinesFile_processSubrHasCall() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("subroutines.clle"), opts);
            ClContent content = (ClContent) doc.getContent();
            ClSubroutine process = content.getSubroutines().stream()
                    .filter(s -> "PROCESS".equals(s.getName()))
                    .findFirst().orElse(null);
            assertThat(process).isNotNull();
            assertThat(process.getCommands()).hasSizeGreaterThanOrEqualTo(1);
        }

        @Test
        void subroutinesFile_callInSubrExtractedAsDependency() {
            IrDocument doc = parser.parse(FIXTURE_DIR.resolve("subroutines.clle"), opts);
            boolean hasSubpgmCall = doc.getDependencies().getCalledPrograms().stream()
                    .anyMatch(r -> "PROCPGM".equals(r.getName()));
            assertThat(hasSubpgmCall).isTrue();
        }
    }

    // =========================================================================
    // CLI — selectParser integration
    // =========================================================================

    @Nested
    class CliRegistration {

        @Test
        void selectParser_returnsClParserForDotCl() {
            As400Parser selected = As400ParserCli.selectParser(Path.of("MYPGM.cl"));
            assertThat(selected).isInstanceOf(ClParserFacade.class);
        }

        @Test
        void selectParser_returnsClParserForDotClp() {
            As400Parser selected = As400ParserCli.selectParser(Path.of("MYPGM.clp"));
            assertThat(selected).isInstanceOf(ClParserFacade.class);
        }

        @Test
        void selectParser_returnsClParserForDotClle() {
            As400Parser selected = As400ParserCli.selectParser(Path.of("MYPGM.clle"));
            assertThat(selected).isInstanceOf(ClParserFacade.class);
        }

        @Test
        void selectParser_doesNotReturnClParserForDotClmod() {
            As400Parser selected = As400ParserCli.selectParser(Path.of("MYPGM.clmod"));
            assertThat(selected).isNotInstanceOf(ClParserFacade.class);
        }
    }

    // =========================================================================
    // Integration: MNUCL.clle — real CL program
    // =========================================================================

    @Nested
    class MnuclIntegration {

        private IrDocument parseMnucl() {
            return parser.parse(MNUCL_PATH, opts);
        }

        @Test
        void mnucl_parsesWithoutError() {
            IrDocument doc = parseMnucl();
            assertThat(doc).isNotNull();
            assertThat(doc.getMetadata().getParseInfo().getParseStatus()).isEqualTo("complete");
        }

        @Test
        void mnucl_metadata_sourceTypeIsCLLE() {
            IrDocument doc = parseMnucl();
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("CLLE");
            assertThat(doc.getMetadata().getSourceMember()).isEqualTo("MNUCL");
            assertThat(doc.getMetadata().getSourceFile()).isEqualTo("QCLSRC");
        }

        @Test
        void mnucl_hasComments() {
            IrDocument doc = parseMnucl();
            ClContent content = (ClContent) doc.getContent();
            assertThat(content.getComments()).isNotEmpty();
        }

        @Test
        void mnucl_hasLabel_ERROR() {
            IrDocument doc = parseMnucl();
            ClContent content = (ClContent) doc.getContent();
            boolean hasError = content.getLabels().stream()
                    .anyMatch(l -> "ERROR".equals(l.getName()));
            assertThat(hasError).isTrue();
        }

        @Test
        void mnucl_hasLabel_ENDPGM() {
            IrDocument doc = parseMnucl();
            ClContent content = (ClContent) doc.getContent();
            boolean hasEndpgm = content.getLabels().stream()
                    .anyMatch(l -> "ENDPGM".equals(l.getName()));
            assertThat(hasEndpgm).isTrue();
        }

        @Test
        void mnucl_calledPrograms_includesMnuprg() {
            IrDocument doc = parseMnucl();
            boolean hasMnuprg = doc.getDependencies().getCalledPrograms().stream()
                    .anyMatch(r -> r.getName().contains("MNUPRG"));
            assertThat(hasMnuprg).isTrue();
        }

        @Test
        void mnucl_referencedFiles_includesOvrdbfTargets() {
            IrDocument doc = parseMnucl();
            // OVRDBF for STUDNTPF → TOFILE(STULIB/STUDNTPF)
            boolean hasStudntpf = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> r.getName().contains("STUDNTPF"));
            assertThat(hasStudntpf).isTrue();
        }

        @Test
        void mnucl_addlible_stulib_extractedAsLibrary() {
            IrDocument doc = parseMnucl();
            boolean hasStulib = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> "STULIB".equals(r.getName()) && "library".equals(r.getReferenceType()));
            assertThat(hasStulib).isTrue();
        }

        @Test
        void mnucl_rmvlible_stulib_extractedAsLibraryRemove() {
            IrDocument doc = parseMnucl();
            boolean hasRemove = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> "STULIB".equals(r.getName()) && "library-remove".equals(r.getReferenceType()));
            assertThat(hasRemove).isTrue();
        }

        @Test
        void mnucl_sndpgmmsg_msgfQcpfmsg_extracted() {
            IrDocument doc = parseMnucl();
            // SNDPGMMSG MSGF(QCPFMSG) — from multi-line continuation
            boolean hasQcpfmsg = doc.getDependencies().getReferencedFiles().stream()
                    .anyMatch(r -> "QCPFMSG".equals(r.getName()) && "message-file".equals(r.getReferenceType()));
            assertThat(hasQcpfmsg).isTrue();
        }

        @Test
        void mnucl_hasCommandsCount() {
            IrDocument doc = parseMnucl();
            ClContent content = (ClContent) doc.getContent();
            // MNUCL has MONMSG, ADDLIBLE, multiple OVRDBF, CALL, DLTOVR, RMVLIBLE, GOTO, SNDPGMMSG, ENDPGM
            assertThat(content.getCommands()).hasSizeGreaterThan(5);
        }

        @Test
        void mnucl_sourceLines_allHaveLineNumbers() {
            IrDocument doc = parseMnucl();
            ClContent content = (ClContent) doc.getContent();
            content.getSourceLines().forEach(line ->
                assertThat(line.getLineNumber()).as("line number on %s", line).isPositive()
            );
        }

        @Test
        void mnucl_fullPipelineDocumentStructure() {
            IrDocument doc = parseMnucl();
            // Full IrDocument envelope is valid
            assertThat(doc.getMetadata()).isNotNull();
            assertThat(doc.getContent()).isInstanceOf(ClContent.class);
            assertThat(doc.getDependencies()).isNotNull();
            assertThat(doc.getErrors()).isNotNull();
        }
    }
}
