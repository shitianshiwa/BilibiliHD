package com.duzhaokun123.bilibilihd.bases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.duzhaokun123.bilibilihd.utils.Handler;

public abstract class BaseDialogBuilder<layout extends ViewDataBinding> extends AlertDialog.Builder implements Handler.IHandlerMessageCallback {
    protected BaseDialogBuilder(@NonNull Context context) {
        super(context);

        config = initConfig();
        if ((config & NEED_HANDLER) == NEED_HANDLER) {
            handler = new Handler(this);
        }


        baseBind = DataBindingUtil.inflate(LayoutInflater.from(context), initLayout(), null, false);
        View rootView = baseBind.getRoot();
        findViews(rootView);
        initView();
        initData();
        setView(baseBind.getRoot());

        setOnDismissListener(dialogInterface -> {
            if (handler != null) {
                handler.destroy();
                handler = null;
            }
        });
    }

    private AlertDialog dialog;

    @Override
    public AlertDialog show() {
        dialog = super.show();
        onShow();
        return dialog;
    }

    protected static final int NEED_HANDLER = 0b010;

    public final String CLASS_NAME = this.getClass().getSimpleName();

    private int config;

    protected layout baseBind;
    @Nullable
    protected Handler handler;

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected abstract int initConfig();

    protected abstract int initLayout();

    protected void findViews(View rootView) {
    }

    protected abstract void initView();

    protected abstract void initData();

    protected void onShow() {
    }
}