package com.bangbangcoding.screenmirror.web.ui.studio.adapter;//package com.highsecure.videodownloader.ui.studio.adapter;
//
//import android.app.Activity;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.webkit.URLUtil;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.highsecure.videodownloader.R;
//import com.highsecure.videodownloader.ui.service.DownloadService;
//import com.highsecure.videodownloader.ui.studio.model.DownloadItem;
//import com.highsecure.videodownloader.ui.studio.multidownload.DownloadInfo;
//import com.highsecure.videodownloader.ui.studio.multidownload.DownloadManager;
//import com.highsecure.videodownloader.utils.PrefUtils;
//import com.highsecure.videodownloader.utils.ad.AdsUtilsSplash;
//import com.highsecure.videodownloader.ui.view.customview.DownloadView;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class DownloadAdapter extends ListAdapter<DownloadAdapter.ViewHolder, DownloadItem> implements OnClickListener {
//    private Context mContext;
//    private FragmentActivity mFragment;
//    private DownloadManager mDownloadManager;
//    private TextView txtYes;
//    private TextView txtNo;
//    private AlertDialog mDialog;
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.overflow_view)
//        ImageButton mCancelButton;
//        @BindView(R.id.btnDownload)
//        ImageButton mDownloadButton;
//        @BindView(R.id.card)
//        DownloadView mDownloadView;
//        @BindView(R.id.progressBar)
//        ProgressBar mProgressBar;
//        @BindView(R.id.tvDownloadPerSize)
//        TextView mTvDownloadPerSize;
//        @BindView(R.id.tvName)
//        TextView mTvName;
//
//        public ViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//
//    public DownloadAdapter(FragmentActivity fragment, Context mContext) {
//        this.mFragment = fragment;
//        this.mContext = mContext;
//        mDownloadManager = DownloadManager.getInstance();
//        mDownloadManager.init(mContext);
//        initDialogAd();
//    }
//
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_download, parent, false);
//        view.setOnClickListener(this);
//        final ViewHolder viewHolder = new ViewHolder(view);
//        viewHolder.mCancelButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                mDialog.show();
//                txtYes.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        DownloadItem downloadItem = (DownloadItem) viewHolder.mDownloadView.getTag();
//                        File downloadDir = new File(PrefUtils.getExternalFilesDirName(mContext.getApplicationContext()));
//                        String fileName = URLUtil.guessFileName(downloadItem.getUrl(), null, null);
//                        File fileDelete = new File(downloadDir, fileName + "downloading");
//                        fileDelete.delete();
//                        remove(downloadItem);
//                        NotificationManager nMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//                        nMgr.cancel(downloadItem.getUrl().hashCode());
//                        DownloadService.cancelDownload(mContext, downloadItem);
//                        mDownloadManager.delete(downloadItem.getUrl());
//
//                        List<DownloadInfo> downloadInfos = mDownloadManager.getAllDownloads();
//                        List<DownloadItem> downloadItems = new ArrayList<>();
//                        for (DownloadInfo downloadInfo : downloadInfos) {
//                            downloadItems.add(new DownloadItem(downloadInfo));
//                        }
//                        if (downloadItems.size() <= 0) {
//                            DownloadService.intentStopServiceDownload(mContext);
//                        }
//                        mDialog.dismiss();
//                    }
//                });
//                txtNo.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mDialog.dismiss();
//                    }
//                });
//            }
//        });
//
//        return viewHolder;
//    }
//
//    @Override
//    public void bindView(ViewHolder holder, DownloadItem model) {
//        holder.mDownloadView.setDownloadItem(model);
//        holder.mDownloadView.setTag(model);
//    }
//
//    private void initDialogAd() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_delete_download, null);
//        builder.setView(view);
//        FrameLayout nativeAds = view.findViewById(R.id.nativeAds);
//        AdsUtilsSplash.getSharedInstance().loadNativeAd((Activity) mContext, nativeAds);
//        txtYes = view.findViewById(R.id.txtYes);
//        txtNo = view.findViewById(R.id.txtNo);
//        mDialog = builder.create();
//    }
//}
