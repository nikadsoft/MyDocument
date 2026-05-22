package net.nikad.mydocument.service;

import net.nikad.mydocument.model.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService();
    }

    @Test
    void openReadsFileContent(@TempDir Path dir) throws IOException {
        Path p = dir.resolve("hello.md");
        Files.writeString(p, "# Hello World");
        Document doc = fileService.open(p);
        assertEquals("# Hello World", doc.getContent());
        assertEquals(p, doc.getPath());
        assertFalse(doc.isDirty());
    }

    @Test
    void openNonExistentFileThrows(@TempDir Path dir) {
        assertThrows(IOException.class,
                () -> fileService.open(dir.resolve("missing.md")));
    }

    @Test
    void saveWritesContentToDisk(@TempDir Path dir) throws IOException {
        Path p = dir.resolve("out.md");
        Files.writeString(p, "original");
        Document doc = fileService.open(p);
        doc.setContent("updated content");
        fileService.save(doc);
        assertEquals("updated content", Files.readString(p));
        assertFalse(doc.isDirty());
    }

    @Test
    void saveThrowsForNewDocument() {
        Document doc = new Document();
        doc.setContent("some text");
        assertThrows(IllegalStateException.class, () -> fileService.save(doc));
    }

    @Test
    void saveAsCreatesNewFile(@TempDir Path dir) throws IOException {
        Document doc = new Document();
        doc.setContent("# New");
        Path target = dir.resolve("target.md");
        fileService.saveAs(doc, target);
        assertTrue(Files.exists(target));
        assertEquals("# New", Files.readString(target));
        assertEquals(target, doc.getPath());
        assertFalse(doc.isDirty());
    }

    @Test
    void saveAsOverwritesExistingFile(@TempDir Path dir) throws IOException {
        Path p = dir.resolve("existing.md");
        Files.writeString(p, "old content");
        Document doc = new Document();
        doc.setContent("new content");
        fileService.saveAs(doc, p);
        assertEquals("new content", Files.readString(p));
    }

    @Test
    void saveAsUpdatesDocumentPath(@TempDir Path dir) throws IOException {
        Document doc = new Document();
        doc.setContent("text");
        Path newPath = dir.resolve("moved.md");
        fileService.saveAs(doc, newPath);
        assertEquals(newPath, doc.getPath());
    }

    @Test
    void roundTripPreservesContent(@TempDir Path dir) throws IOException {
        Path p = dir.resolve("roundtrip.md");
        String content = "# Title\n\nParagraph with **bold** and *italic*.\n\n- item 1\n- item 2\n";
        Files.writeString(p, content);
        Document loaded = fileService.open(p);
        assertEquals(content, loaded.getContent());
        loaded.setContent(loaded.getContent() + "\nExtra line");
        fileService.save(loaded);
        Document reloaded = fileService.open(p);
        assertEquals(loaded.getContent(), reloaded.getContent());
    }
}
