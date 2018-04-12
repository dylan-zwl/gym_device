package com.tapc.platform.model.scancode.api;

import com.tapc.platform.model.scancode.dao.request.UserSportsData;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/1/15.
 */

public interface ScanCodeService {

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> commonRequest(@Url String url, @FieldMap Map<String, Object> map);

    //获取设备相关信息接口
    @POST("information")
    @FormUrlEncoded
    Observable<ResponseBody> getInformation(@Field("device_id") String deviceId, @Field("type") String type);

    //上传设备信息接口
    @POST("uploadInformation")
    @FormUrlEncoded
    Observable<ResponseBody> uploadInformation(@Field("manufacturer_code") String manufacturer_code, @Field
            ("device_id") String deviceId, @Field("type") String type, @Field("version") String version, @Field
                                                       ("parameter") String parameter);

    //更新设备运行状态接口
    @POST("uploadStatus")
    @FormUrlEncoded
    Observable<ResponseBody> uploadStatus(@Field("device_id") String deviceId, @Field("status") String status);

    //获取设备故障信息接口
    @POST("uploadFault")
    @FormUrlEncoded
    Observable<ResponseBody> uploadFault(@Field("device_id") String deviceId, @Field("status") String status, @Field
            ("fault_message") String fault_message);

    //解除设备绑定接口
    @POST("unbind")
    @FormUrlEncoded
    Observable<ResponseBody> unbind(@Field("device_id") String deviceId);

    //上传运动数据接口
    @POST("uploadData")
    Observable<ResponseBody> uploadSportsData(@Body UserSportsData body);
}
