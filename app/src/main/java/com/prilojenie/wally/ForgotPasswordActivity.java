package com.prilojenie.wally;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmailReset;
    Button btnReset;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;

    String emailValidationPattern = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        etEmailReset = findViewById(R.id.etEmailReset);
        btnReset = findViewById(R.id.btnReset);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    void resetPassword()
    {
        String email = etEmailReset.getText().toString();

        if(email.isEmpty() || !email.matches(emailValidationPattern))
        {
            etEmailReset.setError("Enter An Email!");
            etEmailReset.setText("");
        }
        else
        {
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Sending Email...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Email Sent", Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                        sendToLogin();
                    }
                    else
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Something Went Wrong. Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    void sendToLogin()
    {
        Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }

}