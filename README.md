# MyDocument

[![Build & Test](https://github.com/nikadsoft/MyDocument/actions/workflows/build.yml/badge.svg)](https://github.com/nikadsoft/MyDocument/actions/workflows/build.yml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-green.svg)](https://openjfx.io/)

A desktop Markdown editor built with JavaFX 21. Switch between **Source**, **Preview**, and **WYSIWYG** modes with a single click, open and save `.md` files, and get a live rendered preview powered by CommonMark.

## Features

- **Three editing modes** — Source (plain text), Preview (rendered HTML), WYSIWYG (contenteditable browser view)
- **CommonMark rendering** — tables, strikethrough, autolinks, heading anchors (GFM-compatible)
- **Native installers** — `.deb` (Linux), `.msi` (Windows), `.dmg` (macOS) built via jpackage
- **High test coverage** — 91% line coverage on business-logic layers, enforced in CI

## Requirements

| Tool | Version |
|------|---------|
| JDK  | 21+     |
| Gradle | 8.7 (wrapper included) |

## Quick start

```bash
git clone https://github.com/nikadsoft/MyDocument.git
cd MyDocument
./gradlew run
```

## Build

```bash
# Compile only
./gradlew assemble

# Run all tests with coverage report
./gradlew test jacocoTestReport

# Build a native installer for the current platform
./gradlew jpackage
# Output: build/jpackage/
```

## Project structure

```
src/
├── main/java/net/nikad/mydocument/
│   ├── MyDocumentApp.java          # JavaFX entry point
│   ├── controller/MainController   # FXML controller
│   ├── model/Document              # Document model (path, content, dirty flag)
│   ├── service/FileService         # Open / save / save-as
│   ├── service/MarkdownRenderer    # CommonMark → HTML
│   └── view/TitleFormatter         # Window/tab title logic
├── main/resources/                 # FXML layout and app icon
├── main/packaging/                 # Platform-specific jpackage assets
└── test/                           # JUnit 5 tests (46 tests, 91% coverage)
```

## Contributing

1. Fork the repo and create a feature branch from `main`
2. Write tests for any new logic (`./gradlew test` must pass)
3. Open a pull request — CI must be green and coverage must stay ≥ 80%

See the [Wiki](https://github.com/nikadsoft/MyDocument/wiki) for architecture details and the full developer guide.

## License

[GNU General Public License v3.0](LICENSE)
