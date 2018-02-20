package com.madhouseapps.pdfreader;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreatePDFActivity extends AppCompatActivity {

    private TextView titleTV, contentTV;
    private EditText titleInput, contentInput;
    private FloatingActionButton fabSavePDF;
    private Typeface avenir;

    private DisplayMetrics displayMetrics;
    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pdf);

        avenir = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Book.ttf");
        titleTV = findViewById(R.id.pdf_title_label);
        titleTV.setTypeface(avenir);
        contentTV = findViewById(R.id.pdf_content_label);
        contentTV.setTypeface(avenir);
        titleInput = findViewById(R.id.pdf_title_input);
        titleInput.setTypeface(avenir);
        contentInput = findViewById(R.id.pdf_content_input);
        contentInput.setTypeface(avenir);
        fabSavePDF = findViewById(R.id.fab_save_pdf);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        fabSavePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdfDocument document = new PdfDocument();
                int pageNumber = 1;
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width,
                        height - 20, pageNumber).create();

                PdfDocument.Page page = document.startPage(pageInfo);

                Canvas canvas = page.getCanvas();
                contentInput.draw(canvas);

                document.finishPage(page);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
                String pdfName = titleInput.getText().toString() + sdf.format(Calendar.getInstance().getTime()) + ".pdf";
                if (!contentInput.getText().toString().trim().isEmpty()) {
                    Log.d("ADebug", "Content present condition");
                    createFile(document, pdfName);
                } else {
                    Toast.makeText(CreatePDFActivity.this, "Cannot save empty PDF file!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createFile(PdfDocument document, String pdfName) {
        FileOutputStream fos;
        try {
            File folder = new File(Environment.getExternalStorageDirectory(), "PDFReader MadHouse");
            if (!folder.exists()) {
                Log.d("ADebug", "Checking if folder exists");
                folder.mkdirs();
            }
            File outputFile = new File(folder, pdfName);
            outputFile.createNewFile();
            fos = new FileOutputStream(outputFile);
            document.writeTo(fos);
            Toast.makeText(CreatePDFActivity.this, "File created successfully!", Toast.LENGTH_SHORT).show();
            Log.d("ADebug", "File created successfully!");

            //Opening the recently saved file.
            Intent openIntent = new Intent(getApplicationContext(), ReadingActivity.class);
            openIntent.putExtra("fileUri", outputFile.toURI().toString());
            startActivity(openIntent);

            finish();
        } catch (IOException e) {
            Toast.makeText(this, "File creation failed!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
