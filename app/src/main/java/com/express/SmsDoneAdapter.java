package com.express;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SmsDoneAdapter extends ArrayAdapter<SmsData> {

    private int resourceId;

    SmsDoneAdapter(@NonNull Context context, int resource, @NonNull List<SmsData> objects) {
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

        SmsData smsData = this.getItem(position);//获取短信对象
        if (smsData != null) {
            txtDate.setText(smsData.getSmsDate());
            txtPosition.setText(smsData.getPosition());
            txtCode.setText(smsData.getCode());
            txtPhone.setText(smsData.getPhone());
            txtSmsID.setText(smsData.getSmsID());
            txtSmsFetchDateLabel.setText("已取件: ");
            txtSmsFetchDate.setText(smsData.getFetchDate());
        }

        return itemView;
    }

}
