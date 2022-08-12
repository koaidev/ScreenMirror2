package com.bangbangcoding.screenmirror.web.ui.studio.fragment;//package com.highsecure.videodownloader.ui.studio.fragment;
//
//
//import android.content.ContentValues;
//import android.content.Intent;
//import android.graphics.PorterDuff;
//import android.media.MediaScannerConnection;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.text.format.Formatter;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CompoundButton;
//import android.widget.FrameLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.view.ActionMode;
//import androidx.appcompat.widget.PopupMenu;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
//
//public class VideosFragment extends Fragment implements SingleSelectionItemClickListener<VideoDetail>,
//        SingleSelectionItemLongClickListener<VideoDetail>, OnItemCheckedChangeListener<VideoDetail>,
//        OnMoreClickListener<VideoDetail>, LoadCallback<List<VideoDetail>>, ActionMode.Callback {
//
//
//    @BindView(R.id.progress_bar)
//    ProgressBar progressBar;
//    @BindView(R.id.tv_empty)
//    TextView tvEmpty;
//    @BindView(R.id.rv_my_studio_list)
//    RecyclerView rvMyStudioList;
//    @BindView(R.id.nativeAdsDownload)
//    FrameLayout nativeAds;
//    TextView txtYes;
//    TextView txtNo;
//    TextView txtTitle;
//
//    private DownloadManager mDownloadManager;
//    private VideoStudioAdapter mVideoStudioAdapter;
//    private ActionMode mActionMode;
//    private AsyncTask mVideoStudioLoadTask;
//
//    private AlertDialog dialog;
//
//
//    public static VideosFragment newInstance() {
//        return new VideosFragment();
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @NonNull
//    @Override
//    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_my_studio, container, false);
//        ButterKnife.bind(this, view);
//        return view;
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        AdsUtilsSplash.getSharedInstance().loadNativeInsideView(requireActivity(), nativeAds);
//        initAds();
//        mDownloadManager = DownloadManager.getInstance();
//        mDownloadManager.init(getContext());
//        mVideoStudioAdapter = new VideoStudioAdapter(getActivity());
//        mVideoStudioAdapter.setOnItemClickListener(this);
//        mVideoStudioAdapter.setOnItemLongClickListener(this);
//        mVideoStudioAdapter.setOnItemCheckedChangeListener(this);
//        mVideoStudioAdapter.setOnMoreClickListener(this);
//        rvMyStudioList.setAdapter(mVideoStudioAdapter);
//        rvMyStudioList.setLayoutManager(new LinearLayoutManager(getContext()));
//        rvMyStudioList.setHasFixedSize(true);
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            refreshVideoList();
//        }
//    }
//
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_video, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_sort) {
//            SortUtils.showSortDialog(getActivity(), this::refreshVideoList);
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//        refreshVideoList();
//    }
//
//    @Override
//    public void onDestroyView() {
//        cancelLoadTask();
//        super.onDestroyView();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void reloadData(Video video) {
//        refreshVideoList();
//    }
//
//    @Override
//    public void onItemClick(View view, VideoDetail item) {
//        if (mActionMode == null) {
//            Intent intent = new Intent(getActivity(), VideoPreviewActivity.class);
//            intent.putExtra(Const.PREVIEW_URL, item.getPath());
//            startActivity(intent);
//        } else {
//            updateCheckState(item);
//        }
//    }
//
//
//    @Override
//    public boolean onItemLongClick(View view, VideoDetail item) {
//        if (mActionMode == null) {
//            mActionMode = ((DownloadActivity) getActivity()).startSupportActionMode(this);
//        }
//        updateCheckState(item);
//        return true;
//    }
//
//    @Override
//    public void onItemCheckedChanged(CompoundButton buttonView, boolean isChecked, VideoDetail item) {
//        updateActionModeTitle();
//    }
//
//    @Override
//    public void onMoreClick(View view, final VideoDetail item) {
//        PopupMenu popupMenu = new PopupMenu(getContext(), view);
//        popupMenu.inflate(R.menu.popup_menu);
//        Menu menu = popupMenu.getMenu();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            menu.findItem(R.id.action_rename).setVisible(true);
//            menu.findItem(R.id.action_delete).setVisible(true);
//        } else {
//            menu.findItem(R.id.action_rename).setVisible(false);
//            menu.findItem(R.id.action_delete).setVisible(false);
//        }
//
//        int tint = ContextCompat.getColor(getContext(), R.color.colorPrimary);
//        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
//            popupMenu.getMenu().getItem(i).getIcon().setColorFilter(tint, PorterDuff.Mode.SRC_ATOP);
//        }
//        try {
//            Field[] fields = popupMenu.getClass().getDeclaredFields();
//            for (Field field : fields) {
//                if ("mPopup".equals(field.getName())) {
//                    field.setAccessible(true);
//                    Object menuPopupHelper = field.get(popupMenu);
//                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
//                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
//                    setForceIcons.invoke(menuPopupHelper, true);
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        popupMenu.setOnMenuItemClickListener(menuItem -> {
//            switch (menuItem.getItemId()) {
//                case R.id.action_detail:
//                    String detail = getString(R.string.title) + ": " + item.getTitle() + "\n" +
//                            getString(R.string.resolution) + ": " + item.getWidth() + "x" + item.getHeight() + "\n" +
//                            getString(R.string.size) + ": " + Formatter.formatFileSize(getContext(), new File(item.getPath()).length()) + "\n" +
//                            getString(R.string.path) + ": " + item.getPath() + "\n";
//
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle(R.string.detail)
//                            .setMessage(detail)
//                            .setPositiveButton(R.string.alert_ok_button, null)
//                            .show();
//                    return true;
//                case R.id.action_rename:
//                    String extension = ".mp4";
//                    new ValidateNameDialog(getActivity(), Utils.getVideoFolder(getContext()), extension)
//                            .setDefaultName(item.getTitle())
//                            .setCallback(resultPath -> updateVideoInfo(item, resultPath))
//                            .show();
//                    return true;
//                case R.id.action_delete:
//                    txtYes.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            new File(item.getPath()).delete();
//                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(item.getPath()))));
//                            if (item.getId() != -1) {
//                                getActivity().getContentResolver().delete(Uri.parse(item.getUri()), null, null);
//                            }
//                            mVideoStudioAdapter.remove(item);
//                            refreshVideoList();
//                            updateView();
//                        }
//                    });
//                    txtNo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
//                    return true;
//                case R.id.action_share:
//                    Uri contentUri;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (PrefUtils.getExternalFilesDirName(getActivity()).contains(BuildConfig.APPLICATION_ID)) {
//                            contentUri = Uri.fromFile(new File(String.valueOf(new File(item.getPath()))));
//                        } else {
//                            contentUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(item.getPath()));
//                        }
//                    } else {
//                        contentUri = Uri.fromFile(new File(item.getPath()));
//                    }
//                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
//                    shareIntent.setType("video/*");
//                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
//                    return true;
//                default:
//                    return false;
//            }
//        });
//        popupMenu.show();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        refreshVideoList();
//    }
//
//
//    public Boolean isSpecialCharAvailable(String s) {
//        if (s == null || s.trim().isEmpty()) {
//            return false;
//        }
//        Pattern p = Pattern.compile("[^A-Za-z0-9]");
//        Matcher m = p.matcher(s);
//        return m.find();
//    }
//
//    private void updateVideoInfo(final VideoDetail item, final String newPath) {
//        if (isSpecialCharAvailable(newPath)) {
//
//            File originalFile = new File(item.getPath());
//            File newFile = new File(newPath);
//            if (!originalFile.renameTo(newFile)) {
//                Toast.makeText(getActivity(), getString(R.string.save_failed), Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (item.getId() != -1) {
//                getContext().getContentResolver().delete(Uri.parse(item.getUri()), null, null);
//            }
//            progressBar.setVisibility(View.VISIBLE);
//            rvMyStudioList.setVisibility(View.GONE);
//            tvEmpty.setVisibility(View.GONE);
//            cancelLoadTask();
//
//            MediaScannerConnection.scanFile(getContext(), new String[]{newPath}, null, (path, uri) -> {
//                if (uri == null) {
//                    ContentValues values = new ContentValues(3);
//                    values.put(MediaStore.Video.Media.TITLE, path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));
//                    values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
//                    values.put(MediaStore.Video.Media.DATA, path);
//                    getContext().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//                }
//                mVideoStudioLoadTask = new VideoStudioLoadTask(getContext(), VideosFragment.this, 1).execute();
//            });
//        } else {
//            Toast.makeText(getActivity(), getString(R.string.name_file_format), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    @Override
//    public void onLoadCompleted(List<VideoDetail> result) {
//        if (progressBar != null) {
//            progressBar.setVisibility(View.GONE);
//        }
//        mVideoStudioAdapter.setVideoDetails(result);
//        updateView();
//        mVideoStudioLoadTask = null;
//    }
//
//    private void updateView() {
//        rvMyStudioList.setVisibility(mVideoStudioAdapter.isEmpty() ? View.GONE : View.VISIBLE);
//        tvEmpty.setVisibility(mVideoStudioAdapter.isEmpty() ? View.VISIBLE : View.GONE);
//    }
//
//    private void cancelLoadTask() {
//        if (mVideoStudioLoadTask != null) {
//            if (!mVideoStudioLoadTask.isCancelled()) {
//                mVideoStudioLoadTask.cancel(true);
//            }
//            mVideoStudioLoadTask = null;
//        }
//    }
//
//
//    private void refreshVideoList() {
//        if (progressBar != null) {
//            progressBar.setVisibility(View.VISIBLE);
//        }
//        rvMyStudioList.setVisibility(View.GONE);
//        tvEmpty.setVisibility(View.GONE);
//        cancelLoadTask();
//        mVideoStudioLoadTask = new VideoStudioLoadTask(getContext(), this, 1).execute();
//    }
//
//    private void updateCheckState(VideoDetail VideoDetail) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            if (!mVideoStudioAdapter.isChecked(VideoDetail)) {
//                mVideoStudioAdapter.setChecked(VideoDetail, true);
//            } else {
//                mVideoStudioAdapter.setChecked(VideoDetail, false);
//            }
//            updateActionModeTitle();
//        }
//    }
//
//    private void updateActionModeTitle() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            mActionMode.setTitle(String.valueOf(mVideoStudioAdapter.getSelectedItems().size()));
//        }
//    }
//
//    @Override
//    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//        ((DownloadActivity) getActivity()).onCreateActionMode(mode);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            mVideoStudioAdapter.setActionMode(true);
//            mode.getMenuInflater().inflate(R.menu.menu_mode, menu);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_delete:
//                if (!mVideoStudioAdapter.getSelectedItems().isEmpty()) {
//                    String message;
//                    if (mVideoStudioAdapter.getSelectedItems().size() > 1) {
//                        message = getString(R.string.delete_all_video);
//                    } else {
//                        message = getString(R.string.noti_delete_video);
//                    }
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle(R.string.confirm)
//                            .setMessage(message)
//                            .setPositiveButton(R.string.yes, (dialog, which) -> {
//                                for (VideoDetail videoDetail : mVideoStudioAdapter.getSelectedItems()) {
//                                    new File(videoDetail.getPath()).delete();
//                                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(videoDetail.getPath()))));
//                                    if (videoDetail.getId() != -1) {
//                                        getActivity().getContentResolver().delete(Uri.parse(videoDetail.getUri()), null, null);
//                                    }
//                                }
//                                mVideoStudioAdapter.removeSelectedItems();
//                                refreshVideoList();
//                                updateView();
//                                mActionMode.finish();
//                            })
//                            .setNegativeButton(R.string.no, null)
//                            .show();
//                } else {
//                    Toast.makeText(requireActivity(), getString(R.string.select_delete_video), Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            case R.id.action_select_all:
//                mVideoStudioAdapter.updateSelectAll();
//                updateActionModeTitle();
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    @Override
//    public void onDestroyActionMode(ActionMode mode) {
//        mActionMode = null;
//        ((DownloadActivity) getActivity()).onDestroyActionMode(mode);
//        mVideoStudioAdapter.setActionMode(false);
//        mVideoStudioAdapter.setSelectAll(false);
//    }
//
//    private void initAds() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//        View view_download = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_delete_download, null);
//        builder.setView(view_download);
//        FrameLayout nativeAds = view_download.findViewById(R.id.nativeAds);
//        AdsUtilsSplash.getSharedInstance().loadNativeAd(requireActivity(), nativeAds);
//        txtYes = view_download.findViewById(R.id.txtYes);
//        txtNo = view_download.findViewById(R.id.txtNo);
//        txtTitle = view_download.findViewById(R.id.txtTitle);
//        txtTitle.setText(getString(R.string.noti_delete_video));
//        dialog = builder.create();
//    }
//}
