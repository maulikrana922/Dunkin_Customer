package com.dunkin.customer.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.OfferProductModel;

import java.util.List;

/**
 * Created by Android2 on 9/12/2015.
 */
public class OfferProductDetailAdapter extends BaseAdapter {

    private List<OfferProductModel> offerProductModels;
    private LayoutInflater inflater;
    private Context context;
    private String offerIsFree;
    private String priceCurrency;
    private boolean isFromOfferDetail;
    private String offerHappy;

    public OfferProductDetailAdapter(Context context, List<OfferProductModel> offerProductModels, String freeOffer, String currency, boolean fromOfferDetail, String offerHappy) {
        this.context = context;
        this.offerProductModels = offerProductModels;
        this.offerIsFree = freeOffer;
        this.priceCurrency = currency;
        this.isFromOfferDetail = fromOfferDetail;
        this.offerHappy=offerHappy;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return offerProductModels.size();
    }

    @Override
    public Object getItem(int position) {
        return offerProductModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OfferProductModel ofm = (OfferProductModel) getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.offer_product_list, null);

            viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            viewHolder.imgLogof = (ImageView) convertView.findViewById(R.id.imgLogof);

            viewHolder.txtProductPayOption = (TextView) convertView.findViewById(R.id.txtProductPayOption);
            viewHolder.txtProductPurchaseDate = (TextView) convertView.findViewById(R.id.txtProductPurchaseDate);
            viewHolder.offerProductname = (TextView) convertView.findViewById(R.id.offerProductname);
            viewHolder.offerProductBuy = (TextView) convertView.findViewById(R.id.offerProductBuy);
            viewHolder.offerProductfname = (TextView) convertView.findViewById(R.id.offerProductfname);
            viewHolder.offerProductFree = (TextView) convertView.findViewById(R.id.offerProductfree);
            viewHolder.tvOfferPrice1 = (TextView) convertView.findViewById(R.id.tvOfferPrice1);
            viewHolder.tvOfferPoint1 = (TextView) convertView.findViewById(R.id.tvOfferPoint1);
            viewHolder.tvOfferPrice2 = (TextView) convertView.findViewById(R.id.tvOfferPrice2);
            viewHolder.tvOfferPoint2 = (TextView) convertView.findViewById(R.id.tvOfferPoint2);
            viewHolder.txtProductRemainingTimeRedeem = (TextView) convertView.findViewById(R.id.txtProductRemainingTimeRedeem);
            viewHolder.txtLabelAvailedProduct = (TextView) convertView.findViewById(R.id.txtLabelAvailedProduct);
            viewHolder.txtAvailedProduct = (TextView) convertView.findViewById(R.id.txtAvailedProduct);

            viewHolder.llLeft = (RelativeLayout) convertView.findViewById(R.id.llLeft);
            viewHolder.llRight = (RelativeLayout) convertView.findViewById(R.id.llRight);
            viewHolder.llRemainingRedeem = (LinearLayout) convertView.findViewById(R.id.llRemainingRedeem);
            viewHolder.llAvailedProduct = (LinearLayout) convertView.findViewById(R.id.llAvailedProduct);
            viewHolder.viewSeperator = convertView.findViewById(R.id.viewSeperator);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            if (offerIsFree.equals("Yes") || (offerIsFree.equals("No") && offerHappy.equalsIgnoreCase("Yes"))) {
                viewHolder.llRight.setVisibility(View.GONE);
                viewHolder.llRemainingRedeem.setVisibility(View.GONE);
                viewHolder.llAvailedProduct.setVisibility(View.GONE);
                viewHolder.txtProductPayOption.setVisibility(View.GONE);
                viewHolder.viewSeperator.setVisibility(View.GONE);

                if (!isFromOfferDetail) {
                    // Product Purchase Date
                    if (AppUtils.isNotNull(ofm.getOfferonproduct().getPurchase_date())) {
                        viewHolder.txtProductPurchaseDate.setVisibility(View.VISIBLE);
                        viewHolder.txtProductPurchaseDate.setText(context.getString(R.string.txt_offer_purchase_date, AppUtils.getFormattedDateTimeRedeemHistory(ofm.getOfferonproduct().getPurchase_date())));
                    } else
                        viewHolder.txtProductPurchaseDate.setVisibility(View.GONE);
                } else {
                    viewHolder.txtProductPayOption.setVisibility(View.GONE);
                    viewHolder.txtProductPurchaseDate.setVisibility(View.GONE);
                }

                // Free Product Image
                if (AppUtils.isNotNull(ofm.getOfferonproduct().getProductImage())) {
                    viewHolder.imgLogo.setVisibility(View.VISIBLE);
                    AppUtils.setImage(viewHolder.imgLogo, ofm.getOfferonproduct().getProductImage());
                } else
                    viewHolder.imgLogo.setVisibility(View.GONE);

                // Free Product Name
                if (AppUtils.isNotNull(ofm.getOfferonproduct().getProductName())) {
                    viewHolder.offerProductname.setVisibility(View.VISIBLE);
                    viewHolder.offerProductname.setText(Html.fromHtml(ofm.getOfferonproduct().getProductName()));
                } else
                    viewHolder.offerProductname.setVisibility(View.GONE);

                // Free Product
                if(offerIsFree.equals("Yes")) {
                    if (AppUtils.isNotNull(ofm.getOfferonproduct().getBuythisproduct())) {
                        viewHolder.offerProductBuy.setVisibility(View.VISIBLE);
                        viewHolder.offerProductBuy.setText(context.getString(R.string.txt_free_type2, ofm.getOfferonproduct().getBuythisproduct()));
                    } else
                        viewHolder.offerProductBuy.setVisibility(View.GONE);
                }
                else if((offerIsFree.equals("No") && offerHappy.equalsIgnoreCase("Yes")))
                {
                    if (AppUtils.isNotNull(ofm.getOfferonproduct().getBuythisproduct())) {
                        viewHolder.offerProductBuy.setVisibility(View.VISIBLE);
                        viewHolder.offerProductBuy.setText(context.getString(R.string.num_of_buy_products2, ofm.getOfferonproduct().getBuythisproduct()));
                    } else
                        viewHolder.offerProductBuy.setVisibility(View.GONE);
                }


                // Free Product Price
                if (AppUtils.isNotNull(ofm.getOfferonproduct().getProductPrice())) {
                    viewHolder.tvOfferPrice1.setVisibility(View.VISIBLE);
                    viewHolder.tvOfferPrice1.setText(context.getString(R.string.txt_product_amount, AppUtils.CurrencyFormat(Double.parseDouble(ofm.getOfferonproduct().getProductPrice())), priceCurrency));
                } else
                    viewHolder.tvOfferPrice1.setVisibility(View.GONE);

                // Free Product Point
                if (AppUtils.isNotNull(ofm.getOfferonproduct().getItemPoints())) {
                    viewHolder.tvOfferPoint1.setVisibility(View.VISIBLE);
                    viewHolder.tvOfferPoint1.setText(context.getResources().getString(R.string.txt_number_of_points1, AppUtils.CurrencyFormat(Double.parseDouble(ofm.getOfferonproduct().getItemPoints()))));
                } else
                    viewHolder.tvOfferPoint1.setVisibility(View.GONE);
            } else {
                viewHolder.llRight.setVisibility(View.VISIBLE);
                viewHolder.llRemainingRedeem.setVisibility(View.VISIBLE);
                viewHolder.llAvailedProduct.setVisibility(View.VISIBLE);
                viewHolder.txtProductPayOption.setVisibility(View.VISIBLE);
                viewHolder.viewSeperator.setVisibility(View.VISIBLE);

                if (!isFromOfferDetail) {

                    viewHolder.llAvailedProduct.setVisibility(View.GONE);

                    // Product Purchase Date
                    if (AppUtils.isNotNull(ofm.getOfferonproduct().getPurchase_date())) {
                        viewHolder.txtProductPurchaseDate.setVisibility(View.VISIBLE);
                        viewHolder.txtProductPurchaseDate.setText(context.getString(R.string.txt_offer_purchase_date, AppUtils.getFormattedDateTimeRedeemHistory(ofm.getOfferonproduct().getPurchase_date())));
                    } else
                        viewHolder.txtProductPurchaseDate.setVisibility(View.GONE);

                    // Product Pay Option
                    if (AppUtils.isNotNull(ofm.getOfferonproduct().getPay_option())) {
                        viewHolder.txtProductPayOption.setVisibility(View.VISIBLE);

                        if (ofm.getOfferonproduct().getPay_option().equalsIgnoreCase("1"))
                            viewHolder.txtProductPayOption.setText(context.getString(R.string.txt_payment_type, context.getString(R.string.pay_by_cash)));
                        else if (ofm.getOfferonproduct().getPay_option().equalsIgnoreCase("2"))
                            viewHolder.txtProductPayOption.setText(context.getString(R.string.txt_payment_type, context.getString(R.string.pay_by_wallet)));
                        else if (ofm.getOfferonproduct().getPay_option().equalsIgnoreCase("3"))
                            viewHolder.txtProductPayOption.setText(context.getString(R.string.txt_payment_type, context.getString(R.string.pay_by_points)));
                    } else
                        viewHolder.txtProductPayOption.setVisibility(View.GONE);
                } else {
                    viewHolder.txtProductPayOption.setVisibility(View.GONE);
                    viewHolder.txtProductPurchaseDate.setVisibility(View.GONE);
                    viewHolder.llAvailedProduct.setVisibility(View.VISIBLE);
                }

                // Buy Product Image
                if (AppUtils.isNotNull(ofm.getOfferonproduct().getProductImage())) {
                    viewHolder.imgLogo.setVisibility(View.VISIBLE);
                    AppUtils.setImage(viewHolder.imgLogo, ofm.getOfferonproduct().getProductImage());
                } else
                    viewHolder.imgLogo.setVisibility(View.GONE);
                // Free Product Image
                if (AppUtils.isNotNull(ofm.getOfferfreeproduct().getProductImage())) {
                    viewHolder.imgLogof.setVisibility(View.VISIBLE);
                    AppUtils.setImage(viewHolder.imgLogof, ofm.getOfferfreeproduct().getProductImage());
                } else
                    viewHolder.imgLogof.setVisibility(View.GONE);

                // Buy Product Name
                if (AppUtils.isNotNull(ofm.getOfferonproduct().getProductName())) {
                    viewHolder.offerProductname.setVisibility(View.VISIBLE);
                    viewHolder.offerProductname.setText(Html.fromHtml(ofm.getOfferonproduct().getProductName()));
                } else
                    viewHolder.offerProductname.setVisibility(View.GONE);

                // Free Product Name
                if (AppUtils.isNotNull(ofm.getOfferfreeproduct().getProductName())) {
                    viewHolder.offerProductfname.setVisibility(View.VISIBLE);
                    viewHolder.offerProductfname.setText(Html.fromHtml(ofm.getOfferfreeproduct().getProductName()));
                } else
                    viewHolder.offerProductfname.setVisibility(View.GONE);

                // Buy Product
                if (AppUtils.isNotNull(ofm.getOfferonproduct().getBuythisproduct())) {
                    viewHolder.offerProductBuy.setVisibility(View.VISIBLE);
                    viewHolder.offerProductBuy.setText(context.getString(R.string.num_of_buy_products2, ofm.getOfferonproduct().getBuythisproduct()));
                } else
                    viewHolder.offerProductBuy.setVisibility(View.GONE);
                // Free Product
                if (AppUtils.isNotNull(ofm.getOfferfreeproduct().getFreethisproduct())) {
                    viewHolder.offerProductFree.setVisibility(View.VISIBLE);
                    viewHolder.offerProductFree.setText(context.getString(R.string.num_of_free_products2, ofm.getOfferfreeproduct().getFreethisproduct()));
                } else
                    viewHolder.offerProductFree.setVisibility(View.GONE);

                // Buy Product Price
                if (AppUtils.isNotNull(ofm.getOfferonproduct().getProductPrice())) {
                    viewHolder.tvOfferPrice1.setVisibility(View.VISIBLE);
                    viewHolder.tvOfferPrice1.setText(context.getString(R.string.txt_product_amount, AppUtils.CurrencyFormat(Double.parseDouble(ofm.getOfferonproduct().getProductPrice())), priceCurrency));
                } else
                    viewHolder.tvOfferPrice1.setVisibility(View.GONE);
                if (AppUtils.isNotNull(ofm.getOfferfreeproduct().getProductPrice())) {
                    viewHolder.tvOfferPrice2.setVisibility(View.VISIBLE);
                    viewHolder.tvOfferPrice2.setText(context.getString(R.string.txt_product_amount, AppUtils.CurrencyFormat(0.00), priceCurrency));
                } else
                    viewHolder.tvOfferPrice2.setVisibility(View.GONE);
                // Free Product Point
                if (AppUtils.isNotNull(ofm.getOfferonproduct().getItemPoints())) {
                    viewHolder.tvOfferPoint1.setVisibility(View.VISIBLE);
                    viewHolder.tvOfferPoint1.setText(context.getResources().getString(R.string.txt_number_of_points1, AppUtils.CurrencyFormat(Double.parseDouble(ofm.getOfferonproduct().getItemPoints()))));
                } else
                    viewHolder.tvOfferPoint1.setVisibility(View.GONE);
                if (AppUtils.isNotNull(ofm.getOfferfreeproduct().getItemPoints())) {
                    viewHolder.tvOfferPoint2.setVisibility(View.VISIBLE);
                    viewHolder.tvOfferPoint2.setText(context.getResources().getString(R.string.txt_number_of_points1, AppUtils.CurrencyFormat(0.00)));
                } else
                    viewHolder.tvOfferPoint2.setVisibility(View.GONE);

                if (isFromOfferDetail) {
                    if (Integer.parseInt(ofm.getOfferonproduct().getRemainthisproduct()) == 0) {
                        viewHolder.txtProductRemainingTimeRedeem.setText(String.format(" %s", ofm.getOfferonproduct().getBuythisproduct()));
                    } else {
                        viewHolder.txtProductRemainingTimeRedeem.setText(String.format(" %s", ofm.getOfferonproduct().getRemainthisproduct()));
                    }
                } else {
                    viewHolder.llRemainingRedeem.setVisibility(View.GONE);
                }

                if (!isFromOfferDetail) {
                    if (viewHolder.imgLogof.getVisibility() == View.GONE ||
                            viewHolder.offerProductfname.getVisibility() == View.GONE ||
                            viewHolder.offerProductFree.getVisibility() == View.GONE ||
                            viewHolder.tvOfferPrice2.getVisibility() == View.GONE ||
                            viewHolder.tvOfferPoint2.getVisibility() == View.GONE) {

                        viewHolder.llRight.setVisibility(View.GONE);
                        viewHolder.llRemainingRedeem.setVisibility(View.GONE);
                        viewHolder.viewSeperator.setVisibility(View.GONE);
                    } else {
                        viewHolder.llRight.setVisibility(View.VISIBLE);
                        viewHolder.llRemainingRedeem.setVisibility(View.GONE);
                        viewHolder.llAvailedProduct.setVisibility(View.GONE);
                        viewHolder.viewSeperator.setVisibility(View.VISIBLE);
                    }
                }

                viewHolder.txtLabelAvailedProduct.setVisibility(View.GONE);
                viewHolder.txtAvailedProduct.setText(context.getString(R.string.txt_free_product_available, ofm.getOfferfreeproduct().getAvailQty()));
                /*if(!ofm.getOfferfreeproduct().getUsedQty().equalsIgnoreCase("0")) {
                    viewHolder.txtLabelAvailedProduct.setVisibility(View.GONE);
                    viewHolder.txtAvailedProduct.setText(context.getString(R.string.txt_free_product_available, ofm.getOfferfreeproduct().getUsedQty()));
                } else
                    viewHolder.txtLabelAvailedProduct.setVisibility(View.VISIBLE);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        ImageView imgLogo, imgLogof;
        TextView txtProductPayOption, txtProductPurchaseDate, offerProductname, offerProductBuy, offerProductFree, offerProductfname, tvOfferPrice1, tvOfferPrice2, tvOfferPoint1, tvOfferPoint2, txtProductRemainingTimeRedeem, txtLabelAvailedProduct, txtAvailedProduct;
        RelativeLayout llLeft, llRight;
        LinearLayout llRemainingRedeem, llAvailedProduct;
        View viewSeperator;
    }
}
