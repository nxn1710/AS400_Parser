package com.as400parser.common.analyzer;

import com.as400parser.common.analyzer.model.AnalysisResult;
import com.as400parser.common.analyzer.model.DatabaseSchema;
import com.as400parser.common.analyzer.model.CrossReference;
import com.as400parser.common.model.IrDocument;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

/**
 * Unified facade for orchestrating the analysis phase.
 * Combines schema extraction and cross-reference analysis into a single AnalysisResult.
 */
public class AnalyzerFacade {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Executes the full analysis pipeline.
     *
     * @param irDocuments List of all parsed IR documents to analyze.
     * @return The combined AnalysisResult.
     */
    public AnalysisResult analyzeAll(List<IrDocument> irDocuments) {
        AnalysisResult result = new AnalysisResult();

        // 1. Group documents by sourceType for specialized processing
        IrDocumentLoader loader = new IrDocumentLoader();
        Map<String, List<IrDocument>> groupedDocs = loader.groupBySourceType(irDocuments);

        // 2. Database Schema Extraction (Phase 2)
        SchemaAnalyzer schemaAnalyzer = new SchemaAnalyzer();
        DatabaseSchema schema = schemaAnalyzer.analyze(irDocuments);
        result.setSchema(schema);

        // 3. Cross-Reference Analysis (Phase 3)
        CrossReferenceAnalyzer xrefAnalyzer = new CrossReferenceAnalyzer();
        CrossReference crossReference = xrefAnalyzer.analyze(groupedDocs);
        
        // 4. File Usage Analysis (Phase 3)
        FileUsageAnalyzer fileUsageAnalyzer = new FileUsageAnalyzer(crossReference);
        fileUsageAnalyzer.analyze(groupedDocs);
        
        result.setCrossReference(crossReference);

        return result;
    }

    /**
     * Serializes the analysis result to a JSON string.
     *
     * @param result The AnalysisResult to serialize.
     * @return Pretty-printed JSON string.
     */
    public String toJson(AnalysisResult result) {
        return gson.toJson(result);
    }
}
