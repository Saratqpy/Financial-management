package com.example.financial_management;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
public class SignupActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText emailBox, passwordBox, nameBox ,familyBox;
    Button  signupBtn;
    FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        emailBox = findViewById(R.id.emailBox);
        nameBox = findViewById(R.id.nameBox);
        emailBox = findViewById(R.id.familyBox);
        passwordBox = findViewById(R.id.passwordBox);
        signupBtn = findViewById(R.id.createBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,pass,name,family;
                email = emailBox.getText().toString();
                pass = passwordBox.getText().toString();
                name = nameBox.getText().toString();
                family=familyBox.getText().toString();
                User user=new User();
                user.setEmail(email);
                user.setPass(pass);
                user.setName(name);
                user.setFamily(family);
                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            database.collection("Users")
                                    .document().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                                }
                            });
                            Toast.makeText(SignupActivity.this,"حساب کاربری با موفقیت ایجاد شد !",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignupActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}