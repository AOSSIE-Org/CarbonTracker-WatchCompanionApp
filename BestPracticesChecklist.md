# AOSSIE Best Practices Checklist

> Criteria adapted from
> the [OpenSSF Best Practices Badge](https://github.com/coreinfrastructure/best-practices-badge)
> (MIT / CC BY 3.0) by OpenSSF contributors. Modified for AOSSIE multi-repo template use.

> **Purpose:** Covers OpenSSF Best Practices criteria that are NOT auto-detected by OpenSSF
> Scorecard.
> Scorecard already handles: License, SAST tools, CI tests, Security Policy file, Branch Protection,
> Pinned Dependencies, Signed Releases, Maintained status, and Known Vulnerabilities.
>
> **How to use:**
> 1. Fill in checkboxes below — tick `[x]` for Met, leave `[ ]` for Unmet, use `[~]` for N/A
> 2. Add a brief note or URL after each item as evidence
> 3. Run the checklist-score workflow to update the badge automatically
>
> **Legend:**
> - 🔴 MUST — Required for passing
> - 🟡 SHOULD — Required unless documented rationale given
> - 🔵 SUGGESTED — Optional but recommended
> - ⚪ N/A — Mark `[~]` if not applicable, add justification

---

## Score Summary

<!-- Auto-updated by checklist-score.yml workflow — do not edit manually -->

| Category       | Met   | Total  | Status |
|----------------|-------|--------|--------|
| Basics         | 0     | 8      | 🔴     |
| Change Control | 0     | 6      | 🔴     |
| Reporting      | 0     | 8      | 🔴     |
| Quality        | 0     | 11     | 🔴     |
| Security       | 0     | 9      | 🔴     |
| Analysis       | 0     | 7      | 🔴     |
| **Total**      | **0** | **49** | **0%** |

---

## 🏗️ Basics

### Project Website & Documentation

- [x] 🔴 **description_good** — The project README/website clearly describes what the software does
  and what problem it solves.
    - *Evidence
      URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/blob/main/README.md

- [x] 🔴 **interact** — The project provides information on how to obtain the software, submit bug
  reports, and contribute.
    - *Evidence
      URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/blob/main/CONTRIBUTING.md

- [x] 🔴 **contribution** — `CONTRIBUTING.md` explains the contribution process (e.g., PRs are used,
  how to open one).
    - *Evidence
      URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/blob/main/CONTRIBUTING.md

- [x] 🟡 **contribution_requirements** — `CONTRIBUTING.md` references acceptable contribution
  standards (coding style, tests required, etc.).
    - *Evidence
      URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/blob/main/CONTRIBUTING.md

- [x] 🔴 **documentation_basics** — Basic documentation exists for the software (README, Wiki, or
  docs folder).
    - *Evidence
      URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/blob/main/README.md

- [~] 🔴 **documentation_interface** — Reference documentation describes the external interface (API
  inputs/outputs, CLI flags, config schema, etc.).
    - *Evidence URL:* `[ ]` N/A — *Justification:* CarbonTracker Wear OS is an end-user watch
      application and does not expose a public API, CLI, or external configuration interface
      requiring reference documentation. Its only inter-device communication is the internal Wear OS
      Data Layer sync with the CarbonTracker mobile app, which is not a public interface.

### Other Basics

- [x] 🔴 **discussion** — Project has a searchable, URL-addressable discussion mechanism (GitHub
  Issues, Discord with archive, mailing list, etc.) that doesn't require proprietary client
  software.
    - *Evidence URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/issues

- [x] 🟡 **english** — Documentation is provided in English and English bug reports/comments are
  accepted.
    - *Note:*

---

## 🔄 Change Control

### Version Control

- [ ] 🔵 **repo_distributed** — Project uses a distributed VCS (e.g., git). *(SUGGESTED)*
    - *Evidence URL:*

### Version Numbering

- [x] 🔴 **version_unique** — Each release has a unique version identifier (e.g., v1.0.0).
    - *Evidence URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/releases

- [ ] 🔵 **version_semver** — Project uses [SemVer](https://semver.org)
  or [CalVer](https://calver.org/) format. *(SUGGESTED)*
    - *Note:*

- [ ] 🔵 **version_tags** — Releases are tagged in the VCS (e.g., `git tag v1.0.0`). *(SUGGESTED)*
    - *Evidence URL:*

### Release Notes

- [x] 🔴 **release_notes** — Each release includes human-readable release notes summarizing major
  changes. Raw `git log` output is NOT acceptable.
    - *Evidence
      URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/releases/tag/v1.0.1

- [~] 🔴 **release_notes_vulns** — Release notes identify every publicly known vulnerability (with
  CVE) fixed in that release.
    - *Evidence URL:* `[ ]` N/A — *Justification (no publicly known vulns / users can't
      self-update):*

---

## 🐛 Reporting

### Bug Reporting

- [x] 🔴 **report_process** — A bug-reporting process exists (e.g., GitHub Issues link in README).
    - *Evidence
      URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/blob/main/README.md

- [x] 🟡 **report_tracker** — An issue tracker (e.g., GitHub Issues) is used to track individual
  bugs.
    - *Evidence URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/issues

- [~] 🔴 **report_responses** — A majority of bug reports submitted in the last 2–12 months have been
  acknowledged (response ≠ fix).
    - *Self-certification note:* N/A — CarbonTracker Wear OS is a newly established project and does
      not yet
      have 2–12 months of bug-report history. Therefore, there are currently insufficient historical
      bug reports to evaluate the acknowledgment rate.

- [~] 🟡 **enhancement_responses** — More than 50% of enhancement requests in the last 2–12 months
  have received a response.
    - *Self-certification note:* N/A — CarbonTracker Wear OS is a newly established project and has
      not yet received any enhancement requests, so there is no request history to evaluate. Any
      enhancement requests received going forward will be reviewed and responded to.

- [x] 🔴 **report_archive** — Reports and responses are publicly archived and searchable (GitHub
  Issues satisfies this).
    - *Evidence URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/issues

### Vulnerability Reporting

- [x] 🔴 **vulnerability_report_process** — A vulnerability reporting process is documented (e.g.,
  `SECURITY.md`).
    - *Evidence URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/security/policy

- [x] 🟡 **vulnerability_report_private** — If private vulnerability reporting is supported, the
  method for private submission is documented.
    - *Evidence URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/security/policy

- [~] 🔴 **vulnerability_report_response** — Initial response to any vulnerability report received in
  the last 6 months was within 14 days.
    - *Self-certification note:* N/A — *Justification (no reports received):*

---

## ✅ Quality

### Build System

- [x] 🔴 **build** — If the project requires building, a working build system exists that can
  auto-rebuild from source.
    - *Evidence
      URL:* https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/blob/main/README.md https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/blob/main/.github/workflows/version-release.yml

- [ ] 🔵 **build_common_tools** — Common build tools are used (npm, pip, cargo, make, gradle, etc.).
  *(SUGGESTED)*
    - *Evidence URL:* `[ ]` N/A

- [x] 🟡 **build_floss_tools** — The project can be built using only FLOSS tools.
    - *Note:* The project can be built using only FLOSS tools.
    - *Note:* CarbonTracker Wear OS is written in Kotlin with Jetpack Compose and built
      with Gradle, the Android Gradle Plugin, and KSP, all of which are FLOSS. It can be built
      from the command line with `./gradlew assembleDebug` and does not require any proprietary
      IDE or build service. Release signing is optional and only used when a local
      `key.properties` file is present.

### Automated Testing

- [ ] 🔵 **test_invocation** — The test suite can be invoked in a standard way for the language (
  e.g., `npm test`, `pytest`, `cargo test`). *(SUGGESTED)*
    - *Evidence URL:*

- [ ] 🔵 **test_most** — The test suite covers most code branches, input fields, and functionality.
  *(SUGGESTED)*
    - *Estimated coverage %:*

### New Functionality Testing Policy

- [ ] 🔴 **test_policy** — The project has a general policy that new functionality must include tests
  in the automated test suite.
    - *Evidence (CONTRIBUTING reference or informal policy):* Unmet. Automated tests for the Wear OS
      app were not part of the original project proposal, so no test suite exists yet. The app's
      core
      functionality (activity sync, Data Layer communication) only works when paired with the
      CarbonTracker mobile app, so it is currently verified through manual end-to-end testing
      (documented in CONTRIBUTING.md, "Test Your Changes"). CONTRIBUTING.md encourages unit tests
      for isolated logic but does not require them.

- [ ] 🔴 **tests_are_added** — Evidence exists that the test policy has been followed in recent major
  changes (e.g., PRs include tests).
    - *Evidence URL (recent PR with tests):* Unmet. No automated test suite exists yet for the Wear
      OS
      app, so there are no PRs with tests to link. Changes are verified manually on an emulator or
      device paired with the mobile app, as described in CONTRIBUTING.md.

- [ ] 🔵 **tests_documented_added** — The test policy is documented in contribution instructions. *(
  SUGGESTED)*
    - *Evidence URL:*

### Linting / Warning Flags

- [x] 🔴 **warnings** — At least one linter or compiler warning flag is enabled (ESLint, Pylint,
  clippy, golangci-lint, Slither for Solidity, etc.).
    - *Tool used:* Android Lint (`./gradlew lint`)

- [x] 🔴 **warnings_fixed** — Warnings from the linter are addressed (not suppressed without reason).
    - *Note:* Warnings from the linter are addressed (not suppressed without reason).
    - *Note:* Lint reports 0 errors. Code-level warnings were fixed. The remaining 14 are dependency
      "newer version available" notices and one intentional launcher-icon notice (adaptive XML plus
      bitmaps, both in use). The only suppression is a documented entry in `app/lint.xml` for the
      Tile preview images.

- [ ] 🔵 **warnings_strict** — Project uses maximum strictness in linter config where practical. *(
  SUGGESTED)*
    - *Note:*

---

## 🔐 Security

### Secure Development Knowledge

- [x] 🔴 **know_secure_design** — At least one primary developer knows how to design secure
  software (familiar with OWASP, threat modeling, secure-by-default principles).
    - *Self-certification note:* The primary developer (sole maintainer as of now) applies
      secure-by-default design in CarbonTracker wear OS: data is stored locally on-device by
      default,
      location and health permissions are requested only for the features that need them.

- [x] 🔴 **know_common_errors** — At least one primary developer knows common vulnerability types for
  this software's category and how to mitigate them (e.g., injection, XSS, reentrancy for Solidity,
  prompt injection for AI).
    - *Self-certification note:* The primary developer is aware of the common vulnerability types
      for
      a local-first Wear OS app: SQL injection, exposure of sensitive location and health data, and
      untrusted input arriving over the phone-watch Data Layer. SQL injection is mitigated by using
      Room with bound query parameters
      and no string-built SQL. Exposure is limited by keeping data on-device by
      default and requesting location and health permissions only for the features that need them.

### Cryptography (mark N/A if project does not handle cryptography)

- [~] 🔴 **crypto_published** — Only publicly reviewed cryptographic protocols/algorithms are used by
  default.
    - *Note:* `[ ]` N/A

- [~] 🟡 **crypto_call** — Project calls an established crypto library rather than reimplementing
  crypto functions.
    - *Library used:* `[ ]` N/A

- [~] 🔴 **crypto_working** — No broken algorithms (MD4, MD5, single DES, RC4, Dual_EC_DRBG) used
  unless required for interoperability (must be documented).
    - *Note:* `[ ]` N/A

- [~] 🔴 **crypto_keylength** — Key lengths
  meet [NIST 2030 minimums](https://www.keylength.com/en/4/) by default.
    - *Note:* `[ ]` N/A

- [~] 🔴 **crypto_password_storage** — Passwords for external users are stored as iterated salted
  hashes (Argon2id, bcrypt, scrypt, PBKDF2).
    - *Note:* `[ ]` N/A — *Justification (project doesn't store passwords):*

- [~] 🔴 **crypto_random** — Cryptographic keys and nonces are generated using a CSPRNG; insecure
  generators (Math.random, rand()) are NOT used for security purposes.
    - *Note:* `[ ]` N/A

- [~] 🟡 **delivery_unsigned** — Cryptographic hashes are NOT retrieved over plain HTTP without a
  signature check.
    - *Note:*

---

## 🔬 Analysis

### Static Code Analysis

- [x] 🔴 **static_analysis_fixed** — All medium+ severity vulnerabilities found by static analysis
  are fixed in a timely manner after confirmation.
    - *Note:* Static analysis is done with Android Lint (`./gradlew lint`), plus an AI-assisted
      security review of the Wear OS source code. All vulnerabilities found were addressed; the report is
      committed to the repo: https://github.com/AOSSIE-Org/CarbonTracker-WatchCompanionApp/pull/33. Any confirmed medium+ findings in future
      will be fixed promptly.

- [ ] 🔵 **static_analysis_common_vulnerabilities** — The static analysis tool includes checks for
  common vulnerabilities in the language/environment (e.g., eslint-plugin-security, bandit,
  Slither). *(SUGGESTED)*
    - *Tool + ruleset:* `[ ]` N/A

- [ ] 🔵 **static_analysis_often** — Static analysis runs on every commit or at least daily (CI
  integration). *(SUGGESTED)*
    - *Evidence URL:* `[ ]` N/A

### Dynamic Code Analysis

- [ ] 🔵 **dynamic_analysis** — At least one dynamic analysis tool is applied before major releases (
  fuzzer, web app scanner like OWASP ZAP, etc.). *(SUGGESTED)*
    - *Tool used:* `[ ]` N/A — *Justification:*

- [ ] 🔵 **dynamic_analysis_enable_assertions** — Dynamic analysis / testing runs with assertions
  enabled (not just production mode). *(SUGGESTED)*
    - *Note:*

- [ ] 🔴 **dynamic_analysis_fixed** — Medium+ severity vulnerabilities found by dynamic analysis are
  fixed in a timely manner.
    - *Note:* `[x]` N/A (no dynamic analysis tooling is used; manual use of the app has not surfaced
      any vulnerabilities, and any found would be fixed promptly)

- [ ] 🔵 **dynamic_analysis_unsafe** — If the project uses memory-unsafe languages (C/C++), memory
  safety tools (Valgrind, AddressSanitizer) are used. *(SUGGESTED)*
    - *Note:* `[ ]` N/A — *Justification (project uses memory-safe languages):*

---

## 📎 Project-Specific Notes

> Not applicable — CarbonTracker watch app is a wear OS application and is not a Web3, Full-Stack, or
> AI project.

### Web3 / Solidity Notes

- Scorecard does not audit Solidity-specific security.
  Use [Slither](https://github.com/crytic/slither) for `static_analysis` and `warnings` criteria.
- For `crypto_*` criteria, document which cryptographic primitives your contracts rely on (e.g.,
  ECDSA in EVM is standard).
- Smart contract audit reports count as evidence for `know_secure_design`.

### Full-Stack / Next.js Notes

- For `crypto_password_storage`: document which auth library handles hashing (e.g., NextAuth +
  bcrypt).
- For `dynamic_analysis`: [OWASP ZAP](https://www.zaproxy.org/) can be run as a GitHub Action.

### AI / LLM Notes

- For `know_common_errors`: include awareness of prompt injection, data leakage, and model output
  validation.
- For `dynamic_analysis`: consider adversarial input testing as a form of dynamic analysis.

---

*This checklist complements [OpenSSF Scorecard](https://scorecard.dev/) (auto-detected checks) and
is
inspired by the [OpenSSF Best Practices Badge](https://www.bestpractices.dev/en/criteria/0) passing
criteria.*
