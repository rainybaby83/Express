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
    List<Sms> mSmsList;
    ListSmsAdapter mAdapter;
    Context mContext;

    public FragmentFetch() {
        mSmsList = new ArrayList<>();
        mContext=MainActivity.getInstance();
        // mSmsList 旧方法
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


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


    public void refresh(List<Sms> mList) {

        //旧方法
        this.mSmsList = mList;
        mAdapter = new ListSmsAdapter(mContext,R.layout.list_item_fecth,mSmsList);
        this.setListAdapter(mAdapter);

        //新方法
//        mAdapter.clear();
//        mAdapter.addAll(mList);
//        this.setListAdapter(mAdapter);
    }

}
