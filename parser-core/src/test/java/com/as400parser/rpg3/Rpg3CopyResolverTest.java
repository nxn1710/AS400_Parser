package com.as400parser.rpg3;

import com.as400parser.common.model.ResolvedCopy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class Rpg3CopyResolverTest {

    @TempDir
    Path tempDir;

    // =========================================================================
    // Directive parsing
    // =========================================================================

    @Test
    void parseSimpleCopyDirective() {
        var parsed = Rpg3CopyResolver.parseDirective("/COPY QCPYSRC,COPYMBR");
        assertThat(parsed.file).isEqualTo("QCPYSRC");
        assertThat(parsed.member).isEqualTo("COPYMBR");
        assertThat(parsed.library).isNull();
    }

    @Test
    void parseLibFileMemberDirective() {
        var parsed = Rpg3CopyResolver.parseDirective("/COPY MYLIB/QCPYSRC,COPYMBR");
        assertThat(parsed.library).isEqualTo("MYLIB");
        assertThat(parsed.file).isEqualTo("QCPYSRC");
        assertThat(parsed.member).isEqualTo("COPYMBR");
    }

    @Test
    void parseMemberOnlyDirective() {
        var parsed = Rpg3CopyResolver.parseDirective("/COPY COPYMBR");
        assertThat(parsed.member).isEqualTo("COPYMBR");
        assertThat(parsed.file).isNull();
        assertThat(parsed.library).isNull();
    }

    @Test
    void parseIncludeDirective() {
        var parsed = Rpg3CopyResolver.parseDirective("/INCLUDE QCPYSRC,MEMBER1");
        assertThat(parsed.file).isEqualTo("QCPYSRC");
        assertThat(parsed.member).isEqualTo("MEMBER1");
    }

    @Test
    void parseCaseInsensitive() {
        var parsed = Rpg3CopyResolver.parseDirective("/copy qcpysrc,copymbr");
        assertThat(parsed.file).isEqualTo("QCPYSRC");
        assertThat(parsed.member).isEqualTo("COPYMBR");
    }

    // =========================================================================
    // File resolution
    // =========================================================================

    @Test
    void resolvesRpgExtension() throws IOException {
        Path copyDir = tempDir.resolve("QCPYSRC");
        Files.createDirectories(copyDir);
        Files.writeString(copyDir.resolve("COPYMBR.rpg"), "     C* COPY MEMBER CONTENT");

        Rpg3CopyResolver resolver = new Rpg3CopyResolver(List.of(tempDir), tempDir);
        ResolvedCopy result = resolver.resolve("/COPY QCPYSRC,COPYMBR");

        assertThat(result.isFound()).isTrue();
        assertThat(result.getMemberName()).isEqualTo("COPYMBR");
        assertThat(result.getContent()).contains("COPY MEMBER CONTENT");
    }

    @Test
    void resolvesRpg3Extension() throws IOException {
        Path copyDir = tempDir.resolve("QCPYSRC");
        Files.createDirectories(copyDir);
        Files.writeString(copyDir.resolve("COPYMBR.rpg3"), "     C* RPG3 CONTENT");

        Rpg3CopyResolver resolver = new Rpg3CopyResolver(List.of(tempDir), tempDir);
        ResolvedCopy result = resolver.resolve("/COPY QCPYSRC,COPYMBR");

        assertThat(result.isFound()).isTrue();
    }

    @Test
    void resolvesMbrExtension() throws IOException {
        Path copyDir = tempDir.resolve("QCPYSRC");
        Files.createDirectories(copyDir);
        Files.writeString(copyDir.resolve("COPYMBR.mbr"), "MBR CONTENT");

        Rpg3CopyResolver resolver = new Rpg3CopyResolver(List.of(tempDir), tempDir);
        ResolvedCopy result = resolver.resolve("/COPY QCPYSRC,COPYMBR");

        assertThat(result.isFound()).isTrue();
    }

    @Test
    void resolvesNoExtension() throws IOException {
        Path copyDir = tempDir.resolve("QCPYSRC");
        Files.createDirectories(copyDir);
        Files.writeString(copyDir.resolve("COPYMBR"), "NO EXT CONTENT");

        Rpg3CopyResolver resolver = new Rpg3CopyResolver(List.of(tempDir), tempDir);
        ResolvedCopy result = resolver.resolve("/COPY QCPYSRC,COPYMBR");

        assertThat(result.isFound()).isTrue();
    }

    @Test
    void rpgExtensionPrioritizedOverRpg3() throws IOException {
        Path copyDir = tempDir.resolve("QCPYSRC");
        Files.createDirectories(copyDir);
        Files.writeString(copyDir.resolve("COPYMBR.rpg"), "RPG FIRST");
        Files.writeString(copyDir.resolve("COPYMBR.rpg3"), "RPG3 SECOND");

        Rpg3CopyResolver resolver = new Rpg3CopyResolver(List.of(tempDir), tempDir);
        ResolvedCopy result = resolver.resolve("/COPY QCPYSRC,COPYMBR");

        assertThat(result.isFound()).isTrue();
        assertThat(result.getContent()).isEqualTo("RPG FIRST");
    }

    @Test
    void notFoundReturnsWarning() {
        Rpg3CopyResolver resolver = new Rpg3CopyResolver(List.of(tempDir), tempDir);
        ResolvedCopy result = resolver.resolve("/COPY QCPYSRC,NOSUCHMBR");

        assertThat(result.isFound()).isFalse();
        assertThat(result.getMemberName()).isEqualTo("NOSUCHMBR");
        assertThat(result.getWarning()).contains("not found");
    }

    @Test
    void leftToRightPathPriority() throws IOException {
        Path path1 = tempDir.resolve("path1/QCPYSRC");
        Path path2 = tempDir.resolve("path2/QCPYSRC");
        Files.createDirectories(path1);
        Files.createDirectories(path2);
        Files.writeString(path1.resolve("MBR.rpg"), "FROM PATH1");
        Files.writeString(path2.resolve("MBR.rpg"), "FROM PATH2");

        Rpg3CopyResolver resolver = new Rpg3CopyResolver(
                List.of(tempDir.resolve("path1"), tempDir.resolve("path2")),
                tempDir);
        ResolvedCopy result = resolver.resolve("/COPY QCPYSRC,MBR");

        assertThat(result.isFound()).isTrue();
        assertThat(result.getContent()).isEqualTo("FROM PATH1"); // left-to-right
    }

    @Test
    void emptyDirectiveReturnsNotFound() {
        Rpg3CopyResolver resolver = new Rpg3CopyResolver(List.of(), tempDir);
        ResolvedCopy result = resolver.resolve("");

        assertThat(result.isFound()).isFalse();
    }

    @Test
    void libFileMemberResolvesFromSourceRoot() throws IOException {
        Path libDir = tempDir.resolve("MYLIB/QCPYSRC");
        Files.createDirectories(libDir);
        Files.writeString(libDir.resolve("MBR.rpg"), "LIB CONTENT");

        Rpg3CopyResolver resolver = new Rpg3CopyResolver(List.of(), tempDir);
        ResolvedCopy result = resolver.resolve("/COPY MYLIB/QCPYSRC,MBR");

        assertThat(result.isFound()).isTrue();
        assertThat(result.getContent()).isEqualTo("LIB CONTENT");
    }
}
