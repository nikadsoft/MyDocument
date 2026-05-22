package net.nikad.mydocument.view;

import net.nikad.mydocument.model.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TitleFormatterTest {

    @Test
    void newDocumentWindowTitle() {
        Document doc = new Document();
        assertEquals("Untitled — MyDocument", TitleFormatter.windowTitle(doc));
    }

    @Test
    void newDocumentTabTitle() {
        Document doc = new Document();
        assertEquals("Untitled", TitleFormatter.tabTitle(doc));
    }

    @Test
    void dirtyDocumentShowsAsterisk(@TempDir Path dir) {
        Document doc = new Document(dir.resolve("notes.md"), "");
        doc.setContent("changed");
        assertEquals("notes *", TitleFormatter.tabTitle(doc));
        assertEquals("notes * — MyDocument", TitleFormatter.windowTitle(doc));
    }

    @Test
    void cleanDocumentNoAsterisk(@TempDir Path dir) {
        Document doc = new Document(dir.resolve("notes.md"), "content");
        assertEquals("notes", TitleFormatter.tabTitle(doc));
    }

    @Test
    void afterMarkCleanNoAsterisk(@TempDir Path dir) {
        Document doc = new Document(dir.resolve("file.md"), "");
        doc.setContent("changed");
        doc.markClean();
        assertEquals("file", TitleFormatter.tabTitle(doc));
        assertEquals("file — MyDocument", TitleFormatter.windowTitle(doc));
    }

    @Test
    void untitledDirtyDocumentShowsAsterisk() {
        Document doc = new Document();
        doc.setContent("something");
        assertEquals("Untitled *", TitleFormatter.tabTitle(doc));
    }
}
