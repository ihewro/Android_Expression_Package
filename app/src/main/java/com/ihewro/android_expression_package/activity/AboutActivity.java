package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.util.APKVersionCodeUtils;
import com.ihewro.android_expression_package.util.ToastUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.loading_tip)
    TextView loadingTip;


    //毫秒
    private long lastClickTime = -1;
    private long thisClickTime = -1;
    private int clickTimes = 0;

    private boolean isPlayed = false;

    private MediaPlayer mMediaPlayer; // 声明播放器

    public static void actionStart(Activity activity) {
        Intent intent = new Intent(activity, AboutActivity.class);
        activity.startActivity(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        textView2.setText("v" + APKVersionCodeUtils.getVerName(this));

        mMediaPlayer = new MediaPlayer();


        initListener();

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        imageView.setAnimation(rotateAnimation);
        imageView.startAnimation(rotateAnimation);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClickTime == -1) {
                    lastClickTime = System.currentTimeMillis();
                    thisClickTime = System.currentTimeMillis();
                } else {
                    thisClickTime = System.currentTimeMillis();
                    if (thisClickTime - lastClickTime < 500) {//是在0.8秒内点击的
                        lastClickTime = thisClickTime;
                        clickTimes++;

                        if (clickTimes > 3 && !isPlayed){
                            isPlayed = true;
                            Toasty.info(AboutActivity.this,"准备为您播放彩蛋音乐", Toast.LENGTH_LONG).show();
                            String dataSource = "https://www.ihewro.com/little.mp3";
                            ALog.d("播放地址为" + dataSource);

                            try {
                                mMediaPlayer.reset();
                                // 播放器绑定资源
                                mMediaPlayer.setDataSource(dataSource);
                                // 播放器准备,开启了子线程，准备好了会回调函数
                                mMediaPlayer.prepareAsync();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if (isPlayed){
                            Toasty.info(AboutActivity.this,"已经为你播放过啦~",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        lastClickTime = -1;
                        thisClickTime = -1;
                        clickTimes = 0;
                    }
                }
            }
        });
    }

    private void initListener(){
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ALog.d("播放器已经准备好");
                // 播放
                mMediaPlayer.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }
}
