package com.dunkin.customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dunkin.customer.fragments.LoginFragment;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
    }
}
