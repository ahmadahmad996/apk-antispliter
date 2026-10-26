#!/usr/bin/env python3
"""Generates CHANGES.md: file list, per-change explanations, and full file contents."""
import os, io

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.chdir(ROOT)

# (path, one-line role, explanation)
FILES = [
 ("settings.gradle.kts", "Gradle project name",
  "rootProject.name changed from \"AntiSplit M\" to \"APK-AntiSpliter\" so IDE/build outputs use the new name."),

 ("app/src/main/res/values/strings.xml", "Default strings (English)",
  "app_name set to APK-AntiSpliter (translatable=false). Added strings for the new signing-scheme selector, "
  "certificate fingerprint, show-all-apps toggle, section headers, merge card copy, log card copy, and error "
  "messages (invalid_file, out_of_memory, processing). Removed the create_issue string (#619). "
  "new_ver text updated to the new app name."),

 ("app/src/main/res/values/colors.xml", "Brand palette",
  "Fully rewritten with an \"Elegant Dark\" palette: gold (gold_200..gold_700), silver, ink (near-black "
  "surfaces) and warm paper tones for the light theme. Legacy names used by Java code (main_500, secondary_600, "
  "main_dark_800, colorPrimary, colorAccent, colorHeader, textColorPrimary/Secondary) are kept as aliases so no "
  "Java change is required."),

 ("app/src/main/res/values/styles.xml", "Material 3 themes + shared widgets",
  "Theme.MyApp.Dark / .Black / .Light rewritten with full Material3 color roles, transparent status/nav bars and "
  "correct light-bar flags. Adds reusable styles: Widget.AntiSplit.Card (22dp radius, 1dp outline), "
  "TextAppearance.AntiSplit.Section/Title/Subtitle, Widget.AntiSplit.Button.Primary/Outlined, "
  "Widget.AntiSplit.Switch and Widget.AntiSplit.Divider."),

 ("app/src/main/res/values-v21/styles.xml", "API 21+ theme copy",
  "Kept byte-identical to values/styles.xml so API 21+ devices get the same redesigned theme."),

 ("app/src/main/res/values-v31/colors.xml", "API 31+ color overrides",
  "Previously remapped the accent colors to @android:color/system_accent*, which silently replaced the brand "
  "palette on Android 12+. Now pins main_500/secondary_600/main_dark_800 to the gold/silver brand colors."),

 ("app/src/main/res/drawable/bg_main_dark.xml", "Dark window background (new)",
  "Ink vertical gradient plus a soft radial gold glow in the top-right corner. Referenced by Theme.MyApp.Dark."),

 ("app/src/main/res/drawable/bg_main_black.xml", "AMOLED black background (new)",
  "Pure black with a very subtle gold vignette. Referenced by Theme.MyApp.Black."),

 ("app/src/main/res/drawable/bg_main_light.xml", "Light window background (new)",
  "Warm paper gradient with a light gold wash. Referenced by Theme.MyApp.Light."),

 ("app/src/main/res/drawable/bg_monogram.xml", "Header monogram (new)",
  "Gold gradient rounded square used as the background of the 52dp \"A\" monogram in the header."),

 ("app/src/main/res/layout/activity_main.xml", "Main screen layout",
  "Redesigned: header (gold monogram + app name + subtitle), a merge card containing the merge button, formats "
  "caption, installed-apps button and progress indicator, a log card with a section label and scrollable log, and "
  "a vertical FAB column (copy/install/save/cancel/settings). All view ids used by MainActivity are preserved, "
  "so no Java changes were needed for the layout swap."),

 ("app/src/main/res/layout/settings_dialog.xml", "Settings dialog layout",
  "The top theme toggle card (sun / moon / black / system buttons) is removed, as requested. The remaining "
  "options are grouped into four labelled cards: Merging, Output, Signing, Apps and updates. Adds the "
  "signing-scheme dropdown, certificate fingerprint text and copy button, and the show-all-apps switch. All "
  "pre-existing ids are preserved."),

 ("app/src/main/res/layout/dropdownitem.xml", "Dropdown row",
  "Text color changed from a hard-coded white to ?attr/colorOnSurface so dropdown rows stay readable in the "
  "light theme."),

 ("app/src/main/res/layout/list_item.xml", "Installed-app list row",
  "Padding switched to start/end 8dp and top/bottom 12dp for a taller touch target; text color bound to "
  "?attr/colorOnSurface and size set to 15sp."),

 ("app/src/main/java/com/abdurazaaqmohammed/utils/SignUtil.java", "Signing engine",
  "Adds explicit signing schemes: SCHEME_AUTO=0, V1_V2_V3=1, V2_V3=2, V3=3, V1_V2=4, V1=5. resolveScheme() maps "
  "AUTO to v2+v3 when minSdkVersion >= 24 and to v1+v2+v3 otherwise, so large APKs are not forced through slow "
  "v1 JAR hashing (#650). detectMinSdk() reads the binary manifest through ZipFile + "
  "ApkUtils.getMinSdkVersionFromBinaryAndroidManifest; on any failure it falls back to minSdk 21 with "
  "v1+v2+v3 instead of aborting (#633). getSigningCertificateSha256() exposes the debug certificate fingerprint "
  "for display (#673)."),

 ("app/src/main/java/com/reandroid/Merger.java", "Merge pipeline",
  "1) No longer deletes android:extractNativeLibs; it is set to true and ApkModule.setExtractNativeLibs(TRUE) "
  "is called, so native libraries are extracted at install time. This fixes the Android 14+ / 16 KB page-size "
  "install failures (#612, #648). 2) Split-metadata resource removal now collects paths first and mutates after "
  "iteration (avoids ConcurrentModificationException on large tables) and only deletes res/xml/* or *.xml "
  "entries, so icons/drawables referenced by the same table survive (#665). 3) Overloads accept a signing "
  "scheme and pass it to SignUtil.signDebugKey. 4) extractZipFileFallback() retries with java.util.zip.ZipFile "
  "when the streaming reader fails with \"Failed to find end record\" (#20)."),

 ("app/src/main/java/com/abdurazaaqmohammed/AntiSplit/main/MainActivity.java", "Main activity",
  "Adds signingScheme/showAllApps state and persists the processing state, log and error across configuration "
  "changes so the merge UI no longer disappears after rotation (#620); onConfigurationChanged re-applies the "
  "progress/cancel state. Cache deletion is guarded by savedInstanceState == null and onDestroy only cleans up "
  "when isFinishing() && !isChangingConfigurations(). Adds getRawFileName() (display name without extension "
  "rewriting) so .apk-vs-bundle detection is correct (#624) and multi-select works (#609); the open intent now "
  "passes EXTRA_MIME_TYPES so .apkm is selectable (#509). sanitizeFileName() strips apkmirror.com-style "
  "prefixes and illegal characters (#387). showError(Throwable) no longer calls uris.remove(0) on an empty "
  "list. Settings wiring for the scheme dropdown, fingerprint display/copy and show-all-apps filter is added."),

 ("app/src/main/java/com/abdurazaaqmohammed/utils/RunUtil.java", "Background task runner",
  "Both async paths now call executor.shutdown() in a finally block, and reportFailure() unwraps "
  "ExecutionException so an OutOfMemoryError is detected and reported with a friendly message instead of a "
  "generic crash (#653, #143)."),

 ("app/src/main/java/com/abdurazaaqmohammed/utils/UpdateUtil.java", "Update checker",
  "Parses the GitHub releases JSON with org.json and compares versions numerically, so tags such as "
  "\"v2.3.0-beta\" no longer break update detection (#628). Downloaded file is named "
  "APK-AntiSpliter.v<version>.<ext> and changelog newlines are normalized correctly."),

 ("app/src/main/java/com/starry/FileUtils.java", "SAF helper",
  "getDataColumn() uses a null projection with getColumnIndex() plus a fallback, so content providers that do "
  "not expose the legacy _data column no longer throw (#634)."),

 ("README.md", "Project documentation",
  "Rewritten for APK-AntiSpliter: removes the old title, the developer profile / GitHub links and the "
  "third-party app promotion, and documents the new features, signing schemes and permissions."),
]

LOCALE_NOTE = """The following 19 translation files were updated so the app name reads APK-AntiSpliter
instead of the old name. New strings fall back to the English defaults in `values/strings.xml`,
which is the standard Android behaviour and keeps the app fully usable in every locale:

"""

def read(p):
    with io.open(p, encoding="utf-8") as f:
        return f.read()

out = []
w = out.append

w("# APK-AntiSpliter — Change Report\n")
w("This report lists every file that was changed, explains why, and then reproduces the full\n")
w("current content of each file exactly as it exists in the project. Nothing here is\n")
w("paraphrased: the content sections are read directly from the files.\n")

w("\n## 1. File list\n\n")
w("| # | File | Role |\n|---|---|---|\n")
for i,(p,role,_) in enumerate(FILES,1):
    w("| %d | `%s` | %s |\n" % (i,p,role))
w("| %d | `app/src/main/res/values-*/strings.xml` | 19 locale files (app name) |\n" % (len(FILES)+1))

w("\n## 2. What changed and why\n\n")
for p,role,expl in FILES:
    w("### `%s`\n\n%s\n\n" % (p,expl))
w("### Locale strings\n\n" + LOCALE_NOTE)
for d in sorted(os.listdir("app/src/main/res")):
    if d.startswith("values-") and os.path.isfile(os.path.join("app/src/main/res",d,"strings.xml")):
        w("- `app/src/main/res/%s/strings.xml`\n" % d)

w("\n## 3. Issue coverage\n\n")
w("""| Issue | Summary | Where it is addressed |
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
""")

w("\n## 4. Full file contents\n\n")
for p,_,_ in FILES:
    ext = os.path.splitext(p)[1].lstrip(".") or "text"
    w("### `%s`\n\n```%s\n%s\n```\n\n" % (p, ext, read(p).rstrip("\n")))

with io.open("CHANGES.md","w",encoding="utf-8") as f:
    f.write("".join(out))
print("wrote CHANGES.md", os.path.getsize("CHANGES.md"), "bytes")
