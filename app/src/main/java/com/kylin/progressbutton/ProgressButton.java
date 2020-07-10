package com.kylin.progressbutton;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ProgressButton extends View {

    /**
     * 初始状态，进度0%
     */
    public static final int STATE_NORMAL = 0;
    /**
     * 暂停状态，进度在1%-99%之间
     */
    public static final int STATE_PROGRESS = 1;
    /**
     * 完成状态，进度100%
     */
    public static final int STATE_FINISH = 2;
    private int state = 0;

    private int progress, max;
    private int textColor;
    private float textSize;
    private int backgroundColor, foregroundColor;//背景色，前景色
    private String text, progressText, finishText;//默认文本,进度中的文本，进度完成文本
    private float corner;// 圆角的弧度
    private float border;// 边框宽度
    private OnClickListener clickListener;
    private Paint paint = new Paint();
    private RectF rectF = new RectF();//要绘制的矩形坐标，可复用
    private Bitmap progressBitmap;//代码生成的进度条图片

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
        this.backgroundColor = typedArray.getInteger(R.styleable.ProgressButton_backgroundColor, Color.WHITE);
        this.foregroundColor = typedArray.getInteger(R.styleable.ProgressButton_foregroundColor, Color.parseColor("#09c856"));
        this.textColor = typedArray.getInteger(R.styleable.ProgressButton_textColor, Color.BLACK);
        this.max = typedArray.getInteger(R.styleable.ProgressButton_max, 100);
        this.progress = typedArray.getInteger(R.styleable.ProgressButton_progress, 0);
        this.text = typedArray.getString(R.styleable.ProgressButton_text);
        this.finishText = typedArray.getString(R.styleable.ProgressButton_finishText);
        this.textSize = typedArray.getDimension(R.styleable.ProgressButton_textSize, sp2px(14));
        this.corner = typedArray.getDimension(R.styleable.ProgressButton_corner, 0);
        this.border = typedArray.getDimension(R.styleable.ProgressButton_border, dp2px(1));
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setAntiAlias(true);//抗锯齿

        //绘制背景和边框，两矩形叠加
        rectF = resetRect(0, 0, getWidth(), getHeight());
        paint.setColor(foregroundColor);
        canvas.drawRoundRect(rectF, corner, corner, paint);
        rectF = resetRect(border, border, getWidth() - border, getHeight() - border);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(rectF, corner - border, corner - border, paint);

        //绘制进度值
        if (progress > 0) {
            paint.setColor(foregroundColor);
            rectF = resetRect(0, 0, (float) progress / max * getWidth(), getHeight());
            canvas.drawBitmap(getProgressBitmap(), null, rectF, paint);
        }

        //绘制文本
        paint.setTextSize(textSize);
        FontMetrics fm = paint.getFontMetrics();
        int textColor;
        String text;
        if (progress == 0) {
            textColor = foregroundColor;
            text = TextUtils.isEmpty(this.text) ? "下载" : this.text;
        } else if (progress == max) {
            textColor = Color.WHITE;
            text = TextUtils.isEmpty(this.finishText) ? "完成" : this.finishText;
        } else {
            textColor = this.textColor;
            if (TextUtils.isEmpty(progressText)) {
                int present = progress * 100 / max;
                text = present + "%";
            } else {
                text = progressText;
            }
        }
        paint.setColor(textColor);
        float textX = (getMeasuredWidth() - paint.measureText(text)) / 2;
        float textY = (getHeight() - fm.descent - fm.ascent) / 2;
        canvas.drawText(text, textX, textY, paint);
    }

    /**
     * 可复用的矩形，避免在onDraw中频繁申请内存
     *
     * @param x      起点x坐标
     * @param y      起点y坐标
     * @param width  宽
     * @param height 高
     */
    private RectF resetRect(float x, float y, float width, float height) {
        rectF.left = x;
        rectF.top = y;
        rectF.right = width;
        rectF.bottom = height;
        return rectF;
    }

    /**
     * 代码合成的当前进度条的bitmap图片
     */
    private Bitmap getProgressBitmap() {
        if (progressBitmap == null || progressBitmap.isRecycled()) {
            progressBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(progressBitmap);
            RectF rect = new RectF(border, border, getWidth() - border, getHeight() - border);
            paint.setColor(foregroundColor);
            canvas.drawRoundRect(rect, corner - border, corner - border, paint);
        }
        return Bitmap.createBitmap(progressBitmap, 0, 0, getWidth() * progress / max, getHeight());
    }

    /**
     * 设置最大值
     *
     * @param max max
     */
    public void setMax(int max) {
        this.max = max;
        postInvalidate();
    }

    /**
     * 设置初始文本
     *
     * @param text text
     */
    public void setText(String text) {
        this.text = text;
        postInvalidate();
    }

    /**
     * 自定义进度中的文本，默认显示“5%”进度值
     */
    public void setProgressText(String progressText) {
        this.progressText = progressText;
        postInvalidate();
    }

    /**
     * 设置进度条的颜色值
     *
     * @param color color
     */
    public void setForegroundColor(int color) {
        this.foregroundColor = color;
        if (progressBitmap != null) {
            progressBitmap.recycle();
            progressBitmap = null;
        }
        postInvalidate();
    }

    /**
     * 设置进度条的背景色
     */
    @Override
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        postInvalidate();
    }

    /***
     * 设置文本的大小
     */
    public void setTextSize(int size) {
        this.textSize = size;
        postInvalidate();
    }

    /**
     * 设置文本的颜色值(进度值文本颜色，初始文本颜色同按钮颜色，进度完成文本颜色为白色)
     *
     * @param color color
     */
    public void setTextColor(int color) {
        this.textColor = color;
        postInvalidate();
    }

    /**
     * 边框宽度dp
     *
     * @param border dp值
     */
    public void setBorder(float border) {
        this.border = dp2px(border);
        if (progressBitmap != null) {
            progressBitmap.recycle();
            progressBitmap = null;
        }
        postInvalidate();
    }

    /**
     * 圆角dp
     *
     * @param corner dp值
     */
    public void setCorner(float corner) {
        this.corner = dp2px(corner);
        if (progressBitmap != null) {
            progressBitmap.recycle();
            progressBitmap = null;
        }
        postInvalidate();
    }

    public void setFinishText(String finishText) {
        this.finishText = finishText;
        postInvalidate();
    }

    /**
     * 设置进度值
     *
     * @param progress progress
     */
    public void setProgress(int progress) {
        if (progress <= 0) {
            this.progress = 0;
            state = STATE_NORMAL;
        } else if (progress >= max) {
            this.progress = max;
            state = STATE_FINISH;
        } else {
            this.progress = progress;
            state = STATE_PROGRESS;
        }
        // 设置进度之后，要求UI强制进行重绘
        postInvalidate();
    }

    public int getProgress() {
        return progress;
    }

    public String getText() {
        switch (state) {
            case ProgressButton.STATE_NORMAL:
                return text;
            case ProgressButton.STATE_PROGRESS:
                if (TextUtils.isEmpty(progressText)) {
                    int present = progress * 100 / max;
                    return present + "%";
                }
                return progressText;
            case ProgressButton.STATE_FINISH:
                return finishText;
        }
        return "";
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            performClick();
            if (clickListener != null)
                clickListener.onClick(state);
        }
        return true;
    }

    public int dp2px(float dpValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int sp2px(float spValue) {
        float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnClickListener {
        void onClick(int state);
    }

}
