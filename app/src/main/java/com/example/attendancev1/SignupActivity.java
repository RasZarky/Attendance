package com.example.attendancev1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {
    EditText Fname, Lname, Pass, Index, Email;
    RadioButton tutor, student;
    TextView signup;
    Spinner level, Programme;
    DBHelper db;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Fname = findViewById(R.id.txtFname);
        Lname = findViewById(R.id.txtLname);
        Pass = findViewById(R.id.txtPass);
        Index = findViewById(R.id.txtIndexNo);
        Programme = findViewById(R.id.txtProgramme);
        tutor = findViewById(R.id.radioButton2);
        student = findViewById(R.id.radioButton1);
        signup = findViewById(R.id.btnsign_up);
        level = findViewById(R.id.txtLevel);
        Email = findViewById(R.id.txtEmail);
        RadioGroup rbg = findViewById(R.id.radioGroup);
        db = new DBHelper(this);

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");

        rbg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton2) {
                Index.setHint("Enter your staff id");
                Programme.setEnabled(false);
                level.setEnabled(false);
            }
            if (checkedId == R.id.radioButton1) {
                Index.setHint("Enter your ID");
                Programme.setEnabled(true);
                level.setEnabled(true);
            }
        });

        signup.setOnClickListener(v -> {
            String FirstName = Fname.getText().toString();
            String LastName = Lname.getText().toString();
            String Password = Pass.getText().toString();
            String email = Email.getText().toString();
            String IndexNumber = Index.getText().toString();
            String Prog = String.valueOf(Programme.getSelectedItem());
            String Tutor = tutor.getText().toString();
            String Student = student.getText().toString();
            String Level = String.valueOf(level.getSelectedItem());

            final ProgressDialog pd = new ProgressDialog(SignupActivity.this);
            pd.setMessage("Please wait...");
            pd.show();

            Cursor res1 = db.viewData1();
            Cursor res2 = db.viewData2();

            Query query = reference.orderByChild("indexNumber").equalTo(IndexNumber);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()){

                        if (res1.getCount() == 0 && res2.getCount() == 0) {
                            if (student.isChecked()) {
                                if (FirstName.isEmpty() || LastName.isEmpty() || Password.isEmpty() || IndexNumber.isEmpty() || email.isEmpty() || Prog.isEmpty()) {
                                    pd.dismiss();
                                    Snackbar.make(v, "All enabled fields are required", Snackbar.LENGTH_LONG).show();
                                } else if (FirstName.isEmpty() && LastName.isEmpty() && Password.isEmpty() && IndexNumber.isEmpty() && email.isEmpty() && Prog.isEmpty()) {
                                    pd.dismiss();
                                    Snackbar.make(v, "Fill the form to proceed", Snackbar.LENGTH_LONG).show();
                                } else {

                                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                        Email.setError("Please enter a valid email address");
                                        Email.requestFocus();
                                        pd.dismiss();
                                    } else {

                                        boolean checkInsertData = db.insertData1(FirstName, LastName, Password, Student, IndexNumber, Prog, Level, "");
                                        if (checkInsertData) {

                                            Toast.makeText(SignupActivity.this, "Student account created successfully", Toast.LENGTH_SHORT).show();

                                            /*Insert into firebase-----------------------------------------------------------------------*/
                                            personal_Info info = new personal_Info(FirstName, LastName, Password, IndexNumber, Prog, Student, Level);
                                            reference.child(IndexNumber).setValue(info).addOnSuccessListener(suc -> {
                                                pd.dismiss();

                                                startActivity(new Intent(getApplicationContext(),
                                                        loginActivity.class));

                                                Toast.makeText(getApplicationContext(), "Data Recorded", Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(er -> {
                                                Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                            /*-------------------------------------------------------------------------------------------*/

                                        } else {
                                            Snackbar.make(v, "Error saving data", Snackbar.LENGTH_LONG).show();
                                        }


                                    }

                                }
                            }

                            if (tutor.isChecked()) {
                                if (FirstName.isEmpty() || LastName.isEmpty() || Password.isEmpty() || IndexNumber.isEmpty() || Prog.isEmpty()) {
                                    pd.dismiss();
                                    Snackbar.make(v, "All enabled fields are required", Snackbar.LENGTH_LONG).show();
                                } else if (FirstName.isEmpty() && LastName.isEmpty() && Password.isEmpty() && IndexNumber.isEmpty() && Prog.isEmpty()) {
                                    pd.dismiss();
                                    Snackbar.make(v, "Fill the form to proceed", Snackbar.LENGTH_LONG).show();
                                } else {

                                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                        Email.setError("Please enter a valid email address");
                                        Email.requestFocus();
                                        pd.dismiss();
                                    } else {

                                        boolean checkInsertData = db.insertData2(FirstName, LastName, Password, Tutor, IndexNumber, "");
                                        if (checkInsertData) {

                                            Toast.makeText(SignupActivity.this, "Tutor account created successfully", Toast.LENGTH_SHORT).show();
                                            /* Insert into firebase---------------------------------------------------------------------------------------------*/
                                            personal_Info info = new personal_Info(FirstName, LastName, Password, IndexNumber, null, Tutor, null);
                                            reference.child(IndexNumber).setValue(info).addOnSuccessListener(suc -> {
                                                pd.dismiss();

                                                startActivity(new Intent(getApplicationContext(),
                                                        loginActivity.class));

                                                Toast.makeText(getApplicationContext(), "Data Recorded", Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(er -> {
                                                Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                            /*-----------------------------------------------------------------------------------------------------------------*/

                                        } else {
                                            pd.dismiss();
                                            Snackbar.make(v, "Error saving data", Snackbar.LENGTH_LONG).show();
                                        }

                                    }

                                }
                            }

                        } else {
                            pd.dismiss();
                            Toast.makeText(SignupActivity.this, "This device already has an account.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),
                                    loginActivity.class));
                        }

                    } else {
                        Snackbar.make(v, "This account already already exist", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }
}