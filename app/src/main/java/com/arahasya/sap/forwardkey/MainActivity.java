package com.arahasya.sap.forwardkey;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.Toast;

import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.Slide;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    String[] permissions = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS

    };

    private RequestPermissionHandler mRequestPermissionHandler;
    private SQLiteAdapter sqLiteAdapter;
    private ArrayList<RecordModel> recordList;
    private ListView recordListView;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    loadTutorial();
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();

        setContentView(R.layout.activity_main);

        ImageView guide = findViewById(R.id.guide);
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(true)
                        .setTitle("Guide")
                        .setMessage(R.string.info_main)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .create().show();
            }
        });

        ImageView show_tutorial = findViewById(R.id.show_tutorial);
        show_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTutorial();
            }
        });

        mRequestPermissionHandler = new RequestPermissionHandler();

        recordList = new ArrayList<>();
        recordListView = findViewById(R.id.record_list);
        sqLiteAdapter = new SQLiteAdapter(MainActivity.this);

        checkPermissions();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);

            }

        });
    }

    public void loadTutorial() {

        new IntroductionBuilder(this).withSlides(generateSlides()).introduceMyself();
    }

    private List<Slide> generateSlides() {
        List<Slide> result = new ArrayList<>();

        result.add(new Slide()
                .withTitle("ForwardKey")
                .withDescription("Automatically forward incoming messages based on keywords.")
                .withColorResource(R.color.azureColorPrimaryDark)
                .withImage(R.drawable.ic_scan)

        );

        result.add(new Slide()
                .withTitle("Setup")
                .withDescription("Add a keyword record in 2 easy steps.")
                .withColorResource(R.color.azureColorPrimary)
                .withImage(R.drawable.ic_sent)
        );

        result.add(new Slide()
                .withTitle("Security")
                .withDescription(R.string.security)
                .withColorResource(R.color.azureColorPrimaryDark)
                .withImage(R.drawable.ic_group_white)
        );

        return result;
    }

    private void checkPermissions() {
        int PER_CODE = 123;
        mRequestPermissionHandler.requestPermission(this, permissions,
                PER_CODE, new RequestPermissionHandler.RequestPermissionListener() {
                    @Override
                    public void onSuccess() {

                        displayRecordList();
                    }

                    @Override
                    public void onFailed() {


                        Toast.makeText(MainActivity.this, "Request permission failed", Toast.LENGTH_SHORT).show();

                        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                                .setCancelable(false)
                                .setTitle("Permission necessary")
                                .setMessage("Please grant all permissions to proceed with app.")
                                .setPositiveButton("Re-Try", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkPermissions();
                                    }
                                })
                                .setNegativeButton("Settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                        intent.setData(uri);
                                        MainActivity.this.startActivity(intent);
                                        checkPermissions();
                                    }
                                })

                                .create().show();
                    }

                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionHandler.onRequestPermissionsResult(requestCode,
                grantResults);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void displayRecordList() {


        recordList = sqLiteAdapter.getAllRecords(sqLiteAdapter);

        CustomAdapter customAdapter = new CustomAdapter(this, recordList);
        customAdapter.notifyDataSetChanged();
        recordListView.setAdapter(customAdapter);

    }

}
