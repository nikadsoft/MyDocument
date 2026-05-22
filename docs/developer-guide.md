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
| Gradle | 8.7 (wrapper included — no install needed) |

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
├── MyDocumentApp          JavaFX Application entry point
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

## Building native installers

```bash
# On Linux  → produces build/jpackage/mydocument_0.1.0_amd64.deb
# On Windows → produces build/jpackage/MyDocument-0.1.0.msi
# On macOS  → produces build/jpackage/MyDocument-0.1.0.dmg
./gradlew jpackage
```

macOS requires `sips` and `iconutil` (included with Xcode command-line tools) to convert the PNG icon to ICNS.

## Releasing

Push a version tag to trigger the full release pipeline:

```bash
git tag v0.1.0
git push origin v0.1.0
```

GitHub Actions builds installers on all three platforms and attaches them to a new GitHub Release automatically.

## Contributing

1. Create a feature branch from `main`
2. Write tests for any new logic — coverage must stay ≥ 80%
3. Open a pull request — `Build and Test (Java 21)` must be green before merge
