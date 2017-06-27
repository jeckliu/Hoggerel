package com.jeckliu.hoggerel.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jeckliu.codescanner.CaptureZBarActivity;
import com.jeckliu.framwork.base.BaseFragment;
import com.jeckliu.hoggerel.R;
import com.jeckliu.multimedia.MultimediaActivity;
import com.jeckliu.pay.PayModeActivity;

/***
 * Created by Jeck.Liu on 2017/6/6 0006.
 */


public class HomeFragment extends BaseFragment implements View.OnClickListener{
    private ImageView ivScan;
    private TextView tvMultimedia;
    private TextView tvPay;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ivScan = (ImageView) view.findViewById(R.id.fragment_home_scan);
        tvMultimedia = (TextView) view.findViewById(R.id.fragment_home_multimedia);
        tvPay = (TextView) view.findViewById(R.id.fragment_home_pay);

        ivScan.setOnClickListener(this);
        tvMultimedia.setOnClickListener(this);
        tvPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_home_scan:
                startActivity(new Intent(getContext(), CaptureZBarActivity.class));
                break;
            case R.id.fragment_home_multimedia:
                startActivity(new Intent(getContext(), MultimediaActivity.class));
                break;
            case R.id.fragment_home_pay:
                startActivity(new Intent(getContext(), PayModeActivity.class));
                break;
        }
    }

}
