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

    private String pdfPattern = ".pdf";
    File[] filesList;
    private String dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_files);

        toolbar = findViewById(R.id.toolbar_all_files);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.list_view_all_files);
        fileInfoList = new ArrayList<>();
        fileAdapter = new FileAdapter(this, fileInfoList);
        listView.setAdapter(fileAdapter);

        dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        walkDir(dir);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                intent.putExtra("fileUri", fileInfoList.get(i).getUri());
                startActivity(intent);
            }
        });
    }

    private void walkDir(String dir) {
        try {
            File file = new File(dir);
            filesList = file.listFiles();

            if (filesList != null) {
                for (int i = 0; i < filesList.length; i++) {
                    if (filesList[i].isDirectory()) {
                        walkDir(filesList[i].getAbsolutePath());
                    } else {
                        if (filesList[i].getName().endsWith(pdfPattern)) {
                            fileInfoList.add(new FileInfo(filesList[i].getName(), filesList[i].getAbsolutePath()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
