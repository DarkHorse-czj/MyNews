package com.example.darkhorse.mynews.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.darkhorse.mynews.R;

import java.util.List;

/**
 * Created by DarkHorse on 2016/10/29.
 */

public class ViewPagerIndicator extends LinearLayout {

    private float mIndicatorWidth;  //指示器宽度
    private float mIndicatorHeight; //指示器高度
    private float DIMENSION_INDICATOR_WIDTH = getScreenWidth() / 3;//指示器的最大宽度
    private float mInitTranslationX;    //初始时，指示的偏移量
    private float mTranslationX;    //手指滑动时的偏移量

    private Paint mPaint;   //绘制指示器的画笔
    private Path mPath;  //mPath构成指示器形状
    private static final float RADIO_LINE = 3.5f / 5;   //下划线的宽度为单个Tab的4/5
    private static final float RADIO_TRIANGEL = 1.0f / 5;   //三角形的宽度为单个Tab的1/5

    private List<String> mTabTitles;    //Tab显示的内容
    private int mTabVisibleCount = DEFAULT_COUNT_TAB;    //Tab的数量
    private int mTextColorNormal = DEFAULT_TEXT_COLOR_NORMAL;    //标题正常显示的颜色
    private int mTextColorChose = DEFAULT_TEXT_COLOR_CHOSE;    //标题选中的颜色
    private int mTextSize = DEFAULT_TEXT_SIZE;  //标题字体大小
    private int mType = DEFAULT_TYPE;  //指示器形状：1为下划线,2为三角形

    private static final int DEFAULT_TYPE = 0;  //默认不显示指示器
    private static final int DEFAULT_COUNT_TAB = 4;    //默认Tab的数量
    private static final int DEFAULT_TEXT_COLOR_NORMAL = 0xFF808080;    //默认Tab正常显示的颜色（灰色）
    private static final int DEFAULT_TEXT_COLOR_CHOSE = 0xFFFF0000;    //默认Tab选中的颜色（红色）
    private static final int DEFAULT_TEXT_SIZE = 16; //默认字体大小

    public ViewPager mViewPager;    //绑定的ViewPager


    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .ViewPagerIndicator, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ViewPagerIndicator_count:
                    mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_count,
                            DEFAULT_COUNT_TAB);
                    break;
                case R.styleable.ViewPagerIndicator_type:
                    mType = a.getInt(R.styleable.ViewPagerIndicator_type, DEFAULT_TYPE);
                    break;
                case R.styleable.ViewPagerIndicator_color_text_normal:
                    mTextColorNormal = a.getColor(R.styleable.ViewPagerIndicator_color_text_normal,
                            DEFAULT_TEXT_COLOR_NORMAL);
                    break;
                case R.styleable.ViewPagerIndicator_color_text_chose:
                    mTextColorChose = a.getColor(R.styleable.ViewPagerIndicator_color_text_chose,
                            DEFAULT_TEXT_COLOR_CHOSE);
                    break;
                case R.styleable.ViewPagerIndicator_textSize:
                    mTextSize = a.getInt(R.styleable.ViewPagerIndicator_textSize,
                            DEFAULT_TEXT_SIZE);
                    break;
            }

        }
        a.recycle();
    }

    /**
     * 设置布局中view的一些必要属性，如果设置了setTabTitles，布局中view则无效
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        if (cCount == 0)
            return;
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        // 设置点击事件
        setItemClickEvent();
    }

    private void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    public int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        switch (mType) {
            case 0:
                return;

            case 1:
                mIndicatorWidth = (int) (w / mTabVisibleCount * RADIO_LINE);
                mIndicatorWidth = Math.min(DIMENSION_INDICATOR_WIDTH * RADIO_LINE, mIndicatorWidth);
                break;

            case 2:
                mIndicatorWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGEL);
                mIndicatorWidth = Math.min(DIMENSION_INDICATOR_WIDTH * RADIO_TRIANGEL,
                        mIndicatorWidth);
                break;
        }
        //初始化指示器
        initPath();
        mInitTranslationX = (getWidth() / mTabVisibleCount - mIndicatorWidth) / 2;
    }

    private void initPath() {
        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mTextColorChose);   //选择字体颜色与指示器颜色相同
        mPaint.setStyle(Paint.Style.FILL);
        if (mType == 1) {
            mPaint.setPathEffect(new CornerPathEffect(4));
        } else {
            mPaint.setPathEffect(new CornerPathEffect(3));
        }
//
        if (mType == 1) {
            //初始化矩形下划线
            mPath = new Path();
            mIndicatorHeight = 3;
            mPath.moveTo(0, 0);
            mPath.lineTo(mIndicatorWidth, 0);
            mPath.lineTo(mIndicatorWidth, -mIndicatorHeight);
            mPath.lineTo(0, -mIndicatorHeight);
            mPath.close();
        } else {
            //初始化三角型
            mPath = new Path();
            mIndicatorHeight = (int) (mIndicatorWidth / 2 / Math.sqrt(2));
            mPath.moveTo(0, 0);
            mPath.lineTo(mIndicatorWidth, 0);
            mPath.lineTo(mIndicatorWidth / 2, -mIndicatorHeight);
            mPath.close();
        }
    }

    /**
     * 绘制指示器
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {

        if (mType != 0) {
            canvas.save();
            // 将画布平移到正确的位置
            canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 1);
            canvas.drawPath(mPath, mPaint);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
    }

    /**
     * 对外的ViewPager的回调接口
     */
    public interface PageChangeListener {
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);

    }

    // 对外的ViewPager的回调接口
    private PageChangeListener onPageChangeListener;

    // 对外的ViewPager的回调接口的设置
    public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
        this.onPageChangeListener = pageChangeListener;
    }

    public void setViewPager(ViewPager viewPager, int pos) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

                // 滚动
                scroll(position, positionOffset);

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position,
                            positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                resetTextViewColor();
                choseTextView(position);
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
        // 设置当前页
        mViewPager.setCurrentItem(pos);
        choseTextView(pos);
    }

    /**
     * 设置被选中的Tab标题颜色
     *
     * @param position
     */
    protected void choseTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(mTextColorChose);
        }

    }

    /**
     * 重置Tab标题颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(mTextColorNormal);
            }
        }
    }

    /**
     * 指示器跟随手指或ViePager滚动
     *
     * @param position
     * @param positionOffset
     */
    private void scroll(int position, float positionOffset) {
        // 不断改变偏移量，invalidate
        mTranslationX = getWidth() / mTabVisibleCount * (position + positionOffset);

        int tabWidth = getScreenWidth() / mTabVisibleCount;

        // 容器滚动，当移动到倒数最后一个的时候，开始滚动
        if (positionOffset > 0 && getChildCount() > mTabVisibleCount && position >=
                (mTabVisibleCount / 2) && (getChildCount() - position) > (mTabVisibleCount / 2)) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount / 2)) * tabWidth
                        + (int) (tabWidth * positionOffset), 0);
            } else
            // 为count为1时 的特殊处理
            {
                this.scrollTo(
                        position * tabWidth + (int) (tabWidth * positionOffset), 0);
            }
        }

        invalidate();
    }


    /**
     * 设置可见的tab的数量
     *
     * @param count
     */
    public void setVisibleTabCount(int count) {
        this.mTabVisibleCount = count;
    }

    /**
     * 设置tab的标题内容 可选，可以自己在布局文件中写死
     *
     * @param datas
     */
    public void setTabItemTitles(List<String> datas) {
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.size() > 0) {
            this.removeAllViews();
            this.mTabTitles = datas;
            for (String title : mTabTitles) {
                // 添加view
                addView(generateTextView(title));
            }
            // 设置item的click事件
            setItemClickEvent();
        }
    }

    /**
     * 根据标题生成需要的TextView
     *
     * @param text
     * @return
     */
    private TextView generateTextView(String text) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(mTextColorNormal);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        tv.setLayoutParams(lp);
        return tv;
    }

}
