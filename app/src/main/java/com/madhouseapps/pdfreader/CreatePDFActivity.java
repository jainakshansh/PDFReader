package com.madhouseapps.pdfreader;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

public class CreatePDFActivity extends AppCompatActivity {

    private TextView titleTV, contentTV;
    private EditText titleInput, contentInput;
    private FloatingActionButton fabSavePDF;
    private Typeface avenir;

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
    }
}
