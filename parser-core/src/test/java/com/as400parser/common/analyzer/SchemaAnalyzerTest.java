package com.as400parser.common.analyzer;

import com.as400parser.common.analyzer.model.DatabaseSchema;
import com.as400parser.common.analyzer.model.TableDefinition;
import com.as400parser.common.analyzer.model.IndexDefinition;
import com.as400parser.common.analyzer.model.ColumnDefinition;
import com.as400parser.common.model.IrDocument;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SchemaAnalyzerTest {

    @Test
    void testDdsToSqlTypeMapper() {
        assertEquals("VARCHAR(10)", DdsToSqlTypeMapper.mapToSqlType("A", 10, null));
        assertEquals("NUMERIC(9, 2)", DdsToSqlTypeMapper.mapToSqlType("P", 9, 2));
        assertEquals("NUMERIC(5)", DdsToSqlTypeMapper.mapToSqlType("S", 5, 0));
        assertEquals("INTEGER", DdsToSqlTypeMapper.mapToSqlType("B", 8, null));
        assertEquals("SMALLINT", DdsToSqlTypeMapper.mapToSqlType("B", 4, null));
        assertEquals("DATE", DdsToSqlTypeMapper.mapToSqlType("L", 10, null));
    }

    @Test
    void testAnalyzeSchemaFromIrDocs() throws Exception {
        // Load the parsed JSON IR outputs output from previous parsing phases
        // Using sample outputs inside output/QDDSSRC/
        Path rootPath = Paths.get("..", "..", "output", "QDDSSRC");
        // For test runtime paths starting from parser-core, the root happens to be 3 levels up
        Path basePath = Paths.get("../../../output/QDDSSRC").toAbsolutePath().normalize();
        
        IrDocumentLoader loader = new IrDocumentLoader();
        Map<String, List<IrDocument>> groupedDocs = loader.loadFromDirectory(basePath);
        
        List<IrDocument> pfDocs = groupedDocs.get("DDS_PF");
        List<IrDocument> lfDocs = groupedDocs.get("DDS_LF");
        
        assertNotNull(pfDocs, "PF Docs should not be null");
        assertNotNull(lfDocs, "LF Docs should not be null");

        List<IrDocument> allDds = new java.util.ArrayList<>();
        allDds.addAll(pfDocs);
        allDds.addAll(lfDocs);

        SchemaAnalyzer analyzer = new SchemaAnalyzer();
        DatabaseSchema schema = analyzer.analyze(allDds);

        // Verify PF Schema
        assertFalse(schema.getTables().isEmpty(), "Should have parsed some tables");
        
        TableDefinition studentTarget = schema.getTables().stream()
            .filter(t -> "STUDNTPF".equals(t.getName()))
            .findFirst()
            .orElse(null);

        assertNotNull(studentTarget, "Should extract STUDNTPF table");
        assertEquals("STUREC", studentTarget.getRecordFormat());
        assertTrue(studentTarget.isUnique(), "STUDNTPF has UNIQUE keyword");
        assertEquals(2, studentTarget.getPrimaryKey().size(), "STUDNTPF has 2 key fields");
        assertEquals("STUSCL", studentTarget.getPrimaryKey().get(0));
        assertEquals("STUID", studentTarget.getPrimaryKey().get(1));

        ColumnDefinition stuGnd = studentTarget.getColumns().stream()
            .filter(c -> "STUGND".equals(c.getName()))
            .findFirst()
            .orElse(null);
            
        assertNotNull(stuGnd, "Should extract STUGND column");
        assertEquals("A", stuGnd.getDdsType());
        assertEquals("VARCHAR(1)", stuGnd.getSqlType());
        assertTrue(stuGnd.getConstraints().get(0).contains("VALUES("), "Should have VALUES constraint");
        
        // Verify LF Schema
        IndexDefinition studentIndex = schema.getIndexes().stream()
            .filter(i -> "STUDNTL1".equals(i.getName()))
            .findFirst()
            .orElse(null);

        assertNotNull(studentIndex, "Should extract STUDNTL1 index");
        assertEquals("STUDNTPF", studentIndex.getTableName(), "Should link to PFILE STUDNTPF");
        assertEquals(2, studentIndex.getKeyFields().size());
        assertEquals("STUNAM", studentIndex.getKeyFields().get(0).getFieldName());
        
        IndexDefinition lfSelectOmit = schema.getIndexes().stream()
            .filter(i -> "STUDNTL2".equals(i.getName()))
            .findFirst()
            .orElse(null);

        assertNotNull(lfSelectOmit, "Should extract STUDNTL2 index");
        assertFalse(lfSelectOmit.getSelectOmitRules().isEmpty(), "Should have select/omit rules");
    }
}
