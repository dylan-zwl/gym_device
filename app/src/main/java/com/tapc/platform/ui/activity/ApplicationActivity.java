package com.tapc.platform.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tapc.platform.R;
import com.tapc.platform.model.app.AppInfoEntity;
import com.tapc.platform.model.app.AppModel;
import com.tapc.platform.ui.adpater.AppGridViewAdapter;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.ui.base.BaseRecyclerViewAdapter;
import com.tapc.platform.utils.AppUtils;
import com.tapc.platform.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 应用
 *
 * @author sean.guo
 * @date 2015.3.24
 */
public class ApplicationActivity extends BaseActivity {
    @BindView(R.id.show_app_grid)
    RecyclerView show_app_grid;

    public final static int NO_TYPE = 0;
    public final static int START_APP = 255;
    private AppGridViewAdapter mAdapter;
    private List<AppInfoEntity> mlistAppInfo;
    private int mAppType;

    public static void launch(Context c, int appType) {
        Intent i = new Intent(c, ApplicationActivity.class);
        i.putExtra("appType", appType);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        c.startActivity(i);
    }

    public static void launch(Context c, int appType, String packageName, String className) {
        Intent i = new Intent(c, ApplicationActivity.class);
        i.putExtra("appType", appType);
        i.putExtra("packageName", packageName);
        i.putExtra("className", className);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        c.startActivity(i);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_app;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mlistAppInfo = AppModel.getAllAppInfo(this, false);
        //        if (mlistAppInfo == null) {
        //            mlistAppInfo = new ArrayList<AppInfoEntity>();
        //            mlistAppInfo = AppUtils.getAllAppInfo(this);
        //        }
        mAppType = getIntent().getIntExtra("appType", 0);
        switch (mAppType) {
            case NO_TYPE:
                setContentView(R.layout.activity_app);
                init();
                break;
            case START_APP:
                String packageName = getIntent().getStringExtra("packageName");
                String className = getIntent().getStringExtra("className");
                if (packageName != null && className != null) {
                    for (AppInfoEntity app : mlistAppInfo) {
                        if (app.getPkgName().equals(packageName)) {
                            Intent intent = app.getIntent();
                            intent.setAction("android.intent.action.VIEW");
                            startActivity(intent);
                            Log.d("start packagename", packageName);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void init() {
        getThirdApplication();
    }

    private void getThirdApplication() {
        mAdapter = new AppGridViewAdapter(this, mlistAppInfo);
        show_app_grid.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<AppInfoEntity>() {
            @Override
            public void onItemClick(View view, AppInfoEntity appInfoEntity) {
                Intent intent = appInfoEntity.getIntent();
                intent.setAction("android.intent.action.VIEW");
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.app_exit)
    protected void exitAppOnClick(View v) {
        new Runnable() {
            @Override
            public void run() {
                AppUtils.clearAppExit(ApplicationActivity.this, mlistAppInfo);
                Toast.makeText(ApplicationActivity.this, R.string.app_clear_completed, Toast.LENGTH_LONG).show();
            }
        }.run();
    }

    @OnClick(R.id.clear_app_data)
    protected void clearAppOnClick(View v) {
        new Runnable() {
            @Override
            public void run() {
                AppUtils.clearAppExit(mContext, mlistAppInfo);
                for (final AppInfoEntity app : mlistAppInfo) {
                    AppUtils.clearAppUserData(mContext, app.getPkgName(), new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String s, boolean b) throws RemoteException {
                            Log.d("clear " + app.getPkgName() + " data", "" + b);
                        }
                    });
                }
                FileUtils.RecursionDeleteFile(new File(Environment.getExternalStorageDirectory().getPath()), new
                        FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().equals("va");
                    }
                });
                Toast.makeText(mContext, R.string.app_clear_completed, Toast.LENGTH_LONG).show();
            }
        }.run();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
