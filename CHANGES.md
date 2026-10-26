# APK-AntiSpliter — Change Report
This report lists every file that was changed, explains why, and then reproduces the full
current content of each file exactly as it exists in the project. Nothing here is
paraphrased: the content sections are read directly from the files.

## 1. File list

| # | File | Role |
|---|---|---|
| 1 | `settings.gradle.kts` | Gradle project name |
| 2 | `app/src/main/res/values/strings.xml` | Default strings (English) |
| 3 | `app/src/main/res/values/colors.xml` | Brand palette |
| 4 | `app/src/main/res/values/styles.xml` | Material 3 themes + shared widgets |
| 5 | `app/src/main/res/values-v21/styles.xml` | API 21+ theme copy |
| 6 | `app/src/main/res/values-v31/colors.xml` | API 31+ color overrides |
| 7 | `app/src/main/res/drawable/bg_main_dark.xml` | Dark window background (new) |
| 8 | `app/src/main/res/drawable/bg_main_black.xml` | AMOLED black background (new) |
| 9 | `app/src/main/res/drawable/bg_main_light.xml` | Light window background (new) |
| 10 | `app/src/main/res/drawable/bg_monogram.xml` | Header monogram (new) |
| 11 | `app/src/main/res/layout/activity_main.xml` | Main screen layout |
| 12 | `app/src/main/res/layout/settings_dialog.xml` | Settings dialog layout |
| 13 | `app/src/main/res/layout/dropdownitem.xml` | Dropdown row |
| 14 | `app/src/main/res/layout/list_item.xml` | Installed-app list row |
| 15 | `app/src/main/java/com/abdurazaaqmohammed/utils/SignUtil.java` | Signing engine |
| 16 | `app/src/main/java/com/reandroid/Merger.java` | Merge pipeline |
| 17 | `app/src/main/java/com/abdurazaaqmohammed/AntiSplit/main/MainActivity.java` | Main activity |
| 18 | `app/src/main/java/com/abdurazaaqmohammed/utils/RunUtil.java` | Background task runner |
| 19 | `app/src/main/java/com/abdurazaaqmohammed/utils/UpdateUtil.java` | Update checker |
| 20 | `app/src/main/java/com/starry/FileUtils.java` | SAF helper |
| 21 | `README.md` | Project documentation |
| 22 | `app/src/main/res/values-*/strings.xml` | 19 locale files (app name) |

## 2. What changed and why

### `settings.gradle.kts`

rootProject.name changed from "AntiSplit M" to "APK-AntiSpliter" so IDE/build outputs use the new name.

### `app/src/main/res/values/strings.xml`

app_name set to APK-AntiSpliter (translatable=false). Added strings for the new signing-scheme selector, certificate fingerprint, show-all-apps toggle, section headers, merge card copy, log card copy, and error messages (invalid_file, out_of_memory, processing). Removed the create_issue string (#619). new_ver text updated to the new app name.

### `app/src/main/res/values/colors.xml`

Fully rewritten with an "Elegant Dark" palette: gold (gold_200..gold_700), silver, ink (near-black surfaces) and warm paper tones for the light theme. Legacy names used by Java code (main_500, secondary_600, main_dark_800, colorPrimary, colorAccent, colorHeader, textColorPrimary/Secondary) are kept as aliases so no Java change is required.

### `app/src/main/res/values/styles.xml`

Theme.MyApp.Dark / .Black / .Light rewritten with full Material3 color roles, transparent status/nav bars and correct light-bar flags. Adds reusable styles: Widget.AntiSplit.Card (22dp radius, 1dp outline), TextAppearance.AntiSplit.Section/Title/Subtitle, Widget.AntiSplit.Button.Primary/Outlined, Widget.AntiSplit.Switch and Widget.AntiSplit.Divider.

### `app/src/main/res/values-v21/styles.xml`

Kept byte-identical to values/styles.xml so API 21+ devices get the same redesigned theme.

### `app/src/main/res/values-v31/colors.xml`

Previously remapped the accent colors to @android:color/system_accent*, which silently replaced the brand palette on Android 12+. Now pins main_500/secondary_600/main_dark_800 to the gold/silver brand colors.

### `app/src/main/res/drawable/bg_main_dark.xml`

Ink vertical gradient plus a soft radial gold glow in the top-right corner. Referenced by Theme.MyApp.Dark.

### `app/src/main/res/drawable/bg_main_black.xml`

Pure black with a very subtle gold vignette. Referenced by Theme.MyApp.Black.

### `app/src/main/res/drawable/bg_main_light.xml`

Warm paper gradient with a light gold wash. Referenced by Theme.MyApp.Light.

### `app/src/main/res/drawable/bg_monogram.xml`

Gold gradient rounded square used as the background of the 52dp "A" monogram in the header.

### `app/src/main/res/layout/activity_main.xml`

Redesigned: header (gold monogram + app name + subtitle), a merge card containing the merge button, formats caption, installed-apps button and progress indicator, a log card with a section label and scrollable log, and a vertical FAB column (copy/install/save/cancel/settings). All view ids used by MainActivity are preserved, so no Java changes were needed for the layout swap.

### `app/src/main/res/layout/settings_dialog.xml`

The top theme toggle card (sun / moon / black / system buttons) is removed, as requested. The remaining options are grouped into four labelled cards: Merging, Output, Signing, Apps and updates. Adds the signing-scheme dropdown, certificate fingerprint text and copy button, and the show-all-apps switch. All pre-existing ids are preserved.

### `app/src/main/res/layout/dropdownitem.xml`

Text color changed from a hard-coded white to ?attr/colorOnSurface so dropdown rows stay readable in the light theme.

### `app/src/main/res/layout/list_item.xml`

Padding switched to start/end 8dp and top/bottom 12dp for a taller touch target; text color bound to ?attr/colorOnSurface and size set to 15sp.

### `app/src/main/java/com/abdurazaaqmohammed/utils/SignUtil.java`

Adds explicit signing schemes: SCHEME_AUTO=0, V1_V2_V3=1, V2_V3=2, V3=3, V1_V2=4, V1=5. resolveScheme() maps AUTO to v2+v3 when minSdkVersion >= 24 and to v1+v2+v3 otherwise, so large APKs are not forced through slow v1 JAR hashing (#650). detectMinSdk() reads the binary manifest through ZipFile + ApkUtils.getMinSdkVersionFromBinaryAndroidManifest; on any failure it falls back to minSdk 21 with v1+v2+v3 instead of aborting (#633). getSigningCertificateSha256() exposes the debug certificate fingerprint for display (#673).

### `app/src/main/java/com/reandroid/Merger.java`

1) No longer deletes android:extractNativeLibs; it is set to true and ApkModule.setExtractNativeLibs(TRUE) is called, so native libraries are extracted at install time. This fixes the Android 14+ / 16 KB page-size install failures (#612, #648). 2) Split-metadata resource removal now collects paths first and mutates after iteration (avoids ConcurrentModificationException on large tables) and only deletes res/xml/* or *.xml entries, so icons/drawables referenced by the same table survive (#665). 3) Overloads accept a signing scheme and pass it to SignUtil.signDebugKey. 4) extractZipFileFallback() retries with java.util.zip.ZipFile when the streaming reader fails with "Failed to find end record" (#20).

### `app/src/main/java/com/abdurazaaqmohammed/AntiSplit/main/MainActivity.java`

Adds signingScheme/showAllApps state and persists the processing state, log and error across configuration changes so the merge UI no longer disappears after rotation (#620); onConfigurationChanged re-applies the progress/cancel state. Cache deletion is guarded by savedInstanceState == null and onDestroy only cleans up when isFinishing() && !isChangingConfigurations(). Adds getRawFileName() (display name without extension rewriting) so .apk-vs-bundle detection is correct (#624) and multi-select works (#609); the open intent now passes EXTRA_MIME_TYPES so .apkm is selectable (#509). sanitizeFileName() strips apkmirror.com-style prefixes and illegal characters (#387). showError(Throwable) no longer calls uris.remove(0) on an empty list. Settings wiring for the scheme dropdown, fingerprint display/copy and show-all-apps filter is added.

### `app/src/main/java/com/abdurazaaqmohammed/utils/RunUtil.java`

Both async paths now call executor.shutdown() in a finally block, and reportFailure() unwraps ExecutionException so an OutOfMemoryError is detected and reported with a friendly message instead of a generic crash (#653, #143).

### `app/src/main/java/com/abdurazaaqmohammed/utils/UpdateUtil.java`

Parses the GitHub releases JSON with org.json and compares versions numerically, so tags such as "v2.3.0-beta" no longer break update detection (#628). Downloaded file is named APK-AntiSpliter.v<version>.<ext> and changelog newlines are normalized correctly.

### `app/src/main/java/com/starry/FileUtils.java`

getDataColumn() uses a null projection with getColumnIndex() plus a fallback, so content providers that do not expose the legacy _data column no longer throw (#634).

### `README.md`

Rewritten for APK-AntiSpliter: removes the old title, the developer profile / GitHub links and the third-party app promotion, and documents the new features, signing schemes and permissions.

### Locale strings

The following 19 translation files were updated so the app name reads APK-AntiSpliter
instead of the old name. New strings fall back to the English defaults in `values/strings.xml`,
which is the standard Android behaviour and keeps the app fully usable in every locale:

- `app/src/main/res/values-ar/strings.xml`
- `app/src/main/res/values-de/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/main/res/values-fa/strings.xml`
- `app/src/main/res/values-fr/strings.xml`
- `app/src/main/res/values-hu/strings.xml`
- `app/src/main/res/values-in/strings.xml`
- `app/src/main/res/values-it/strings.xml`
- `app/src/main/res/values-ja/strings.xml`
- `app/src/main/res/values-ko/strings.xml`
- `app/src/main/res/values-pl/strings.xml`
- `app/src/main/res/values-pt-rBR/strings.xml`
- `app/src/main/res/values-ru/strings.xml`
- `app/src/main/res/values-sk/strings.xml`
- `app/src/main/res/values-tr/strings.xml`
- `app/src/main/res/values-uk/strings.xml`
- `app/src/main/res/values-uz/strings.xml`
- `app/src/main/res/values-vi/strings.xml`
- `app/src/main/res/values-zh-rCN/strings.xml`
- `app/src/main/res/values-zh-rTW/strings.xml`

## 3. Issue coverage

| Issue | Summary | Where it is addressed |
|---|---|---|
| #620 | Merge UI disappears after rotation | MainActivity: onSaveInstanceState/onRestoreInstanceState for processing, log and error; onConfigurationChanged |
| #648 | Newer split APKs produce crashing / uninstallable APK | Merger: extractNativeLibs forced true + setExtractNativeLibs(TRUE) |
| #650 | Signing hangs on large APKs | SignUtil: AUTO scheme skips v1 JAR hashing when minSdk >= 24 |
| #653 | Out of memory on large split APKs | RunUtil.reportFailure unwraps ExecutionException / OutOfMemoryError |
| #646 | Compression options ineffective / split metadata remains | Merger compression level + guarded metadata removal; settings dropdown maps to level |
| #612 | Merged APK not installable on Android 14 | Merger: extractNativeLibs handling (16 KB page size) |
| #665 | Icons/images removed after conversion | Merger: only res/xml/* and *.xml entries are deleted |
| #624 | Valid APKs rejected as "Not an APK File" | MainActivity.getRawFileName + isApkFileName checks |
| #509 | APKM missing from document picker | EXTRA_MIME_TYPES includes application/vnd.apkm |
| #387 | apkmirror.com file names break selection | MainActivity.sanitizeFileName |
| #609 | Multi-select restricted to one file | getRawFileName + EXTRA_ALLOW_MULTIPLE / ClipData handling |
| #628 | GitHub release version format breaks update check | UpdateUtil JSON parsing + numeric compare |
| #634 | Content provider lacks legacy _data column | FileUtils.getDataColumn null-projection fallback |
| #633 | Signing fails when minSdk is undeterminable | SignUtil fallback to minSdk 21 + v1+v2+v3 |
| #673 | Publish signing cert SHA-256 fingerprint | SignUtil.getSigningCertificateSha256 + settings card |
| #619 | Missing strings.xml entries | strings.xml additions (all R.string references verified present) |
| #627 | Configurable compression level | Settings dropdown + ApkBundle(compressionLevel) |
| #641 | List all installed apps, not only split APKs | showAllApps setting + filter in installedAppsClickListener |
| #143 | Out of memory | Same as #653 |
| #20 | "Failed to find end record" | Merger.extractZipFileFallback (ZipFile retry) |

## 4. Full file contents

### `settings.gradle.kts`

```kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "APK-AntiSpliter"
include(":app")
```

### `app/src/main/res/values/strings.xml`

```xml
<resources>
    <string name="app_name" translatable="false">APK-AntiSpliter</string>
    <string name="merge">Select split APK file to merge (APKS;APKM;XAPK)</string>
    <string name="success_saved">File saved successfully</string>
    <string name="enable_logs">Enable logs</string>
    <string name="ask">Ask each time</string>
    <string name="same">Same directory as selected file</string>
    <string name="grant_storage">Storage permission needed to save to the same dir as the selected file</string>
    <string name="no_filepath">"Can\'t write to the filepath "</string>
    <string name="output">"Output directory: "</string>
    <string name="show_dialog">Show dialog allowing selection of which splits to include</string>
    <string name="nothing">Nothing selected</string>
    <string name="all">Select All</string>
    <string name="for_device">Select all splits for your device specifications</string>
    <string name="lang_for_device">Select language for your device</string>
    <string name="arch_for_device">Select architecture for your device</string>
    <string name="dpi_for_device">Select screen widths for your device</string>
    <string name="select_splits">Select splits</string>
    <string name="sign_apk">Sign the APK after merging</string>
    <string name="automatically_select">Automatically select splits for your device specifications</string>
    <string name="sign_failed">Failed to sign APK, saving without signing</string>
    <string name="signing">Signing APK…</string>
    <string name="saving">Saving…</string>
    <string name="removed_table_entry">"Removed table-entry : "</string>
    <string name="sanitizing_manifest">Sanitizing manifest…</string>
    <string name="skipping">"Skipping "</string>
    <string name="not_apk">: Not an APK file</string>
    <string name="unselected">: Unselected</string>
    <string name="searching">Searching APK files…</string>
    <string name="cancel">Cancel</string>
    <string name="select">Select</string>
    <string name="lang">Language</string>
    <string name="update">Update available</string>
    <string name="new_ver">A new version of APK-AntiSpliter is available</string>
    <string name="dl">Download</string>
    <string name="auto_update">Check for updates automatically</string>
    <string name="check_update_now">Check for update now</string>
    <string name="no_update_found">No update found</string>
    <string name="settings">Settings</string>
    <string name="close">Close</string>
    <string name="mismatch">The following splits (%1$s) have mismatched version codes. They are only language codes, so the app can run without them, but these languages will not work in the app. Do you want to continue with merging anyway?</string>
    <string name="warning">Warning</string>
    <string name="mismatch_base">" version code does not match that of base.apk"</string>
    <string name="copy_log">Copy log</string>
    <string name="pause">Pause button</string>
    <string name="copied_log">Copied log</string>
    <string name="select_from_installed_apps">Select from Installed Apps</string>
    <string name="app_icon_list_label">App icon</string>
    <string name="install">Install</string>
    <string name="black_theme">Black theme</string>
    <string name="dark_theme">Dark theme</string>
    <string name="light_theme">Light theme</string>
    <string name="system_theme">System theme</string>
    <string name="suffix">Suffix</string>
    <string name="pairip_warning">The app you are trying to merge uses Pairip/Google加固 Integrity to protect it from modification. The merged version will crash on opening unless you know how to modify the APK to bypass it.</string>
    <string name="force">Force merging mismatched splits (with different versionCode or package name)</string>
    <string name="file_save_method">File save method</string>
    <string name="pick_folder">Enter folder</string>
    <string name="not_split">This does not seem to be a split APK (%1$s)</string>
    <string name="merged">Merged successfully, select where to save</string>
    <string name="save">save</string>
    <string name="check_file_valid">"Please check that file selected is a valid ZIP/split APK (Try opening in file manager or installing it). If you have it installed but still getting error please try the feature "</string> <!-- select_from_installed_apps -->
    <string name="select_compression_level">Select compression level</string><!-- lower = faster, higher = smaller file -->
    <string name="default_c">Default</string>

    <!-- Signing scheme -->
    <string name="signing_scheme">Signing scheme</string>
    <string name="signing_scheme_desc">Choose which APK signature schemes to use. Auto picks the best scheme for the app\'s minimum Android version.</string>
    <string name="scheme_auto">Auto (recommended)</string>
    <string name="scheme_v1v2v3">v1 + v2 + v3</string>
    <string name="scheme_v2v3">v2 + v3</string>
    <string name="scheme_v3">v3 only</string>
    <string name="scheme_v1v2">v1 + v2</string>
    <string name="scheme_v1">v1 only</string>
    <string name="signing_cert_fingerprint">Signing certificate SHA-256</string>
    <string name="copy_fingerprint">Copy fingerprint</string>
    <string name="fingerprint_copied">Fingerprint copied</string>
    <string name="fingerprint_unavailable">Fingerprint unavailable</string>

    <!-- Installed apps -->
    <string name="show_all_apps">Show all installed apps (not only split APKs)</string>
    <string name="app_name_label">App name</string>

    <!-- Misc -->
    <string name="processing">Processing\u2026</string>
    <string name="invalid_file">Selected file could not be read as a ZIP/APK</string>
    <string name="out_of_memory">Not enough memory to process this APK. Try closing other apps or selecting fewer splits.</string>
    <string name="theme_section">Appearance</string>
    <string name="signing_section">Signing</string>
    <string name="merge_section">Merging</string>
    <string name="output_section">Output</string>
    <string name="app_subtitle">Merge split APKs into one installable APK</string>
    <string name="merge_card_title">Start a merge</string>
    <string name="merge_card_desc">Pick an APKS, APKM or XAPK bundle, or pull the splits straight from an installed app.</string>
    <string name="log_card_title">Activity log</string>
    <string name="ready_hint">Ready when you are.</string>
    <string name="merge_button">Select split APK</string>
    <string name="merge_formats">APKS · APKM · XAPK</string>
    <string name="apps_section">Apps and updates</string>
</resources>
```

### `app/src/main/res/values/colors.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  APK-AntiSpliter — "Elegant Dark" palette.
  A deep charcoal/ink background paired with a champagne-gold accent and a
  soft silver secondary. The light variant uses warm paper tones so the same
  gold accent stays readable in daylight.
-->
<resources>
    <!-- Legacy names still referenced by code / other resources -->
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>

    <!-- Primary accent: champagne gold -->
    <color name="gold_200">#F7E3B8</color>
    <color name="gold_300">#F2D28C</color>
    <color name="gold_400">#EFC46A</color>
    <color name="gold_500">#E4B04A</color>
    <color name="gold_600">#C9922F</color>
    <color name="gold_700">#A5761F</color>
    <color name="gold_soft_dark">#3A2E17</color>
    <color name="gold_soft_light">#FBF1D8</color>

    <!-- Secondary accent: cool silver -->
    <color name="silver_200">#D7DEE6</color>
    <color name="silver_400">#A9B6C4</color>
    <color name="silver_600">#7C8A99</color>

    <!-- Dark surfaces (ink) -->
    <color name="ink_950">#080B10</color>
    <color name="ink_900">#0E141B</color>
    <color name="ink_850">#121A23</color>
    <color name="ink_800">#18222D</color>
    <color name="ink_700">#22303D</color>
    <color name="ink_600">#2E3F4F</color>

    <!-- Light surfaces (warm paper) -->
    <color name="paper_50">#FCFAF5</color>
    <color name="paper_100">#F6F2E9</color>
    <color name="paper_200">#EDE7DA</color>
    <color name="paper_300">#DED6C5</color>

    <!-- Text -->
    <color name="text_dark_primary">#F4F7FA</color>
    <color name="text_dark_secondary">#9FB0BF</color>
    <color name="text_light_primary">#151A20</color>
    <color name="text_light_secondary">#5B6774</color>

    <!-- Dividers / outlines -->
    <color name="divider_dark">#24313E</color>
    <color name="divider_light">#E2DACB</color>

    <!-- Semantic -->
    <color name="error_dark">#FF7A7A</color>
    <color name="error_light">#B3261E</color>

    <!--
      Legacy aliases. main_500 / secondary_600 / main_dark_800 are used all
      over the Java code (dialogs, FAB tint, borders), so they are remapped to
      the new accent instead of being removed.
    -->
    <color name="colorPrimary">@color/gold_500</color>
    <color name="colorAccent">@color/gold_500</color>
    <color name="textColorPrimary">@color/text_dark_primary</color>
    <color name="textColorSecondary">@color/text_dark_secondary</color>
    <color name="colorHeader">@color/gold_500</color>
    <color name="main_500">@color/gold_500</color>
    <color name="secondary_600">@color/silver_400</color>
    <color name="main_dark_800">@color/gold_700</color>
</resources>
```

### `app/src/main/res/values/styles.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  APK-AntiSpliter themes.
  Design direction: "Elegant Dark" — deep ink surfaces, champagne-gold accent,
  silver secondary, generous corner radii and hairline outlines instead of
  heavy shadows (a flat, glass-adjacent look that still reads as Material 3).
-->
<resources>

    <!-- ============================ DARK ============================ -->
    <style name="Theme.MyApp.Dark" parent="Theme.Material3.Dark.NoActionBar">
        <item name="android:windowBackground">@drawable/bg_main_dark</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@color/ink_950</item>
        <item name="android:windowLightStatusBar">false</item>
        <item name="android:windowLightNavigationBar">false</item>

        <item name="colorPrimary">@color/gold_500</item>
        <item name="colorOnPrimary">@color/ink_950</item>
        <item name="colorPrimaryContainer">@color/gold_soft_dark</item>
        <item name="colorOnPrimaryContainer">@color/gold_200</item>
        <item name="colorPrimaryVariant">@color/gold_700</item>

        <item name="colorSecondary">@color/silver_400</item>
        <item name="colorOnSecondary">@color/ink_950</item>
        <item name="colorSecondaryContainer">@color/ink_700</item>
        <item name="colorOnSecondaryContainer">@color/silver_200</item>
        <item name="colorAccent">@color/gold_500</item>

        <item name="android:colorBackground">@color/ink_950</item>
        <item name="colorBackground">@color/ink_950</item>
        <item name="colorOnBackground">@color/text_dark_primary</item>
        <item name="colorSurface">@color/ink_900</item>
        <item name="colorOnSurface">@color/text_dark_primary</item>
        <item name="colorSurfaceVariant">@color/ink_800</item>
        <item name="colorOnSurfaceVariant">@color/text_dark_secondary</item>
        <item name="colorOutline">@color/divider_dark</item>
        <item name="colorOutlineVariant">@color/ink_700</item>
        <item name="colorError">@color/error_dark</item>
        <item name="colorOnError">@color/ink_950</item>
        <item name="colorControlHighlight">#33E4B04A</item>
    </style>

    <!-- ============================ BLACK ============================ -->
    <style name="Theme.MyApp.Black" parent="Theme.Material3.Dark.NoActionBar">
        <item name="android:windowBackground">@drawable/bg_main_black</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@color/black</item>
        <item name="android:windowLightStatusBar">false</item>
        <item name="android:windowLightNavigationBar">false</item>

        <item name="colorPrimary">@color/gold_500</item>
        <item name="colorOnPrimary">@color/black</item>
        <item name="colorPrimaryContainer">@color/gold_soft_dark</item>
        <item name="colorOnPrimaryContainer">@color/gold_200</item>
        <item name="colorPrimaryVariant">@color/gold_700</item>

        <item name="colorSecondary">@color/silver_400</item>
        <item name="colorOnSecondary">@color/black</item>
        <item name="colorSecondaryContainer">#1A1A1A</item>
        <item name="colorOnSecondaryContainer">@color/silver_200</item>
        <item name="colorAccent">@color/gold_500</item>

        <item name="android:colorBackground">@color/black</item>
        <item name="colorBackground">@color/black</item>
        <item name="colorOnBackground">@color/text_dark_primary</item>
        <item name="colorSurface">#0A0A0A</item>
        <item name="colorOnSurface">@color/text_dark_primary</item>
        <item name="colorSurfaceVariant">#141414</item>
        <item name="colorOnSurfaceVariant">@color/text_dark_secondary</item>
        <item name="colorOutline">#242424</item>
        <item name="colorOutlineVariant">#1C1C1C</item>
        <item name="colorError">@color/error_dark</item>
        <item name="colorOnError">@color/black</item>
        <item name="colorControlHighlight">#33E4B04A</item>
    </style>

    <!-- ============================ LIGHT ============================ -->
    <style name="Theme.MyApp.Light" parent="Theme.Material3.Light.NoActionBar">
        <item name="android:windowBackground">@drawable/bg_main_light</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@color/paper_100</item>
        <item name="android:windowLightStatusBar">true</item>
        <item name="android:windowLightNavigationBar">true</item>

        <item name="colorPrimary">@color/gold_600</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorPrimaryContainer">@color/gold_soft_light</item>
        <item name="colorOnPrimaryContainer">@color/gold_700</item>
        <item name="colorPrimaryVariant">@color/gold_700</item>

        <item name="colorSecondary">@color/silver_600</item>
        <item name="colorOnSecondary">@color/white</item>
        <item name="colorSecondaryContainer">@color/paper_200</item>
        <item name="colorOnSecondaryContainer">@color/text_light_primary</item>
        <item name="colorAccent">@color/gold_600</item>

        <item name="android:colorBackground">@color/paper_100</item>
        <item name="colorBackground">@color/paper_100</item>
        <item name="colorOnBackground">@color/text_light_primary</item>
        <item name="colorSurface">@color/white</item>
        <item name="colorOnSurface">@color/text_light_primary</item>
        <item name="colorSurfaceVariant">@color/paper_200</item>
        <item name="colorOnSurfaceVariant">@color/text_light_secondary</item>
        <item name="colorOutline">@color/divider_light</item>
        <item name="colorOutlineVariant">@color/paper_300</item>
        <item name="colorError">@color/error_light</item>
        <item name="colorOnError">@color/white</item>
        <item name="colorControlHighlight">#26C9922F</item>
    </style>

    <!-- ======================== SHARED WIDGETS ======================== -->

    <!-- Hairline "glass" card: flat, outlined, rounded. -->
    <style name="Widget.AntiSplit.Card" parent="Widget.Material3.CardView.Filled">
        <item name="cardBackgroundColor">?attr/colorSurface</item>
        <item name="cardCornerRadius">22dp</item>
        <item name="cardElevation">0dp</item>
        <item name="cardPreventCornerOverlap">true</item>
        <item name="strokeWidth">1dp</item>
        <item name="strokeColor">?attr/colorOutline</item>
        <item name="rippleColor">?attr/colorControlHighlight</item>
    </style>

    <!-- Small uppercase section label inside the settings sheet. -->
    <style name="TextAppearance.AntiSplit.Section" parent="TextAppearance.Material3.LabelMedium">
        <item name="android:textSize">12sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">0.14</item>
        <item name="android:textAllCaps">true</item>
        <item name="android:textColor">?attr/colorPrimary</item>
    </style>

    <!-- Big app title on the main screen. -->
    <style name="TextAppearance.AntiSplit.Title" parent="TextAppearance.Material3.HeadlineMedium">
        <item name="android:textSize">28sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">0.01</item>
        <item name="android:textColor">?attr/colorOnBackground</item>
    </style>

    <style name="TextAppearance.AntiSplit.Subtitle" parent="TextAppearance.Material3.BodyMedium">
        <item name="android:textSize">14sp</item>
        <item name="android:textColor">?attr/colorOnSurfaceVariant</item>
    </style>

    <style name="Widget.AntiSplit.Button.Primary" parent="Widget.Material3.Button">
        <item name="android:minHeight">52dp</item>
        <item name="cornerRadius">16dp</item>
        <item name="android:textSize">15sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">0.02</item>
    </style>

    <style name="Widget.AntiSplit.Button.Outlined" parent="Widget.Material3.Button.OutlinedButton">
        <item name="android:minHeight">52dp</item>
        <item name="cornerRadius">16dp</item>
        <item name="android:textSize">15sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">0.02</item>
        <item name="strokeColor">?attr/colorOutline</item>
    </style>

    <style name="Widget.AntiSplit.Switch" parent="Widget.Material3.CompoundButton.MaterialSwitch">
        <item name="android:textSize">15sp</item>
        <item name="android:textColor">?attr/colorOnSurface</item>
        <item name="android:minHeight">56dp</item>
    </style>

    <style name="Widget.AntiSplit.Divider">
        <item name="android:layout_width">match_parent</item>
        <item name="android:layout_height">1dp</item>
        <item name="android:background">?attr/colorOutlineVariant</item>
    </style>
</resources>
```

### `app/src/main/res/values-v21/styles.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  APK-AntiSpliter themes.
  Design direction: "Elegant Dark" — deep ink surfaces, champagne-gold accent,
  silver secondary, generous corner radii and hairline outlines instead of
  heavy shadows (a flat, glass-adjacent look that still reads as Material 3).
-->
<resources>

    <!-- ============================ DARK ============================ -->
    <style name="Theme.MyApp.Dark" parent="Theme.Material3.Dark.NoActionBar">
        <item name="android:windowBackground">@drawable/bg_main_dark</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@color/ink_950</item>
        <item name="android:windowLightStatusBar">false</item>
        <item name="android:windowLightNavigationBar">false</item>

        <item name="colorPrimary">@color/gold_500</item>
        <item name="colorOnPrimary">@color/ink_950</item>
        <item name="colorPrimaryContainer">@color/gold_soft_dark</item>
        <item name="colorOnPrimaryContainer">@color/gold_200</item>
        <item name="colorPrimaryVariant">@color/gold_700</item>

        <item name="colorSecondary">@color/silver_400</item>
        <item name="colorOnSecondary">@color/ink_950</item>
        <item name="colorSecondaryContainer">@color/ink_700</item>
        <item name="colorOnSecondaryContainer">@color/silver_200</item>
        <item name="colorAccent">@color/gold_500</item>

        <item name="android:colorBackground">@color/ink_950</item>
        <item name="colorBackground">@color/ink_950</item>
        <item name="colorOnBackground">@color/text_dark_primary</item>
        <item name="colorSurface">@color/ink_900</item>
        <item name="colorOnSurface">@color/text_dark_primary</item>
        <item name="colorSurfaceVariant">@color/ink_800</item>
        <item name="colorOnSurfaceVariant">@color/text_dark_secondary</item>
        <item name="colorOutline">@color/divider_dark</item>
        <item name="colorOutlineVariant">@color/ink_700</item>
        <item name="colorError">@color/error_dark</item>
        <item name="colorOnError">@color/ink_950</item>
        <item name="colorControlHighlight">#33E4B04A</item>
    </style>

    <!-- ============================ BLACK ============================ -->
    <style name="Theme.MyApp.Black" parent="Theme.Material3.Dark.NoActionBar">
        <item name="android:windowBackground">@drawable/bg_main_black</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@color/black</item>
        <item name="android:windowLightStatusBar">false</item>
        <item name="android:windowLightNavigationBar">false</item>

        <item name="colorPrimary">@color/gold_500</item>
        <item name="colorOnPrimary">@color/black</item>
        <item name="colorPrimaryContainer">@color/gold_soft_dark</item>
        <item name="colorOnPrimaryContainer">@color/gold_200</item>
        <item name="colorPrimaryVariant">@color/gold_700</item>

        <item name="colorSecondary">@color/silver_400</item>
        <item name="colorOnSecondary">@color/black</item>
        <item name="colorSecondaryContainer">#1A1A1A</item>
        <item name="colorOnSecondaryContainer">@color/silver_200</item>
        <item name="colorAccent">@color/gold_500</item>

        <item name="android:colorBackground">@color/black</item>
        <item name="colorBackground">@color/black</item>
        <item name="colorOnBackground">@color/text_dark_primary</item>
        <item name="colorSurface">#0A0A0A</item>
        <item name="colorOnSurface">@color/text_dark_primary</item>
        <item name="colorSurfaceVariant">#141414</item>
        <item name="colorOnSurfaceVariant">@color/text_dark_secondary</item>
        <item name="colorOutline">#242424</item>
        <item name="colorOutlineVariant">#1C1C1C</item>
        <item name="colorError">@color/error_dark</item>
        <item name="colorOnError">@color/black</item>
        <item name="colorControlHighlight">#33E4B04A</item>
    </style>

    <!-- ============================ LIGHT ============================ -->
    <style name="Theme.MyApp.Light" parent="Theme.Material3.Light.NoActionBar">
        <item name="android:windowBackground">@drawable/bg_main_light</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@color/paper_100</item>
        <item name="android:windowLightStatusBar">true</item>
        <item name="android:windowLightNavigationBar">true</item>

        <item name="colorPrimary">@color/gold_600</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorPrimaryContainer">@color/gold_soft_light</item>
        <item name="colorOnPrimaryContainer">@color/gold_700</item>
        <item name="colorPrimaryVariant">@color/gold_700</item>

        <item name="colorSecondary">@color/silver_600</item>
        <item name="colorOnSecondary">@color/white</item>
        <item name="colorSecondaryContainer">@color/paper_200</item>
        <item name="colorOnSecondaryContainer">@color/text_light_primary</item>
        <item name="colorAccent">@color/gold_600</item>

        <item name="android:colorBackground">@color/paper_100</item>
        <item name="colorBackground">@color/paper_100</item>
        <item name="colorOnBackground">@color/text_light_primary</item>
        <item name="colorSurface">@color/white</item>
        <item name="colorOnSurface">@color/text_light_primary</item>
        <item name="colorSurfaceVariant">@color/paper_200</item>
        <item name="colorOnSurfaceVariant">@color/text_light_secondary</item>
        <item name="colorOutline">@color/divider_light</item>
        <item name="colorOutlineVariant">@color/paper_300</item>
        <item name="colorError">@color/error_light</item>
        <item name="colorOnError">@color/white</item>
        <item name="colorControlHighlight">#26C9922F</item>
    </style>

    <!-- ======================== SHARED WIDGETS ======================== -->

    <!-- Hairline "glass" card: flat, outlined, rounded. -->
    <style name="Widget.AntiSplit.Card" parent="Widget.Material3.CardView.Filled">
        <item name="cardBackgroundColor">?attr/colorSurface</item>
        <item name="cardCornerRadius">22dp</item>
        <item name="cardElevation">0dp</item>
        <item name="cardPreventCornerOverlap">true</item>
        <item name="strokeWidth">1dp</item>
        <item name="strokeColor">?attr/colorOutline</item>
        <item name="rippleColor">?attr/colorControlHighlight</item>
    </style>

    <!-- Small uppercase section label inside the settings sheet. -->
    <style name="TextAppearance.AntiSplit.Section" parent="TextAppearance.Material3.LabelMedium">
        <item name="android:textSize">12sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">0.14</item>
        <item name="android:textAllCaps">true</item>
        <item name="android:textColor">?attr/colorPrimary</item>
    </style>

    <!-- Big app title on the main screen. -->
    <style name="TextAppearance.AntiSplit.Title" parent="TextAppearance.Material3.HeadlineMedium">
        <item name="android:textSize">28sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">0.01</item>
        <item name="android:textColor">?attr/colorOnBackground</item>
    </style>

    <style name="TextAppearance.AntiSplit.Subtitle" parent="TextAppearance.Material3.BodyMedium">
        <item name="android:textSize">14sp</item>
        <item name="android:textColor">?attr/colorOnSurfaceVariant</item>
    </style>

    <style name="Widget.AntiSplit.Button.Primary" parent="Widget.Material3.Button">
        <item name="android:minHeight">52dp</item>
        <item name="cornerRadius">16dp</item>
        <item name="android:textSize">15sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">0.02</item>
    </style>

    <style name="Widget.AntiSplit.Button.Outlined" parent="Widget.Material3.Button.OutlinedButton">
        <item name="android:minHeight">52dp</item>
        <item name="cornerRadius">16dp</item>
        <item name="android:textSize">15sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">0.02</item>
        <item name="strokeColor">?attr/colorOutline</item>
    </style>

    <style name="Widget.AntiSplit.Switch" parent="Widget.Material3.CompoundButton.MaterialSwitch">
        <item name="android:textSize">15sp</item>
        <item name="android:textColor">?attr/colorOnSurface</item>
        <item name="android:minHeight">56dp</item>
    </style>

    <style name="Widget.AntiSplit.Divider">
        <item name="android:layout_width">match_parent</item>
        <item name="android:layout_height">1dp</item>
        <item name="android:background">?attr/colorOutlineVariant</item>
    </style>
</resources>
```

### `app/src/main/res/values-v31/colors.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  On Android 12+ the platform normally replaces the app accent with the
  user's wallpaper-derived "system accent". APK-AntiSpliter has a deliberate
  brand palette (champagne gold), so the aliases are pinned back to it here.
-->
<resources>
    <color name="main_500">@color/gold_500</color>
    <color name="secondary_600">@color/silver_400</color>
    <color name="main_dark_800">@color/gold_700</color>
</resources>
```

### `app/src/main/res/drawable/bg_main_dark.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- Deep ink gradient with a faint champagne glow in the top-right corner. -->
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <gradient
                android:angle="270"
                android:startColor="#0B1118"
                android:centerColor="#0E141B"
                android:endColor="#070A0E"
                android:type="linear" />
        </shape>
    </item>
    <item>
        <shape android:shape="oval">
            <gradient
                android:type="radial"
                android:centerX="0.88"
                android:centerY="0.02"
                android:gradientRadius="420dp"
                android:startColor="#26E4B04A"
                android:endColor="#00E4B04A" />
        </shape>
    </item>
</layer-list>
```

### `app/src/main/res/drawable/bg_main_black.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- Pure-black AMOLED variant with an extremely subtle gold vignette. -->
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="#000000" />
        </shape>
    </item>
    <item>
        <shape android:shape="oval">
            <gradient
                android:type="radial"
                android:centerX="0.9"
                android:centerY="0.0"
                android:gradientRadius="360dp"
                android:startColor="#1FE4B04A"
                android:endColor="#00E4B04A" />
        </shape>
    </item>
</layer-list>
```

### `app/src/main/res/drawable/bg_main_light.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- Warm paper gradient with a soft gold wash, for the light theme. -->
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <gradient
                android:angle="270"
                android:startColor="#FDFBF7"
                android:centerColor="#F6F2E9"
                android:endColor="#EFE9DC"
                android:type="linear" />
        </shape>
    </item>
    <item>
        <shape android:shape="oval">
            <gradient
                android:type="radial"
                android:centerX="0.9"
                android:centerY="0.0"
                android:gradientRadius="380dp"
                android:startColor="#33E4B04A"
                android:endColor="#00E4B04A" />
        </shape>
    </item>
</layer-list>
```

### `app/src/main/res/drawable/bg_monogram.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <gradient
        android:angle="315"
        android:startColor="#F2D28C"
        android:centerColor="#E4B04A"
        android:endColor="#C9922F"
        android:type="linear" />
    <corners android:radius="16dp" />
</shape>
```

### `app/src/main/res/layout/activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  Main screen — "Elegant Dark" layout.
  Header (monogram + wordmark) → merge card → activity-log card → floating actions.
  All view ids used by MainActivity are preserved.
-->
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?android:attr/windowBackground"
    android:fitsSystemWindows="true"
    tools:context=".main.MainActivity">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:paddingStart="20dp"
        android:paddingEnd="20dp"
        android:paddingTop="18dp"
        android:paddingBottom="12dp">

        <!-- ======================= HEADER ======================= -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginBottom="20dp">

            <TextView
                android:layout_width="52dp"
                android:layout_height="52dp"
                android:background="@drawable/bg_monogram"
                android:gravity="center"
                android:text="A"
                android:textColor="@color/ink_950"
                android:textSize="24sp"
                android:textStyle="bold"
                android:includeFontPadding="false"
                android:contentDescription="@string/app_name" />

            <LinearLayout
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="14dp"
                android:orientation="vertical">

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/app_name"
                    android:textAppearance="@style/TextAppearance.AntiSplit.Title"
                    android:maxLines="1"
                    android:ellipsize="end" />

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="2dp"
                    android:text="@string/app_subtitle"
                    android:textAppearance="@style/TextAppearance.AntiSplit.Subtitle" />
            </LinearLayout>
        </LinearLayout>

        <!-- ====================== MERGE CARD ====================== -->
        <com.google.android.material.card.MaterialCardView
            style="@style/Widget.AntiSplit.Card"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">

            <LinearLayout
                android:id="@+id/processButtons"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="20dp">

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/merge_card_title"
                    android:textSize="18sp"
                    android:textStyle="bold"
                    android:textColor="?attr/colorOnSurface" />

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp"
                    android:layout_marginBottom="16dp"
                    android:text="@string/merge_card_desc"
                    android:textAppearance="@style/TextAppearance.AntiSplit.Subtitle" />

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/decodeButton"
                    style="@style/Widget.AntiSplit.Button.Primary"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/merge_button"
                    app:icon="@android:drawable/ic_menu_add"
                    app:iconGravity="textStart"
                    app:iconPadding="8dp" />

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="6dp"
                    android:text="@string/merge_formats"
                    android:textSize="11sp"
                    android:letterSpacing="0.08"
                    android:textColor="?attr/colorOnSurfaceVariant"
                    android:gravity="center" />

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/fromAppsButton"
                    style="@style/Widget.AntiSplit.Button.Outlined"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="12dp"
                    android:text="@string/select_from_installed_apps" />

                <com.google.android.material.progressindicator.LinearProgressIndicator
                    android:id="@+id/progressIndicator"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="14dp"
                    android:indeterminate="true"
                    android:visibility="gone" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- ======================= LOG CARD ======================= -->
        <com.google.android.material.card.MaterialCardView
            style="@style/Widget.AntiSplit.Card"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/log_card_title"
                    android:textAppearance="@style/TextAppearance.AntiSplit.Section"
                    android:layout_marginBottom="10dp" />

                <View
                    android:layout_width="match_parent"
                    android:layout_height="1dp"
                    android:background="?attr/colorOutlineVariant" />

                <androidx.core.widget.NestedScrollView
                    android:id="@+id/scrollView"
                    android:layout_width="match_parent"
                    android:layout_height="0dp"
                    android:layout_weight="1"
                    android:layout_marginTop="10dp"
                    android:fillViewport="true">

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="vertical">

                        <com.google.android.material.textview.MaterialTextView
                            android:id="@+id/errorField"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:textIsSelectable="true"
                            android:textAppearance="?attr/textAppearanceBody1"
                            android:textColor="?attr/colorError"
                            android:visibility="gone" />

                        <com.google.android.material.textview.MaterialTextView
                            android:id="@+id/logField"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:text="@string/ready_hint"
                            android:textAppearance="?attr/textAppearanceBody2"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:lineSpacingExtra="3dp"
                            android:textIsSelectable="true" />
                    </LinearLayout>
                </androidx.core.widget.NestedScrollView>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>
    </LinearLayout>

    <!-- ===================== FLOATING ACTIONS ===================== -->
    <LinearLayout
        android:id="@+id/fabs"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="20dp"
        android:orientation="vertical">

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/copyButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="10dp"
            android:contentDescription="@string/copy_log"
            android:src="@drawable/copy"
            android:visibility="gone"
            app:backgroundTint="?attr/colorPrimary"
            app:tint="?attr/colorOnPrimary"
            app:elevation="2dp"
            app:borderWidth="0dp" />

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/installButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="10dp"
            android:contentDescription="@string/install"
            android:src="@android:drawable/stat_sys_download"
            android:visibility="gone"
            app:backgroundTint="?attr/colorPrimary"
            app:tint="?attr/colorOnPrimary"
            app:elevation="2dp"
            app:borderWidth="0dp" />

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/saveButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="10dp"
            android:contentDescription="@string/save"
            android:src="@android:drawable/ic_menu_save"
            android:visibility="gone"
            app:backgroundTint="?attr/colorPrimary"
            app:tint="?attr/colorOnPrimary"
            app:elevation="2dp"
            app:borderWidth="0dp" />

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/cancelButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="10dp"
            android:contentDescription="@string/cancel"
            android:src="@android:drawable/ic_menu_close_clear_cancel"
            android:visibility="gone"
            app:backgroundTint="?attr/colorPrimary"
            app:tint="?attr/colorOnPrimary"
            app:elevation="2dp"
            app:borderWidth="0dp" />

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/settingsButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:contentDescription="@string/settings"
            android:src="@drawable/settings"
            app:backgroundTint="?attr/colorPrimary"
            app:tint="?attr/colorOnPrimary"
            app:elevation="2dp"
            app:borderWidth="0dp" />
    </LinearLayout>
</FrameLayout>
```

### `app/src/main/res/layout/settings_dialog.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  Settings sheet — "Elegant Dark" layout.
  The old sun/moon/black/system theme-toggle card has been removed; appearance
  now follows the saved/system theme automatically. Settings are grouped into
  labelled cards: Merging, Output, Signing, Apps & updates.
  All view ids used by MainActivity are preserved.
-->
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="16dp"
    android:clipToPadding="false"
    android:scrollbars="none">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingStart="4dp"
        android:paddingEnd="4dp"
        android:paddingTop="4dp"
        android:paddingBottom="20dp"
        android:orientation="vertical">

        <!-- ======================= MERGING ======================= -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="6dp"
            android:layout_marginBottom="8dp"
            android:text="@string/merge_section"
            android:textAppearance="@style/TextAppearance.AntiSplit.Section" />

        <com.google.android.material.card.MaterialCardView
            style="@style/Widget.AntiSplit.Card"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="18dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:paddingStart="16dp"
                android:paddingEnd="16dp"
                android:paddingTop="6dp"
                android:paddingBottom="6dp">

                <com.google.android.material.materialswitch.MaterialSwitch
                    android:id="@+id/showDialogToggle"
                    style="@style/Widget.AntiSplit.Switch"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/show_dialog" />

                <View style="@style/Widget.AntiSplit.Divider" />

                <com.google.android.material.materialswitch.MaterialSwitch
                    android:id="@+id/selectSplitsForDeviceToggle"
                    style="@style/Widget.AntiSplit.Switch"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/automatically_select" />

                <View style="@style/Widget.AntiSplit.Divider" />

                <com.google.android.material.materialswitch.MaterialSwitch
                    android:id="@+id/forceToggle"
                    style="@style/Widget.AntiSplit.Switch"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/force" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- ======================= OUTPUT ======================= -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="6dp"
            android:layout_marginBottom="8dp"
            android:text="@string/output_section"
            android:textAppearance="@style/TextAppearance.AntiSplit.Section" />

        <com.google.android.material.card.MaterialCardView
            style="@style/Widget.AntiSplit.Card"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="18dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/dropdown_menu"
                    style="@style/Widget.Material3.TextInputLayout.OutlinedBox.ExposedDropdownMenu"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="@string/file_save_method">

                    <AutoCompleteTextView
                        android:id="@+id/auto_complete_tv"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="none"
                        android:singleLine="false"
                        android:lines="2"
                        android:maxLines="3" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/dropdown_compress_level"
                    style="@style/Widget.Material3.TextInputLayout.OutlinedBox.ExposedDropdownMenu"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="12dp"
                    android:hint="@string/select_compression_level">

                    <AutoCompleteTextView
                        android:id="@+id/auto_complete_tv2"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="none"
                        android:singleLine="false"
                        android:lines="2"
                        android:maxLines="11" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/suffixLayout"
                    style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="12dp"
                    android:hint="@string/suffix"
                    app:endIconMode="clear_text">

                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/suffixInput"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:importantForAutofill="no"
                        android:inputType="text" />
                </com.google.android.material.textfield.TextInputLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- ======================= SIGNING ======================= -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="6dp"
            android:layout_marginBottom="8dp"
            android:text="@string/signing_section"
            android:textAppearance="@style/TextAppearance.AntiSplit.Section" />

        <com.google.android.material.card.MaterialCardView
            style="@style/Widget.AntiSplit.Card"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="18dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:paddingStart="16dp"
                android:paddingEnd="16dp"
                android:paddingTop="6dp"
                android:paddingBottom="16dp">

                <com.google.android.material.materialswitch.MaterialSwitch
                    android:id="@+id/signToggle"
                    style="@style/Widget.AntiSplit.Switch"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/sign_apk" />

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/dropdown_signing_scheme"
                    style="@style/Widget.Material3.TextInputLayout.OutlinedBox.ExposedDropdownMenu"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:hint="@string/signing_scheme"
                    app:helperText="@string/signing_scheme_desc">

                    <AutoCompleteTextView
                        android:id="@+id/auto_complete_tv3"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="none"
                        android:singleLine="false"
                        android:lines="2"
                        android:maxLines="6" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textview.MaterialTextView
                    android:id="@+id/fingerprintText"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="12dp"
                    android:textSize="12sp"
                    android:fontFamily="monospace"
                    android:textColor="?attr/colorOnSurfaceVariant"
                    android:textIsSelectable="true"
                    android:text="@string/signing_cert_fingerprint" />

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/copyFingerprintButton"
                    style="@style/Widget.Material3.Button.TextButton"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="2dp"
                    android:text="@string/copy_fingerprint" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- ==================== APPS & UPDATES ==================== -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="6dp"
            android:layout_marginBottom="8dp"
            android:text="@string/apps_section"
            android:textAppearance="@style/TextAppearance.AntiSplit.Section" />

        <com.google.android.material.card.MaterialCardView
            style="@style/Widget.AntiSplit.Card"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="18dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:paddingStart="16dp"
                android:paddingEnd="16dp"
                android:paddingTop="6dp"
                android:paddingBottom="6dp">

                <com.google.android.material.materialswitch.MaterialSwitch
                    android:id="@+id/showAllAppsToggle"
                    style="@style/Widget.AntiSplit.Switch"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/show_all_apps" />

                <View style="@style/Widget.AntiSplit.Divider" />

                <com.google.android.material.materialswitch.MaterialSwitch
                    android:id="@+id/logToggle"
                    style="@style/Widget.AntiSplit.Switch"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/enable_logs" />

                <View style="@style/Widget.AntiSplit.Divider" />

                <com.google.android.material.materialswitch.MaterialSwitch
                    android:id="@+id/updateToggle"
                    style="@style/Widget.AntiSplit.Switch"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="@string/auto_update" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <com.google.android.material.button.MaterialButton
            android:id="@+id/checkUpdateNow"
            style="@style/Widget.AntiSplit.Button.Outlined"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="12dp"
            android:text="@string/check_update_now" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/langPicker"
            style="@style/Widget.AntiSplit.Button.Outlined"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/lang" />
    </LinearLayout>
</ScrollView>
```

### `app/src/main/res/layout/dropdownitem.xml`

```xml
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@android:id/text1"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="16dp"
    android:maxLines="11"
    android:ellipsize="end"
    android:textSize="16sp"
    android:textColor="?attr/colorOnSurface"/>
```

### `app/src/main/res/layout/list_item.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:paddingStart="8dp"
    android:paddingEnd="8dp"
    android:paddingTop="12dp"
    android:paddingBottom="12dp">

    <ImageView
        android:id="@+id/icon_view"
        android:layout_width="32dp"
        android:layout_height="32dp"
        android:layout_marginEnd="8dp"
        android:layout_marginRight="8dp"
        android:contentDescription="@string/app_icon_list_label" />

    <TextView
        android:id="@+id/text_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical"
        android:textColor="?attr/colorOnSurface"
        android:textSize="15sp" />

</LinearLayout>
```

### `app/src/main/java/com/abdurazaaqmohammed/utils/SignUtil.java`

```java
package com.abdurazaaqmohammed.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.abdurazaaqmohammed.AntiSplit.R;
import com.abdurazaaqmohammed.AntiSplit.main.MainActivity;
import com.aefyr.pseudoapksigner.IOUtils;
import com.aefyr.pseudoapksigner.PseudoApkSigner;
import com.android.apksig.ApkSigner;
import com.android.apksig.apk.ApkFormatException;
import com.android.apksig.apk.ApkUtils;
import com.android.apksig.apk.MinSdkVersionException;
import com.starry.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Signing helper.
 *
 * <p>Not every APK is signed with the same set of signature schemes. Depending on the
 * original packaging tool an APK may carry only v1, only v2/v3, or any combination.
 * Android accepts a v2/v3-only APK on API 24+, while older devices require v1 (JAR)
 * signing. This class therefore exposes explicit scheme constants plus an {@code AUTO}
 * mode which inspects the APK's minSdkVersion and picks a compatible scheme.</p>
 */
public class SignUtil {

    /** Pick the best scheme for the APK automatically (recommended). */
    public static final int SCHEME_AUTO = 0;
    /** v1 (JAR) + v2 + v3. Widest compatibility. */
    public static final int SCHEME_V1_V2_V3 = 1;
    /** v2 + v3 only (Android 7.0+). */
    public static final int SCHEME_V2_V3 = 2;
    /** v3 only (Android 9+). */
    public static final int SCHEME_V3 = 3;
    /** v1 + v2. */
    public static final int SCHEME_V1_V2 = 4;
    /** v1 only (legacy, Android 6 and below). */
    public static final int SCHEME_V1 = 5;

    /** Fallback minSdk used when the manifest cannot be parsed (issue #633). */
    private static final int FALLBACK_MIN_SDK = 21;

    public static void signApk(InputStream key, String password, File inputApk, File output)
            throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException,
            ApkFormatException, SignatureException, InvalidKeyException, UnrecoverableEntryException {
        signApk(key, password, inputApk, output, true, true, true);
    }

    public static void signApk(InputStream key, String password, File inputApk, File output,
                               boolean v1, boolean v2, boolean v3)
            throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException,
            ApkFormatException, SignatureException, InvalidKeyException, UnrecoverableEntryException {
        signApk(key, password, inputApk, output, v1, v2, v3, null);
    }

    /**
     * @param minSdkOverride when non-null, forces the minSdk used by apksig. This avoids
     *                       {@link MinSdkVersionException} on APKs whose manifest cannot be
     *                       parsed by apksig (issue #633).
     */
    public static void signApk(InputStream key, String password, File inputApk, File output,
                               boolean v1, boolean v2, boolean v3, Integer minSdkOverride)
            throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException,
            ApkFormatException, SignatureException, InvalidKeyException, UnrecoverableEntryException {
        char[] pw = password.toCharArray();

        KeyStore keystore = KeyStore.getInstance("BKS");
        keystore.load(key, pw);

        String alias = keystore.aliases().nextElement();

        ApkSigner.Builder builder = new ApkSigner.Builder(Collections.singletonList(
                new ApkSigner.SignerConfig.Builder("CERT",
                        ((KeyStore.PrivateKeyEntry) keystore.getEntry(alias,
                                new KeyStore.PasswordProtection(pw))).getPrivateKey(),
                        Collections.singletonList((X509Certificate) keystore.getCertificate(alias)))
                        .build()))
                .setInputApk(inputApk)
                .setOutputApk(output)
                .setCreatedBy("APK-AntiSpliter")
                .setV1SigningEnabled(v1)
                .setV2SigningEnabled(v2)
                .setV3SigningEnabled(v3);

        if (minSdkOverride != null) {
            builder.setMinSdkVersion(minSdkOverride);
        }

        builder.build().sign();
    }

    /** Backwards-compatible entry point: signs with v1+v2+v3. */
    public static void signDebugKey(Context c, File inputApk, File output)
            throws IOException, ApkFormatException, UnrecoverableEntryException, CertificateException,
            KeyStoreException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        signDebugKey(c, inputApk, output, SCHEME_AUTO);
    }

    public static void signDebugKey(Context c, File inputApk, File output, boolean v1, boolean v2,
                                    boolean v3)
            throws IOException, ApkFormatException, UnrecoverableEntryException, CertificateException,
            KeyStoreException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        signApk(c.getAssets().open("debug23.keystore"), "android", inputApk, output, v1, v2, v3);
    }

    /**
     * Signs {@code inputApk} into {@code output} using the requested scheme. If the scheme is
     * {@link #SCHEME_AUTO} the minSdkVersion is read from the APK; if that fails we fall back to
     * v1+v2+v3 with a safe minSdk so signing never hard-fails (issue #633).
     */
    public static void signDebugKey(Context c, File inputApk, File output, int scheme)
            throws IOException, ApkFormatException, UnrecoverableEntryException, CertificateException,
            KeyStoreException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Scheme resolved = resolveScheme(inputApk, scheme);
        signApk(c.getAssets().open("debug23.keystore"), "android", inputApk, output,
                resolved.v1, resolved.v2, resolved.v3, resolved.minSdkOverride);
    }

    private static final class Scheme {
        final boolean v1;
        final boolean v2;
        final boolean v3;
        final Integer minSdkOverride;

        Scheme(boolean v1, boolean v2, boolean v3, Integer minSdkOverride) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.minSdkOverride = minSdkOverride;
        }
    }

    private static Scheme resolveScheme(File apk, int scheme) {
        switch (scheme) {
            case SCHEME_V1_V2_V3:
                return new Scheme(true, true, true, null);
            case SCHEME_V2_V3:
                return new Scheme(false, true, true, null);
            case SCHEME_V3:
                return new Scheme(false, false, true, null);
            case SCHEME_V1_V2:
                return new Scheme(true, true, false, null);
            case SCHEME_V1:
                return new Scheme(true, false, false, null);
            case SCHEME_AUTO:
            default:
                try {
                    int minSdk = detectMinSdk(apk);
                    // v2/v3 are only verified on API 24+. Below that we must include v1.
                    return minSdk >= 24
                            ? new Scheme(false, true, true, null)
                            : new Scheme(true, true, true, null);
                } catch (Exception e) {
                    // Manifest unreadable/codename minSdk: use the widest scheme and a safe
                    // minSdk so apksig does not throw (issue #633).
                    return new Scheme(true, true, true, FALLBACK_MIN_SDK);
                }
        }
    }

    private static int detectMinSdk(File apk) throws IOException, MinSdkVersionException {
        try (ZipFile zipFile = new ZipFile(apk)) {
            ZipEntry entry = zipFile.getEntry("AndroidManifest.xml");
            if (entry == null) {
                throw new MinSdkVersionException("AndroidManifest.xml not found in " + apk.getName());
            }
            try (InputStream is = zipFile.getInputStream(entry)) {
                return ApkUtils.getMinSdkVersionFromBinaryAndroidManifest(
                        ByteBuffer.wrap(readFully(is)));
            }
        }
    }

    private static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    /**
     * Returns the SHA-256 fingerprint of the bundled signing certificate, formatted as
     * colon-separated uppercase hex (issue #673). Returns {@code null} if it cannot be read.
     */
    public static String getSigningCertificateSha256(Context context) {
        try (InputStream key = context.getAssets().open("debug23.keystore")) {
            char[] pw = "android".toCharArray();
            KeyStore keystore = KeyStore.getInstance("BKS");
            keystore.load(key, pw);
            String alias = keystore.aliases().nextElement();
            X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(cert.getEncoded());
            StringBuilder sb = new StringBuilder(digest.length * 3);
            for (int i = 0; i < digest.length; i++) {
                if (i > 0) sb.append(':');
                sb.append(String.format("%02X", digest[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static void signPseudoApkSigner(File temp, MainActivity context, Uri out, Exception e) throws Exception {
        String msg = context.getRss().getString(R.string.sign_failed);
        if(Build.VERSION.SDK_INT < 30) {
            // When I tried signing with apksig in AVD with sdk 10 java.security is throwing some error saying something not found
            // Apparently 11 is the last version that supports v1 signing alone.
            try (InputStream is = com.abdurazaaqmohammed.utils.FileUtils.getInputStream(temp)) {
                final String FILE_NAME_PAST = "testkey.past";
                final String FILE_NAME_PRIVATE_KEY = "testkey.pk8";
                File signingEnvironment = new File(context.getFilesDir(), "signing");
                File pastFile = new File(signingEnvironment, FILE_NAME_PAST);
                File privateKeyFile = new File(signingEnvironment, FILE_NAME_PRIVATE_KEY);

                if (!pastFile.exists() || !privateKeyFile.exists()) {
                    signingEnvironment.mkdir();
                    IOUtils.copyFileFromAssets(context, FILE_NAME_PAST, pastFile);
                    IOUtils.copyFileFromAssets(context, FILE_NAME_PRIVATE_KEY, privateKeyFile);
                }

                try(OutputStream os = FileUtils.getOutputStream(out, context)) {
                    PseudoApkSigner.sign(is, os, pastFile, privateKeyFile);
                }
            } catch (Exception e2) {
                context.getLogger().logMessage(msg);
                try(OutputStream os = FileUtils.getOutputStream(out, context)) {
                    com.abdurazaaqmohammed.utils.FileUtils.copyFile(temp, os);
                }
                StringBuilder errs = new StringBuilder(e2.toString());
                for(StackTraceElement stackTraceElement : e2.getStackTrace()) errs.append('\n').append(stackTraceElement);
                for(StackTraceElement stackTraceElement : e.getStackTrace()) errs.append('\n').append(stackTraceElement);
                throw(new RuntimeException(errs.toString())); // for showError
            }
        } else {
            context.getLogger().logMessage(msg);
            try(OutputStream os = FileUtils.getOutputStream(out, context)) {
                com.abdurazaaqmohammed.utils.FileUtils.copyFile(temp, os);
            }
            throw((e)); // for showError
        }
    }
}
```

### `app/src/main/java/com/reandroid/Merger.java`

```java
/*
 *  Copyright (C) 2022 github.com/REAndroid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reandroid;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.abdurazaaqmohammed.AntiSplit.R;
import com.abdurazaaqmohammed.AntiSplit.main.MyAPKLogger;
import com.abdurazaaqmohammed.utils.DeviceSpecsUtil;
import com.abdurazaaqmohammed.AntiSplit.main.MainActivity;
import com.abdurazaaqmohammed.utils.SignUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.reandroid.apk.ApkBundle;
import com.reandroid.apk.ApkModule;
import com.reandroid.apkeditor.common.AndroidManifestHelper;
import com.reandroid.app.AndroidManifest;
import com.reandroid.archive.ArchiveFile;
import com.reandroid.archive.InputSource;
import com.reandroid.archive.ZipEntryMap;
import com.reandroid.arsc.chunk.TableBlock;
import com.reandroid.arsc.chunk.xml.AndroidManifestBlock;
import com.reandroid.arsc.chunk.xml.ResXmlAttribute;
import com.reandroid.arsc.chunk.xml.ResXmlElement;
import com.reandroid.arsc.container.SpecTypePair;
import com.reandroid.arsc.model.ResourceEntry;
import com.reandroid.arsc.value.Entry;
import com.reandroid.arsc.value.ResValue;
import com.reandroid.arsc.value.ValueType;
import com.starry.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Merger {

    private File workingDirectory;
    private final MainActivity context;
    private final MyAPKLogger logger;
    private final Resources rss;
    public Uri signedApk;
    private final int compressionLevel;

    public Merger(File workingDirectory, MainActivity context, int compressionLevel) {
        this.workingDirectory = workingDirectory;
        this.rss = (this.context = context).getRss();
        this.logger = context.getLogger();
        this.compressionLevel = compressionLevel;
    }

    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    private void extractAndLoadFromZipFile(Uri splitAPKUri,  List<String> splitsToNotInclude, ApkBundle bundle, boolean checkSplits) throws IOException {
        // Check if already copied the file earlier to get list of splitsToNotInclude.
        long size;
        boolean notAlreadyCopied = DeviceSpecsUtil.zipFile == null;
        if (notAlreadyCopied) {
            boolean cantReadFile = com.abdurazaaqmohammed.utils.FileUtils.doesNotHaveStoragePerm(context);
            File inputZipFile = null;
            String path;

            if(!cantReadFile) try {
                path = FileUtils.getPath(splitAPKUri, context);
                cantReadFile = TextUtils.isEmpty(path) || !((inputZipFile= new File(path)).canRead());
            } catch (Exception exception) {
                cantReadFile = true;
            }

            if (cantReadFile) {
                try(InputStream is = context.getContentResolver().openInputStream(splitAPKUri)) {
                    //File parentFile = cacheDir.getParentFile();
                    // com.abdurazaaqmohammed.utils.FileUtils.copyFile(is, input = new File(parentFile != null && parentFile.canRead() ? parentFile : cacheDir, input == null ? context.getOriginalFileName(in) :  input.getName()));
                    inputZipFile = new File(workingDirectory, System.currentTimeMillis() + '_' + context.getOriginalFileName(splitAPKUri));
                    com.abdurazaaqmohammed.utils.FileUtils.copyFile(is, inputZipFile);
                }
            }
            size = inputZipFile.length();
            try {
                ArchiveFile zf = new ArchiveFile(inputZipFile);
                extractZipFile(zf, checkSplits, splitsToNotInclude);
            } catch (Exception archiveError) {
                // Some APKS/APKM files use ZIP structures that reAndroid's ArchiveFile
                // rejects ("Failed to find end record"). Retry with the JDK ZIP reader (#20).
                logger.logMessage("ArchiveFile failed (" + archiveError.getMessage() + "), retrying with ZipFile");
                extractZipFileFallback(inputZipFile, checkSplits, splitsToNotInclude);
            }
            if (cantReadFile) inputZipFile.delete();
        } else {
            extractZipFile(DeviceSpecsUtil.zipFile, checkSplits, splitsToNotInclude);
            size = DeviceSpecsUtil.zipFile.size();
        }
        try {
            bundle.loadApkDirectory(workingDirectory);
        } catch (FileNotFoundException fileNotFoundException) {
            String path;
            try {
                path = FileUtils.getPath(splitAPKUri, context);
            } catch (Exception exception) {
                path = context.getOriginalFileName(splitAPKUri);
            }
            throw(new IOException(fileNotFoundException.getMessage() + " file " + splitAPKUri + ' ' + path + ' ' + (notAlreadyCopied ? "size" : "length") +' ' + size, fileNotFoundException));
        }
    }

    private void extractZipFile(ArchiveFile zf, boolean checkSplits, List<String> splitsToNotInclude) throws IOException {
        for(InputSource archiveEntry : zf.getInputSources()) {
            String name = archiveEntry.getName();
            if (name.endsWith(".apk")) {
                if ((checkSplits && splitsToNotInclude.contains(name)))
                    logger.logMessage(rss.getString(R.string.skipping) + name + rss.getString(R.string.unselected));
                else try (InputStream is = archiveEntry.openStream()) {
                   // com.abdurazaaqmohammed.utils.FileUtils.copyFile(is, new File(cacheDir, name));
                    File outputFile = new File(workingDirectory, name);
                    if (outputFile.getCanonicalPath().startsWith(workingDirectory.getCanonicalPath() + File.separator)) {
                        File parentDir = outputFile.getParentFile();
                        if (parentDir != null && !parentDir.exists()) {
                            parentDir.mkdirs();
                        }
                        com.abdurazaaqmohammed.utils.FileUtils.copyFile(is, outputFile);
                    } else {
                        logger.logMessage("Skipped invalid path: " + name);
                    }
                }
            } else logger.logMessage(rss.getString(R.string.skipping) + name + rss.getString(R.string.not_apk));
        }
        zf.close();
    }

    private void extractZipFileFallback(File zipFile, boolean checkSplits, List<String> splitsToNotInclude) throws IOException {
        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(zipFile)) {
            java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                java.util.zip.ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.endsWith(".apk")) {
                    logger.logMessage(rss.getString(R.string.skipping) + name + rss.getString(R.string.not_apk));
                    continue;
                }
                if (checkSplits && splitsToNotInclude.contains(name)) {
                    logger.logMessage(rss.getString(R.string.skipping) + name + rss.getString(R.string.unselected));
                    continue;
                }
                File outputFile = new File(workingDirectory, name);
                if (!outputFile.getCanonicalPath().startsWith(workingDirectory.getCanonicalPath() + File.separator)) {
                    logger.logMessage("Skipped invalid path: " + name);
                    continue;
                }
                File parentDir = outputFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();
                try (InputStream is = zf.getInputStream(entry)) {
                    com.abdurazaaqmohammed.utils.FileUtils.copyFile(is, outputFile);
                }
            }
        }
    }

    public File run(ApkBundle bundle, boolean signApk, boolean force) throws Exception {
        return run(bundle, signApk, force, SignUtil.SCHEME_AUTO);
    }

    public File run(ApkBundle bundle, boolean signApk, boolean force, int signingScheme) throws Exception {
        MyAPKLogger logger = context.getLogger();
        logger.logMessage("Found modules: " + bundle.getApkModuleList().size());
        //final boolean[] saveToCacheDir = {true}; // I found writeApk(OutputStream) is really slow and writing to file and copying is actually faster
        final boolean[] sign = {signApk};
        File[] splits = workingDirectory.listFiles();
        for(File split : splits) {
            String splitName = split.getName();
            String arch = null;
            String var = "x86";
            if(splitName.contains(var)) arch = var;
            else if(splitName.contains(var = "x86_64") || splitName.contains("x86-64") || splitName.contains("x64")) arch = var;
            else if(splitName.contains("arm64")) arch = "arm64-v8a";
            else if(splitName.contains("v7a") || splitName.contains("arm7")) arch = "armeabi-v7a";
            if(arch != null) try (ApkModule zf = ApkModule.loadApkFile(split, splitName)) {
                if (zf.containsFile("lib" + File.separator + arch + File.separator + "libpairipcore.so")) {
                    final CountDownLatch latch = new CountDownLatch(1);
                    context.getHandler().post(() ->
                        context.runOnUiThread(new MaterialAlertDialogBuilder(context).setTitle(rss.getString(R.string.warning)).setMessage(R.string.pairip_warning)
                        .setPositiveButton("OK", (dialog, which) -> {
                            //saveToCacheDir[0] = true;
                            sign[0] = false;
                            latch.countDown();
                        }).setNegativeButton(rss.getString(R.string.cancel), (dialog, which) -> {
                            context.startActivity(new Intent(context, MainActivity.class));
                            context.finishAffinity();
                            latch.countDown();
                        }).create()::show));
                    latch.await();
                    break;
                }
            }
        }
        try (ApkModule mergedModule = bundle.mergeModules(!force)) { // I guess force meant force throw ..
            if (mergedModule.hasAndroidManifest()) {
                AndroidManifestBlock manifest = mergedModule.getAndroidManifest();
                logger.logMessage((R.string.sanitizing_manifest));

                AndroidManifestHelper.removeAttributeFromManifestById(manifest,
                        AndroidManifest.ID_requiredSplitTypes, logger);
                AndroidManifestHelper.removeAttributeFromManifestById(manifest,
                        AndroidManifest.ID_splitTypes, logger);
                AndroidManifestHelper.removeAttributeFromManifestByName(manifest,
                        AndroidManifest.NAME_splitTypes, logger);

                AndroidManifestHelper.removeAttributeFromManifestByName(manifest,
                        AndroidManifest.NAME_requiredSplitTypes, logger);
                AndroidManifestHelper.removeAttributeFromManifestByName(manifest,
                        AndroidManifest.NAME_splitTypes, logger);
                // Do NOT delete android:extractNativeLibs. When the attribute is absent,
                // Android 14+ assumes native libraries are stored uncompressed and
                // 16 KB page aligned; if the merged APK does not satisfy that, the
                // package is rejected (INSTALL_FAILED_INVALID_APK) or crashes at
                // startup (#612, #648). Forcing it to "true" tells the platform to
                // extract the libraries at install time, which always works.
                AndroidManifestHelper.removeAttributeFromManifestById(manifest,
                        AndroidManifest.ID_extractNativeLibs, logger);
                mergedModule.setExtractNativeLibs(Boolean.TRUE);
                AndroidManifestHelper.removeAttributeFromManifestAndApplication(manifest,
                        AndroidManifest.ID_isSplitRequired, logger,AndroidManifest.NAME_isSplitRequired
                );

                ResXmlElement application = manifest.getApplicationElement();
                List<ResXmlElement> splitMetaDataElements =
                        AndroidManifestHelper.listSplitRequired(application);
                boolean splits_removed = false;
                for (ResXmlElement meta : splitMetaDataElements) {
                    if (!splits_removed) {
                        boolean result = false;
                        ResXmlAttribute nameAttribute = meta.searchAttributeByResourceId(AndroidManifest.ID_name);
                        if (nameAttribute != null) {
                            if ("com.android.vending.splits".equals(nameAttribute.getValueAsString())) {
                                ResXmlAttribute valueAttribute = meta.searchAttributeByResourceId(
                                        AndroidManifest.ID_value);
                                if (valueAttribute == null) {
                                    valueAttribute = meta.searchAttributeByResourceId(
                                            AndroidManifest.ID_resource);
                                }
                                if (valueAttribute != null
                                        && valueAttribute.getValueType() == ValueType.REFERENCE) {
                                    if (mergedModule.hasTableBlock()) {
                                        TableBlock tableBlock = mergedModule.getTableBlock();
                                        ResourceEntry resourceEntry = tableBlock.getResource(valueAttribute.getData());
                                        if (resourceEntry != null) {
                                            ZipEntryMap zipEntryMap = mergedModule.getZipEntryMap();
                                            // Collect first, mutate later: modifying the resource
                                            // entry list while iterating it can throw
                                            // ConcurrentModificationException on large tables.
                                            java.util.List<String> pathsToRemove = new java.util.ArrayList<>();
                                            java.util.List<Entry> entriesToNull = new java.util.ArrayList<>();
                                            for (Entry entry : resourceEntry) {
                                                if (entry == null) continue;
                                                ResValue resValue = entry.getResValue();
                                                if (resValue == null) continue;
                                                String path = resValue.getValueAsString();
                                                logger.logMessage((R.string.removed_table_entry) + " " + path);
                                                // The split list is always an XML resource. Guard the
                                                // deletion so a mis-resolved reference can never wipe
                                                // drawables/icons from the merged APK (#665).
                                                if (path != null && (path.startsWith("res/xml/") || path.endsWith(".xml"))) {
                                                    pathsToRemove.add(path);
                                                } else if (path != null) {
                                                    logger.logMessage("Kept non-XML split metadata resource: " + path);
                                                }
                                                entriesToNull.add(entry);
                                            }
                                            for (String path : pathsToRemove) zipEntryMap.remove(path);
                                            for (Entry entry : entriesToNull) {
                                                // It's not safe to destroy the entry, the resource id
                                                // might be used in dex code. Replace it with null.
                                                entry.setNull(true);
                                                SpecTypePair specTypePair = entry.getTypeBlock()
                                                        .getParentSpecTypePair();
                                                specTypePair.removeNullEntries(entry.getId());
                                            }
                                            result = true;
                                        }
                                    }
                                }
                            }
                        }
                        splits_removed = result;
                    }
                    logger. logMessage("Removed-element : <" + meta.getName() + "> name=\"" + AndroidManifestBlock.getAndroidNameValue(meta) + "\"");
                    application.remove(meta);
                }
                manifest.refresh();
            }
            logger.logMessage((R.string.saving));

            File mergedAPK = new File(workingDirectory, "merged_" + System.currentTimeMillis() + ".apk");
            mergedModule.writeApk(mergedAPK);
            if (sign[0]) {
                logger.logMessage((R.string.signing));
                File outputSigned = new File(workingDirectory, System.currentTimeMillis() + "signed.apk");
               // try {
                SignUtil.signDebugKey(context, mergedAPK, outputSigned, signingScheme);
                signedApk = FileProvider.getUriForFile(context, "com.abdurazaaqmohammed.AntiSplit.fileprovider", outputSigned);
                return outputSigned;
              //  } catch (Exception e) {
                //    SignUtil.signPseudoApkSigner(mergedAPK, context, out, e); // Just forget about this because I never figure out how to solve any of the error from it and it probably does not help
              //  }
            } else {
                return mergedAPK;
            }
        }
    }


    public File run(Uri splitAPKUri, List<String> splitsToNotInclude, boolean signApk, boolean force) throws Exception {
        return run(splitAPKUri, splitsToNotInclude, signApk, force, SignUtil.SCHEME_AUTO);
    }

    public File run(Uri splitAPKUri, List<String> splitsToNotInclude, boolean signApk, boolean force, int signingScheme) throws Exception {
        MyAPKLogger logger = context.getLogger();
        logger.logMessage((R.string.searching));
        try (ApkBundle bundle = new ApkBundle(compressionLevel)) {
            if (splitAPKUri == null) {
                // Multiple splits from a split apk, already copied to cache dir
                try {
                    bundle.loadApkDirectory(workingDirectory);
                } catch (FileNotFoundException fileNotFoundException) {
                    if(splitsToNotInclude != null) throw(new IOException(fileNotFoundException.getMessage() + " file " + splitsToNotInclude, fileNotFoundException));
                    else throw fileNotFoundException;
                }
            }
            else {
                logger.logMessage("MIME Type " + context.getContentResolver().getType(splitAPKUri));
                boolean notAlreadyCopied = DeviceSpecsUtil.zipFile == null;
                boolean checkSplits = splitsToNotInclude != null && !splitsToNotInclude.isEmpty();
                if (notAlreadyCopied) {
                    //if (Build.VERSION.SDK_INT < 23) {
                        logger.logMessage(splitAPKUri.getPath());

                        extractAndLoadFromZipFile(splitAPKUri, splitsToNotInclude, bundle, checkSplits);
                   // } else extractAndLoadFromInputStream(splitAPKUri, splitsToNotInclude, bundle);
                } else {
                    extractZipFile(DeviceSpecsUtil.zipFile, checkSplits, splitsToNotInclude);
                    try {
                        bundle.loadApkDirectory(workingDirectory);
                    } catch (FileNotFoundException fileNotFoundException) {
                        String path;
                        try {
                            path = FileUtils.getPath(splitAPKUri, context);
                        } catch (Exception exception) {
                            path = context.getOriginalFileName(splitAPKUri);
                        }
                        throw(new IOException(fileNotFoundException.getMessage() + " uri " + splitAPKUri + " file " + path, fileNotFoundException));
                    }
                }
            }
            return run(bundle, signApk, force, signingScheme);
        }
    }
}
```

### `app/src/main/java/com/abdurazaaqmohammed/AntiSplit/main/MainActivity.java`

```java
package com.abdurazaaqmohammed.AntiSplit.main;

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static com.abdurazaaqmohammed.utils.LegacyUtils.aboveSdk20;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;

import com.abdurazaaqmohammed.AntiSplit.R;
import com.abdurazaaqmohammed.utils.CompareUtils;
import com.abdurazaaqmohammed.utils.DeviceSpecsUtil;
import com.abdurazaaqmohammed.utils.InstallUtil;
import com.abdurazaaqmohammed.utils.LanguageUtil;
import com.abdurazaaqmohammed.utils.LegacyUtils;
import com.abdurazaaqmohammed.utils.RunUtil;
import com.abdurazaaqmohammed.utils.SignUtil;
import com.abdurazaaqmohammed.utils.UpdateUtil;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.reandroid.Merger;
import com.reandroid.apk.ApkBundle;
import com.reandroid.utils.io.FileUtil;
import com.starry.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.Deflater;

import com.github.paul035.LocaleHelper;

/** @noinspection deprecation */
public class MainActivity extends AppCompatActivity {
    private int compressionLevel;
    private int signingScheme = SignUtil.SCHEME_AUTO;
    private int saveMode = 0;
    private String outputFolder;
    private boolean showDialog;
    private boolean signApk;
    private boolean selectSplitsForDevice;
    private Uri splitAPKUri;
    private ArrayList<Uri> uris;
    private boolean urisAreSplitApks = true;
    private boolean criticalErrorOccurred;
    private boolean checkForUpdates;
    public String lastVerChecked;
    boolean logEnabled;
    private boolean force;
    private String lang;
    private int theme;
    private int sortMode;
    private DeviceSpecsUtil deviceSpecsUtil;
    private String pkgName;
    private String suffix;
    private boolean systemTheme;
    private boolean showAllApps;
    private boolean isProcessing;

    private Resources rss;

    public Resources getRss() {
        return rss;
    }

    private Handler handler;

    public Handler getHandler() {
        return handler;
    }

    private MyAPKLogger logger;

    public MyAPKLogger getLogger() {
        return logger;
    }

    private String openedFile;
    private List<String> splitsUnselectedInDialog;

    /** @noinspection AssignmentUsedAsCondition */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        handler = new Handler(Looper.getMainLooper());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        SharedPreferences settings = getSharedPreferences("set", Context.MODE_PRIVATE);

        String deviceLang = Locale.getDefault().getLanguage();
        boolean zh = deviceLang.equals("zh");
        boolean supportedLang = zh || deviceLang.equals("ar") || deviceLang.equals("es")
                || deviceLang.equals("fr") || deviceLang.equals("in") || deviceLang.equals("it")
                || deviceLang.equals("pt-BR") || deviceLang.equals("ru") || deviceLang.equals("tr")
                || deviceLang.equals("uk") || deviceLang.equals("vi") || deviceLang.equals("hu")
                || deviceLang.equals("pl") || deviceLang.equals("sk") || deviceLang.equals("ko")
                || deviceLang.equals("uz") || deviceLang.equals("ja") || deviceLang.equals("de")
                || deviceLang.equals("fa");
        if(zh) {
            deviceLang = Locale.getDefault().getCountry().equals("TW") ? "zh-TW" : "zh-rCN";
        }
        lang = settings.getString("lang", supportedLang ? deviceLang : "en");
        boolean useDeviceRss = lang.equals(deviceLang);
        rss = useDeviceRss ? getResources() : LocaleHelper.setLocale(this, lang).getResources();

        boolean dark = (rss.getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        setTheme(theme = settings.getInt("theme", dark
                ? R.style.Theme_MyApp_Dark
                : R.style.Theme_MyApp_Light));

        setContentView(R.layout.activity_main);

        if (!useDeviceRss)
            LanguageUtil.updateMain(rss, this);

        if (theme == R.style.Theme_MyApp_Black)
            findViewById(R.id.main).setBackgroundColor(Color.BLACK);
        deviceSpecsUtil = new DeviceSpecsUtil(this);

        TextView logField = findViewById(R.id.logField);
        NestedScrollView scrollView = findViewById(R.id.scrollView);
        logger = new MyAPKLogger(this, logField, scrollView);

        // Restore the processing UI after a configuration change / recreation (#620).
        if (savedInstanceState != null) {
            String restoredLog = savedInstanceState.getString("log");
            if (!TextUtils.isEmpty(restoredLog)) logField.setText(restoredLog);
            String restoredError = savedInstanceState.getString("error");
            if (!TextUtils.isEmpty(restoredError)) {
                TextView errorField = findViewById(R.id.errorField);
                errorField.setText(restoredError);
                errorField.setVisibility(View.VISIBLE);
            }
            isProcessing = savedInstanceState.getBoolean("processing", false);
            if (isProcessing) {
                findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);
                findViewById(R.id.cancelButton).setVisibility(View.VISIBLE);
                findViewById(R.id.installButton).setVisibility(View.GONE);
            }
        }

        // Never wipe the cache while an activity recreation may be in progress; doing so
        // deletes the working directory of an ongoing merge (#620).
        if (savedInstanceState == null)
            RunUtil.runInBackground(() -> FileUtil.deleteDirectory(getCacheDir()));

        if (aboveSdk20) {
            getWindow().addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int transparent = Color.TRANSPARENT;
            getWindow().setNavigationBarColor(transparent);
            getWindow().setStatusBarColor(transparent);
        }

        if (!LegacyUtils.supportsWriteExternalStorage) {
            EdgeToEdge.enable(this);
            getWindow().setStatusBarContrastEnforced(true);
            getWindow().setNavigationBarContrastEnforced(true);
        }

        // Fetch settings from SharedPreferences
        lastVerChecked = getSharedPreferences("set", Context.MODE_PRIVATE).getString("lastVerChecked", null);
        if ((checkForUpdates = settings.getBoolean("checkForUpdates", true)))
            UpdateUtil.checkForUpdates(false, this);
        signApk = settings.getBoolean("signApk", true);
        force = settings.getBoolean("force", false);
        showDialog = settings.getBoolean("showDialog", false);
        selectSplitsForDevice = settings.getBoolean("selectSplitsForDevice", false);
        logEnabled = settings.getBoolean("logEnabled", true);
        saveMode = settings.getInt("saveMode", 0);
        systemTheme = settings.getBoolean("systemTheme", true);
        showAllApps = settings.getBoolean("showAllApps", false);
        sortMode = settings.getInt("sortMode", 0);
        compressionLevel = settings.getInt("compressionLevel", Deflater.DEFAULT_COMPRESSION);
        signingScheme = settings.getInt("signingScheme", SignUtil.SCHEME_AUTO);
        suffix = settings.getString("suffix", "_antisplit");
        outputFolder = settings.getString("outputFolder",
                com.abdurazaaqmohammed.utils.FileUtils.getAntisplitMFolder().getPath());

        View selectFromInstalledApps = findViewById(R.id.fromAppsButton);
        if (aboveSdk20)
            selectFromInstalledApps.setOnClickListener(this::installedAppsClickListener);
        else
            selectFromInstalledApps.setVisibility(View.GONE);

        findViewById(R.id.settingsButton).setOnClickListener(this::settingsButtonListener);

        findViewById(R.id.decodeButton).setOnClickListener(v -> {
            ((TextView) findViewById(R.id.logField)).setText("");
            ((TextView) findViewById(R.id.errorField)).setText("");
            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("*/*")
                    .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    .putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                            "application/zip",
                            "application/x-zip-compressed",
                            "application/vnd.android.package-archive",
                            "application/vnd.apkm",
                            "application/octet-stream"})
                    , 1);
        });
        cleanupAppFolder();

        // Check if user shared or opened file with the app.
        final Intent openIntent = getIntent();
        final String action = openIntent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            splitAPKUri = openIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            splitAPKUri = openIntent.getData();
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && openIntent.hasExtra(Intent.EXTRA_STREAM)) {
            uris = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    ? openIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri.class)
                    : openIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (uris != null && !uris.isEmpty()) {
                final Uri uri = uris.get(0);
                // Resolve the real display name: content Uris have no usable path (#609).
                if (urisAreSplitApks = !isApkFileName(getRawFileName(uri)))
                    splitAPKUri = uri;
                else
                    process();
            }
        }
        if (splitAPKUri != null) {
            // A single plain .apk cannot be "anti-split"; guide the user instead of
            // failing deep inside the ZIP reader (#624).
            if (isApkFileName(getRawFileName(splitAPKUri))) {
                showError(rss.getString(R.string.not_split, splitAPKUri.toString()));
            } else if (showDialog)
                showApkSelectionDialog();
            else
                process();
        }
    }

    public void styleAlertDialog(AlertDialog ad) {
        styleAlertDialog(ad, true);
    }

    public void styleAlertDialog(AlertDialog ad, boolean fixedHeight) {
        Window w = ad.getWindow();
        if (w != null) {
            GradientDrawable border = new GradientDrawable();
            boolean light = theme == R.style.Theme_MyApp_Light;
            border.setColor(light ? Color.WHITE : Color.BLACK); // Background color

            // Border width and color
            border.setStroke(5, rss.getColor(R.color.main_500));

            border.setCornerRadius(24);
            w.setBackgroundDrawable(border);
            if (fixedHeight) {
                double m = 0.8;
                DisplayMetrics displayMetrics = rss.getDisplayMetrics();

                int height = (int) (displayMetrics.heightPixels * m);
                int width = (int) (displayMetrics.widthPixels * m);
                w.setLayout(width, height);
            }
        }
        runOnUiThread(ad::show);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Re-apply the transient processing state so the progress bar does not vanish (#620).
        if (isProcessing) {
            View progress = findViewById(R.id.progressIndicator);
            if (progress != null) progress.setVisibility(View.VISIBLE);
            View cancel = findViewById(R.id.cancelButton);
            if (cancel != null) cancel.setVisibility(View.VISIBLE);
        }
        if (systemTheme) {
            int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                if(theme != R.style.Theme_MyApp_Dark) {
                    setTheme(theme = R.style.Theme_MyApp_Dark);
                    recreate();
                }
            } else if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                if(theme != R.style.Theme_MyApp_Light) {
                    setTheme(theme = R.style.Theme_MyApp_Light);
                    recreate();
                }
            }
        }
    }

    public void checkStoragePerm(int requestCode) {
        if (com.abdurazaaqmohammed.utils.FileUtils.doesNotHaveStoragePerm(this)) {
            getHandler()
                    .post(() -> Toast.makeText(this, rss.getString(R.string.grant_storage), Toast.LENGTH_LONG).show());
            if (LegacyUtils.supportsWriteExternalStorage)
                requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, requestCode);
            else
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + getPackageName())), requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            checkUpdateAfterStoragePermission.run();
        else if (requestCode == 9 && com.abdurazaaqmohammed.utils.FileUtils.doesNotHaveStoragePerm(this))
            saveMode = 0;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(onDownloadComplete);
        getSharedPreferences("set", Context.MODE_PRIVATE).edit()
                .putBoolean("logEnabled", logEnabled)
                .putInt("saveMode", saveMode)
                .putBoolean("showDialog", showDialog)
                .putBoolean("signApk", signApk)
                .putBoolean("force", force)
                .putBoolean("systemTheme", systemTheme)
                .putBoolean("showAllApps", showAllApps)
                .putBoolean("selectSplitsForDevice", selectSplitsForDevice)
                .putInt("theme", theme)
                .putInt("sortMode", sortMode)
                .putInt("compressionLevel", compressionLevel)
                .putInt("signingScheme", signingScheme)
                .putBoolean("checkForUpdates", checkForUpdates)
                .putString("lang", lang)
                .putString("lastVerChecked", lastVerChecked)
                .putString("suffix", suffix)
                .putString("outputFolder", outputFolder)
                .apply();
        super.onPause();
    }

    /** @noinspection ResultOfMethodCallIgnored */
    private void cleanupAppFolder() {
        if (com.abdurazaaqmohammed.utils.FileUtils.doesNotHaveStoragePerm(this))
            return;
        File appFolder = new File(Environment.getExternalStorageDirectory(), "AntiSplit-M");
        if (!appFolder.exists())
            return;
        File[] children = appFolder.listFiles();
        if (children == null)
            return;
        if (children.length == 0)
            appFolder.delete();
        else {
            for (File child : children)
                if (child.isFile() && child.length() == 0)
                    child.delete();
            File[] remainingChildren = appFolder.listFiles();
            if (remainingChildren == null || remainingChildren.length == 0)
                appFolder.delete();
        }
    }

    @Override
    protected void onDestroy() {
        // Only clean up when the activity is really going away, not on rotation/recreate.
        if (isFinishing() && !isChangingConfigurations()) {
            FileUtil.deleteDirectory(getCacheDir());
            cleanupAppFolder();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("processing", isProcessing);
        TextView logField = findViewById(R.id.logField);
        if (logField != null) outState.putString("log", logField.getText().toString());
        TextView errorField = findViewById(R.id.errorField);
        if (errorField != null) outState.putString("error", errorField.getText().toString());
    }

    File merged;

    private void process() {
        findViewById(R.id.installButton).setVisibility(View.GONE);
        toggleAnimation(true);

        MainActivity context = MainActivity.this;

        final File cacheDir = getCacheDir();
        boolean selectedFromInstalledApps = !TextUtils.isEmpty(pkgName);
        if (selectedFromInstalledApps)
            urisAreSplitApks = false;

        Merger merger = new Merger(cacheDir, this, compressionLevel);

        new RunUtil(handler, context, null).runInBackground(() -> {
            criticalErrorOccurred = false; // reset to make sure success message shows
            try {
                if (selectedFromInstalledApps) {
                    try (ApkBundle bundle = new ApkBundle(compressionLevel)) {
                        // Selected from apps list
                        PackageManager packageManager = context.getPackageManager();
                        bundle.loadApkDirectory(
                                new File(packageManager.getPackageInfo(pkgName, 0).applicationInfo.sourceDir)
                                        .getParentFile());
                        merged = merger.run(bundle, signApk, force, signingScheme);
                        selectDirToSaveAPKOrSaveNow(0);
                    }
                } else {
                    // selected from anything except app list
                    File workingFolder = new File(cacheDir, UUID.randomUUID().toString());
                    if (!workingFolder.mkdir())
                        workingFolder = cacheDir;
                    merger.setWorkingDirectory(workingFolder);

                    if (urisAreSplitApks) {
                        List<String> splitsToNotInclude = selectSplitsForDevice
                                ? deviceSpecsUtil.getSplitsForDevice(splitAPKUri)
                                : splitsUnselectedInDialog;
                        merged = merger.run(splitAPKUri, splitsToNotInclude, signApk, force, signingScheme);
                        selectDirToSaveAPKOrSaveNow();
                    } else
                        try (ApkBundle bundle = new ApkBundle(compressionLevel)) {
                            ArrayList<Uri> uriArrayList = uris;
                            for (int i = 0; i < uriArrayList.size(); i++) {
                                Uri uri = uriArrayList.get(i);
                                try (InputStream is = FileUtils.getInputStream(uri, context)) {
                                    String fileName = getOriginalFileName(uri);
                                    if (TextUtils.isEmpty(fileName))
                                        fileName = "split_" + i + "_.apk";
                                    com.abdurazaaqmohammed.utils.FileUtils.copyFile(is,
                                            new File(workingFolder, fileName));
                                }
                            }

                            bundle.loadApkDirectory(workingFolder);
                            merged = merger.run(bundle, signApk, force, signingScheme);
                            selectDirToSaveAPKOrSaveNow();
                        }
                }
                toggleAnimation(false);
                return true;
            } catch (Exception e) {
                showError(e);
                return false;
            }
        }, () -> {
            pkgName = null; // reset
            if (urisAreSplitApks)
                getHandler().post(() -> {
                    try {
                        uris.remove(0);
                        splitAPKUri = uris.get(0);
                        if (showDialog)
                            showApkSelectionDialog();
                        else
                            process();
                    } catch (IndexOutOfBoundsException | NullPointerException ignored) {
                        // End of list, I don't know why but isEmpty is not working
                        setUIAfterMerging(merger);
                    }
                });
            else
                setUIAfterMerging(merger); // multiple splits from inside the APK, can only be one go
        }, true);

        findViewById(R.id.fabs).setAlpha(0.5f);
        View cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setVisibility(View.VISIBLE);
        cancelButton.setOnClickListener(v -> {
            try {
                startActivity(Intent.makeRestartActivityTask(
                        Objects.requireNonNull(getPackageManager().getLaunchIntentForPackage(getPackageName()))
                                .getComponent()));
                Runtime.getRuntime().exit(0);
            } catch (Exception e) {
                recreate();
            }
        });

        View copyButton = findViewById(R.id.copyButton);
        copyButton.setVisibility(View.VISIBLE);
        copyButton.setOnClickListener(
                v -> copyText(new StringBuilder().append(((TextView) findViewById(R.id.logField)).getText())
                        .append('\n').append(((TextView) findViewById(R.id.errorField)).getText())));
    }

    private void processOneSplitApkUri(Uri uri) {
        splitAPKUri = uri;
        if (showDialog)
            showApkSelectionDialog();
        else
            process();
    }

    public void toggleAnimation(boolean on) {
        isProcessing = on;
        runOnUiThread(() -> {
            View progress = findViewById(R.id.progressIndicator);
            if (progress != null) progress.setVisibility(on ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9 && com.abdurazaaqmohammed.utils.FileUtils.doesNotHaveStoragePerm(this))
            saveMode = 0;
        else if (resultCode != RESULT_OK) {
            ((TextView) findViewById(R.id.logField)).setText("");
            ((TextView) findViewById(R.id.errorField)).setText("");
            cleanupAppFolder();
        } else if (data != null)
            switch (requestCode) {
                case 1:
                    // opened through button in the app
                    ClipData clipData = data.getClipData();
                    if (clipData == null) {
                        Uri uri = data.getData();
                        if (uri == null)
                            showError("input Uri null");
                        else {
                            String uriString = uri.toString();
                            // Content URIs do not expose a path ending in ".apk"; resolve the
                            // real display name instead (fixes #624).
                            if (isApkFileName(getRawFileName(uri))) {
                                showError(rss.getString(R.string.not_split, uriString));
                            } else
                                processOneSplitApkUri(uri);
                        }
                    } else {
                        // multiple files selected
                        Uri first = clipData.getItemAt(0).getUri();
                        uris = new ArrayList<>();
                        // Use the resolved display name, not Uri.getPath(), because content
                        // URIs never end with ".apk" (fixes #609).
                        if (isApkFileName(getRawFileName(first))) {
                            urisAreSplitApks = false;
                        }
                        uris.add(first);
                        for (int i = 1; i < clipData.getItemCount(); i++)
                            uris.add(clipData.getItemAt(i).getUri());

                        if (urisAreSplitApks)
                            processOneSplitApkUri(first);
                        else
                            process();
                    }
                    break;
                case 2:
                    // NOT going to process a file now we are saving it
                    new RunUtil(handler, this, null).runInBackground(() -> {
                        try (OutputStream outputStream = getContentResolver().openOutputStream(data.getData())) {
                            com.abdurazaaqmohammed.utils.FileUtils.copyFile(merged, outputStream);
                            return true;
                        } catch (Exception e) {
                            showError(e);
                            return false;
                        }
                    }, () -> {
                        if (!criticalErrorOccurred) {
                            final String success = rss.getString(R.string.success_saved);
                            logger.logMessage(success);
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, success, Toast.LENGTH_SHORT).show());
                        }
                    }, true);
                    break;
            }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > 32) {
            registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                    Context.RECEIVER_NOT_EXPORTED);
        } else
            registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public long downloadId;
    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (id == downloadId) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                try (Cursor cursor = downloadManager.query(query)) {
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                            int columnIndex1 = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            String fileUri = cursor.getString(columnIndex1);
                            InstallUtil.installApk(MainActivity.this, Uri.parse(fileUri));
                        }
                    }
                }
            }
        }
    };

    public Runnable checkUpdateAfterStoragePermission;

    public void showApkSelectionDialog() {
        toggleAnimation(true);
        logger.logMessage(R.string.searching);
        RunUtil.runInBackground(() -> {
            try {
                List<String> splits = deviceSpecsUtil.getListOfSplits(splitAPKUri);
                Collections.sort(splits, CompareUtils::compareByName);
                final int initialSize = splits.size();
                String[] apkNames = new String[initialSize + 5];
                boolean[] checkedItems = new boolean[initialSize + 5];

                apkNames[0] = rss.getString(R.string.all);
                apkNames[1] = rss.getString(R.string.for_device);
                apkNames[2] = rss.getString(R.string.arch_for_device);
                apkNames[3] = rss.getString(R.string.dpi_for_device);
                apkNames[4] = rss.getString(R.string.lang_for_device);
                for (int i = 5; i < initialSize + 5; i++) {
                    apkNames[i] = splits.get(i - 5);
                    checkedItems[i] = false;
                }

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this).setTitle(rss.getString(R.string.select_splits))
                        .setMultiChoiceItems(apkNames, checkedItems, (dialog, which, isChecked) -> {
                            switch (which) {
                                case 0:
                                    // "Select All" option
                                    for (int i = 5; i < checkedItems.length; i++)
                                        ((AlertDialog) dialog).getListView().setItemChecked(i, checkedItems[i] = isChecked);
                                    break;
                                case 1:
                                    // device specs option
                                    for (int i = 5; i < checkedItems.length; i++) {
                                        ((AlertDialog) dialog).getListView().setItemChecked(i, checkedItems[i] = (isChecked
                                                && deviceSpecsUtil.shouldIncludeSplit(apkNames[i])));
                                    }
                                    boolean didNotFindAppropriateDpi = true;
                                    for (int i = 5; i < checkedItems.length; i++) {
                                        if (checkedItems[i] && apkNames[i].contains("dpi")) {
                                            didNotFindAppropriateDpi = false;
                                            break;
                                        }
                                    }
                                    if (didNotFindAppropriateDpi) {
                                        for (int i = 5; i < checkedItems.length; i++) {
                                            if (apkNames[i].contains("hdpi")) {
                                                ((AlertDialog) dialog).getListView().setItemChecked(i,
                                                        checkedItems[i] = isChecked);
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                case 2:
                                    // arch for device
                                    for (int i = 5; i < checkedItems.length; i++) {
                                        if (deviceSpecsUtil.shouldIncludeArch(apkNames[i]))
                                            ((AlertDialog) dialog).getListView().setItemChecked(i,
                                                    checkedItems[i] = isChecked);
                                    }
                                    break;
                                case 3:
                                    // dpi for device
                                    for (int i = 5; i < checkedItems.length; i++) {
                                        if (deviceSpecsUtil.shouldIncludeDpi(apkNames[i]))
                                            ((AlertDialog) dialog).getListView().setItemChecked(i,
                                                    checkedItems[i] = isChecked);
                                    }
                                    boolean didNotFoundAppropriateDpi = true;
                                    for (int i = 5; i < checkedItems.length; i++) {
                                        if (checkedItems[i] && apkNames[i].contains("dpi")) {
                                            didNotFoundAppropriateDpi = false;
                                            break;
                                        }
                                    }
                                    if (didNotFoundAppropriateDpi) {
                                        for (int i = 5; i < checkedItems.length; i++) {
                                            if (apkNames[i].contains("hdpi")) {
                                                ((AlertDialog) dialog).getListView().setItemChecked(i,
                                                        checkedItems[i] = isChecked);
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                case 4:
                                    // lang for device
                                    for (int i = 5; i < checkedItems.length; i++) {
                                        if (deviceSpecsUtil.shouldIncludeLang(apkNames[i]))
                                            ((AlertDialog) dialog).getListView().setItemChecked(i,
                                                    checkedItems[i] = isChecked);
                                    }
                                    break;
                                default:
                                    ListView listView = ((AlertDialog) dialog).getListView();
                                    if (!isChecked)
                                        listView.setItemChecked(0, checkedItems[0] = false); // Uncheck "Select All" if any
                                    // individual item is unchecked
                                    for (int i = 1; i <= 4; i++) {
                                        if (checkedItems[i] && !deviceSpecsUtil.shouldIncludeSplit(apkNames[which])) {
                                            listView.setItemChecked(i, checkedItems[i] = false); // uncheck device arch if
                                            // non device arch selected
                                        }
                                    }
                                    break;
                            }
                        }).setPositiveButton("OK", (dialog, which) -> {
                            for (int i = 1; i < checkedItems.length; i++) {
                                if (checkedItems[i])
                                    splits.remove(apkNames[i]);
                            }

                            if (splits.size() == initialSize) {
                                urisAreSplitApks = true; // reset
                                showError(rss.getString(R.string.nothing));
                            } else {
                                splitsUnselectedInDialog = splits;
                                process();
                            }
                        }).setNegativeButton("Cancel", null);
                getHandler().post(() -> {
                    toggleAnimation(false);
                    AlertDialog alertDialog = builder.create();
                    styleAlertDialog(alertDialog);
                    // Select base.apk by default
                    // Not force include base.apk because there may be some use case where you
                    // actually need to merge other splits only
                    for (int i = 5; i < apkNames.length; i++) {
                        if (DeviceSpecsUtil.isBaseApk(apkNames[i])) {
                            alertDialog.getListView().setItemChecked(i, checkedItems[i] = true);
                            break;
                        }
                    }
                });
            } catch (Exception e) {
                toggleAnimation(false);
                showError(e);
            }
        });
    }

    private void setUIAfterMerging(Merger merger) {
        urisAreSplitApks = true; // reset
        findViewById(R.id.cancelButton).setVisibility(View.GONE);
        View installButton = findViewById(R.id.installButton);
        View saveButton = findViewById(R.id.saveButton);
        if (criticalErrorOccurred) {
            installButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        } else {
            if (signApk && merger.signedApk != null) {
                installButton.setVisibility(View.VISIBLE);
                installButton.setOnClickListener(v -> InstallUtil.installApk(MainActivity.this, merger.signedApk));
            } else
                installButton.setVisibility(View.GONE);

            // Now that the prompt to save file is after merging, put a new button to prompt
            // again in case you closed it by accident.
            if (saveMode == 0) {
                saveButton.setVisibility(View.VISIBLE);

                    saveButton.setOnClickListener(view -> {
                        try { startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                .addCategory(Intent.CATEGORY_OPENABLE)
                                .setType("application/vnd.android.package-archive")
                                .putExtra(Intent.EXTRA_TITLE, "merged.apk"), 2);
                        } catch (ActivityNotFoundException e) {
                            showError("Was not able to find Android system files app to save APK, please grant files permission to save successfully");
                            saveNow(true);
                        }
                    });

            }
        }
    }

    private void copyText(CharSequence text) {
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                .setPrimaryClip(ClipData.newPlainText("log", text));
        Toast.makeText(this, rss.getString(R.string.copied_log), Toast.LENGTH_SHORT).show();
    }

    public void showError(Throwable e) {
        toggleAnimation(false);
        // uris can be null/empty when the failure happens before any Uri was queued;
        // removing unconditionally used to crash with IndexOutOfBoundsException.
        if (uris != null && !uris.isEmpty()) uris.remove(0);
        if (!(e instanceof ClosedByInterruptException)) {
            final String mainErr = e.getMessage();

            final boolean errEmpty = TextUtils.isEmpty(mainErr);
            criticalErrorOccurred = errEmpty || !mainErr.equals(rss.getString(R.string.sign_failed));

            StringBuilder stackTrace = new StringBuilder();
            if (!errEmpty) {
                stackTrace.append(mainErr);
                boolean zipError = mainErr.contains("Failed to find end record");
                if (!zipError) {
                    Uri u = splitAPKUri;
                    if (u != null) {
                        String mimeType = getContentResolver().getType(u);
                        zipError = (TextUtils.isEmpty(mimeType)
                                || (!mimeType.equals("application/zip") && !mimeType.equals("application/octet-stream")
                                && !mimeType.equals("application/vnd.apkm")));
                    }
                }

                if (zipError) {
                    stackTrace.append(" - ")
                            .append(rss.getString(R.string.check_file_valid))
                            .append(rss.getString(R.string.select_from_installed_apps))
                            .append('\n');
                }
            }
            if (!TextUtils.isEmpty(openedFile)) {
                stackTrace.append(openedFile).append('\n');
                openedFile = null;
            }
            for (StackTraceElement line : e.getStackTrace())
                stackTrace.append(line).append('\n');
            StringBuilder fullLog = new StringBuilder(stackTrace).append('\n')
                    .append("SDK ").append(Build.VERSION.SDK_INT).append('\n')
                    .append(rss.getString(R.string.app_name)).append(' ');
            String currentVer;
            try {
                currentVer = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (Exception ex) {
                currentVer = "2.3.0";
            }
            fullLog.append(currentVer).append('\n')
                    .append("Storage permission granted: ")
                    .append(!com.abdurazaaqmohammed.utils.FileUtils.doesNotHaveStoragePerm(this))
                    .append('\n').append(((TextView) findViewById(R.id.logField)).getText());

            getHandler().post(() -> {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_button_layout, null);

                ((TextView) dialogView.findViewById(R.id.errorD)).setText(stackTrace);
                styleAlertDialog(new MaterialAlertDialogBuilder(this)
                        .setTitle(mainErr)
                        .setView(dialogView)
                        .setPositiveButton(rss.getString(R.string.copy_log), (dialog, which) -> {
                            copyText(fullLog);
                            dialog.dismiss();
                        })
                        .setNegativeButton(rss.getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                        .create());
                ScrollView scrollView = dialogView.findViewById(R.id.errorView);

                ViewGroup.LayoutParams params = scrollView.getLayoutParams();
                params.height = (int) (rss.getDisplayMetrics().heightPixels * 0.5);
                scrollView.setLayoutParams(params);
            });
        }
        cleanupAppFolder();
    }

    public void showError(String err) {
        toggleAnimation(false);
        runOnUiThread(() -> {
            TextView errorBox = findViewById(R.id.errorField);
            errorBox.setVisibility(View.VISIBLE);
            errorBox.setText(err);
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
        });
    }

    public String getOriginalFileName(Uri uri) {
        String result = null;

        String scheme = uri.getScheme();
        if (!TextUtils.isEmpty(scheme) && "content".equals(scheme)) {
            ContentResolver contentResolver = getContentResolver();
            if (contentResolver != null)
                try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (index >= 0) result = cursor.getString(index);
                    }
                } catch (Exception ignored) {
                }
        }

        if (TextUtils.isEmpty(result)) {
            result = uri.getPath();
            if (!TextUtils.isEmpty(result)) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }

        logger.logMessage(openedFile = result);

        if (TextUtils.isEmpty(result)) {
            result = "file_" + System.currentTimeMillis();
        }
        result = sanitizeFileName(result);
        try {
            return result.replaceFirst("\\.(?:zip|xapk|aspk|apk[sm])$", suffix + ".apk");
        } catch (Exception e) {
            return result + suffix + ".apk";
        }
    }

    /**
     * Resolves the display name of a content Uri WITHOUT rewriting its extension.
     * getOriginalFileName() normalises every name to "*.apk", so it cannot be used to
     * decide whether the user picked a plain APK or a bundle (#624/#609).
     */
    private String getRawFileName(Uri uri) {
        String result = null;
        String scheme = uri.getScheme();
        if (!TextUtils.isEmpty(scheme) && "content".equals(scheme)) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) result = cursor.getString(index);
                }
            } catch (Exception ignored) {
            }
        }
        if (TextUtils.isEmpty(result)) {
            result = uri.getPath();
            if (!TextUtils.isEmpty(result)) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Removes characters that are illegal in file names and strips host-name noise
     * (e.g. "apkmirror.com") that some downloaders prepend to file names (#387).
     */
    private static String sanitizeFileName(String name) {
        if (name == null) return null;
        String cleaned = name.replaceAll("(?i)apkmirror\\.com", "")
                .replaceAll("[\\\\/:*?\"<>|\\r\\n\\t]", "_")
                .trim();
        while (cleaned.startsWith(".") || cleaned.startsWith("_"))
            cleaned = cleaned.substring(1);
        if (cleaned.length() > 120)
            cleaned = cleaned.substring(cleaned.length() - 120);
        return cleaned.isEmpty() ? "file_" + System.currentTimeMillis() : cleaned;
    }

    private static boolean isApkFileName(String name) {
        return name != null && name.toLowerCase(java.util.Locale.ROOT).endsWith(".apk");
    }

    private void selectDirToSaveAPKOrSaveNow() {
        selectDirToSaveAPKOrSaveNow(saveMode);
    }

    private void selectDirToSaveAPKOrSaveNow(int saveMode) {
        boolean askWhereToSave = saveMode == 0;
        if (askWhereToSave || com.abdurazaaqmohammed.utils.FileUtils.doesNotHaveStoragePerm(this)) {
            getHandler().post(() -> Toast.makeText(this, R.string.merged, Toast.LENGTH_LONG).show());
            String filename;
            if (TextUtils.isEmpty(pkgName))
                filename = (urisAreSplitApks ? getOriginalFileName(splitAPKUri) : getNameFromNonSplitApks());
            else {
                String versionName;
                try {
                    versionName = " v" + getPackageManager().getPackageInfo(pkgName, 0).versionName;
                } catch (Exception ignored) {
                    versionName = "";
                }
                filename = pkgName + versionName + suffix;
            }

            try {
                startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT)
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType("application/vnd.android.package-archive")
                        .putExtra(Intent.EXTRA_TITLE, filename), 2);
            } catch (ActivityNotFoundException e) {
                showError("Was not able to find Android system files app to save APK, please grant files permission to save successfully");
                saveNow(true);
            }
        } else saveNow(false);
    }

    private void saveNow(boolean notFoundCreateDocument) {
        checkStoragePerm(9);
        RunUtil.runInBackground(() -> {
                try {
                    File f;
                    String fp;
                    if (urisAreSplitApks) {
                        try {
                            fp = FileUtils.getPath(splitAPKUri, MainActivity.this);
                            if (TextUtils.isEmpty(fp)
                                    || fp.startsWith("/data/")
                                    || fp.contains("/Android/data")) {
                                f = new File(outputFolder, getOriginalFileName(splitAPKUri));
                            } else
                                f = new File(fp.replaceFirst("\\.(?:zip|xapk|aspk|apk[sm])", suffix + ".apk"));
                        } catch (Exception e) {
                            f = new File(outputFolder, getOriginalFileName(splitAPKUri));
                        }
                    } else {
                        String path;

                        try {
                            path = FileUtils.getPath(uris.get(0), MainActivity.this);
                            if (TextUtils.isEmpty(path))
                                fp = outputFolder + File.separator + getNameFromNonSplitApks();
                            else
                                fp = path.substring(0, path.lastIndexOf(File.separator)) + File.separator
                                        + getNameFromNonSplitApks();
                            if (TextUtils.isEmpty(fp)
                                    || fp.startsWith("/data/")
                                    || fp.contains("/Android/data")) {
                                f = new File(outputFolder, getNameFromNonSplitApks());
                            } else
                                f = new File(fp);
                        } catch (Exception e) {
                            f = new File(outputFolder, getOriginalFileName(uris.get(0)));
                        }
                    }

                    f = com.abdurazaaqmohammed.utils.FileUtils.getUnusedFile(f);
                    logger.logMessage(rss.getString(R.string.output) + f);
                    com.abdurazaaqmohammed.utils.FileUtils.copyFile(merged, f);
                    if (!criticalErrorOccurred) {
                        final String success = rss.getString(R.string.success_saved);
                        logger.logMessage(success);
                        runOnUiThread(() -> Toast.makeText(this, success, Toast.LENGTH_SHORT).show());
                    }
                } catch (IOException e) {
                    if(notFoundCreateDocument) {
                        showError(e);
                    } else runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        selectDirToSaveAPKOrSaveNow(0);
                    });
                }
            });
    }

    private String getNameFromNonSplitApks() {
        String realName = null;
        String base = "base.apk";
        for (Uri uri : uris) {
            final String fileName = getOriginalFileName(uri);
            if (base.equals(fileName)) {
                try {
                    realName = Objects.requireNonNull(getPackageManager().getPackageArchiveInfo(
                            Objects.requireNonNull(FileUtils.getPath(uri, this)), 0)).packageName;
                    break;
                } catch (NullPointerException | IOException ignored) {
                }
            } else if (!fileName.startsWith("config") && !fileName.startsWith("split")) {
                // this should hopefully be base.apk renamed to the package name
                realName = fileName;
                break;
            }
        }
        return (TextUtils.isEmpty(realName) ? "unknown" + suffix + ".apk"
                : realName.replaceFirst("\\.apk$", suffix + ".apk"));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void installedAppsClickListener(View v3) {
        ((TextView) findViewById(R.id.logField)).setText("");
        ((TextView) findViewById(R.id.errorField)).setText("");
        AlertDialog ad = new MaterialAlertDialogBuilder(MainActivity.this)
                .setNegativeButton(rss.getString(R.string.cancel), null).create();
        PackageManager pm = getPackageManager();

        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<>();

        for (PackageInfo packageInfo : packageInfoList) {
            try {
                String packageName = packageInfo.packageName;
                ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
                if (showAllApps || ai.splitSourceDirs != null) {
                    appInfoList.add(new AppInfo(
                            (String) pm.getApplicationLabel(ai),
                            pm.getApplicationIcon(ai),
                            packageName,
                            packageInfo.lastUpdateTime,
                            packageInfo.firstInstallTime));
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        if (sortMode == 0)
            Collections.sort(appInfoList, CompareUtils::compareAppInfoByName);
        else
            Collections.sort(appInfoList, (p1, p2) -> {
                long field1 = (sortMode == 1) ? p1.lastUpdated : p1.firstInstall;
                long field2 = (sortMode == 1) ? p2.lastUpdated : p2.firstInstall;
                return Long.compare(field2, field1);
            });

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View dialogView = layoutInflater.inflate(R.layout.dialog_search, null);

        ListView listView = dialogView.findViewById(R.id.list_view);
        final AppListArrayAdapter adapter = new AppListArrayAdapter(MainActivity.this, appInfoList, true);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ad.dismiss();
            pkgName = adapter.filteredAppInfoList.get(position).packageName;
            process();
        });
        EditText searchBar = dialogView.findViewById(R.id.search_bar);

        View clearButton = dialogView.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> searchBar.setText(""));

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearButton.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        dialogView.findViewById(R.id.filter_button).setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.sort_name) {
                    sortMode = 0;
                    Collections.sort(appInfoList, CompareUtils::compareAppInfoByName);
                } else if (itemId == R.id.date_updated) {
                    sortMode = 1;
                    Collections.sort(appInfoList, (p1, p2) -> {
                        long field1 = p1.lastUpdated;
                        long field2 = p2.lastUpdated;
                        return Long.compare(field2, field1);
                    });
                } else {
                    sortMode = 2;
                    Collections.sort(appInfoList, (p1, p2) -> {
                        long field1 = p1.firstInstall;
                        long field2 = p2.firstInstall;
                        return Long.compare(field2, field1);
                    });
                }
                listView.setAdapter(new AppListArrayAdapter(MainActivity.this, appInfoList, true));
                return true;
            });

            popupMenu.show();
        });

        ad.setView(dialogView);
        styleAlertDialog(ad);
    }

    /** @noinspection AssignmentUsedAsCondition */
    private void settingsButtonListener(View v) {
        ScrollView settingsDialog = (ScrollView) LayoutInflater.from(MainActivity.this).inflate(R.layout.settings_dialog, null);
        settingsDialog.setId(R.id.icon_view);
        LanguageUtil.updateSettingsDialog(settingsDialog, rss);

        TextInputEditText suffixInput = settingsDialog.findViewById(R.id.suffixInput);
        suffixInput.setText(suffix);
        suffixInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                suffix = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button checkUpdateNow = settingsDialog.findViewById(R.id.checkUpdateNow);
        CompoundButton updateSwitch = settingsDialog.findViewById(R.id.updateToggle);
        updateSwitch.setChecked(checkForUpdates);
        updateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> checkUpdateNow
                .setVisibility((checkForUpdates = isChecked) ? View.GONE : View.VISIBLE));
        checkUpdateNow.setVisibility(checkForUpdates ? View.GONE : View.VISIBLE);
        checkUpdateNow.setOnClickListener(v1 -> UpdateUtil.checkForUpdates(true, this));

        CompoundButton showAllAppsSwitch = settingsDialog.findViewById(R.id.showAllAppsToggle);
        showAllAppsSwitch.setChecked(showAllApps);
        showAllAppsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> showAllApps = isChecked);

        CompoundButton logSwitch = settingsDialog.findViewById(R.id.logToggle);
        logSwitch.setChecked(logEnabled);
        logSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> logEnabled = isChecked);

        CompoundButton signToggle = settingsDialog.findViewById(R.id.signToggle);
        signToggle.setChecked(signApk);
        signToggle.setOnCheckedChangeListener((buttonView, isChecked) -> signApk = isChecked);

        // Signing scheme selector (v1/v2/v3 combinations, or Auto)
        TextInputLayout signingSchemeLayout = settingsDialog.findViewById(R.id.dropdown_signing_scheme);
        AutoCompleteTextView signingSchemeView = settingsDialog.findViewById(R.id.auto_complete_tv3);
        final String[] schemeItems = {
                rss.getString(R.string.scheme_auto),
                rss.getString(R.string.scheme_v1v2v3),
                rss.getString(R.string.scheme_v2v3),
                rss.getString(R.string.scheme_v3),
                rss.getString(R.string.scheme_v1v2),
                rss.getString(R.string.scheme_v1)
        };
        signingSchemeLayout.setHint(rss.getString(R.string.signing_scheme));
        ArrayAdapter<String> schemeAdapter = new ArrayAdapter<String>(this, R.layout.dropdownitem, schemeItems) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dropdownitem, parent, false);
                TextView view = (TextView) convertView;
                view.setTextColor(theme == R.style.Theme_MyApp_Light ? Color.BLACK : Color.WHITE);
                view.setText(schemeItems[position]);
                return convertView;
            }
        };
        int schemeIndex = Math.max(0, Math.min(signingScheme, schemeItems.length - 1));
        signingSchemeView.setText(schemeItems[schemeIndex]);
        signingSchemeView.setAdapter(schemeAdapter);
        signingSchemeView.setThreshold(1);
        signingSchemeView.setOnItemClickListener((parent, view, position, id) -> signingScheme = position);

        // Show the SHA-256 fingerprint of the bundled signing certificate
        MaterialTextView fingerprintText = settingsDialog.findViewById(R.id.fingerprintText);
        String fingerprint;
        try {
            fingerprint = SignUtil.getSigningCertificateSha256(this);
        } catch (Exception e) {
            fingerprint = rss.getString(R.string.fingerprint_unavailable);
        }
        fingerprintText.setText(rss.getString(R.string.signing_cert_fingerprint) + "\n" + fingerprint);
        final String fingerprintValue = fingerprint;
        settingsDialog.findViewById(R.id.copyFingerprintButton)
                .setOnClickListener(v3 -> copyText(fingerprintValue));

        CompoundButton forceToggle = settingsDialog.findViewById(R.id.forceToggle);
        forceToggle.setChecked(force);
        forceToggle.setOnCheckedChangeListener((buttonView, isChecked) -> force = isChecked);

        CompoundButton selectSplitsAutomaticallySwitch = settingsDialog.findViewById(R.id.selectSplitsForDeviceToggle);
        CompoundButton showDialogSwitch = settingsDialog.findViewById(R.id.showDialogToggle);

        showDialogSwitch.setChecked(showDialog);
        showDialogSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (showDialog = isChecked) {
                selectSplitsAutomaticallySwitch.setChecked(selectSplitsForDevice = false);
            } else {
                splitsUnselectedInDialog = null;
            }
        });

        selectSplitsAutomaticallySwitch.setChecked(selectSplitsForDevice);
        selectSplitsAutomaticallySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (selectSplitsForDevice = isChecked)
                showDialogSwitch.setChecked(showDialog = false);
        });

        settingsDialog.findViewById(R.id.langPicker).setOnClickListener(v2 -> {
            String[] langs = rss.getStringArray(R.array.langs);
            String[] display = rss.getStringArray(R.array.langs_display);

            AlertDialog ad = new MaterialAlertDialogBuilder(MainActivity.this)
                    .setSingleChoiceItems(display, -1, (dialog, which) -> {
                        LanguageUtil.updateLang(
                                rss = LocaleHelper.setLocale(MainActivity.this, lang = langs[which]).getResources(),
                                settingsDialog, MainActivity.this);
                        dialog.dismiss();
                    }).create();
            MainActivity.this.styleAlertDialog(ad);
            ad.getListView().setAdapter(
                    new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_singlechoice, display));
            for (int i = 0; i < langs.length; i++)
                if (lang.equals(langs[i])) {
                    ad.getListView().setItemChecked(i, true);
                    break;
                }
        });
        MaterialTextView title = new MaterialTextView(MainActivity.this);
        title.setText(rss.getString(R.string.settings));
        int size = 20;
        title.setPadding(size, size, size, size);
        title.setTextSize(size);
        title.setGravity(Gravity.CENTER);
        AlertDialog ad = new MaterialAlertDialogBuilder(MainActivity.this).setCustomTitle(title).setView(settingsDialog)
                .setPositiveButton(rss.getString(R.string.close), (dialog, which) -> dialog.dismiss()).create();
        MainActivity.this.styleAlertDialog(ad);

        TextInputLayout dropdownLayout = settingsDialog.findViewById(R.id.dropdown_menu);
        AutoCompleteTextView autoCompleteTextView = settingsDialog.findViewById(R.id.auto_complete_tv);

        String[] items = { rss.getString(R.string.ask), rss.getString(R.string.same), rss.getString(R.string.pick_folder) };
        dropdownLayout.setHint(rss.getString(R.string.file_save_method));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dropdownitem, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dropdownitem, parent, false);
                TextView view = (TextView) convertView;
                view.setTextColor(theme == R.style.Theme_MyApp_Light ? Color.BLACK : Color.WHITE);
                view.setText(items[position]);
                return convertView;
            }
        };
        autoCompleteTextView.setText(items[saveMode]);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            saveMode = position;
            if ((position != 0) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M))
                checkStoragePerm(9);
            if (position == 2) {
                TextInputEditText editText = new TextInputEditText(this);
                editText.setHint(rss.getString(R.string.pick_folder));
                editText.setText(outputFolder);
                editText.setPadding(16, 16, 16, 16);
                styleAlertDialog(new MaterialAlertDialogBuilder(this).setTitle(rss.getString(R.string.pick_folder))
                        .setView(editText).setNegativeButton(rss.getString(R.string.cancel), null)
                        .setPositiveButton("OK", (dialog, which) -> {
                            CharSequence text = editText.getText();
                            if (TextUtils.isEmpty(text))
                                showError(rss.getString(R.string.no_filepath) + text);
                            else {
                                String string = text.toString();
                                if ((new File(string).exists() || new File(string).mkdirs())) {
                                    outputFolder = string;
                                } else {
                                    showError(rss.getString(R.string.no_filepath) + text);
                                    saveMode = 0;
                                }
                            }
                        }).create(), false);
            }
        });

        TextInputLayout dropdownLayout2 = settingsDialog.findViewById(R.id.dropdown_compress_level);
        AutoCompleteTextView autoCompleteTextView2 = settingsDialog.findViewById(R.id.auto_complete_tv2);

        String[] items2 = {rss.getString(R.string.default_c), "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        dropdownLayout2.setHint(rss.getString(R.string.select_compression_level));

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.dropdownitem, items2) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dropdownitem, parent, false);
                TextView view = (TextView) convertView;
                view.setTextColor(theme == R.style.Theme_MyApp_Light ? Color.BLACK : Color.WHITE);
                view.setText(items2[position]);
                return convertView;
            }
        };
        autoCompleteTextView2.setText(items2[compressionLevel + 1]);
        autoCompleteTextView2.setAdapter(adapter2);
        autoCompleteTextView2.setThreshold(1);
        autoCompleteTextView2.setOnItemClickListener((parent, view, position, id) -> compressionLevel = position - 1);
    }
}
```

### `app/src/main/java/com/abdurazaaqmohammed/utils/RunUtil.java`

```java
package com.abdurazaaqmohammed.utils;


import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.abdurazaaqmohammed.AntiSplit.main.MainActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RunUtil {

    private final Handler handler;
    private final MainActivity context;
    private final CharSequence msg;

    public RunUtil(Handler handler, MainActivity context, CharSequence msg) {
        this.handler = handler;
        this.context = context;
        this.msg = msg;
    }

    public static void runInBackground(Runnable runnable) {
        try {
            Executors.newSingleThreadExecutor().submit(runnable);
        } catch (Exception ignored) { }
    }

    public void runInBackground(Callable<Boolean> callable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(callable);

        executor.submit(() -> {
            try {
                Boolean success = future.get();

                if (success && handler != null && !TextUtils.isEmpty(msg))
                    handler.post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
            } catch (Throwable e) {
                reportFailure(e);
            } finally {
                executor.shutdown();
            }
        });
    }

    public void runInBackground(Callable<Boolean> callable, Runnable doAfter, boolean onlyIfSuccessful) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(callable);

        executor.submit(() -> {
            try {
                if (future.get() && handler != null) handler.post(doAfter);
            } catch (Throwable e) {
                if (!onlyIfSuccessful && handler != null) handler.post(doAfter);
                reportFailure(e);
            } finally {
                executor.shutdown();
            }
        });
    }

    /**
     * Future.get() wraps the real failure in an ExecutionException, so unwrap it
     * before deciding whether we ran out of memory (#653 / #143).
     */
    private void reportFailure(Throwable e) {
        Throwable cause = e;
        if (e instanceof java.util.concurrent.ExecutionException && e.getCause() != null)
            cause = e.getCause();
        if (cause instanceof OutOfMemoryError)
            context.showError(context.getRss().getString(com.abdurazaaqmohammed.AntiSplit.R.string.out_of_memory));
        else
            context.showError(cause);
    }
}
```

### `app/src/main/java/com/abdurazaaqmohammed/utils/UpdateUtil.java`

```java
package com.abdurazaaqmohammed.utils;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.abdurazaaqmohammed.AntiSplit.R;
import com.abdurazaaqmohammed.AntiSplit.main.MainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateUtil {
    public static void checkForUpdates(boolean toast, MainActivity context) {
        new Thread(() -> {
            try {
                HttpURLConnection conn = getHttpURLConnection();

                try (InputStream inputStream = conn.getInputStream();
                     InputStreamReader in = new InputStreamReader(inputStream);
                     BufferedReader reader = new BufferedReader(in)) {
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        json.append(line).append('\n');

                    String latestVersion = "";
                    String changelog = "";
                    String dl = "";

                    // Parse the GitHub releases JSON properly instead of relying on the
                    // position of slashes/quotes, which broke when the release tag format
                    // changed (#628).
                    org.json.JSONArray releases = new org.json.JSONArray(json.toString());
                    for (int i = 0; i < releases.length(); i++) {
                        org.json.JSONObject release = releases.getJSONObject(i);
                        if (release.optBoolean("draft") || release.optBoolean("prerelease"))
                            continue;
                        String tag = release.optString("tag_name", "");
                        String version = tag.replaceFirst("^[vV]", "").trim();
                        if (TextUtils.isEmpty(version))
                            continue;
                        org.json.JSONArray assets = release.optJSONArray("assets");
                        String downloadUrl = null;
                        if (assets != null) {
                            for (int j = 0; j < assets.length(); j++) {
                                org.json.JSONObject asset = assets.getJSONObject(j);
                                String name = asset.optString("name", "");
                                if (name.toLowerCase(java.util.Locale.ROOT).endsWith(".apk")) {
                                    downloadUrl = asset.optString("browser_download_url", null);
                                    break;
                                }
                            }
                        }
                        if (downloadUrl == null)
                            continue;
                        latestVersion = version;
                        dl = downloadUrl;
                        changelog = release.optString("body", "");
                        break;
                    }

                    String currentVer;
                    try {
                        currentVer = (context).getPackageManager().getPackageInfo((context).getPackageName(), 0).versionName;
                    } catch (Exception e) {
                        currentVer = null;
                    }
                    boolean newVer = !TextUtils.isEmpty(latestVersion)
                            && compareVersions(latestVersion, TextUtils.isEmpty(currentVer) ? "0" : currentVer) > 0;
                    Resources rss = context.getRss();

                    if (newVer) {
                        if (!toast && !TextUtils.isEmpty(context.lastVerChecked) && context.lastVerChecked.equals(latestVersion))
                            return;
                        String ending = ".apk";
                        String filename = "APK-AntiSpliter.v" + latestVersion + ending;
                        String link = dl.endsWith(ending) ? dl : dl + File.separator + filename;
                        MaterialTextView changelogText = new MaterialTextView(context);
                        String linebreak = "<br />";
                        changelogText.setText(Html.fromHtml(
                                rss.getString(R.string.new_ver) + " (" + latestVersion + ")" + linebreak + linebreak
                                        + "Changelog:" + linebreak + changelog.replace("\r\n", linebreak)));
                        int padding = 16;
                        changelogText.setPadding(padding, padding, padding, padding);
                        MaterialTextView title = new MaterialTextView(context);
                        title.setText(rss.getString(R.string.update));
                        int size = 20;
                        title.setPadding(size, size, size, size);
                        title.setTextSize(size);
                        title.setGravity(Gravity.CENTER);

                        String finalLatestVersion = latestVersion;
                        context.getHandler().post(() -> {
                            AlertDialog alertDialog = new MaterialAlertDialogBuilder(context)
                                    .setCustomTitle(title).setView(changelogText)
                                    .setPositiveButton(rss.getString(R.string.dl), (dialog, which) -> {
                                        if (context.checkUpdateAfterStoragePermission == null)
                                            context.checkUpdateAfterStoragePermission = () -> {
                                                DownloadManager.Request request = new DownloadManager.Request(
                                                        Uri.parse(link))
                                                        .setTitle(filename).setDescription(filename)
                                                        .setMimeType("application/vnd.android.package-archive")
                                                        .setDestinationInExternalPublicDir(
                                                                Environment.DIRECTORY_DOWNLOADS, filename)
                                                        .setNotificationVisibility(
                                                                DownloadManager.Request.VISIBILITY_VISIBLE
                                                                        | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                context.downloadId = ((DownloadManager) context
                                                        .getSystemService(DOWNLOAD_SERVICE)).enqueue(request);
                                            };
                                        if (Build.VERSION.SDK_INT < 29
                                                && com.abdurazaaqmohammed.utils.FileUtils.doesNotHaveStoragePerm(context))
                                            context.checkStoragePerm(10);
                                        else
                                            context.checkUpdateAfterStoragePermission.run();
                                    })
                                    .setNegativeButton(rss.getString(R.string.cancel), null).create();
                            alertDialog.setOnDismissListener(dialog -> context.lastVerChecked = finalLatestVersion);
                            context.runOnUiThread(alertDialog::show);
                        });
                    } else if (toast)
                        context.getHandler().post(() -> context.runOnUiThread(() -> Toast.makeText(context,
                                rss.getString(R.string.no_update_found), Toast.LENGTH_SHORT).show()));
                    // return new String[]{ver, changelog, dl};
                }
            } catch (Exception e) {
                // context.showError(e);
                if (toast)
                    context.runOnUiThread(() -> Toast
                            .makeText(context, "Failed to check for update", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(
                "https://api.github.com/repos/AbdurazaaqMohammed/AntiSplit-M/releases").openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 Edg/128.0.0.0");
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        return conn;
    }

    /** Numeric, dot-separated version comparison. Returns >0 if a is newer than b. */
    private static int compareVersions(String a, String b) {
        String[] pa = a.split("[^0-9]+");
        String[] pb = b.split("[^0-9]+");
        int len = Math.max(pa.length, pb.length);
        for (int i = 0; i < len; i++) {
            int na = 0, nb = 0;
            try { if (i < pa.length && !pa[i].isEmpty()) na = Integer.parseInt(pa[i]); } catch (NumberFormatException ignored) {}
            try { if (i < pb.length && !pb[i].isEmpty()) nb = Integer.parseInt(pb[i]); } catch (NumberFormatException ignored) {}
            if (na != nb) return na - nb;
        }
        return 0;
    }
}
```

### `app/src/main/java/com/starry/FileUtils.java`

```java
package com.starry;

import static com.abdurazaaqmohammed.utils.FileUtils.doesNotHaveStoragePerm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.abdurazaaqmohammed.AntiSplit.main.MainActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    /*
    MIT License

    Copyright (c) 2023 Stɑrry Shivɑm

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

    https://github.com/starry-shivam/FileUtils/blob/main/file-utils/src/main/java/com/starry/file_utils/FileUtils.kt
     */

    public static OutputStream getOutputStream(Uri uri, MainActivity context) throws IOException {
        String uriPath;
        if(doesNotHaveStoragePerm(context) || (uriPath = uri.getPath()) == null || uriPath.startsWith("/document/msf:")) return context.getContentResolver().openOutputStream(uri);
        try {
            String filePath = getPath(uri, context);
            File file = filePath == null ? null : new File(filePath);
            return file != null && file.canWrite() ? com.abdurazaaqmohammed.utils.FileUtils.getOutputStream(file) : context.getContentResolver().openOutputStream(uri);
        } catch (Exception e) {
            return context.getContentResolver().openOutputStream(uri);
        }
    }

    public static InputStream getInputStream(Uri uri, MainActivity context) throws IOException {
        if(doesNotHaveStoragePerm(context)) return context.getContentResolver().openInputStream(uri);
        String filePath = getPath(uri, context);
        File file = filePath == null ? null : new File(filePath);
        return file != null && file.canRead() ? com.abdurazaaqmohammed.utils.FileUtils.getInputStream(file) : context.getContentResolver().openInputStream(uri);
    }

    private static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    private static String getPathFromExtSD(String[] pathData) {
        String type = pathData[0];
        String relativePath = File.separator + pathData[1];
        String fullPath;

        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory().toString() + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }

        if ("home".equalsIgnoreCase(type)) {
            fullPath = "/storage/emulated/0/Documents" + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }

        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        return fileExists(fullPath) ? fullPath : null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        try {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isDownloadsDocument(Uri uri) {
       try {
           return "com.android.providers.downloads.documents".equals(uri.getAuthority());
       } catch (Exception e) {
           return false;
       }
    }

    private static boolean isMediaDocument(Uri uri) {
       try{
           return "com.android.providers.media.documents".equals(uri.getAuthority());
       } catch (Exception e) {
           return false;
       }
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        try{
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("NewApi")
    public static String getPath(Uri uri, MainActivity context) throws IOException {
        String selection;
        String[] selectionArgs;

        if (isExternalStorageDocument(uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            String[] split = docId.split(":");
            String fullPath = getPathFromExtSD(split);
            if (fullPath == null || !fileExists(fullPath)) {
                fullPath = copyFileToInternalStorageAndGetPath(uri, context);
            }
            return TextUtils.isEmpty(fullPath) ? null : fullPath;
        }

        if (isDownloadsDocument(uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (docId.startsWith("msf:")) {
                final String[] split = docId.split(":");
                selection = "_id=?";
                selectionArgs = new String[] { split[1] };
                String relativePath = getDataColumn(context, MediaStore.Downloads.EXTERNAL_CONTENT_URI, selection, selectionArgs);
                if(TextUtils.isEmpty(relativePath)) try (Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        String fileName = cursor.getString(0);
                        String path = Environment.getExternalStorageDirectory() + "/Download/" + fileName;
                        if (!TextUtils.isEmpty(path)) {
                            return path;
                        }
                    }
                } else return relativePath;
            }

            String id = DocumentsContract.getDocumentId(uri);
            if (!TextUtils.isEmpty(id)) {
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }

//                String[] contentUriPrefixesToTry = {
//                        "content://downloads/public_downloads",
//                        "content://downloads/my_downloads"
//                };
//
//                for (String contentUriPrefix : contentUriPrefixesToTry) {
//                    try {
//                        Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.parseLong(id));
//                        return getDataColumn(context, contentUri, null, null);
//                    } catch (NumberFormatException e) {
//                        return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
//                    }
//                }
            }
        }

        if (isMediaDocument(uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            String[] split = docId.split(":");
            String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            } else if ("document".equals(type)) {
                contentUri = MediaStore.Files.getContentUri(MediaStore.getVolumeName(uri));
            }

            selection = "_id=?";
            selectionArgs = new String[]{split[1]};
            return getDataColumn(context, contentUri, selection, selectionArgs);
        }

        String scheme = uri.getScheme();
        if(!TextUtils.isEmpty(scheme)) {
            if ("content".equalsIgnoreCase(scheme)) {
                return doesNotHaveStoragePerm(context) ?
                                copyFileToInternalStorageAndGetPath(uri, context) :
                                getDataColumn(context, uri, null, null);
            }

            if ("file".equalsIgnoreCase(scheme)) {
                return uri.getPath();
            }
        }


        return copyFileToInternalStorageAndGetPath(uri, context);
    }

    public static String copyFileToInternalStorageAndGetPath(Uri uri, MainActivity context) throws IOException {
        return copyFileToInternalStorage(uri, context).getPath();
    }

    public static File copyFileToInternalStorage(Uri uri, MainActivity context) throws IOException {
        File output = new File(context.getCacheDir(), context.getOriginalFileName(uri));
        if(output.exists() && output.length() > 999) return output;
        try (OutputStream outputStream = com.abdurazaaqmohammed.utils.FileUtils.getOutputStream(output); InputStream cursor = context.getContentResolver().openInputStream(uri)) {
            int read;
            byte[] buffers = new byte[1024];
            while ((read = cursor.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
        }
        return output;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        final String column = "_data";
        // Query without a projection: some providers (Android 10+ / cloud providers) do
        // not expose the legacy _data column and throw when it is requested (#634).
        try (Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(column);
                if (index >= 0) {
                    String value = cursor.getString(index);
                    if (!TextUtils.isEmpty(value)) return value;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
```

### `README.md`

```md
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
```

