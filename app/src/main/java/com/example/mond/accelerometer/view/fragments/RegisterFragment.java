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

public class RegisterFragment extends Fragment {

    public static final String REGISTER_FRAGMENT_TAG = "registerFragmentTag";

    @BindView(R.id.et_user_email)
    EditText mEmailInput;
    @BindView(R.id.et_user_password)
    EditText mPasswordInput;
    @BindView(R.id.btn_register)
    Button mRegisterBtn;
    @BindView(R.id.tv_sign_in_propose)
    TextView mSignInProposeText;

    private AuthenticationInteractionListener mListener;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.btn_register)
    public void registration() {
        if (Util.isFieldsNotEmpty(mEmailInput.getText().toString(),
                mPasswordInput.getText().toString())) {
            mListener.onRegister(mEmailInput.getText().toString(), mPasswordInput.getText().toString());
        }
    }

    @OnClick(R.id.tv_sign_in_propose)
    public void changeAuthentication() {
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
