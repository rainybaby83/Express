package com.express;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class FragmentFetch extends ListFragment {
    ListSmsAdapter mAdapter;
    Context mContext;

    public FragmentFetch() {
        List<Sms> mSmsList = new ArrayList<>();
        mContext=MainActivity.getInstance();
        mAdapter = new ListSmsAdapter(mContext,R.layout.list_item_fecth,mSmsList);
        this.setListAdapter(mAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fetch, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void refresh(List<Sms> mList) {
        mAdapter.clear();
        mAdapter.addAll(mList);
        this.setListAdapter(mAdapter);
    }

}
