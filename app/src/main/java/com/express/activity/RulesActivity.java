package com.express.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.express.R;
import com.express.adapter.RulesAdapter;
import com.express.database.DBManager;
import com.express.entity.RulesEntity;

import java.util.ArrayList;
import java.util.List;

public class RulesActivity extends ListActivity {

    private RulesAdapter mAdapter;
    private static RulesActivity mInstance;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        mText = findViewById(R.id.txt_example);
        mInstance = this;
        this.initToobar();
        this.initListView();
    }


    /**
     * 初始化菜单栏
     */
    private void initToobar() {
        Toolbar mToolbar = findViewById(R.id.toolbarRules);
        mToolbar.inflateMenu(R.menu.menu_rules);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.menu_new_rules) {
                    RulesActivity.this.refresh();
                    CreateRulesDialog createUserDialog = new CreateRulesDialog(RulesActivity.this);
                    createUserDialog.show();
                }
                return true;
            }
        });
    }

    private void initListView() {
        List<RulesEntity> mRulesList = new ArrayList<>();
        mAdapter = new RulesAdapter(this,R.layout.list_item_rules,mRulesList);
        this.refresh();
    }

    public void refresh() {
        List<RulesEntity> mList = DBManager.getRules();
        mAdapter.clear();
        mAdapter.addAll(mList);
        this.setListAdapter(mAdapter);
    }


//    获取Activity实例
    public static RulesActivity getInstance() {
        return mInstance;
    }

}
