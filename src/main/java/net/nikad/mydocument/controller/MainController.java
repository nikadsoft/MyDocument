package net.nikad.mydocument.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    @FXML private VBox wysiwyPane;
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

    private static final String HELP_MARKDOWN = """
            # Markdown Quick Reference

            MyDocument supports **CommonMark** with GitHub-Flavoured Markdown (GFM) extensions.

            ---

            ## Text Formatting

            | Syntax | Result |
            |---|---|
            | `**bold**` or `__bold__` | **bold** |
            | `*italic*` or `_italic_` | *italic* |
            | `~~strikethrough~~` | ~~strikethrough~~ |
            | `` `inline code` `` | `inline code` |

            ---

            ## Headings

            ```
            # Heading 1
            ## Heading 2
            ### Heading 3
            #### Heading 4
            ##### Heading 5
            ###### Heading 6
            ```

            ---

            ## Lists

            **Unordered** — use `-`, `*`, or `+`:

            ```
            - Item 1
            - Item 2
              - Nested item
            - Item 3
            ```

            **Ordered:**

            ```
            1. First item
            2. Second item
            3. Third item
            ```

            ---

            ## Links and Images

            ```
            [Link text](https://example.com)
            [Link with title](https://example.com "Title")

            ![Alt text](image.png)
            ```

            Bare URLs are auto-linked: https://example.com

            ---

            ## Code

            **Inline** — wrap with backticks:

            ```
            Use `System.out.println()` here.
            ```

            **Block** — wrap with triple backticks (optionally add a language):

            ````
            ```java
            public class Hello {
                public static void main(String[] args) {
                    System.out.println("Hello, world!");
                }
            }
            ```
            ````

            ---

            ## Blockquotes

            ```
            > This is a blockquote.
            > It can span multiple lines.
            >
            > Even multiple paragraphs.
            ```

            ---

            ## Tables (GFM)

            ```
            | Header 1 | Header 2 | Header 3 |
            |----------|:--------:|---------:|
            | Left     | Center   | Right    |
            | Cell     | Cell     | Cell     |
            ```

            Column alignment: `:---` left · `:---:` centre · `---:` right.

            ---

            ## Horizontal Rule

            Three or more hyphens, asterisks, or underscores on their own line:

            ```
            ---
            ```

            ---

            ## Keyboard Shortcuts

            | Action   | Shortcut        |
            |----------|-----------------|
            | New      | Ctrl+N          |
            | Open     | Ctrl+O          |
            | Save     | Ctrl+S          |
            | Save As  | Ctrl+Shift+S    |
            | Quit     | Ctrl+Q          |
            | Help     | F1              |
            """;

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
            function insertCode() {
              var sel = window.getSelection();
              if (!sel || sel.rangeCount === 0) return;
              var range = sel.getRangeAt(0);
              var selected = range.toString();
              range.deleteContents();
              var el = document.createElement('code');
              el.textContent = selected.length > 0 ? selected : 'code';
              range.insertNode(el);
              var r = document.createRange();
              r.setStartAfter(el); r.collapse(true);
              sel.removeAllRanges(); sel.addRange(r);
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

    // ── WYSIWYG formatting toolbar ────────────────────────────────────────────

    @FXML void onWysiwygH1()        { execWysiwyg("document.execCommand('formatBlock',false,'h1')"); }
    @FXML void onWysiwygH2()        { execWysiwyg("document.execCommand('formatBlock',false,'h2')"); }
    @FXML void onWysiwygH3()        { execWysiwyg("document.execCommand('formatBlock',false,'h3')"); }
    @FXML void onWysiwygH4()        { execWysiwyg("document.execCommand('formatBlock',false,'h4')"); }
    @FXML void onWysiwygH5()        { execWysiwyg("document.execCommand('formatBlock',false,'h5')"); }
    @FXML void onWysiwygH6()        { execWysiwyg("document.execCommand('formatBlock',false,'h6')"); }
    @FXML void onWysiwygParagraph() { execWysiwyg("document.execCommand('formatBlock',false,'p')"); }
    @FXML void onWysiwygBold()          { execWysiwyg("document.execCommand('bold')"); }
    @FXML void onWysiwygItalic()        { execWysiwyg("document.execCommand('italic')"); }
    @FXML void onWysiwygStrikethrough() { execWysiwyg("document.execCommand('strikeThrough')"); }
    @FXML void onWysiwygBulletList()    { execWysiwyg("document.execCommand('insertUnorderedList')"); }
    @FXML void onWysiwygOrderedList()   { execWysiwyg("document.execCommand('insertOrderedList')"); }
    @FXML void onWysiwygBlockquote()    { execWysiwyg("document.execCommand('formatBlock',false,'blockquote')"); }
    @FXML void onWysiwygInlineCode()    { execWysiwyg("insertCode()"); }
    @FXML void onWysiwygCodeBlock()     { execWysiwyg("document.execCommand('formatBlock',false,'pre')"); }
    @FXML void onWysiwygHr()            { execWysiwyg("document.execCommand('insertHorizontalRule')"); }

    @FXML
    private void onWysiwygLink() {
        TextInputDialog dialog = new TextInputDialog("https://");
        dialog.initOwner(getStage());
        dialog.setTitle("Insert Link");
        dialog.setHeaderText(null);
        dialog.setContentText("URL:");
        dialog.showAndWait().ifPresent(url -> {
            String safe = url.replace("\\", "\\\\").replace("'", "\\'");
            execWysiwyg("document.execCommand('createLink',false,'" + safe + "')");
        });
    }

    private void execWysiwyg(String js) {
        wysiwygEditor.getEngine().executeScript(js);
    }

    // ── Help ──────────────────────────────────────────────────────────────────

    @FXML
    private void onHelp() {
        Stage helpStage = new Stage();
        helpStage.initOwner(getStage());
        helpStage.setTitle("Markdown Reference — MyDocument");
        WebView webView = new WebView();
        webView.getEngine().loadContent(renderer.renderToHtml(HELP_MARKDOWN), "text/html");
        helpStage.setScene(new Scene(webView, 720, 640));
        helpStage.show();
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(getStage());
        alert.setTitle("About MyDocument");
        alert.setHeaderText("MyDocument 1.0.2");
        alert.setContentText("""
                A desktop Markdown editor built with JavaFX 21.

                CommonMark with GFM extensions:
                tables · strikethrough · autolinks · heading anchors

                Open-source under the GNU GPL v3.
                https://github.com/nikadsoft/MyDocument""");
        alert.showAndWait();
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
