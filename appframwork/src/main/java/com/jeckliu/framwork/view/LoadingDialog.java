package com.jeckliu.framwork.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jeckliu.framwork.R;

/***
 * Created by Jeck.Liu on 2017/6/8 0008.
 */

public class LoadingDialog {

    private static LoadingDialog instance;
    private static LoadingDialogFragment dialogFragment;

    public static LoadingDialog getInstance(){
        if(instance == null){
            instance = new LoadingDialog();
        }
        return instance;
    }

    private LoadingDialog() {
        dialogFragment = new LoadingDialogFragment();
    }

    public void show(FragmentManager fragmentManager){
        if(dialogFragment.isVisible()){
            dialogFragment.dismiss();
        }
        dialogFragment.show(fragmentManager,"");
    }

    public void dismiss(){
        dialogFragment.dismiss();
    }

    public static class LoadingDialogFragment extends DialogFragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_loading,container,false);
        }
    }

}
