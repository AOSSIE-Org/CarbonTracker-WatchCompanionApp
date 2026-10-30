# Remediations of Security Review Findings

Review date and time: 2026-10-19T11:15:53Z

## Remediations

### Remediation of Finding 1: Sensitive health data logged to Logcat

- [x] This remediation implements the security review's recommendation for this finding.
- [ ] This remediation addresses the finding in a way that differs from the security review's
  recommendation.

Remediation commits:

- [59f0028](https://github.com/aneesafatima/CarbonTracker-WatchCompanionApp/commit/59f0028912959a964597d0331c9db8285fe303fa)

Description: Removed raw heart-rate logging from `HealthServicesManager.kt` so heart-rate values are
no longer printed to Logcat.

### Remediation of Finding 3: Wear Data Layer message handling trusts incoming messages and lacks input size/structure validation

- [x] This remediation implements the security review's recommendation for this finding.
- [ ] This remediation addresses the finding in a way that differs from the security review's
  recommendation.

Remediation commits:

- [8afb61e](https://github.com/aneesafatima/CarbonTracker-WatchCompanionApp/commit/8afb61e50fb74617a8a9670a67a7f2f1c0dadf3d)

Description: Added payload size checks, acknowledgement list size bounds, and per-acknowledgement
field validation (`id` and `lastUpdated`) in `WearListenerService.kt` before marking exercises as
synced.

## Non-remediated findings

### Finding 2: Health/fitness records stored unencrypted in local Room database

This finding was not remediated because No DB encryption now; internal app storage +
backup/device-transfer exclusions; residual risk on rooted/compromised device accepted for current
threat model.

### Finding 4: Potentially sensitive signing credentials present in local `wear/key.properties`

`wear/key.properties` is git-ignored and was not committed. However, the keystore passwords were
accidentally included in a security report committed in PR #33. The keystore file itself was not
exposed. The passwords have been rotated and the report redacted.
