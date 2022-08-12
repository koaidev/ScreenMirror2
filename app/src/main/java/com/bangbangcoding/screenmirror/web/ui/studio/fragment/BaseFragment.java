package com.bangbangcoding.screenmirror.web.ui.studio.fragment;//package com.highsecure.videodownloader.ui.studio.fragment;
//
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.highsecure.videodownloader.R;
//import com.highsecure.videodownloader.ui.studio.adapter.ListAdapter;
//import com.highsecure.videodownloader.ui.studio.itemdecorations.GridDividerDecoration;
//import com.highsecure.videodownloader.ui.studio.itemdecorations.InsetDecoration;
//
//import rx.subscriptions.CompositeSubscription;
//import rx.subscriptions.Subscriptions;
//
//public abstract class BaseFragment<ViewHolderType extends RecyclerView.ViewHolder, ModelType> extends Fragment {
//
//    @BindView(R.id.list)
//    protected RecyclerView mRecyclerView;
//    @BindView(R.id.tv_empty_download)
//    protected TextView tvEmpty;
//    @BindView(R.id.native_Ads)
//    FrameLayout nativeAds;
//
//    protected CompositeSubscription mSubscription;
//
//    protected RecyclerView.LayoutManager mLayoutManager;
//    public ListAdapter<ViewHolderType, ModelType> mAdapter;
//
//    protected boolean mDrawInsets = false;
//    protected boolean mEnableSelection = false;
//    protected boolean mForceGrid = false;
//    protected int mColumnCount = 1;
//
//    public BaseFragment() {
//        setRetainInstance(true);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
//        ButterKnife.bind(this, view);
//        mSubscription = Subscriptions.from(Subscriptions.empty());
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
////        AdsUtilsSplash.getSharedInstance().loadNativeInsideView(requireActivity(),nativeAds);
//        setupData();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        fetchData();
//    }
//
//    @Override
//    public void onDestroyView() {
//        mLayoutManager = null;
//        mSubscription.unsubscribe();
//        super.onDestroyView();
//    }
//
//
//    private void setupData() {
//        boolean needToFetchData = false;
//        if (mForceGrid) {
//            mLayoutManager = new GridLayoutManager(getActivity(), mColumnCount + 1);
//            mRecyclerView.addItemDecoration(new GridDividerDecoration(getActivity()));
//        } else if (mDrawInsets) {
//            mLayoutManager = new LinearLayoutManager(getActivity());
//            mRecyclerView.addItemDecoration(new InsetDecoration(getContext()));
//        } else if (mColumnCount == 1) {
//            mLayoutManager = new LinearLayoutManager(getActivity());
//            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
//        } else {
//            mLayoutManager = new GridLayoutManager(getActivity(), mColumnCount);
//            mRecyclerView.addItemDecoration(new GridDividerDecoration(getActivity()));
//        }
//        if (mAdapter == null) {
//            mAdapter = getAdapter();
//            mAdapter.setSelectionEnabled(mEnableSelection);
//            mAdapter.setHasStableIds(true);
//            needToFetchData = true;
//        }
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mAdapter);
//        if (needToFetchData) {
//            fetchData();
//        } else {
//            mAdapter.notifyDataSetChanged();
//        }
//        mAdapter.setOnItemClickListener(this::onDataItemClicked);
//    }
//
//
//    protected abstract void fetchData();
//
//    protected abstract ListAdapter<ViewHolderType, ModelType> getAdapter();
//
//    protected abstract void onDataItemClicked(View view, ModelType modelType, int i);
//
//
//}
