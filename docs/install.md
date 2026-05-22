---
layout: page
title: Installation
nav_order: 2
---

# Installation

## Pre-built installers

Download the latest installer for your platform from [GitHub Releases](https://github.com/nikadsoft/MyDocument/releases).

### Linux (.deb)

```bash
sudo dpkg -i MyDocument_0.1.0_amd64.deb
```

### Windows (.msi)

Run `MyDocument-0.1.0.msi` and follow the installer wizard. A Start Menu shortcut and desktop icon are created automatically.

### macOS (.dmg)

Open `MyDocument-0.1.0.dmg`, drag **MyDocument** to your Applications folder, and launch it from Launchpad.

---

## Build from source

**Requirements:** JDK 21+, Git

```bash
git clone https://github.com/nikadsoft/MyDocument.git
cd MyDocument

# Run directly (no install needed)
./gradlew run

# Build a native installer for your current platform
./gradlew jpackage
# Installer written to:  build/jpackage/
```
