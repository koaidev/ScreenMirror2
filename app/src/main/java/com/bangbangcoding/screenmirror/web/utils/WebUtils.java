package com.bangbangcoding.screenmirror.web.utils;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.bangbangcoding.screenmirror.web.data.history.HistoryRepository;

import io.reactivex.Scheduler;

public final class WebUtils {

    private WebUtils() {}

    public static void clearCookies() {
        CookieManager c = CookieManager.getInstance();
        c.removeAllCookies(null);
    }

    public static void clearWebStorage() {
        WebStorage.getInstance().deleteAllData();
    }

    public static void clearHistory(@NonNull Context context,
                                    @NonNull HistoryRepository historyRepository,
                                    @NonNull Scheduler databaseScheduler) {
        historyRepository.deleteHistory()
            .subscribeOn(databaseScheduler)
            .subscribe();
        WebViewDatabase webViewDatabase = WebViewDatabase.getInstance(context);
        webViewDatabase.clearFormData();
        webViewDatabase.clearHttpAuthUsernamePassword();
        Utils.trimCache(context);
    }

    public static void clearCache(@Nullable WebView view) {
        if (view == null) return;
        view.clearCache(true);
    }

}
