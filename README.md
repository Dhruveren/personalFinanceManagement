# PFM – Personal Finance Manager (CLI)

A fast, cross-platform CLI to track expenses/income, set budgets, and export reports with charts.

## Features
- CSV import, categorization rules, SQLite persistence
- Budgets: set/get/list/delete/report (variance)
- HTML report export with charts

## Downloads
- Visit Releases and download a platform installer:
    - macOS: pfm-<version>.dmg
    - Windows: pfm-<version>.msi or .exe
    - Linux (Debian/Ubuntu): pfm-<version>.deb
- Prefer a Java-only option? Download the shaded JAR (pfm-cli.jar) and run with Java 21+.

## Install (macOS DMG)
1) Double-click the .dmg to mount it.
2) Drag the “pfm” app into Applications in the window that opens.
3) Eject the DMG when done.
4) First run: if macOS shows an “unidentified developer” warning, open System Settings → Privacy & Security and click “Open Anyway,” or Control‑click the app in Applications → Open → Open once to allow it.  
   Notes:
- The app bundle includes a Java runtime, so no separate Java install is required.
- Uninstall by deleting “pfm.app” from Applications; optionally remove any symlink you created (see below).

## Run from Terminal (macOS)
- Direct executable inside the app bundle:
- /Applications/pfm.app/Contents/MacOS/pfm --help
- /Applications/pfm.app/Contents/MacOS/pfm import ~/Downloads/transactions.csv
- Launch via Finder’s app mechanism with arguments:
- open /Applications/pfm.app --args --help
- Optional: make a global “pfm” command (may require sudo):
- sudo ln -s /Applications/pfm.app/Contents/MacOS/pfm /usr/local/bin/pfm
- pfm --help

(If not on PATH, check the Start Menu shortcut or the installation folder under Program Files.)

## Install (Linux – DEB)
- Install on Debian/Ubuntu:
- sudo dpkg -i pfm-<version>.deb || sudo apt -f install
- pfm --help

(If the command isn’t on PATH in your distro, check /usr/bin or the install location noted by your package manager.)

## Standalone JAR (all OS)
- Requires Java 21+:
- java -jar pfm-cli.jar --help


## Quickstart
- pfm init-db
- pfm import /path/to/transactions.csv
- pfm list --month 2025-09
- pfm budget-set 2025-09 Food 12000
- pfm budget-report 2025-09 --all
- pfm report-export 2025-09 --out report_sep.html


## Configuration
- Use `--config` to point to a YAML/properties file with DB path and rules file.
- Use `--verbose` to enable debug logs.

## Build from source
- mvn -pl cli -am clean package
- java -jar cli/target/pfm-cli.jar --help


## Troubleshooting (macOS)
- Gatekeeper blocked on first run: System Settings → Privacy & Security → “Open Anyway” for pfm, or Control‑click the app in Applications → Open → Open to save an exception.
- Logs not visible when launching the .app from Finder? Run it from Terminal using the bundle path or the optional symlink shown above.

## Contributing
See CONTRIBUTING.md and CODE_OF_CONDUCT.md.

## Security
See SECURITY.md for reporting vulnerabilities.

## License
Apache-2.0 (see LICENSE).

## Changelog
See CHANGELOG.md (Keep a Changelog style, SemVer).

