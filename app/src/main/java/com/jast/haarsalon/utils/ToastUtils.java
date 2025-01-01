package com.jast.haarsalon.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showSuccess(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}