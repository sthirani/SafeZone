package com.slohackathon.safezone;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private static final String TAG="LoginActivity";
    private EditText email;
    private EditText password;
    private Button login;
    private Button create;
    private Button signout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        email=(EditText)findViewById(R.id.Username_id);
        password=(EditText)findViewById(R.id.Password_id);
        login=(Button)findViewById(R.id.Loginbutton_id);
        signout=(Button)findViewById(R.id.signout_id);
        create=(Button)findViewById(R.id.Createbutton_id);

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    Toast.makeText(LoginActivity.this, "Signed In already", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Not Signed In", Toast.LENGTH_LONG).show();
                }
            }
        };


        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String emailmsg=email.getText().toString();
                String pwdmsg=password.getText().toString();
                if(!TextUtils.isEmpty(emailmsg)&&!TextUtils.isEmpty(pwdmsg))
                {
                    loginfunc(emailmsg,pwdmsg);
                }
                else{

                }
            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(LoginActivity.this,"You signed out!",Toast.LENGTH_SHORT).show();
            }
        });

        create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String emailmsg=email.getText().toString();
                String pwdmsg=password.getText().toString();
                if(!TextUtils.isEmpty(emailmsg)&&!TextUtils.isEmpty(pwdmsg))
                {
                    mAuth.createUserWithEmailAndPassword(emailmsg,pwdmsg)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(!task.isSuccessful())
                                    {

                                        Toast.makeText(LoginActivity.this,"Not Successful",Toast.LENGTH_LONG).show();

                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this,"Created Account!",Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(LoginActivity.this, AlertActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                }

            }
        });
    }




    private void loginfunc(String emailmsg, String pwdmsg)
    {
        mAuth.signInWithEmailAndPassword(emailmsg,pwdmsg)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(LoginActivity.this, AlertActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Incorrect!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(mAuthListener!=null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    /*@Override

    private void updateUI(FirebaseUser user) {
        if (user != null) {
        Log.d(TAG,"User Already Signed in");

        }
        else {
            Log.d(TAG,"User signed out ");
        }
    }
    */


}
