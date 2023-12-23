package com.example.attendancev1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class scannerActivity extends AppCompatActivity {
    CodeScanner mCodeScanner;
    CodeScannerView scannerView;
    TextView trick;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        initialWork();
        exqListener();
        bottomNavigation();

    }

    private void bottomNavigation() {
        FloatingActionButton addBtn;
        LinearLayout profileBtn, connectBtn, classBtn, HomeButton;
        profileBtn = findViewById(R.id.profileBtn);
        connectBtn = findViewById(R.id.connectBtn);
        addBtn = findViewById(R.id.addBtn);
        classBtn = findViewById(R.id.classBtn);
        HomeButton = findViewById(R.id.homeBtn);

        Cursor stat = db.viewStatus();
        if (stat.getCount() == 0) {

            HomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(scannerActivity.this, HomeActivity.class));
                    finish();
                }
            });

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        startActivity(new Intent(scannerActivity.this, scannerActivity.class));
                        finish();

                }
            });

            connectBtn.setOnClickListener(v -> {
                new AlertDialog.Builder(scannerActivity.this)
                        .setTitle("Turn on Mobile data")
                        .setMessage("All features of the app work without internet but some features require internet for the app to reflect changes \n\n Turn on mobile data?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                                startActivity(intent);
                            }
                        }).create().show();

            });

            classBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(scannerActivity.this, ClassActivity.class));
                    finish();
                }
            });

        } else {

            HomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(scannerActivity.this, HomeActivity.class));
                    finish();
                }
            });

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(scannerActivity.this, MainActivity.class));
                    finish();
                }
            });

            connectBtn.setOnClickListener(v -> {
                new AlertDialog.Builder(scannerActivity.this)
                        .setTitle("Turn on Mobile data")
                        .setMessage("All features of the app work without internet but some features require internet for the app to reflect changes \n\n Turn on mobile data?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                                startActivity(intent);
                            }
                        }).create().show();

            });

            classBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(scannerActivity.this, ClassActivity.class));
                    finish();
                }
            });

        }


        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(scannerActivity.this, profile.class));
                finish();
            }
        });
    }


    private void exqListener() {

        final Cursor viewTutorIndex = db.viewIndexInStudent();
        StringBuilder builder = new StringBuilder();
        while (viewTutorIndex.moveToNext()) {
            builder.append(viewTutorIndex.getString(0));
        }
        String ID = builder.toString();

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            final FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            final DatabaseReference reference = rootNode.getReference("Users").child(ID);

            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(() -> {
                    Cursor viewProgress = db.viewAllInProgress();
                    Cursor viewProgrammeInStudent = db.viewProgrammeInStudent();
                    Cursor viewLevelInStudent = db.viewLevelInStudent();

                    StringBuilder buffer3 = new StringBuilder();
                    while (viewProgrammeInStudent.moveToNext()) {
                        buffer3.append(viewProgrammeInStudent.getString(0));
                    }
                    String StudentProgramme = buffer3.toString();

                    StringBuilder buffer4 = new StringBuilder();
                    while (viewLevelInStudent.moveToNext()) {
                        buffer4.append(viewLevelInStudent.getString(0));
                    }
                    String StudentLevel = buffer4.toString();

                    String Result = result.getText();
                    int IndexOfSpace = Result.indexOf("/");
                    int IndexOfDash = Result.indexOf("-");

                    if (IndexOfDash == -1 || IndexOfSpace == -1) {
                        Toast.makeText(scannerActivity.this, "The qr code scanned does not match the format for class attendance", Toast.LENGTH_LONG).show();
                    } else {
                        String Programme = Result.substring(0, IndexOfSpace);
                        String Course = Result.substring(IndexOfSpace + 1, IndexOfDash);
                        String Level = Result.substring(IndexOfDash + 1).replaceAll(" ", "");
                        Boolean isEqual = null;

                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Users");

                        reference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String User = dataSnapshot.child("indexNumber").getValue(String.class);
                                    String finalCourse = dataSnapshot.child(Course).child("course").getValue(String.class);
                                    String Progress = dataSnapshot.child(Course).child("progress").getValue(String.class);

                                    if (Course.equals(finalCourse)) {
                                        String TutorProgress = Progress;
                                        trick.setText(TutorProgress);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if (StudentProgramme.equals(Programme) && StudentLevel.equals(Level)) {

                            if (viewProgress.getCount() == 0) {

                                boolean checkInsertedDataIntoClass = db.insertDataIntoClass(Programme, Course, Level);
                                if (checkInsertedDataIntoClass) {

                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                        v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                                    }else {
                                        v.vibrate(500);
                                    }

                                    Toast.makeText(scannerActivity.this, "Class enrolled successfully", Toast.LENGTH_LONG).show();

                                    boolean checkInsertData1 = db.insertDataIntoProgress(Course, 1);
                                    if (checkInsertData1) {
                                        db.close();
                                        /*Insert into firebase-----------------------------------------------------------------------*/
                                        Progress_Info info = new Progress_Info(Course, "1");
                                        reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                            Toast.makeText(scannerActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                        }).addOnFailureListener(er -> {
                                            Toast.makeText(scannerActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                                        /*-------------------------------------------------------------------------------------------*/

                                        // Toast.makeText(scannerActivity.this, "Good luck", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(getApplicationContext(),
                                                ClassActivity.class));
                                    }
                                }
                            } else {

                                Cursor getData = db.viewAllInClass();
                                int id = 1;
                                while (id <= getData.getCount()) {
                                    String Id = Integer.toString(id);
                                    Cursor getCourse = db.viewCourseInClass(Id);
                                    StringBuilder buffer = new StringBuilder();

                                    while (getCourse.moveToNext()) {
                                        buffer.append(getCourse.getString(0));
                                    }
                                    String course = buffer.toString();

                                    isEqual = Course.equals(course);
                                    if (isEqual) {

                                        try {
                                            Bundle bundle = getIntent().getExtras();
                                            String progressFromActivity = bundle.getString("Progress");

                                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                                v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                                            }else {
                                                v.vibrate(500);
                                            }

                                            Integer Progress = Integer.parseInt(progressFromActivity) + 1;
                                            /*Insert into firebase-----------------------------------------------------------------------*/
                                            Progress_Info info = new Progress_Info(Course, Progress.toString());
                                            reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                                Toast.makeText(scannerActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(er -> {
                                                Toast.makeText(scannerActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                            /*-------------------------------------------------------------------------------------------*/

                                            Toast.makeText(scannerActivity.this, "Class Progress updated", Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(getApplicationContext(),
                                                    ClassActivity.class));

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            startActivity(new Intent(scannerActivity.this, ClassActivity.class));
                                            Toast.makeText(scannerActivity.this, "Select The Class You Want Update", Toast.LENGTH_LONG).show();
                                            finish();

                                        }


                                    }
                                    id++;
                                }

                                if (isEqual) {

                                } else {
                                    boolean checkInsertedDataIntoClass = db.insertDataIntoClass(Programme, Course, Level);
                                    if (checkInsertedDataIntoClass) {
                                        Toast.makeText(scannerActivity.this, "Class enrolled successfully", Toast.LENGTH_LONG).show();

                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                                        }else {
                                            v.vibrate(500);
                                        }

                                        boolean checkInsertData1 = db.insertDataIntoProgress(Course, 1);
                                        if (checkInsertData1) {
                                            db.close();
                                            /*Insert into firebase-----------------------------------------------------------------------*/
                                            Progress_Info info = new Progress_Info(Course, "1");
                                            reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                                Toast.makeText(scannerActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(er -> {
                                                Toast.makeText(scannerActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                            /*-------------------------------------------------------------------------------------------*/

                                            // Toast.makeText(scannerActivity.this, "Good luck", Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(getApplicationContext(),
                                                    ClassActivity.class));
                                        }
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(scannerActivity.this, "Error, programme or level scanned does not match student details.", Toast.LENGTH_LONG).show();
                        }
                    }

                });
            }
        });

        scannerView.setOnClickListener(v -> mCodeScanner.startPreview());

    }

    private void initialWork() {
        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        trick = findViewById(R.id.trick);
        db = new DBHelper(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void requestForCamera() {
        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                mCodeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(scannerActivity.this, "The Application needs permission to use camera in order to scan.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public void updateClassProgress(String Course) {

        final Cursor viewTutorIndex = db.viewIndexInStudent();
        StringBuilder builder = new StringBuilder();
        while (viewTutorIndex.moveToNext()) {
            builder.append(viewTutorIndex.getString(0));
        }
        String ID = builder.toString();

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Users");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String User = dataSnapshot.child("indexNumber").getValue(String.class);
                    String finalCourse = dataSnapshot.child(Course).child("course").getValue(String.class);
                    String Progress = dataSnapshot.child(Course).child("progress").getValue(String.class);

                    if (Course.equals(finalCourse)) {
                        String TutorProgress = Progress;
                        trick.setText(TutorProgress);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");

       /* Cursor viewProgress = db.viewProgressInProgress(Course);
        StringBuilder buffer1 = new StringBuilder();
        while (viewProgress.moveToNext()) {
            buffer1.append(viewProgress.getString(0));
        }
        String c = buffer1.toString();
        int progress = Integer.parseInt(c);
        Integer Progress = progress + 1;

        boolean ProgressUpdate = db.updateDataInProgress(Course, Progress);
        if (ProgressUpdate) {*/

        String progress = trick.getText().toString();
        Integer Progress = Integer.parseInt(progress) + 1;
        /*Insert into firebase-----------------------------------------------------------------------*/
        Progress_Info info = new Progress_Info(Course, Progress.toString());
        reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
            Toast.makeText(scannerActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(er -> {
            Toast.makeText(scannerActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
        });
        /*-------------------------------------------------------------------------------------------*/

        Toast.makeText(scannerActivity.this, "Class Progress updated", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getApplicationContext(),
                ClassActivity.class));
       /* } else {
            Toast.makeText(scannerActivity.this, "Error updating class", Toast.LENGTH_SHORT).show();
        }*/

    }

}