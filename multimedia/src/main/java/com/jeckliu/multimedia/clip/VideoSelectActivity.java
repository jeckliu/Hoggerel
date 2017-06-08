package com.jeckliu.multimedia.clip;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.util.DateUtil;
import com.jeckliu.framwork.view.CommonTitleBar;
import com.jeckliu.framwork.view.LoadingDialog;
import com.jeckliu.framwork.view.ToastShow;
import com.jeckliu.multimedia.R;
import com.jeckliu.multimedia.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/3/15 0015.
 */
public class VideoSelectActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_select);
        new CommonTitleBar(this, "视频选择");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        new LoadVideosTask().execute();
    }

    private class LoadVideosTask extends AsyncTask<Void, Void, Boolean> {
        List<VideoBean> videoBeanList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoadingDialog.getInstance().show(getSupportFragmentManager());
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isError;
            try {
                Cursor cursor = MediaStore.Video.query(getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null);
                initVideo(cursor, videoBeanList);
                isError = false;
            } catch (Exception e) {
                isError = true;
            }
            return isError;
        }

        @Override
        protected void onPostExecute(Boolean isError) {
            super.onPostExecute(isError);
            if (!isError) {
                LoadingDialog.getInstance().dismiss();
                adapter.setData(videoBeanList);
            }
        }
    }

    private void initVideo(Cursor cursor, List<VideoBean> videoBeanList) {
        int count = cursor.getCount();
        cursor.moveToFirst();
        for (int i = 0; i < count; i++) {
            VideoBean bean = new VideoBean();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
            long dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
            bean.path = path;
            bean.thumbnail = thumbnail;
            bean.duration = duration;
            bean.dateModified = dateModified;
            videoBeanList.add(bean);
            cursor.moveToNext();
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {
        private List<VideoBean> videoBeanList = new ArrayList<>();

        public void setData(List<VideoBean> videoBeanList) {
            this.videoBeanList = videoBeanList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(VideoSelectActivity.this, R.layout.item_video_select, null);
            return new RecyclerHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, final int position) {
            holder.imageView.setImageBitmap(videoBeanList.get(position).thumbnail);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(videoBeanList.get(position).duration < 10000){
                        ToastShow.showLongMessage("暂只支持10秒以上视频进行剪辑");
                        return;
                    }
                    Intent intent = new Intent(VideoSelectActivity.this, VideoClipActivity.class);
                    intent.putExtra(VideoClipActivity.SRC_PATH, videoBeanList.get(position).path);
                    startActivity(intent);
                }
            });
            holder.tvDate.setText(DateUtil.getYMD_HMS(videoBeanList.get(position).dateModified));
            holder.tvDuration.setText(DateUtil.getMS(videoBeanList.get(position).duration));
        }

        @Override
        public int getItemCount() {
            return videoBeanList.size();
        }
    }

    private class RecyclerHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tvDate;
        private TextView tvDuration;

        private RecyclerHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_video_select_iv);
            tvDate = (TextView) itemView.findViewById(R.id.item_video_select_date);
            tvDuration = (TextView) itemView.findViewById(R.id.item_video_select_duration);
        }
    }
}
