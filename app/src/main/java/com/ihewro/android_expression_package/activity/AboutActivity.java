package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ihewro.android_expression_package.R;

public class AboutActivity extends AppCompatActivity {

    public static void actionStart(Activity activity){
        Intent intent = new Intent(activity,AboutActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}
