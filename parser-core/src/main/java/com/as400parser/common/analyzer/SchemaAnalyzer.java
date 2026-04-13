package com.as400parser.common.analyzer;

import com.as400parser.common.analyzer.model.ColumnDefinition;
import com.as400parser.common.analyzer.model.DatabaseSchema;
import com.as400parser.common.analyzer.model.IndexDefinition;
import com.as400parser.common.analyzer.model.KeyFieldDef;
import com.as400parser.common.analyzer.model.SelectOmitRule;
import com.as400parser.common.analyzer.model.TableDefinition;
import com.as400parser.common.model.IrDocument;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extracts normalized relational schema definitions (Tables, Indexes)
 * from DDS IR documents (PF and LF).
 */
public class SchemaAnalyzer {
    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

    /**
     * Analyzes a collection of DDS IR Documents and returns a combined relational schema.
     *
     * @param irDocs List of parsed IR documents (PF and LF mixed).
     * @return DatabaseSchema containing normalized Tables and Indexes.
     */
    public DatabaseSchema analyze(List<IrDocument> irDocs) {
        DatabaseSchema schema = new DatabaseSchema();

        for (IrDocument doc : irDocs) {
            if (doc.getMetadata() == null || doc.getMetadata().getSourceType() == null) continue;

            String sourceType = doc.getMetadata().getSourceType().toUpperCase();
            if ("DDS_PF".equals(sourceType)) {
                TableDefinition table = processPhysicalFile(doc);
                if (table != null) {
                    schema.getTables().add(table);
                }
            } else if ("DDS_LF".equals(sourceType)) {
                IndexDefinition index = processLogicalFile(doc);
                if (index != null) {
                    schema.getIndexes().add(index);
                }
            }
        }
        return schema;
    }

    @SuppressWarnings("unchecked")
    private TableDefinition processPhysicalFile(IrDocument doc) {
        TableDefinition table = new TableDefinition();
        table.setName(doc.getMetadata().getSourceMember());

        Map<String, Object> content = convertToMap(doc.getContent());
        if (content == null) return null;

        // Extract File Keywords (e.g. UNIQUE)
        List<Map<String, Object>> fileKeywords = (List<Map<String, Object>>) content.get("fileKeywords");
        if (fileKeywords != null) {
            for (Map<String, Object> kw : fileKeywords) {
                String name = (String) kw.get("name");
                if ("UNIQUE".equalsIgnoreCase(name)) {
                    table.setUnique(true);
                }
                table.getFileKeywords().put(name, (String) kw.get("rawText"));
            }
        }

        // Extract Record Formats and Columns
        List<Map<String, Object>> formats = (List<Map<String, Object>>) content.get("recordFormats");
        if (formats == null || formats.isEmpty()) return table;

        Map<String, Object> firstFormat = formats.get(0);
        table.setRecordFormat((String) firstFormat.get("name"));

        // Extract TEXT keyword for description
        List<Map<String, Object>> formatKeywords = (List<Map<String, Object>>) firstFormat.get("keywords");
        table.setDescription(getKeywordValue(formatKeywords, "TEXT"));

        // Extract Fields -> Columns
        List<Map<String, Object>> fields = (List<Map<String, Object>>) firstFormat.get("fields");
        if (fields != null) {
            for (Map<String, Object> field : fields) {
                ColumnDefinition col = extractColumn(field);
                table.getColumns().add(col);
            }
        }

        // Extract Keys
        List<Map<String, Object>> keys = (List<Map<String, Object>>) firstFormat.get("keys");
        if (keys != null) {
            for (Map<String, Object> key : keys) {
                String fieldName = (String) key.get("fieldName");
                if (fieldName != null) {
                    table.getPrimaryKey().add(fieldName);
                }
            }
        }

        return table;
    }

    @SuppressWarnings("unchecked")
    private IndexDefinition processLogicalFile(IrDocument doc) {
        IndexDefinition index = new IndexDefinition();
        index.setName(doc.getMetadata().getSourceMember());

        Map<String, Object> content = convertToMap(doc.getContent());
        if (content == null) return null;

        List<Map<String, Object>> formats = (List<Map<String, Object>>) content.get("recordFormats");
        if (formats == null || formats.isEmpty()) return index;

        Map<String, Object> firstFormat = formats.get(0);
        
        // PFILE reference
        String pfile = (String) firstFormat.get("pfile");
        if (pfile == null) {
            // Backup check via keywords
            pfile = getKeywordValue((List<Map<String, Object>>) firstFormat.get("keywords"), "PFILE");
        }
        index.setTableName(pfile);

        // LF Type Check
        String lfType = (String) firstFormat.get("lfType");
        if ("join".equalsIgnoreCase(lfType)) {
            // Handle JFILE basic support
            String jfile = getKeywordValue((List<Map<String, Object>>) firstFormat.get("keywords"), "JFILE");
            if (jfile != null) {
                index.setTableName(jfile); // Basic fallback to the JFILE defined target
            }
            index.setType("JOIN");
        } else if ("select-omit".equalsIgnoreCase(lfType) || firstFormat.containsKey("selectOmit")) {
            index.setType("SELECT_OMIT");
        } else {
            index.setType("SIMPLE");
        }

        // Keys
        List<Map<String, Object>> keys = (List<Map<String, Object>>) firstFormat.get("keys");
        if (keys != null) {
            for (Map<String, Object> key : keys) {
                String fieldName = (String) key.get("fieldName");
                String order = (String) key.get("sortOrder");
                index.getKeyFields().add(new KeyFieldDef(fieldName, order != null ? order.toUpperCase() : "ASC"));
            }
        }

        // Select/Omit Rules
        List<Map<String, Object>> rules = (List<Map<String, Object>>) firstFormat.get("selectOmit");
        if (rules != null) {
            for (Map<String, Object> rule : rules) {
                SelectOmitRule r = new SelectOmitRule();
                r.setType((String) rule.get("type"));
                r.setFieldName((String) rule.get("fieldName"));
                r.setCondition((String) rule.get("condition"));
                r.setValue(String.valueOf(rule.get("value")));
                index.getSelectOmitRules().add(r);
            }
        }

        return index;
    }

    @SuppressWarnings("unchecked")
    private ColumnDefinition extractColumn(Map<String, Object> field) {
        ColumnDefinition col = new ColumnDefinition();
        col.setName((String) field.get("name"));
        
        String ddsType = (String) field.get("dataType");
        col.setDdsType(ddsType);
        
        Number lenNum = (Number) field.get("length");
        int length = lenNum != null ? lenNum.intValue() : 0;
        col.setLength(length);
        
        Number decNum = (Number) field.get("decimalPositions");
        Integer decimals = decNum != null ? decNum.intValue() : null;
        col.setDecimalPositions(decimals);
        
        col.setSqlType(DdsToSqlTypeMapper.mapToSqlType(ddsType, length, decimals));

        // Keywords mapping
        List<Map<String, Object>> keywords = (List<Map<String, Object>>) field.get("keywords");
        col.setDescription(getKeywordValue(keywords, "TEXT"));
        col.setDefaultValue(getKeywordValue(keywords, "DFT"));
        
        String colhdg = getKeywordValue(keywords, "COLHDG");
        if (colhdg != null) {
            col.setColumnHeading(colhdg);
        } else {
            List<String> values = getKeywordValuesList(keywords, "COLHDG");
            if (values != null && !values.isEmpty()) {
                col.setColumnHeading(String.join(" ", values));
            }
        }

        List<String> validValues = getKeywordValuesList(keywords, "VALUES");
        if (validValues != null && !validValues.isEmpty()) {
            col.getConstraints().add("VALUES(" + String.join(", ", validValues) + ")");
        }

        col.setNullable(false); // Default logic unless ALWNULL exists
        if (getKeywordValue(keywords, "ALWNULL") != null) {
            col.setNullable(true);
        }

        return col;
    }

    private String getKeywordValue(List<Map<String, Object>> keywords, String keywordName) {
        if (keywords == null) return null;
        for (Map<String, Object> kw : keywords) {
            if (keywordName.equalsIgnoreCase((String) kw.get("name"))) {
                return (String) kw.get("value");
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<String> getKeywordValuesList(List<Map<String, Object>> keywords, String keywordName) {
        if (keywords == null) return null;
        for (Map<String, Object> kw : keywords) {
            if (keywordName.equalsIgnoreCase((String) kw.get("name"))) {
                Object values = kw.get("values");
                if (values instanceof List) {
                    return (List<String>) values;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object content) {
        if (content == null) return null;
        if (content instanceof Map) return (Map<String, Object>) content;
        // If it's a POJO, convert via GSON to Map for uniform processing
        String json = gson.toJson(content);
        return gson.fromJson(json, mapType);
    }
}
