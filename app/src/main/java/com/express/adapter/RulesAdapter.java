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

import com.express.entity.RulesEntity;
import com.express.R;

import java.util.List;

public class RulesAdapter extends ArrayAdapter<RulesEntity> {

    private int resourceId;

    public RulesAdapter(@NonNull Context context, int resource, @NonNull List<RulesEntity> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        @SuppressLint("ViewHolder") final View itemView = LayoutInflater.from(getContext()).inflate(resourceId,null);
        itemView.setTag(position);

        TextView labelKeywords = itemView.findViewById(R.id.labelKeyword);
        TextView labelLeft = itemView.findViewById(R.id.labelLeft);
        TextView labelRight = itemView.findViewById(R.id.labelRight);
        TextView txtKeyword = itemView.findViewById(R.id.itemKeyword);
        TextView textLeft = itemView.findViewById(R.id.itemLeft);
        TextView txtRight = itemView.findViewById(R.id.itemRight);

        //获取规则对象
        RulesEntity rulesEntity = this.getItem(position);
        if (rulesEntity != null) {
            labelKeywords.setText("短信关键词：");
            labelLeft.setText("验证码左侧：");
            labelRight.setText("验证码右侧：");
            txtKeyword.setText(rulesEntity.getKeyword());
            textLeft.setText(rulesEntity.getCodeLeft());
            txtRight.setText(rulesEntity.getCodeRight());
        }

        return itemView;
    }

}
