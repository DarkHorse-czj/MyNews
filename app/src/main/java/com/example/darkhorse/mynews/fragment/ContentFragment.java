package com.example.darkhorse.mynews.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.darkhorse.mynews.R;
import com.example.darkhorse.mynews.base.BaseFragment;

/**
 * Created by DarkHorse on 2016/10/28.
 */

public class ContentFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        this.mInflater = inflater;
        rootView = mInflater.inflate(R.layout.fragment_content, container, false);
        return rootView;
    }
}
