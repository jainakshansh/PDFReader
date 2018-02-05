package com.madhouseapps.pdfreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

public class ReadingActivity extends AppCompatActivity {

    private PDFView pdfView;
    private String fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Intent uriIntent = getIntent();
        pdfView = findViewById(R.id.pdfView);
        if (uriIntent.getExtras() != null) {
            fileUri = uriIntent.getStringExtra("fileUri");
            pdfView.fromUri(Uri.parse(fileUri))
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .password(null)
                    .scrollHandle(null)
                    .spacing(0)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .load();
        } else {
            Toast.makeText(this, "File Not Found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
