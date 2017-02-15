package com.dunkin.customer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.CreditCardModel;
import com.dunkin.customer.widget.RelativeLayoutButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


public class EditCardActivity extends BackActivity implements View.OnClickListener {

    private EditText edCardNumber, edCardHolderName, spMonths, spYears;


    private CreditCardModel creditCardModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_add_new_card, getString(R.string.lbl_edit_card));
        initializeViews();


    }

    private void initializeViews() {

        creditCardModel = (CreditCardModel) getIntent().getSerializableExtra("creditcard");


        edCardNumber = (EditText) findViewById(R.id.edCardNumber);
        edCardHolderName = (EditText) findViewById(R.id.edCardHolderName);
        spMonths = (EditText) findViewById(R.id.spExpiryMonth);
        spYears = (EditText) findViewById(R.id.spExpiryYears);
        RelativeLayoutButton btnSubmit = (RelativeLayoutButton) findViewById(R.id.btnSubmit);
        btnSubmit.btnText.setText(getString(R.string.btn_submit));
        btnSubmit.imgIcon.setImageResource(R.drawable.ic_submit);

        String number = creditCardModel.getCardNumber();
        String masknumber = AppUtils.maskCardNumber(number, "xxxx-xxxx-xxxx" + "-" + number.substring(number.length() - 4, number.length()));


        edCardNumber.setText(masknumber);
        edCardHolderName.setText(creditCardModel.getCardHolderName());

        spYears.setText(creditCardModel.getExpiry().split("-")[0]);
        spMonths.setText(creditCardModel.getExpiry().split("-")[1]);


        btnSubmit.setOnClickListener(EditCardActivity.this);

        edCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove all spacing char

                char space = ' ';
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 4;
                while (true) {
                    if (pos >= s.length()) break;
                    final char c = s.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + space);
                    }
                    pos += 5;
                }

            }
        });

        edCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edCardNumber.setText("");
            }
        });
    }

    @Override
    public void onClick(View v) {
        String currentYear = String.valueOf(Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR));
        String currentMonth = String.valueOf(Calendar.getInstance(Locale.getDefault()).get(Calendar.MONTH) + 1);
        if (Integer.parseInt(currentMonth) < 10) {
            currentMonth = "0" + currentMonth;
        }

        if (edCardNumber.getText().length() == 0) {
            AppUtils.showError(edCardNumber, getString(R.string.empty_card_number));
        } else if (edCardNumber.getText().length() < 19) {
            AppUtils.showError(edCardNumber, getString(R.string.min_length_card_number));
        } else if (edCardHolderName.getText().length() == 0) {
            AppUtils.showError(edCardHolderName, getString(R.string.empty_cardholder_name));
        } else if (spMonths.getText().length() < 2) {
            AppUtils.showError(spMonths, getString(R.string.min_length_expiry_month));
        } else if (Integer.parseInt(spMonths.getText().toString()) == 0 || Integer.parseInt(spMonths.getText().toString()) > 12) {
            AppUtils.showError(spMonths, getString(R.string.max_val_expiry_month));
        } else if (spYears.getText().length() < 4) {
            AppUtils.showError(spYears, getString(R.string.min_length_expiry_year));
        } else if (Integer.parseInt(spYears.getText().toString()) < Integer.parseInt(currentYear)) {
            AppUtils.showError(spYears, getString(R.string.min_val_expiry_year));
        } else if (currentYear.equals(spYears.getText().toString()) && (Integer.parseInt(currentMonth) > Integer.parseInt(spMonths.getText().toString()))) {
            AppUtils.showError(spYears, getString(R.string.min_val_expiry_month_current));
        } else {


            String cardNumber = edCardNumber.getText().toString().replace(" ", "-");

            String expiry = spYears.getText().toString() + "-" + spMonths.getText().toString();
            String cardHolder = edCardHolderName.getText().toString();


            AppController.editCardDetail(EditCardActivity.this, creditCardModel.getId(), cardNumber, expiry, cardHolder, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);


                    if (jsonResponse.getInt("success") == 1) {
                        AppUtils.showToastMessage(EditCardActivity.this, getString(R.string.msg_card_added_success));
                        setResult(RESULT_OK);
                        finish();
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }else {
                        AppUtils.showToastMessage(EditCardActivity.this, getString(R.string.msg_card_added_failed));
                    }

                }
            });

        }
    }
}
