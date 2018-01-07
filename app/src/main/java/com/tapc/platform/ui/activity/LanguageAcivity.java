package com.tapc.platform.ui.activity;


import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.widget.RadioGroup;

import com.tapc.platform.R;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.language.LanguageModel;
import com.tapc.platform.ui.base.BaseActivity;

import butterknife.BindView;

public class LanguageAcivity extends BaseActivity {
    @BindView(R.id.language_select)
    RadioGroup mRadioGroup;

    private LanguageModel.Language mLanguage;

    @Override
    protected int getContentView() {
        return R.layout.activity_language;
    }


    @Override
    protected void initView() {
        super.initView();
        initCurrentLanguage();
    }

    private void initCurrentLanguage() {
        String languageIndex = ConfigModel.getLanguage(mContext, LanguageModel.Language.CHINESE.languageName);
        mLanguage = LanguageModel.getLanguage(languageIndex);
        int rid = 0;
        switch (mLanguage) {
            case CHINESE:
                rid = R.id.language_cn;
                break;
            case ENGLISH:
                rid = R.id.language_en;
                break;
        }
        if (rid != 0) {
            mRadioGroup.check(rid);
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.language_cn:
                        mLanguage = LanguageModel.Language.CHINESE;
                        break;
                    case R.id.language_en:
                        mLanguage = LanguageModel.Language.ENGLISH;
                        break;
                }
                if (mLanguage != null && LanguageModel.setSystemLanguage(mLanguage)) {
                    String languageIndex = LanguageModel.getLanguageIndex(mLanguage);
                    if (!TextUtils.isEmpty(languageIndex)) {
                        ConfigModel.setLanguage(mContext, languageIndex);
                    }
                }
            }
        });
    }
}
