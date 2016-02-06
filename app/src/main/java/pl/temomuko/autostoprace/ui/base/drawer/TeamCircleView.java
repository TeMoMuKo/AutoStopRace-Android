package pl.temomuko.autostoprace.ui.base.drawer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import pl.temomuko.autostoprace.R;

/**
 * Created by szymen on 2016-02-06.
 */
public class TeamCircleView extends View {

    private int mCircleColor;
    private int mTextColor;
    private int mCircleWidth;
    private int mCircleHeight;
    private int mTeamId = 0;
    private float mCircleTextSize;
    private Paint mCirclePaint;
    private Paint mTextPaint;

    public TeamCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
        setupCirclePaint();
        setupTextPaint();
    }

    public TeamCircleView(Context context) {
        this(context, null);
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TeamCircleView);
            try {
                mCircleColor = array.getColor(R.styleable.TeamCircleView_circleColor, Color.BLACK);
                mTextColor = array.getColor(R.styleable.TeamCircleView_textColor, Color.BLACK);
                mCircleTextSize = array.getDimension(R.styleable.TeamCircleView_textSize,
                        getResources().getDimension(R.dimen.default_team_circle_text_size));
            } finally {
                array.recycle();
            }
        }
    }

    private void setupCirclePaint() {
        mCirclePaint = new Paint();
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
        mCircleWidth = getMeasuredWidth();
        mCircleHeight = getMeasuredHeight();
        super.onDraw(canvas);
        canvas.drawCircle(mCircleWidth / 2, mCircleHeight / 2, mCircleWidth / 2, mCirclePaint);
        float xPos = canvas.getWidth() / 2;
        float yPos =  ((mCircleHeight / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
        canvas.drawText("#" + mTeamId, xPos, yPos, mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTeamId(int teamId) {
        mTeamId = teamId;
    }
}

