package com.bangbangcoding.screenmirror.web.ui.studio.adapter;//package com.highsecure.videodownloader.ui.studio.adapter;
//
//import android.content.Context;
//import android.content.res.ColorStateList;
//import android.graphics.Bitmap;
//import android.graphics.Typeface;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.TextUtils;
//import android.text.format.Formatter;
//import android.text.style.TextAppearanceSpan;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.drawable.RoundedBitmapDrawable;
//import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.ImageViewTarget;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Locale;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class VideoStudioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private static final int CONTENT = 1;
//    private static final int ADS = 2;
//
//
//    private Context mContext;
//    private DownloadManager mDownloadManager;
//
//    private List<VideoDetail> mVideoDetails = new ArrayList<>();
//
//    private ArrayList<VideoDetail> mVideoDetailsClone = new ArrayList<>();
//    private ArrayList<VideoDetail> mSelectedItems = new ArrayList<>();
//    private SingleSelectionItemClickListener<VideoDetail> mOnItemClickListener;
//    private SingleSelectionItemLongClickListener<VideoDetail> mOnItemLongClickListener;
//    private OnMoreClickListener<VideoDetail> mOnMoreClickListener;
//    private OnItemCheckedChangeListener<VideoDetail> mOnItemCheckedChangeListener;
//
//    private String mConstraint;
//
//    private boolean isActionMode;
//
//    public VideoStudioAdapter(@NonNull Context context, @NonNull List<VideoDetail> videoDetails) {
//        mContext = context;
//        mVideoDetailsClone.addAll(videoDetails);
//        mVideoDetails.addAll(videoDetails);
//    }
//
//    public VideoStudioAdapter(@NonNull Context context) {
//        mContext = context;
//        mDownloadManager = DownloadManager.getInstance();
//        mDownloadManager.init(mContext);
//        sortImages();
//    }
//
//    private void sortImages() {
//        Collections.sort(mVideoDetails, (o1, o2) -> {
//            if (o1.getId() != Integer.MIN_VALUE && o2.getId() != Integer.MIN_VALUE) {
//                String name1 = o1.getTitle();
//                String name2 = o2.getTitle();
//                File f1 = new File(o1.getPath());
//                File f2 = new File(o2.getPath());
//                switch (SortUtils.getSortType(MyApplication.getAppInstance())) {
//                    case R.id.rb_sort_by_name:
//                        return SortUtils.compare(MyApplication.getAppInstance(), name1, name2);
//                    case R.id.rb_sort_by_last_modified:
//                        int csblm = SortUtils.compare(MyApplication.getAppInstance(), f1.lastModified(), f2.lastModified());
//                        if (csblm != 0) {
//                            return csblm;
//                        }
//                        break;
//                }
//
//                return name1.compareToIgnoreCase(name2);
//            }
//            return o1.compareTo(o2);
//        });
//    }
//
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        if (viewType == CONTENT) {
//            return new VideoStudioViewHolder(inflater.inflate(R.layout.item_studio_video, parent, false));
//        }
//        return new AdsHolder(inflater.inflate(R.layout.item_natvive_banner, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof VideoStudioViewHolder) {
//            VideoStudioViewHolder vHolder = (VideoStudioViewHolder) holder;
//            final VideoDetail videoDetail = getItem(position);
//            GlideApp.with(mContext)
//                    .asBitmap()
//                    .apply(new RequestOptions()
//                            .centerCrop()
//                            .error(R.drawable.ic_thumb_d)
//                            .placeholder(R.drawable.ic_thumb_d))
//                    .skipMemoryCache(true)
//                    .load(videoDetail.getPath())
//                    .into(new ImageViewTarget<Bitmap>(vHolder.videoCover) {
//                        @Override
//                        protected void setResource(Bitmap resource) {
//                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
//                            roundedBitmapDrawable.setCornerRadius(NvpUtils.dpToPixels(mContext, 2));
//                            vHolder.videoCover.setImageDrawable(roundedBitmapDrawable);
//                        }
//                    });
//
//            File videoFile = new File(videoDetail.getPath());
//            vHolder.videoTitle.setText(getSpannable(videoDetail.getTitle()));
//            vHolder.videoDate.setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(videoFile.lastModified()));
//            vHolder.videoSizeDuration.setText(String.format(Locale.getDefault(), "%1$s  %2$s", Formatter.formatFileSize(mContext, videoFile.length()), NvpUtils.formatTimeDuration(videoDetail.getDuration())));
//            holder.itemView.setOnClickListener(view -> {
//                if (mOnItemClickListener != null) {
//                    mOnItemClickListener.onItemClick(view, videoDetail);
//                }
//            });
//            holder.itemView.setOnLongClickListener(view -> mOnItemLongClickListener != null && mOnItemLongClickListener.onItemLongClick(view, videoDetail));
//
//
//            if (isActionMode) {
//                vHolder.more.setVisibility(View.GONE);
//                vHolder.checkBox.setVisibility(View.VISIBLE);
//
//                vHolder.checkBox.setOnCheckedChangeListener(null);
//                vHolder.checkBox.setChecked(mSelectedItems.contains(videoDetail));
//
//                vHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                    addToSelectedList(videoDetail, isChecked);
//                    if (mOnItemCheckedChangeListener != null) {
//                        mOnItemCheckedChangeListener.onItemCheckedChanged(buttonView, isChecked, videoDetail);
//                    }
//                });
//            } else {
//                vHolder.checkBox.setVisibility(View.GONE);
//                vHolder.more.setVisibility(View.VISIBLE);
//                vHolder.more.setOnClickListener(view -> {
//                    if (mOnMoreClickListener != null) {
//                        mOnMoreClickListener.onMoreClick(view, videoDetail);
//                    }
//                });
//            }
//        }
//    }
//
//
//    @Override
//    public int getItemViewType(int position) {
//        if (getItem(position).getId() == Integer.MIN_VALUE) {
//            return ADS;
//        }
//        return CONTENT;
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return mVideoDetails.size();
//    }
//
//    public VideoDetail getItem(int position) {
//        return mVideoDetails.get(position);
//    }
//
//    public boolean isEmpty() {
//        return getItemCount() == 0;
//    }
//
//    private Spannable getSpannable(String s) {
//        Spannable spannable = new SpannableString(s);
//
//        if (!TextUtils.isEmpty(mConstraint)) {
//            int index = s.toUpperCase().lastIndexOf(mConstraint.toUpperCase());
//            if (index != -1) {
//                ColorStateList color = new ColorStateList(new int[][]{new int[]{}}, new int[]{ContextCompat.getColor(mContext, R.color.colorAccent)});
//                TextAppearanceSpan highlight = new TextAppearanceSpan(null, Typeface.BOLD, -1, color, null);
//                spannable.setSpan(highlight, index, index + mConstraint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }
//
//        return spannable;
//    }
//
//    public void filter(String newText) {
//        mConstraint = newText;
////        mVideoDetails.beginBatchedUpdates();
//        mVideoDetails.clear();
//        if (!TextUtils.isEmpty(newText)) {
//            for (VideoDetail videoDetail : mVideoDetailsClone) {
//                if (videoDetail.getTitle().toUpperCase().contains(newText.toUpperCase())) {
//                    mVideoDetails.add(videoDetail);
//                }
//            }
//        }
////        mVideoDetails.endBatchedUpdates();
//    }
//
//    public void setOnItemClickListener(SingleSelectionItemClickListener<VideoDetail> onItemClickListener) {
//        mOnItemClickListener = onItemClickListener;
//    }
//
//    public void setOnItemLongClickListener(SingleSelectionItemLongClickListener<VideoDetail> onItemLongClickListener) {
//        mOnItemLongClickListener = onItemLongClickListener;
//    }
//
//    public void setOnMoreClickListener(OnMoreClickListener<VideoDetail> onMoreClickListener) {
//        mOnMoreClickListener = onMoreClickListener;
//    }
//
//    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener<VideoDetail> onItemCheckedChangeListener) {
//        mOnItemCheckedChangeListener = onItemCheckedChangeListener;
//    }
//
//    public void add(ArrayList<VideoDetail> videoDetails) {
////        mVideoDetails.beginBatchedUpdates();
//        mVideoDetails.addAll(videoDetails);
////        mVideoDetails.endBatchedUpdates();
//        mVideoDetailsClone.addAll(videoDetails);
//    }
//
//    public void remove(ArrayList<VideoDetail> videoDetails) {
////        mVideoDetails.beginBatchedUpdates();
//        for (VideoDetail videoDetail : videoDetails) {
//            mVideoDetails.remove(videoDetail);
//        }
////        mVideoDetails.endBatchedUpdates();
//        mVideoDetailsClone.removeAll(videoDetails);
//    }
//
//    public void add(VideoDetail videoDetail) {
//        mVideoDetails.add(videoDetail);
//        mVideoDetailsClone.add(videoDetail);
//    }
//
//    public void remove(VideoDetail videoDetail) {
//        mVideoDetails.remove(videoDetail);
//        mVideoDetailsClone.remove(videoDetail);
//    }
//
//    public int positionOf(VideoDetail videoDetail) {
//        int size = mVideoDetails.size();
//        for (int i = 0; i < size; i++) {
//            if (TextUtils.equals(videoDetail.getPath(), mVideoDetails.get(i).getPath())) return i;
//        }
//        return -1;
//    }
//
//    private void addToSelectedList(int position, boolean isChecked) {
//        if (isChecked) {
//            mSelectedItems.add(getItem(position));
//        } else {
//            mSelectedItems.remove(getItem(position));
//        }
//    }
//
//    private void addToSelectedList(VideoDetail VideoDetail, boolean isChecked) {
//        int position = positionOf(VideoDetail);
//        if (position == -1) return;
//        addToSelectedList(position, isChecked);
//    }
//
//    public void setChecked(int position, boolean isChecked) {
//        addToSelectedList(position, isChecked);
//        notifyItemChanged(position);
//    }
//
//    public void setChecked(VideoDetail VideoDetail, boolean isChecked) {
//        int position = positionOf(VideoDetail);
//        if (position == -1) return;
//        setChecked(position, isChecked);
//    }
//
//    public boolean isChecked(int position) {
//        return mSelectedItems.contains(getItem(position));
//    }
//
//    public boolean isChecked(VideoDetail VideoDetail) {
//        int position = positionOf(VideoDetail);
//        if (position == -1) return false;
//        return isChecked(positionOf(VideoDetail));
//    }
//
//    public void setActionMode(boolean isActionMode) {
//        this.isActionMode = isActionMode;
//        notifyDataSetChanged();
//    }
//
//    public void updateSelectAll() {
//        setSelectAll(mSelectedItems.size() < mVideoDetails.size());
//    }
//
//    public void setSelectAll(boolean selectAll) {
//        mSelectedItems.clear();
//        if (selectAll) {
//            mSelectedItems.addAll(mVideoDetailsClone);
//        }
//        notifyDataSetChanged();
//    }
//
//    public List<VideoDetail> getSelectedItems() {
//        return mSelectedItems;
//    }
//
//    public void removeSelectedItems() {
//        for (VideoDetail VideoDetail : mSelectedItems) {
//            remove(VideoDetail);
//        }
//        mSelectedItems.clear();
//    }
//
//    public void setVideoDetails(List<VideoDetail> videoDetails) {
//        mVideoDetails.clear();
//        mVideoDetailsClone.clear();
//        mVideoDetailsClone.addAll(videoDetails);
//        mVideoDetails.addAll(videoDetails);
//        sortImages();
//        notifyDataSetChanged();
//    }
//
//
//    class VideoStudioViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.iv_cover)
//        ImageView videoCover;
//        @BindView(R.id.tv_video_title)
//        TextView videoTitle;
//        @BindView(R.id.tv_video_date)
//        TextView videoDate;
//        @BindView(R.id.tv_video_size_duration)
//        TextView videoSizeDuration;
//        @BindView(R.id.checkbox)
//        CheckBox checkBox;
//        @BindView(R.id.ib_more)
//        ImageView more;
//
//        public VideoStudioViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//    static class AdsHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.native_ads_banner)
//        FrameLayout nativeBanner;
//
//        AdsHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//}
