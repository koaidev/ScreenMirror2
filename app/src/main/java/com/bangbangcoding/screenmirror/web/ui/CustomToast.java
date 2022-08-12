package com.bangbangcoding.screenmirror.web.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bangbangcoding.screenmirror.R;

public class CustomToast extends Toast {
    public static int SUCCESS = 1;
    public static int WARNING = 2;

    public static int SHORT = 4000;
    public static int LONG = 7000;

    public CustomToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, String message, int duration, int type) {
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null, false);
        TextView l1 = (TextView) layout.findViewById(R.id.toast_text);
        LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.toast_type);
        l1.setText(message);
        if (type == 1) {
            linearLayout.setBackgroundResource(R.drawable.sucess_shape);
        } else if (type == 2) {
            linearLayout.setBackgroundResource(R.drawable.warning_shape);
        }
        toast.setView(layout);
        return toast;
    }

}
