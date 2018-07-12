package com.ihewro.android_expression_package.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.callback.GetExpFolderDataListener;
import com.ihewro.android_expression_package.task.AppStartTask;
import com.ihewro.android_expression_package.util.APKVersionCodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.textView2)
    TextView versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        ButterKnife.bind(this);
        versionName.setText("v" + APKVersionCodeUtils.getVerName(this));

        new AppStartTask(new GetExpFolderDataListener() {
            @Override
            public void onFinish(String jsonString) {
                finish();
                MainActivity.actionStart(WelcomeActivity.this);
            }
        }).execute();

    }

}
