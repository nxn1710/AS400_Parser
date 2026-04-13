package com.as400parser.common.analyzer;

import com.as400parser.cl.model.ClCommand;
import com.as400parser.cl.model.ClContent;
import com.as400parser.cl.model.ClParameter;
import com.as400parser.common.analyzer.model.CallTarget;
import com.as400parser.common.analyzer.model.CopyReference;
import com.as400parser.common.analyzer.model.CrossReference;
import com.as400parser.common.analyzer.model.IndicatorUsage;
import com.as400parser.common.model.IrDocument;
import com.as400parser.rpg3.model.CalcSpec;
import com.as400parser.rpg3.model.Rpg3Content;
import com.as400parser.rpgle.model.FileSpec;
import com.as400parser.rpgle.model.FreeFormatStatement;
import com.as400parser.rpgle.model.RpgleContent;
import com.as400parser.rpg3.model.IdentifierNode;
import com.as400parser.rpg3.model.LiteralNode;
import com.as400parser.rpg3.model.SpecialValueNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Analyzer for extracting program call graphs, subroutine calls,
 * indicator usage flow, and copy member references.
 */
public class CrossReferenceAnalyzer {

    private final CrossReference crossReference = new CrossReference();
    private final Gson gson = new Gson();

    /**
     * Performs cross-reference analysis on a collection of IR documents.
     *
     * @param groupedDocs Map of source type to list of documents.
     * @return The populated CrossReference model.
     */
    public CrossReference analyze(Map<String, List<IrDocument>> groupedDocs) {
        for (Map.Entry<String, List<IrDocument>> entry : groupedDocs.entrySet()) {
            String sourceType = entry.getKey();
            for (IrDocument doc : entry.getValue()) {
                String programName = doc.getMetadata().getSourceMember();
                processDoc(doc, programName, sourceType);
            }
        }
        return crossReference;
    }

    private void processDoc(IrDocument doc, String programName, String sourceType) {
        // 1. Extract Copy Members (Common for all)
        extractCopyMembers(doc, programName);

        // 2. Extract Type-Specific Calls and Indicators
        switch (sourceType.toUpperCase()) {
            case "RPG3":
                processRpg3(doc, programName);
                break;
            case "RPGLE":
            case "SQLRPGLE":
                processRpgle(doc, programName);
                break;
            case "CL":
            case "CLLE":
            case "CLP":
                processCl(doc, programName);
                break;
        }
    }

    private void extractCopyMembers(IrDocument doc, String programName) {
        if (doc.getDependencies() != null && doc.getDependencies().getCopyMembers() != null) {
            List<CopyReference> refs = new ArrayList<>();
            for (IrDocument.CopyMemberRef copy : doc.getDependencies().getCopyMembers()) {
                refs.add(new CopyReference(copy.getMemberName(), copy.getResolvedPath(), copy.isResolved()));
            }
            if (!refs.isEmpty()) {
                crossReference.getCopyMembers().put(programName, refs);
            }
        }
    }

    private void processRpg3(IrDocument doc, String programName) {
        // Content is Rpg3Content or a JsonObject if using generic loader
        Rpg3Content content = convertToRpg3(doc.getContent());
        if (content == null) return;

        List<CallTarget> calls = new ArrayList<>();
        List<IndicatorUsage> indicators = new ArrayList<>();

        // Scan Calculation Specs recursively
        if (content.getCalculationSpecs() != null) {
            for (Object obj : content.getCalculationSpecs()) {
                scanRpg3CalcNode(obj, calls, indicators);
            }
        }

        if (!calls.isEmpty()) crossReference.getCallGraph().put(programName, calls);
        if (!indicators.isEmpty()) crossReference.getIndicatorUsage().put(programName, indicators);
    }

    private void processRpgle(IrDocument doc, String programName) {
        RpgleContent content = convertToRpgle(doc.getContent());
        if (content == null) return;

        List<CallTarget> calls = new ArrayList<>();
        List<IndicatorUsage> indicators = new ArrayList<>();

        // Scan Fixed Specs
        if (content.getCalculationSpecs() != null) {
            for (Object obj : content.getCalculationSpecs()) {
                scanRpg3CalcNode(obj, calls, indicators); // RPGLE uses RPG3 CalcSpec models for fixed
            }
        }

        // Scan Free-Format
        if (content.getFreeFormatStatements() != null) {
            for (FreeFormatStatement stmt : content.getFreeFormatStatements()) {
                scanRpgleFreeStmt(stmt, calls, indicators);
            }
        }

        if (!calls.isEmpty()) crossReference.getCallGraph().put(programName, calls);
        if (!indicators.isEmpty()) crossReference.getIndicatorUsage().put(programName, indicators);
    }

    private void scanRpg3CalcNode(Object obj, List<CallTarget> calls, List<IndicatorUsage> indicators) {
        // This handles polymorphic nodes like Operation, SubroutineBlock, etc.
        if (obj instanceof CalcSpec.Operation) {
            CalcSpec.Operation op = (CalcSpec.Operation) obj;
            String opcode = op.getOpcode() != null ? op.getOpcode().toUpperCase() : "";
            
            if (opcode.equals("CALL")) {
                String target = resolveName(op.getFactor2());
                calls.add(new CallTarget(target, "PROGRAM", op.getRawSourceLine()));
            } else if (opcode.equals("EXSR")) {
                String target = resolveName(op.getFactor2());
                calls.add(new CallTarget(target, "SUBROUTINE", op.getRawSourceLine()));
            }

            // Extract Indicators from Resulting Indicators
            if (op.getResultingIndicators() != null) {
                addIndicatorIfPresent(op.getResultingIndicators().getHigh(), "SET", "C", indicators);
                addIndicatorIfPresent(op.getResultingIndicators().getLow(), "SET", "C", indicators);
                addIndicatorIfPresent(op.getResultingIndicators().getEqual(), "SET", "C", indicators);
            }
            
            // Extract Conditioning Indicators
            if (op.getConditioningIndicators() != null) {
                for (CalcSpec.ConditioningIndicator ind : op.getConditioningIndicators()) {
                    indicators.add(new IndicatorUsage(ind.getIndicator(), "CHECK", "C"));
                }
            }
        } else if (obj instanceof CalcSpec.CallSubroutine) {
            CalcSpec.CallSubroutine csr = (CalcSpec.CallSubroutine) obj;
            calls.add(new CallTarget(csr.getSubroutineName(), "SUBROUTINE", "EXSR " + csr.getSubroutineName()));
        } else if (obj instanceof CalcSpec.SubroutineBlock) {
            CalcSpec.SubroutineBlock block = (CalcSpec.SubroutineBlock) obj;
            for (Object child : block.getOperations()) {
                scanRpg3CalcNode(child, calls, indicators);
            }
        } else if (obj instanceof CalcSpec.ConditionalBlock) {
            CalcSpec.ConditionalBlock block = (CalcSpec.ConditionalBlock) obj;
            for (Object child : block.getThenOps()) scanRpg3CalcNode(child, calls, indicators);
            for (Object child : block.getElseOps()) scanRpg3CalcNode(child, calls, indicators);
        } else if (obj instanceof CalcSpec.DoWhileBlock) {
            for (Object child : ((CalcSpec.DoWhileBlock) obj).getBodyOps()) scanRpg3CalcNode(child, calls, indicators);
        } else if (obj instanceof CalcSpec.DoUntilBlock) {
            for (Object child : ((CalcSpec.DoUntilBlock) obj).getBodyOps()) scanRpg3CalcNode(child, calls, indicators);
        } else if (obj instanceof CalcSpec.DoBlock) {
            for (Object child : ((CalcSpec.DoBlock) obj).getBodyOps()) scanRpg3CalcNode(child, calls, indicators);
        }
    }

    private void scanRpgleFreeStmt(FreeFormatStatement stmt, List<CallTarget> calls, List<IndicatorUsage> indicators) {
        String operation = stmt.getOperation();
        String expression = stmt.getExpression();
        String fullLine = (operation != null ? operation : "") + " " + (expression != null ? expression : "");

        if (operation != null && operation.equalsIgnoreCase("EXSR")) {
            calls.add(new CallTarget(expression.replace(";", "").trim(), "SUBROUTINE", fullLine));
        }
        
        if (operation != null && operation.equalsIgnoreCase("CALLP")) {
            calls.add(new CallTarget(expression.replace(";", "").trim(), "PROGRAM", fullLine));
        }
    }

    private void processCl(IrDocument doc, String programName) {
        ClContent content = convertToCl(doc.getContent());
        if (content == null) return;

        List<CallTarget> calls = new ArrayList<>();
        if (content.getCommands() != null) {
            for (ClCommand cmd : content.getCommands()) {
                scanClCommand(cmd, calls);
            }
        }
        if (!calls.isEmpty()) {
            crossReference.getCallGraph().put(programName, calls);
        }
    }

    private void scanClCommand(ClCommand cmd, List<CallTarget> calls) {
        String name = cmd.getName().toUpperCase();
        if (name.equals("CALL") || name.equals("SBMJOB")) {
            for (ClParameter param : cmd.getParameters()) {
                if ("PGM".equalsIgnoreCase(param.getKeyword()) || (param.isPositional() && calls.isEmpty())) {
                    calls.add(new CallTarget(param.getValue(), "PROGRAM", cmd.getName() + " " + param.getValue()));
                    break;
                }
            }
        }
        // Recursive for nested commands (e.g. MONMSG execution or IF/THEN)
        if (cmd.getExecCommands() != null) {
            for (ClCommand sub : cmd.getExecCommands()) {
                scanClCommand(sub, calls);
            }
        }
    }

    private void addIndicatorIfPresent(CalcSpec.IndicatorRef ref, String usageType, String specType, List<IndicatorUsage> indicators) {
        if (ref != null && ref.getName() != null) {
            indicators.add(new IndicatorUsage(ref.getName(), usageType, specType));
        }
    }

    private Rpg3Content convertToRpg3(Object content) {
        if (content instanceof Rpg3Content) return (Rpg3Content) content;
        if (content instanceof JsonObject) return gson.fromJson((JsonObject) content, Rpg3Content.class);
        return null;
    }

    private RpgleContent convertToRpgle(Object content) {
        if (content instanceof RpgleContent) return (RpgleContent) content;
        if (content instanceof JsonObject) return gson.fromJson((JsonObject) content, RpgleContent.class);
        return null;
    }

    private ClContent convertToCl(Object content) {
        if (content instanceof ClContent) return (ClContent) content;
        if (content instanceof JsonObject) return gson.fromJson((JsonObject) content, ClContent.class);
        return null;
    }

    public static String resolveName(Object obj) {
        if (obj == null) return "UNKNOWN";
        if (obj instanceof String) return (String) obj;
        if (obj instanceof IdentifierNode) return ((IdentifierNode) obj).getName();
        if (obj instanceof LiteralNode) return String.valueOf(((LiteralNode) obj).getValue());
        if (obj instanceof SpecialValueNode) return ((SpecialValueNode) obj).getValue();
        
        // Fallback for Gson-deserialized maps (if not using specific model classes)
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            if (map.containsKey("name")) return String.valueOf(map.get("name"));
            if (map.containsKey("value")) return String.valueOf(map.get("value"));
        }
        
        return obj.toString();
    }
}
