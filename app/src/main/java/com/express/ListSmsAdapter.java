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
import android.widget.Toast;

import java.util.List;

public class ListSmsAdapter extends ArrayAdapter<Sms> {

    private int resourceId;
    public Button btnFetch;
    private String mSmsID;

    ListSmsAdapter(@NonNull Context context, int resource, @NonNull List<Sms> objects) {
        super(context, resource, objects);
        resourceId = resource;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Sms sms = getItem(position);//获取sms对象
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView txtDate = view.findViewById(R.id.itemDate);
        TextView txtPosition = view.findViewById(R.id.itemPosition);
        TextView txtCode = view.findViewById(R.id.itemCode);
        TextView txtPhone = view.findViewById(R.id.itemPhone);
        TextView txtSmsID = view.findViewById(R.id.itemSmsID);

        assert sms != null;
        txtDate.setText(sms.getSmsDate());
        txtPosition.setText(sms.getPosition());
        txtCode.setText(sms.getCode());
        txtPhone.setText(sms.getPhone());
        txtSmsID.setText(sms.getSmsID());
        btnFetch = view.findViewById(R.id.btnFetch);
        btnFetch.setTag(position-1);

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFetch.getVisibility() == Button.VISIBLE) {
                    int tag = (int) btnFetch.getTag();
                    int tag2 = (Integer) v.getTag();
                    Sms sms = getItem(tag);
                    boolean returnValue  = DBManager.updateFetchStatus(sms.getSmsID());
                    if (returnValue) {
                        btnFetch.setVisibility(Button.INVISIBLE);
                    }
                }


            }
        });

        return view;
    }

    public String getSmsID() {
        return mSmsID;
    }

}
