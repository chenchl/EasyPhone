package cn.chenchl.libs.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;

import cn.chenchl.libs.log.LogUtil;

/**
 * 自定义回弹Behavior 支持appbar内部嵌入格外一个view
 */
public class AppBarLayoutOverScrollViewBehavior extends AppBarLayout.Behavior {

    private static final String TAG = "AppBarLayout_overScroll";
    private View mTargetView;       // 目标View
    private int mParentHeight;      // AppBarLayout的初始高度
    private int mTargetViewHeight;  // 目标View的高度
    private boolean isAnimate = false;
    private int targetHeight = 1500; // 最大滑动距离
    private float targetScare = 1.5f; // 最大缩放比例
    private long targetAnimDuration = 500; // 回弹动画时间
    private float mTotalDy;     // 总滑动的像素数
    private float mLastScale;   // 最终放大比例
    private int mLastBottom;    // AppBarLayout的最终Bottom值
    private int mLastBottomView;
    private View mBottomView;
    private int mBottomViewHeight;
    private int mBottomViewBottom;
    private int mDistanceY;
    private boolean isOK;
    private ValueAnimator anim;


    public AppBarLayoutOverScrollViewBehavior() {
    }

    public AppBarLayoutOverScrollViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getMaxOverScrollHeight() {
        return targetHeight;
    }

    public void setMaxOverScrollHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    public float getTargetScare() {
        return targetScare;
    }

    public void setTargetScare(float targetScare) {
        this.targetScare = targetScare;
    }

    public long getScrollAnimDuration() {
        return targetAnimDuration;
    }

    public void setScrollAnimDuration(long targetAnimDuration) {
        this.targetAnimDuration = targetAnimDuration;
    }

    /**
     * AppBarLayout布局时调用
     *
     * @param parent          父布局CoordinatorLayout
     * @param abl             使用此Behavior的AppBarLayout
     * @param layoutDirection 布局方向
     * @return 返回true表示子View重新布局，返回false表示请求默认布局
     */
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        boolean handled = super.onLayoutChild(parent, abl, layoutDirection);
        // 需要在调用过super.onLayoutChild()方法之后获取
        if (mTargetView == null) {
            mTargetView = parent.findViewWithTag(TAG);
            if (mTargetView != null) {
                initial(abl);
            }
        }
        if (mBottomView == null) {
            mBottomView = abl.getChildAt(1);
            if (mBottomView != null) {
                mBottomViewHeight = mBottomView.getHeight();
                mBottomViewBottom = mBottomView.getBottom();
            }
        }
        return handled;
    }

    private void initial(AppBarLayout abl) {
        // 必须设置ClipChildren为false，这样目标View在放大时才能超出布局的范围
        abl.setClipChildren(false);
        mParentHeight = abl.getHeight();
        mTargetViewHeight = mTargetView.getHeight();

        //targetHeight = mTargetViewHeight / 2;
    }

    /**
     * 当准备开始嵌套滚动时调用
     *
     * @param coordinatorLayout 父布局CoordinatorLayout
     * @param abl               使用此Behavior的AppBarLayout
     * @param target            发起嵌套滚动的目标View(即AppBarLayout下面的ScrollView或RecyclerView)
     * @param dx                用户在水平方向上滑动的像素数
     * @param dy                用户在垂直方向上滑动的像素数
     * @param consumed          输出参数，consumed[0]为水平方向应该消耗的距离，consumed[1]为垂直方向应该消耗的距离
     */
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int dx, int dy, int[] consumed, int type) {
        // 1.mTargetView不为null
        // 2.是向下滑动，dy<0表示向下滑动
        // 3.AppBarLayout已经完全展开，child.getBottom() >= mParentHeight
        if (mTargetView != null && ((dy < 0 && abl.getBottom() >= mParentHeight) || dy > 0 && abl.getBottom() > mParentHeight)) {
            // 累加垂直方向上滑动的像素数
            mTotalDy += -dy;
            // 不能大于最大滑动距离
            mTotalDy = Math.min(mTotalDy, targetHeight);
            // 计算目标View缩放比例，不能小于1
            mLastScale = Math.max(1f, 1f + mTotalDy / targetHeight);
            //不能超过最大缩放比例
            if (Math.abs(mLastScale) > targetScare) {
                mLastScale = mLastScale >= 0 ? targetScare : -targetScare;
            }
            // 缩放目标View
            ViewCompat.setScaleX(mTargetView, mLastScale);
            ViewCompat.setScaleY(mTargetView, mLastScale);
            // 计算目标View放大后增加的高度
            mLastBottom = mParentHeight + (int) (mTargetViewHeight / 2 * (mLastScale - 1));
            // 修改AppBarLayout的高度
            abl.setBottom(mLastBottom);
            //appbar含有底部栏
            if (mBottomView != null) {
                mLastBottomView = mBottomViewBottom + (int) (mTargetViewHeight / 2 * (mLastScale - 1));
                mBottomView.setTop(mLastBottomView - mBottomViewHeight);
                mBottomView.setBottom(mLastBottomView);
            }
            target.setScrollY(0);
        } else {
            super.onNestedPreScroll(coordinatorLayout, abl, target, dx, dy, consumed, type);
            LogUtil.INSTANCE.e("onNestedPreScroll", consumed[1] + "");
        }
    }

    /**
     * 当停止滚动时调用
     *
     * @param coordinatorLayout 父布局CoordinatorLayout
     * @param abl               使用此Behavior的AppBarLayout
     * @param target            发起嵌套滚动的目标View(即AppBarLayout下面的ScrollView或RecyclerView)
     */
    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int type) {

        LogUtil.INSTANCE.e("onStopNestedScroll", type + "1");
        recovery(abl);
        type = 1;
        super.onStopNestedScroll(coordinatorLayout, abl, target, type);


    }

    /**
     * 开始嵌套滑动时调用
     *
     * @param parent
     * @param child
     * @param directTargetChild
     * @param target
     * @param nestedScrollAxes
     * @param type
     * @return
     */
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes, int type) {
        // 开始滑动时，启用动画
        isAnimate = true;
        mTotalDy = 0;
        if (anim != null)
            anim.cancel();
        LogUtil.INSTANCE.e("onStartNestedScroll", type + " " + mTotalDy);
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type);
    }

    /**
     * 触发惯性滑动前调用
     *
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY) {
        // 如果触发了快速滚动且垂直方向上速度大于100，则禁用动画
        if (velocityY > 20000) {
            isAnimate = false;
            if (anim != null)
                anim.cancel();
        }
        LogUtil.INSTANCE.e("onNestedPreFling", "1");
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        LogUtil.INSTANCE.e("onNestedFling", "1");
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    /**
     * 还原位置
     *
     * @param abl
     */
    private void recovery(final AppBarLayout abl) {
        if (mTotalDy > 0) {
            mTotalDy = 0;
            if (isAnimate) {//需要动画
                // 使用属性动画还原
                anim = ValueAnimator.ofFloat(mLastScale, 1f).setDuration(targetAnimDuration);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    int currentBottom = 0;
                    int currentBottomView = 0;

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        ViewCompat.setScaleX(mTargetView, value);
                        ViewCompat.setScaleY(mTargetView, value);
                        currentBottom = (int) (mLastBottom - (mLastBottom - mParentHeight) * animation.getAnimatedFraction());
                        abl.setBottom(currentBottom);
                        if (mBottomView != null) {
                            currentBottomView = (int) (mLastBottomView - (mLastBottomView - mBottomViewBottom) * animation.getAnimatedFraction());
                            mBottomView.setTop(currentBottomView - mBottomViewHeight);
                            mBottomView.setBottom(currentBottomView);
                        }
                    }
                });
                anim.start();
            } else {//直接还原到位
                if (anim != null)
                    anim.cancel();
                ViewCompat.setScaleX(mTargetView, 1f);
                ViewCompat.setScaleY(mTargetView, 1f);
                abl.setBottom(mParentHeight);
                if (mBottomView != null) {
                    mBottomView.setTop(mBottomViewBottom - mBottomViewHeight);
                    mBottomView.setBottom(mBottomViewBottom);
                }
            }
        }
    }


}