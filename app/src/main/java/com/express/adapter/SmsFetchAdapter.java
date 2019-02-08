package com.express.adapter;


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

import com.express.database.DBManager;
import com.express.entity.SmsEntity;
import com.express.R;

import java.util.Date;
import java.util.List;

public class SmsFetchAdapter extends ArrayAdapter<SmsEntity> {
    private int resourceId;

    public SmsFetchAdapter(@NonNull Context context, int resource, @NonNull List<SmsEntity> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        @SuppressLint("ViewHolder") final View itemView = LayoutInflater.from(getContext()).inflate(resourceId,null);
        Button btnFetch = itemView.findViewById(R.id.btnFetch);
        itemView.setTag(position);

        TextView txtDate = itemView.findViewById(R.id.itemDate);
        TextView txtPosition = itemView.findViewById(R.id.itemPosition);
        TextView txtCode = itemView.findViewById(R.id.itemCode);
        TextView txtPhone = itemView.findViewById(R.id.itemPhone);
        TextView txtSmsID = itemView.findViewById(R.id.itemSmsID);

        SmsEntity smsEntity = this.getItem(position);//获取短信对象
        if (smsEntity != null) {
            txtDate.setText(smsEntity.getSmsDate());
            txtPosition.setText(smsEntity.getPosition());
            txtCode.setText(smsEntity.getCode());
            txtPhone.setText(smsEntity.getPhone());
            txtSmsID.setText(smsEntity.getSmsID());
        }

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vButton) {
                if (vButton.getVisibility() == Button.VISIBLE) {
                    SmsEntity selectedSmsEntity = getItem((Integer) itemView.getTag());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd HH:mm");
                    String strDate = dateFormat.format(new Date());

                    if (selectedSmsEntity != null) {
                        boolean returnValue = DBManager.updateFetch(selectedSmsEntity.getSmsID(), strDate);
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
