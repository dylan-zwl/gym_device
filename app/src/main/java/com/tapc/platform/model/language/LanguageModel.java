package com.tapc.platform.model.language;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by Administrator on 2017/10/25.
 */

public class LanguageModel {

    public enum Language {
        CHINESE("chinese", "zh", "CN"), ENGLISH("english", "en", "TR");

        public String languageName;
        public String languageCode;
        public String countryCode;

        Language(String language, String languageCode, String countryCode) {
            this.languageName = language;
            this.languageCode = languageCode;
            this.countryCode = countryCode;
        }
    }

    private static Locale getLocale(Language language) {
        return new Locale(language.languageCode, language.countryCode);
    }

    public static boolean setSystemLanguage(Language language) {
        try {
            if (language != null) {
                IActivityManager amn = null;
                Configuration config = null;
                amn = ActivityManagerNative.getDefault();
                config = amn.getConfiguration();
                config.userSetLocale = true;
                config.locale = getLocale(language);
                amn.updateConfiguration(config);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getLanguageIndex(Language language) {
        String languageStr = "";
        Language[] list = Language.values();
        for (int i = 0; i < list.length; i++) {
            if (language.languageName.equals(list[i].languageName)) {
                return language.languageName;
            }
        }
        return languageStr;
    }

    public static Language getLanguage(String language) {
        Language[] list = Language.values();
        for (int i = 0; i < list.length; i++) {
            if (language.equals(list[i].languageName)) {
                return list[i];
            }
        }
        return null;
    }
}
