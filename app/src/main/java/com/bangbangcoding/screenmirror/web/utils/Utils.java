/*
 * Copyright 2014 A.C.R. Development
 */
package com.bangbangcoding.screenmirror.web.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class Utils {

    private static final DecimalFormat DF = new DecimalFormat("0.00");
    public static final int FORMAT_AUTO = 1;
    public static final int FORMAT_LONG = 2;

    private static final String TAG = "Utils";

    private Utils() {
    }

    public static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static String getSizeUrl(long total) {
        return DF.format((double) (((float) total) / 1048576.0f)) + "MB";
    }

    /**
     * Creates a new intent that can launch the email
     * app with a subject, address, body, and cc. It
     * is used to handle mail:to links.
     *
     * @param address the address to send the email to.
     * @param subject the subject of the email.
     * @param body    the body of the email.
     * @param cc      extra addresses to CC.
     * @return a valid intent.
     */
    @NonNull
    public static Intent newEmailIntent(String address, String subject,
                                        String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

//    /**
//     * Creates a dialog with only a title, message, and okay button.
//     *
//     * @param activity the activity needed to create a dialog.
//     * @param title    the title of the dialog.
//     * @param message  the message of the dialog.
//     */
//    public static void createInformativeDialog(@NonNull Activity activity, @StringRes int title, @StringRes int message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle(title);
//        builder.setMessage(message)
//            .setCancelable(true)
//            .setPositiveButton(activity.getResources().getString(R.string.action_ok),
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//        BrowserDialog.setDialogSize(activity, alert);
//    }
//

    /**
     * Converts Density Pixels (DP) to Pixels (PX).
     *
     * @param dp the number of density pixels to convert.
     * @return the number of pixels that the conversion generates.
     */
    public static int dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density + 0.5f);
    }

    public static boolean needsStoragePermission(Context context) {
        return Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }


    public static long getDurationByMetadata(String path) {
        long duration = 0L;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = convertMillisecond(durationStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return duration;
    }

    public static long convertMillisecond(String duration) {
        if (duration == null) return 0L;
        long timeMillisecond = 0L;
        try {
            timeMillisecond = Long.parseLong(duration);
        } catch (Exception e) {
            Log.e("ttt", "Exception: ", e);
        }
        return timeMillisecond;
    }


    public static void trimCache(@NonNull Context context) {
        try {
            File dir = context.getCacheDir();

            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception ignored) {

        }
    }

    private static boolean deleteDir(@Nullable File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir != null && dir.delete();
    }

    //
//    public static boolean isColorTooDark(int color) {
//        final byte RED_CHANNEL = 16;
//        final byte GREEN_CHANNEL = 8;
//        //final byte BLUE_CHANNEL = 0;
//
//        int r = ((int) ((float) (color >> RED_CHANNEL & 0xff) * 0.3f)) & 0xff;
//        int g = ((int) ((float) (color >> GREEN_CHANNEL & 0xff) * 0.59)) & 0xff;
//        int b = ((int) ((float) (color /* >> BLUE_CHANNEL */ & 0xff) * 0.11)) & 0xff;
//        int gr = (r + g + b) & 0xff;
//        int gray = gr /* << BLUE_CHANNEL */ + (gr << GREEN_CHANNEL) + (gr << RED_CHANNEL);
//
//        return gray < 0x727272;
//    }
//
//    public static int mixTwoColors(int color1, int color2, float amount) {
//        final byte ALPHA_CHANNEL = 24;
//        final byte RED_CHANNEL = 16;
//        final byte GREEN_CHANNEL = 8;
//        //final byte BLUE_CHANNEL = 0;
//
//        final float inverseAmount = 1.0f - amount;
//
//        int r = ((int) (((float) (color1 >> RED_CHANNEL & 0xff) * amount) + ((float) (color2 >> RED_CHANNEL & 0xff) * inverseAmount))) & 0xff;
//        int g = ((int) (((float) (color1 >> GREEN_CHANNEL & 0xff) * amount) + ((float) (color2 >> GREEN_CHANNEL & 0xff) * inverseAmount))) & 0xff;
//        int b = ((int) (((float) (color1 & 0xff) * amount) + ((float) (color2 & 0xff) * inverseAmount))) & 0xff;
//
//        return 0xff << ALPHA_CHANNEL | r << RED_CHANNEL | g << GREEN_CHANNEL | b;
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    public static File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + '_';
//        File storageDir = Environment
//            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        return File.createTempFile(imageFileName, /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        );
//    }
//
//    /**
//     * Checks if flash player is installed
//     *
//     * @param context the context needed to obtain the PackageManager
//     * @return true if flash is installed, false otherwise
//     */
//    public static boolean isFlashInstalled(@NonNull Context context) {
//        try {
//            PackageManager pm = context.getPackageManager();
//            ApplicationInfo ai = pm.getApplicationInfo("com.adobe.flashplayer", 0);
//            if (ai != null) {
//                return true;
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }
//        return false;
//    }
//
//    /**
//     * Quietly closes a closeable object like an InputStream or OutputStream without
//     * throwing any errors or requiring you do do any checks.
//     *
//     * @param closeable the object to close
//     */
    public static void close(@Nullable Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to close closeable", e);
        }
    }
//
//    /**
//     * Creates a shortcut on the homescreen using the
//     * {@link HistoryEntry} information that opens the
//     * browser. The icon, URL, and title are used in
//     * the creation of the shortcut.
//     *
//     * @param activity the activity needed to create
//     *                 the intent and show a snackbar message
//     * @param historyEntry     the HistoryEntity to create the shortcut from
//     */
//    public static void createShortcut(@NonNull Activity activity,
//                                      @NonNull HistoryEntry historyEntry,
//                                      @NonNull Bitmap favicon) {
//        Intent shortcutIntent = new Intent(Intent.ACTION_VIEW);
//        shortcutIntent.setData(Uri.parse(historyEntry.getUrl()));
//
//        final String title = TextUtils.isEmpty(historyEntry.getTitle()) ? activity.getString(R.string.untitled) : historyEntry.getTitle();
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            Intent addIntent = new Intent();
//            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
//            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, favicon);
//            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//            activity.sendBroadcast(addIntent);
//            ActivityExtensions.snackbar(activity, R.string.message_added_to_homescreen);
//        } else {
//            ShortcutManager shortcutManager = activity.getSystemService(ShortcutManager.class);
//            if (shortcutManager.isRequestPinShortcutSupported()) {
//                ShortcutInfo pinShortcutInfo =
//                    new ShortcutInfo.Builder(activity, "browser-shortcut-" + historyEntry.getUrl().hashCode())
//                        .setIntent(shortcutIntent)
//                        .setIcon(Icon.createWithBitmap(favicon))
//                        .setShortLabel(title)
//                        .build();
//
//                shortcutManager.requestPinShortcut(pinShortcutInfo, null);
//                ActivityExtensions.snackbar(activity, R.string.message_added_to_homescreen);
//            } else {
//                ActivityExtensions.snackbar(activity, R.string.shortcut_message_failed_to_add);
//            }
//        }
//    }

    public static int calculateInSampleSize(@NonNull BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Nullable
    public static String guessFileExtension(@NonNull String filename) {
        int lastIndex = filename.lastIndexOf('.') + 1;
        if (lastIndex > 0 && filename.length() > lastIndex) {
            return filename.substring(lastIndex, filename.length());
        }
        return null;
    }

    public static String getDownloadPerSize(long finished, long total) {
        return DF.format((double) (((float) finished) / 1048576.0f)) + "M/" + DF.format((double) (((float) total) / 1048576.0f)) + "M";
    }

    public static boolean checkWifiOnAndConnected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiMgr != null;
        if (wifiMgr.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            return wifiInfo.getNetworkId() != -1;
        } else {
            return false;
        }
    }

    public static String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return null;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < ((long) unit)) {
            return bytes + " B";
        }
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(((int) (Math.log((double) bytes) / Math.log((double) unit))) - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", ((double) bytes) / Math.pow((double) unit, (double) ((int) (Math.log((double) bytes) / Math.log((double) unit)))), pre);
    }

    public static long getAvailableSpaceInMB(Context context) {
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(PrefUtils.getExternalFilesDirName(context));
        availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        return availableSpace / SIZE_MB;
    }

    public static int dpToPixels(Context context, int dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics));
    }

    public static String formatTimeDuration(long timeMillis, int format) {
        if (timeMillis < 0) {
            timeMillis = 0;
        }

        long hour = TimeUnit.MILLISECONDS.toHours(timeMillis);
        long minute = TimeUnit.MILLISECONDS.toMinutes(timeMillis) - TimeUnit.HOURS.toMinutes(hour);
        long second = TimeUnit.MILLISECONDS.toSeconds(timeMillis) - TimeUnit.HOURS.toSeconds(hour) - TimeUnit.MINUTES.toSeconds(minute);

        if (format == FORMAT_AUTO) {
            return hour > 0 ? String.format(Locale.getDefault(), "%1$02d:%2$02d:%3$02d", hour, minute, second) :
                    String.format(Locale.getDefault(), "%1$02d:%2$02d", minute, second);
        }

        if (format == FORMAT_LONG) {
            return String.format(Locale.getDefault(), "%1$02d:%2$02d:%3$02d", hour, minute, second);
        }

        return String.format(Locale.getDefault(), "%1$02d:%2$02d", minute, second);
    }

    public static String formatTimeDuration(long timeMillis) {
        return formatTimeDuration(timeMillis, FORMAT_AUTO);
    }


    public static File getVideoFolder(Context context) {
        String path = new File(Environment.getExternalStorageDirectory(), "DownloadVideo2019Pro").getAbsolutePath();
        File file = new File(PreferenceManager.getDefaultSharedPreferences(context)
                .getString("f_dir", path));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }


    @SuppressLint("NewApi")
    public static ArrayList<String> getAllFile(Context context) {
        File[] files = context.getExternalFilesDirs(null);
        ArrayList<String> arrayList = new ArrayList<>(files.length + 1);
        for (File file : files) {
            if (file != null && file.exists()) {
                arrayList.add(file.getPath());
            }
        }
        return arrayList;
    }

    //    public static String formatSizeStorage(Context context, String str) {
//        try {
//            StatFs statFs = new StatFs(str);
//            long blockSize = (long) statFs.getBlockSize();
//            long blockCount = (long) statFs.getBlockCount();
//            String freeSize = Formatter.formatFileSize(context, (blockCount - ((long) statFs.getAvailableBlocks())) * blockSize),
//            Formatter.formatFileSize(context, blockSize * blockCount);
//
//            return context.getString(R.string.free, freeSize);
//        } catch (Exception e) {
//            return e.getMessage();
//        }
//    }
    public static void showToast(Context context, String str) {
       Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }


    public static String showCookies(String websiteURL) {

        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            // Access the website
            URL url = new URL(websiteURL);

            URLConnection urlConnection = url.openConnection();
            urlConnection.getContent();

            // Get CookieStore
            CookieStore cookieStore = cookieManager.getCookieStore();

            // Get cookies


            Object[] arr = cookieStore.getCookies().toArray();

            String csrftoken = "" + arr[0].toString();
            // csrftoken = csrftoken.replace("csrftoken=", "");
            String mid = "" + arr[1];
            // mid = mid.replace("mid=", "");

            String ig_did = "" + arr[2];
            //  ig_did = ig_did.replace("ig_did=", "");

            String ig_nrcb = "" + arr[3];
            //  ig_nrcb = ig_nrcb.replace("ig_nrcb=", "");

            System.out.println("working errpr \t Value: " + csrftoken + mid);

            // return csrftoken + "; ds_user_id=24740642071; sessionid=8354837521:IfDmOl5NAeYI8m:18; " + mid + "; " + ig_did + "; " + ig_nrcb;
            return csrftoken + "; ds_user_id=8354837521; sessionid=8354837521:lLvOY6YNZscMVx:15; " + mid + "; " + ig_did + "; " + ig_nrcb;

        } catch (Exception e) {
            System.out.println("working errpr \t Value: " + e.getMessage());
            return "";
        }
    }

    public static String getStringSizeLengthFile(long j) {
        return getStringSizeLengthFile(j, "0.00");
    }

    public static String getStringSizeLengthFile(long j, String pattern) {
        try {
            if (j == 0L) {
                return "";
            }

            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            float f = (float) j;
            if (f < 1048576.0f) {
                return decimalFormat.format((double) (f / 1024.0f)) + " KB";
            } else if (f < 1.07374182E9f) {
                return decimalFormat.format((double) (f / 1048576.0f)) + " MB";
            } else if (f >= 1.09951163E12f) {
                return "";
            } else {
                return decimalFormat.format((double) (f / 1.07374182E9f)) + " GB";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    public static String formatDuration(long j) {
        String str;
        String str2;
        long j2 = (j / 1000) % 60;
        long j3 = (j / 60000) % 60;
        long j4 = j / 3600000;
        StringBuilder sb = new StringBuilder();
        if (j4 == 0) {
            str = "";
        } else if (j4 < 10) {
            str = String.valueOf(0 + j4);
        } else {
            str = String.valueOf(j4);
        }
        sb.append(str);
        if (j4 != 0) {
            sb.append("h");
        }
        String str3 = "00";
        if (j3 == 0) {
            str2 = str3;
        } else if (j3 < 10) {
            str2 = String.valueOf(0 + j3);
        } else {
            str2 = String.valueOf(j3);
        }
        sb.append(str2);
        sb.append("min");
        if (j2 != 0) {
            if (j2 < 10) {
                str3 = String.valueOf(0 + j2);
            } else {
                str3 = String.valueOf(j2);
            }
        }
        sb.append(str3);
        sb.append("s");
        return sb.toString();
    }

    @NonNull
    public static String getDomainName(@Nullable String url) {
        if (url == null || url.isEmpty()) return "";

        boolean ssl = URLUtil.isHttpsUrl(url);
        int index = url.indexOf('/', 8);
        if (index != -1) {
            url = url.substring(0, index);
        }

        URI uri;
        String domain;
        try {
            uri = new URI(url);
            domain = uri.getHost();
        } catch (URISyntaxException e) {
            Log.e(TAG, "Unable to parse URI", e);
            domain = null;
        }

        if (domain == null || domain.isEmpty()) {
            return url;
        }
        if (ssl)
            return Constants.HTTPS + domain;
        else
            return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static void openApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }
}
