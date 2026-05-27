# Contributing to declarative-vertx

Thank you for your interest in contributing to **declarative-vertx**! We welcome contributions from the community and appreciate your time and effort. This guide will help you get started.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Project Overview](#project-overview)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Enhancements](#suggesting-enhancements)
  - [Submitting Pull Requests](#submitting-pull-requests)
- [Coding Standards](#coding-standards)
- [Testing](#testing)
- [Documentation](#documentation)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Review Process](#review-process)

---

## Code of Conduct

This project adheres to the [Open Source Code of Conduct](https://docs.github.com/en/site-policy/github-terms/github-community-code-of-conduct). By participating, you are expected to uphold this code. Please report unacceptable behavior to the repository maintainers.

---

## Project Overview

**declarative-vertx** is a declarative approach in creating a REST API using Vert.x with minimal coding effort.

Key capabilities:
- Primarily define the data structures (resources) that your API exposes, outlining their attributes and relationships, rather than intricate logic for manipulating them.
- Utilize standard HTTP methods like GET, POST, PUT, DELETE to clearly indicate the intended actions on those resources, allowing the client to understand the operations without needing additional instructions.
- Each request should contain all the necessary information to be processed independently, without relying on previous interactions with the server.
- Use declarative frameworks or tools to specify the desired API behavior through configuration files, allowing for easier management and updates.

---

## Getting Started

1. **Fork** the repository on GitHub Enterprise.
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/<your-username>/declarative-vertx.git
   cd declarative-vertx
   ```
3. **Add the upstream remote**:
   ```bash
   git remote add upstream https://github.com/javalabs-eng/declarative-vertx.git
   ```

---

## Development Setup

This project uses **Maven** as its build tool. Ensure you have the following installed:

- Java 11 or higher
- Maven 3.8+

Build the project:

```bash
./mvnw clean install
```

To skip tests during the build:

```bash
./mvnw clean install -DskipTests
```

To run tests only:

```bash
./mvnw test
```

> **Note:** Ensure the `javax.persistence-api` and `slf4j-api` JARs are available in your local Maven repository or via the configured `settings.xml`.

---

## How to Contribute

### Reporting Bugs

If you find a bug, please [open an issue](https://github.com/javalabs-eng/declarative-vertx/issues) and include:

- A clear, descriptive title.
- Steps to reproduce the problem.
- Expected behavior vs. actual behavior.
- Java version and OS details.
- Any relevant logs or stack traces.

### Suggesting Enhancements

We welcome ideas for new features or improvements. Please [open an issue](https://github.com/javalabs-eng/declarative-vertx/issues) with:

- A clear description of the enhancement.
- The motivation and use case behind the request.
- Any alternative solutions you have considered.

### Submitting Pull Requests

1. **Sync** your fork with the latest upstream changes:
   ```bash
   git fetch upstream
   git checkout main
   git merge upstream/main
   ```
2. **Create a feature branch** off `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes**, following the [coding standards](#coding-standards).
4. **Write or update tests** to cover your changes.
5. **Run the full test suite** to ensure nothing is broken (see [Testing](#testing)).
6. **Commit** your changes with a clear message (see [Commit Message Guidelines](#commit-message-guidelines)).
7. **Push** your branch to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```
8. **Open a Pull Request** against the `main` branch of the upstream repository.

---

## Coding Standards

- Follow standard **Java coding conventions** (Oracle Java Code Conventions).
- Use meaningful class, method, and variable names.
- Keep methods focused and concise — prefer single-responsibility design.
- Add Javadoc comments to all public classes and methods.
- Avoid introducing unnecessary external dependencies without prior discussion.

---

## Testing

All contributions must include appropriate tests. The project uses **JUnit 5 (Jupiter)**.

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=YourTestClass
```

- Aim for high test coverage on new code.
- Ensure all existing tests continue to pass.
- Tests are located under `src/test/java/`.
- Performance-sensitive code paths should include benchmarking notes in the PR description.

---

## Documentation

- Update `README.md` if your change affects public-facing behavior, configuration, or API usage.
- Add or update Javadoc for any modified or new public APIs.

---

## Commit Message Guidelines

Use clear and descriptive commit messages. We follow the [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>(<scope>): <short summary>

[optional body]

[optional footer]
```

**Types:**
- `feat` – A new feature
- `fix` – A bug fix
- `docs` – Documentation changes only
- `test` – Adding or updating tests
- `refactor` – Code change that neither fixes a bug nor adds a feature
- `chore` – Build process or tooling changes
- `ci` – CI/CD pipeline changes
- `perf` – A code change that improves performance

**Example:**
```
feat(criteria): add support for IS NULL predicate in Criteria query

Adds `.isNull()` predicate to the Criteria fluent API, enabling queries
like `.where("column").isNull()` to be expressed without raw SQL.

Closes #42
```

---

## Review Process

- All pull requests require at least **one approval** from a [code owner](CODEOWNERS) before merging.
- CI checks (build, tests, Jacoco coverage) must pass before a PR can be merged.
- Feedback will be provided within a reasonable timeframe.
- Please be responsive to review comments and update your PR accordingly.

---

## Questions?

If you have any questions, feel free to open a discussion or reach out to the maintainers listed in [CODEOWNERS](CODEOWNERS).

Thank you for contributing! 🎉

