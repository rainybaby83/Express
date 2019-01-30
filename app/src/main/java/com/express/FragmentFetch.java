package com.express;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Text;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FragmentFetch extends ListFragment {
    List<Sms> mSmsList;
    ListSmsAdapter mAdapter;
    Context mContext;

    public FragmentFetch() {
        mSmsList = new ArrayList<>();
        mContext=MainActivity.getInstance();
        mAdapter = new ListSmsAdapter(mContext,R.layout.list_item,mSmsList);
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
        this.mSmsList = mList;
        mAdapter = new ListSmsAdapter(mContext,R.layout.list_item,mSmsList);
        this.setListAdapter(mAdapter);
    }
}
