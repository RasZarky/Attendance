 package com.example.attendancev1;

import android.app.Notification;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ProgressActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelClass> userList;
    Adapter adapter;
    DBHelper db;

    ImageView qrimage;
    TextView attended, courseName, trick;
    ImageView btnPlus, btnMinus;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        db = new DBHelper(this);
        qrimage = findViewById(R.id.qrimage2);
        btnPlus = findViewById(R.id.plus);
        btnMinus = findViewById(R.id.minus);
        attended = findViewById(R.id.textView2);
        courseName = findViewById(R.id.courseName);
        trick = findViewById(R.id.trick);
        progress = findViewById(R.id.progress_circular);
        recyclerView = findViewById(R.id.recyclerView);

        progress.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
        String Course = bundle.getString("Course");
        String Programme = bundle.getString("Programme");

        courseName.setText(Programme);

        userList = new ArrayList<>();



        initialWork();
        exqListener();
        initRecycler();
        intData();


    }

    private void intData() {
        Bundle bundle = getIntent().getExtras();
        String Course = bundle.getString("Course");
        String course = Course.toUpperCase().replaceAll(" ", "");

        final Cursor viewTutorIndex = db.viewIndexInTutor();
        StringBuilder builder = new StringBuilder();
        while (viewTutorIndex.moveToNext()) {
            builder.append(viewTutorIndex.getString(0));
        }
        String ID = builder.toString();

        Cursor getProgress = db.viewProgressInProgress(course);
        StringBuilder buffer1 = new StringBuilder();
        while (getProgress.moveToNext()) {
            buffer1.append(getProgress.getString(0));
        }
        String c = buffer1.toString();
        db.close();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Users");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "No student has joined this class", Toast.LENGTH_SHORT).show();
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String User = dataSnapshot.child("indexNumber").getValue(String.class);
                    String finalCourse = dataSnapshot.child(course).child("course").getValue(String.class);
                    String Progress = dataSnapshot.child(course).child("progress").getValue(String.class);

                    if (course.equals(finalCourse) && User.equals(ID)) {
                        String TutorProgress = Progress;
                        trick.setText(TutorProgress);
                        attended.setText("Days attended:"+trick.getText().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                if (!snapshot.exists()) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "No student has joined this class", Toast.LENGTH_SHORT).show();
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String User = dataSnapshot.child("indexNumber").getValue(String.class);
                    String finalCourse = dataSnapshot.child(course).child("course").getValue(String.class);
                    String Progress = dataSnapshot.child(course).child("progress").getValue(String.class);


                    String StudentProgress = null;
                  //  if (!User.equals(ID)) {
                        StudentProgress = Progress;
                   // }
                    String TutorProgress = trick.getText().toString();
                    String total = StudentProgress + "/" + TutorProgress;

                    if (course.equals(finalCourse) && !User.equals(ID)) {
                        progress.setVisibility(View.GONE);
                        userList.add(new ModelClass("", User, "", total));
                    }
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initRecycler() {
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(userList, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.isClickable = false;
    }


    private void exqListener() {

        intData();

        final Cursor viewTutorIndex = db.viewIndexInTutor();
        StringBuilder builder = new StringBuilder();
        while (viewTutorIndex.moveToNext()) {
            builder.append(viewTutorIndex.getString(0));
        }
        String ID = builder.toString();

        btnMinus.setOnClickListener(new View.OnClickListener() {
            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            DatabaseReference reference = rootNode.getReference("Users").child(ID);

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProgressActivity.this)
                        .setTitle("Update class Progress")
                        .setMessage("Press ok the decreases days class has been attended by 1")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Bundle bundle = getIntent().getExtras();
                                String Course = bundle.getString("Course");
                                String course = Course;
                                Cursor viewProgress = db.viewAllInProgress();

                                String progress = trick.getText().toString();

                                if (progress == null) {
                                    Snackbar.make(v, "Class has never been attended or data is still loading.", BaseTransientBottomBar.LENGTH_LONG).show();
                                    attended.setText("Days attended: 0");
                                } else {

                                    if (progress == "0") {

                                    } else {
                                        Integer Progress = Integer.parseInt(progress) - 1;

                                        /*Insert into firebase-----------------------------------------------------------------------*/
                                        Progress_Info info = new Progress_Info(Course, Progress.toString());
                                        reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                            Toast.makeText(ProgressActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();

                                        }).addOnFailureListener(er -> {
                                            Toast.makeText(ProgressActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                                        /*-------------------------------------------------------------------------------------------*/

                                        attended.setText("Days attended: " + Progress);

                                    }

                                }
                            }
                        }).create().show();

            }
        });


        btnPlus.setOnClickListener(new View.OnClickListener() {

            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            DatabaseReference reference = rootNode.getReference("Users").child(ID);

            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ProgressActivity.this)
                        .setTitle("Update class Progress")
                        .setMessage("Press ok the increase days class has been attended by 1")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bundle = getIntent().getExtras();
                                String Programme = bundle.getString("Programme");
                                String Course = bundle.getString("Course");
                                String Level = bundle.getString("Level");
                                int indexOfSpace = Level.indexOf(":");
                                String Lev = Level.substring(indexOfSpace + 1);

                                String programme = Programme;
                                String course = Course;
                                String level = Lev.replaceAll(" ", "");
                                String data = programme + "/" + course + "-" + level;

                                // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
                                QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 1000);
                                qrgEncoder.setColorBlack(Color.BLACK);
                                qrgEncoder.setColorWhite(Color.WHITE);
                                if (Programme == null || Course == null || Level == null) {
                                    Snackbar.make(findViewById(android.R.id.content), "Unexpected error", Snackbar.LENGTH_LONG).show();
                                } else {
                                    try {
                                        // Getting QR-Code as Bitmap
                                        Bitmap qrBits = qrgEncoder.getBitmap();
                                        // Setting Bitmap to ImageView
                                        qrimage.setImageBitmap(qrBits);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                Cursor viewProgress = db.viewAllInProgress();

                                String progress = trick.getText().toString();

                                if (viewProgress.getCount() == 0) {
                                    boolean checkInsertData1 = db.insertDataIntoProgress(course, 1);
                                    if (checkInsertData1) {
                                        db.close();
                                        /*Insert into firebase-----------------------------------------------------------------------*/
                                        Progress_Info info = new Progress_Info(Course, "1");
                                        reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                            Toast.makeText(ProgressActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                        }).addOnFailureListener(er -> {
                                            Toast.makeText(ProgressActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                                        /*-------------------------------------------------------------------------------------------*/

                                        // Toast.makeText(ProgressActivity.this, "Good luck!", Toast.LENGTH_SHORT).show();
                                        attended.setText("Days attended: 1");
                                    }

                                } else {

                                    Integer Progress = Integer.parseInt(progress) + 1;
                                    /*Insert into firebase-----------------------------------------------------------------------*/
                                    Progress_Info info = new Progress_Info(Course, Progress.toString());
                                    reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                        Toast.makeText(ProgressActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(er -> {
                                        Toast.makeText(ProgressActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                    /*-------------------------------------------------------------------------------------------*/


                                    //  Toast.makeText(ProgressActivity.this, "Class Progress updated", Toast.LENGTH_SHORT).show();
                                    attended.setText("Days attended: " + Progress);
                                    // } else {
                                    //  Toast.makeText(ProgressActivity.this, "Error updating class", Toast.LENGTH_SHORT).show();
                                    // }
                                }
                            }
                        }).create().show();
            }
        });

    }

    private void initialWork() {
        db = new DBHelper(this);
        qrimage = findViewById(R.id.qrimage2);
        btnPlus = findViewById(R.id.plus);
        btnMinus = findViewById(R.id.minus);
        attended = findViewById(R.id.textView2);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}