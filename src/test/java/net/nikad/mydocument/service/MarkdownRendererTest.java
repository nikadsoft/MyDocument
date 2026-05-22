package net.nikad.mydocument.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownRendererTest {

    private MarkdownRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new MarkdownRenderer();
    }

    @Test
    void renderToHtmlProducesFullDocument() {
        String html = renderer.renderToHtml("# Hello");
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("<h1"));
        assertTrue(html.contains("Hello"));
    }

    @Test
    void renderToHtmlHandlesNull() {
        String html = renderer.renderToHtml(null);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertFalse(html.contains("<h1"));
    }

    @Test
    void renderToHtmlHandlesBlank() {
        String html = renderer.renderToHtml("   ");
        assertTrue(html.contains("<!DOCTYPE html>"));
    }

    @Test
    void renderToHtmlRendersBold() {
        String html = renderer.renderToHtml("**bold**");
        assertTrue(html.contains("<strong>bold</strong>"));
    }

    @Test
    void renderToHtmlRendersItalic() {
        String html = renderer.renderToHtml("*italic*");
        assertTrue(html.contains("<em>italic</em>"));
    }

    @Test
    void renderToHtmlRendersInlineCode() {
        String html = renderer.renderToHtml("`code`");
        assertTrue(html.contains("<code>code</code>"));
    }

    @Test
    void renderToHtmlRendersFencedCodeBlock() {
        String html = renderer.renderToHtml("```\nSystem.out.println();\n```");
        assertTrue(html.contains("<pre>"));
        assertTrue(html.contains("System.out.println"));
    }

    @Test
    void renderToHtmlRendersTable() {
        String md = "| A | B |\n|---|---|\n| 1 | 2 |";
        String html = renderer.renderToHtml(md);
        assertTrue(html.contains("<table>"));
        assertTrue(html.contains("<td>1</td>"));
    }

    @Test
    void renderToHtmlRendersStrikethrough() {
        String html = renderer.renderToHtml("~~strike~~");
        assertTrue(html.contains("<del>strike</del>"));
    }

    @Test
    void renderToHtmlRendersBlockquote() {
        String html = renderer.renderToHtml("> quote");
        assertTrue(html.contains("<blockquote>"));
    }

    @Test
    void renderToHtmlRendersLink() {
        String html = renderer.renderToHtml("[text](https://example.com)");
        assertTrue(html.contains("href=\"https://example.com\""));
        assertTrue(html.contains(">text<"));
    }

    @Test
    void renderToHtmlRendersUnorderedList() {
        String html = renderer.renderToHtml("- item one\n- item two");
        assertTrue(html.contains("<ul>"));
        assertTrue(html.contains("<li>"));
    }

    @Test
    void renderToHtmlRendersOrderedList() {
        String html = renderer.renderToHtml("1. first\n2. second");
        assertTrue(html.contains("<ol>"));
    }

    @Test
    void renderToHtmlRendersHeadingAnchor() {
        String html = renderer.renderToHtml("## Section");
        assertTrue(html.contains("id="));
    }

    @Test
    void renderToFragmentReturnsBodyOnly() {
        String fragment = renderer.renderToFragment("# Title");
        assertTrue(fragment.contains("<h1"));
        assertFalse(fragment.contains("<!DOCTYPE"));
    }

    @Test
    void renderToFragmentHandlesNull() {
        assertEquals("", renderer.renderToFragment(null));
    }

    @Test
    void renderToFragmentHandlesBlank() {
        assertEquals("", renderer.renderToFragment("  "));
    }

    @Test
    void renderToHtmlRendersAutolink() {
        String html = renderer.renderToHtml("Visit https://example.com today");
        assertTrue(html.contains("<a href=\"https://example.com\""));
    }

    @Test
    void renderToHtmlEscapesHtmlEntities() {
        String html = renderer.renderToHtml("1 < 2 & 3 > 2");
        assertTrue(html.contains("&lt;"));
    }
}
