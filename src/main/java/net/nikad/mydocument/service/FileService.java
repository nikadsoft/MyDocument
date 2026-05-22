package net.nikad.mydocument.service;

import net.nikad.mydocument.model.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileService {

    public Document open(Path path) throws IOException {
        return Document.fromPath(path);
    }

    public void save(Document document) throws IOException {
        if (document.isNew()) {
            throw new IllegalStateException("Cannot save a new document without a path");
        }
        write(document.getPath(), document.getContent());
        document.markClean();
    }

    public void saveAs(Document document, Path path) throws IOException {
        write(path, document.getContent());
        document.setPath(path);
        document.markClean();
    }

    private void write(Path path, String content) throws IOException {
        Files.writeString(path, content,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }
}
