package com.icegod.calendar;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends CommonActivity implements
        OnClickListener {
    private GestureDetector gestureDetector = null;
    public static CalendarAdapter calV = null;
    private ViewFlipper flipper = null;
    private GridView gridView = null;
    private static int jumpMonth = 0; // 每次滑动，增加或减去�?个月,默认�?0（即显示当前月）
    private static int jumpYear = 0; // 滑动跨越�?年，则增加或者减去一�?,默认�?0(即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private String currentDate = "";
    /**
     * 每次添加gridview到viewflipper中时给的标记
     */
    private int gvFlag = 0;
    /**
     * 当前的年月，现在日历顶端
     */
    private TextView currentMonth;
    // /** 上个�? */
    // private ImageView prevMonth;
    // /** 下个�? */
    // private ImageView nextMonth;
    // 修改后的上一个点击的日期
    public static View previousOnclickDateView;
    // 修改前的上一个点击的日期位置
    public static int[] previousOnclickDateViewPosition = new int[3];
    // 日历根布�?
    private LinearLayout rootCalenderLinearLayout;
    public static Activity INSTANCE;

    public CalendarActivity() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date); // 当期日期
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
    }

    public void onCreate(Bundle savedInstanceState) {
        INSTANCE = this;
        super.TAG = CalendarActivity.class.getCanonicalName();
        super.context = CalendarActivity.this;
        super.onCreate(savedInstanceState, R.layout.alert_calendar_layout);
        // commonHeaderTitleTextView =
        // (TextView)findViewById(R.id.common_header_title);
        // 移除头部标题组件
        TableLayout commonHeaderTitleTableLayout = (TableLayout) View.inflate(
                this.context, R.layout.public_header_layout, null);
        /*
         * TableRow commonHeaderTitleTableRow = (TableRow)
		 * commonHeaderTitleTableLayout.getChildAt(0);
		 */
        TableRow commonHeaderTitleTableRow = (TableRow) findViewById(R.id.common_header_title_row_id);
        commonHeaderTitleTableRow.removeViewAt(1);
        // 添加日期选择组件
        LinearLayout linearLayout = (LinearLayout) View.inflate(this.context,
                R.layout.alert_calendar_date_change_layout, null);

        // 设置日期
        TextView dateTextView = (TextView) linearLayout
                .findViewById(R.id.alert_calendar_date_text_id);
        dateTextView.setText("2014-11-29");
        commonHeaderTitleTableRow.addView(linearLayout, 1);
        LayoutParams layoutParams = (LayoutParams) commonHeaderTitleTableRow
                .getChildAt(1).getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);
        commonHeaderTitleTableLayout.setColumnShrinkable(1, true);
        commonHeaderTitleTableLayout.setColumnStretchable(1, true);
        // 设置头部右侧图标
        commonHeaderTitleRightButton
                .setBackgroundResource(R.drawable.calendar_today_button_background);
        commonHeaderTitleRightButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int jumpMonthTemp = jumpMonth < 0 ? 0 - jumpMonth : jumpMonth;
                for (int i = 0; i < jumpMonthTemp; i++) {
                    if (jumpMonth < 0) {
                        enterNextMonth(gvFlag);
                    } else {
                        enterPrevMonth(gvFlag);
                    }
                }
                initCalendar();
            }
        });

        initCalendar();
    }

    /**
     * 初始化日�?
     */
    private void initCalendar() {
        // setContentView(R.layout.calendar);
        rootCalenderLinearLayout = (LinearLayout) findViewById(R.id.root_calendar_layout_id);
        currentMonth = (TextView) findViewById(R.id.alert_calendar_date_text_id);
        // prevMonth = (ImageView) findViewById(R.id.prevMonth);
        // nextMonth = (ImageView) findViewById(R.id.nextMonth);
        // setListener();

        gestureDetector = new GestureDetector(this, new MyGestureListener());
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        flipper.removeAllViews();
        calV = new CalendarAdapter(this, this, getResources(), jumpMonth,
                jumpYear, year_c, month_c, day_c);
        addGridView();
        gridView.setAdapter(calV);
        flipper.addView(gridView, 0);
        addTextToTopTextView(currentMonth);
    }

    private class MyGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
            if (e1.getX() - e2.getX() > 120) {
                // 像左滑动
                enterNextMonth(gvFlag);
                return true;
            } else if (e1.getX() - e2.getX() < -120) {
                // 向右滑动
                enterPrevMonth(gvFlag);
                return true;
            }
            return false;
        }
    }

    /**
     * 移动到下�?个月
     *
     * @param gvFlag
     */
    private void enterNextMonth(int gvFlag) {
        addGridView(); // 添加�?个gridView
        jumpMonth++; // 下一个月

        calV = new CalendarAdapter(context, this, this.getResources(),
                jumpMonth, jumpYear, year_c, month_c, day_c);
        gridView.setAdapter(calV);
        addTextToTopTextView(currentMonth); // 移动到下�?月后，将当月显示在头标题�?
        gvFlag++;
        flipper.addView(gridView, gvFlag);
        flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.push_left_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.push_left_out));
        flipper.showNext();
        flipper.removeViewAt(0);
        cleanShowAlertList();
    }

    /**
     * 移动到上�?个月
     *
     * @param gvFlag
     */
    private void enterPrevMonth(int gvFlag) {
        addGridView(); // 添加�?个gridView
        jumpMonth--; // 上一个月

        calV = new CalendarAdapter(context, this, this.getResources(),
                jumpMonth, jumpYear, year_c, month_c, day_c);
        gridView.setAdapter(calV);
        gvFlag++;
        addTextToTopTextView(currentMonth); // 移动到上�?月后，将当月显示在头标题�?
        flipper.addView(gridView, gvFlag);

        flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.push_right_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.push_right_out));
        flipper.showPrevious();
        flipper.removeViewAt(0);
        cleanShowAlertList();
    }

    /**
     * 添加头部的年�? 闰哪月等信息
     *
     * @param view
     */
    public void addTextToTopTextView(TextView view) {
        StringBuffer textDate = new StringBuffer();
        // draw = getResources().getDrawable(R.drawable.top_day);
        // view.setBackgroundDrawable(draw);
        textDate.append(calV.getShowYear()).append("年")
                .append(calV.getShowMonth()).append("月");
        view.setText(textDate);
    }

    private void addGridView() {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // 取得屏幕的宽度和高度
        /*
         * WindowManager windowManager = getWindowManager(); Display display =
		 * windowManager.getDefaultDisplay(); int Width = display.getWidth();
		 * int Height = display.getHeight();
		 */
        // 获取日历控件�?在布�?宽高
        int Height = rootCalenderLinearLayout.getMeasuredHeight();
        int Width = rootCalenderLinearLayout.getMeasuredWidth();
        int numberColumns = 7;
        gridView = new GridView(this);
        gridView.setNumColumns(numberColumns);
        gridView.setColumnWidth(Width / numberColumns);

        // gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        /*
         * if (Width == 720 && Height == 1280) { gridView.setColumnWidth(40); }
		 */
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 去除gridView边框
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setOnTouchListener(new OnTouchListener() {
            // 将gridview中的触摸事件回传给gestureDetector
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return CalendarActivity.this.gestureDetector
                        .onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new CalendarItemOnclickListener(
                context, this));
        gridView.setLayoutParams(params);
    }

    // private void setListener() {
    // prevMonth.setOnClickListener(this);
    // nextMonth.setOnClickListener(this);
    // }

    // @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.nextMonth: // 下一个月
                enterNextMonth(gvFlag);
                break;
            case R.id.prevMonth: // 上一个月
                enterPrevMonth(gvFlag);
                break;
            default:
                break;
        }
    }

    /**
     * 清除查询的提醒列�?
     */
    public void cleanShowAlertList() {
        ListView listView = (ListView) findViewById(R.id.service_message_scroll_view_id);
        listView.setAdapter(null);
    }
}
