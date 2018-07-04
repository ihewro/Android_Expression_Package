package com.ihewro.android_expression_package.http;

import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.web.WebExpressionFolder;
import com.ihewro.android_expression_package.bean.web.WebExpressionFolderList;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface WebImageInterface {
    @GET("expFolderList.php")
    public Call<WebExpressionFolderList> getDirList();

    @GET("expFolderDetail.php")
    public Call<List<Expression>> getDirDetail(@Query("dir") int dir, @Query("page") int page, @Query("pageSize") int pageSize);


    @GET
    Call<ResponseBody> downloadWebExp(@Url String fileUrl);
}
