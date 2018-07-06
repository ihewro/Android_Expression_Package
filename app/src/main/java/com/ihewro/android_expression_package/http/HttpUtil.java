package com.ihewro.android_expression_package.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.blankj.ALog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.util.UIUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class HttpUtil {

    /**
     * 自定义okhttp拦截器，以便能够打印请求地址、请求头等请求信息
     * @param readTimeout 单位s
     * @param writeTimeout 单位s
     * @param connectTimeout 单位s
     * @return OkHttpClient
     */
    public static OkHttpClient getOkHttpClient(int readTimeout, int writeTimeout, int connectTimeout){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                ALog.dTag("RetrofitLog","retrofitBack = "+message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = ProgressManager.getInstance().with(new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .readTimeout(readTimeout, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(writeTimeout,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(connectTimeout,TimeUnit.SECONDS)//设置连接超时时间
        ).build();

        return client;
    }

    /**
     * 返回retrofit的实体对象
     * @param readTimeout
     * @param writeTimeout
     * @param connectTimeout
     * @return
     */
    public static Retrofit getRetrofit(int readTimeout, int writeTimeout, int connectTimeout){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalConfig.serverUrl)
                .client(HttpUtil.getOkHttpClient(readTimeout,writeTimeout,connectTimeout))
                .addConverterFactory(JacksonConverterFactory.create())//retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了
                .build();

        return retrofit;
    }

    /**
     * make true current connect service is wifi
     */
    public static boolean isWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) UIUtil.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    public static void getExpressionList(int dirId, int page, int pageSize,String dirName, Callback<List<Expression>> callback){

        Retrofit retrofit = HttpUtil.getRetrofit(10,10,10);
        WebImageInterface request = retrofit.create(WebImageInterface.class);
        Call<List<Expression>> call = request.getDirDetail(dirId,dirName,1,pageSize);

        call.enqueue(callback);
    }




}
