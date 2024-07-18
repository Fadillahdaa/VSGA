package com.kelmobile.vsgaakhir;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_login);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenLoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // Delay 3 detik
    }
}