package com.example.lab5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LevelView extends View {

    private Paint bubblePaint;
    private Paint levelPaint;
    private Paint textPaint;
    private Paint markerPaint;

    private float pitch = 0;
    private float roll = 0;

    private RectF levelRect;
    private Path bubblePath;


    private static final float MAX_ANGLE = 30.0f;
    private static final int BUBBLE_RADIUS = 30;
    private static final int MARKER_WIDTH = 3;

    public LevelView(Context context) {
        super(context);
        init();
    }

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LevelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        bubblePaint = new Paint();
        bubblePaint.setColor(Color.GREEN);
        bubblePaint.setAntiAlias(true);
        bubblePaint.setStyle(Paint.Style.FILL);

        levelPaint = new Paint();
        levelPaint.setColor(Color.BLACK);
        levelPaint.setAntiAlias(true);
        levelPaint.setStyle(Paint.Style.STROKE);
        levelPaint.setStrokeWidth(2);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        markerPaint = new Paint();
        markerPaint.setColor(Color.RED);
        markerPaint.setStrokeWidth(MARKER_WIDTH);
        markerPaint.setStyle(Paint.Style.STROKE);

        levelRect = new RectF();
        bubblePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        int padding = Math.max(getPaddingLeft() + getPaddingRight(),
                getPaddingTop() + getPaddingBottom()) + BUBBLE_RADIUS * 2;

        int size = Math.min(w, h) - padding;
        int left = (w - size) / 2;
        int top = (h - size) / 2;

        levelRect.set(left, top, left + size, top + size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;


        canvas.drawOval(levelRect, levelPaint);


        canvas.drawLine(levelRect.left, centerY, levelRect.right, centerY, markerPaint);
        canvas.drawLine(centerX, levelRect.top, centerX, levelRect.bottom, markerPaint);


        canvas.drawCircle(centerX, centerY, 5, markerPaint);


        float bubbleX = centerX + (roll / MAX_ANGLE) * (levelRect.width() / 2 - BUBBLE_RADIUS);
        float bubbleY = centerY - (pitch / MAX_ANGLE) * (levelRect.height() / 2 - BUBBLE_RADIUS);


        float radius = Math.min(levelRect.width(), levelRect.height()) / 2 - BUBBLE_RADIUS;
        float dx = bubbleX - centerX;
        float dy = bubbleY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > radius) {
            bubbleX = centerX + (dx / distance * radius);
            bubbleY = centerY + (dy / distance * radius);
        }


        if (Math.abs(pitch) < 1.0 && Math.abs(roll) < 1.0) {
            bubblePaint.setColor(Color.GREEN);
        } else {
            bubblePaint.setColor(Color.RED);
        }

        canvas.drawCircle(bubbleX, bubbleY, BUBBLE_RADIUS, bubblePaint);


        drawAngleMarkers(canvas, centerX, centerY, radius);
    }

    private void drawAngleMarkers(Canvas canvas, int centerX, int centerY, float radius) {

        canvas.drawLine(centerX - radius, centerY, centerX - radius - 20, centerY, markerPaint);
        canvas.drawLine(centerX + radius, centerY, centerX + radius + 20, centerY, markerPaint);
        canvas.drawLine(centerX, centerY - radius, centerX, centerY - radius - 20, markerPaint);
        canvas.drawLine(centerX, centerY + radius, centerX, centerY + radius + 20, markerPaint);


        canvas.drawText("0°", centerX + radius + 40, centerY, textPaint);
        canvas.drawText("180°", centerX - radius - 40, centerY, textPaint);
        canvas.drawText("90°", centerX, centerY - radius - 40, textPaint);
        canvas.drawText("270°", centerX, centerY + radius + 40, textPaint);
    }

    public void updateAngles(float pitch, float roll) {
        this.pitch = pitch;
        this.roll = roll;
        invalidate();
    }
}