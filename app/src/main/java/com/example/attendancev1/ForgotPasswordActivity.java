package com.example.attendancev1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText index, email;
    TextView Proceed;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        index = findViewById(R.id.txtIndex);
        email = findViewById(R.id.email);
        Proceed = findViewById(R.id.proceed);
        db = new DBHelper(this);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        Proceed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {

                if (index.getText().toString().isEmpty()) {

                    index.setError("Enter your Id");
                    index.requestFocus();
                } else if (email.getText().toString().isEmpty()) {
                    email.setError("Enter your Surname");
                    email.requestFocus();
                }else {

                    final ProgressDialog pd = new ProgressDialog(ForgotPasswordActivity.this);
                    pd.setMessage("Fetching data. Please wait...");
                    pd.show();

                    String Index = index.getText().toString();
                    String Email = email.getText().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    builder.setTitle("New Password");
                    builder.setMessage("Enter your new password");
                    final EditText input = new EditText(ForgotPasswordActivity.this);
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    input.setBackgroundColor(getResources().getColor(R.drawable.edittext_bg));
                    input.setPadding(50, 20, 50, 20);
                    input.setHint("Password");
                    builder.setView(input);
                    builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String m_Text = input.getText().toString();

                            Query query = reference.orderByChild("indexNumber").equalTo(index.getText().toString());

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot user : snapshot.getChildren()) {

                                            String id = user.child("indexNumber").getValue(String.class);
                                            String LastName = user.child("lastName").getValue(String.class);

                                            if (LastName.equals(Email) && id.equals(Index)) {


                                                    db.updatePasswordInStudent("Student",m_Text);

                                                    db.updatePasswordInTutor("Tutor", m_Text);


                                                reference.child(Index).child("password").setValue(m_Text).addOnSuccessListener(suc -> {

                                                    pd.dismiss();
                                                    Toast.makeText(ForgotPasswordActivity.this, "Password reset successful.", Toast.LENGTH_LONG).show();

                                                }).addOnFailureListener(er -> {

                                                    pd.dismiss();
                                                    Toast.makeText(ForgotPasswordActivity.this, "Password reset failed.", Toast.LENGTH_LONG).show();

                                                });
                                            }

                                        }
                                    } else {
                                        pd.dismiss();
                                        Snackbar.make(v, "User not found.", Snackbar.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });
    }
}