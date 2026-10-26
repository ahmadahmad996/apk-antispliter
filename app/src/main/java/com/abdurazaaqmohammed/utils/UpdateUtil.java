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
