package com.madhouseapps.pdfreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Akshansh on 14-02-2018.
 */

public class FileAdapter extends ArrayAdapter<FileInfo> {

    private Context context;
    private List<FileInfo> fileInfoList;

    public FileAdapter(Context context, List<FileInfo> fileInfoList) {
        super(context, 0, fileInfoList);
        this.context = context;
        this.fileInfoList = fileInfoList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        TextView textView = view.findViewById(R.id.file_title);
        textView.setText(fileInfoList.get(position).getName());

        return view;
    }
}
