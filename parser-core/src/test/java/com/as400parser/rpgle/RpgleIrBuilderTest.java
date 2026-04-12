package com.as400parser.rpgle;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.rpgle.model.FreeFormatStatement;
import com.as400parser.rpgle.model.RpgleContent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RpgleIrBuilder orchestration and IR assembly.
 */
class RpgleIrBuilderTest {

    private IrDocument build(String source) {
        SourceNormalizer normalizer = new SourceNormalizer(SourceNormalizer.SEU_TAB_STOPS, false);
        NormalizedSource normalized = normalizer.normalize(source);
        return new RpgleIrBuilder(normalized).build();
    }

    @Test
    void build_fullyFreeSource_usesFreeParserAndBuildsSourceLines() {
        IrDocument doc = build(String.join("\n",
                "**FREE",
                "CTL-OPT DATFMT(*ISO);",
                "DCL-S Counter PACKED(5:0);"
        ));

        assertThat(doc.getContent()).isInstanceOf(RpgleContent.class);
        RpgleContent content = (RpgleContent) doc.getContent();

        assertThat(content.getControlSpecs()).hasSize(1);
        assertThat(content.getDefinitionSpecs()).hasSize(1);
        assertThat(content.getSourceLines()).hasSize(3);
        assertThat(content.getSourceLines().get(0).getLineNumber()).isEqualTo(1);
        assertThat(content.getSourceLines().get(1).isBlank()).isFalse();
    }

    @Test
    void build_mixedFormat_parsesFreeBlockAndCopyDirective() {
        IrDocument doc = build(String.join("\n",
                "     H DATFMT(*ISO)",
                "      /COPY QCPYSRC,STUDNTCPY",
                "      /FREE",
                "      DCL-S Counter PACKED(5:0);",
                "      IF Counter = 0;",
                "      ENDIF;",
                "      /END-FREE"
        ));

        RpgleContent content = (RpgleContent) doc.getContent();

        assertThat(content.getControlSpecs()).hasSize(1);
        assertThat(content.getDefinitionSpecs())
                .anyMatch(def -> "Counter".equals(def.getName()) && "S".equals(def.getDefinitionType()));
        assertThat(content.getFreeFormatStatements())
                .extracting(FreeFormatStatement::getNodeType)
                .contains("if", "endBlock");

        assertThat(doc.getDependencies().getCopyMembers()).hasSize(1);
        IrDocument.CopyMemberRef copyRef = doc.getDependencies().getCopyMembers().get(0);
        assertThat(copyRef.getDirective()).isEqualTo("/COPY");
        assertThat(copyRef.getFileName()).isEqualTo("QCPYSRC");
        assertThat(copyRef.getMemberName()).isEqualTo("STUDNTCPY");
        assertThat(copyRef.isResolved()).isFalse();
    }

    @Test
    void build_fixedFormat_extractsDependenciesSymbolTableAndSubroutineCalls() {
        IrDocument doc = build(String.join("\n",
                "     FSTUDNTPF  IF   E           K DISK",
                "     D Counter         S              5P 0",
                "     C     *IN03         IFEQ      *ON",
                "     C                   EXSR      LOADSR",
                "     C                   ENDIF",
                "     C     LOADSR        BEGSR",
                "     C                   SETON                                        LR",
                "     C                   ENDSR"
        ));

        RpgleContent content = (RpgleContent) doc.getContent();

        assertThat(doc.getDependencies().getReferencedFiles())
                .anyMatch(ref -> "STUDNTPF".equals(ref.getName()) && "input".equals(ref.getReferenceType()));

        assertThat(content.getSymbolTable())
                .anyMatch(symbol -> "Counter".equals(symbol.getName()) && "D-spec".equals(symbol.getDefinedIn()));

        assertThat(content.getSubroutines()).hasSize(1);
        assertThat(content.getSubroutines().get(0).getSubroutineName()).isEqualTo("LOADSR");
        assertThat(content.getSubroutines().get(0).getCalledFrom()).containsExactly(4);
    }
}
