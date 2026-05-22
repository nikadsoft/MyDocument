# Security Policy

## Supported Versions

| Version | Supported |
|---------|-----------|
| 0.1.x   | Yes       |

## Reporting a Vulnerability

Please **do not** open a public GitHub issue for security vulnerabilities.

Instead, report them privately via [GitHub's private vulnerability reporting](https://github.com/nikadsoft/MyDocument/security/advisories/new).

Include as much of the following as you can:

- A description of the vulnerability and its potential impact
- Steps to reproduce or a proof-of-concept
- Affected versions
- Any suggested mitigations

You can expect an acknowledgement within **48 hours** and a status update within **7 days**. If a fix is warranted, a patched release will be issued and you will be credited in the release notes unless you prefer to remain anonymous.

## Scope

This project is a local desktop application that reads and writes files on the user's machine. The primary attack surfaces are:

- **Markdown rendering** — rendered HTML is displayed in a JavaFX WebView. Malicious Markdown files could attempt XSS via injected HTML/JavaScript.
- **File handling** — the app reads arbitrary `.md` files from disk; path traversal is not applicable (user selects files via a file chooser), but very large files could cause resource exhaustion.
- **Dependencies** — vulnerabilities in CommonMark, JavaFX, or the JDK itself.

## Security Measures

- The JavaFX WebView used for Preview and WYSIWYG mode does not have access to the local filesystem via JavaScript (the default WebView security policy applies).
- CommonMark's HTML renderer escapes raw HTML by default; the app does not enable unsafe HTML passthrough.
- Dependencies are declared with pinned versions and should be reviewed regularly for CVEs.
