package com.express;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.express.activity.RulesActivity;
import com.express.database.DBManager;

import org.apache.commons.lang3.StringUtils;

public class CreateRulesDialog extends Dialog {

    /**
     * 上下文对象 *
     */
    private RulesActivity mContext;
    private EditText txtKeyword;
    private EditText txtCodeLeft;
    private EditText txtCodeRight;

    public CreateRulesDialog(RulesActivity context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.create_rules);
        txtKeyword = findViewById(R.id.txtKeyword);
        txtCodeLeft = findViewById(R.id.txtCodeLeft);
        txtCodeRight = findViewById(R.id.txtCodeRight);

       //获取窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window对象,这样这可以以同样的方式改变这个Activity的属性.
        Window dialogWindow = this.getWindow();
        WindowManager windowManager = mContext.getWindowManager();
        // 获取屏幕宽、高用
        Display display = windowManager.getDefaultDisplay();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        Point size = new Point();
        display.getSize(size);
        // 高度设置为屏幕的0.6，宽度设置为屏幕的0.8
        p.height = (int) (size.y * 0.55);
        p.width = (int) (size.x * 0.8);
        dialogWindow.setAttributes(p);

        // 根据id在布局中找到控件对象
        Button btnSave = findViewById(R.id.btn_save);

        // 为按钮绑定点击事件监听器
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strKeyword,strCodeLeft,strCodeRight;
                strKeyword = StringUtils.trim(txtKeyword.getText().toString());
                strCodeLeft = StringUtils.trim(txtCodeLeft.getText().toString());
                strCodeRight = StringUtils.trim(txtCodeRight.getText().toString());
                if ("".equals(strKeyword) || "".equals(strCodeLeft) || "".equals(strCodeRight)) {
                    Toast.makeText(mContext, "输入文本不能为空！",Toast.LENGTH_LONG).show();
                } else {
                    boolean tmp = DBManager.insertRule(strKeyword, strCodeLeft, strCodeRight);
                    if (tmp) {
                        Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                        //关闭Dialog
                        CreateRulesDialog.super.dismiss();
                        //刷新页面列表
                        mContext.refresh();

                    } else {
                        Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        this.setCancelable(true);
    }
}
