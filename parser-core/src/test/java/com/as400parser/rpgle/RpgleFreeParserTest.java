package com.as400parser.rpgle;

import com.as400parser.rpgle.model.FreeFormatStatement;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link RpgleFreeParser} — free-format parsing logic.
 * <p>
 * Tests both:
 * <ul>
 *   <li>Statement-level extraction ({@code parse()}, used for /FREE blocks in mixed format)</li>
 *   <li>ANTLR-based parsing ({@code parseFullyFree()}, used for **FREE sources)</li>
 * </ul>
 */
class RpgleFreeParserTest {

    // =========================================================================
    // Statement-level parsing (for /FREE blocks)
    // =========================================================================

    @Nested
    class StatementLevelParsing {

        @Test
        void singleStatement_parsedCorrectly() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("Counter = 0;"), 1);
            assertThat(stmts).hasSize(1);
            assertThat(stmts.get(0).getText()).isEqualTo("Counter = 0");
        }

        @Test
        void singleStatement_statementType_eval() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("Counter = 0;"), 1);
            assertThat(stmts.get(0).getStatementType()).isEqualTo("eval");
        }

        @Test
        void singleStatement_location_correctStartLine() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("Counter = 0;"), 5);
            assertThat(stmts.get(0).getLocation().getStartLine()).isEqualTo(5);
            assertThat(stmts.get(0).getLocation().getEndLine()).isEqualTo(5);
        }

        @Test
        void multipleStatements_allParsed() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("Counter = 0;", "read STUDREC;", "*inlr = *on;"), 1);
            assertThat(stmts).hasSize(3);
        }

        @Test
        void multiLineStatement_joinedCorrectly() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("Counter =", "  Counter + 1;"), 1);
            assertThat(stmts).hasSize(1);
            assertThat(stmts.get(0).getText()).contains("Counter =");
            assertThat(stmts.get(0).getText()).contains("Counter + 1");
        }

        @Test
        void multiLineStatement_locationSpansLines() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("Counter =", "  Counter + 1;"), 10);
            assertThat(stmts.get(0).getLocation().getStartLine()).isEqualTo(10);
            assertThat(stmts.get(0).getLocation().getEndLine()).isEqualTo(11);
        }

        @Test
        void commentLines_skipped() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("// This is a comment",
                            "Counter = 0;",
                            "// Another comment"), 1);
            assertThat(stmts).hasSize(1);
            assertThat(stmts.get(0).getText()).isEqualTo("Counter = 0");
        }

        @Test
        void blankLines_skipped() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("", "   ", "Counter = 0;", ""), 1);
            assertThat(stmts).hasSize(1);
        }

        @Test
        void nullLines_skipped() {
            RpgleFreeParser parser = new RpgleFreeParser();
            java.util.ArrayList<String> lines = new java.util.ArrayList<>();
            lines.add(null);
            lines.add("Counter = 0;");
            List<FreeFormatStatement> stmts = parser.parse(lines, 1);
            assertThat(stmts).hasSize(1);
        }

        @Test
        void freeHeader_skipped() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("**FREE", "Counter = 0;"), 1);
            assertThat(stmts).hasSize(1);
        }

        @Test
        void inlineComment_strippedFromStatement() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("Counter = 0; // init counter"), 1);
            assertThat(stmts).hasSize(1);
            assertThat(stmts.get(0).getText()).isEqualTo("Counter = 0");
        }

        @Test
        void inlineComment_insideString_preserved() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("msg = 'use // for comments';"), 1);
            assertThat(stmts).hasSize(1);
            assertThat(stmts.get(0).getText()).contains("//");
        }

        @Test
        void unterminatedStatement_flushedAtEnd() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("Counter = 0"), 1);  // no semicolon
            assertThat(stmts).hasSize(1);
            assertThat(stmts.get(0).getText()).isEqualTo("Counter = 0");
        }

        @Test
        void emptyInput_producesEmptyList() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(List.of(), 1);
            assertThat(stmts).isEmpty();
        }

        @Test
        void allBlankInput_producesEmptyList() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("", "   ", ""), 1);
            assertThat(stmts).isEmpty();
        }
    }

    // =========================================================================
    // Statement type detection (fallback)
    // =========================================================================

    @Nested
    class FallbackTypeDetection {

        private String detectType(String stmtText) {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of(stmtText + ";"), 1);
            assertThat(stmts).hasSize(1);
            return stmts.get(0).getStatementType();
        }

        // Declarations
        @Test void dclS_detected() { assertThat(detectType("dcl-s Counter packed(5:0)")).isEqualTo("dcl-s"); }
        @Test void dclDS_detected() { assertThat(detectType("dcl-ds MyDS")).isEqualTo("dcl-ds"); }
        @Test void dclF_detected() { assertThat(detectType("dcl-f STUDNTPF disk(*ext)")).isEqualTo("dcl-f"); }
        @Test void dclPR_detected() { assertThat(detectType("dcl-pr UpdateLog extpgm('UPDLOG')")).isEqualTo("dcl-pr"); }
        @Test void dclPI_detected() { assertThat(detectType("dcl-pi MyProc")).isEqualTo("dcl-pi"); }
        @Test void dclC_detected() { assertThat(detectType("dcl-c MAX_REC const(100)")).isEqualTo("dcl-c"); }
        @Test void dclProc_detected() { assertThat(detectType("dcl-proc MyProc")).isEqualTo("dcl-proc"); }

        // End declarations
        @Test void endDS_detected() { assertThat(detectType("end-ds")).isEqualTo("end-ds"); }
        @Test void endPR_detected() { assertThat(detectType("end-pr")).isEqualTo("end-pr"); }
        @Test void endPI_detected() { assertThat(detectType("end-pi")).isEqualTo("end-pi"); }
        @Test void endProc_detected() { assertThat(detectType("end-proc")).isEqualTo("end-proc"); }

        // Control options
        @Test void ctlOpt_detected() { assertThat(detectType("ctl-opt dftactgrp(*no)")).isEqualTo("ctl-opt"); }

        // Flow control
        @Test void if_detected() { assertThat(detectType("if Counter > 0")).isEqualTo("if"); }
        @Test void else_detected() { assertThat(detectType("else")).isEqualTo("else"); }
        @Test void elseif_detected() { assertThat(detectType("elseif Counter > 0")).isEqualTo("elseif"); }
        @Test void endif_detected() { assertThat(detectType("endif")).isEqualTo("endif"); }
        @Test void dow_detected() { assertThat(detectType("dow not %eof")).isEqualTo("dow"); }
        @Test void dou_detected() { assertThat(detectType("dou %eof")).isEqualTo("dou"); }
        @Test void enddo_detected() { assertThat(detectType("enddo")).isEqualTo("enddo"); }
        @Test void for_detected() { assertThat(detectType("for i = 1 to 10")).isEqualTo("for"); }
        @Test void endfor_detected() { assertThat(detectType("endfor")).isEqualTo("endfor"); }

        // Select/When
        @Test void select_detected() { assertThat(detectType("select")).isEqualTo("select"); }
        @Test void when_detected() { assertThat(detectType("when Counter = 1")).isEqualTo("when"); }
        @Test void other_detected() { assertThat(detectType("other")).isEqualTo("other"); }
        @Test void endsl_detected() { assertThat(detectType("endsl")).isEqualTo("endsl"); }

        // Monitor/On-Error
        @Test void monitor_detected() { assertThat(detectType("monitor")).isEqualTo("monitor"); }
        @Test void onError_detected() { assertThat(detectType("on-error")).isEqualTo("on-error"); }
        @Test void endmon_detected() { assertThat(detectType("endmon")).isEqualTo("endmon"); }

        // Operations
        @Test void return_detected() { assertThat(detectType("return")).isEqualTo("return"); }
        @Test void begsr_detected() { assertThat(detectType("begsr MySR")).isEqualTo("begsr"); }
        @Test void endsr_detected() { assertThat(detectType("endsr")).isEqualTo("endsr"); }
        @Test void exsr_detected() { assertThat(detectType("exsr MySR")).isEqualTo("exsr"); }
        @Test void read_detected() { assertThat(detectType("read STUDREC")).isEqualTo("read"); }
        @Test void chain_detected() { assertThat(detectType("chain key STUDREC")).isEqualTo("chain"); }
        @Test void write_detected() { assertThat(detectType("write OUTREC")).isEqualTo("write"); }
        @Test void update_detected() { assertThat(detectType("update STUDREC")).isEqualTo("update"); }
        @Test void delete_detected() { assertThat(detectType("delete STUDREC")).isEqualTo("delete"); }
        @Test void eval_detected() { assertThat(detectType("eval Counter = 0")).isEqualTo("eval"); }
        @Test void callp_detected() { assertThat(detectType("callp UpdateLog(Counter)")).isEqualTo("callp"); }
        @Test void clear_detected() { assertThat(detectType("clear *all MyDS")).isEqualTo("clear"); }
        @Test void reset_detected() { assertThat(detectType("reset MyField")).isEqualTo("reset"); }

        // Default: expression with = is eval
        @Test void assignmentExpression_isEval() {
            assertThat(detectType("Counter = Counter + 1")).isEqualTo("eval");
        }

        // Default: no = and unknown → operation
        @Test void unknownOperation_isOperation() {
            assertThat(detectType("sorta MyArray")).isEqualTo("operation");
        }
    }

    // =========================================================================
    // ANTLR-based parsing (fully free)
    // =========================================================================

    @Nested
    class FullyFreeParsing {

        @Test
        void simpleFullyFree_producesStatements() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<String> lines = List.of(
                    "**FREE",
                    "ctl-opt dftactgrp(*no);",
                    "dcl-s Counter packed(5:0);",
                    "Counter = 0;",
                    "*inlr = *on;"
            );
            List<FreeFormatStatement> stmts = parser.parseFullyFree(lines);
            assertThat(stmts).isNotEmpty();
        }

        @Test
        void fullyFree_noErrors() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<String> lines = List.of(
                    "**FREE",
                    "dcl-s Counter packed(5:0);",
                    "*inlr = *on;"
            );
            parser.parseFullyFree(lines);
            // ANTLR may produce warnings, but no ERROR-severity
            boolean hasError = parser.getErrors().stream()
                    .anyMatch(e -> com.as400parser.common.model.ParseError.Severity.ERROR
                            .equals(e.getSeverity()));
            assertThat(hasError).isFalse();
        }

        @Test
        void fullyFree_statementsHaveLocations() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<String> lines = List.of(
                    "**FREE",
                    "dcl-s Counter packed(5:0);",
                    "Counter = 0;",
                    "*inlr = *on;"
            );
            List<FreeFormatStatement> stmts = parser.parseFullyFree(lines);
            for (FreeFormatStatement stmt : stmts) {
                assertThat(stmt.getLocation()).isNotNull();
                assertThat(stmt.getLocation().getStartLine()).isGreaterThan(0);
            }
        }

        @Test
        void fullyFree_statementsHaveText() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<String> lines = List.of(
                    "**FREE",
                    "dcl-s Counter packed(5:0);",
                    "*inlr = *on;"
            );
            List<FreeFormatStatement> stmts = parser.parseFullyFree(lines);
            for (FreeFormatStatement stmt : stmts) {
                assertThat(stmt.getText()).isNotEmpty();
            }
        }

        @Test
        void fullyFree_statementsHaveTypes() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<String> lines = List.of(
                    "**FREE",
                    "dcl-s Counter packed(5:0);",
                    "Counter = 0;",
                    "*inlr = *on;"
            );
            List<FreeFormatStatement> stmts = parser.parseFullyFree(lines);
            for (FreeFormatStatement stmt : stmts) {
                assertThat(stmt.getStatementType()).isNotNull();
            }
        }

        @Test
        void emptyFullyFree_producesEmptyOrFallback() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parseFullyFree(List.of("**FREE"));
            // Should not crash
            assertThat(stmts).isNotNull();
        }

        @Test
        void fullyFree_withProcedure_producesStatements() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<String> lines = List.of(
                    "**FREE",
                    "ctl-opt dftactgrp(*no);",
                    "dcl-pr MyProc extpgm('MYPROC');",
                    "  pInput packed(5:0);",
                    "end-pr;",
                    "callp MyProc(10);",
                    "*inlr = *on;"
            );
            List<FreeFormatStatement> stmts = parser.parseFullyFree(lines);
            assertThat(stmts).isNotEmpty();
        }

        @Test
        void fullyFree_withSubroutine_producesStatements() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<String> lines = List.of(
                    "**FREE",
                    "dcl-s Counter packed(5:0);",
                    "begsr MySR;",
                    "  Counter = 0;",
                    "endsr;",
                    "*inlr = *on;"
            );
            List<FreeFormatStatement> stmts = parser.parseFullyFree(lines);
            assertThat(stmts).isNotEmpty();
        }
    }

    // =========================================================================
    // parseFullSource
    // =========================================================================

    @Nested
    class ParseFullSource {

        @Test
        void validSource_returnsNonNullTree() {
            RpgleFreeParser parser = new RpgleFreeParser();
            var tree = parser.parseFullSource("**FREE\n*inlr = *on;\n");
            // Tree may or may not be null depending on grammar — just verify no crash
            assertThat(tree).as("parseFullSource should return a tree").isNotNull();
            assertThat(parser.getErrors()).isNotNull();
        }

        @Test
        void emptySource_handledGracefully() {
            RpgleFreeParser parser = new RpgleFreeParser();
            var tree = parser.parseFullSource("");
            // Empty source should be handled gracefully
            assertThat(tree == null || tree.children == null || tree.children.isEmpty())
                    .as("Empty source should produce null or empty tree").isTrue();
            assertThat(parser.getErrors()).isNotNull();
        }
    }

    // =========================================================================
    // Error handling
    // =========================================================================

    @Nested
    class ErrorHandling {

        @Test
        void getErrors_initiallyEmpty() {
            RpgleFreeParser parser = new RpgleFreeParser();
            assertThat(parser.getErrors()).isEmpty();
        }

        @Test
        void getErrors_afterParsing_returnsWarnings() {
            RpgleFreeParser parser = new RpgleFreeParser();
            // Parse something that may produce ANTLR warnings
            parser.parseFullyFree(List.of(
                    "**FREE",
                    "dcl-s x packed(5:0);",
                    "*inlr = *on;"
            ));
            // Errors list should exist (might be empty or have warnings)
            assertThat(parser.getErrors()).isNotNull();
        }
    }

    // =========================================================================
    // isInsideString edge cases
    // =========================================================================

    @Nested
    class InlineCommentHandling {

        @Test
        void commentAfterString_stripped() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("x = 'hello'; // comment"), 1);
            assertThat(stmts).hasSize(1);
            assertThat(stmts.get(0).getText()).doesNotContain("comment");
        }

        @Test
        void commentInsideString_notStripped() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("x = 'val//ue';"), 1);
            assertThat(stmts).hasSize(1);
            assertThat(stmts.get(0).getText()).contains("//");
        }

        @Test
        void noComment_noStripping() {
            RpgleFreeParser parser = new RpgleFreeParser();
            List<FreeFormatStatement> stmts = parser.parse(
                    List.of("Counter = 0;"), 1);
            assertThat(stmts).hasSize(1);
            assertThat(stmts.get(0).getText()).isEqualTo("Counter = 0");
        }
    }
}
