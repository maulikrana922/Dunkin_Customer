package com.dunkin.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.ProductModel;
import com.dunkin.customer.widget.RelativeLayoutButton;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class ProductDetailActivity extends BackActivity implements View.OnClickListener {

    RelativeLayoutButton btnAddToCart, btnAddToppings;
    private ProductModel pm;
    private Context context;
    private String currency_code;
    private ImageView imgLogo, btnAddQty, btnDeletQty;
    private TextView txtProductName, txtItemPoint, txtProductPrice, txtProductDescription;
    private EditText edProQuantity;
    private DBAdapter dbAdapter;
    private int country_id;
    private ScrollView scrollContainer;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = (ProductModel) getIntent().getSerializableExtra("product");

        inflateView(R.layout.activity_product_detail, pm.getProductName());

        context = ProductDetailActivity.this;
        dbAdapter = new DBAdapter(context);

        currency_code = pm.getCurrency();

        country_id = getIntent().getIntExtra("country_id", 0);

        initializeViews();

        /*try {
            getDataFromAPI(pm.getProductId());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }*/
    }

    // Uncomment if required
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        *//*MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.submit_btn_menu, menu);*//*

        getMenuInflater().inflate(R.menu.cart_btn_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Uncomment if required
        /*if (item.getItemId() == R.id.menu_cart) {
            Intent i = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivityForResult(i, 1);
        }*/

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

    private void initializeViews() {
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        txtProductName = (TextView) findViewById(R.id.txtTitle);
        txtProductPrice = (TextView) findViewById(R.id.txtProductPrice);
        txtItemPoint = (TextView) findViewById(R.id.txtItemPoint);
        txtProductDescription = (TextView) findViewById(R.id.txtProductDescription);
        btnAddQty = (ImageView) findViewById(R.id.btnAddQty);
        btnDeletQty = (ImageView) findViewById(R.id.btnDeletQty);
        edProQuantity = (EditText) findViewById(R.id.edProQuantity);
        btnAddToCart = (RelativeLayoutButton) findViewById(R.id.btnAddToCart);
        btnAddToppings = (RelativeLayoutButton) findViewById(R.id.btnAddToppings);

        //btnAddToCart.imgIcon.setImageResource(R.drawable.ic_cart_icon_57);
        btnAddToCart.imgIcon.setImageResource(R.drawable.ic_nav_my_order);
        btnAddToCart.btnText.setText(context.getString(R.string.txt_add_to_cart));

        //btnAddToppings.imgIcon.setImageResource(R.drawable.ic_cart_icon_57);
        btnAddToppings.imgIcon.setImageResource(R.drawable.ic_nav_my_order);
        btnAddToppings.btnText.setText(context.getString(R.string.btn_add_toppings2));

        /*if (pm.getToppings() != null && pm.getToppings().size() == 0) {
            btnAddToppings.setVisibility(View.GONE);
        } else {
            btnAddToppings.setVisibility(View.VISIBLE);
        }*/

        if (pm.getToppings() != null && pm.getToppings().size() > 0) {
            btnAddToppings.setVisibility(View.VISIBLE);
        } else {
            btnAddToppings.setVisibility(View.INVISIBLE);
        }

        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);

        edProQuantity.setText("" + pm.getQty());

        btnAddToCart.setOnClickListener(this);
        btnAddToppings.setOnClickListener(this);

        btnAddQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pm.setQty(pm.getQty() + 1);
                edProQuantity.setText("" + pm.getQty());
            }
        });

        btnDeletQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pm.getQty() != 1) {
                    pm.setQty(pm.getQty() - 1);
                    edProQuantity.setText("" + pm.getQty());
                }
            }

        });

        if (country_id == AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1)) {
            btnAddToCart.setVisibility(View.VISIBLE);
        } else
            btnAddToCart.setVisibility(View.GONE);

        if (pm != null) {
            progressLoading.setVisibility(View.GONE);
            scrollContainer.setVisibility(View.VISIBLE);

            AppUtils.setImage(imgLogo, pm.getProductImage());

            String productName = Html.fromHtml(pm.getProductName()).toString();
            txtProductName.setText(productName);

            String itemPrice = Html.fromHtml(getString(R.string.txt_number_of_points1, String.valueOf(pm.getItemPoints()))).toString();
            txtItemPoint.setText(itemPrice);

            // String productDescription = Html.fromHtml("<br/><b> " + getString(R.string.txt_product_description) + " : </b> <br/>" + pm.getProductDescription()).toString();
            txtProductDescription.setText(Html.fromHtml(pm.getProductDescription()));
            txtProductDescription.setMovementMethod(LinkMovementMethod.getInstance());

            pm.setProductPrice(pm.getProductPrice());

            //String productPrice = Html.fromHtml(AppUtils.currencyFormat(pm.getProductPrice()) + " " + pm.getCurrency()).toString();
            txtProductPrice.setText(getString(R.string.txt_product_amount, AppUtils.currencyFormat(pm.getProductPrice()), pm.getCurrency()));
        } else {
            try {
                getDataFromAPI(pm.getProductId());
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDataFromAPI(int productId) throws UnsupportedEncodingException, JSONException {

        AppController.getProductDetail(context, productId, country_id, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);

                //Dunkin_Log.e("DataResponse", jsonResponse.toString());

                progressLoading.setVisibility(View.GONE);
                if (jsonResponse != null && jsonResponse.getInt("success") == 1) {
                    scrollContainer.setVisibility(View.VISIBLE);
                    ProductModel productModel = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("productDetail").toString(), new TypeReference<ProductModel>() {
                    });
                    if (productModel != null) {
                        AppUtils.setImage(imgLogo, productModel.getProductImage());

                        String productName = Html.fromHtml(productModel.getProductName()).toString();
                        txtProductName.setText(productName);

                        String itemPrice = Html.fromHtml(getString(R.string.txt_number_of_points1, String.valueOf(productModel.getItemPoints()))).toString();
                        txtItemPoint.setText(itemPrice);

                        // String productDescription = Html.fromHtml("<br/><b> " + getString(R.string.txt_product_description) + " : </b> <br/>" + pm.getProductDescription()).toString();
                        txtProductDescription.setText(Html.fromHtml(productModel.getProductDescription()));
                        txtProductDescription.setMovementMethod(LinkMovementMethod.getInstance());

                        pm.setProductPrice(productModel.getProductPrice());

                        //String productPrice = Html.fromHtml(AppUtils.currencyFormat(pm.getProductPrice()) + " " + pm.getCurrency()).toString();
                        txtProductPrice.setText(getString(R.string.txt_product_amount, AppUtils.currencyFormat(pm.getProductPrice()), pm.getCurrency()));
                    }
                }else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }
            }
        });
    }

    public void addCartItems() {
        pm.setProductToppings(pm.getToppings());
        int subtotal = pm.getProductPrice() * pm.getQty();
        pm.setSubTotal(subtotal);
        openToppingDialog(true, false);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAddToppings) {
            pm.setProductToppings(pm.getToppings());
            openToppingDialog(false, true);
        }

        if (v == btnAddToCart) {
            addCartItems();
        }
    }

    private void openToppingDialog(final boolean isFromOrder, final boolean fromAddOnOrCart) {
        if (pm.getToppings() != null && pm.getToppings().size() > 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(context.getString(R.string.al_warning));
            alert.setMessage(getString(R.string.msg_al_add_topping_add_ons));
            alert.setPositiveButton(context.getString(R.string.al_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AppUtils.openToppingActivity(context, pm, fromAddOnOrCart);
               /* Intent i = new Intent(context,ToppingListActivity.class);
                i.putExtra("product",pm);
                context.startActivity(i);*/
                }
            });

            alert.setNegativeButton(context.getString(R.string.al_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (isFromOrder) {
                        dbAdapter.open();
                        dbAdapter.addProductInCart(pm, null);
                        dbAdapter.close();
                    }
                }
            });
            alert.create().show();
        } else {
            dbAdapter.open();
            dbAdapter.addProductInCart(pm, null);
            dbAdapter.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
            Intent i = new Intent(ProductDetailActivity.this, HomeActivity.class);
            i.putExtra("navigateflag", AppConstants.MENU_PRODUCTS);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }
}
