package com.example.attendancev1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {

    DBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

       TextView btnLog = findViewById(R.id.button);
       TextView btnSign = findViewById(R.id.button2);
       db = new DBHelper(this);

        Cursor viewLogin = db.viewLoggedIn();
        StringBuilder buffer = new StringBuilder();
        if (viewLogin.getCount() == 0) {
            db.insertIntoLog(0);
        }else {
            while (viewLogin.moveToNext()) {
                buffer.append(viewLogin.getInt(0));
            }
            String log = buffer.toString();
            viewLogin.close();
            db.close();
            int LOG = Integer.parseInt(log);

            if (LOG != 0) {

                startActivity(new Intent(IntroActivity.this, HomeActivity.class));
                finish();

            }
        }

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        SignupActivity.class));

            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(),
                           loginActivity.class));
           }
       });
    }
}