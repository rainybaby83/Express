package com.express;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class FragmentFetch extends ListFragment {
    List<Sms> mSmsList;
    private TextView mText;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fetch, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSmsList = new ArrayList<>();

        ListSmsAdapter adapter = new ListSmsAdapter(getContext(),R.layout.list_item,mSmsList);
        this.setListAdapter(adapter);

    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


    /**
     * 从数据库刷
     * @return
     */
    public String getSmsFromPhone() {

        StringBuilder smsBuilder = new StringBuilder();
        try {
            Uri uri = Uri.parse("content://sms/inbox");
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type",};
            Cursor cur = getContext().getContentResolver().query(uri, projection, null, null, "date desc"); // 获取手机内部短信

            if (cur.moveToFirst()) {
//                int index_Address = cur.getColumnIndex("address");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                Sms tmpSms;
                String smsId;
                String strCode;


                do {
                    String strBody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);
//                    StringBuilder tmpStr = new StringBuilder();
                    tmpSms = null;
                    smsId = String.valueOf(d);
                    strCode = null;


                    if (strBody.contains("馒头房")) {

                        strCode = StringUtils.substringBetween(strBody, "提货码", "来取");
                        tmpSms = new Sms(0,smsId,strDate,strCode,"7350","馒头房",null,0);
                        this.mSmsList.add(tmpSms);
//                        tmpStr.append(strDate).append("：馒头房：").append(expressCode);
//                        smsBuilder.append(tmpStr).append(" \n\n");

                    } else if (strBody.contains("丰巢")) {
                        strCode = StringUtils.substringBetween(strBody, "请凭取件码『", "』前往明珠西苑");
                        tmpSms = new Sms(0,smsId,strDate,strCode,"7350","丰巢",null,0);
                        this.mSmsList.add(tmpSms);
//                        tmpStr.append(strDate).append("：丰巢：").append(expressCode);
//                        smsBuilder.append(tmpStr).append(" \n\n");

                    } else if (strBody.contains("日日顺")) {
                        strCode = StringUtils.substringBetween(strBody, "凭取件码", "到明珠西苑");
                        tmpSms = new Sms(0,smsId,strDate,strCode,"7350","日日顺",null,0);
                        this.mSmsList.add(tmpSms);
//                        tmpStr.append(strDate).append("：日日顺：").append(expressCode);
//                        smsBuilder.append(tmpStr).append(" \n\n");
                    }

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                }
            } else {
                smsBuilder.append("no result!");
            }


        } catch (SQLiteException ex) {
            Log.d("SQLiteException in getSmsFromPhone", ex.getMessage());
        } catch (NullPointerException ignored) {
        }

        return smsBuilder.toString();
    }

}
