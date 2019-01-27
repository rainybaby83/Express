package com.express;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.express.R;


public class MainActivity extends FragmentActivity implements OnClickListener {
    //声明四个Tab的布局文件
    private LinearLayout mTabTodo;
    private LinearLayout mTabDone;

    //声明四个Tab的ImageButton
    private ImageButton btnTodo;
    private ImageButton btnDone;


    //声明四个Tab分别对应的Fragment
    private Fragment frgTodo;
    private Fragment frgDone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initViews();//初始化控件
        initEvents();//初始化事件
        selectTab(0);//默认选中第一个Tab
    }

    private void initEvents() {
        //初始化四个Tab的点击事件
        mTabTodo.setOnClickListener(this);
        mTabDone.setOnClickListener(this);
    }

    private void initViews() {
        //初始化四个Tab的布局文件
        mTabTodo = (LinearLayout) findViewById(R.id.tab_todo);
        mTabDone = (LinearLayout) findViewById(R.id.id_tab_done);


        //初始化四个ImageButton
        btnTodo = (ImageButton) findViewById(R.id.btn_todo);
        btnDone = (ImageButton) findViewById(R.id.btn_done);

    }

    //处理Tab的点击事件
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tab_todo:
                selectTab(0);
                break;
            case R.id.id_tab_done:
                selectTab(1);
                break;
        }

    }

    //进行选中Tab的处理
    private void selectTab(int i) {
        //获取FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            //当选中点击的是微信的Tab时
            case 0:
                //设置微信的ImageButton为绿色
//                btnTodo.setImageResource(R.mipmap.tab_weixin_pressed);
                //如果微信对应的Fragment没有实例化，则进行实例化，并显示出来
                if (frgTodo == null) {
                    frgTodo = new TodoFragment();
                    transaction.add(R.id.id_content, frgTodo);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(frgTodo);
                }
                break;
            case 1:
//                btnDone.setImageResource(R.mipmap.tab_find_frd_pressed);
                if (frgDone == null) {
                    frgDone = new DoneFragment();
                    transaction.add(R.id.id_content, frgDone);
                } else {
                    transaction.show(frgDone);
                }
                break;
        }
        //不要忘记提交事务
        transaction.commit();
    }

    //将四个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (frgTodo != null) {
            transaction.hide(frgTodo);
        }
        if (frgDone != null) {
            transaction.hide(frgDone);
        }

    }


}
