package com.example.attendancev1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class loginActivity extends AppCompatActivity {
    EditText index, password;
    TextView login, forgotPassword;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        index = findViewById(R.id.txtIndex);
        password = findViewById(R.id.txtPassword);
        login = findViewById(R.id.btnLogin);
        forgotPassword = findViewById(R.id.forgotPassword);
        db = new DBHelper(this);


        Cursor viewLogin = db.viewLoggedIn();
        StringBuilder buffer = new StringBuilder();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        while (viewLogin.moveToNext()) {
            buffer.append(viewLogin.getInt(0));
        }
        String log = buffer.toString();
        viewLogin.close();
        db.close();
        int LOG = Integer.parseInt(log);

        if (LOG != 0) {

            startActivity(new Intent(loginActivity.this, HomeActivity.class));
            finish();

        } else {

            login.setOnClickListener(v -> {
                String INDEX = index.getText().toString();
                String PASSWORD = password.getText().toString();

                if (INDEX.isEmpty() || PASSWORD.isEmpty()) {
                    Snackbar.make(v, "All fields are required", Snackbar.LENGTH_LONG).show();
                } else if (INDEX.isEmpty() && PASSWORD.isEmpty()){
                    Snackbar.make(v, "Enter Id and password", Snackbar.LENGTH_LONG).show();
                }else {
                    int x = db.Login1(INDEX, PASSWORD);
                    int y = db.Login2(INDEX, PASSWORD);
                    if (x == 1 || y == 1) {
                        db.updateLog(1);

                        startActivity(new Intent(loginActivity.this, HomeActivity.class));
                        finish();

                    } else if(x != 1 && y != 1){

                        Cursor res1 = db.viewData1();
                        Cursor res2 = db.viewData2();

                        if (res1.getCount() == 0 && res2.getCount() == 0) {

                            final ProgressDialog pd = new ProgressDialog(loginActivity.this);
                            pd.setMessage("Fetching data. Please wait...");
                            pd.show();

                            Query query = reference.orderByChild("indexNumber").equalTo(index.getText().toString());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot user : snapshot.getChildren()){

                                            Cursor res1 = db.viewData1();
                                            Cursor res2 = db.viewData2();

                                            String id = user.child("indexNumber").getValue(String.class);
                                            String FirstName = user.child("firstName").getValue(String.class);
                                            String LastName = user.child("lastName").getValue(String.class);
                                            String Password = user.child("password").getValue(String.class);
                                            String level = user.child("level").getValue(String.class);
                                            String Programme = user.child("programme").getValue(String.class);
                                            String Position = user.child("position").getValue(String.class);

                                            if (Password.equals(PASSWORD) && id.equals(INDEX)){
                                                if (Position.equals("Tutor")){
                                                    if (res1.getCount()==0 && res2.getCount()==0) {
                                                        boolean checkInsertData = db.insertData2(FirstName, LastName, Password, Position, id,"");
                                                        if (checkInsertData) {
                                                            db.updateLog(1);

                                                            pd.dismiss();
                                                            startActivity(new Intent(loginActivity.this, HomeActivity.class));
                                                            finish();
                                                        }
                                                    }else {
                                                        pd.dismiss();
                                                        Snackbar.make(v, "This device already has an account login with with a different device.", Snackbar.LENGTH_LONG).show();
                                                    }
                                                }else {
                                                    if (res1.getCount()==0 && res2.getCount()==0) {
                                                        boolean checkInsertData = db.insertData1(FirstName, LastName, Password, Position, id,Programme, level,"");
                                                        if (checkInsertData) {
                                                            db.updateLog(1);

                                                            pd.dismiss();
                                                            startActivity(new Intent(loginActivity.this, HomeActivity.class));
                                                            finish();
                                                        }
                                                    }else {
                                                        pd.dismiss();
                                                        Snackbar.make(v, "This device already has an account login with with a different device.", Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            }

                                        }
                                    }else {
                                        pd.dismiss();
                                        Snackbar.make(v, "User not found.", Snackbar.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {

                            Toast.makeText(loginActivity.this, "The account on the device does not match the details entered.", Toast.LENGTH_LONG).show();

                        }

                    }
                }
            });

        }

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }
}

