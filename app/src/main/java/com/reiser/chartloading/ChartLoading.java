package com.reiser.chartloading;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sunsharp on 16/8/31.
 */


public class ChartLoading extends View {
    private Paint mPaint;
    //View内部使用的是像素值
    private int defaultWidth = 200;
    private int defaultHeight = 200;

    private int width;
    private int height;
    private int raduis;

    private int pointNum = 5;

    private Point[] points = new Point[pointNum];
    private float moveXFloats;
    private float moveYFloats;


    private int mColor1 = Color.rgb(61, 125, 236);
    private int mColor2 = Color.rgb(227, 239, 63);
    private int mColor3 = Color.rgb(63, 200, 189);
    private int mColor4 = Color.rgb(130, 226, 107);
    private int mColor5 = Color.rgb(226, 108, 62);
    private int mColor6 = Color.rgb(123, 179, 221);

    private boolean mAutoPlay;

    private boolean needRuning = true;


    private float linePercentage = 0.0f;

    public float getLinePercentage() {
        return linePercentage;
    }

    public void setLinePercentage(float linePercentage) {
        this.linePercentage = linePercentage;
        postInvalidate();
    }

    private float rectPercentage = 0.0f;

    public float getRectPercentage() {
        return rectPercentage;
    }

    public void setRectPercentage(float rectPercentage) {
        this.rectPercentage = rectPercentage;
        postInvalidate();
    }

    private float piePercentage = 0.0f;

    public float getPiePercentage() {
        return piePercentage;
    }

    public void setPiePercentage(float piePercentage) {
        this.piePercentage = piePercentage;
        postInvalidate();
    }

    public ChartLoading(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mColor1);
        createAnimation();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        createAnimation();
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public ChartLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获得配置的自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChartLoading);
        mAutoPlay = a.getBoolean(R.styleable.ChartLoading_auto_play, true);
        a.recycle();
        init();
    }

    public ChartLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        drawLine(canvas);

        drawRect(canvas);

        drawPoint(canvas);

        drawPie(canvas);


    }

    private void drawPie(Canvas canvas) {
        if (piePercentage > 0) {
            float x = centerPointX;
            float y = centerPointY;
            float r = raduis / 20;
            r = r + r * 2 * piePercentage;
            mPaint.setColor(mColor1);

            RectF oval = new RectF(x - r, y - r, x + r, y + r);// 设置个新的长方形，扫描测量

            if (piePercentage > 0.2) {
                canvas.drawArc(oval, 0, 300, true, mPaint);
                mPaint.setColor(mColor2);
                float alpha = piePercentage * 2;
                if (alpha <= 1) {
                    mPaint.setAlpha((int) (255 * alpha));
                }
                canvas.save();
                canvas.translate(15 * piePercentage, (-15 * 0.866f) * piePercentage);
                canvas.drawArc(oval, 0, -60, true, mPaint);
                canvas.restore();
            } else {
                canvas.drawArc(oval, 0, 360, true, mPaint);

            }
//            canvas.drawCircle(x, y, r, mPaint);
        }
    }

    private void drawRect(Canvas canvas) {
        if (rectPercentage > 0 && rectPercentage < 2) {
            float bottomLine = relativeYFromView(0.9f);
            for (int i = 0; i < pointNum; i++) {
                Point item = points[i];
                mPaint.setColor(item.getColor());
                float x = centerPointX + (item.getX() - centerPointX) * moveXFloats;
                float y = centerPointY + (item.getY() - centerPointY) * moveYFloats;
                float top = y;
                float bottom = bottomLine;
                if (rectPercentage < 1) {
                    top = y + (bottomLine - y) * (1 - rectPercentage);
                } else {
                    bottom = bottomLine - (bottomLine - y) * (rectPercentage - 1);
                }

                RectF rectF = new RectF(x - item.getR(), top, x + item.getR(), bottom);
                canvas.drawRect(rectF, mPaint);
            }
        }

    }

    private void drawPoint(Canvas canvas) {
        if (piePercentage > 0)
            return;
        for (int i = 0; i < pointNum; i++) {
            Point item = points[i];
            mPaint.setColor(item.getColor());
            float x = centerPointX + (item.getX() - centerPointX) * moveXFloats;
            float y = centerPointY + (item.getY() - centerPointY) * moveYFloats;
            float bigger = 1.0f;
            if (linePercentage > 0) {
                float j = linePercentage * pointNum;
                if (j > i && j < i + 1) {
                    bigger = 1 + (j - i) * 0.2f;
                }
            }
            canvas.drawCircle(x, y, item.getR() * bigger, mPaint);

            if (rectPercentage > 0.9f && rectPercentage < 2) {
                mPaint.setColor(Color.WHITE);
                canvas.drawCircle(x, y, item.getR() / 3, mPaint);

            }
        }
    }

    private void drawLine(Canvas canvas) {
        if (linePercentage > 0) {
            Paint paint = new Paint();
            paint.setColor(mColor6);
            paint.setStyle(Paint.Style.STROKE); //一定要设置为画线条
            paint.setStrokeWidth(4);
            paint.setAntiAlias(true);
            Path path = new Path();
            path.moveTo(points[0].getX(), points[0].getY());   //定位path的起点
            path.lineTo(points[1].getX(), points[1].getY());
            path.lineTo(points[4].getX(), points[4].getY());
            path.lineTo(points[3].getX(), points[3].getY());
            path.lineTo(points[2].getX(), points[2].getY());

            PathMeasure measure = new PathMeasure(path, false);
            float length = measure.getLength();

            PathEffect effect = new DashPathEffect(new float[]{length, length}, length - length * linePercentage);
            paint.setPathEffect(effect);
            canvas.drawPath(path, paint);
        }
    }

    int mDuration = 600;
    int mDelay = 0;
    AnimatorSet animatorSet;

    public void createAnimation() {
        animatorSet = new AnimatorSet();
        mDelay = 0;

        ValueAnimator moveAnimX = ValueAnimator.ofFloat(0, 1f);
        moveAnimX.setDuration(mDuration);
        moveAnimX.setStartDelay(mDelay);
        mDelay = mDelay + mDuration;
        moveAnimX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveXFloats = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });


        ValueAnimator moveAnimY = ValueAnimator.ofFloat(0, 1f);
        moveAnimY.setDuration(mDuration);
        moveAnimY.setStartDelay(mDelay);
        mDelay = mDelay + mDuration;
        moveAnimY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveYFloats = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        Animator animatorLine = ObjectAnimator.ofFloat(this, "linePercentage", 0.0f, 1.0f);
        animatorLine.setDuration(mDuration);
        animatorLine.setStartDelay(mDelay);
        mDelay = mDelay + mDuration;


        Animator animatorLineBack = ObjectAnimator.ofFloat(this, "linePercentage", 1.0f, 2.0f);
        animatorLineBack.setDuration(mDuration);
        animatorLineBack.setStartDelay(mDelay);
        mDelay = mDelay + mDuration;

        ValueAnimator moveAnimYChange = ValueAnimator.ofFloat(1f, -1f);
        moveAnimYChange.setDuration(mDuration);
        moveAnimYChange.setStartDelay(mDelay);
        mDelay = mDelay + mDuration;
        moveAnimYChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveYFloats = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });


        Animator animatorRect = ObjectAnimator.ofFloat(this, "rectPercentage", 0.0f, 2.0f);
        animatorRect.setDuration(mDuration);
        animatorRect.setStartDelay(mDelay);
        mDelay = mDelay + mDuration;


        ValueAnimator moveAnimXCenter = ValueAnimator.ofFloat(1f, 0f);
        moveAnimXCenter.setDuration(mDuration);
        moveAnimXCenter.setStartDelay(mDelay);
        moveAnimXCenter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveXFloats = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        ValueAnimator moveAnimYCenter = ValueAnimator.ofFloat(-1f, 0f);
        moveAnimYCenter.setDuration(mDuration);
        moveAnimYCenter.setStartDelay(mDelay);
        mDelay = mDelay + mDuration;
        moveAnimYCenter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveYFloats = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        ObjectAnimator animatorPie = ObjectAnimator.ofFloat(this, "piePercentage", 0.0f, 1.0f, 0.0f);
        animatorPie.setDuration(mDuration);
        animatorPie.setInterpolator(new DecelerateAccelerateInterpolator());
        animatorPie.setStartDelay(mDelay);
        mDelay = mDelay + mDuration;

        ValueAnimator anim = ValueAnimator.ofFloat(-1f, 0f);
        anim.setDuration(mDuration / 2);
        anim.setStartDelay(mDelay);

        animatorSet.play(moveAnimX).with(moveAnimY)
                .with(animatorLine).with(animatorLineBack)
                .with(moveAnimYChange).with(animatorRect)
                .with(moveAnimXCenter).with(moveAnimYCenter)
                .with(animatorPie).with(anim)
        ;
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mAutoPlay) {
                    animatorSet.start();
                }
            }
        });

        if (mAutoPlay) {
            animatorSet.start();
        }

    }


    private float centerPointX, centerPointY;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //MeasureSpec.AT_MOST 是wrap_content的情况,处理后可以使用wrap_content
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth, defaultHeight);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, defaultHeight);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth, heightSpecSize);
        }
        width = getWidth();
        height = getHeight();
        raduis = Math.min(width, height);
        centerPointX = relativeXFromView(0.5f);
        centerPointY = relativeYFromView(0.5f);
        getPoint(centerPointX, centerPointY, raduis);


    }


    private void getPoint(float centerPointX, float centerPointY, int raduis) {
        int pointRaduis = raduis / 20;
        points[0] = new Point(centerPointX - pointRaduis * 8, centerPointY - pointRaduis * 4, pointRaduis, mColor4);
        points[1] = new Point(centerPointX - pointRaduis * 4, centerPointY + pointRaduis * 2, pointRaduis, mColor3);
        points[2] = new Point(centerPointX + pointRaduis * 8, centerPointY - pointRaduis * 2, pointRaduis, mColor5);
        points[3] = new Point(centerPointX + pointRaduis * 4, centerPointY + pointRaduis * 3, pointRaduis, mColor2);
        points[4] = new Point(centerPointX, centerPointY - pointRaduis, pointRaduis, mColor1);


    }


    private float relativeXFromView(float percent) {
        return getWidth() * percent;
    }

    private float relativeYFromView(float percent) {
        return getHeight() * percent;
    }


    public void startLoading() {
        if (animatorSet != null && !animatorSet.isRunning()) {
            mAutoPlay = true;
            animatorSet.start();
        }
    }

    public void stopLoading() {
        if (animatorSet != null && (animatorSet.isStarted() || animatorSet.isRunning())) {
            mAutoPlay = false;
            animatorSet.end();
        }
    }


}
