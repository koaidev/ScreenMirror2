package com.bangbangcoding.screenmirror.easybrowser.contract;

import com.bangbangcoding.screenmirror.easybrowser.entity.dao.History;

import java.util.List;


public interface IHistory {

    interface View {
        void showHistory(List<History> result);
        void showEmptyResult();
    }

    interface Presenter {
        void getHistory(int pageNo, int pageSize);
        void onDestroy();
    }
}
