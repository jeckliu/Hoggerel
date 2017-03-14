package com.jeckliu.mediarecorder.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jeckliu.mediarecorder.R;

import java.util.ArrayList;
import java.util.List;

/***
 * 图片双向滑动选择器（组合控件辅助类）
 * Created by Jeck.Liu on 2017/3/13 0013.
 */
public class PictureSeekPickerHelper {
    private static PictureSeekPickerHelper instance;
    private OnSeekProgress onSeekProgress;
    private Activity activity;
    private RelativeLayout rootParent;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private ImageView ivLeft;
    private ImageView ivRight;
    private View floatLeft;
    private View floatRight;

    private float leftThumbLastX;
    private float rightThumbLastX;
    private float leftThumbEndX;
    private float rightThumbEndX;
    private int leftProgress;
    private int rightProgress;
    private int visibleCount;
    private float leftThumbInitialPosition;
    private float rightThumbInitialPosition;

    public static PictureSeekPickerHelper getInstance(Activity mActivity) {
        if (instance == null) {
            instance = new PictureSeekPickerHelper(mActivity);
        }
        return instance;
    }

    private PictureSeekPickerHelper(Activity mActivity) {
        activity = mActivity;
        initView();
    }

    private void initView() {
        rootParent = (RelativeLayout) activity.findViewById(R.id.item_picture_seek_picker_helper);
        recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view);
        ivLeft = (ImageView) activity.findViewById(R.id.iv_left);
        ivRight = (ImageView) activity.findViewById(R.id.iv_right);
        floatLeft = activity.findViewById(R.id.float_left);
        floatRight = activity.findViewById(R.id.float_right);

        adapter = new ImageAdapter(activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleCount = recyclerView.getChildCount();
                calculateProgress();
            }
        });

        ivLeft.post(new Runnable() {
            @Override
            public void run() {
                int[] locations = new int[2];
                ivLeft.getLocationOnScreen(locations);
                leftThumbInitialPosition = leftThumbEndX = locations[0];
                calculateProgress();
            }
        });

        ivRight.post(new Runnable() {
            @Override
            public void run() {
                int[] locations = new int[2];
                ivRight.getLocationOnScreen(locations);
                rightThumbInitialPosition = rightThumbEndX = locations[0];
                calculateProgress();
            }
        });

        ivLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        leftThumbLastX = event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        leftThumbEndX = event.getRawX();
                        int dx = (int) (leftThumbEndX - leftThumbLastX);
                        int left = v.getLeft() + dx;
                        int right = v.getRight() + dx;
                        int top = v.getTop();
                        int bottom = v.getBottom();
                        if(left < leftThumbInitialPosition){
                            left = (int) leftThumbInitialPosition;
                            right = left + v.getWidth();
                        }
                        if(right > ivRight.getLeft()){
                            right = ivRight.getLeft();
                            left = right - v.getWidth();
                        }
                        v.layout(left, top, right, bottom);
                        v.postInvalidate();
                        floatLeft.layout(floatLeft.getLeft(),floatLeft.getTop(),right,floatLeft.getBottom());
                        floatLeft.postInvalidate();
                        leftThumbLastX = event.getRawX();
                        leftThumbEndX = left;
                        calculateProgress();
                        break;
                }
                return true;
            }
        });

        ivRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rightThumbLastX = event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        rightThumbEndX = event.getRawX();
                        int dx = (int) (rightThumbEndX - rightThumbLastX);
                        int left = v.getLeft() + dx;
                        int right = v.getRight() + dx;
                        int top = v.getTop();
                        int bottom = v.getBottom();
                        if(left < ivLeft.getRight()){
                            left = ivLeft.getRight();
                            right = left + v.getWidth();
                        }
                        if(right > rightThumbInitialPosition){
                            right = (int) rightThumbInitialPosition;
                            left = right - v.getWidth();
                        }
                        v.layout(left, top, right, bottom);
                        v.postInvalidate();
                        floatRight.layout(left,floatRight.getTop(),floatRight.getRight(),floatRight.getBottom());
                        floatRight.postInvalidate();
                        rightThumbLastX = event.getRawX();
                        rightThumbEndX = left;
                        calculateProgress();
                        break;
                }

                return true;
            }
        });

    }

    private void calculateProgress(){
        for(int i = 0; i < visibleCount; i++){
            if(leftThumbEndX >= recyclerView.getChildAt(i).getLeft() && leftThumbEndX <= recyclerView.getChildAt(i).getRight()){
                leftProgress = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i))+1;
            }
            if(rightThumbEndX >= recyclerView.getChildAt(i).getLeft() && rightThumbEndX <= recyclerView.getChildAt(i).getRight()){
                rightProgress = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i))+1;
            }
            onSeekProgress.setOnSeekProgress(leftProgress,rightProgress);
        }
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        rootParent.setVisibility(View.VISIBLE);
        adapter.setData(bitmaps);
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private Context context;
        private List<Bitmap> bitmaps = new ArrayList<>();

        public ImageAdapter(Context context) {
            this.context = context;
        }

        public void setData(List<Bitmap> bitmaps) {
            this.bitmaps = bitmaps;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image,null);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imageView.setImageBitmap(bitmaps.get(position));
        }

        @Override
        public int getItemCount() {
            return bitmaps.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public void setOnSeekProgress(OnSeekProgress onSeekProgress){
        this.onSeekProgress = onSeekProgress;
    }

    public interface OnSeekProgress{
        void setOnSeekProgress(int leftProgress, int rightProgress);
    }

}
