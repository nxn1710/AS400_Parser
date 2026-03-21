package com.as400parser.dds;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.model.Metadata;
import com.as400parser.dds.model.*;

import java.util.*;

/**
 * Post-processor that resolves REFFLD references across parsed DDS files.
 * <p>
 * Given a collection of parsed IrDocuments, builds a field lookup map
 * (filename → fieldName → FieldDefinition) and resolves each reference field's
 * {@code dataType}, {@code length}, and {@code decimalPositions} from the
 * referenced source field.
 * <p>
 * Resolution order:
 * <ol>
 *   <li>Field-level REFFLD 2nd arg ({@code referenceFile}) takes precedence</li>
 *   <li>Falls back to file-level REF keyword ({@code value})</li>
 * </ol>
 */
public class DdsRefResolver {

    /**
     * Build a field lookup map from a list of parsed IrDocuments.
     * Key: uppercase filename (e.g., "FLDREFPF"), Value: map of fieldName → FieldDefinition.
     * Only PF documents contribute to the lookup.
     */
    public Map<String, Map<String, FieldDefinition>> buildFieldLookup(List<IrDocument> documents) {
        Map<String, Map<String, FieldDefinition>> lookup = new HashMap<>();

        for (IrDocument doc : documents) {
            if (doc.getContent() instanceof DdsPfContent pfContent) {
                String fileName = extractFileName(doc);
                if (fileName == null) continue;

                Map<String, FieldDefinition> fields = new HashMap<>();
                for (RecordFormat rf : pfContent.getRecordFormats()) {
                    for (FieldDefinition fd : rf.getFields()) {
                        if (fd.getName() != null) {
                            fields.put(fd.getName().toUpperCase(), fd);
                        }
                    }
                }
                lookup.put(fileName.toUpperCase(), fields);
            }
        }

        return lookup;
    }

    /**
     * Resolve REFFLD references in a single IrDocument using the field lookup.
     * For each field with {@code source="reference"}, looks up the referenced
     * field and copies {@code dataType}, {@code length}, {@code decimalPositions}.
     *
     * @param document    the document to resolve references in
     * @param fieldLookup the lookup map built by {@link #buildFieldLookup}
     * @return number of fields resolved
     */
    public int resolve(IrDocument document, Map<String, Map<String, FieldDefinition>> fieldLookup) {
        int resolved = 0;

        if (document.getContent() instanceof DdsPfContent pfContent) {
            String fileRefName = extractFileRefName(pfContent.getFileKeywords());
            for (RecordFormat rf : pfContent.getRecordFormats()) {
                resolved += resolveFields(rf.getFields(), fileRefName, fieldLookup);
            }
        } else if (document.getContent() instanceof DdsLfContent lfContent) {
            String fileRefName = extractFileRefName(lfContent.getFileKeywords());
            for (LfRecordFormat lrf : lfContent.getRecordFormats()) {
                resolved += resolveFields(lrf.getFields(), fileRefName, fieldLookup);
            }
        }

        return resolved;
    }

    /**
     * Resolve all REFFLD references in a list of IrDocuments.
     * Convenience method that builds the lookup and resolves all documents.
     *
     * @param documents all parsed documents
     * @return total number of fields resolved
     */
    public int resolveAll(List<IrDocument> documents) {
        Map<String, Map<String, FieldDefinition>> lookup = buildFieldLookup(documents);
        int total = 0;
        for (IrDocument doc : documents) {
            total += resolve(doc, lookup);
        }
        return total;
    }

    // --- Internal helpers ---

    private int resolveFields(List<FieldDefinition> fields, String fileRefName,
                               Map<String, Map<String, FieldDefinition>> fieldLookup) {
        int resolved = 0;
        for (FieldDefinition field : fields) {
            if (!"reference".equals(field.getSource())) continue;
            if (field.getReferenceField() == null) continue;

            // Determine which file to look up in
            String targetFile = field.getReferenceFile() != null
                    ? field.getReferenceFile()
                    : fileRefName;
            if (targetFile == null) continue;

            // Strip library qualifier if present (e.g., "MYLIB/FLDREFPF" → "FLDREFPF")
            String cleanFile = stripLibrary(targetFile).toUpperCase();

            Map<String, FieldDefinition> fileFields = fieldLookup.get(cleanFile);
            if (fileFields == null) continue;

            FieldDefinition refField = fileFields.get(field.getReferenceField().toUpperCase());
            if (refField == null) continue;

            // Copy resolved attributes (only if not already explicitly set)
            if (field.getDataType() == null && refField.getDataType() != null) {
                field.setDataType(refField.getDataType());
            }
            if (field.getLength() == null && refField.getLength() != null) {
                field.setLength(refField.getLength());
            }
            if (field.getDecimalPositions() == null && refField.getDecimalPositions() != null) {
                field.setDecimalPositions(refField.getDecimalPositions());
            }
            resolved++;
        }
        return resolved;
    }

    /**
     * Extract the file-level REF keyword value (the default reference file).
     */
    private String extractFileRefName(List<DdsKeyword> fileKeywords) {
        if (fileKeywords == null) return null;
        for (DdsKeyword kw : fileKeywords) {
            if ("REF".equals(kw.getName()) && kw.getValue() != null) {
                return kw.getValue();
            }
        }
        return null;
    }

    /**
     * Extract sourceMember from document metadata as the filename key.
     */
    private String extractFileName(IrDocument doc) {
        Metadata meta = doc.getMetadata();
        if (meta != null && meta.getSourceMember() != null) {
            return meta.getSourceMember();
        }
        return null;
    }

    /**
     * Strip library qualifier from a file reference.
     * e.g., "MYLIB/FLDREFPF" → "FLDREFPF", "*LIBL/FLDREFPF" → "FLDREFPF"
     */
    private String stripLibrary(String ref) {
        int slashIdx = ref.indexOf('/');
        return slashIdx >= 0 ? ref.substring(slashIdx + 1) : ref;
    }
}
