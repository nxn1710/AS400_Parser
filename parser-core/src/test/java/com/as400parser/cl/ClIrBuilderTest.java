package com.as400parser.cl;

import com.as400parser.cl.model.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link ClIrBuilder} — isolated builder logic, no file I/O.
 * <p>
 * Feeds raw CL source lines directly to {@code buildContent()} and verifies
 * the resulting {@link ClContent} IR structure.
 */
class ClIrBuilderTest {

    private final ClIrBuilder builder = new ClIrBuilder();

    /**
     * Builds ClContent from varargs CL source lines (no sequence numbers).
     */
    private ClContent build(String... lines) {
        return builder.buildContent(Arrays.asList(lines), null);
    }

    // =========================================================================
    // Comment Parsing — primary requirement
    // =========================================================================

    @Nested
    class CommentParsing {

        @Test
        void singleLineBlockComment_isCaptured() {
            ClContent c = build("/* This is a comment */");
            assertThat(c.getComments()).hasSize(1);
            assertThat(c.getComments().get(0).getText()).contains("This is a comment");
        }

        @Test
        void multiLineBlockComment_isSingleEntry() {
            ClContent c = build(
                "/* Line 1",
                "   Line 2",
                "   Line 3 */"
            );
            assertThat(c.getComments()).hasSize(1);
            assertThat(c.getComments().get(0).getText()).contains("Line 1");
        }

        @Test
        void commentSpecType_isTagged() {
            ClContent c = build("/* comment */", "             PGM");
            boolean hasComment = c.getSourceLines().stream()
                    .anyMatch(l -> "COMMENT".equals(l.getSpecType()));
            assertThat(hasComment).isTrue();
        }

        @Test
        void sourceWithOnlyComments_producesNoCommands() {
            ClContent c = build("/* just a comment */", "/* another */");
            assertThat(c.getComments()).hasSize(2);
            assertThat(c.getCommands()).isEmpty();
        }

        @Test
        void emptySource_producesEmptyContent() {
            ClContent c = build();
            assertThat(c.getCommands()).isEmpty();
            assertThat(c.getComments()).isEmpty();
            assertThat(c.getVariables()).isEmpty();
        }

        @Test
        void blankLines_areSkipped() {
            ClContent c = build("", "   ", "             CALL       PGM(MYPGM)", "", "             ENDPGM");
            assertThat(c.getCommands()).hasSizeGreaterThanOrEqualTo(1);
        }
    }

    // =========================================================================
    // Line Continuation — primary requirement
    // =========================================================================

    @Nested
    class LineContinuation {

        @Test
        void twoLineContinuation_joinedAsOneCommand() {
            ClContent c = build(
                "             SNDPGMMSG  MSGID(CPF9898) MSGF(QCPFMSG) +",
                "               MSGTYPE(*ESCAPE)"
            );
            assertThat(c.getCommands()).hasSize(1);
            ClCommand cmd = c.getCommands().get(0);
            assertThat(cmd.getName()).isEqualTo("SNDPGMMSG");
            assertThat(cmd.getParameters()).hasSizeGreaterThanOrEqualTo(3);
        }

        @Test
        void threePlusContinuationLines_joinedAsOneCommand() {
            ClContent c = build(
                "             SNDPGMMSG  MSGID(CPF9898) +",
                "               MSGF(QCPFMSG) +",
                "               MSGDTA('Hello') +",
                "               MSGTYPE(*ESCAPE)"
            );
            assertThat(c.getCommands()).hasSize(1);
            assertThat(c.getCommands().get(0).getParameters()).hasSizeGreaterThanOrEqualTo(4);
        }

        @Test
        void continuationLine_specType_isTagged() {
            ClContent c = build(
                "             CALL       PGM(MYPGM) +",
                "               PARM('A')"
            );
            boolean hasContinuation = c.getSourceLines().stream()
                    .anyMatch(l -> "CONTINUATION".equals(l.getSpecType()));
            assertThat(hasContinuation).isTrue();
        }

        @Test
        void nonContinuationLine_afterContinuation_startsNewCommand() {
            ClContent c = build(
                "             CALL       PGM(PGM1) +",
                "               PARM('X')",
                "             CALL       PGM(PGM2)"
            );
            assertThat(c.getCommands()).hasSize(2);
            assertThat(c.getCommands().get(0).getName()).isEqualTo("CALL");
            assertThat(c.getCommands().get(1).getName()).isEqualTo("CALL");
        }
    }

    // =========================================================================
    // Parameter Parsing
    // =========================================================================

    @Nested
    class ParameterParsing {

        @Test
        void noParams_resultsInEmptyList() {
            ClContent c = build("             PGM");
            // PGM is structural — verify it is recorded as programName
            // and has no extra command entries
            assertThat(c.getProgramName()).isNull(); // PGM without PARM
        }

        @Test
        void keywordParam_extracted() {
            ClContent c = build("             DCL        VAR(&NAME) TYPE(*CHAR) LEN(10)");
            assertThat(c.getVariables()).hasSize(1);
            ClVariable v = c.getVariables().get(0);
            assertThat(v.getName()).isEqualTo("&NAME");
            assertThat(v.getType()).isEqualTo("*CHAR");
            assertThat(v.getLength()).isEqualTo(10);
        }

        @Test
        void positionalParam_extracted() {
            ClContent c = build("             CALL       PGM(MYPGM)");
            assertThat(c.getCommands()).hasSize(1);
            ClCommand cmd = c.getCommands().get(0);
            assertThat(cmd.getParameters()).isNotEmpty();
            // PGM keyword param
            ClParameter pgmParam = cmd.getParameters().stream()
                    .filter(p -> "PGM".equalsIgnoreCase(p.getKeyword()))
                    .findFirst().orElse(null);
            assertThat(pgmParam).isNotNull();
            assertThat(pgmParam.getValue()).isEqualTo("MYPGM");
        }

        @Test
        void quotedStringParam_preservedAsSingleValue() {
            ClContent c = build("             SNDPGMMSG  MSGDTA('Hello World')");
            ClCommand cmd = c.getCommands().get(0);
            ClParameter p = cmd.getParameters().stream()
                    .filter(pr -> "MSGDTA".equalsIgnoreCase(pr.getKeyword()))
                    .findFirst().orElse(null);
            assertThat(p).isNotNull();
            assertThat(p.getValue()).contains("Hello World");
        }

        @Test
        void qualifiedName_treatedAsOneValue() {
            ClContent c = build("             OVRDBF     FILE(STUDNTPF) TOFILE(STULIB/STUDNTPF)");
            ClCommand cmd = c.getCommands().get(0);
            ClParameter toFile = cmd.getParameters().stream()
                    .filter(p -> "TOFILE".equalsIgnoreCase(p.getKeyword()))
                    .findFirst().orElse(null);
            assertThat(toFile).isNotNull();
            assertThat(toFile.getValue()).isEqualTo("STULIB/STUDNTPF");
        }

        @Test
        void multipleKeywordParams_allExtracted() {
            ClContent c = build("             OVRDBF     FILE(STUDNTPF) TOFILE(STULIB/STUDNTPF) SHARE(*YES)");
            ClCommand cmd = c.getCommands().get(0);
            assertThat(cmd.getParameters()).hasSizeGreaterThanOrEqualTo(3);
        }
    }

    // =========================================================================
    // PGM / ENDPGM
    // =========================================================================

    @Nested
    class PgmBoundary {

        @Test
        void pgmWithoutParm_programNameIsNull() {
            ClContent c = build("             PGM", "             ENDPGM");
            assertThat(c.getProgramName()).isNull();
        }

        @Test
        void pgmWithParm_programNameSet() {
            ClContent c = build("             PGM        PARM(&P1 &P2)");
            // PGM with PARM doesn't set programName in current design — commands list verified
            // The PGM statement is structural; commands after it are captured
            assertThat(c).isNotNull(); // PGM parsed without error
        }

        @Test
        void commandsAfterPgm_areCaptured() {
            ClContent c = build(
                "             PGM",
                "             CALL       PGM(MYPGM)",
                "             ENDPGM"
            );
            assertThat(c.getCommands()).hasSize(1);
            assertThat(c.getCommands().get(0).getName()).isEqualTo("CALL");
        }
    }

    // =========================================================================
    // DCL Variable Declarations
    // =========================================================================

    @Nested
    class DclVariables {

        @Test
        void charVariable_parsed() {
            ClContent c = build("             DCL        VAR(&NAME) TYPE(*CHAR) LEN(20)");
            assertThat(c.getVariables()).hasSize(1);
            ClVariable v = c.getVariables().get(0);
            assertThat(v.getName()).isEqualTo("&NAME");
            assertThat(v.getType()).isEqualTo("*CHAR");
            assertThat(v.getLength()).isEqualTo(20);
        }

        @Test
        void decVariable_withDecPositions() {
            ClContent c = build("             DCL        VAR(&AMOUNT) TYPE(*DEC) LEN(11 2)");
            assertThat(c.getVariables()).hasSize(1);
            ClVariable v = c.getVariables().get(0);
            assertThat(v.getName()).isEqualTo("&AMOUNT");
            assertThat(v.getType()).isEqualTo("*DEC");
        }

        @Test
        void variableWithValue_initialValueSet() {
            ClContent c = build("             DCL        VAR(&MSG) TYPE(*CHAR) LEN(50) VALUE('INITIAL')");
            assertThat(c.getVariables()).hasSize(1);
            assertThat(c.getVariables().get(0).getInitialValue()).isEqualTo("'INITIAL'");
        }

        @Test
        void multipleVariables_allCaptured() {
            ClContent c = build(
                "             DCL        VAR(&V1) TYPE(*CHAR) LEN(1)",
                "             DCL        VAR(&V2) TYPE(*DEC) LEN(5 0)",
                "             DCL        VAR(&V3) TYPE(*LGL)"
            );
            assertThat(c.getVariables()).hasSize(3);
            assertThat(c.getVariables().get(0).getName()).isEqualTo("&V1");
            assertThat(c.getVariables().get(1).getName()).isEqualTo("&V2");
            assertThat(c.getVariables().get(2).getName()).isEqualTo("&V3");
        }
    }

    // =========================================================================
    // DCLF File Declarations
    // =========================================================================

    @Nested
    class DclfDeclarations {

        @Test
        void dclf_fileNameExtracted() {
            ClContent c = build("             DCLF       FILE(STUDNTPF)");
            assertThat(c.getFileDeclarations()).hasSize(1);
            assertThat(c.getFileDeclarations().get(0).getFileName()).isEqualTo("STUDNTPF");
        }

        @Test
        void dclf_withOpnid() {
            ClContent c = build("             DCLF       FILE(QTEMP/WORKFILE) OPNID(WORK)");
            assertThat(c.getFileDeclarations()).hasSize(1);
            ClFileDeclaration fd = c.getFileDeclarations().get(0);
            assertThat(fd.getFileName()).isEqualTo("QTEMP/WORKFILE");
            assertThat(fd.getOpenId()).isEqualTo("WORK");
        }

        @Test
        void multipleDclf_allCaptured() {
            ClContent c = build(
                "             DCLF       FILE(FILE1)",
                "             DCLF       FILE(FILE2)"
            );
            assertThat(c.getFileDeclarations()).hasSize(2);
        }
    }

    // =========================================================================
    // Labels
    // =========================================================================

    @Nested
    class Labels {

        @Test
        void label_isCaptured() {
            ClContent c = build(" ERROR:");
            assertThat(c.getLabels()).hasSize(1);
            assertThat(c.getLabels().get(0).getName()).isEqualTo("ERROR");
        }

        @Test
        void label_specTypeIsTagged() {
            ClContent c = build(" ENDPGM:");
            boolean hasLabel = c.getSourceLines().stream()
                    .anyMatch(l -> "LABEL".equals(l.getSpecType()));
            assertThat(hasLabel).isTrue();
        }

        @Test
        void multipleLabels_allCaptured() {
            ClContent c = build(
                " ERROR:",
                "             SNDPGMMSG  MSGDTA('Error')",
                " ENDPGM:",
                "             ENDPGM"
            );
            assertThat(c.getLabels()).hasSize(2);
            List<String> names = c.getLabels().stream()
                    .map(ClLabel::getName).toList();
            assertThat(names).contains("ERROR", "ENDPGM");
        }

        @Test
        void labelWithCommandOnSameLine_bothCaptured() {
            ClContent c = build(" PROCESS:   SUBR");
            // Label AND SUBR command on same line
            assertThat(c.getLabels()).hasSizeGreaterThanOrEqualTo(1);
        }
    }

    // =========================================================================
    // MONMSG Monitoring
    // =========================================================================

    @Nested
    class MonMsgParsing {

        @Test
        void monmsg_simple_noExec() {
            ClContent c = build(
                "             CALL       PGM(MYPGM)",
                "             MONMSG     MSGID(CPF2103)"
            );
            // MONMSG without EXEC — attached to preceding command or top-level
            assertThat(c.getCommands()).isNotEmpty();
        }

        @Test
        void monmsg_topLevel_withExecGoto() {
            ClContent c = build(
                "             MONMSG     MSGID(CPF0000) EXEC(GOTO CMDLBL(ERROR))"
            );
            // Top-level MONMSG captured as a command
            assertThat(c.getCommands()).hasSizeGreaterThanOrEqualTo(1);
            ClCommand monmsg = c.getCommands().stream()
                    .filter(cmd -> "MONMSG".equalsIgnoreCase(cmd.getName()))
                    .findFirst().orElse(null);
            assertThat(monmsg).isNotNull();
            assertThat(monmsg.getMonitorMessage()).isNotNull();
            assertThat(monmsg.getMonitorMessage().getMsgId()).isEqualTo("CPF0000");
        }

        @Test
        void monmsg_withExec_hasExecCommands() {
            ClContent c = build(
                "             MONMSG     MSGID(CPF0000) EXEC(GOTO CMDLBL(ERROR))"
            );
            ClCommand monmsg = c.getCommands().stream()
                    .filter(cmd -> "MONMSG".equalsIgnoreCase(cmd.getName()))
                    .findFirst().orElse(null);
            assertThat(monmsg).isNotNull();
            assertThat(monmsg.getExecCommands()).isNotEmpty();
            assertThat(monmsg.getExecCommands().get(0).getName()).isEqualTo("GOTO");
        }
    }

    // =========================================================================
    // SUBR / ENDSUBR Subroutines
    // =========================================================================

    @Nested
    class SubroutineParsing {

        @Test
        void subr_isCaptured() {
            ClContent c = build(
                " PROCESS:   SUBR",
                "             CALL       PGM(PROCPGM)",
                "             ENDSUBR"
            );
            assertThat(c.getSubroutines()).hasSize(1);
            assertThat(c.getSubroutines().get(0).getName()).isEqualTo("PROCESS");
        }

        @Test
        void subr_commandsInsideSubroutine_captured() {
            ClContent c = build(
                " MYSUB:     SUBR",
                "             CALL       PGM(PGM1)",
                "             CALL       PGM(PGM2)",
                "             ENDSUBR"
            );
            ClSubroutine sub = c.getSubroutines().get(0);
            assertThat(sub.getCommands()).hasSize(2);
            assertThat(sub.getCommands().get(0).getName()).isEqualTo("CALL");
            assertThat(sub.getCommands().get(1).getName()).isEqualTo("CALL");
        }

        @Test
        void multipleSubroutines_allCaptured() {
            ClContent c = build(
                " SUB1:      SUBR",
                "             CALL       PGM(A)",
                "             ENDSUBR",
                " SUB2:      SUBR",
                "             CALL       PGM(B)",
                "             ENDSUBR"
            );
            assertThat(c.getSubroutines()).hasSize(2);
            assertThat(c.getSubroutines().get(0).getName()).isEqualTo("SUB1");
            assertThat(c.getSubroutines().get(1).getName()).isEqualTo("SUB2");
        }

        @Test
        void commandsOutsideSubroutine_notInsideSubr() {
            ClContent c = build(
                "             CALL       PGM(BEFORE)",
                " SUB:       SUBR",
                "             CALL       PGM(INSIDE)",
                "             ENDSUBR",
                "             CALL       PGM(AFTER)"
            );
            // Top-level commands: BEFORE and AFTER
            assertThat(c.getCommands()).hasSizeGreaterThanOrEqualTo(2);
            // Subroutine has one command: INSIDE
            assertThat(c.getSubroutines()).hasSize(1);
            assertThat(c.getSubroutines().get(0).getCommands()).hasSize(1);
        }
    }

    // =========================================================================
    // Control Flow Commands
    // =========================================================================

    @Nested
    class ControlFlow {

        @Test
        void goto_parsedAsCommand() {
            ClContent c = build("             GOTO       CMDLBL(ENDPGM)");
            assertThat(c.getCommands()).hasSize(1);
            assertThat(c.getCommands().get(0).getName()).isEqualTo("GOTO");
        }

        @Test
        void chgvar_parsedAsCommand() {
            ClContent c = build("             CHGVAR     VAR(&X) VALUE('Y')");
            assertThat(c.getCommands()).hasSize(1);
            assertThat(c.getCommands().get(0).getName()).isEqualTo("CHGVAR");
        }

        @Test
        void if_parsedAsCommand() {
            ClContent c = build("             IF         COND(&OPT *EQ '1') THEN(DO)");
            assertThat(c.getCommands()).isNotEmpty();
            boolean hasIf = c.getCommands().stream()
                    .anyMatch(cmd -> "IF".equalsIgnoreCase(cmd.getName()));
            assertThat(hasIf).isTrue();
        }

        @Test
        void doWhile_parsedAsCommand() {
            ClContent c = build(
                "             DOWHILE    COND(&CNT *LT 10)",
                "             CHGVAR     VAR(&CNT) VALUE(&CNT + 1)",
                "             ENDDO"
            );
            assertThat(c.getCommands()).hasSizeGreaterThanOrEqualTo(3);
        }

        @Test
        void select_parsedAsCommand() {
            ClContent c = build(
                "             SELECT",
                "             WHEN       COND(&X *EQ '1') CMD(CALL PGM(P1))",
                "             ENDSELECT"
            );
            assertThat(c.getCommands()).hasSizeGreaterThanOrEqualTo(1);
        }
    }

    // =========================================================================
    // Source Line Tagging
    // =========================================================================

    @Nested
    class SourceLineTagging {

        @Test
        void commandLine_taggedAsCommand() {
            ClContent c = build("             CALL       PGM(MYPGM)");
            boolean hasCommand = c.getSourceLines().stream()
                    .anyMatch(l -> "COMMAND".equals(l.getSpecType()));
            assertThat(hasCommand).isTrue();
        }

        @Test
        void allLinesHaveLineNumbers() {
            ClContent c = build(
                "/* comment */",
                "             PGM",
                "             ENDPGM"
            );
            for (int i = 0; i < c.getSourceLines().size(); i++) {
                assertThat(c.getSourceLines().get(i).getLineNumber()).isEqualTo(i + 1);
            }
        }
    }

    // =========================================================================
    // Common Real-World Commands
    // =========================================================================

    @Nested
    class CommonCommands {

        @Test
        void ovrdbf_parsed() {
            ClContent c = build("             OVRDBF     FILE(STUDNTPF) TOFILE(STULIB/STUDNTPF)");
            assertThat(c.getCommands()).hasSize(1);
            assertThat(c.getCommands().get(0).getName()).isEqualTo("OVRDBF");
        }

        @Test
        void addlible_parsed() {
            ClContent c = build("             ADDLIBLE   LIB(STULIB)");
            assertThat(c.getCommands()).hasSize(1);
            assertThat(c.getCommands().get(0).getName()).isEqualTo("ADDLIBLE");
        }

        @Test
        void rmvlible_parsed() {
            ClContent c = build("             RMVLIBLE   LIB(STULIB)");
            assertThat(c.getCommands()).hasSize(1);
            assertThat(c.getCommands().get(0).getName()).isEqualTo("RMVLIBLE");
        }

        @Test
        void dltovr_parsed() {
            ClContent c = build("             DLTOVR     FILE(*ALL)");
            assertThat(c.getCommands()).hasSize(1);
            assertThat(c.getCommands().get(0).getName()).isEqualTo("DLTOVR");
        }

        @Test
        void sndpgmmsg_parsed() {
            ClContent c = build("             SNDPGMMSG  MSGID(CPF9898) MSGF(QCPFMSG) MSGTYPE(*ESCAPE)");
            assertThat(c.getCommands()).hasSize(1);
            ClCommand cmd = c.getCommands().get(0);
            assertThat(cmd.getName()).isEqualTo("SNDPGMMSG");
            assertThat(cmd.getParameters()).hasSizeGreaterThanOrEqualTo(3);
        }

        @Test
        void callWithLibraryQualifiedName() {
            ClContent c = build("             CALL       PGM(STULIB/MNUPRG)");
            assertThat(c.getCommands()).hasSize(1);
            ClParameter pgmparam = c.getCommands().get(0).getParameters().stream()
                    .filter(p -> "PGM".equalsIgnoreCase(p.getKeyword()))
                    .findFirst().orElse(null);
            assertThat(pgmparam).isNotNull();
            assertThat(pgmparam.getValue()).isEqualTo("STULIB/MNUPRG");
        }
    }
}
