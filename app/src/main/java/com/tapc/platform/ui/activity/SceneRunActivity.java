package com.tapc.platform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tapc.platform.R;
import com.tapc.platform.model.vaplayer.PlayEntity;
import com.tapc.platform.model.vaplayer.ValUtil;
import com.tapc.platform.ui.adpater.VaAdapter;
import com.tapc.platform.ui.base.BaseActivity;
import com.tapc.platform.ui.base.BaseRecyclerViewAdapter;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("NewApi")
public class SceneRunActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private VaAdapter mVaAdapter;
    private static List<String> VA_FILE_PATH;
    private ArrayList<PlayEntity> mPlayList;

    public static void launch(Context c) {
        Intent i = new Intent(c, SceneRunActivity.class);
        c.startActivity(i);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_scene_run;
    }

    @Override
    protected void initView() {
        super.initView();
        initPlayer();
    }

    private void initPlayer() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                if (VA_FILE_PATH == null) {
                    VA_FILE_PATH = new ArrayList<String>();
                    VA_FILE_PATH.add(Environment.getExternalStorageDirectory().getPath());
                    VA_FILE_PATH.add(System.getenv("SECONDARY_STORAGE"));
                }

                mPlayList = new ArrayList<PlayEntity>();
                for (String path : VA_FILE_PATH) {
                    ArrayList<PlayEntity> playList = ValUtil.getValList(path);
                    if (playList != null) {
                        mPlayList.addAll(playList);
                    }
                }
                e.onNext("show");
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).compose(this.bindUntilEvent
                (ActivityEvent.DESTROY)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (mPlayList != null) {
                    mVaAdapter = new VaAdapter(mPlayList);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,
                            false));

                    mRecyclerView.setAdapter(mVaAdapter);
                    mVaAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<PlayEntity>() {
                        @Override
                        public void onItemClick(View view, PlayEntity playEntity) {
                            ScenePlayActivity.launch(mContext, playEntity);
                        }
                    });
                    mVaAdapter.notifyDataSetChanged(mPlayList);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
