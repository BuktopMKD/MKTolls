package com.denofdevelopers.mktolls.util;

import android.content.Context;
import android.widget.Toast;

public final class ToastUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
