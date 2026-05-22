---
layout: page
title: User Guide
nav_order: 3
---

# User Guide

## Editing modes

MyDocument has three modes, switchable via the toolbar at the top of the window.

| Mode | Description |
|------|-------------|
| **Source** | Plain-text Markdown editor with a monospaced font |
| **Preview** | Read-only rendered view of the current document |
| **WYSIWYG** | Contenteditable browser view — format text directly without writing Markdown syntax |

Switching from WYSIWYG back to Source or Preview syncs any edits you made in the browser view back to the document model.

## File operations

| Action | Shortcut |
|--------|----------|
| New    | `Ctrl+N` |
| Open   | `Ctrl+O` |
| Save   | `Ctrl+S` |
| Save As | `Ctrl+Shift+S` |
| Quit   | `Ctrl+Q` |

A `*` in the window title indicates unsaved changes. MyDocument will prompt you before discarding unsaved work when opening a new file or quitting.

## Supported Markdown syntax

MyDocument uses [CommonMark](https://commonmark.org/) with the following extensions enabled:

- **Tables** (GFM)
- **Strikethrough** (`~~text~~`)
- **Autolinks** — bare URLs become clickable links automatically
- **Heading anchors** — each heading gets an `id` attribute for deep-linking
