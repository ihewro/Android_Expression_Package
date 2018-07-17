package com.ihewro.android_expression_package.callback;

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface GetMainExpListener {
    public void onFinish(List<Fragment> fragmentList,List<String> pageTitleList);
}
