# Remediation Report: CarbonTracker Watch

**Developer:** Aneesa Fatima
**Audit report:** `audit/AUDIT_REPORT.md`

## Summary

Finding 3 of the audit concerns the watch app and is fixed in this repo. Findings 1 and 2 are
app-related and have been remediated in the CarbonTracker repo.

## Findings

### 3. Room database warning when building the watch app

- **Status:** Fixed (this repo)
- **Fix:** Room was warning that no schema export directory was provided. Added
  `room.schemaLocation` to the wear module's `build.gradle.kts` so Room writes schema JSON files
  to `schemas/`, and committed the generated `schemas/` folder. The build no longer shows the
  warning.
