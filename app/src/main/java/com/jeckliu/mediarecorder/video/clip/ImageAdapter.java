package com.jeckliu.mediarecorder.video.clip;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.jeckliu.mediarecorder.R;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/2/17 0017.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{
    private LayoutInflater inflater;
    private List<Bitmap> bitmaps;

    public ImageAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Bitmap> bitmaps){
        this.bitmaps = bitmaps;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_image,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(bitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }
}
