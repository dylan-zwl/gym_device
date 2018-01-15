package com.tapc.platform.model.scancode.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/1/15.
 */

public interface ScanQrcodeService {

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> getCode(@Url String url, @FieldMap Map<String, Object> map);
}
