# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Kieker Trace Diagnosis UI is a JavaFX desktop application for visualizing and analyzing Kieker monitoring logs (traces, methods, statistics). It is a Maven multi-module project targeting Java 21.

## Build Commands

```bash
# Full build
mvn clean package

# Skip tests
mvn clean package -DskipTests

# Using Maven wrapper
./mvnw clean install

# Build a specific module
mvn -pl ktd-frontend/ktd-frontend-application clean package
```

## Running Tests

```bash
# All tests
mvn test

# Tests for a specific module
mvn -pl ktd-backend/ktd-backend-data test

# Single test class
mvn test -Dtest=ClassName

# Single test method
mvn test -Dtest=ClassName#methodName

# UI tests (disabled by default, require headless JavaFX via Monocle)
mvn test -Denable.ui.tests=true
```

Test locale/timezone is locked to German (DE_de, Berlin) for consistency. JaCoCo coverage reports land in `target/site/jacoco/index.html`.

## Running the Application

Build the release artifact and run via the generated scripts:

```bash
mvn clean package
# Linux:
tar -xzf ktd-release-engineering/target/Kieker-Trace-Diagnosis-*-linux.tar.gz
cd Kieker-Trace-Diagnosis-*/
./start-unix
```

From an IDE (Eclipse), add these VM args to the run configuration:
```
--module-path <PATH_TO_OPENJFX_SDK> --add-modules=javafx.controls
```

Main class: `kieker.diagnosis.frontend.application.KiekerTraceDiagnosis`

## Architecture

### Module Structure

```
kieker-trace-diagnosis-ui/
├── ktd-dependencies/       # Central dependency/version management (BOM)
├── ktd-parent/             # Build plugin configuration (Surefire, JaCoCo, Lombok)
├── ktd-backend/            # Business logic, data processing
│   ├── ktd-backend-base/   # Service marker interface, ClassUtil (Guice proxy helper)
│   ├── ktd-backend-data/   # Domain models, Kieker record parsing
│   ├── ktd-backend-cache/  # Query result caching
│   ├── ktd-backend-filter/ # Filter/sort logic
│   ├── ktd-backend-search/ # Search/query engine
│   ├── ktd-backend-pattern/# Pattern matching (regex)
│   ├── ktd-backend-export/ # CSV/report export
│   ├── ktd-backend-settings/    # User preferences
│   ├── ktd-backend-monitoring/  # Kieker integration
│   └── ktd-backend-properties/  # App-wide property definitions
├── ktd-frontend/           # JavaFX UI components
│   ├── ktd-frontend-application/ # Entry point (KiekerTraceDiagnosis), Guice wiring, ArchUnit tests
│   ├── ktd-frontend-main/       # MainPane, MainMenuBar, MainTabPane
│   ├── ktd-frontend-base/       # Shared mixins and JavaFX utilities
│   ├── ktd-frontend-dialog/     # Subdirectory with per-dialog modules (settings, about, progress, etc.)
│   ├── ktd-frontend-tab/        # Subdirectory with per-tab modules (traces, methods, statistics, aggregated-methods)
│   └── ktd-frontend-test/       # Shared test fixtures for UI tests
└── ktd-release-engineering/ # AppAssembler + Assembly packaging into tar.gz/zip distributions
```

### Key Frameworks

- **JavaFX 21** — UI framework (platform classifiers for Linux/Windows natives)
- **Google Guice 5.1.0** — Dependency injection (not Spring); modules wired in `ktd-frontend-application`
- **Kieker 1.15.2** — Monitoring log parsing
- **HPPC 0.10.0** — High-performance primitive collections (used where memory efficiency matters for large logs)
- **Lombok 1.18.44** — Annotation-based code generation (`@Getter`, `@Data`, etc.)
- **Log4j 2.x** — Logging
- **TestFX + Monocle** — Headless JavaFX UI testing
- **ArchUnit** — Architecture constraint tests (in `ktd-frontend-application`)

### Layering Conventions

- Package pattern: `kieker.diagnosis.[backend|frontend].<module>.<layer>`
- Common layer names: `service`, `common`, `composite`, `complex`, `dialog`, `mixin`
- All backend services implement the `Service` marker interface (`ktd-backend-base`) and are Guice singletons
- `ClassUtil` (in `ktd-backend-base`) handles Guice CGLIB proxy unwrapping — use it when reflecting on service classes
- UI tests are named `*TestUI.java`; unit tests are named `*Test.java`
