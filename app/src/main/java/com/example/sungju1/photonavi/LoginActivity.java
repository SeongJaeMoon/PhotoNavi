package com.example.sungju1.photonavi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailText, passwordText;
    private Button  loginBtn;
    private TextView forgotAcoount, newAccount;
    private ProgressBar progressBar;
    private Button btnKakao;
    private Button btnNaver;
    private FirebaseAuth mFirbaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirbaseAuth = FirebaseAuth.getInstance();



        setContentView(R.layout.activity_login);
        emailText = (EditText) findViewById(R.id.xetEmail);
        passwordText = (EditText)findViewById(R.id.xetPassword);
        loginBtn = (Button)findViewById(R.id.xbtnLogIn);
        forgotAcoount = (TextView)findViewById(R.id.xbtnForgotAccount);
        newAccount = (TextView)findViewById(R.id.xbtnNewAccount);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);


    }

    public void doLogin(){
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if(!email.isEmpty() && !password.isEmpty()){
            progressBar.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

}
