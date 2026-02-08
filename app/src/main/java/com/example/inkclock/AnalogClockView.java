package com.example.inkclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class AnalogClockView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Date time = new Date();

    public AnalogClockView(Context context) { super(context); init(); }
    public AnalogClockView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public AnalogClockView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
    }

    public void setTime(Date d) { this.time = d; }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        int r = Math.min(cx, cy) - 8;

        // tarcza
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        canvas.drawCircle(cx, cy, r, paint);

        // znaczniki godzin
        paint.setStrokeWidth(3f);
        for (int i = 0; i < 12; i++) {
            double ang = Math.toRadians(i * 30.0 - 90.0);
            float x1 = (float) (cx + Math.cos(ang) * (r - 12));
            float y1 = (float) (cy + Math.sin(ang) * (r - 12));
            float x2 = (float) (cx + Math.cos(ang) * (r - 2));
            float y2 = (float) (cy + Math.sin(ang) * (r - 2));
            canvas.drawLine(x1, y1, x2, y2, paint);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        float hourAngle = (hour + minute / 60f) * 30f - 90f;
        float minAngle  = minute * 6f - 90f;

        // godzinowa
        paint.setStrokeWidth(6f);
        drawHand(canvas, cx, cy, r * 0.55f, hourAngle);

        // minutowa
        paint.setStrokeWidth(4f);
        drawHand(canvas, cx, cy, r * 0.80f, minAngle);

        // środek
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, 6f, paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void drawHand(Canvas canvas, int cx, int cy, float len, float angleDeg) {
        double ang = Math.toRadians(angleDeg);
        float x = (float) (cx + Math.cos(ang) * len);
        float y = (float) (cy + Math.sin(ang) * len);
        canvas.drawLine(cx, cy, x, y, paint);
    }
}
