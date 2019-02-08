package com.express.adapter;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.express.activity.MainActivity;
import com.express.activity.RulesActivity;
import com.express.database.DBManager;
import com.express.entity.RulesEntity;
import com.express.R;
import com.express.entity.SmsEntity;

import java.util.List;

public class RulesAdapter extends ArrayAdapter<RulesEntity> {


    private int resourceId;
    private AlertDialog.Builder alertDialog;


    public RulesAdapter(@NonNull Context context, int resource, @NonNull List<RulesEntity> objects) {
        super(context, resource, objects);
        resourceId = resource;
        alertDialog = new AlertDialog.Builder(getContext());
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") final View itemView = LayoutInflater.from(getContext()).inflate(resourceId,null);
        itemView.setTag(position);
        Button btnDelRule = itemView.findViewById(R.id.btnDelRule);
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

        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RulesEntity selectedRulesEntity = getItem((Integer) itemView.getTag());
                if (selectedRulesEntity != null) {
                    DBManager.deleteRule(selectedRulesEntity);
                    remove(selectedRulesEntity);
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setMessage("将要删除1条规则，是否确认？");


        btnDelRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.create().show();
            }
        });

        return itemView;
    }
}
