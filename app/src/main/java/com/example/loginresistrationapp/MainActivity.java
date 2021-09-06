package com.example.loginresistrationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ornach.nobobutton.NoboButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView Sign_up;
    NoboButton Sign_in;
    FirebaseAuth auth;
    DatabaseReference users;
    FirebaseDatabase db;
    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init FireBase :
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("users");

        Sign_in = (NoboButton) findViewById(R.id.btn_sign_in);
        Sign_up = (TextView) findViewById(R.id.btn_register);
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);

        Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowRegisterDialog();
            }
        });

        Sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialEditText email = findViewById(R.id.email);
                MaterialEditText password = findViewById(R.id.password);
                if(TextUtils.isEmpty(email.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter email ! ", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter password ! ", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Intent intent = new Intent(MainActivity.this,Welcome.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void ShowRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.register_layout,null);

        MaterialEditText email = register_layout.findViewById(R.id.email);
        MaterialEditText password = register_layout.findViewById(R.id.password);
        MaterialEditText name = register_layout.findViewById(R.id.name);
        MaterialEditText phone = register_layout.findViewById(R.id.phone);

        dialog.setView(register_layout);

        // set Button :

        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                // check validation :

                if(TextUtils.isEmpty(email.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter email ! ", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString().length() < 6){
                    Snackbar.make(rootLayout, "Passwordk too short  ! ", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter password ! ", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(phone.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter phone number ! ", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(name.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter your name ! ", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // register new user :

                auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                               // save new user
                                Users user = new Users();
                                user.setEmail(email.getText().toString());
                                user.setPassword(password.getText().toString());
                                user.setPhone(phone.getText().toString());
                                user.setName(name.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)

                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                               Snackbar.make(rootLayout,"Register Success ! ", Snackbar.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(rootLayout,"Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout,"Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


dialog.show();

    }
}