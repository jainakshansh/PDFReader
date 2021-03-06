package com.madhouseapps.pdfreader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Typeface avenir;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fabCreate;
    private Button tapToOpen;
    private ImageView bookIcon;

    private static final int READ_REQUEST_CODE = 6;

    private TextView recent1, recent2, recent3, recent4;
    private TextView titleOne, titleTwo, titleThree, titleFour;

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
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("PDF Creator & Reader");
        }
        /*
        Reading the permission status stored in the shared preferences.
         */
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        bookIcon = findViewById(R.id.pdf_read_icon);
        avenir = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Book.ttf");
        tapToOpen = findViewById(R.id.tap_to_open_pdf);
        tapToOpen.setTypeface(avenir);

        navigationView = findViewById(R.id.navigation_view_main);
        drawerLayout = findViewById(R.id.drawer_layout_main);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.fab_create_pdf:
                        permissionCodeLogic();
                        startActivity(new Intent(getApplicationContext(), CreatePDFActivity.class));
                        break;
                    case R.id.show_all_files:
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Loading all PDF files on device.\nPlease Wait ... ", Toast.LENGTH_LONG).show();
                            }
                        });
                        permissionCodeLogic();
                        startActivity(new Intent(getApplicationContext(), AllFilesActivity.class));
                        break;
                    case R.id.image_to_pdf:
                        permissionCodeLogic();
                        startActivity(new Intent(getApplicationContext(), ImageToPDFActivity.class));
                        break;
                    case R.id.rate_app:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.madhouseapps.pdfreader")));
                        break;
                    case R.id.share_app:
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this awesome PDF Creator and Reader by MadHouse at: https://play.google.com/store/apps/details?id=com.madhouseapps.pdfreader");
                        shareIntent.setType("text/plain");
                        startActivity(shareIntent);
                        break;
                    case R.id.more_apps:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=MadHouse")));
                        break;
                }
                return true;
            }
        });

        /*
        Recent referencing and getting the recent files that were opened.
         */
        recent1 = findViewById(R.id.recent_one);
        recent1.setTypeface(avenir, Typeface.BOLD);
        recent2 = findViewById(R.id.recent_two);
        recent2.setTypeface(avenir, Typeface.BOLD);
        recent3 = findViewById(R.id.recent_three);
        recent3.setTypeface(avenir, Typeface.BOLD);
        recent4 = findViewById(R.id.recent_four);
        recent4.setTypeface(avenir, Typeface.BOLD);

        titleOne = findViewById(R.id.recent_one_title);
        titleOne.setTypeface(avenir);
        titleTwo = findViewById(R.id.recent_two_title);
        titleTwo.setTypeface(avenir);
        titleThree = findViewById(R.id.recent_three_title);
        titleThree.setTypeface(avenir);
        titleFour = findViewById(R.id.recent_four_title);
        titleFour.setTypeface(avenir);

        fabCreate = findViewById(R.id.fab_create_pdf);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreatePDFActivity.class));
            }
        });

        tapToOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionCodeLogic();
            }
        });

        recentFiles(null, null);

        /*
        Handling the intent from the AllFiles to store the recent values and sending user ahead to the Reading Activity.
         */
        Intent intent = getIntent();
        if (intent.getExtras() == null) {
            recentFiles(null, null);
        } else {
            String uri = intent.getStringExtra("fileUri");
            String title = intent.getStringExtra("fileTitle");
            if (uri != null && title != null) {
                recentFiles(Uri.parse(uri), title);
                Intent readIntent = new Intent(getApplicationContext(), ReadingActivity.class);
                readIntent.putExtra("fileUri", uri);
                readIntent.putExtra("fileTitle", title);
                startActivity(readIntent);
            } else {
                recentFiles(null, null);
            }
        }
    }

    private void render() {
        Intent readIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        readIntent.addCategory(Intent.CATEGORY_OPENABLE);
        readIntent.setType("application/pdf");
        startActivityForResult(readIntent, READ_REQUEST_CODE);
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
            render();
        }
    }

    private void recentFiles(Uri uri, String title) {
        String res1 = null, res2 = null, res3 = null, res4 = null;
        String title1 = null, title2 = null, title3 = null, title4 = null;
        if (uri != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            res1 = preferences.getString("recentOne", null);
            title1 = preferences.getString("title1", null);
            res2 = preferences.getString("recentTwo", null);
            title2 = preferences.getString("title2", null);
            res3 = preferences.getString("recentThree", null);
            title3 = preferences.getString("title3", null);
            res4 = preferences.getString("recentFour", null);
            title4 = preferences.getString("title4", null);

            res4 = res3;
            res3 = res2;
            res2 = res1;
            res1 = uri.toString();
            title4 = title3;
            title3 = title2;
            title2 = title1;
            title1 = title;

            editor.putString("recentOne", res1);
            editor.putString("title1", title1);
            editor.putString("recentTwo", res2);
            editor.putString("title2", title2);
            editor.putString("recentThree", res3);
            editor.putString("title3", title3);
            editor.putString("recentFour", res4);
            editor.putString("title4", title4);
            editor.apply();

            final String finalRes = res1;
            final String finalTitle = title1;
            recent1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                    intent.putExtra("fileUri", finalRes);
                    intent.putExtra("fileTitle", finalTitle);
                    startActivity(intent);
                }
            });
            final String finalRes1 = res2;
            final String finalTitle1 = title2;
            recent2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                    intent.putExtra("fileUri", finalRes1);
                    intent.putExtra("fileTitle", finalTitle1);
                    startActivity(intent);
                }
            });
            final String finalRes2 = res3;
            final String finalTitle2 = title3;
            recent3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                    intent.putExtra("fileUri", finalRes2);
                    intent.putExtra("fileTitle", finalTitle2);
                    startActivity(intent);
                }
            });
            final String finalRes3 = res4;
            final String finalTitle3 = title4;
            recent4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                    intent.putExtra("fileUri", finalRes3);
                    intent.putExtra("fileTitle", finalTitle3);
                    startActivity(intent);
                }
            });
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            res1 = preferences.getString("recentOne", null);
            title1 = preferences.getString("title1", null);
            res2 = preferences.getString("recentTwo", null);
            title2 = preferences.getString("title2", null);
            res3 = preferences.getString("recentThree", null);
            title3 = preferences.getString("title3", null);
            res4 = preferences.getString("recentFour", null);
            title4 = preferences.getString("title4", null);

            final String finalRes = res1;
            final String finalTitle4 = title1;
            recent1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalRes != null) {
                        Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                        intent.putExtra("fileUri", finalRes);
                        intent.putExtra("fileTitle", finalTitle4);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Recent not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            final String finalRes1 = res2;
            final String finalTitle5 = title2;
            recent2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalRes1 != null) {
                        Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                        intent.putExtra("fileUri", finalRes1);
                        intent.putExtra("fileTitle", finalTitle5);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Recent not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            final String finalRes2 = res3;
            final String finalTitle6 = title3;
            recent3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalRes2 != null) {
                        Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                        intent.putExtra("fileUri", finalRes2);
                        intent.putExtra("fileTitle", finalTitle6);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Recent not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            final String finalRes3 = res4;
            final String finalTitle7 = title4;
            recent4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalRes3 != null) {
                        Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                        intent.putExtra("fileUri", finalRes3);
                        intent.putExtra("fileTitle", finalTitle7);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Recent not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (res1 != null) {
            recent1.setBackgroundResource(R.drawable.pdf_book);
        }
        if (res2 != null) {
            recent2.setBackgroundResource(R.drawable.pdf_book);
        }
        if (res3 != null) {
            recent3.setBackgroundResource(R.drawable.pdf_book);
        }
        if (res4 != null) {
            recent4.setBackgroundResource(R.drawable.pdf_book);
        }

        if (title1 != null) {
            titleOne.setText(title1);
        }
        if (title2 != null) {
            titleTwo.setText(title2);
        }
        if (title3 != null) {
            titleThree.setText(title3);
        }
        if (title4 != null) {
            titleFour.setText(title4);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
                render();
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
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //After getting the permissions.
                render();
            }
        }
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                intent.putExtra("fileUri", uri.toString());

                Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                String title = returnCursor.getString(nameIndex);

                recentFiles(uri, title);
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                /*
                After getting the permissions.
                 */
                render();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}