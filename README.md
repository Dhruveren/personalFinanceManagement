# PFM – Personal Finance Manager (CLI)

A fast, cross-platform CLI to track expenses/income, set budgets, and export reports with charts.

## Features
- CSV import, categorization rules, SQLite persistence
- Budgets: set/get/list/delete/report (variance)
- HTML report export with charts

## Install
- macOS: download the DMG from Releases, mount, and drag the app to Applications.
- Cross-platform: download the shaded JAR (pfm-cli.jar) from Releases and run with Java 21+.

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

## Build
- mvn -pl cli -am clean package
- java -jar cli/target/pfm-cli.jar --help


## Contributing
See CONTRIBUTING.md and CODE_OF_CONDUCT.md.

## Security
See SECURITY.md for reporting vulnerabilities.

## License
Apache-2.0 (see LICENSE).

- Link this README from Releases and the docs site so instructions stay consistent. [1]

## Changelog
- Keep a human‑readable CHANGELOG using “Keep a Changelog” format and state adoption of Semantic Versioning. [7]

File: CHANGELOG.md [7]  
