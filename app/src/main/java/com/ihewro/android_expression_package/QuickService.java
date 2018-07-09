package com.ihewro.android_expression_package;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

import com.ihewro.android_expression_package.activity.MainActivity;
import com.ihewro.android_expression_package.activity.WelcomeActivity;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickService extends TileService {
    public QuickService() {
    }

    @Override
    public void onClick() {
        super.onClick();
        Intent intent = new Intent(this,WelcomeActivity.class);
        startActivityAndCollapse(intent);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

}
