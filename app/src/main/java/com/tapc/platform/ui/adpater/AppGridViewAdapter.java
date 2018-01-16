package com.tapc.platform.ui.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tapc.platform.R;
import com.tapc.platform.model.app.AppInfoEntity;
import com.tapc.platform.ui.base.BaseRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppGridViewAdapter extends BaseRecyclerViewAdapter<AppGridViewAdapter.ViewHolder, AppInfoEntity> {

    private List<AppInfoEntity> mListAppInfo = null;
    private Context mContext;

    public AppGridViewAdapter(Context context, List<AppInfoEntity> listAppInfo) {
        super(listAppInfo);
        this.mListAppInfo = listAppInfo;
        this.mContext = context;
    }

    @Override
    public int getContentView() {
        return R.layout.item_app_start;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        AppInfoEntity appInfo = mDatas.get(position);
        holder.app_icon.setImageDrawable(appInfo.getAppIcon());
        holder.app_name.setText(appInfo.getAppLabel());
        holder.itemView.setTag(appInfo);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setBackgroundResource(R.drawable.bg_item_click);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.app_icon)
        ImageView app_icon;// 图像
        @BindView(R.id.app_name)
        TextView app_name;// 名称

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
