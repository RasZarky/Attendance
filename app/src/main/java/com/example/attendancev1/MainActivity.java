package com.example.attendancev1;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {
    EditText txtCourse,txtDate;
    Spinner txtProg,txtlevel;
    Button btngen,btnClass;
    ImageView qrimage;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtProg = findViewById(R.id.txtProg);
        txtCourse = findViewById(R.id.txtCourse);
        txtDate = findViewById(R.id.txtDate);
        txtlevel = findViewById(R.id.txtlevel);
        btngen = findViewById(R.id.btngen);
        db = new DBHelper(this);

        String date = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        txtDate.setText(date);
        txtDate.setFocusable(false);

        generateCode();
        bottomNavigation();
    }

    private void bottomNavigation() {
        FloatingActionButton addBtn;
        LinearLayout profileBtn,connectBtn,classBtn, HomeButton;
        connectBtn = findViewById(R.id.connectBtn);
        addBtn = findViewById(R.id.addBtn);
        classBtn = findViewById(R.id.classBtn);
        profileBtn = findViewById(R.id.profileBtn);
        HomeButton = findViewById(R.id.homeBtn);

        Cursor stat = db.viewStatus();
        if (stat.getCount() == 0){

            HomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }
            });

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor viewAllInClass = db.viewAllInClass();

                        startActivity(new Intent(MainActivity.this, scannerActivity.class));
                        finish();
                }
            });

            connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(MainActivity.this)
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

            classBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ClassActivity.class));
                    finish();
                }
            });

        }else {

            HomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }
            });

            classBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ClassActivity.class));
                    finish();
                }
            });

            connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(MainActivity.this)
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

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, profile.class));
                finish();
            }
        });

    }


    public void generateCode(){

        btngen.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Create new class")
                    .setMessage("Press ok to create new class with details entered above")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean checkInsertData;
                            String Programme = String.valueOf(txtProg.getSelectedItem());
                            String Course = txtCourse.getText().toString().toUpperCase().replaceAll(" ", "");
                            String Level = String.valueOf(txtlevel.getSelectedItem());


                            if (Programme.equals("SELECT YOUR PROGRAMME")){
                                txtProg.requestFocus();
                                Snackbar.make(v, "All fields are required.", Snackbar.LENGTH_SHORT).show();
                            }else if (Course.isEmpty()){
                                txtCourse.requestFocus();
                                Snackbar.make(v, "All fields are required.", Snackbar.LENGTH_SHORT).show();
                            }else if(Level.equals("Select Level")){
                                txtlevel.requestFocus();
                                Snackbar.make(v, "All fields are required.", Snackbar.LENGTH_SHORT).show();
                            }else {
                                String data = Programme + " "
                                        + Course + "-"
                                        + Level;
                                String Final = null;
                                Cursor getClassData = db.viewAllInClass();

                                boolean Empty = Programme.isEmpty() || Course.isEmpty() || Level.isEmpty();

                                Cursor viewStat = db.viewStatus();
                                if (viewStat.getCount() == 0) {
                                    Cursor viewStudent = db.viewData1();
                                    StringBuilder buffer4 = new StringBuilder();

                                    while (viewStudent.moveToNext()) {
                                        buffer4.append(viewStudent.getString(4));
                                    }

                                    String ID = buffer4.toString();
                                    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                                    DatabaseReference reference2 = rootNode.getReference("Users").child(ID);
                                    DatabaseReference reference = rootNode.getReference("Class Details");

                                    if (getClassData.getCount() == 0) {

                                        if (Empty) {
                                            Snackbar.make(v, "All fields are required", Snackbar.LENGTH_LONG).show();
                                        } else {
                                            checkInsertData = db.insertDataIntoClass(Programme, Course, Level);
                                            if (checkInsertData) {

                                                /*Insert into firebase-----------------------------------------------------------------------*/
                                                Class_Info info = new Class_Info(Programme, Course, Level);
                                                reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                                    Toast.makeText(MainActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                                }).addOnFailureListener(er -> {
                                                    Toast.makeText(MainActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                                /*-------------------------------------------------------------------------------------------*/

                                                /*Insert into firebase-----------------------------------------------------------------------*/
                                                Progress_Info Pinfo = new Progress_Info(Course, "0");
                                                reference2.child(Course).setValue(Pinfo).addOnSuccessListener(suc -> {
                                                    Toast.makeText(MainActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                                }).addOnFailureListener(er -> {
                                                    Toast.makeText(MainActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                                /*-------------------------------------------------------------------------------------------*/
                                                Toast.makeText(MainActivity.this, "Class created successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(),
                                                        ClassActivity.class));

                                            } else {
                                                Toast.makeText(MainActivity.this, "Error creating class", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {

                                        Boolean isEqual = null;
                                        Boolean isEqual2 = null;
                                        if (Empty) {
                                            Snackbar.make(v, "All fields are required", Snackbar.LENGTH_LONG).show();
                                        } else {
                                            Cursor ClassData = db.viewAllInClass();
                                            int id = 1;
                                            while (id <= ClassData.getCount()) {
                                                String Id = Integer.toString(id);
                                                Cursor stat = db.viewProgrammeInClass(Id);
                                                StringBuilder buffer = new StringBuilder();
                                                StringBuilder buffer2 = new StringBuilder();
                                                StringBuilder buffer3 = new StringBuilder();

                                                while (stat.moveToNext()) {
                                                    buffer.append(stat.getString(0));
                                                }
                                                String class1 = buffer.toString();

                                                Cursor stat2 = db.viewCourseInClass(Id);
                                                while (stat2.moveToNext()) {
                                                    buffer2.append(stat2.getString(0));
                                                }
                                                String class2 = buffer2.toString();

                                                Cursor stat3 = db.viewLevelInClass(Id);
                                                while (stat3.moveToNext()) {
                                                    buffer3.append(stat3.getString(0));
                                                }
                                                String class3 = buffer3.toString();

                                                Final = class1 + " " +
                                                        class2 + "-" +
                                                        class3;

                                                isEqual = Course.equals(class2);
                                                isEqual2 = Programme.equals(class1);
                                                if (isEqual || isEqual2) {
                                                    startActivity(new Intent(getApplicationContext(),
                                                            ClassActivity.class));
                                                    Toast.makeText(MainActivity.this, "Class already exist", Toast.LENGTH_LONG).show();
                                                    break;
                                                }
                                                id++;

                                            }

                                            if (!isEqual && !isEqual2) {

                                                checkInsertData = db.insertDataIntoClass(Programme, Course, Level);
                                                if (checkInsertData) {

                                                    /*Insert into firebase-----------------------------------------------------------------------*/
                                                    Class_Info info = new Class_Info(Programme, Course, Level);
                                                    reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                                        Toast.makeText(MainActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                                    }).addOnFailureListener(er -> {
                                                        Toast.makeText(MainActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                                    /*-------------------------------------------------------------------------------------------*/

                                                    /*Insert into firebase-----------------------------------------------------------------------*/
                                                    Progress_Info Pinfo = new Progress_Info(Course, "0");
                                                    reference2.child(Course).setValue(Pinfo).addOnSuccessListener(suc -> {
                                                        Toast.makeText(MainActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                                    }).addOnFailureListener(er -> {
                                                        Toast.makeText(MainActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                                    /*-------------------------------------------------------------------------------------------*/

                                                    Toast.makeText(MainActivity.this, "Class created successfully", Toast.LENGTH_SHORT).show();
                                                }

                                                startActivity(new Intent(getApplicationContext(),
                                                        ClassActivity.class));

                                            }
                                        }
                                    }

                                } else {
                                    Cursor viewStudent = db.viewData2();
                                    StringBuilder buffer4 = new StringBuilder();

                                    while (viewStudent.moveToNext()) {
                                        buffer4.append(viewStudent.getString(4));
                                    }

                                    String ID = buffer4.toString();
                                    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                                    DatabaseReference reference2 = rootNode.getReference("Users").child(ID);

                                    DatabaseReference reference = rootNode.getReference("Class Details");

                                    if (getClassData.getCount() == 0) {

                                        if (Empty) {
                                            Snackbar.make(v, "All fields are required", Snackbar.LENGTH_LONG).show();
                                        } else {
                                            checkInsertData = db.insertDataIntoClass(Programme, Course, Level);
                                            if (checkInsertData) {

                                                /*Insert into firebase-----------------------------------------------------------------------*/
                                                Class_Info info = new Class_Info(Programme, Course, Level);
                                                reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                                    Toast.makeText(MainActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                                }).addOnFailureListener(er -> {
                                                    Toast.makeText(MainActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                                /*-------------------------------------------------------------------------------------------*/

                                                /*Insert into firebase-----------------------------------------------------------------------*/
                                                Progress_Info Pinfo = new Progress_Info(Course, "0");
                                                reference2.child(Course).setValue(Pinfo).addOnSuccessListener(suc -> {
                                                    Toast.makeText(MainActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                                }).addOnFailureListener(er -> {
                                                    Toast.makeText(MainActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                                /*-------------------------------------------------------------------------------------------*/
                                                Toast.makeText(MainActivity.this, "Class created successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(),
                                                        ClassActivity.class));

                                            } else {
                                                Toast.makeText(MainActivity.this, "Error creating class", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {

                                        Boolean isEqual = null;
                                        Boolean isEqual2 = null;
                                        if (Empty) {
                                            Snackbar.make(v, "All fields are required", Snackbar.LENGTH_LONG).show();
                                        } else {
                                            Cursor ClassData = db.viewAllInClass();
                                            int id = 1;
                                            while (id <= ClassData.getCount()) {
                                                String Id = Integer.toString(id);
                                                Cursor stat = db.viewProgrammeInClass(Id);
                                                StringBuilder buffer = new StringBuilder();
                                                StringBuilder buffer2 = new StringBuilder();
                                                StringBuilder buffer3 = new StringBuilder();

                                                while (stat.moveToNext()) {
                                                    buffer.append(stat.getString(0));
                                                }
                                                String class1 = buffer.toString();

                                                Cursor stat2 = db.viewCourseInClass(Id);
                                                while (stat2.moveToNext()) {
                                                    buffer2.append(stat2.getString(0));
                                                }
                                                String class2 = buffer2.toString();

                                                Cursor stat3 = db.viewLevelInClass(Id);
                                                while (stat3.moveToNext()) {
                                                    buffer3.append(stat3.getString(0));
                                                }
                                                String class3 = buffer3.toString();

                                                Final = class1 + " " +
                                                        class2 + "-" +
                                                        class3;

                                                isEqual = Course.equals(class2);

                                                if (isEqual) {
                                                    startActivity(new Intent(getApplicationContext(),
                                                            ClassActivity.class));
                                                    Toast.makeText(MainActivity.this, "Class already exist", Toast.LENGTH_LONG).show();
                                                    break;
                                                }
                                                id++;

                                            }

                                            if (!isEqual) {

                                                checkInsertData = db.insertDataIntoClass(Programme, Course, Level);
                                                if (checkInsertData) {

                                                    /*Insert into firebase-----------------------------------------------------------------------*/
                                                    Class_Info info = new Class_Info(Programme, Course, Level);
                                                    reference.child(Course).setValue(info).addOnSuccessListener(suc -> {
                                                        Toast.makeText(MainActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                                    }).addOnFailureListener(er -> {
                                                        Toast.makeText(MainActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                                    /*-------------------------------------------------------------------------------------------*/

                                                    /*Insert into firebase-----------------------------------------------------------------------*/
                                                    Progress_Info Pinfo = new Progress_Info(Course, "0");
                                                    reference2.child(Course).setValue(Pinfo).addOnSuccessListener(suc -> {
                                                        Toast.makeText(MainActivity.this, "Data Recorded", Toast.LENGTH_SHORT).show();
                                                    }).addOnFailureListener(er -> {
                                                        Toast.makeText(MainActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                                    /*-------------------------------------------------------------------------------------------*/

                                                    Toast.makeText(MainActivity.this, "Class created successfully", Toast.LENGTH_SHORT).show();
                                                }

                                                startActivity(new Intent(getApplicationContext(),
                                                        ClassActivity.class));

                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }).create().show();


        });

    }
}