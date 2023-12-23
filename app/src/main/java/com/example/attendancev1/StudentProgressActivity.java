package com.example.attendancev1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentProgressActivity extends AppCompatActivity {
     TextView CourseCode, TotalPercentage, TotalDays, DaysAttended, DaysMissed, trick;
     ProgressBar progressBar1,progressBar2,progressBar3,progressBar4;
     DBHelper db;
     CircleImageView updateClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_progress);

        CourseCode = findViewById(R.id.Course_Code);
        TotalPercentage = findViewById(R.id.Total_Percentage);
        TotalDays = findViewById(R.id.Total_Days1);
        DaysAttended = findViewById(R.id.Days_Attended);
        DaysMissed = findViewById(R.id.Days_Missed);
        progressBar1 = findViewById(R.id.progress_circular1);
        progressBar2 = findViewById(R.id.progress_circular2);
        progressBar3 = findViewById(R.id.progress_circular3);
        progressBar4 = findViewById(R.id.progress_circular4);
        updateClass = findViewById(R.id.updateClass);
        trick = findViewById(R.id.trick);
        db = new DBHelper(this);

        Bundle bundle = getIntent().getExtras();
        String Course = bundle.getString("Course");
        String course = Course;

        final Cursor viewStudentIndex = db.viewIndexInStudent();
        StringBuilder builder5 = new StringBuilder();
        while (viewStudentIndex.moveToNext()) {
            builder5.append(viewStudentIndex.getString(0));
        }
        String Id = builder5.toString();

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Users");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My notification id","My notification id", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String User = dataSnapshot.child("indexNumber").getValue(String.class);
                    String finalCourse = dataSnapshot.child(course).child("course").getValue(String.class);
                    String Progress = dataSnapshot.child(course).child("progress").getValue(String.class);

                    if (course.equals(finalCourse) && User.equals(Id)) {
                        String StudentProgress = Progress;
                        trick.setText(StudentProgress);
                        DaysAttended.setText(trick.getText().toString());

                        updateClass.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), scannerActivity.class);
                                intent.putExtra("Progress", StudentProgress);
                                startActivity(intent);
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        CourseCode.setText(course);

        final Cursor viewTutorIndex = db.viewIndexInTutor();
        StringBuilder builder = new StringBuilder();
        while (viewTutorIndex.moveToNext()) {
            builder.append(viewTutorIndex.getString(0));
        }
        String ID = builder.toString();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String User = dataSnapshot.child("indexNumber").getValue(String.class);
                    String finalCourse = dataSnapshot.child(course).child("course").getValue(String.class);

                    String Progress = dataSnapshot.child(course).child("progress").getValue(String.class);

                    String Position = dataSnapshot.child("position").getValue(String.class);

                    if (course.equals(finalCourse) && !User.equals(ID) && Position.equals("Tutor")) {
                        progressBar1.setVisibility(View.GONE);
                        progressBar2.setVisibility(View.GONE);
                        progressBar3.setVisibility(View.GONE);
                        progressBar4.setVisibility(View.GONE);

                        TotalDays.setText(Progress);

                        Integer missed = Integer.parseInt(Progress) - Integer.parseInt(trick.getText().toString());
                        DaysMissed.setText(missed.toString());

                        Double percent = (Double.parseDouble(trick.getText().toString())/Double.parseDouble(Progress))*100;
                        TotalPercentage.setText((percent.toString())+"%");

                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                /*-------NOTIFICATION-------------------------------------------------------------------------------------------------------*/
                NotificationCompat.Builder notification = new NotificationCompat.Builder(StudentProgressActivity.this, "My notification id")
                        .setContentTitle("Progress updated")
                        .setContentText(course+"`s progress has been updated from \n"+previousChildName+" \nCheck it out...")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true);

                Intent intent = new Intent(StudentProgressActivity.this,StudentProgressActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Course",course);

                PendingIntent pendingIntent = PendingIntent.getActivity(StudentProgressActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                notification.setContentIntent(pendingIntent);
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(StudentProgressActivity.this);
                managerCompat.notify(1,notification.build());
                /*-------------------------------------------------------------------------------------------------------------------------------*/

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
