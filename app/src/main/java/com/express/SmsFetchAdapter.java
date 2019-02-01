package com.express;


import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class SmsFetchAdapter extends ArrayAdapter<SmsData> {

    private int resourceId;
    public Button btnFetch;

    SmsFetchAdapter(@NonNull Context context, int resource, @NonNull List<SmsData> objects) {
        super(context, resource, objects);
        resourceId = resource;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        @SuppressLint("ViewHolder") final View itemView = LayoutInflater.from(getContext()).inflate(resourceId,null);
        btnFetch = itemView.findViewById(R.id.btnFetch);
        itemView.setTag(position);

        TextView txtDate = itemView.findViewById(R.id.itemDate);
        TextView txtPosition = itemView.findViewById(R.id.itemPosition);
        TextView txtCode = itemView.findViewById(R.id.itemCode);
        TextView txtPhone = itemView.findViewById(R.id.itemPhone);
        TextView txtSmsID = itemView.findViewById(R.id.itemSmsID);

        SmsData smsData = this.getItem(position);//获取短信对象
        if (smsData != null) {
            txtDate.setText(smsData.getSmsDate());
            txtPosition.setText(smsData.getPosition());
            txtCode.setText(smsData.getCode());
            txtPhone.setText(smsData.getPhone());
            txtSmsID.setText(smsData.getSmsID());
        }

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vButton) {
                if (vButton.getVisibility() == Button.VISIBLE) {
                    SmsData temp = getItem((Integer) itemView.getTag());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd HH:mm");
                    String strDate = dateFormat.format(new Date());

                    if (temp != null) {
                        boolean returnValue = DBManager.updateFetch(temp.getSmsID(), strDate);
                        if (returnValue) {
                            vButton.setVisibility(Button.INVISIBLE);
                            //划掉验证码
                            TextView txtCode = itemView.findViewById(R.id.itemCode);
                            SpannableString ss = new SpannableString(txtCode.getText());
                            ss.setSpan(new StrikethroughSpan(), 0, txtCode.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            txtCode.setText(ss);
                        }
                    }
                }
            }
        });

        return itemView;
    }

}
