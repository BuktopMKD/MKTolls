package com.denofdevelopers.mktolls.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public final class InputViewHandler {
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showCursor(View view) {
        if (view instanceof EditText) {
            EditText editText = ((EditText) view);
            editText.setCursorVisible(true);
        }
    }
}
