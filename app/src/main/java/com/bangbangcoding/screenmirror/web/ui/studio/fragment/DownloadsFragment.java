package com.bangbangcoding.screenmirror.web.ui.studio.fragment;//package com.highsecure.videodownloader.ui.studio.fragment;
//
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//
//import com.highsecure.videodownloader.ui.studio.adapter.DownloadAdapter;
//import com.highsecure.videodownloader.ui.studio.adapter.ListAdapter;
//import com.highsecure.videodownloader.ui.studio.model.DownloadItem;
//import com.highsecure.videodownloader.ui.studio.multidownload.DownloadInfo;
//import com.highsecure.videodownloader.ui.studio.multidownload.DownloadManager;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import rx.Observable;
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
//public class DownloadsFragment extends BaseFragment<DownloadAdapter.ViewHolder, DownloadItem> {
//    private DownloadManager mDownloadManager;
//
//
//    public static DownloadsFragment newInstance() {
//        DownloadsFragment fragment = new DownloadsFragment();
//        fragment.setArguments(new Bundle());
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(false);
//    }
//
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        menu.clear();
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mDownloadManager = DownloadManager.getInstance();
//        mDownloadManager.init(getContext());
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }
//
//    @Override
//    public void onStart() {
//        EventBus.getDefault().register(this);
//        super.onStart();
//    }
//
//    @Override
//    public void onStop() {
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//    }
//
//    @Override
//    public void fetchData() {
//        mSubscription.add(Observable.defer(() -> Observable.create((Observable.OnSubscribe<List<DownloadItem>>) subscriber -> {
//            try {
//                List<DownloadInfo> downloadInfos = mDownloadManager.getAllDownloads();
//                List<DownloadItem> downloadItems = new ArrayList<>();
//                for (DownloadInfo downloadInfo : downloadInfos) {
//                    downloadItems.add(new DownloadItem(downloadInfo));
//                }
//                subscriber.onNext(downloadItems);
//                subscriber.onCompleted();
//            } catch (Exception e) {
//                subscriber.onError(e);
//            }
//        })).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<DownloadItem>>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onNext(List<DownloadItem> downloadItems) {
//                tvEmpty.setVisibility(downloadItems.size() > 0 ? View.GONE : View.VISIBLE);
//                mAdapter.addAll(downloadItems);
//            }
//        }));
//    }
//
//    @Override
//    protected ListAdapter<DownloadAdapter.ViewHolder, DownloadItem> getAdapter() {
//        return new DownloadAdapter(getActivity(), getActivity());
//    }
//
//    @Override
//    protected void onDataItemClicked(View view, DownloadItem downloadItem, int i) {
//
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onUpdateView(Integer pos) {
//        fetchData();
//    }
//}
