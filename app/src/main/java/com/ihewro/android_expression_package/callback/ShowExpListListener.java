package com.ihewro.android_expression_package.callback;

import com.ihewro.android_expression_package.bean.Expression;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface ShowExpListListener {
    public void onFinish(List<Expression> expressions);
}
