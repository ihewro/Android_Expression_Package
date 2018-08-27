package com.ihewro.android_expression_package.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.blankj.ALog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.litepal.LitePal;

import java.io.File;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GetExpDesTask extends AsyncTask<Expression,Void,Void> {

    private Activity activity;
    private int count = 0;
    private boolean isRepeat;

    GetExpDesTask(Activity activity,boolean isRepeat) {
        this.activity = activity;
        this.isRepeat = isRepeat;
    }

    public GetExpDesTask(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    @Override
    protected Void doInBackground(Expression... expressions) {
        final Expression expression = expressions[0];
        final File tempFile = new File(GlobalConfig.appTempDirPath + expression.getName());
        FileUtil.bytesSavedToFile(expression,tempFile);

        if (expression.getDesStatus() == 0){
            GeneralBasicParams param = new GeneralBasicParams();
            param.setDetectDirection(true);
            param.setImageFile(tempFile);
            OCR.getInstance(UIUtil.getContext()).recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
                @Override
                public void onResult(GeneralResult result) {
                    StringBuilder sb = new StringBuilder();
                    for (WordSimple wordSimple : result.getWordList()) {
                        WordSimple word = wordSimple;
                        sb.append(word.getWords());
                        sb.append("\n");
                    }
                    if (sb.length()>1){
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    expression.setDesStatus(1);
                    expression.setDescription(sb.toString());
                    expression.save();
                    ALog.d(sb);
                    count ++;
                    ALog.d("获取文字" + count + "次");
                    tempFile.delete();
                }

                @Override
                public void onError(OCRError error) {
                    ALog.d(error.getMessage());
                    //Toasty.info(activity,expression.getName()+"表情的描述自动获取失败，你可以稍后手动识别描述").show();
                    if (isRepeat){
                        new GetExpDesTask(isRepeat).execute(expression);
                    }
                    count ++;
                    ALog.d("获取文字" + count + "次");
                    tempFile.delete();
                }
            });
        }
        return null;
    }

}
