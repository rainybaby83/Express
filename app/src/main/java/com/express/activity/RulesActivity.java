package com.express.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.express.R;
import com.express.adapter.RulesAdapter;
import com.express.entity.RulesEntity;

import java.util.ArrayList;
import java.util.List;

public class RulesActivity extends ListActivity {

    private RulesAdapter mAdapter;

    private Button btnNew;
    private Toolbar mToolbar;
//    public RulesActivity() {
//
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        mToolbar = findViewById(R.id.toolbarRules);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNew = findViewById(R.id.btnNew);

        List<RulesEntity> mRulesList = new ArrayList<>();
        mAdapter = new RulesAdapter(this,R.layout.list_item_rules,mRulesList);
        this.setListAdapter(mAdapter);

    }


    public void refresh(List<RulesEntity> mList) {
        mAdapter.clear();
        mAdapter.addAll(mList);
        this.setListAdapter(mAdapter);
    }
}
