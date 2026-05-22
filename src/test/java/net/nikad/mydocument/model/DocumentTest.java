package net.nikad.mydocument.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    void newDocumentHasEmptyContentAndIsNotDirty() {
        Document doc = new Document();
        assertEquals("", doc.getContent());
        assertFalse(doc.isDirty());
        assertTrue(doc.isNew());
    }

    @Test
    void settingContentMarksDirty() {
        Document doc = new Document();
        doc.setContent("# Hello");
        assertTrue(doc.isDirty());
        assertEquals("# Hello", doc.getContent());
    }

    @Test
    void settingSameContentDoesNotMarkDirty() {
        Document doc = new Document();
        doc.setContent("same");
        doc.markClean();
        doc.setContent("same");
        assertFalse(doc.isDirty());
    }

    @Test
    void markCleanClearsDirtyFlag() {
        Document doc = new Document();
        doc.setContent("changed");
        assertTrue(doc.isDirty());
        doc.markClean();
        assertFalse(doc.isDirty());
    }

    @Test
    void isNewReturnsFalseWhenPathIsSet(@TempDir Path dir) {
        Path p = dir.resolve("test.md");
        Document doc = new Document(p, "content");
        assertFalse(doc.isNew());
    }

    @Test
    void getTitleStripsExtension(@TempDir Path dir) {
        Document doc = new Document(dir.resolve("my-notes.md"), "");
        assertEquals("my-notes", doc.getTitle());
    }

    @Test
    void getTitleReturnsUntitledForNewDocument() {
        assertEquals("Untitled", new Document().getTitle());
    }

    @Test
    void getTitleHandlesNoExtension(@TempDir Path dir) {
        Document doc = new Document(dir.resolve("README"), "");
        assertEquals("README", doc.getTitle());
    }

    @Test
    void fromPathLoadsContent(@TempDir Path dir) throws IOException {
        Path p = dir.resolve("sample.md");
        Files.writeString(p, "# Title\nBody text");
        Document doc = Document.fromPath(p);
        assertEquals("# Title\nBody text", doc.getContent());
        assertEquals(p, doc.getPath());
        assertFalse(doc.isDirty());
    }

    @Test
    void setPathUpdatesPath(@TempDir Path dir) {
        Document doc = new Document();
        Path p = dir.resolve("new.md");
        doc.setPath(p);
        assertEquals(p, doc.getPath());
        assertFalse(doc.isNew());
    }

    @Test
    void setContentThrowsOnNull() {
        Document doc = new Document();
        assertThrows(NullPointerException.class, () -> doc.setContent(null));
    }

    @Test
    void constructorThrowsOnNullPath() {
        assertThrows(NullPointerException.class, () -> new Document(null, ""));
    }

    @Test
    void constructorThrowsOnNullContent(@TempDir Path dir) {
        assertThrows(NullPointerException.class,
                () -> new Document(dir.resolve("x.md"), null));
    }
}
