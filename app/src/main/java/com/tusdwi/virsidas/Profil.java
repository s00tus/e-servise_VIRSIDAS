package com.tusdwi.virsidas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;


public class Profil extends AppCompatActivity {
    TextView NamaLengkap, NoIndukPegawai, email, alamat, tanggalLahir, noHandphone;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    String userId;
    ImageView photoProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        NamaLengkap = findViewById(R.id.profilNama);
        NoIndukPegawai = findViewById(R.id.noIndukP);
        email = findViewById(R.id.profilEmail);
        tanggalLahir = findViewById(R.id.tanggalLahir);
        alamat = findViewById(R.id.Alamat);
        noHandphone = findViewById(R.id.noHandpone);
        photoProfil = findViewById(R.id.PhotoProfil);


        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        StorageReference profileRef = storageReference.child("Users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(photoProfil);
            }
        });

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    NamaLengkap.setText(documentSnapshot.getString("NamaLengkap"));
                    NoIndukPegawai.setText(documentSnapshot.getString("NIP"));
                    email.setText(documentSnapshot.getString("Email"));
                    tanggalLahir.setText(documentSnapshot.getString("TanggalLahir"));
                    noHandphone.setText(documentSnapshot.getString("NoHandphone"));
                } else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dawer_setting, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.pengaturan){
            //Edit Profil
            Intent intent = new Intent(Profil.this, UpdateProfil.class);
            intent.putExtra("NamaLengkap", NamaLengkap.getText().toString());
            intent.putExtra("NIP", NoIndukPegawai.getText().toString());
            intent.putExtra("Email", email.getText().toString());
            intent.putExtra("TanggalLahir", tanggalLahir.getText().toString());
            intent.putExtra("Alamat", alamat.getText().toString());
            intent.putExtra("NoHandphone", noHandphone.getText().toString());
            startActivity(intent);


        } else if (item.getItemId() == R.id.gantipassword) {
            //Ganti Password
        }

        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}