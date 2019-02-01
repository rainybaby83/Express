package com.express;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FragmentDone extends ListFragment {
    SmsDoneAdapter mAdapter;
    Context mContext;

    public FragmentDone() {
        List<SmsData> mSmsList = new ArrayList<>();
        mContext=MainActivity.getInstance();
        mAdapter = new SmsDoneAdapter(mContext,R.layout.list_item_done,mSmsList);
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

    public void refresh(List<SmsData> mList) {
        mAdapter.clear();
        mAdapter.addAll(mList);
        this.setListAdapter(mAdapter);
    }

}
