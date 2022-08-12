package com.bangbangcoding.screenmirror.web.ui.model.bulkdownloader;

import androidx.annotation.Keep;

@Keep

public class DataInfoInsta {
    UserInfoInstaProfile user;

    public UserInfoInstaProfile getUser() {
        return this.user;
    }

    public void setUser(UserInfoInstaProfile user) {
        this.user = user;
    }
}
