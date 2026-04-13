package com.as400parser.common.analyzer;

import com.as400parser.cl.model.ClCommand;
import com.as400parser.cl.model.ClContent;
import com.as400parser.cl.model.ClParameter;
import com.as400parser.common.analyzer.model.*;
import com.as400parser.common.model.IrDocument;
import com.as400parser.common.model.Metadata;
import com.as400parser.rpg3.model.CalcSpec;
import com.as400parser.rpg3.model.IdentifierNode;
import com.as400parser.rpg3.model.Rpg3Content;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CrossReferenceAnalyzerTest {

    @Test
    void testAnalyzeRpg3Calls() {
        Rpg3Content content = new Rpg3Content();
        CalcSpec.Operation callOp = new CalcSpec.Operation();
        callOp.setOpcode("CALL");
        callOp.setFactor2(new IdentifierNode("SUBPGM"));
        content.getCalculationSpecs().add(callOp);

        IrDocument doc = createIrDoc("TESTRPG", "RPG3", content);
        
        Map<String, List<IrDocument>> grouped = new HashMap<>();
        grouped.put("RPG3", Collections.singletonList(doc));

        CrossReferenceAnalyzer analyzer = new CrossReferenceAnalyzer();
        CrossReference result = analyzer.analyze(grouped);

        assertTrue(result.getCallGraph().containsKey("TESTRPG"));
        List<CallTarget> targets = result.getCallGraph().get("TESTRPG");
        assertEquals(1, targets.size());
        assertEquals("SUBPGM", targets.get(0).getTargetName());
        assertEquals("PROGRAM", targets.get(0).getCallType());
    }

    @Test
    void testAnalyzeClCalls() {
        ClContent content = new ClContent();
        ClCommand cmd = new ClCommand();
        cmd.setName("CALL");
        cmd.getParameters().add(new ClParameter("PGM", "MYPGM", false));
        content.getCommands().add(cmd);

        IrDocument doc = createIrDoc("TESTCL", "CL", content);
        
        Map<String, List<IrDocument>> grouped = new HashMap<>();
        grouped.put("CL", Collections.singletonList(doc));

        CrossReferenceAnalyzer analyzer = new CrossReferenceAnalyzer();
        CrossReference result = analyzer.analyze(grouped);

        assertTrue(result.getCallGraph().containsKey("TESTCL"));
        List<CallTarget> targets = result.getCallGraph().get("TESTCL");
        assertEquals(1, targets.size());
        assertEquals("MYPGM", targets.get(0).getTargetName());
    }

    @Test
    void testAnalyzeIndicators() {
        Rpg3Content content = new Rpg3Content();
        CalcSpec.Operation op = new CalcSpec.Operation();
        op.setOpcode("MOVE");
        CalcSpec.ResultingIndicators indicators = new CalcSpec.ResultingIndicators();
        indicators.setHigh(new CalcSpec.IndicatorRef("LR", "special"));
        op.setResultingIndicators(indicators);
        content.getCalculationSpecs().add(op);

        IrDocument doc = createIrDoc("TESTIND", "RPG3", content);
        Map<String, List<IrDocument>> grouped = new HashMap<>();
        grouped.put("RPG3", Collections.singletonList(doc));

        CrossReferenceAnalyzer analyzer = new CrossReferenceAnalyzer();
        CrossReference result = analyzer.analyze(grouped);

        assertTrue(result.getIndicatorUsage().containsKey("TESTIND"));
        List<IndicatorUsage> usage = result.getIndicatorUsage().get("TESTIND");
        assertEquals(1, usage.size());
        assertEquals("LR", usage.get(0).getIndicator());
        assertEquals("SET", usage.get(0).getUsageType());
    }

    @Test
    void testFileUsageAnalysis() {
        Rpg3Content content = new Rpg3Content();
        // Add F-spec
        Rpg3Content.FileSpec fspec = new Rpg3Content.FileSpec();
        fspec.setFileName("MYFILE");
        content.getFileSpecs().add(fspec);

        // Add READ operation
        CalcSpec.Operation readOp = new CalcSpec.Operation();
        readOp.setOpcode("READ");
        readOp.setFactor2(new IdentifierNode("MYFILE"));
        content.getCalculationSpecs().add(readOp);

        IrDocument doc = createIrDoc("TESTFILE", "RPG3", content);
        Map<String, List<IrDocument>> grouped = new HashMap<>();
        grouped.put("RPG3", Collections.singletonList(doc));

        CrossReference xref = new CrossReference();
        FileUsageAnalyzer analyzer = new FileUsageAnalyzer(xref);
        analyzer.analyze(grouped);

        assertTrue(xref.getFileUsage().containsKey("TESTFILE"));
        List<FileUsage> usages = xref.getFileUsage().get("TESTFILE");
        assertEquals(1, usages.size());
        assertEquals("MYFILE", usages.get(0).getFileName());
        assertTrue(usages.get(0).getOperations().contains(IOOperation.READ));
    }

    private IrDocument createIrDoc(String name, String type, Object content) {
        IrDocument doc = new IrDocument();
        Metadata meta = new Metadata();
        meta.setSourceMember(name);
        meta.setSourceType(type);
        doc.setMetadata(meta);
        doc.setContent(content);
        return doc;
    }
}
