package com.ihewro.android_expression_package.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.callback.UpdateDatabaseListener;
import com.ihewro.android_expression_package.task.RecoverDataTask;
import com.ihewro.android_expression_package.task.UpdateDatabaseTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class ErrorActivity extends AppCompatActivity {


    @BindView(R.id.button7)
    Button restart;
    @BindView(R.id.button6)
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        ButterKnife.bind(this);

//        updateDatabase();

        initListener();
    }


    private void updateDatabase() {
        UpdateDatabaseTask task = new UpdateDatabaseTask(this,new UpdateDatabaseListener() {

            private MaterialDialog updateLoadingDialog;

            @Override
            public void onFinished() {
                updateLoadingDialog.setContent("终于同步完成");
                //更新RecyclerView 布局
            }

            @Override
            public void onProgress(int progress, int max) {
                if (max > 0) {
                    if (!updateLoadingDialog.isShowing()) {
                        updateLoadingDialog.setMaxProgress(max);
                        updateLoadingDialog.show();
                    }

                    if (progress > 0) {
                        updateLoadingDialog.setProgress(progress);
                    }

                }
            }

            @Override
            public void onStart() {
                updateLoadingDialog = new MaterialDialog.Builder(ErrorActivity.this)
                        .title("正在同步信息")
                        .content("发生了一个错误，我们为您重新同步数据库信息，这可能会解决该问题。")
                        .progress(false, 0, true)
                        .build();

            }
        });
        task.execute();
    }

    private void initListener() {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateDatabase();
                new RecoverDataTask(ErrorActivity.this).execute();
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新启动
                final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());
                assert config != null;
                CustomActivityOnCrash.restartApplication(ErrorActivity.this, config);

            }
        });
    }
}
