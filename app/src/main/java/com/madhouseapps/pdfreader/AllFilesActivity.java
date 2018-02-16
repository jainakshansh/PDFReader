package com.madhouseapps.pdfreader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        getFiles(file);
    }

    private List<FileInfo> getFiles(File dir) {
        File[] filesList = dir.listFiles();
        if (filesList != null && filesList.length > 0) {
            for (int i = 0; i < filesList.length; i++) {
                if (filesList[i].isDirectory()) {
                    getFiles(filesList[i]);
                } else {
                    if (filesList[i].getName().endsWith(".pdf")) {
                        fileInfoList.add(new FileInfo(filesList[i].getName(), filesList[i].toURI().toString()));
                    }
                }
            }
        }
        return fileInfoList;
    }
}