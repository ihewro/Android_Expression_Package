package com.ihewro.android_expression_package.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.callback.GetExpFolderDataListener;
import com.ihewro.android_expression_package.task.GetExpFolderDataTask;

import static java.lang.Thread.sleep;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);


        new GetExpFolderDataTask(new GetExpFolderDataListener() {
            @Override
            public void onFinish(String jsonString) {
                finish();
                MainActivity.actionStart(WelcomeActivity.this,jsonString);
            }
        }).execute();

    }

}
