package com.icegod.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日历gridview中的每一个item显示的textview
 *
 * @author Vincent Lee
 */
public class CalendarAdapter extends BaseAdapter {
    private final static String TAG = CalendarAdapter.class.getCanonicalName();
    // 当月的提醒对象列表
    //private List<AlertSetting> alertSettings;
    private boolean isLeapyear = false; // 是否为闰年
    private int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0; // 具体某一天是星期几
    private int lastDaysOfMonth = 0; // 上一个月的总天数
    private Context context;
    private Activity activity;
    private int dayNumberLength = 42;
    private List<DayNumber> dayNumbers = new ArrayList<DayNumber>(
            dayNumberLength);
    // private String[] dayNumber = new String[42]; // 一个gridview中的日期存入此数组中
    // private static String week[] = {"周日","周一","周二","周三","周四","周五","周六"};
    private SpecialCalendar specialCalendar = null;
    private LunarCalendar lunarCalendar = null;
    private Resources resources = null;
    private Drawable drawable = null;

    private int currentYear;
    private int currentMonth;
    private int currentDay;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private int currentFlag = -1; // 用于标记当天
    // private int[] schDateTagFlag = null; // 存储当月所有的日程日期

    private int showYear; // 用于在头部显示的年份
    private int showMonth; // 用于在头部显示的月份
    private String animalsYear;
    private int leapMonth; // 闰哪一个月
    private String cyclical = ""; // 天干地支
    // 系统当前时间
    private String systemDate;
    private int systemYear;
    private int systemMonth;
    private int systemDay;

    public CalendarAdapter() {
        Date date = new Date();
        systemDate = sdf.format(date); // 当期日期
        Calendar calendar = Calendar.getInstance();
        systemYear = calendar.get(Calendar.YEAR);
        systemMonth = calendar.get(Calendar.MONTH);
        systemDay = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "当前日期：" + systemDate + " | " + systemYear + "-"
                + systemMonth + "-" + systemDay);
    }

    public CalendarAdapter(Context context, Activity activity,
                           Resources resources, int jumpMonth, int jumpYear, int year_c,
                           int month_c, int day_c) {
        this();
        this.context = context;
        this.activity = activity;
        specialCalendar = new SpecialCalendar();
        lunarCalendar = new LunarCalendar();
        this.resources = resources;

        int stepYear = year_c + jumpYear;
        int stepMonth = month_c + jumpMonth;
        if (stepMonth > 0) {
            // 往下一个月滑动
            if (stepMonth % 12 == 0) {
                stepYear = year_c + stepMonth / 12 - 1;
                stepMonth = 12;
            } else {
                stepYear = year_c + stepMonth / 12;
                stepMonth = stepMonth % 12;
            }
        } else {
            // 往上一个月滑动
            stepYear = year_c - 1 + stepMonth / 12;
            stepMonth = stepMonth % 12 + 12;
            if (stepMonth % 12 == 0) {

            }
        }

        currentYear = stepYear; // 得到当前的年份
        currentMonth = stepMonth; // 得到本月
        // （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
        currentDay = day_c; // 得到当前日期是哪天

        getCalendar(currentYear, currentMonth);

        // 查询当月所有提醒
        // alertSettings = AlertSettingDao.getInstance().selectAll(
        // context.getContentResolver(),
        // AlertContentProvider.COLUMN_YEAR + "=" + currentYear + " and "
        // + AlertContentProvider.COLUMN_MONTH + "="
        // + (Integer.valueOf(currentMonth) - 1)
        // + " ) GROUP BY ( day ");
    }

    public CalendarAdapter(Context context, Resources resources, int year,
                           int month, int day) {
        this();
        this.context = context;
        specialCalendar = new SpecialCalendar();
        lunarCalendar = new LunarCalendar();
        this.resources = resources;
        currentYear = year;// 得到跳转到的年份
        currentMonth = month; // 得到跳转到的月份
        currentDay = day; // 得到跳转到的天
        getCalendar(currentYear, currentMonth);
    }

    // @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dayNumbers.size();
    }

    // @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    // @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    // @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CalendarItemViewHolder viewHolder = new CalendarItemViewHolder();

        // if (convertView == null) {
        convertView = View.inflate(context, R.layout.calendar_item, null);
        // }
        // 当日有提醒的图片标志
        /*
         * ImageView alertDateImageView = (ImageView) convertView
		 * .findViewById(R.id.alert_date_icon_id);
		 */

        // 阳历日期文本控件
        TextView dateTextView = (TextView) convertView
                .findViewById(R.id.alert_date_text_id);
        // 阴历日期文本控件
        TextView chinaDateTextView = (TextView) convertView
                .findViewById(R.id.alert_china_date_text_id);
        ImageView imageView = (ImageView) convertView
                .findViewById(R.id.alert_date_icon_id);

        DayNumber dayNumber = dayNumbers.get(position);
        String dateString = dayNumber.day + ""; // dayNumber[position].split("\\.")[0];
        String chinaDateString = dayNumber.chinaDayString;

        dateTextView.setText(dateString);
        chinaDateTextView.setText(chinaDateString);
        // for (AlertSetting alertSetting : alertSettings) {
        // if (dayNumber.day == alertSetting.getDay()
        // && dayNumber.month == alertSetting.getMonth() + 1
        // && dayNumber.year == alertSetting.getYear()) {
        // imageView.setBackgroundResource(R.drawable.dot_2x);
        // viewHolder.ifExistsAlert = true;
        // }
        // }
        setDateTextColor(dateTextView, chinaDateTextView, position,
                daysOfMonth, dayOfWeek);
        convertView.setTag(viewHolder);
        if (currentFlag == position) {
            // TODO 设置当天的背景
            // View checkedView = convertView;
            /*
             * AlertCalendarActivity.previousOnclickDateViewPosition[0] =
			 * position;
			 * AlertCalendarActivity.previousOnclickDateViewPosition[1] =
			 * daysOfMonth;
			 * AlertCalendarActivity.previousOnclickDateViewPosition[2] =
			 * dayOfWeek; setCheckedBackground(convertView);
			 * AlertCalendarActivity.previousOnclickDateView = convertView;
			 */
            new CalendarItemOnclickListener(context, activity).onItemClick(
                    (AdapterView<?>) parent, convertView, position, position);
        }

        final View dateView = convertView;
        ViewTreeObserver viewTreeObserver = dateView.getViewTreeObserver();
        viewTreeObserver
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        dateView.getViewTreeObserver().removeOnPreDrawListener(
                                this);
                        int height = dateView.getMeasuredHeight();
                        int width = dateView.getMeasuredWidth();
                        ViewGroup.LayoutParams layoutParams = dateView
                                .getLayoutParams();
                        layoutParams.width = height;
                        dateView.setLayoutParams(layoutParams);
                        Log.d(TAG, "convertView: " + height + " , " + width);
                        return true;
                    }
                });
        return dateView;
    }

    /**
     * 设置字体颜色
     */
    public void setDateTextColor(TextView dateTextView,
                                 TextView chinaDateTextView, int position, int daysOfMonth,
                                 int dayOfWeek) {
        dateTextView.setTextColor(Color.parseColor("#b1b1b1"));// 当月字体设黑
        chinaDateTextView.setTextColor(dateTextView.getTextColors());// 当月字体设黑
        if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
            // 当前月信息显示
            dateTextView.setTextColor(Color.parseColor("#666666"));// 当月字体设黑
            chinaDateTextView.setTextColor(dateTextView.getTextColors());// 当月字体设黑
            if (position % 7 == 0 || position % 7 == 6) {
                // 当前月信息显示
                dateTextView.setTextColor(Color.parseColor("#b1b1b1"));// 当月字体设黑
                chinaDateTextView.setTextColor(dateTextView.getTextColors());// 当月字体设黑
            }
        }
    }

    // 得到某年的某月的天数且这月的第一天是星期几
    public void getCalendar(int year, int month) {
        isLeapyear = specialCalendar.isLeapYear(year); // 是否为闰年
        daysOfMonth = specialCalendar.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        dayOfWeek = specialCalendar.getWeekdayOfMonth(year, month); // 某月第一天为星期几
        lastDaysOfMonth = specialCalendar.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
        Log.d("DAY", isLeapyear + " ======  " + daysOfMonth
                + "  ============  " + dayOfWeek + "  =========   "
                + lastDaysOfMonth);
        getDayNumber(year, month);
    }

    /**
     * 设置选中时的背景色
     *
     * @param view
     * @return view
     */
    public View setCheckedBackground(View view) {
        CalendarItemViewHolder viewHolder = (CalendarItemViewHolder) view
                .getTag();
        LinearLayout linearLayout = (LinearLayout) view;
        linearLayout.setBackgroundResource(R.drawable.calendar_background);
        ImageView imageView = (ImageView) linearLayout.getChildAt(2);
        ImageView imageView2 = (ImageView) linearLayout.getChildAt(3);
        TextView dateTextView = (TextView) linearLayout.getChildAt(0);
        TextView chinaDateTextView = (TextView) linearLayout.getChildAt(1);
        if (viewHolder.ifExistsAlert) {
            imageView2.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }
        dateTextView.setTextColor(Color.WHITE);
        chinaDateTextView.setTextColor(Color.WHITE);
        return view;
    }

    /**
     * 原始样式view
     *
     * @param oldView
     * @param newView
     */
    public void recoverStyle(View view, int[] defaultViewPosition) {
        // default
        /*
		 * LinearLayout defaultViewLinearLayout = (LinearLayout)defaultView;
		 * ImageView defaultViewImageView = (ImageView)
		 * defaultViewLinearLayout.getChildAt(0); TextView
		 * defaultViewDateTextView =
		 * (TextView)defaultViewLinearLayout.getChildAt(1); TextView
		 * defaultViewChinaDateTextView =
		 * (TextView)defaultViewLinearLayout.getChildAt(2);
		 */
        // new
        LinearLayout viewLinearLayout = (LinearLayout) view;
        viewLinearLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        ImageView viewImageView = (ImageView) viewLinearLayout.getChildAt(2);
        ImageView view2ImageView = (ImageView) viewLinearLayout.getChildAt(3);
        viewImageView.setVisibility(View.VISIBLE);
        view2ImageView.setVisibility(View.GONE);
        TextView viewDateTextView = (TextView) viewLinearLayout.getChildAt(0);
        TextView viewChinaDateTextView = (TextView) viewLinearLayout
                .getChildAt(1);
        // if(defaultViewImageView.getVisibility() == View.VISIBLE){
        // viewImageView.setBackgroundResource(R.drawable.dot_2x);
        setDateTextColor(viewDateTextView, viewChinaDateTextView,
                defaultViewPosition[0], defaultViewPosition[1],
                defaultViewPosition[2]);

    }

    // 将一个月中的每一天的值添加入数组dayNumber中
    private void getDayNumber(int year, int month) {
        int j = 1;
        int flag = 0;
        String lunarDay = "";
        for (int i = 0; i < dayNumberLength; i++) {
            DayNumber dayNumber = new DayNumber();
            dayNumber.setYear(year);
            // 周一
            // if(i<7){
            // dayNumber[i]=week[i]+"."+" ";
            // }
            if (i < dayOfWeek) { // 前一个月
                int temp = lastDaysOfMonth - dayOfWeek + 1;
                lunarDay = lunarCalendar.getLunarDate(year, month - 1,
                        temp + i, false);
                dayNumber.setMonth(month - 1);
                dayNumber.setDay(temp + i);
                dayNumber.setChinaDayString(lunarDay);

            } else if (i < daysOfMonth + dayOfWeek) { // 本月
                int day = i - dayOfWeek + 1; // 得到的日期
                lunarDay = lunarCalendar.getLunarDate(year, month, i
                        - dayOfWeek + 1, false);
                dayNumber.setMonth(month);
                dayNumber.setDay(day);
                dayNumber.setChinaDayString(lunarDay);
                // 对于当前月才去标记当前日期
                if (systemYear == year && systemMonth + 1 == month
                        && systemDay == day) {
                    // 标记当前日期
                    currentFlag = i;
                }
                setShowYear(year);
                setShowMonth(month);
                setAnimalsYear(lunarCalendar.animalsYear(year));
                setLeapMonth(lunarCalendar.leapMonth == 0 ? 0
                        : lunarCalendar.leapMonth);
                setCyclical(lunarCalendar.cyclical(year));
            } else { // 下一个月
                lunarDay = lunarCalendar
                        .getLunarDate(year, month + 1, j, false);
                dayNumber.setMonth(month + 1);
                dayNumber.setDay(j);
                dayNumber.setChinaDayString(lunarDay);
                j++;
            }
            dayNumbers.add(dayNumber);
        }
        String abc = "";
        for (int i = 0; i < dayNumberLength; i++) {
            abc = abc + dayNumbers.get(i).getYear() + "-"
                    + dayNumbers.get(i).getMonth() + "-"
                    + dayNumbers.get(i).getDay() + " "
                    + dayNumbers.get(i).getChinaDayString();
            if ((i + 1) % 7 == 0) {
                abc = abc + "\n";
            }
        }
        Log.d(TAG, "DAYNUMBER" + abc);

    }

    public void matchScheduleDate(int year, int month, int day) {

    }

    /**
     * 点击每一个item时返回item中的日期
     *
     * @param position
     * @return
     */
    public DayNumber getDateByClickItem(int position) {
        return dayNumbers.get(position);
    }

    /**
     * 在点击gridView时，得到这个月中第一天的位置
     *
     * @return
     */
    public int getStartPositon() {
        return dayOfWeek + 7;
    }

    /**
     * 在点击gridView时，得到这个月中最后一天的位置
     *
     * @return
     */
    public int getEndPosition() {
        return (dayOfWeek + daysOfMonth + 7) - 1;
    }

    public int getShowYear() {
        return showYear;
    }

    public void setShowYear(int showYear) {
        this.showYear = showYear;
    }

    public int getShowMonth() {
        return showMonth;
    }

    public void setShowMonth(int showMonth) {
        this.showMonth = showMonth;
    }

    public String getAnimalsYear() {
        return animalsYear;
    }

    public void setAnimalsYear(String animalsYear) {
        this.animalsYear = animalsYear;
    }

    public int getLeapMonth() {
        return leapMonth;
    }

    public void setLeapMonth(int leapMonth) {
        this.leapMonth = leapMonth;
    }

    public String getCyclical() {
        return cyclical;
    }

    public void setCyclical(String cyclical) {
        this.cyclical = cyclical;
    }

    public static class CalendarItemViewHolder {
        public boolean ifExistsAlert = false;
    }

    public class DayNumber {
        private int year;
        private int month;
        private int day;
        private int week;
        private String chinaDayString;

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            if (month < 0) {
                this.year -= 1;
                this.month = 12;
                return;
            }

            if (month > 12) {
                this.year += 1;
                this.month = month - 12;
                return;
            }
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public String getChinaDayString() {
            return chinaDayString;
        }

        public void setChinaDayString(String chinaDayString) {
            this.chinaDayString = chinaDayString;
        }
    }
}
