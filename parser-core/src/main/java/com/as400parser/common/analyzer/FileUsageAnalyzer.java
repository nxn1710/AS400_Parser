package com.as400parser.common.analyzer;

import com.as400parser.cl.model.ClCommand;
import com.as400parser.cl.model.ClContent;
import com.as400parser.cl.model.ClFileDeclaration;
import com.as400parser.common.analyzer.model.CrossReference;
import com.as400parser.common.analyzer.model.FileUsage;
import com.as400parser.common.analyzer.model.IOOperation;
import com.as400parser.common.model.IrDocument;
import com.as400parser.rpg3.model.CalcSpec;
import com.as400parser.rpg3.model.Rpg3Content;
import com.as400parser.rpgle.model.FileSpec;
import com.as400parser.rpgle.model.FreeFormatStatement;
import com.as400parser.rpgle.model.RpgleContent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * Analyzer for extracting database file usage patterns from RPG and CL source.
 * Tracks I/O operations (READ, WRITE, UPDATE, etc.) and maps record formats to files.
 */
public class FileUsageAnalyzer {

    private final CrossReference crossReference;
    private final Gson gson = new Gson();

    public FileUsageAnalyzer(CrossReference crossReference) {
        this.crossReference = crossReference;
    }

    /**
     * Performs file usage analysis on a collection of IR documents.
     *
     * @param groupedDocs Map of source type to list of documents.
     */
    public void analyze(Map<String, List<IrDocument>> groupedDocs) {
        for (Map.Entry<String, List<IrDocument>> entry : groupedDocs.entrySet()) {
            String sourceType = entry.getKey();
            for (IrDocument doc : entry.getValue()) {
                String programName = doc.getMetadata().getSourceMember();
                processDoc(doc, programName, sourceType);
            }
        }
    }

    private void processDoc(IrDocument doc, String programName, String sourceType) {
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

    private void processRpg3(IrDocument doc, String programName) {
        Rpg3Content content = convertToRpg3(doc.getContent());
        if (content == null) return;

        Map<String, FileUsage> fileMap = new LinkedHashMap<>();
        Map<String, String> formatToFile = new HashMap<>();

        // 1. Initial Scan: File Specs (F-Specs)
        if (content.getFileSpecs() != null) {
            for (Rpg3Content.FileSpec fspec : content.getFileSpecs()) {
                String fileName = fspec.getFileName();
                FileUsage usage = new FileUsage(fileName);
                fileMap.put(fileName, usage);
                // Default format name is same as file name unless renamed
                formatToFile.put(fileName, fileName);

                if (fspec.getContinuationLines() != null) {
                    for (Rpg3Content.FileKeyword kw : fspec.getContinuationLines()) {
                        if ("RENAME".equalsIgnoreCase(kw.getKeyword())) {
                            formatToFile.put(kw.getNewName(), fileName);
                        }
                    }
                }
            }
        }

        // 2. Scan Calculation Specs for I/O Opcodes
        if (content.getCalculationSpecs() != null) {
            for (Object obj : content.getCalculationSpecs()) {
                scanRpg3FileUsage(obj, fileMap, formatToFile);
            }
        }

        if (!fileMap.isEmpty()) {
            crossReference.getFileUsage().put(programName, new ArrayList<>(fileMap.values()));
        }
    }

    private void processRpgle(IrDocument doc, String programName) {
        RpgleContent content = convertToRpgle(doc.getContent());
        if (content == null) return;

        Map<String, FileUsage> fileMap = new LinkedHashMap<>();
        Map<String, String> formatToFile = new HashMap<>();

        // 1. Scan RPGLE F-Specs
        if (content.getFileSpecs() != null) {
            for (FileSpec fspec : content.getFileSpecs()) {
                String fileName = fspec.getFileName();
                FileUsage usage = new FileUsage(fileName);
                fileMap.put(fileName, usage);
                formatToFile.put(fileName, fileName);

                if (fspec.getParsedKeywords() != null) {
                    for (FileSpec.FileKeyword kw : fspec.getParsedKeywords()) {
                        if ("RENAME".equalsIgnoreCase(kw.getKeyword())) {
                            formatToFile.put(kw.getValue(), fileName);
                        }
                    }
                }
            }
        }

        // 2. Scan Calculation Specs (Fixed)
        if (content.getCalculationSpecs() != null) {
            for (Object obj : content.getCalculationSpecs()) {
                scanRpg3FileUsage(obj, fileMap, formatToFile);
            }
        }
        
        // 3. Scan Free Format
        if (content.getFreeFormatStatements() != null) {
            for (FreeFormatStatement stmt : content.getFreeFormatStatements()) {
                scanRpgleFreeFileUsage(stmt, fileMap, formatToFile);
            }
        }

        if (!fileMap.isEmpty()) {
            crossReference.getFileUsage().put(programName, new ArrayList<>(fileMap.values()));
        }
    }

    private void scanRpg3FileUsage(Object obj, Map<String, FileUsage> fileMap, Map<String, String> formatToFile) {
        if (obj instanceof CalcSpec.Operation) {
            CalcSpec.Operation op = (CalcSpec.Operation) obj;
            String opcode = op.getOpcode() != null ? op.getOpcode().toUpperCase() : "";
            String factor2 = CrossReferenceAnalyzer.resolveName(op.getFactor2());

            IOOperation ioOp = mapOpcode(opcode);
            if (ioOp != null) {
                String fileName = formatToFile.getOrDefault(factor2, factor2);
                FileUsage usage = fileMap.get(fileName);
                if (usage == null) {
                    usage = new FileUsage(fileName);
                    fileMap.put(fileName, usage);
                }
                if (!usage.getOperations().contains(ioOp)) {
                    usage.getOperations().add(ioOp);
                }
            }
        } else if (obj instanceof CalcSpec.SubroutineBlock) {
            for (Object child : ((CalcSpec.SubroutineBlock) obj).getOperations()) scanRpg3FileUsage(child, fileMap, formatToFile);
        } else if (obj instanceof CalcSpec.ConditionalBlock) {
            CalcSpec.ConditionalBlock cb = (CalcSpec.ConditionalBlock) obj;
            for (Object child : cb.getThenOps()) scanRpg3FileUsage(child, fileMap, formatToFile);
            for (Object child : cb.getElseOps()) scanRpg3FileUsage(child, fileMap, formatToFile);
        } else if (obj instanceof CalcSpec.DoWhileBlock) {
            for (Object child : ((CalcSpec.DoWhileBlock) obj).getBodyOps()) scanRpg3FileUsage(child, fileMap, formatToFile);
        }
    }

    private void scanRpgleFreeFileUsage(FreeFormatStatement stmt, Map<String, FileUsage> fileMap, Map<String, String> formatToFile) {
        String operation = stmt.getOperation();
        String expression = stmt.getExpression();
        if (operation == null || expression == null) return;

        IOOperation ioOp = mapOpcode(operation.toUpperCase().trim());
        if (ioOp != null) {
            String target = expression.split("\\s+")[0].replace(";", "").replace("(", "").trim();
            String fileName = formatToFile.getOrDefault(target, target);
            FileUsage usage = fileMap.get(fileName);
            if (usage == null) {
                usage = new FileUsage(fileName);
                fileMap.put(fileName, usage);
            }
            if (!usage.getOperations().contains(ioOp)) {
                usage.getOperations().add(ioOp);
            }
        }
    }

    private void processCl(IrDocument doc, String programName) {
        ClContent content = convertToCl(doc.getContent());
        if (content == null) return;

        List<FileUsage> usages = new ArrayList<>();
        if (content.getFileDeclarations() != null) {
            for (ClFileDeclaration dclf : content.getFileDeclarations()) {
                FileUsage usage = new FileUsage(dclf.getFileName());
                usage.getOperations().add(IOOperation.READ); // CL usually reads/displays
                usages.add(usage);
            }
        }

        if (!usages.isEmpty()) {
            crossReference.getFileUsage().put(programName, usages);
        }
    }

    private IOOperation mapOpcode(String opcode) {
        try {
            switch (opcode) {
                case "READE":
                case "READP":
                case "REDPE":
                    return IOOperation.READ;
                default:
                    return IOOperation.valueOf(opcode);
            }
        } catch (IllegalArgumentException e) {
            return null;
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
}
