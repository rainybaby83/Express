package com.express;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ListSmsAdapter extends ArrayAdapter<Sms> {

    private int resourceId;
    ListSmsAdapter(@NonNull Context context, int resource, @NonNull List<Sms> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Sms sms = getItem(position);//获取sms对象
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView txtCode = view.findViewById(R.id.list_text_code);
        Button btnFetch = view.findViewById(R.id.btnFetch);

        assert sms != null;
        if (sms.getFetchStatus() == "已取") {
            btnFetch.setVisibility(Button.INVISIBLE);
        }
        txtCode.setText(sms.getCode());
        return view;
    }
}