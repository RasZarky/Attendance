package com.example.attendancev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity {

    DBHelper db;
    BarChart barChart;
    TextView MoreInfo, MoreInfo2,bestProgress, leastProgress;
    ListView listView;
    ProgressBar p1,p2,p3;
    RelativeLayout best,least;
    FloatingActionButton addBtn;
    LinearLayout connectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = new DBHelper(this);
        barChart = findViewById(R.id.BarChart);
        MoreInfo = findViewById(R.id.MoreInfo);
        MoreInfo2 = findViewById(R.id.MoreInfo2);
        bestProgress = findViewById(R.id.bestProgress);
        leastProgress = findViewById(R.id.leastProgress);
        p1 = findViewById(R.id.progress_circular1);
        p2 = findViewById(R.id.progress_circular2);
        p3 = findViewById(R.id.progress_circular3);
        listView = findViewById(R.id.listview);
        best = findViewById(R.id.best);
        least = findViewById(R.id.least);
        addBtn = findViewById(R.id.addBtn);
        connectBtn = findViewById(R.id.connectBtn);


        Cursor viewProgress = db.viewAllInClass();
        ArrayList<BarEntry> classes = new ArrayList<>();
        ArrayList<Integer> ArrayProgress = new ArrayList<>();
        ArrayAdapter<String> info = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);

        best.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up));
        least.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up));

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HomeActivity.this)
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

        if (viewProgress.getCount() > 0) {
            int id = 1;

            while (id < viewProgress.getCount() + 1) {
                String Id = Integer.toString(id);
                Cursor viewCourseInProgressDetails = db.viewCourseInClass(Id);
                StringBuilder buffer = new StringBuilder();
                while (viewCourseInProgressDetails.moveToNext()) {
                    buffer.append(viewCourseInProgressDetails.getString(0));
                }
                String course = buffer.toString();

                Cursor viewStat = db.viewStatus();
                if (viewStat.getCount() == 0){
                    Cursor viewStudent = db.viewData1();
                    StringBuilder buffer4 = new StringBuilder();

                    while(viewStudent.moveToNext()){
                        buffer4.append(viewStudent.getString(4));
                    }

                    String ID = buffer4.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                    int finalId = id;
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String User = dataSnapshot.child("indexNumber").getValue(String.class);
                                String finalCourse = dataSnapshot.child(course).child("course").getValue(String.class);
                                String Progress = dataSnapshot.child(course).child("progress").getValue(String.class);

                                // String TutorProgress = trick.getText().toString();

                                if (course.equals(finalCourse) && User.equals(ID)) {
                                    p1.setVisibility(View.GONE);
                                    p2.setVisibility(View.GONE);
                                    p3.setVisibility(View.GONE);
                                    classes.add(new BarEntry(finalId, Integer.parseInt(Progress)));
                                    String Info = Id + ".  " + course + "    Progress: " + Progress;
                                    ArrayProgress.add(Integer.parseInt(Progress));
                                    info.add(Info);
                                    bottomNavigation();

                                }
                                listView.setAdapter(info);
                            }

                            BarDataSet barDataSet = new BarDataSet(classes, "classes");
                            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            barDataSet.setValueTextColor(Color.BLACK);
                            barDataSet.setValueTextSize(16f);

                            BarData barData = new BarData(barDataSet);

                            barChart.setFitBars(true);
                            barChart.setData(barData);
                            barChart.getDescription().setText("Classes Summery");
                            barChart.animateXY(2000, 3000);

                            if (ArrayProgress.size() > 0) {
                                Object objMax = Collections.max(ArrayProgress);
                                Object objMin = Collections.min(ArrayProgress);

                                bestProgress.setText(objMax.toString()+" Days");
                                leastProgress.setText(objMin.toString()+" Days");
                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else {
                    Cursor viewStudent = db.viewData2();
                    StringBuilder buffer4 = new StringBuilder();

                    while(viewStudent.moveToNext()){
                        buffer4.append(viewStudent.getString(4));
                    }

                    String ID = buffer4.toString();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                    int finalId = id;
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                Toast.makeText(getApplicationContext(), "No student has joined this class", Toast.LENGTH_SHORT).show();
                            }

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String User = dataSnapshot.child("indexNumber").getValue(String.class);
                                String finalCourse = dataSnapshot.child(course).child("course").getValue(String.class);
                                String Progress = dataSnapshot.child(course).child("progress").getValue(String.class);

                                // String TutorProgress = trick.getText().toString();

                                if (course.equals(finalCourse) && User.equals(ID)) {
                                    p1.setVisibility(View.GONE);
                                    p2.setVisibility(View.GONE);
                                    p3.setVisibility(View.GONE);
                                    classes.add(new BarEntry(finalId, Integer.parseInt(Progress)));
                                    String Info = Id + ".  " + course + "    Progress: " + Progress;
                                    ArrayProgress.add(Integer.parseInt(Progress));
                                    info.add(Info);
                                    listView.setAdapter(info);
                                    bottomNavigation();
                                }
                            }

                            BarDataSet barDataSet = new BarDataSet(classes, "classes");
                            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            barDataSet.setValueTextColor(Color.BLACK);
                            barDataSet.setValueTextSize(16f);

                            BarData barData = new BarData(barDataSet);

                            barChart.setFitBars(true);
                            barChart.setData(barData);
                            barChart.getDescription().setText("Classes Summery");
                            barChart.animateXY(2000, 3000);

                            if (ArrayProgress.size() > 0) {
                                Object objMax = Collections.max(ArrayProgress);
                                Object objMin = Collections.min(ArrayProgress);

                                bestProgress.setText(objMax.toString()+" Days");
                                leastProgress.setText(objMin.toString()+" Days");
                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                /*Cursor viewProgressInProgressDetails = db.viewProgressInProgress(Course);
                StringBuilder buffer2 = new StringBuilder();
                while (viewProgressInProgressDetails.moveToNext()) {
                    buffer2.append(viewProgressInProgressDetails.getString(0));
                }
                String Progress = buffer2.toString();
                int Prog = Integer.parseInt(Progress);



              */

                id++;
            }
        }else {
            p1.setVisibility(View.GONE);
            p2.setVisibility(View.GONE);
            p3.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
            bottomNavigation();
        }



        MoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
                MoreInfo.setVisibility(View.GONE);
                MoreInfo2.setVisibility(View.VISIBLE);
            }
        });

        MoreInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.GONE);
                MoreInfo.setVisibility(View.VISIBLE);
                MoreInfo2.setVisibility(View.GONE);
            }
        });


    }

    private void bottomNavigation() {
        LinearLayout profileBtn, connectBtn, classBtn, HomeButton;
        profileBtn = findViewById(R.id.profileBtn);
        classBtn = findViewById(R.id.classBtn);
        HomeButton = findViewById(R.id.homeBtn);

        Cursor stat = db.viewStatus();
        if (stat.getCount() == 0) {

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        startActivity(new Intent(HomeActivity.this, scannerActivity.class));
                        finish();
                }
            });


            classBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, ClassActivity.class));
                    finish();
                }
            });
        } else {

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    finish();
                }
            });

            classBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, ClassActivity.class));
                    finish();
                }
            });
        }


        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, profile.class));
                finish();
            }
        });
    }

}