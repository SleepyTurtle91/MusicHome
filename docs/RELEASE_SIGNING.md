# MusicHome Release Signing

This repository never stores release keystores, signing passwords, or encoded signing material.

## 1. Generate the permanent release key (on the owner's computer)

Run on the current development laptop. Do not run this in CI and do not commit the resulting file.

```bash
umask 077
mkdir -p "$HOME/.keys"
keytool -genkeypair -v \
  -keystore "$HOME/.keys/musichome-release.p12" \
  -storetype PKCS12 \
  -keyalg RSA -keysize 4096 -validity 10000 \
  -alias musichome-release
```

Let `keytool` prompt for the keystore password and certificate details. Keep the password strong and unique. With no separate `-keypass`, the key password is the same as the keystore password.

Verify the key and record its SHA-256 certificate fingerprint:

```bash
keytool -list -v \
  -keystore "$HOME/.keys/musichome-release.p12" \
  -storetype PKCS12 \
  -alias musichome-release
```

Make at least two encrypted/offline backups of the `.p12` file. Keep the password separately. Losing this key means future APKs cannot update installations signed with it.

## 2. Configure GitHub Actions secrets

Install and authenticate GitHub CLI (`gh auth login`), then run these commands from any directory:

```bash
gh secret set SIGNING_KEY < <(base64 -w 0 "$HOME/.keys/musichome-release.p12")
gh secret set KEY_ALIAS --body "musichome-release"

read -rsp "Keystore password: " KEYSTORE_PASSWORD; printf '\\n'
printf '%s' "$KEYSTORE_PASSWORD" | gh secret set KEY_STORE_PASSWORD
printf '%s' "$KEYSTORE_PASSWORD" | gh secret set KEY_PASSWORD
unset KEYSTORE_PASSWORD
```

These commands send the encoded keystore and passwords directly to GitHub Actions Secrets; they do not add them to the repository. Do not paste secrets into chat, issues, pull requests, or commit history.

The workflow expects exactly these repository secrets:

- `SIGNING_KEY`: Base64-encoded PKCS12 keystore
- `KEY_ALIAS`: `musichome-release`
- `KEY_STORE_PASSWORD`: keystore password
- `KEY_PASSWORD`: private-key password (same as keystore password in this setup)

## 3. Validate before publishing

1. Confirm all four Actions secrets exist in repository Settings → Secrets and variables → Actions. Never reveal their values.
2. Merge the signing workflow only after the key is generated, backed up, and secrets are configured.
3. Trigger a trusted push build and confirm the signed APK artifact is produced.
4. Verify the artifact locally with Android SDK Build Tools:

   ```bash
   apksigner verify --verbose --print-certs app-release.apk
   ```

5. Compare the signer SHA-256 digest with the fingerprint recorded in step 1. Keep the same keystore for every future MusicHome release.

## Migration note

The old v2.7.0 APK was distributed through GitHub Releases, not Google Play. The original laptop is no longer available and its signing identity is being treated as unrecoverable. A newly generated release key establishes the permanent identity for future releases. Android will not accept an APK signed by this new key as an in-place update to an installation signed by a different key; affected users will need to uninstall the old app before installing the new signed release. Back up any user data they want to preserve before uninstalling.
