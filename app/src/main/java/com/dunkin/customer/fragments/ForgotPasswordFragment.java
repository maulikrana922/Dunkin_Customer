package com.dunkin.customer.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.widget.RelativeLayoutButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private Context context;

    private EditText edEmail;
    private RelativeLayoutButton btnSubmit;
    private TextView txtReturnToSignIn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        edEmail = (EditText) rootView.findViewById(R.id.edEmail);
        btnSubmit = (RelativeLayoutButton) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.btnText.setText(getString(R.string.btn_submit));
        btnSubmit.imgIcon.setImageResource(R.drawable.ic_submit);

        txtReturnToSignIn = (TextView) rootView.findViewById(R.id.txtReturnToSignIn);

        btnSubmit.setOnClickListener(this);
        txtReturnToSignIn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == txtReturnToSignIn) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
        }

        if (v == btnSubmit) {
            if (edEmail.getText().length() == 0) {
                AppUtils.showError(edEmail, getString(R.string.empty_email));
            } else if (!Pattern.matches(String.valueOf(Patterns.EMAIL_ADDRESS), edEmail.getText().toString())) {
                AppUtils.showError(edEmail, getString(R.string.invalid_email));
            } else {
                // CALL THE WEBSERVICE
                try {
                    AppController.forgotPassword(context, edEmail.getText().toString(), new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);
                            if (jsonResponse.getInt("success") == 0) {
                                AppUtils.showToastMessage(context, getString(R.string.msg_login_user_not_exist));
                            }

                            if (jsonResponse.getInt("success") == 2) {
                                AppUtils.showToastMessage(context, getString(R.string.msg_fp_failure_status_2));
                            }

                            if (jsonResponse.getInt("success") == 1) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setMessage(context.getString(R.string.msg_fp_response_success));
                                alert.setPositiveButton(context.getString(R.string.al_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //getFragmentManager().popBackStack();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
                                    }
                                });
                                alert.create().show();
                            }
                             if (jsonResponse.getInt("success") == 100) {
                                AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                            }
                        }
                    });
                } catch (JSONException | UnsupportedEncodingException e) {
                    AppUtils.showToastMessage(context, getString(R.string.msg_fp_failure_status_2));
                    e.printStackTrace();
                }
            }
        }
    }
}
