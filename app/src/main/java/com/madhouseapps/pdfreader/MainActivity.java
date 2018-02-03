package com.madhouseapps.pdfreader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Typeface avenir;

    private ConstraintLayout constraintLayoutParent;
    private FloatingActionButton fabCreate;
    private Button tapToOpen;

    private ImageView readPDF, bookIcon;

    private static final int READ_REQUEST_CODE = 9;

    /*
   These variables are for requesting permissions at run-time.
    */
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PERMISSION_CALLBACK_CONSTANT = 9;
    private static final int REQUEST_PERMISSION_SETTINGS = 7;
    private String[] permissionsRequired = new String[]{
            //Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting up the activity for full screen mode.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        /*
        Reading the permission status stored in the shared preferences.
         */
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        constraintLayoutParent = findViewById(R.id.constraint_main_parent);
        bookIcon = findViewById(R.id.pdf_read_icon);
        avenir = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Book.ttf");
        tapToOpen = findViewById(R.id.tap_to_open_pdf);
        tapToOpen.setTypeface(avenir);

        fabCreate = findViewById(R.id.fab_create_pdf);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreatePDFActivity.class));
            }
        });

        constraintLayoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCodeLogic();
            }
        });
    }

    private void render() {
        fabCreate.setVisibility(View.GONE);
        tapToOpen.setVisibility(View.GONE);
        bookIcon.setVisibility(View.GONE);
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            readPDF = findViewById(R.id.main_image_container);
            int width = (int) displayMetrics.xdpi;
            int height = (int) displayMetrics.ydpi;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/Akshansh Jain.pdf");
            PdfRenderer pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

            Matrix matrix = readPDF.getImageMatrix();
            Rect rect = new Rect(0, 0, width, height);
            pdfRenderer.openPage(0).render(bitmap, rect, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            readPDF.setImageMatrix(matrix);
            readPDF.setImageBitmap(bitmap);
            readPDF.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void permissionCodeLogic() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    /*|| ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, Manifest.permission.CAMERA)*/) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                //Manifest.permission.CAMERA,
                                                Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSIONS);
                            }
                        }).setActionTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white))
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                /*Manifest.permission.CAMERA*/},
                        REQUEST_PERMISSIONS);
            }
        } else {
            /*
            Calling the function to be performed if the permissions have already been provided.
             */
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
        render();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //Checking if all permissions are granted.
            boolean allGranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allGranted = true;
                } else {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[1])
                    /*|| ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, permissionsRequired[2])*/) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Storage Permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //After getting the permissions.
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                /*
                After getting the permissions.
                 */
                proceedAfterPermission();
            }
        }
    }
}
