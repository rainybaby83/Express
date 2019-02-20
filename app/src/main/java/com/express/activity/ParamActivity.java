package com.express.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.express.Const;
import com.express.R;
import com.express.database.DBManager;
import com.express.database.NetDBManager;

import static com.express.database.DBManager.PARAM_NAME_APP_MODE;
import static com.express.database.DBManager.PARAM_NAME_DB_URL;
import static com.express.database.DBManager.PARAM_NAME_PASSWORD;
import static com.express.database.DBManager.PARAM_NAME_PORT;
import static com.express.database.DBManager.PARAM_NAME_USER;

public class ParamActivity extends Activity {

    private TextView txtIP,txtPort,txtUser,txtPassword;
    private Button btnTest, btnSave;
    private RadioButton rbtSingle, rbtNet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);
        initUI();
        initListener();
        initUIContent();

    }


    private void initUI() {
        txtIP = findViewById(R.id.text_ip);
        txtPort = findViewById(R.id.txt_port);
        txtUser = findViewById(R.id.txt_user);
        txtPassword = findViewById(R.id.txt_password);
        btnTest = findViewById(R.id.btn_test);
        btnSave = findViewById(R.id.btn_save);
        btnTest.setEnabled(false);
        rbtSingle = findViewById(R.id.radioButton_single);
        rbtNet = findViewById(R.id.radioButton_net);
    }


    private void initListener() {
        //单击模式
        rbtSingle.setOnClickListener(v -> {
            txtIP.setEnabled(false);
            txtPort.setEnabled(false);
            txtUser.setEnabled(false);
            txtPassword.setEnabled(false);
            btnTest.setEnabled(false);
        });

        //网络模式
        rbtNet.setOnClickListener(v -> {
            txtIP.setEnabled(true);
            txtPort.setEnabled(true);
            txtUser.setEnabled(true);
            txtPassword.setEnabled(true);
            btnTest.setEnabled(true);
        });


        btnTest.setOnClickListener(v ->{
            String mUrl, mPort, mUser, mPassword;
            mUrl = txtIP.getText().toString();
            mPort = txtPort.getText().toString();
            mUser = txtUser.getText().toString();
            mPassword = txtPassword.getText().toString();

            String longUrl = "jdbc:mysql://" + mUrl + ":" + mPort + "/";
            boolean a = NetDBManager.getConnectStatusForParamTest(longUrl, mUser, mPassword);
            if (a) {
                NetDBManager.setParam(longUrl, mUser, mPassword);
                Toast.makeText(this, "连接成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> {
            if (rbtSingle.isChecked()) {
                MainActivity.getInstance().mAppMode = Const.APP_MODE_SINGLE;
                DBManager.updateParam(PARAM_NAME_APP_MODE, Const.APP_MODE_SINGLE);
                Toast.makeText(this, "保存成功!", Toast.LENGTH_LONG).show();
                finish();
            } else if (rbtNet.isChecked()) {
                MainActivity.getInstance().mAppMode = Const.APP_MODE_NET;
                DBManager.updateParam(PARAM_NAME_APP_MODE, Const.APP_MODE_NET);
                DBManager.updateParam(PARAM_NAME_DB_URL, txtIP.getText().toString());
                DBManager.updateParam(PARAM_NAME_PORT, txtPort.getText().toString());
                DBManager.updateParam(PARAM_NAME_USER, txtUser.getText().toString());
                DBManager.updateParam(PARAM_NAME_PASSWORD, txtPassword.getText().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("保存成功，请重新运行!")
                        .setPositiveButton("确定", (dialog, which) -> System.exit(1))
                        .show();
            }

        });
    }


    private void initUIContent() {
        txtIP.setText(DBManager.getParam(PARAM_NAME_DB_URL));
        txtPort.setText(DBManager.getParam(PARAM_NAME_PORT));
        txtUser.setText(DBManager.getParam(PARAM_NAME_USER));
        txtPassword.setText(DBManager.getParam(PARAM_NAME_PASSWORD));
        if (MainActivity.getInstance().mAppMode.equals(Const.APP_MODE_SINGLE)) {
            rbtSingle.setChecked(true);
            rbtSingle.callOnClick();
        } else if (MainActivity.getInstance().mAppMode.equals(Const.APP_MODE_NET)) {
            rbtNet.setChecked(true);
            rbtNet.callOnClick();
        }
    }
}
