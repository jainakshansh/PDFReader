package com.madhouseapps.pdfreader;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.io.File;

public class AllFilesActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private String pdfPattern = ".pdf";
    File[] filesList;
    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_files);

        toolbar = findViewById(R.id.toolbar_all_files);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dir = Environment.getExternalStorageDirectory();
        walkDir(dir);
    }

    private void walkDir(File dir) {
        filesList = dir.listFiles();

        if (filesList != null) {
            for (int i = 0; i < filesList.length; i++) {
                if (filesList[i].isDirectory()) {
                    walkDir(filesList[i]);
                } else {
                    if (filesList[i].getName().endsWith(pdfPattern)) {

                    }
                }
            }
        }
    }
}
