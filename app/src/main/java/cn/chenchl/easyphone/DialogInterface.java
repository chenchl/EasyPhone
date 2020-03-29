package cn.chenchl.easyphone;

import android.content.Context;

/**
 * created by ccl on 2019/2/25
 **/
public interface DialogInterface {

    DialogInterface create(Context context);

    DialogInterface show();

    DialogInterface setTitle(String title);

    DialogInterface setContent(String content);

    DialogInterface setAccpet(String accpet, onDialogAccpetClickListener listener);

    DialogInterface setCancel(String cancel, onDialogCancelClickListener listener);

    DialogInterface setObj(Object obj);

    DialogInterface dismiss();


    public interface onDialogAccpetClickListener<T> {
        void accpet(T t, DialogInterface dialog);
    }

    public interface onDialogCancelClickListener<T> {
        void cancel(T t, DialogInterface dialog);
    }
}
