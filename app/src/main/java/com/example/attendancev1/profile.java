package com.example.attendancev1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class profile extends AppCompatActivity {

    int REQUEST_PERMISSION_CODE = 99;
    int RESULT_LOAD_IMAGE = 1;
    ImageView Image;
    RelativeLayout Logout,index,lev,Prog;
    ImageView pick_pic;
    TextView Name,Status,Id,Programme,Level;
    DBHelper db;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Cursor Stat = db.viewStatus();
        String status;
        if (Stat.getCount()==0){
            status = "Student";
        }else{
            status = "Tutor";
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(selectedImage,filePathColumn,null,null,null);
            if (cursor != null){
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                Image = findViewById(R.id.image);
                Image.setImageURI(selectedImage);
                String uri = selectedImage.toString();


               if (status.equals("Student")){
                   Cursor viewStudent = db.viewData1();
                   StringBuilder getpic = new StringBuilder();
                   while (viewStudent.moveToNext()){
                       getpic.append(viewStudent.getString(7));
                   }
                   String IMG = getpic.toString();

                   if (IMG == null){
                       db.insertPicIntoStudent(uri);
                   }else {
                       db.updatePicInStudent("Student", uri);
                   }
               }else {
                   Cursor viewTutor = db.viewData2();
                   StringBuilder getpic = new StringBuilder();
                   while (viewTutor.moveToNext()){
                       getpic.append(viewTutor.getString(5));
                   }
                   String IMG = getpic.toString();

                   if (IMG == null){
                       db.insertPicIntoTutor(uri);
                   }else {
                       db.updatePicInTutor("Tutor", uri);
                   }
               }

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Cursor viewStat = db.viewStatus();
                    if (viewStat.getCount() == 0){
                        Cursor viewStudent = db.viewData1();
                        StringBuilder buffer5 = new StringBuilder();
                        while(viewStudent.moveToNext()){
                            buffer5.append(viewStudent.getString(7));
                        }
                        String pic = buffer5.toString();
                        setPic(pic);
                    }else{
                        Cursor viewStudent = db.viewData2();
                        StringBuilder buffer5 = new StringBuilder();
                        while(viewStudent.moveToNext()){
                            buffer5.append(viewStudent.getString(5));
                        }
                        String pic = buffer5.toString();
                        setPic(pic);
                    }

                }
            }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Logout = findViewById(R.id.logout);
        Image = findViewById(R.id.image);
        pick_pic = findViewById(R.id.pick_pic);
        Name = findViewById(R.id.name);
        Status = findViewById(R.id.status);
        Id = findViewById(R.id.ID);
        Programme = findViewById(R.id.programme);
        Level = findViewById(R.id.level);
        index = findViewById(R.id.index);
        lev = findViewById(R.id.lev);
        Prog = findViewById(R.id.Prog);
        db = new DBHelper(this);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(1500);

        Image.startAnimation(alphaAnimation);
        pick_pic.startAnimation(alphaAnimation);

        Logout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up));
        index.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up));
        Prog.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up));
        lev.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up));

        Cursor viewStat = db.viewStatus();
        if (viewStat.getCount() == 0){
            Status.setText("Student");

            Cursor viewStudent = db.viewData1();
            StringBuilder buffer1 = new StringBuilder();
            StringBuilder buffer2 = new StringBuilder();
            StringBuilder buffer4 = new StringBuilder();
            StringBuilder buffer5 = new StringBuilder();
            StringBuilder buffer6 = new StringBuilder();
            StringBuilder buffer7 = new StringBuilder();

            while(viewStudent.moveToNext()){
                buffer1.append(viewStudent.getString(0));
                buffer2.append(viewStudent.getString(1));
                buffer4.append(viewStudent.getString(4));
                buffer5.append(viewStudent.getString(5));
                buffer6.append(viewStudent.getString(6));
                buffer7.append(viewStudent.getString(7));
            }

            String Fname = buffer1.toString();
            String Sname = buffer2.toString();
            String fullName = Fname+" "+Sname;
            Name.setText(fullName);

            String Index = buffer4.toString();
            Id.setText(Index);

            String programme = buffer5.toString();
            Programme.setText(programme);

            String level = buffer6.toString();
            Level.setText(level);

            String pic = buffer7.toString();
            if (pic == ""){
                Image.setImageResource(R.drawable.ic_baseline_person_24);
            }else{
                Uri Pic = Uri.parse(pic);
                Image.setImageURI(Pic);
            }
        }else{
            Status.setText("Tutor");

            Cursor viewStudent = db.viewData2();
            StringBuilder buffer1 = new StringBuilder();
            StringBuilder buffer2 = new StringBuilder();
            StringBuilder buffer4 = new StringBuilder();
            StringBuilder buffer5 = new StringBuilder();

            while(viewStudent.moveToNext()){
                buffer1.append(viewStudent.getString(0));
                buffer2.append(viewStudent.getString(1));
                buffer4.append(viewStudent.getString(4));
                buffer5.append(viewStudent.getString(5));
            }

            String Fname = buffer1.toString();
            String Sname = buffer2.toString();
            String fullName = Fname+" "+Sname;
            Name.setText(fullName);

            String Index = buffer4.toString();
            Id.setText(Index);

            Programme.setText("Not Applicable");

            Level.setText("Not Applicable");

            String pic = buffer5.toString();
            if (pic == ""){
                Image.setImageResource(R.drawable.ic_baseline_person_24_2);
            }else {
                if (ActivityCompat.checkSelfPermission(profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(profile.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
                    }
                }else{
                    Uri Pic = Uri.parse(pic);
                    Image.setImageURI(Pic);
                }


            }
        }

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateLog(0);
                Intent logout = new Intent(getApplicationContext(), loginActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logout);
               /* if (ActivityCompat.checkSelfPermission(profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(profile.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        startActivity(logout);
                    }else {
                        ActivityCompat.requestPermissions(profile.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION_CODE);
                    }
                }*/
            }
        });

        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Pick_pic = new Intent();
                Pick_pic.setType("image/*");
                Pick_pic.setAction(Intent.ACTION_OPEN_DOCUMENT);
                Pick_pic.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                Pick_pic.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                if (ActivityCompat.checkSelfPermission(profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(profile.this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                    }else {
                        ActivityCompat.requestPermissions(profile.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION_CODE);
                    }
                }
                startActivityForResult(Pick_pic.createChooser(Pick_pic,"Select Picture"),RESULT_LOAD_IMAGE);
            }
        });

        bottomNavigation();
    }

    private void setPic(String pic) {
        Uri Pic = Uri.parse(pic);
        Image.setImageURI(Pic);
    }

    private void bottomNavigation() {
        FloatingActionButton addBtn;
        LinearLayout profileBtn,connectBtn,classBtn, HomeButton;
        profileBtn = findViewById(R.id.profileBtn);
        connectBtn = findViewById(R.id.connectBtn);
        addBtn = findViewById(R.id.addBtn);
        classBtn = findViewById(R.id.classBtn);
        HomeButton = findViewById(R.id.homeBtn);

        Cursor stat = db.viewStatus();
        if (stat.getCount() == 0){

            HomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(profile.this, HomeActivity.class));
                    finish();
                }
            });

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        startActivity(new Intent(profile.this, scannerActivity.class));
                        finish();
                }
            });

            connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(profile.this)
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
                    startActivity(new Intent(profile.this, ClassActivity.class));
                    finish();
                }
            });
        }else {

            HomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(profile.this, HomeActivity.class));
                    finish();
                }
            });

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(profile.this, MainActivity.class));
                    finish();
                }
            });

            connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(profile.this)
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
                    startActivity(new Intent(profile.this, ClassActivity.class));
                    finish();
                }
            });
        }

    }

}