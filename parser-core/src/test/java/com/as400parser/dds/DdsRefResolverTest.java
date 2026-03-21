package com.as400parser.dds;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.model.Metadata;
import com.as400parser.dds.model.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DdsRefResolver}.
 * Tests cross-file REFFLD resolution using a field lookup map.
 */
class DdsRefResolverTest {

    private final DdsRefResolver resolver = new DdsRefResolver();

    // ============================================================
    // buildFieldLookup
    // ============================================================

    @Test
    void buildFieldLookup_extractsFieldsFromPf() {
        IrDocument refDoc = createRefFilePf();
        Map<String, Map<String, FieldDefinition>> lookup = resolver.buildFieldLookup(List.of(refDoc));

        assertThat(lookup).containsKey("FLDREFPF");
        Map<String, FieldDefinition> fields = lookup.get("FLDREFPF");
        assertThat(fields).containsKeys("RSTUID", "RNAME", "RDATE");

        FieldDefinition rstuid = fields.get("RSTUID");
        assertThat(rstuid.getLength()).isEqualTo(6);
        assertThat(rstuid.getDataType()).isEqualTo("A");
    }

    @Test
    void buildFieldLookup_ignoresLfDocuments() {
        IrDocument lfDoc = createSimpleLf();
        Map<String, Map<String, FieldDefinition>> lookup = resolver.buildFieldLookup(List.of(lfDoc));
        assertThat(lookup).isEmpty();
    }

    // ============================================================
    // resolve — 1-arg REFFLD (file-level REF)
    // ============================================================

    @Test
    void resolve_singleArgReffld_usesFileLevelRef() {
        IrDocument refDoc = createRefFilePf();
        IrDocument targetDoc = createTargetPf_singleArgReffld();

        Map<String, Map<String, FieldDefinition>> lookup = resolver.buildFieldLookup(List.of(refDoc));
        int count = resolver.resolve(targetDoc, lookup);

        assertThat(count).isEqualTo(2);

        DdsPfContent content = (DdsPfContent) targetDoc.getContent();
        List<FieldDefinition> fields = content.getRecordFormats().get(0).getFields();

        // TCHID refs RSTUID (6A)
        FieldDefinition tchid = fields.get(0);
        assertThat(tchid.getDataType()).isEqualTo("A");
        assertThat(tchid.getLength()).isEqualTo(6);
        assertThat(tchid.getDecimalPositions()).isNull(); // char field, no decimals

        // TCHBDT refs RDATE (8S0)
        FieldDefinition tchbdt = fields.get(1);
        assertThat(tchbdt.getDataType()).isEqualTo("S");
        assertThat(tchbdt.getLength()).isEqualTo(8);
        assertThat(tchbdt.getDecimalPositions()).isEqualTo(0);
    }

    // ============================================================
    // resolve — 2-arg REFFLD (explicit file)
    // ============================================================

    @Test
    void resolve_twoArgReffld_overridesFileLevelRef() {
        IrDocument refDoc1 = createRefFilePf();
        IrDocument refDoc2 = createAnotherPf();
        IrDocument targetDoc = createTargetPf_twoArgReffld();

        Map<String, Map<String, FieldDefinition>> lookup =
                resolver.buildFieldLookup(List.of(refDoc1, refDoc2));
        int count = resolver.resolve(targetDoc, lookup);

        assertThat(count).isEqualTo(1);

        DdsPfContent content = (DdsPfContent) targetDoc.getContent();
        FieldDefinition field = content.getRecordFormats().get(0).getFields().get(0);

        // Should resolve from OTHERPF, not FLDREFPF
        assertThat(field.getDataType()).isEqualTo("P");
        assertThat(field.getLength()).isEqualTo(9);
        assertThat(field.getDecimalPositions()).isEqualTo(2);
    }

    // ============================================================
    // resolve — unresolved reference (no crash)
    // ============================================================

    @Test
    void resolve_unresolvedReference_fieldUnchanged() {
        IrDocument targetDoc = createTargetPf_singleArgReffld();
        // Empty lookup — nothing to resolve from
        Map<String, Map<String, FieldDefinition>> lookup = resolver.buildFieldLookup(List.of());

        int count = resolver.resolve(targetDoc, lookup);

        assertThat(count).isEqualTo(0);

        DdsPfContent content = (DdsPfContent) targetDoc.getContent();
        FieldDefinition field = content.getRecordFormats().get(0).getFields().get(0);
        // Fields remain null (unresolved)
        assertThat(field.getDataType()).isNull();
        assertThat(field.getLength()).isNull();
    }

    // ============================================================
    // resolve — field with explicit dataType not overwritten
    // ============================================================

    @Test
    void resolve_explicitDataType_notOverwritten() {
        IrDocument refDoc = createRefFilePf();
        IrDocument targetDoc = createTargetPf_withExplicitType();

        Map<String, Map<String, FieldDefinition>> lookup = resolver.buildFieldLookup(List.of(refDoc));
        int count = resolver.resolve(targetDoc, lookup);

        assertThat(count).isEqualTo(1);

        DdsPfContent content = (DdsPfContent) targetDoc.getContent();
        FieldDefinition field = content.getRecordFormats().get(0).getFields().get(0);
        // Explicit dataType/length should NOT be overwritten
        assertThat(field.getDataType()).isEqualTo("P"); // kept original
        assertThat(field.getLength()).isEqualTo(10);     // kept original
    }

    // ============================================================
    // resolveAll convenience
    // ============================================================

    @Test
    void resolveAll_resolvesAcrossDocuments() {
        IrDocument refDoc = createRefFilePf();
        IrDocument targetDoc = createTargetPf_singleArgReffld();

        int count = resolver.resolveAll(List.of(refDoc, targetDoc));
        assertThat(count).isEqualTo(2);
    }

    // ============================================================
    // Helpers — create test IrDocuments
    // ============================================================

    /** FLDREFPF: reference file with RSTUID(6A), RNAME(30A), RDATE(8S0) */
    private IrDocument createRefFilePf() {
        DdsPfContent content = new DdsPfContent();

        RecordFormat rf = new RecordFormat();
        rf.setName("REFREC");
        rf.setFields(new ArrayList<>(List.of(
                createDirectField("RSTUID", 6, "A", null),
                createDirectField("RNAME", 30, "A", null),
                createDirectField("RDATE", 8, "S", 0)
        )));
        content.setRecordFormats(List.of(rf));

        return wrapInDocument(content, "FLDREFPF", "DDS_PF");
    }

    /** OTHERPF: another file with AMOUNT(9P2) */
    private IrDocument createAnotherPf() {
        DdsPfContent content = new DdsPfContent();

        RecordFormat rf = new RecordFormat();
        rf.setName("OTHERREC");
        rf.setFields(new ArrayList<>(List.of(
                createDirectField("AMOUNT", 9, "P", 2)
        )));
        content.setRecordFormats(List.of(rf));

        return wrapInDocument(content, "OTHERPF", "DDS_PF");
    }

    /** Target PF using REF(FLDREFPF) + REFFLD(fieldname) — single arg */
    private IrDocument createTargetPf_singleArgReffld() {
        DdsPfContent content = new DdsPfContent();

        // File-level REF(FLDREFPF)
        DdsKeyword refKw = new DdsKeyword();
        refKw.setName("REF");
        refKw.setValue("FLDREFPF");
        content.setFileKeywords(List.of(refKw));

        RecordFormat rf = new RecordFormat();
        rf.setName("TCHREC");
        rf.setFields(new ArrayList<>(List.of(
                createRefField("TCHID", "RSTUID", null),
                createRefField("TCHBDT", "RDATE", null)
        )));
        content.setRecordFormats(List.of(rf));

        return wrapInDocument(content, "TEACHPF", "DDS_PF");
    }

    /** Target PF using REFFLD(fieldname filename) — 2-arg, explicit file */
    private IrDocument createTargetPf_twoArgReffld() {
        DdsPfContent content = new DdsPfContent();

        // File-level REF(FLDREFPF) — should be overridden by field-level
        DdsKeyword refKw = new DdsKeyword();
        refKw.setName("REF");
        refKw.setValue("FLDREFPF");
        content.setFileKeywords(List.of(refKw));

        RecordFormat rf = new RecordFormat();
        rf.setName("TESTREC");
        rf.setFields(new ArrayList<>(List.of(
                createRefField("MYAMT", "AMOUNT", "OTHERPF")
        )));
        content.setRecordFormats(List.of(rf));

        return wrapInDocument(content, "TESTPF", "DDS_PF");
    }

    /** Target PF with explicit dataType set — should not be overwritten */
    private IrDocument createTargetPf_withExplicitType() {
        DdsPfContent content = new DdsPfContent();

        DdsKeyword refKw = new DdsKeyword();
        refKw.setName("REF");
        refKw.setValue("FLDREFPF");
        content.setFileKeywords(List.of(refKw));

        RecordFormat rf = new RecordFormat();
        rf.setName("TESTREC");

        // Field with explicit type already set
        FieldDefinition fd = createRefField("MYID", "RSTUID", null);
        fd.setDataType("P");
        fd.setLength(10);

        rf.setFields(new ArrayList<>(List.of(fd)));
        content.setRecordFormats(List.of(rf));

        return wrapInDocument(content, "TESTPF2", "DDS_PF");
    }

    private IrDocument createSimpleLf() {
        DdsLfContent content = new DdsLfContent();
        return wrapInDocument(content, "TESTLF", "DDS_LF");
    }

    private FieldDefinition createDirectField(String name, int length, String dataType, Integer decimals) {
        FieldDefinition fd = new FieldDefinition();
        fd.setName(name);
        fd.setLength(length);
        fd.setDataType(dataType);
        fd.setDecimalPositions(decimals);
        fd.setSource("direct");
        return fd;
    }

    private FieldDefinition createRefField(String name, String refFieldName, String refFileName) {
        FieldDefinition fd = new FieldDefinition();
        fd.setName(name);
        fd.setReference("R");
        fd.setSource("reference");
        fd.setReferenceField(refFieldName);
        fd.setReferenceFile(refFileName);

        DdsKeyword reffld = new DdsKeyword();
        reffld.setName("REFFLD");
        reffld.setReferenceField(refFieldName);
        reffld.setReferenceFile(refFileName);
        if (refFileName != null) {
            reffld.setValues(List.of(refFieldName, refFileName));
        } else {
            reffld.setValue(refFieldName);
        }
        fd.setKeywords(List.of(reffld));

        return fd;
    }

    private IrDocument wrapInDocument(Object content, String memberName, String sourceType) {
        IrDocument doc = new IrDocument();
        doc.setContent(content);

        Metadata meta = new Metadata();
        meta.setSourceMember(memberName);
        meta.setSourceType(sourceType);
        doc.setMetadata(meta);

        return doc;
    }
}
