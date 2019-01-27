package com.express;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Created by caobotao on 16/1/4.
 */
public class TodoFragment extends ListFragment {
    private ListView mList;
    private SimpleAdapter adapter;
    private TextView mText;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.todo, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mText = getActivity().findViewById(R.id.txt1);
        mText.setText(getSmsInPhone());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        String[] listItem = { "a", "b", "c", "d", "e" };
//        int[] iconItem = { R.drawable.ic_launcher, R.drawable.ic_launcher,
//                R.drawable.ic_launcher, R.drawable.ic_launcher,
//                R.drawable.ic_launcher };
//        adapter = new SimpleAdapter(ge tActivity(), getData(listItem, iconItem),
//                R.layout.me_function_item, new String[] { "name", "icon" },
//                new int[] { R.id.functionName, R.id.functionIcon });
//        setListAdapter(adapter);

    }

    private List<? extends Map<String, ?>> getData(String[] listItem, int[] iconItem) {
        return null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


    public String getSmsInPhone() {

        StringBuilder smsBuilder = new StringBuilder();
        try {
            Uri uri = Uri.parse("content://sms/inbox");
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type",};
            Cursor cur = getContext().getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信

            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");


                do {
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);

                    if (strbody.contains("快递超市") || strbody.contains("验证码")) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date d = new Date(longDate);
                        String strDate = dateFormat.format(d);

                        smsBuilder.append("[ ");
                        smsBuilder.append(strbody + ", ");
                        smsBuilder.append(strDate + ", ");
                        smsBuilder.append(" ]\n\n");
                    }

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            }


        } catch (SQLiteException ex) {
            Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
        } catch (NullPointerException ignored) {
        }

        return smsBuilder.toString();
    }
}
