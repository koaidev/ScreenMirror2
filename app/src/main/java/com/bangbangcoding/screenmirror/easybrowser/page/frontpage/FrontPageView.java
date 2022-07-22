package com.bangbangcoding.screenmirror.easybrowser.page.frontpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.easybrowser.common.BrowserConst;
import com.bangbangcoding.screenmirror.easybrowser.contract.IBrowser;
import com.bangbangcoding.screenmirror.easybrowser.contract.IFrontPage;
import com.bangbangcoding.screenmirror.easybrowser.entity.dao.WebSite;
import com.bangbangcoding.screenmirror.easybrowser.widget.BrowserNavBar;

import java.util.List;

public class FrontPageView extends FrameLayout implements IFrontPage.View {

    private RecyclerView siteGird;
    private SiteAdapterV2 siteAdapter;
    private BrowserNavBar navBar;

    private IFrontPage.Presenter presenter;

    public FrontPageView(Context context) {
        this(context, null);
    }

    public FrontPageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrontPageView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_new_tab, this);

        siteGird = findViewById(R.id.site_grid);
        siteGird.setLayoutManager(new GridLayoutManager(getContext(), 4));
        siteAdapter = new SiteAdapterV2(getContext());
        siteGird.setAdapter(siteAdapter);
        //PagerSnapHelper snapHelper = new PagerSnapHelper();
        //snapHelper.attachToRecyclerView(siteGird);

        navBar = findViewById(R.id.new_tab_nav_bar);
        navBar.setNavListener(new FrontPageNavListener(getContext()));

        ImageView gotoButton = findViewById(R.id.goto_button);
        gotoButton.setImageResource(R.drawable.ic_baseline_arrow_back_24);
        TextView addressBar = findViewById(R.id.address_url);
        addressBar.setText(R.string.search_or_type_url);
        addressBar.setTextColor(getResources().getColor(R.color.gray_600));
        addressBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof IBrowser) {
                    IBrowser.INavController navController = (IBrowser.INavController)
                            ((IBrowser) context).provideBrowserComponent(BrowserConst.NAVIGATION_COMPONENT);
                    navController.showAddress("about:blank");
                }
            }
        });
        presenter = new FrontPagePresenterImpl(getContext(), this);
        presenter.getWebSite();
    }

    public void setSiteListener(SiteAdapterV2.OnSiteItemClickListener listener) {
        siteAdapter.setListener(listener);
    }

    public void setTabTitle(String name) {
        // FIXME
    }

    @Override
    public void showWebSite(List<WebSite> webSiteList) {
        if (siteAdapter == null || webSiteList == null) {
            return;
        }
        siteAdapter.appendDataList(webSiteList);
        siteAdapter.notifyDataSetChanged();
    }

    static class FrontPageNavListener implements BrowserNavBar.OnNavClickListener {
        private Context _context;

        public FrontPageNavListener(Context _context) {
            this._context = _context;
        }

        @Override
        public void onItemClick(View itemView) {
            boolean isBrowserController = _context instanceof IBrowser;
            if (!isBrowserController) {
                return;
            }
            IBrowser browser = (IBrowser) _context;
            IBrowser.INavController navController = (IBrowser.INavController)
                    browser.provideBrowserComponent(BrowserConst.NAVIGATION_COMPONENT);
            int id = itemView.getId();
            switch (id) {
                case R.id.nav_back:
                    break;
                case R.id.nav_forward:
                    break;
                case R.id.nav_home:
                    break;
                case R.id.nav_show_tabs:
                    navController.showTabs();
                    break;
                case R.id.nav_setting:
                    navController.showSetting();
                    break;
            }
        }
    }

}
