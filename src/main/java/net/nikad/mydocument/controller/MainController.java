package net.nikad.mydocument.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.nikad.mydocument.model.Document;
import net.nikad.mydocument.service.FileService;
import net.nikad.mydocument.service.MarkdownRenderer;
import net.nikad.mydocument.view.EditMode;
import net.nikad.mydocument.view.TitleFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MainController {

    @FXML private TextArea sourceEditor;
    @FXML private WebView previewPane;
    @FXML private StackPane wysiwyPane;
    @FXML private WebView wysiwygEditor;
    @FXML private Label statusLabel;
    @FXML private ToggleButton btnSource;
    @FXML private ToggleButton btnPreview;
    @FXML private ToggleButton btnWysiwyg;

    private final MarkdownRenderer renderer = new MarkdownRenderer();
    private final FileService fileService = new FileService();

    private Document currentDocument = new Document();
    private EditMode currentMode = EditMode.SOURCE;
    private boolean updatingFromWysiwyg = false;

    private static final String WYSIWYG_TEMPLATE = """
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="UTF-8">
            <style>
              body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                     line-height: 1.6; color: #24292e; max-width: 860px; margin: 0 auto; padding: 24px;
                     outline: none; }
              [contenteditable]:focus { outline: none; }
              pre  { background: #f6f8fa; border-radius: 6px; padding: 16px; }
              code { font-family: monospace; font-size: 0.9em; }
              blockquote { border-left: 4px solid #dfe2e5; margin: 0; padding: 0 16px; color: #6a737d; }
              table { border-collapse: collapse; width: 100%%; }
              th, td { border: 1px solid #dfe2e5; padding: 6px 13px; }
              th { background: #f6f8fa; }
            </style>
            <script>
            function htmlToMarkdown(el) {
              function convert(node) {
                if (node.nodeType === 3) return node.textContent;
                if (node.nodeType !== 1) return '';
                var tag = node.tagName.toLowerCase();
                var inner = function() { return Array.from(node.childNodes).map(convert).join(''); };
                switch (tag) {
                  case 'h1': return '# ' + inner().trim() + '\\n\\n';
                  case 'h2': return '## ' + inner().trim() + '\\n\\n';
                  case 'h3': return '### ' + inner().trim() + '\\n\\n';
                  case 'h4': return '#### ' + inner().trim() + '\\n\\n';
                  case 'h5': return '##### ' + inner().trim() + '\\n\\n';
                  case 'h6': return '###### ' + inner().trim() + '\\n\\n';
                  case 'p':  return inner().trim() + '\\n\\n';
                  case 'br': return '\\n';
                  case 'strong': case 'b': return '**' + inner() + '**';
                  case 'em':    case 'i': return '*'  + inner() + '*';
                  case 'del':   case 's': return '~~' + inner() + '~~';
                  case 'code':
                    if (node.parentNode && node.parentNode.tagName === 'PRE') return inner();
                    return '`' + inner() + '`';
                  case 'pre': {
                    var codeEl = node.querySelector('code');
                    var lang = codeEl ? (codeEl.className || '').replace('language-', '') : '';
                    var content = codeEl ? codeEl.textContent : inner();
                    return '```' + lang + '\\n' + content + '\\n```\\n\\n';
                  }
                  case 'blockquote':
                    return inner().trim().split('\\n')
                      .map(function(l) { return '> ' + l; }).join('\\n') + '\\n\\n';
                  case 'ul':
                    return Array.from(node.children)
                      .map(function(li) { return '- ' + convert(li).trim(); })
                      .join('\\n') + '\\n\\n';
                  case 'ol':
                    return Array.from(node.children)
                      .map(function(li, i) { return (i + 1) + '. ' + convert(li).trim(); })
                      .join('\\n') + '\\n\\n';
                  case 'li': return inner();
                  case 'a':   return '[' + inner() + '](' + (node.getAttribute('href') || '') + ')';
                  case 'img': return '![' + (node.getAttribute('alt') || '') + '](' + (node.getAttribute('src') || '') + ')';
                  case 'hr':  return '\\n---\\n\\n';
                  case 'table': return convertTable(node);
                  case 'thead': case 'tbody': case 'tfoot':
                  case 'tr':    case 'th':    case 'td': return inner();
                  default: return inner();
                }
              }
              function convertTable(table) {
                var rows = Array.from(table.querySelectorAll('tr'));
                if (rows.length === 0) return '';
                var headers = Array.from(rows[0].querySelectorAll('th,td'))
                  .map(function(c) { return c.textContent.trim(); });
                var sep = headers.map(function() { return '---'; });
                var body = rows.slice(1).map(function(row) {
                  return '| ' + Array.from(row.querySelectorAll('td'))
                    .map(function(c) { return c.textContent.trim(); }).join(' | ') + ' |';
                });
                return '| ' + headers.join(' | ') + ' |\\n| ' + sep.join(' | ') + ' |\\n'
                  + body.join('\\n') + '\\n\\n';
              }
              return Array.from(el.childNodes).map(convert).join('')
                .replace(/\\n{3,}/g, '\\n\\n').trim();
            }
            </script>
            </head>
            <body contenteditable="true" id="editor">
            %s
            </body>
            </html>
            """;

    @FXML
    public void initialize() {
        sourceEditor.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!updatingFromWysiwyg) {
                currentDocument.setContent(newVal);
                updateTitle();
                if (currentMode == EditMode.PREVIEW) {
                    refreshPreview();
                }
            }
        });

        applyMode(EditMode.SOURCE);
    }

    // ── Mode switching ────────────────────────────────────────────────────────

    @FXML
    private void onSourceMode() {
        if (currentMode == EditMode.WYSIWYG) {
            syncFromWysiwyg();
        }
        applyMode(EditMode.SOURCE);
    }

    @FXML
    private void onPreviewMode() {
        if (currentMode == EditMode.WYSIWYG) {
            syncFromWysiwyg();
        }
        applyMode(EditMode.PREVIEW);
        refreshPreview();
    }

    @FXML
    private void onWysiwygMode() {
        applyMode(EditMode.WYSIWYG);
        refreshWysiwyg();
    }

    private void applyMode(EditMode mode) {
        this.currentMode = mode;
        sourceEditor.setVisible(mode == EditMode.SOURCE);
        sourceEditor.setManaged(mode == EditMode.SOURCE);
        previewPane.setVisible(mode == EditMode.PREVIEW);
        previewPane.setManaged(mode == EditMode.PREVIEW);
        wysiwyPane.setVisible(mode == EditMode.WYSIWYG);
        wysiwyPane.setManaged(mode == EditMode.WYSIWYG);
    }

    // ── File operations ───────────────────────────────────────────────────────

    @FXML
    private void onNew() {
        if (!confirmDiscardChanges()) return;
        currentDocument = new Document();
        sourceEditor.setText("");
        updateTitle();
        setStatus("New document");
    }

    @FXML
    private void onOpen() {
        if (!confirmDiscardChanges()) return;
        FileChooser chooser = buildMarkdownChooser("Open Markdown File");
        File file = chooser.showOpenDialog(getStage());
        if (file == null) return;
        try {
            currentDocument = fileService.open(file.toPath());
            updatingFromWysiwyg = true;
            sourceEditor.setText(currentDocument.getContent());
            updatingFromWysiwyg = false;
            updateTitle();
            if (currentMode == EditMode.PREVIEW) refreshPreview();
            if (currentMode == EditMode.WYSIWYG) refreshWysiwyg();
            setStatus("Opened: " + file.getName());
        } catch (IOException e) {
            showError("Open failed", e.getMessage());
        }
    }

    @FXML
    private void onSave() {
        if (currentMode == EditMode.WYSIWYG) syncFromWysiwyg();
        if (currentDocument.isNew()) {
            onSaveAs();
            return;
        }
        try {
            fileService.save(currentDocument);
            updateTitle();
            setStatus("Saved: " + currentDocument.getPath().getFileName());
        } catch (IOException e) {
            showError("Save failed", e.getMessage());
        }
    }

    @FXML
    private void onSaveAs() {
        if (currentMode == EditMode.WYSIWYG) syncFromWysiwyg();
        FileChooser chooser = buildMarkdownChooser("Save As");
        File file = chooser.showSaveDialog(getStage());
        if (file == null) return;
        Path path = file.toPath();
        if (!path.toString().endsWith(".md")) {
            path = Path.of(path + ".md");
        }
        try {
            fileService.saveAs(currentDocument, path);
            updateTitle();
            setStatus("Saved: " + currentDocument.getPath().getFileName());
        } catch (IOException e) {
            showError("Save failed", e.getMessage());
        }
    }

    @FXML
    private void onQuit() {
        if (!confirmDiscardChanges()) return;
        Platform.exit();
    }

    // ── Rendering helpers ─────────────────────────────────────────────────────

    private void refreshPreview() {
        String html = renderer.renderToHtml(currentDocument.getContent());
        previewPane.getEngine().loadContent(html, "text/html");
    }

    private void refreshWysiwyg() {
        String fragment = renderer.renderToFragment(currentDocument.getContent());
        String html = String.format(WYSIWYG_TEMPLATE, fragment);
        wysiwygEditor.getEngine().loadContent(html, "text/html");
    }

    private void syncFromWysiwyg() {
        WebEngine engine = wysiwygEditor.getEngine();
        Object result = engine.executeScript(
                "htmlToMarkdown(document.getElementById('editor'))");
        if (result instanceof String markdown) {
            updatingFromWysiwyg = true;
            currentDocument.setContent(markdown);
            sourceEditor.setText(markdown);
            updatingFromWysiwyg = false;
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private boolean confirmDiscardChanges() {
        if (!currentDocument.isDirty()) return true;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes.");
        alert.setContentText("Discard changes and continue?");
        return alert.showAndWait()
                .filter(r -> r == ButtonType.OK)
                .isPresent();
    }

    private void updateTitle() {
        Stage stage = getStage();
        if (stage != null) {
            stage.setTitle(TitleFormatter.windowTitle(currentDocument));
        }
        setStatus(TitleFormatter.tabTitle(currentDocument));
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private Stage getStage() {
        if (sourceEditor != null && sourceEditor.getScene() != null) {
            return (Stage) sourceEditor.getScene().getWindow();
        }
        return null;
    }

    private FileChooser buildMarkdownChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Markdown Files", "*.md", "*.markdown"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return chooser;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ── Package-visible for testing ───────────────────────────────────────────

    Document getCurrentDocument() { return currentDocument; }
    EditMode getCurrentMode() { return currentMode; }
    void setCurrentDocument(Document doc) {
        this.currentDocument = doc;
        sourceEditor.setText(doc.getContent());
    }
}
