package com.dunkin.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.adapters.ProductListFragmentAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.models.CategoryModel;
import com.dunkin.customer.models.ChildCatModel;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.List;

public class ProductListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CategoryModel categoryModel = (CategoryModel) getIntent().getSerializableExtra("category");

        inflateView(R.layout.fragment_page_taber, categoryModel.getCategoryName());

        Context context = ProductListActivity.this;
        TextView emptyElement = (TextView) findViewById(R.id.emptyElement);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ProgressBar progressLoad = (ProgressBar) findViewById(R.id.progressLoad);
        final SmartTabLayout tabsStrip = (SmartTabLayout) findViewById(R.id.tabs);
        LinearLayout scrollContainer = (LinearLayout) findViewById(R.id.scrollContainer);

        scrollContainer.setVisibility(View.VISIBLE);

        List<ChildCatModel> childCatList = categoryModel.getChildCategory();

        if (childCatList.size() > 0) {

            progressLoad.setVisibility(View.GONE);
            emptyElement.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            tabsStrip.setVisibility(View.VISIBLE);

            int country_id = getIntent().getIntExtra("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));

            viewPager.setAdapter(new ProductListFragmentAdapter(getSupportFragmentManager(), country_id, childCatList));

            tabsStrip.setViewPager(viewPager);
        } else {
            progressLoad.setVisibility(View.GONE);
            emptyElement.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            tabsStrip.setVisibility(View.GONE);
        }
    }

    // Uncomment if required
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_btn_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        // Uncomment if required
        /*if (item.getItemId() == R.id.menu_cart) {
            Intent i = new Intent(ProductListActivity.this, CartActivity.class);
            startActivityForResult(i, 1);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
            Intent i = new Intent(ProductListActivity.this, HomeActivity.class);
            i.putExtra("navigateflag", AppConstants.MENU_PRODUCTS);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }
}
