package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.interfaces.AuthenticationInteractionListener;
import com.example.mond.accelerometer.util.Util;
import com.example.mond.accelerometer.view.fragments.LogInFragment;
import com.example.mond.accelerometer.view.fragments.RegisterFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthenticationActivity extends AppCompatActivity implements AuthenticationInteractionListener,
        GoogleApiClient.OnConnectionFailedListener {

    private final int RC_SIGN_IN = 4004;

    private FirebaseAuth mAuth;
    private FragmentManager mFm;

    private LogInFragment mLogInFragment;
    private RegisterFragment mRegisterFragment;

    private GoogleApiClient mGoogleApiClient;

    @BindView(R.id.sign_in_btn_google_auth)
    SignInButton mSignInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        initFragments();
        googleAuthInit();
    }

    private void initFragments() {
        mFm = getSupportFragmentManager();
        FragmentTransaction ft = mFm.beginTransaction();

        if (mFm.findFragmentByTag(LogInFragment.LOG_IN_FRAGMENT_TAG) == null) {
            mLogInFragment = LogInFragment.newInstance();
            ft.replace(R.id.fl_log_in_fragment_container, mLogInFragment, LogInFragment.LOG_IN_FRAGMENT_TAG);
        } else {
            mLogInFragment = (LogInFragment) mFm.findFragmentByTag(LogInFragment.LOG_IN_FRAGMENT_TAG);
        }

        if (mFm.findFragmentByTag(RegisterFragment.REGISTER_FRAGMENT_TAG) == null) {
            mRegisterFragment = RegisterFragment.newInstance();
            ft.replace(R.id.fl_registration_fragment_container, mRegisterFragment, RegisterFragment.REGISTER_FRAGMENT_TAG);
            ft.hide(mRegisterFragment);
        } else {
            mRegisterFragment = (RegisterFragment) mFm.findFragmentByTag(RegisterFragment.REGISTER_FRAGMENT_TAG);
        }

        ft.commit();
    }

    @OnClick(R.id.sign_in_btn_google_auth)
    void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleAuthInit() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                signInWithGoogle(acct);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(AuthenticationActivity.this, R.string.error_log_in_filed,
                Toast.LENGTH_SHORT).show();
    }

    private void signInWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startSessionActivity();
                } else {
                    Toast.makeText(AuthenticationActivity.this, getResources().getString(R.string.error_log_in_filed),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createAccount(final String email, final String pswd) {
        mAuth.createUserWithEmailAndPassword(email, pswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(AuthenticationActivity.this, getResources().getString(R.string.error_create_account_filed),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            logIn(email, pswd);
                        }
                    }
                });
    }

    private void logIn(final String email, String pswd) {
        mAuth.signInWithEmailAndPassword(email, pswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(AuthenticationActivity.this, getResources().getString(R.string.error_log_in_filed),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuthenticationActivity.this, getResources().getString(R.string.message_logged),
                                    Toast.LENGTH_SHORT).show();
                            startSessionActivity();
                        }
                    }
                });
    }

    private void startSessionActivity() {
        Intent intent = new Intent(AuthenticationActivity.this, SessionActivity.class);
        intent.putExtra(SessionActivity.UID, mAuth.getCurrentUser().getUid());
        startActivity(intent);

        finish();
    }

    @Override
    public void changeAuthenticationForm() {
        FragmentTransaction ft = mFm.beginTransaction();
        if (mRegisterFragment.isHidden()) {
            ft.show(mRegisterFragment);
            ft.hide(mLogInFragment);
            ft.commit();
        } else {
            ft.show(mLogInFragment);
            ft.hide(mRegisterFragment);
            ft.commit();
        }
    }

    @Override
    public void onLogIn(String email, String pswd) {
        if (Util.isNetworkAvailable(AuthenticationActivity.this)) {
            logIn(email, pswd);
        }
    }

    @Override
    public void onRegister(String email, String pswd) {
        if (Util.isNetworkAvailable(AuthenticationActivity.this)) {
            createAccount(email, pswd);
        }
    }
}
