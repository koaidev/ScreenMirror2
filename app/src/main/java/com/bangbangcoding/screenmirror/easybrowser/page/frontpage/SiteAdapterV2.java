package com.bangbangcoding.screenmirror.easybrowser.page.frontpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.easybrowser.EasyApplication;
import com.bangbangcoding.screenmirror.easybrowser.entity.dao.AppDatabase;
import com.bangbangcoding.screenmirror.easybrowser.entity.dao.WebSite;
import com.bangbangcoding.screenmirror.easybrowser.utils.StringUtils;
import com.bangbangcoding.screenmirror.easybrowser.widget.WebSiteLogo;

import java.util.ArrayList;
import java.util.List;


public class SiteAdapterV2 extends RecyclerView.Adapter<SiteAdapterV2.SiteViewHolder> {

    private final List<WebSite> dataList = new ArrayList<>();
    private Context context;

    private OnSiteItemClickListener listener;

    public SiteAdapterV2(Context context) {
        this.context = context;
    }

    public final List<WebSite> getDataList() {
        return dataList;
    }

    public void clearDataList() {
        dataList.clear();
    }

    public void appendDataList(List<WebSite> list) {
        dataList.addAll(list);
    }

    public OnSiteItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnSiteItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_grid_item, parent, false);
        SiteViewHolder holder = new SiteViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SiteViewHolder holder, int position) {
        if (getItemCount() <= position) {
            return;
        }

        final WebSite entity = dataList.get(position);
        holder.title.setText(entity.siteName);
        if (StringUtils.isNotEmpty(entity.siteName) && entity.siteName.length() > 0) {
            String drawName = String.valueOf(entity.siteName.charAt(0));
            holder.icon.setName(drawName);
        } else {
            holder.icon.setName("E");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSiteItemClick(entity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class SiteViewHolder extends RecyclerView.ViewHolder {

        WebSiteLogo icon;
        TextView title;

        public SiteViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
        }
    }

    public interface OnSiteItemClickListener {
        void onSiteItemClick(WebSite webSite);
    }

    public static List<WebSite> getTestDataList(Context context) {
        if (context == null || context.getApplicationContext() == null) {
            return new ArrayList<>();
        }
        final EasyApplication application = (EasyApplication) context.getApplicationContext();
        AppDatabase db = application.getAppDatabase();
        List<WebSite> dbList = db.webSiteDao().getAll();
        if (dbList == null || dbList.size() <= 0) {
            return new ArrayList<>();
        }

        return new ArrayList<>(dbList);
    }
}