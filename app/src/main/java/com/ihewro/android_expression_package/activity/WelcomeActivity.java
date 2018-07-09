package com.ihewro.android_expression_package.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.callback.GetExpFolderDataListener;
import com.ihewro.android_expression_package.task.GetExpFolderDataTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        ButterKnife.bind(this);

        new GetExpFolderDataTask(new GetExpFolderDataListener() {
            @Override
            public void onFinish(String jsonString) {
                finish();
                MainActivity.actionStart(WelcomeActivity.this, jsonString);
            }
        }).execute();

    }

}
