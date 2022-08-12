package com.bangbangcoding.screenmirror.web.ui.studio.adapter;//package com.highsecure.videodownloader.ui.studio.adapter;
//
//import android.content.Context;
//
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;
//
//import com.highsecure.videodownloader.R;
//import com.highsecure.videodownloader.ui.progress.DownloadedFragment;
//
//public class StudioAdapter extends FragmentPagerAdapter {
//    private String[] names;
//
//    public StudioAdapter(Context context, FragmentManager fm) {
//        super(fm);
//        names = new String[]{context.getString(R.string.tab_download), context.getString(R.string.tab_video)};
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                return DownloadedFragment.Companion.newInstance();
//            case 1:
//                return VideosFragment.newInstance();
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return 2;
//    }
//
//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return names[position];
//    }
//}
