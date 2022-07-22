package com.bangbangcoding.screenmirror.easybrowser.page.browser;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.bangbangcoding.screenmirror.easybrowser.page.history.HistoryActivity;
import com.bangbangcoding.screenmirror.easybrowser.page.setting.SettingDialogKt;
import com.bangbangcoding.screenmirror.easybrowser.page.tabpreview.TabDialogKt;


import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.easybrowser.EasyApplication;
import com.bangbangcoding.screenmirror.easybrowser.common.BrowserConst;
import com.bangbangcoding.screenmirror.easybrowser.common.Const;
import com.bangbangcoding.screenmirror.easybrowser.common.TabConst;
import com.bangbangcoding.screenmirror.easybrowser.contract.IBrowser;
import com.bangbangcoding.screenmirror.easybrowser.contract.ITab;
import com.bangbangcoding.screenmirror.easybrowser.contract.IWebView;
import com.bangbangcoding.screenmirror.easybrowser.entity.bo.ClickInfo;
import com.bangbangcoding.screenmirror.easybrowser.entity.bo.TabInfo;
import com.bangbangcoding.screenmirror.easybrowser.entity.dao.AppDatabase;
import com.bangbangcoding.screenmirror.easybrowser.entity.dao.History;
import com.bangbangcoding.screenmirror.easybrowser.page.address.AddressDialog;
import com.bangbangcoding.screenmirror.easybrowser.utils.FragmentBackHandleHelper;
import com.bangbangcoding.screenmirror.easybrowser.utils.TabHelper;


public class BrowserActivity extends AppCompatActivity implements IWebView.OnWebInteractListener,
        IBrowser {

    private static final String TAG = "BrowserActivity";

    private static final String SETTING_DIALOG_TAG = "setting_dialog";
    private static final String TAB_DIALOG_TAG = "tab_dialog";
    private static final String ADDRESS_DIALOG_TAG = "address_dialog";

    FrameLayout webContentFrame;

    INavController navController;
    IHistoryController historyController;
    ITabController tabController;
    IBookmarkController bookmarkController;
    IDownloadController downloadController;
    IComponent stubComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        getTabController();

        webContentFrame = findViewById(R.id.web_content_frame);

        if (savedInstanceState == null) {

            TabInfo tabInfo = TabInfo.create(
                    System.currentTimeMillis() + "",
                    getString(R.string.new_tab_welcome));
            getTabController().onTabCreate(tabInfo, false);
        } else {
            Fragment prevDialog = getSupportFragmentManager().findFragmentByTag(TAB_DIALOG_TAG);
            if (prevDialog instanceof TabDialogKt) {
                ((TabDialogKt) prevDialog).setTabViewSubject(getTabController());
                ((TabDialogKt) prevDialog).dismiss();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        ArrayList<TabInfo> restoreList = savedInstanceState.getParcelableArrayList("tablist");
        if (restoreList == null) {
            return;
        }

        getTabController().provideInfoList().addAll(restoreList);
        List<Fragment> restoredFragmentList = getSupportFragmentManager().getFragments();
        if (restoredFragmentList.size() > 0) {
            for (Fragment target : restoredFragmentList) {
                if (target instanceof ITab && target.getArguments() != null) {
                    // 根据Fragment参数，还原TabInfo信息用于列表中查找
                    TabInfo info = TabInfo.create(
                            target.getArguments().getString(TabConst.ARG_TAG),
                            target.getArguments().getString(TabConst.ARG_TITLE));
                    getTabController().onRestoreTabCache(info, target);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<TabInfo> storeList = new ArrayList<>();
        storeList.addAll(getTabController().provideInfoList());
        outState.putParcelableArrayList("tablist", storeList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tabController != null) {
            tabController.onCloseAllTabs();
            tabController.detach();
            tabController.onDestroy();
            tabController = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onPageTitleChange(TabInfo tabInfo) {
        getTabController().updateTabInfo(tabInfo);
    }

    @Override
    public void onLongClick(ClickInfo clickInfo) {
        if (clickInfo == null) {
            return;
        }

        switch (clickInfo.type) {
            case WebView.HitTestResult.IMAGE_TYPE:
                showImageActionDialog(clickInfo);
                break;
            case WebView.HitTestResult.SRC_ANCHOR_TYPE:
            case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                showUrlActionDialog(clickInfo);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (FragmentBackHandleHelper.isFragmentBackHandled(getSupportFragmentManager())) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Const.RequestCode.SHOW_HISTORY) {
            showHistoryResult(resultCode, data);
        }
    }

    private void showHistoryResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        TabInfo info = data.getParcelableExtra(Const.Key.TAB_INFO);
        if (info == null) {
            return;
        }

        getTabController().onTabCreate(info, false);
    }

    private void showTabDialog() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag(TAB_DIALOG_TAG);
        if (prev != null) {
            ((TabDialogKt) prev).dismiss();
            return;
        }

        TabDialogKt tabDialog = new TabDialogKt();
        tabDialog.setCancelable(false);

        tabDialog.setTabViewSubject(getTabController());
        tabDialog.show(getSupportFragmentManager(), TAB_DIALOG_TAG);
    }

    private void showSettingDialog() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag(SETTING_DIALOG_TAG);
        if (prev != null) {
            ((SettingDialogKt) prev).dismiss();
            return;
        }

        SettingDialogKt settingDialog = new SettingDialogKt();
        settingDialog.setCancelable(false);

        settingDialog.show(getSupportFragmentManager(), SETTING_DIALOG_TAG);
    }

    private void showAddressDialog(String currentUrl) {
        Fragment prev = getSupportFragmentManager().findFragmentByTag(ADDRESS_DIALOG_TAG);
        if (prev != null) {
            ((AddressDialog) prev).dismiss();
            return;
        }

        AddressDialog addressDialog = new AddressDialog();
        addressDialog.setCurrentUrl(currentUrl);
        addressDialog.show(getSupportFragmentManager(), ADDRESS_DIALOG_TAG);
    }

    private void showImageActionDialog(@NonNull final ClickInfo clickInfo) {
        AlertDialog.Builder imageDialogbuilder = new AlertDialog.Builder(this);
        imageDialogbuilder.setItems(R.array.image_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == TabConst.TAB_OPEN_ACTION_BACKSTAGE) {
                    TabHelper.createTab(BrowserActivity.this,
                            R.string.new_tab_welcome,
                            clickInfo.url,
                            true);
                } else if (which == TabConst.TAB_OPEN_ACTION_FRONTSTAGE) {
                    TabHelper.createTab(BrowserActivity.this,
                            R.string.new_tab_welcome,
                            clickInfo.url,
                            false);
                }
            }
        });
        imageDialogbuilder.show();
    }

    private void showUrlActionDialog(@NonNull final ClickInfo clickInfo) {
        AlertDialog.Builder urlDialogbuilder = new AlertDialog.Builder(this);
        urlDialogbuilder.setItems(R.array.url_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == TabConst.TAB_OPEN_ACTION_BACKSTAGE) {
                    TabHelper.createTab(BrowserActivity.this,
                            R.string.new_tab_welcome,
                            clickInfo.url,
                            true);
                } else if (which == TabConst.TAB_OPEN_ACTION_FRONTSTAGE) {
                    TabHelper.createTab(BrowserActivity.this,
                            R.string.new_tab_welcome,
                            clickInfo.url,
                            false);
                }
            }
        });
        urlDialogbuilder.show();
    }

    @UiThread
    @NonNull
    @Override
    public IComponent provideBrowserComponent(String componentName) {
        synchronized (this) {
            if (TextUtils.isEmpty(componentName)) {
                return getStubComponent();
            }

            switch (componentName) {
                case BrowserConst.BOOKMARK_COMPONENT:
                    return getBookmarkController();
                case BrowserConst.DOWNLOAD_COMPONENT:
                    return getDownloadController();
                case BrowserConst.HISTORY_COMPONENT:
                    return getHistoryController();
                case BrowserConst.NAVIGATION_COMPONENT:
                    return getNavController();
                case BrowserConst.TAB_COMPONENT:
                    return getTabController();
                default:
                    return getStubComponent();
            }
        }
    }

    private IComponent getStubComponent() {
        if (stubComponent == null) {
            stubComponent = new StubBrowserComponent();
        }
        return stubComponent;
    }

    private IBookmarkController getBookmarkController() {
        if (bookmarkController == null) {
            bookmarkController = new StubBookmarkController();
        }
        return bookmarkController;
    }

    private IDownloadController getDownloadController() {
        if (downloadController == null) {
            downloadController = new StubDownloadController();
        }
        return downloadController;
    }

    private IHistoryController getHistoryController() {
        if (historyController == null) {
            historyController = new EasyHistoryController();
        }
        return historyController;
    }

    private INavController getNavController() {
        if (navController == null) {
            navController = new EasyNavController();
        }
        return navController;
    }

    private ITabController getTabController() {
        if (tabController == null) {
            tabController = new TabCacheManager(this, getSupportFragmentManager(), 3, R.id.web_content_frame);
        }
        return tabController;
    }

    class EasyNavController implements INavController {
        @Override
        public void goBack() {
            onBackPressed();
        }

        @Override
        public void goForward() {
            getTabController().onTabGoForward();
        }

        @Override
        public void goHome() {
            getTabController().onTabGoHome();
        }

        @Override
        public void showTabs() {
            showTabDialog();
        }

        @Override
        public void showAddress(String currentUrl) {
            showAddressDialog(currentUrl);
        }

        @Override
        public void showSetting() {
            showSettingDialog();
        }

        @Override
        public void showHistory() {
            Intent intent = new Intent();
            intent.setClass(BrowserActivity.this, HistoryActivity.class);
            startActivityForResult(intent, Const.RequestCode.SHOW_HISTORY);
        }
    }

    class EasyHistoryController implements IHistoryController {
        @Override
        public void addHistory(final History entity) {
            Disposable dps = Observable.create(new ObservableOnSubscribe<Long>() {

                @Override
                public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                    final EasyApplication application = (EasyApplication) getApplicationContext();
                    AppDatabase db = application.getAppDatabase();
                    long rowId = db.historyDao().insertHistory(entity);
                    Log.i(TAG, "inserted id    is : " + rowId);
                    Log.i(TAG, "inserted title is : " + entity.title);
                    Log.i(TAG, "inserted url   is : " + entity.url);
                    emitter.onNext(rowId);
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    static class StubDownloadController implements IDownloadController {
    }

    static class StubBookmarkController implements IBookmarkController {
    }

    static class StubBrowserComponent implements IComponent {
    }
}
