package com.tapc.platform.ui.activity;

import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.model.common.ConfigModel;
import com.tapc.platform.model.language.LanguageModel;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.utils.FormatUtils;

import java.io.InputStream;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/1/5.
 */

public class HelpActivity extends BaseActivity {
    @BindView(R.id.help_txt)
    TextView helpText;

    private LanguageModel.Language mLanguage;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_help;
    }

    @Override
    protected void initView() {
        super.initView();
        String languageIndex = ConfigModel.getLanguage(mContext, LanguageModel.Language.CHINESE.languageName);
        mLanguage = LanguageModel.getLanguage(languageIndex);
        InputStream inputStream = null;
        switch (mLanguage) {
            case CHINESE:
                inputStream = getResources().openRawResource(R.raw.help);
                break;
            case ENGLISH:
                inputStream = getResources().openRawResource(R.raw.help_en);
                break;
        }
        if (inputStream != null) {
            String txt = FormatUtils.getInputStream2String(inputStream);
            helpText.setText(txt);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
