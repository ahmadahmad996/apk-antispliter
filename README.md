# APK-AntiSpliter

Android app that merges "split" APK bundles (`.apks`, `.xapk`, `.apkm`) back into a single, installable `.apk` file.

APK-AntiSpliter is a GUI front-end for the merge utilities of the open-source
[REAndroid APKEditor](https://github.com/REAndroid/APKEditor) library, combined with an
Android port of Google's `apksig` library for signing.

## Why merging matters

Split APKs are how modern Android ships large apps: a base APK plus configuration
splits for density, language, CPU architecture and features. That is fine for the
Play Store, but it is inconvenient when you want to archive an app, install it on a
different device, or modify it.

Naive mergers leave the split metadata behind in `AndroidManifest.xml` and in the
resource table. Android then refuses to install the result with a generic
"App not installed" error. APK-AntiSpliter removes that metadata properly, so the
merged APK installs cleanly.

## Features

- Merge `.apks` / `.xapk` / `.apkm` bundles, or a folder of split APKs, into one `.apk`.
- Three ways to start: share a file to the app, open a file with the app, or pick a
  file from inside the app.
- Pick an already-installed app from the device instead of a downloaded bundle.
- Optional dialog to choose exactly which splits are included in the merge.
- Optional automatic split selection based on the device's own screen density,
  languages and CPU ABIs.
- Optional forced merge of mismatched splits (different `versionCode` or package name).
- Selectable signing scheme: **Auto**, **v1 + v2 + v3**, **v2 + v3**, **v3 only**,
  **v1 + v2**, **v1 only**. Auto picks the smallest scheme that the APK's
  `minSdkVersion` actually needs.
- Configurable ZIP compression level (store / fast / normal / maximum).
- Configurable output file name suffix, and optional saving next to the source bundle.
- Shows the SHA-256 fingerprint of the signing certificate so you can verify builds.
- Built-in log view, copy / save / install / cancel actions.
- 21 interface languages.

## Usage

1. Obtain a split bundle (`.apks`, `.xapk`, `.apkm`) or a folder of split APKs.
2. Open it in one of these ways:
   - Share the file and choose **APK-AntiSpliter** in the share sheet.
   - Tap the file in a file manager and choose **APK-AntiSpliter**.
   - Launch the app and press the merge button, then pick the file.
3. Press **Merge** and wait for the progress indicator to finish.
4. Use the buttons to install, save, or copy the log.

You can also open the app list from the merge screen and pick an installed app
directly. Enable **Show all installed apps** in settings to list every app, not only
those installed as splits.

> An APK must be signed before Android will install it. If you plan to modify the
> APK further, sign it after your modifications instead. Some apps verify their own
> signature and may refuse to start after being re-signed.

## Signing schemes

Not every app is signed with all three schemes. Depending on `minSdkVersion` and on
how the original was published, an APK may carry v1 + v2 + v3, v2 + v3 only, v3 only,
v1 + v2, or v1 only. APK-AntiSpliter lets you choose:

| Option | Behaviour |
| --- | --- |
| Auto | `minSdkVersion >= 24` → v2 + v3, otherwise v1 + v2 + v3 |
| v1 + v2 + v3 | Maximum compatibility, slowest on very large APKs |
| v2 + v3 | Modern devices, no JAR signing |
| v3 only | Android 9+ only |
| v1 + v2 | Legacy-compatible without v3 |
| v1 only | Oldest devices, JAR signing only |

If the `minSdkVersion` cannot be determined, the app falls back to v1 + v2 + v3 with
`minSdkVersion 21` instead of failing.

## Permissions

- **Storage / All files access** — only requested if you enable saving the output to
  the same folder as the source bundle.
- **QUERY_ALL_PACKAGES** — to list installed apps for the "pick an installed app" flow.
- **REQUEST_INSTALL_PACKAGES** — to show the install button after a merge.
- **Internet** — to check for updates; can be disabled in settings.

## Building

```bash
./gradlew assembleDebug
```

Requirements: JDK 17+, Android SDK with `compileSdk 36`.

## Open-source components

- [APKEditor / ARSCLib](https://github.com/REAndroid/APKEditor) by REAndroid — the
  merge engine.
- [apksig](https://android.googlesource.com/platform/tools/apksig/) — APK signing,
  via an Android-compatible port.

Both are used under their respective open-source licenses; see `LICENSE`.
