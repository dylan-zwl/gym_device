package com.android.module.retrofit.api;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/12/28.
 */

public interface CommonService {
    /**
     * 功能描述 :下载单个文件
     *
     * @param :
     */
    @Streaming/*大文件需要加入这个判断，防止下载过程中写入到内存中*/
    @GET
    Observable<ResponseBody> download(@Url String url);

    /**
     * 功能描述 : 上传多个文件
     *
     * @param :
     */
    @Multipart
    @POST()
    Observable<ResponseBody> upload(@Url String url, @Part MultipartBody.Part file);
}
