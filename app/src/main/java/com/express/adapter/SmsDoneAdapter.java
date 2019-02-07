package com.express.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.express.entity.SmsEntity;
import com.express.R;

import java.util.List;

public class SmsDoneAdapter extends ArrayAdapter<SmsEntity> {

    private int resourceId;

    public SmsDoneAdapter(@NonNull Context context, int resource, @NonNull List<SmsEntity> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        @SuppressLint("ViewHolder") final View itemView = LayoutInflater.from(getContext()).inflate(resourceId,null);
        itemView.setTag(position);

        TextView txtDate = itemView.findViewById(R.id.itemDate);
        TextView txtPosition = itemView.findViewById(R.id.itemPosition);
        TextView txtCode = itemView.findViewById(R.id.itemCode);
        TextView txtPhone = itemView.findViewById(R.id.itemPhone);
        TextView txtSmsID = itemView.findViewById(R.id.itemSmsID);
        TextView txtSmsFetchDateLabel = itemView.findViewById(R.id.labelFetchDate);
        TextView txtSmsFetchDate = itemView.findViewById(R.id.itemFetchDate);

        SmsEntity smsEntity = this.getItem(position);//获取短信对象
        if (smsEntity != null) {
            txtDate.setText(smsEntity.getSmsDate());
            txtPosition.setText(smsEntity.getPosition());
            txtCode.setText(smsEntity.getCode());
            txtPhone.setText(smsEntity.getPhone());
            txtSmsID.setText(smsEntity.getSmsID());
            txtSmsFetchDateLabel.setText("取件时间: ");
            txtSmsFetchDate.setText(smsEntity.getFetchDate());
        }

        return itemView;
    }

}
