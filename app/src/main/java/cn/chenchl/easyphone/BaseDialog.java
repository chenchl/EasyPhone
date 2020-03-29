package cn.chenchl.easyphone;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * created by ccl on 2019/2/25
 **/
public class BaseDialog implements DialogInterface {

    protected Dialog dialog;

    protected boolean isCancelable = false;
    protected boolean isCanceledOnTouchOutside = false;
    protected TextView tvContent;
    protected TextView tvTitle;
    protected TextView tvAccpet;
    protected TextView tvCancel;
    protected ImageView ivClose;//关闭控件
    private TextView tvClose;
    protected int height;
    protected int width;
    private Object obj;
    private int xmlLayout = 0;//设置xml文件布局
    private boolean isSetLayout = false;//是否为自己设置xml布局
    private Context context;
    private boolean isPlayMp3 = false;
    private boolean isShow = false;

    /**
     * 设置xml文件布局构造方法
     *
     * @param xmlLayout 设置xml布局文件
     */
    public BaseDialog(int xmlLayout) {
        this.xmlLayout = xmlLayout;
        isSetLayout = true;
    }

    /**
     * 空构造方法，保证原有的dialog不会受到影响
     */
    public BaseDialog() {
        isSetLayout = false;
    }

    public int setLayoutXml() {
        //兼容之前dialog不受影响，当xmlLayout为空的时候加载默认布局
        //新布局和默认布局的xml的id要完全一致
        if (!isSetLayout) {
            return R.layout.common_base_dialog;
        } else {
            //多一次兼容性判断
            if (0 == xmlLayout) {
                return R.layout.common_base_dialog;
            } else {
                return xmlLayout;
            }
        }
    }

    public void init() {
    }

    @Override
    public BaseDialog create(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(setLayoutXml(), null);
        tvContent = (TextView) view.findViewById(R.id.tv_dialog_content);
        tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        tvAccpet = (TextView) view.findViewById(R.id.tv_dialog_accpet);
        tvCancel = (TextView) view.findViewById(R.id.tv_dialog_cancel);
        //关闭控件，xml里默认设置了View.GONE，在代码里可能找不到对应控件，设置方法也做了判空处理
       /* ivClose = (ImageView) view.findViewById(R.id.iv_dialog_close);
        tvClose = (TextView) view.findViewById(R.id.tv_dialog_close_text);*/
        tvCancel.setVisibility(View.GONE);
        tvAccpet.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        tvContent.setVisibility(View.GONE);
        if (height == 0)
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (width == 0)
            width = context.getResources().getDimensionPixelOffset(R.dimen.x600);
        dialog = new Dialog(context, R.style.common_base_dialog);
        dialog.setCancelable(isCancelable); //不可以用“返回键”取消
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);//不可dialog外部点击取消
        dialog.setContentView(view); //设置布局
        Window window = dialog.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvAccpet.setTextColor(context.getResources().getColor(R.color.black));
        return this;
    }

    @Override
    public BaseDialog setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public BaseDialog showTitle(boolean isShow) {
        if (tvTitle != null) {
            tvTitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    @Override
    public BaseDialog setContent(String content) {
        if (tvContent != null) {
            tvContent.setText(content);
            tvContent.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public BaseDialog setContent(String content, float textSize, int gravity) {
        if (tvContent != null) {
            tvContent.setText(content);
            tvContent.setGravity(gravity);
            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tvContent.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public BaseDialog setIsCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
        if (dialog != null)
            dialog.setCancelable(isCancelable);
        return this;
    }

    public BaseDialog setIsCanceledOnTouchOutside(boolean isCanceledOnTouchOutside) {
        this.isCanceledOnTouchOutside = isCanceledOnTouchOutside;
        if (dialog != null)
            dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        return this;
    }

    /**
     * 弹框是否展示关闭的气泡控件
     *
     * @param isShowClose 是否展示控件，true展示，false不展示
     */
    public BaseDialog setCloseShow(boolean isShowClose) {
        if (null != ivClose) {
            ivClose.setVisibility(isShowClose ? View.VISIBLE : View.GONE);
            //如果展示该关闭控件，设置关闭点击事件
            if (isShowClose) {
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
            }
        }
        return this;
    }

    /**
     * 弹框是否展示关闭的气泡控件
     *
     * @param isShowClose 是否展示控件，true展示，false不展示
     */
    public BaseDialog setCloseShow(boolean isShowClose, final String text, final onDialogCancelClickListener listener) {
        if (null != ivClose) {
            ivClose.setVisibility(View.GONE);
            if (null != tvClose) {
                tvClose.setVisibility(isShowClose ? View.VISIBLE : View.GONE);
                tvClose.setText(text);
                tvClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null)
                            listener.cancel(text, BaseDialog.this);
                    }
                });
            }
            //如果展示该关闭控件，设置关闭点击事件
            if (isShowClose) {
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null)
                            listener.cancel(text, BaseDialog.this);
                    }
                });
            }
        }
        return this;
    }

    @Override
    public BaseDialog setAccpet(final String accpet, final onDialogAccpetClickListener listener) {
        if (tvAccpet != null) {
            tvAccpet.setVisibility(View.VISIBLE);
            tvAccpet.setText(accpet);
            tvAccpet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.accpet(accpet, BaseDialog.this);
                    }
                }
            });
        }
        return this;
    }

    public BaseDialog setAccpet(final String accpet, float textSize, final onDialogAccpetClickListener listener) {
        if (tvAccpet != null) {
            tvAccpet.setVisibility(View.VISIBLE);
            tvAccpet.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tvAccpet.setText(accpet);
            tvAccpet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.accpet(accpet, BaseDialog.this);
                    }
                }
            });
        }
        return this;
    }

    @Override
    public BaseDialog setCancel(final String cancel, final onDialogCancelClickListener listener) {
        if (tvCancel != null) {
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.cancel(cancel, BaseDialog.this);
                    }
                }
            });
        }
        //增加判断，如果没有设置accpet，则把button背景图设置为长图
        //多一次兼容性判断，只有当自己设置的xml的时候生效修改布局button背景
        if (tvAccpet != null && isSetLayout) {
            if (tvAccpet.getVisibility() == View.GONE && tvCancel != null) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen.x300), (int) context.getResources().getDimension(R.dimen.x80));
                tvCancel.setLayoutParams(layoutParams);
                //tvCancel.setBackgroundResource(R.mipmap.icon_button_blue_long);
            }
        }
        return this;
    }

    public BaseDialog setCancel(final String cancel, float textSize, final onDialogCancelClickListener listener) {
        if (tvCancel != null) {
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tvCancel.setText(cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.cancel(cancel, BaseDialog.this);
                    }
                }
            });
        }
        //增加判断，如果没有设置accpet，则把button背景图设置为长图
        //多一次兼容性判断，只有当自己设置的xml的时候生效修改布局button背景
        if (tvAccpet != null && isSetLayout) {
            if (tvAccpet.getVisibility() == View.GONE && tvCancel != null) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen.x300), (int) context.getResources().getDimension(R.dimen.x80));
                tvCancel.setLayoutParams(layoutParams);
                //tvCancel.setBackgroundResource(R.mipmap.icon_button_blue_long);
            }
        }
        return this;
    }

    public TextView getCancelButton() {
        return tvCancel;
    }

    @Override
    public BaseDialog setObj(Object obj) {
        this.obj = obj;
        return this;
    }

    @Override
    public BaseDialog show() {
        try {
            isShow = true;
            if (dialog != null) {
                if (dialog.getContext() != null)
                    dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public BaseDialog dismiss() {
        try {
            isShow = false;
            if (dialog != null) {
                if (dialog.getContext() != null && dialog.isShowing())
                    dialog.dismiss();
            }
            if (isPlayMp3) {
                isPlayMp3 = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public BaseDialog setHw(int height, int width) {
        this.height = height;
        this.width = width;
        return this;
    }

    public BaseDialog setMp3(String assetPath) {
        try {
            isPlayMp3 = true;
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(assetPath);
        } catch (Exception e) {

        }
        return this;
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public boolean isShow() {
        return isShow;
    }

}
