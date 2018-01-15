package com.android.module.retrofit;

import android.content.Context;
import android.text.TextUtils;

import com.android.module.retrofit.common.ProgressListener;
import com.android.module.retrofit.common.ProgressObserver;
import com.android.module.retrofit.common.ProgressRequestBody;
import com.android.module.retrofit.common.ProgressResponseBody;
import com.android.module.retrofit.api.CommonService;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/12/27.
 */

public class RetrofitClient {
    private static final String BASE_URL = "https://hao.360.cn";
    private static final int DEFAULT_TIMEOUT = 5;
    private static RetrofitClient sInstance;
    private Retrofit mRetrofit;
    private Map<String, Object> mServiceMap;
    private String mUrl;

    public static void init(Context context, String baseUrl) {
        if (sInstance == null) {
            synchronized (RetrofitClient.class) {
                if (sInstance == null) {
                    sInstance = new RetrofitClient(context.getApplicationContext(), baseUrl);
                }
            }
        }
    }

    public static RetrofitClient getInstance() {
        return sInstance;
    }

    private RetrofitClient() {

    }

    private RetrofitClient(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            mUrl = BASE_URL;
        } else {
            mUrl = url;
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(mUrl)
                .build();
        mServiceMap = new HashMap<>();
    }

    public <T> T getService(final Class<T> service) {
        String className = service.getClass().getName();
        if (!mServiceMap.containsKey(className)) {
            mServiceMap.put(className, mRetrofit.create(service));
        }
        return (T) mServiceMap.get(className);
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.mRetrofit = retrofit;
    }

    public void download(String url, @NonNull final ProgressObserver observer) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponseBody(response.body(), new ProgressListener() {
                    @Override
                    public void onProgress(long progress, long total, boolean done) {
                        observer.onProgress(progress, total, done);
                    }
                })).build();
            }
        });
        OkHttpClient okHttpClient = builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(mUrl)
                .build();

        CommonService service = retrofit.create(CommonService.class);
        service.download(url).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(observer);
    }

    public void upload(String url, String filePath, @NonNull final ProgressObserver
            observer) {
        File file = new File(filePath);
        RequestBody body = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(body, new ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done) {
                observer.onProgress(progress, total, done);
            }
        });
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), progressRequestBody);
        CommonService service = getService(CommonService.class);
        service.upload(url, part).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(observer);
    }
}
