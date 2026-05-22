package net.nikad.mydocument.service;

import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;

public class MarkdownRenderer {

    private static final String HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="UTF-8">
            <style>
              body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                     line-height: 1.6; color: #24292e; max-width: 860px; margin: 0 auto; padding: 24px; }
              pre  { background: #f6f8fa; border-radius: 6px; padding: 16px; overflow: auto; }
              code { font-family: 'SFMono-Regular', Consolas, monospace; font-size: 0.9em; }
              p > code { background: #f6f8fa; border-radius: 3px; padding: 2px 4px; }
              table { border-collapse: collapse; width: 100%%; }
              th, td { border: 1px solid #dfe2e5; padding: 6px 13px; }
              th { background: #f6f8fa; }
              blockquote { border-left: 4px solid #dfe2e5; margin: 0; padding: 0 16px; color: #6a737d; }
              a { color: #0366d6; }
              img { max-width: 100%%; }
            </style>
            </head>
            <body>
            %s
            </body>
            </html>
            """;

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownRenderer() {
        var extensions = List.of(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create(),
                HeadingAnchorExtension.create()
        );
        this.parser = Parser.builder().extensions(extensions).build();
        this.renderer = HtmlRenderer.builder().extensions(extensions).build();
    }

    public String renderToHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return String.format(HTML_TEMPLATE, "");
        }
        var document = parser.parse(markdown);
        String body = renderer.render(document);
        return String.format(HTML_TEMPLATE, body);
    }

    public String renderToFragment(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        return renderer.render(parser.parse(markdown));
    }
}
