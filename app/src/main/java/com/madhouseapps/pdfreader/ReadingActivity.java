package com.madhouseapps.pdfreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

public class ReadingActivity extends AppCompatActivity {

    private PDFView pdfView;
    private String fileUri, fileTitle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        toolbar = findViewById(R.id.toolbar_reading);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent uriIntent = getIntent();
        pdfView = findViewById(R.id.pdfView);
        if (uriIntent.getAction() != null) {
            if (Intent.ACTION_VIEW.equals(uriIntent.getAction())) {
                fileUri = uriIntent.getData().toString();
            }
        } else if (uriIntent.getExtras() != null) {
            fileUri = uriIntent.getStringExtra("fileUri");
            fileTitle = uriIntent.getStringExtra("fileTitle");
        } else {
            Toast.makeText(this, "File Not Found!", Toast.LENGTH_SHORT).show();
        }
        getSupportActionBar().setTitle(fileTitle);
        pdfView.fromUri(Uri.parse(fileUri))
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .password(null)
                .scrollHandle(null)
                .spacing(0)
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
