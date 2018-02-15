package com.madhouseapps.pdfreader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllFilesActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ListView listView;
    private List<FileInfo> fileInfoList;
    private FileAdapter fileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_files);

        toolbar = findViewById(R.id.toolbar_all_files);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Files");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.list_view_all_files);
        fileInfoList = new ArrayList<>();
        fileAdapter = new FileAdapter(AllFilesActivity.this, fileInfoList);
        listView.setAdapter(fileAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                intent.putExtra("fileUri", fileInfoList.get(i).getUri());
                startActivity(intent);
            }
        });

        /*
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                initList(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
        });
        */

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                getFile(root);
            }
        });
    }

    private void initList(String path) {
        try {
            File file = new File(path);
            File[] fileArr = file.listFiles();
            for (File file1 : fileArr) {
                if (file1.isDirectory()) {
                    initList(file1.getAbsolutePath());
                } else {
                    if (file1.getName().endsWith(".pdf")) {
                        fileInfoList.add(new FileInfo(file1.getName(), file1.toURI().toString()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<FileInfo> getFile(File dir) {
        File[] listFile = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getFile(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(".pdf")) {
                        fileInfoList.add(new FileInfo(listFile[i].getName(), listFile[i].toURI().toString()));
                    }
                }
            }
        }
        return fileInfoList;
    }
}