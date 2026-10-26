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
