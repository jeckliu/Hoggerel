package com.jeckliu.hoggerel.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeckliu.framwork.base.BaseFragment;
import com.jeckliu.hoggerel.R;

/***
 * Created by Jeck.Liu on 2017/6/6 0006.
 */

public class MineFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine,container,false);
    }
}
