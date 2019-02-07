package com.express.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.express.R;
import com.express.entity.SmsEntity;
import com.express.adapter.SmsFetchAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentFetch extends ListFragment {
    SmsFetchAdapter mAdapter;
    Context mContext;

    public FragmentFetch() {
        List<SmsEntity> mSmsList = new ArrayList<>();
        mContext=MainActivity.getInstance();
        mAdapter = new SmsFetchAdapter(mContext, R.layout.list_item_fecth,mSmsList);
        this.setListAdapter(mAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_fetch, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void refresh(List<SmsEntity> mList) {
        mAdapter.clear();
        mAdapter.addAll(mList);
        this.setListAdapter(mAdapter);
    }

}
