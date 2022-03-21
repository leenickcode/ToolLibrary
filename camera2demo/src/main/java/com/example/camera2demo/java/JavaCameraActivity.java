package com.example.camera2demo.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.camera2demo.R;

public class JavaCameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_camera);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentd, new CameraFragment());
        transaction.commit();
    }
    protected void hideBottomMenu() {
        final android.view.View decorView = getWindow().getDecorView();
        final int option = 0x16F3006;
        decorView.setSystemUiVisibility(option);
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & android.view.View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(option);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomMenu();
    }
}