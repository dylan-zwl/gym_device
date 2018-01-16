package com.tapc.platform.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Json解析工具类
 * Created by Administrator on 2018/1/16.
 */

public class GsonUtils {
    private static Gson mGson = new Gson();

    public static <T> T fromJson(String json, Class<T> classOfT) {
        Object object = null;
        try {
            object = mGson.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) object;
    }

    public static <T> List<T> fromJsonList(String json, Class<T> classOfT) {
        Type type = new TypeToken<List<T>>() {
        }.getType();
        List<T> list = null;
        try {
            list = mGson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String toJson(Object src) {
        String json = null;
        try {
            json = mGson.toJson(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static <T> String toJsonList(String src, Class<T> classOfT) {
        Type type = new TypeToken<List<T>>() {
        }.getType();
        String json = null;
        try {
            json = mGson.toJson(src, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
