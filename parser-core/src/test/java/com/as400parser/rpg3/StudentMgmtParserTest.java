package com.as400parser.rpg3;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.common.serializer.IrJsonSerializer;
import com.as400parser.rpg3.model.Rpg3Content;
import com.as400parser.rpg3.model.CalcSpec.SubroutineBlock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

/**
 * Parser test against real RPG3 program files in rpg3-student-mgmt/QRPGSRC/.
 */
class StudentMgmtParserTest {

    private static final Path SOURCE_DIR = Path.of("D:/Code/AS400_Parser/rpg3-student-mgmt/QRPGSRC");
    private static final Rpg3ParserFacade FACADE = new Rpg3ParserFacade();

    @ParameterizedTest
    @ValueSource(strings = {"MNUPRG.rpg", "STULST.rpg", "STUPRG.rpg", "STURPT.rpg"})
    void parsesSuccessfully(String fileName) {
        Path file = SOURCE_DIR.resolve(fileName);
        IrDocument doc = FACADE.parse(file, ParseOptions.defaults());

        assertThat(doc).as(fileName + " should produce a document").isNotNull();
        assertThat(doc.getContent()).as(fileName + " content not null").isNotNull();
        assertThat(doc.getMetadata()).as(fileName + " metadata not null").isNotNull();
        assertThat(doc.getMetadata().getParseInfo().getParseStatus())
            .as(fileName + " parse status")
            .isEqualTo("complete");
    }

    @ParameterizedTest
    @ValueSource(strings = {"MNUPRG.rpg", "STULST.rpg", "STUPRG.rpg", "STURPT.rpg"})
    void extractsSpecTypes(String fileName) {
        Path file = SOURCE_DIR.resolve(fileName);
        IrDocument doc = FACADE.parse(file, ParseOptions.defaults());
        Rpg3Content content = (Rpg3Content) doc.getContent();

        // All files should have some C-specs
        assertThat(content.getCalculationSpecs())
            .as(fileName + " should have calc specs")
            .isNotEmpty();

        // All files should have some comments
        assertThat(content.getComments())
            .as(fileName + " should have comments")
            .isNotEmpty();
    }

    @Test
    void mnuprgHasExpectedStructure() {
        Path file = SOURCE_DIR.resolve("MNUPRG.rpg");
        IrDocument doc = FACADE.parse(file, ParseOptions.defaults());
        Rpg3Content content = (Rpg3Content) doc.getContent();

        // 1 file spec (MNUDSPF)
        assertThat(content.getFileSpecs()).hasSizeGreaterThanOrEqualTo(1);
        assertThat(content.getFileSpecs().get(0).getFileName()).isEqualTo("MNUDSPF");

        // I-specs for SDS fields
        assertThat(content.getInputSpecs()).isNotEmpty();

        // 5 subroutines: OPT01, OPT02, OPT03, OPT09, OPTER
        assertThat(content.getSubroutines()).hasSizeGreaterThanOrEqualTo(5);
        var srNames = content.getSubroutines().stream()
            .map(SubroutineBlock::getSubroutineName).toList();
        assertThat(srNames).contains("OPT01", "OPT02", "OPT03", "OPT09", "OPTER");

        // CALL programs
        var calledNames = doc.getDependencies().getCalledPrograms().stream()
            .map(IrDocument.DependencyRef::getName).toList();
        assertThat(calledNames)
            .contains("STUPRG", "STULST", "STURPT");
    }

    @Test
    void generateFullReport() throws Exception {
        String[] files = {"MNUPRG.rpg", "STULST.rpg", "STUPRG.rpg", "STURPT.rpg"};
        IrJsonSerializer serializer = new IrJsonSerializer();
        Path outputDir = Path.of("d:/Code/AS400_Parser/output");
        Files.createDirectories(outputDir);

        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputDir.resolve("parser_test_report.txt").toString()), StandardCharsets.UTF_8))) {
            for (String fileName : files) {
                Path file = SOURCE_DIR.resolve(fileName);
                IrDocument doc = FACADE.parse(file, ParseOptions.defaults());
                Rpg3Content content = (Rpg3Content) doc.getContent();

                out.println("=== " + fileName + " ===");
                out.println("Parse status: " + doc.getMetadata().getParseInfo().getParseStatus());
                out.println("Total lines: " + doc.getMetadata().getParseInfo().getTotalLines());
                out.println("H-specs: " + content.getHeaderSpecs().size());
                out.println("F-specs: " + content.getFileSpecs().size());
                if (!content.getFileSpecs().isEmpty()) {
                    out.println("  Files: " + content.getFileSpecs().stream()
                        .map(f -> f.getFileName())
                        .toList());
                }
                out.println("E-specs: " + content.getExtensionSpecs().size());
                out.println("I-specs: " + content.getInputSpecs().size());
                out.println("C-specs: " + content.getCalculationSpecs().size());
                out.println("O-specs: " + content.getOutputSpecs().size());
                out.println("Comments: " + content.getComments().size());
                out.println("Data structures: " + content.getDataStructures().size());
                out.println("Subroutines: " + content.getSubroutines().size());
                if (!content.getSubroutines().isEmpty()) {
                    out.println("  Names: " + content.getSubroutines().stream()
                        .map(SubroutineBlock::getSubroutineName).toList());
                }
                out.println("Symbol table entries: " + content.getSymbolTable().size());
                out.println("Called programs: " + doc.getDependencies().getCalledPrograms());
                out.println("Referenced files: " + doc.getDependencies().getReferencedFiles());
                out.println("Errors: " + doc.getErrors().size());
                if (!doc.getErrors().isEmpty()) {
                    doc.getErrors().forEach(e -> out.println("  - line " + e.getLine() + ": " + e.getMessage()));
                }

                // Save JSON IR
                String json = serializer.serialize(doc);
                String jsonPath = outputDir.resolve(fileName.replace(".rpg", "_ir.json")).toString();
                try (PrintWriter jsonOut = new PrintWriter(new OutputStreamWriter(new FileOutputStream(jsonPath), StandardCharsets.UTF_8))) {
                    jsonOut.print(json);
                }
                out.println("JSON IR saved to: " + jsonPath);
                out.println();
            }
        }
    }
}
