package pl.temomuko.autostoprace.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

import pl.temomuko.autostoprace.R;

/**
 * Created by Szymon Kozak on 2016-02-06.
 */
public class TextCircleView extends View {

    private
    @ColorInt int mCircleColor;
    private
    @ColorInt int mTextColor;
    private int mCircleWidth;
    private int mCircleHeight;
    private float mCircleTextSize;
    private Paint mCirclePaint;
    private Paint mTextPaint;
    private String mText = "";

    public TextCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
        setupCirclePaint();
        setupTextPaint();
    }

    public TextCircleView(Context context) {
        this(context, null);
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TextCircleView);
            try {
                mCircleColor = array.getColor(R.styleable.TextCircleView_circleColor, Color.BLACK);
                mTextColor = array.getColor(R.styleable.TextCircleView_textColor, Color.BLACK);
                mCircleTextSize = array.getDimension(R.styleable.TextCircleView_textSize,
                        getResources().getDimension(R.dimen.default_team_circle_text_size));
            } finally {
                array.recycle();
            }
        }
    }

    private void setupCirclePaint() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mCircleColor);
    }

    private void setupTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mCircleTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCircleWidth = getMeasuredWidth();
        mCircleHeight = getMeasuredHeight();
        canvas.drawCircle(mCircleWidth / 2, mCircleHeight / 2, mCircleWidth / 2, mCirclePaint);
        float xPos = canvas.getWidth() / 2;
        float yPos = ((mCircleHeight / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
        canvas.drawText(mText, xPos, yPos, mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setText(String text) {
        mText = text != null ? text : "";
        invalidate();
    }

    public void setCircleColor(@ColorInt int circleColor) {
        mCircleColor = circleColor;
        mCirclePaint.setColor(mCircleColor);
        invalidate();
    }
}

