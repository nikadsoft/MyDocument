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
sudo dpkg -i MyDocument_1.0.2_amd64.deb
```

### Windows (.msi)

Run `MyDocument-1.0.2.msi` and follow the installer wizard. A Start Menu shortcut and desktop icon are created automatically.

### macOS (.dmg)

Open `MyDocument-1.0.2.dmg`, drag **MyDocument** to your Applications folder, and launch it from Launchpad.

---

## File associations

The installer registers MyDocument as the default handler for `.md` files on all platforms. After installation, double-clicking any `.md` file opens it directly in Preview mode. You can change the default application at any time through your operating system's file association settings.

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
