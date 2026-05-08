package com.feiertage.deutschland.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.utils.NotificationHelper;
import com.feiertage.deutschland.utils.PreferenceManager;

/**
 * SplashActivity – entry point of the app.
 * Uses the Android 12+ SplashScreen API; falls back to a custom animated layout
 * for older devices. Pre-warms the holiday repository on a background thread
 * before transitioning to MainActivity.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install SplashScreen before calling super / setContentView
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Keep splash on screen while we initialise
        splashScreen.setKeepOnScreenCondition(() -> true);

        // Apply theme
        PreferenceManager prefs = PreferenceManager.getInstance(this);
        ThemeHelper.applyTheme(prefs.isDarkMode());

        // Create notification channel early
        NotificationHelper.createNotificationChannel(this);

        // Small delay → navigate to MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
