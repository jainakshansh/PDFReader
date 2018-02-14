package com.madhouseapps.pdfreader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageToPDFActivity extends AppCompatActivity {

    private ImageView selectedImage;
    private Toolbar toolbar;
    private Button selectImage, convertImage;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_pdf);

        toolbar = findViewById(R.id.toolbar_image);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Image to PDF");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectedImage = findViewById(R.id.image_selected);
        selectImage = findViewById(R.id.select_image_button);
        convertImage = findViewById(R.id.convert_img_pdf);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 9);
            }
        });

        convertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    float height = displayMetrics.heightPixels;
                    float width = displayMetrics.widthPixels;

                    int imgHeight = (int) height;
                    int imgWidth = (int) width;

                    PdfDocument pdfDocument = new PdfDocument();
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    pdfDocument.finishPage(page);

                    String pdfName = String.valueOf(System.currentTimeMillis() + ".pdf");

                    try {
                        File folder = new File(Environment.getExternalStorageDirectory(), "PDFReader MadHouse");
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        File outputFile = new File(folder, pdfName);
                        outputFile.createNewFile();
                        pdfDocument.writeTo(new FileOutputStream(outputFile));
                        Toast.makeText(ImageToPDFActivity.this, "File created successfully!", Toast.LENGTH_SHORT).show();

                        //Opening the recently saved file.
                        Intent openIntent = new Intent(getApplicationContext(), ReadingActivity.class);
                        openIntent.putExtra("fileUri", outputFile.toURI().toString());
                        startActivity(openIntent);

                        finish();
                    } catch (IOException e) {
                        Toast.makeText(ImageToPDFActivity.this, "File creation failed!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } finally {
                        pdfDocument.close();
                    }
                } else {
                    Toast.makeText(ImageToPDFActivity.this, "Please select an image to convert!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 9 && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                String[] file = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(uri, file, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(file[0]);
                String image = cursor.getString(columnIndex);
                cursor.close();

                bitmap = BitmapFactory.decodeFile(image);
                selectedImage.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
