package com.arahasya.sap.forwardkey;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CustomAdapter extends ArrayAdapter<RecordModel> {

    private Context mContext;
    private List<RecordModel> recordList;
    private SQLiteAdapter sqLiteAdapter;

    CustomAdapter(@NonNull Context context, ArrayList<RecordModel> list) {
        super(context, 0, list);
        mContext = context;
        recordList = list;

    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);

        RecordModel recordModel = recordList.get(position);
        sqLiteAdapter = new SQLiteAdapter(mContext);

        TextView keyword_1 = listItem.findViewById(R.id.textView_keyword);
        keyword_1.setText(recordModel.getKeyword());

        TextView contactNames = listItem.findViewById(R.id.textView_contact_names);
        contactNames.setText(recordModel.getContactNames());


        ImageView imageButton = listItem.findViewById(R.id.delete);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                new AlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String key = recordList.get(position).getId();

                                sqLiteAdapter.deleteRow(sqLiteAdapter, key);
                                recordList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })

                        .create().show();
            }
        });


        return listItem;
    }


}


