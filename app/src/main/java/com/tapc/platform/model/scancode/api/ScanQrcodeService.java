package com.tapc.platform.model.scancode.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/1/15.
 */

public interface ScanQrcodeService {
    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> commonRequest(@Url String url, @FieldMap Map<String, Object> map);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> getCode(@Url String url, @Query("device_id") String deviceId, @Query("type") String type);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> uploadDevicData(@Url String url, @Query("device_id") String deviceId, @Query
            ("parameter") String parameter);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> uploadDeviceStatus(@Url String url, @Query("device_id") String deviceId, @Query
            ("status") String status);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> uploadDevicError(@Url String url, @Query("device_id") String deviceId, @Query
            ("status") String status, @Query("fault_message") String fault_message);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> unbundlingEquipment(@Url String url, @Query("device_id") String deviceId);

    @POST()
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ResponseBody> uploadSportData(@Url String url, @Body RequestBody body);
}
