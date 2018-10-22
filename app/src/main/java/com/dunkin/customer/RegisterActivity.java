package com.dunkin.customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.fragments.LoginFragment;
import com.dunkin.customer.fragments.RegisterFragment;


public class RegisterActivity extends AppCompatActivity {

    LinearLayout llLoginSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        llLoginSignUp = (LinearLayout) findViewById(R.id.llLoginSignUp);

        llLoginSignUp.setVisibility(View.VISIBLE);
        ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.txtLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLoginSignUp.setVisibility(View.GONE);
                ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
            }
        });

        ((TextView) findViewById(R.id.txtSignUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLoginSignUp.setVisibility(View.GONE);
                ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new RegisterFragment()).commit();
            }
        });

        if (getIntent() != null && getIntent().hasExtra("isRegister")) {
            if (getIntent().getBooleanExtra("isRegister", false)){
                llLoginSignUp.setVisibility(View.GONE);
                ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
            }
        }

//        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
    }
}
