package com.dunkin.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.PromoModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by qtm-c-android on 1/6/17.
 */

public class PromationDetailsActivity extends BaseActivity implements Animation.AnimationListener {
    private TextView txtOfferName, txtOfferDescription, txtPoint, txtUserPoint, txtPurchase, txtToken;
    private ImageView imgLogo;
    private RelativeLayout scrollContainer;
    private PromoModel promo_data;
    private ImageView btnAddQty, btnDeletQty;
    private EditText edProQuantity;
    private String user_point;
    Animation animFadein;
    Animation animFadeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView1(R.layout.activity_promo_detail, getString(R.string.header_promo));

        promo_data = (PromoModel) getIntent().getSerializableExtra("promo_data");
        user_point = getIntent().getStringExtra("user_point");

        initializeViews();
    }

    private void initializeViews() {
        txtOfferName = (TextView) findViewById(R.id.txtTitle);
        txtOfferDescription = (TextView) findViewById(R.id.txtDesc);
        txtPoint = (TextView) findViewById(R.id.txtPoint);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        scrollContainer = (RelativeLayout) findViewById(R.id.scrollContainer);
        btnAddQty = (ImageView) findViewById(R.id.btnAddQty);
        btnDeletQty = (ImageView) findViewById(R.id.btnDeletQty);
        edProQuantity = (EditText) findViewById(R.id.edProQuantity);
        txtUserPoint = (TextView) findViewById(R.id.txtUserPoint);
        txtPurchase = (TextView) findViewById(R.id.txtPurchase);
        txtToken = (TextView) findViewById(R.id.txtToken);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        scrollContainer.startAnimation(animFadein);

        animFadeout = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
        animFadeout.setAnimationListener(this);

        txtOfferName.setText(Html.fromHtml(promo_data.getName()));
        txtOfferDescription.setText(Html.fromHtml(promo_data.getDescription()));
        txtPoint.setText(promo_data.getTicketPoint() + " Point(s)");
        AppUtils.setImage(imgLogo, promo_data.getPromoImage());

        txtUserPoint.setText("Current Points : " + user_point);
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        Date current_date = null;
        try {
            current_date = df.parse(formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        promo_data.setQty(1);
        edProQuantity.setText("" + promo_data.getQty());

        btnAddQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promo_data.setQty(promo_data.getQty() + 1);
                edProQuantity.setText("" + promo_data.getQty());
            }
        });

        btnDeletQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (promo_data.getQty() != 1) {
                    promo_data.setQty(promo_data.getQty() - 1);
                    edProQuantity.setText("" + promo_data.getQty());
                }
            }

        });

        txtToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PromationDetailsActivity.this, TokensActivity.class);
                i.putExtra("promo_id", promo_data.getId());
                startActivity(i);
            }
        });

        txtPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float point = promo_data.getQty() * Float.parseFloat(promo_data.getTicketPoint());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PromationDetailsActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("Confirmation");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to purchase " + promo_data.getQty()
                        + " ticket(s) worth " + point + " points? amount is " +
                        "non-refundable");

                // On pressing Settings button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            getPromoTicket(promo_data.getId(), promo_data.getQty());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // on pressing cancel button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        });

        if (current_date != null) {
            if ((df.format(current_date).equals(df.format(promo_data.getStartDate())) ||
                    current_date.after(promo_data.getStartDate())) &&
                    (df.format(current_date).equals(df.format(promo_data.getEndDate())) ||
                            current_date.before(promo_data.getEndDate())))
            {
                txtPurchase.setVisibility(View.VISIBLE);
                btnAddQty.setVisibility(View.VISIBLE);
                btnDeletQty.setVisibility(View.VISIBLE);
                edProQuantity.setVisibility(View.VISIBLE);
            } else {
                txtPurchase.setVisibility(View.GONE);
                btnAddQty.setVisibility(View.INVISIBLE);
                btnDeletQty.setVisibility(View.INVISIBLE);
                edProQuantity.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void getPromoTicket(String promo_id, int qty) throws UnsupportedEncodingException, JSONException {
        AppController.getPromoTicket(PromationDetailsActivity.this, promo_id, qty, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                if (jsonResponse != null && jsonResponse.getInt("success") == 1) {
                    finish();
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PromationDetailsActivity.this);
//
//                    // Setting Dialog Message
//                    alertDialog.setMessage(jsonResponse.getString("message"));
//
//                    // On pressing Settings button
//                    alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            finish();
//                        }
//                    });
//                    // Showing Alert Message
//                    alertDialog.show();
                } else if (jsonResponse.getInt("success") == 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PromationDetailsActivity.this);

                    // Setting Dialog Message
                    alertDialog.setMessage(jsonResponse.getString("message"));

                    // On pressing Settings button
                    alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                } else {
                    if(jsonResponse.getInt("success") != 99) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PromationDetailsActivity.this);

                        // Setting Dialog Message
                        alertDialog.setMessage(getString(R.string.system_error));

                        // On pressing Settings button
                        alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                }
            }
        });
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    void inflateView1(int layout, String title) {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        if (toolBar != null) {
            toolBar.setTitleTextColor(ContextCompat.getColor(PromationDetailsActivity.this, android.R.color.white));
            setSupportActionBar(toolBar);
            if (title != null && title.length() > 0) {
                toolBar.findViewById(R.id.brandLogo).setVisibility(View.GONE);
                TextView tv = (TextView) toolBar.findViewById(R.id.toolbar_title);
                tv.setText(title);
                tv.setVisibility(View.VISIBLE);
            } else {
                toolBar.findViewById(R.id.brandLogo).setVisibility(View.VISIBLE);
                toolBar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            }
        }

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(PromationDetailsActivity.this, R.drawable.ic_nav_back));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FrameLayout viewContainer = (FrameLayout) findViewById(R.id.container);
        ViewGroup.inflate(PromationDetailsActivity.this, layout, viewContainer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
//            onBackPressed();  return true;
            scrollContainer.startAnimation(animFadeout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
