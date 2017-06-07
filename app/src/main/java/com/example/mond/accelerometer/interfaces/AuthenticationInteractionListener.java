package com.example.mond.accelerometer.interfaces;

public interface AuthenticationInteractionListener {

    void changeAuthenticationForm();
    void onLogIn(String email, String pswd);
    void onRegister(String email, String pswd);
}
