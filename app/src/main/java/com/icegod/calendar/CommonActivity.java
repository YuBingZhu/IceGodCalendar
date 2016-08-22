package com.icegod.calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TextView;

public class CommonActivity extends Activity {
    protected String TAG = CommonActivity.class.getCanonicalName();
    public Context context = CommonActivity.this;
    public Activity activity = CommonActivity.this;
    // 头部标题文本控件
    protected TextView commonHeaderTitleTextView;
    // 头部标题左侧按钮
    protected TextView commonHeaderTitleLeftButton;
    // 头部标题右侧按钮
    protected TextView commonHeaderTitleRightButton;
    // 顶部提示文字控件
    protected TextView commonTopHintTextView;
    // 下单步骤按钮控件
    protected TextView orderStepButton;
    // 头部标题文字资源
    protected String commonHeaderTitleText;

    public void onCreate(Bundle savedInstanceState, int layoutId) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        // 设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        /*
         * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */

        setContentView(layoutId);
        // 设置默认值
        this.commonHeaderTitleRightButton = (TextView) findViewById(R.id.common_header_title_right_button);
    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 设置公共头部标题
     *
     * @param id 标题
     * @return void
     */
    protected void setCommonHeaderTitleText(String text) {
        this.commonHeaderTitleTextView = (TextView) findViewById(R.id.common_header_title);
        this.commonHeaderTitleTextView.setText(text);
    }

    /**
     * 自动margin
     *
     * @param changeView    被设置margin的view的 ID
     * @param referenceView 设置margin的参照view 的 ID，将根据此view的宽或高度为基础
     * @param orientation   设置哪一侧的margin 1 左外边距 2 上外边距 3 右外边距 4 下外边距
     * @param addValue      补加的margin值
     * @return void
     */
    public void autoMargin(int changeViewId, int referenceViewId,
                           int orientation, int addValue) {
        View referenceView = (View) findViewById(referenceViewId);
        ViewTreeObserver referenceViewTreeObserver = referenceView
                .getViewTreeObserver();
        referenceViewTreeObserver
                .addOnPreDrawListener(new AutoMarginOnPreDrawListener(
                        findViewById(changeViewId), referenceView, orientation,
                        addValue));
    }

    /**
     * 自动宽度
     *
     * @param changeView    被设置margin的view
     * @param referenceView 设置margin的参照view，将根据此view的宽或高度为基础
     * @param addValue      补加的margin值
     * @return void
     */
    public void autoWidth(View changeView, View referenceView, int addValue) {
        ViewTreeObserver referenceViewTreeObserver = referenceView
                .getViewTreeObserver();
        referenceViewTreeObserver
                .addOnPreDrawListener(new AutoMarginOnPreDrawListener(
                        changeView, referenceView, addValue));
    }

    // 公共头部后退按钮点击事件
    public void commonBackButtonOnClick(View view) {
        finish();
    }
}
