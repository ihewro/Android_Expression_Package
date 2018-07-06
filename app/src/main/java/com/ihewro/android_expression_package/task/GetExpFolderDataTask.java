package com.ihewro.android_expression_package.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.blankj.ALog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.OneDetail;
import com.ihewro.android_expression_package.bean.OneDetailList;
import com.ihewro.android_expression_package.callback.GetExpFolderDataListener;
import com.ihewro.android_expression_package.http.HttpUtil;
import com.ihewro.android_expression_package.http.WebImageInterface;
import com.ihewro.android_expression_package.util.DataUtil;
import com.ihewro.android_expression_package.util.DateUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.litepal.LitePal;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.lang.Thread.sleep;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GetExpFolderDataTask extends AsyncTask<Void,Integer,String> {

    GetExpFolderDataListener listener;
    private boolean getOnes = false;
    long beginTime;
    long endTime;

    public GetExpFolderDataTask(GetExpFolderDataListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        beginTime = System.currentTimeMillis();
        //获取one数据库信息
        List<OneDetailList> oneDetailListList = LitePal.findAll(OneDetailList.class);
        boolean flag = false;
        if (oneDetailListList.size()>0){
            if (DateUtil.isTimeout(DateUtil.getNowDateStr(),oneDetailListList.get(0).getDate())){//超时了，需要更新数据库信息
                flag = true;
            }
        }else {//数据库中没有内容获取新的请求，更新数据库信息
            flag = true;
        }

        Call<OneDetailList> call = null;
        if (flag){

            call = HttpUtil.getOnes(new Callback<OneDetailList>() {
                @Override
                public void onResponse(@NonNull Call<OneDetailList> call, @NonNull Response<OneDetailList> response) {

                    //获取数据成功后删除旧的数据
                    LitePal.deleteAll(OneDetailList.class);
                    LitePal.deleteAll(OneDetail.class);

                    //存储新的数据
                    final OneDetailList oneDetailList = response.body();
                    oneDetailList.save();

                    for (int i =0;i<oneDetailList.getCount();i++){
                        OneDetail oneDetail = oneDetailList.getOneDetailList().get(i);
                        oneDetail.setOneDetailList(oneDetailList);
                        oneDetail.save();
                        getOnes = true;
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OneDetailList> call, @NonNull Throwable t) {
                    //什么也不做
                    getOnes = true;
                    ALog.d("请求失败" + t.getMessage());
                }
            });
        }

        //获取表情包数据库信息
        List<ExpressionFolder> expressionFolderList = LitePal.findAll(ExpressionFolder.class,true);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(expressionFolderList);
            while (true){
                ALog.d("getones",getOnes);
                endTime = System.currentTimeMillis();

                if (endTime - beginTime > 1500){//最大容忍度为1.5s，1.5秒还没有请求就歇着吧，用户不然就要关闭应用了
                    if (call!=null){
                        //请求超时了
                        ALog.d("请求超时了");
                        call.cancel();
                    }
                    getOnes = true;
                }
                if (getOnes){
                    break;
                }else {
                    sleep(10);
                }
            }

            endTime = System.currentTimeMillis();
            long diff = endTime - beginTime;//执行的毫秒数
            if (diff <1700){//再让他睡会，否则启动页面都看不见
                sleep(1700-diff);
            }
            return jsonString;
        } catch (JsonProcessingException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onFinish(s);
    }

}
