---
layout: page
title: Developer Guide
nav_order: 4
---

# Developer Guide

## Prerequisites

| Tool | Version |
|------|---------|
| JDK | 21 (LTS) |
| Git | any recent |
| Gradle | wrapper included — no install needed |

## Running locally

```bash
./gradlew run
```

## Running tests

```bash
# Run all tests
./gradlew test

# Run tests + generate HTML coverage report
./gradlew test jacocoTestReport
# Report: build/reports/jacoco/test/html/index.html

# Verify coverage gate (≥ 80% line coverage on business-logic layers)
./gradlew jacocoTestCoverageVerification
```

## Architecture

```
net.nikad.mydocument
├── MyDocumentApp          JavaFX Application entry point; reads argv[0]
│                          and opens the file in Preview mode if present
├── model/
│   └── Document           Holds path, content string, and dirty flag
├── service/
│   ├── FileService        open() / save() / saveAs() — thin I/O wrapper
│   └── MarkdownRenderer   Wraps CommonMark parser+renderer; produces full HTML pages
├── view/
│   ├── EditMode           SOURCE | PREVIEW | WYSIWYG enum
│   └── TitleFormatter     Derives window title and tab label from Document state
└── controller/
    └── MainController     FXML wiring — connects UI events to model/service calls
```

**Coverage policy:** `MyDocumentApp` and `controller.*` are excluded from the JaCoCo coverage threshold (pure JavaFX wiring). All business logic lives in `model`, `service`, and `view` and must remain ≥ 80% covered.

### Version number

The application version is defined once in `build.gradle` (`version = '…'`). The `processResources` task expands `src/main/resources/net/nikad/mydocument/version.properties` at build time, replacing `${projectVersion}` with the real value. `MainController` reads this file at startup and displays it in the About dialog.

### WYSIWYG ↔ Source round-trip

The WYSIWYG editor is a `contenteditable` WebView that displays rendered HTML. When the user switches away from WYSIWYG mode, `syncFromWysiwyg()` calls an `htmlToMarkdown()` JavaScript function embedded in the page. This function converts the DOM back to Markdown syntax (headings, emphasis, lists, code, tables, links, images, blockquotes) and writes the result to the document model, so the `.md` source is always preserved.

## Building native installers

```bash
# On Linux   → produces build/jpackage/mydocument_1.0.2_amd64.deb
# On Windows → produces build/jpackage/MyDocument-1.0.2.msi
# On macOS   → produces build/jpackage/MyDocument-1.0.2.dmg
./gradlew jpackage
```

macOS requires `sips` and `iconutil` (included with Xcode command-line tools) to convert the PNG icon to ICNS.

The installers include a `.md` file association (`src/main/packaging/file-associations.properties`) so the OS registers MyDocument as the default handler for Markdown files.

## Releasing

Push an annotated version tag to trigger the full release pipeline:

```bash
git tag -a v1.0.2 -m "Release 1.0.2"
git push origin v1.0.2
```

GitHub Actions builds installers on all three platforms and attaches them to a new GitHub Release automatically.

## Contributing

1. Create a feature branch from `main`
2. Write tests for any new logic — coverage must stay ≥ 80%
3. Open a pull request — `Build and Test (Java 21)` must be green before merge
