package com.example.inkclock;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    // prosta zmiana by sprawdzić gita

    private TextView tvDateTime;
    private AnalogClockView clockView;

    private final Handler handler = new Handler();

    private final Handler uiHandler = new Handler();

    private static final long HIDE_DELAY_MS = 15_000; // 15 sekund

    private static final long DOUBLE_TAP_MS = 350; // okno na 2 kliknięcia
    private long lastTapMs = 0L;


    private final Runnable hideStatusBarRunnable = new Runnable() {
        @Override
        public void run() {
            hideStatusBar();
        }
    };

    private void hideStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void showStatusBar() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void scheduleHideStatusBar() {
        uiHandler.removeCallbacks(hideStatusBarRunnable);
        uiHandler.postDelayed(hideStatusBarRunnable, HIDE_DELAY_MS);
    }


    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault());

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            updateUi();

            long now = System.currentTimeMillis();
            long nextMinute = ((now / 60000L) + 1L) * 60000L;
            long delay = nextMinute - now;

            handler.postDelayed(this, delay);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        tvDateTime = findViewById(R.id.tvDateTime);
        clockView = findViewById(R.id.clockView);

        findViewById(android.R.id.content).setOnClickListener(v -> {
            long now = android.os.SystemClock.uptimeMillis();

            if (now - lastTapMs <= DOUBLE_TAP_MS) {
                // double tap -> schowaj natychmiast
                uiHandler.removeCallbacks(hideStatusBarRunnable);
                hideStatusBar();
                lastTapMs = 0L; // reset, żeby nie łapać potrójnego jako kolejny double
            } else {
                // single tap -> pokaż i zaplanuj schowanie po 15s
                showStatusBar();
                scheduleHideStatusBar();
                lastTapMs = now;
            }
        });



    }

    @Override protected void onResume() {
        super.onResume();
        handler.removeCallbacks(tick);
        handler.post(tick); // start od razu

        showStatusBar();          // pokaż przy starcie
        scheduleHideStatusBar();  // i zaplanuj ukrycie
    }

    @Override protected void onPause() {
        super.onPause();
        handler.removeCallbacks(tick);

        uiHandler.removeCallbacks(hideStatusBarRunnable);
    }

    private void updateUi() {
        Date d = new Date();
        tvDateTime.setText(fmt.format(d));
        clockView.setTime(d);
        clockView.invalidate();
    }
}
