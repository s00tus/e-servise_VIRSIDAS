package com.tusdwi.virsidas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registrasi extends AppCompatActivity {
    EditText fullName,email,password,phone;
    Button registerBtn;
    TextView loginBtn;
    boolean valid = true;
    CheckBox Guru, Siswa;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        phone = findViewById(R.id.registerPhone);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.login);
        Guru = findViewById(R.id.isTeacher);
        Siswa = findViewById(R.id.isStudent);

        Siswa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    Guru.setChecked(false);
                }
            }
        });

        Guru.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    Siswa.setChecked(false);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(fullName);
                checkField(email);
                checkField(password);
                checkField(phone);

                //checkbox
                if (!(Guru.isChecked() || Siswa.isChecked())){
                    Toast.makeText(Registrasi.this, "Pilih jenis akun", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (valid) {
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Registrasi.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("NamaLengkap", fullName.getText().toString());
                            userInfo.put("Email", email.getText().toString());
                            userInfo.put("Password", password.getText().toString());
                            userInfo.put("NoHandphone", phone.getText().toString());

                            //user
                            if(Guru.isChecked()){
                                userInfo.put("Teacher", "1");
                            }

                            if(Siswa.isChecked()){
                                userInfo.put("Student", "1");
                            }

                            df.set(userInfo);
                            if(Guru.isChecked()){
                                startActivity(new Intent(getApplicationContext(), Teacher.class));
                                finish();
                            }
                            if(Siswa.isChecked()){
                                startActivity(new Intent(getApplicationContext(), Students.class));
                                finish();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Registrasi.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


            }

            public boolean checkField(EditText textField) {
                if (textField.getText().toString().isEmpty()) {
                    textField.setError("Error");
                    valid = false;
                } else {
                    valid = true;
                }

                return valid;
            }

    public void loginBtn(View view) {
        Intent data = new Intent(Registrasi.this, Login.class);
        startActivity(data);

    }
}