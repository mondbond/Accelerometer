package com.example.mond.accelerometer.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.interfaces.AuthenticationInteractionListener;
import com.example.mond.accelerometer.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInFragment extends Fragment {

    public static final String LOG_IN_FRAGMENT_TAG = "logInFragmentTag";

    @BindView(R.id.field_email) EditText mEmailInput;
    @BindView(R.id.field_password) EditText mPasswordInput;
    @BindView(R.id.log_in_button) Button mLogInBtn;
    @BindView(R.id.registration_propose_text) TextView mRegistrationProposeText;

    private AuthenticationInteractionListener mListener;

    public static LogInFragment newInstance() {
        return new LogInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log_in, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.log_in_button)
    public void signIn(){
        if(Util.isFieldsNotEmpty(mEmailInput.getText().toString(),
                mPasswordInput.getText().toString())) {
            mListener.onLogIn(mEmailInput.getText().toString(), mPasswordInput.getText().toString());
        }
    }

    @OnClick(R.id.registration_propose_text)
    public void changeAuthentication(){
        mListener.changeAuthenticationForm();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthenticationInteractionListener) {
            mListener = (AuthenticationInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
