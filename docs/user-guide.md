---
layout: page
title: User Guide
nav_order: 3
---

# User Guide

## Editing modes

MyDocument has three modes, switchable via the icon buttons in the toolbar at the top of the window.

| Icon | Mode | Description |
|------|------|-------------|
| `</>` | **Source** | Plain-text Markdown editor with a monospaced font |
| Eye | **Preview** | Read-only rendered view of the current document |
| Pencil | **WYSIWYG** | Visual editing — format text without writing Markdown syntax |

Hover over any button to see a tooltip label. Switching from WYSIWYG back to Source converts the browser content back to Markdown syntax, so your `.md` file is always preserved.

---

## WYSIWYG formatting toolbar

When WYSIWYG mode is active a second toolbar appears with formatting controls.

| Button | Action |
|--------|--------|
| **H ▾** | Apply heading level H1–H6, or reset to paragraph |
| **B** | Bold |
| **I** | Italic |
| **~~S~~** | Strikethrough |
| List (bullets) | Bulleted list |
| List (numbers) | Numbered list |
| Quote | Blockquote |
| `</>` | Inline code — wraps the selection in a `<code>` element |
| `{}` | Code block |
| Link | Insert link (prompts for URL) |
| — | Horizontal rule |

Select text before clicking a button to apply the format to the selection; click without a selection to format the current block.

---

## File operations

| Action  | Shortcut       |
|---------|----------------|
| New     | `Ctrl+N`       |
| Open    | `Ctrl+O`       |
| Save    | `Ctrl+S`       |
| Save As | `Ctrl+Shift+S` |
| Quit    | `Ctrl+Q`       |

A `*` in the window title indicates unsaved changes. MyDocument will prompt you before discarding unsaved work when opening a new file or quitting.

**Opening files from the OS:** If `.md` files are associated with MyDocument (set up by the installer), double-clicking a file opens it directly in Preview mode so you can read it immediately. Switch to Source or WYSIWYG mode to edit.

---

## Help

Press **F1** or open **Help → Markdown Reference** to open a rendered quick-reference window covering all supported syntax. **Help → About** shows the application version.

---

## Supported Markdown syntax

MyDocument uses [CommonMark](https://commonmark.org/) with the following extensions enabled:

| Syntax | Extension |
|--------|-----------|
| `| col | col |` tables | GFM Tables |
| `~~strikethrough~~` | GFM Strikethrough |
| Bare URLs become links | Autolinks |
| `# Heading` → `id` attribute | Heading Anchors |

For a full syntax reference, press **F1** inside the application.
