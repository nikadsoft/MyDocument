package net.nikad.mydocument.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Document {

    private Path path;
    private String content;
    private boolean dirty;

    public Document() {
        this.content = "";
        this.dirty = false;
    }

    public Document(Path path, String content) {
        this.path = Objects.requireNonNull(path);
        this.content = Objects.requireNonNull(content);
        this.dirty = false;
    }

    public static Document fromPath(Path path) throws IOException {
        String content = Files.readString(path);
        return new Document(path, content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        Objects.requireNonNull(content);
        if (!content.equals(this.content)) {
            this.content = content;
            this.dirty = true;
        }
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = Objects.requireNonNull(path);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markClean() {
        this.dirty = false;
    }

    public boolean isNew() {
        return path == null;
    }

    public String getTitle() {
        if (path == null) {
            return "Untitled";
        }
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
