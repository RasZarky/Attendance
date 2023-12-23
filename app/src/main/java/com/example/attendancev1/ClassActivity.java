package com.example.attendancev1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ClassActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelClass> userList;
    Adapter adapter;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        db = new DBHelper(this);
        Cursor viewStatus = db.viewStatus();
        TextView txtProgress = findViewById(R.id.txtProgress);

        if (viewStatus.getCount() == 0) {

            intData1();
        } else {

            intData();
        }

        initRecycler();
        bottomNavigation();

    }

    private void bottomNavigation() {
        FloatingActionButton addBtn;
        LinearLayout ProfileBtn, connectBtn, classBtn, HomeButton;
        ProfileBtn = findViewById(R.id.profileBtn);
        connectBtn = findViewById(R.id.connectBtn);
        addBtn = findViewById(R.id.addBtn);
        classBtn = findViewById(R.id.classBtn);
        HomeButton = findViewById(R.id.homeBtn);

        Cursor stat = db.viewStatus();
        if (stat.getCount() == 0) {

            HomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ClassActivity.this, HomeActivity.class));
                    finish();
                }
            });

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor viewAllInClass = db.viewAllInClass();

                        startActivity(new Intent(ClassActivity.this, scannerActivity.class));
                        finish();
                }
            });

            connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ClassActivity.this)
                            .setTitle("Turn on Mobile data")
                            .setMessage("All features of the app work without internet but some features require internet for the app to reflect changes \n\n Turn on mobile data?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                                    startActivity(intent);
                                }
                            }).create().show();
                }
            });

        } else {

            HomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ClassActivity.this, HomeActivity.class));
                    finish();
                }
            });

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ClassActivity.this, MainActivity.class));
                    finish();
                }
            });

            connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(ClassActivity.this)
                            .setTitle("Turn on Mobile data")
                            .setMessage("All features of the app work without internet but some features require internet for the app to reflect changes \n\n Turn on mobile data?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                                    startActivity(intent);
                                }
                            }).create().show();

                }
            });

        }
        ProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClassActivity.this, profile.class));
                finish();
            }
        });


    }


    private void intData() {
        userList = new ArrayList<>();

        Cursor getClassData = db.viewAllInClass();
        boolean isEmpty = getClassData.getCount() == 0;
        if (isEmpty) {
            Toast.makeText(ClassActivity.this, "No class Available", Toast.LENGTH_SHORT).show();
        } else {
            int id = 1;
            while (id < getClassData.getCount() + 1) {
                String Id = Integer.toString(id);
                Cursor viewProgrammeInClass = db.viewProgrammeInClass(Id);
                Cursor viewCourseInClass = db.viewCourseInClass(Id);
                Cursor viewLevelInClass = db.viewLevelInClass(Id);

                StringBuilder buffer = new StringBuilder();
                while (viewProgrammeInClass.moveToNext()) {
                    buffer.append(viewProgrammeInClass.getString(0));
                }
                String Programme = buffer.toString();

                StringBuilder buffer2 = new StringBuilder();
                while (viewCourseInClass.moveToNext()) {
                    buffer2.append(viewCourseInClass.getString(0));
                }
                String Course = buffer2.toString();

                StringBuilder buffer3 = new StringBuilder();
                while (viewLevelInClass.moveToNext()) {
                    buffer3.append(viewLevelInClass.getString(0));
                }
                String Level = buffer3.toString();

                userList.add(0,new ModelClass(Programme, Course, "Level: " + Level, ""));
                id++;
            }
        }
    }

    private void intData1() {
        userList = new ArrayList<>();
        Cursor viewProgress = db.viewAllInProgress();

        if (viewProgress.getCount() > 0) {
            int id = 1;

            while (id < viewProgress.getCount() + 1) {
                String Id = Integer.toString(id);
                Cursor viewCourseInProgressDetails = db.viewCourseInProgress(Id);
                StringBuilder buffer = new StringBuilder();
                while (viewCourseInProgressDetails.moveToNext()) {
                    buffer.append(viewCourseInProgressDetails.getString(0));
                }
                String Course = buffer.toString();

                Cursor viewProgressInProgressDetails = db.viewProgressInProgress(Course);
                StringBuilder buffer2 = new StringBuilder();
                while (viewProgressInProgressDetails.moveToNext()) {
                    buffer2.append(viewProgressInProgressDetails.getString(0));
                }
                String Progress = buffer2.toString();

                userList.add(0,new ModelClass("", Course, "", ""));
                id++;
            }
        }
    }

    private void initRecycler() {
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(userList, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.isClickable = true;
    }
}