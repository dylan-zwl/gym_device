package com.tapc.platform.ui.activity;


import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.widget.RadioGroup;

import com.tapc.platform.R;
import com.tapc.platform.entity.EventEntity;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.language.LanguageModel;
import com.tapc.platform.ui.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

import static com.tapc.platform.model.language.LanguageModel.Language;

public class LanguageAcivity extends BaseActivity {
    @BindView(R.id.language_select)
    RadioGroup mRadioGroup;

    private Language mLanguage;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_language;
    }


    @Override
    protected void initView() {
        super.initView();
        initCurrentLanguage();
    }

    private void initRid() {
        Language.CHINESE.setRid(R.id.language_cn);
        Language.ENGLISH.setRid(R.id.language_en);
    }

    private void initCurrentLanguage() {
        initRid();
        String languageIndex = ConfigModel.getLanguage(mContext, Language.CHINESE.languageName);
        mLanguage = LanguageModel.getLanguage(languageIndex);
        int rid = mLanguage.getRid();
        if (rid != 0) {
            mRadioGroup.check(rid);
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (Language language : Language.values()) {
                    if (checkedId == language.getRid()) {
                        mLanguage = language;
                        break;
                    }
                }
                if (mLanguage != null && LanguageModel.setSystemLanguage(mLanguage)) {
                    String languageIndex = LanguageModel.getLanguageIndex(mLanguage);
                    if (!TextUtils.isEmpty(languageIndex)) {
                        ConfigModel.setLanguage(mContext, languageIndex);
                        EventBus.getDefault().post(new EventEntity.ReloadApp());
                    }
                }
            }
        });
    }
}
