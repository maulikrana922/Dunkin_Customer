package com.dunkin.customer.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.TokensActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.PromoModel;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.dunkin.customer.R.id.btnAddQty;
import static com.dunkin.customer.R.id.btnDeletQty;
import static com.dunkin.customer.R.id.edProQuantity;
import static com.dunkin.customer.R.id.txtPurchase;
import static com.dunkin.customer.R.id.txtToken;
import static com.dunkin.customer.R.id.txtUserPoint;

/**
 * Created by qtm-c-android on 1/6/17.
 */

public class PlayItemAdapter extends ArrayAdapter<PromoModel> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
    private Context context;
    private String user_point;

    public PlayItemAdapter(Context context, List<PromoModel> objects, String user_point) {
        super(context, 0, objects);
        this.context = context;
        this.user_point = user_point;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item for selected view
        // if cell is exists - reuse it, if not - create the new one from resource

        final PromoModel playModel = (PromoModel) getItem(position);
        FoldingCell cell = (FoldingCell) convertView;
        final ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.main_item_play, parent, false);

            viewHolder.imgLogo = (ImageView) cell.findViewById(R.id.imgLogo);
            viewHolder.txtTitle = (TextView) cell.findViewById(R.id.txtTitle);
            viewHolder.txtDesc = (TextView) cell.findViewById(R.id.txtDesc);
            viewHolder.txtPoint = (TextView) cell.findViewById(R.id.txtPoint);
            viewHolder.itemLayout = (RelativeLayout) cell.findViewById(R.id.itemLayout);
            viewHolder.txtNoOfDays = (TextView) cell.findViewById(R.id.txtNoOfDays);

            viewHolder.txtOfferName = (TextView) cell.findViewById(R.id.txtTitle1);
            viewHolder.txtOfferDescription = (TextView) cell.findViewById(R.id.txtDesc);
            viewHolder.txtPoint1 = (TextView) cell.findViewById(R.id.txtPoint1);
            viewHolder.imgLogo1 = (ImageView) cell.findViewById(R.id.imgLogo1);
            viewHolder.scrollContainer = (ScrollView) cell.findViewById(R.id.scrollContainer);
            viewHolder.btnAddQty = (ImageView) cell.findViewById(btnAddQty);
            viewHolder.btnDeletQty = (ImageView) cell.findViewById(btnDeletQty);
            viewHolder.edProQuantity = (EditText) cell.findViewById(edProQuantity);
            viewHolder.txtUserPoint = (TextView) cell.findViewById(txtUserPoint);
            viewHolder.txtPurchase = (TextView) cell.findViewById(txtPurchase);
            viewHolder.txtToken = (TextView) cell.findViewById(txtToken);

            cell.setTag(viewHolder);
        } else {
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }
        viewHolder.itemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        try {
            AppUtils.setImage(viewHolder.imgLogo, playModel.getPromoImage());

            viewHolder.txtPoint.setText(playModel.getTicketPoint() + " Points");

            viewHolder.txtTitle.setText(Html.fromHtml(playModel.getName()));
            viewHolder.txtDesc.setText(Html.fromHtml(playModel.getDescription()));
            if(!playModel.getDays().equals("0")) {
                viewHolder.txtNoOfDays.setText(playModel.getDays() + " Day(s) Left");
            }
            // viewHolder.txtDesc.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.txtOfferName.setText(Html.fromHtml(playModel.getName()));
        viewHolder.txtOfferDescription.setText(Html.fromHtml(playModel.getDescription()));
        viewHolder.txtPoint1.setText(playModel.getTicketPoint() + " Point(s)");
        AppUtils.setImage(viewHolder.imgLogo1, playModel.getPromoImage());

        viewHolder.txtUserPoint.setText("Current Points : " + user_point);

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

        playModel.setQty(1);
        viewHolder.edProQuantity.setText("" + playModel.getQty());

        viewHolder.btnAddQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playModel.setQty(playModel.getQty() + 1);
                viewHolder.edProQuantity.setText("" + playModel.getQty());
            }
        });

        viewHolder.btnDeletQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playModel.getQty() != 1) {
                    playModel.setQty(playModel.getQty() - 1);
                    viewHolder.edProQuantity.setText("" + playModel.getQty());
                }
            }

        });

        viewHolder.txtToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TokensActivity.class);
                i.putExtra("promo_id", playModel.getId());
                context.startActivity(i);
            }
        });

        viewHolder.txtPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float point = playModel.getQty() * Float.parseFloat(playModel.getTicketPoint());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                // Setting Dialog Title
                alertDialog.setTitle("Confirmation");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to purchase " + playModel.getQty()
                        + " ticket(s) worth " + point + " points? amount is " +
                        "non-refundable");

                // On pressing Settings button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            getPromoTicket(playModel.getId(), playModel.getQty());
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
            if (current_date.after(playModel.getStartDate()) && current_date.before(playModel.getEndDate())) {
                viewHolder.txtPurchase.setVisibility(View.VISIBLE);
                viewHolder.btnAddQty.setVisibility(View.VISIBLE);
                viewHolder.btnDeletQty.setVisibility(View.VISIBLE);
                viewHolder.edProQuantity.setVisibility(View.VISIBLE);
            } else {
                viewHolder.txtPurchase.setVisibility(View.GONE);
                viewHolder.btnAddQty.setVisibility(View.INVISIBLE);
                viewHolder.btnDeletQty.setVisibility(View.INVISIBLE);
                viewHolder.edProQuantity.setVisibility(View.INVISIBLE);
            }
        }

        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }

    // View lookup cache
    private static class ViewHolder {
        RelativeLayout itemLayout;
        ImageView imgLogo;
        TextView txtTitle, txtDesc, txtPoint, txtNoOfDays;

        TextView txtOfferName, txtOfferDescription, txtPoint1, txtUserPoint, txtPurchase, txtToken;
        ImageView imgLogo1;
        ScrollView scrollContainer;
        PromoModel promo_data;
        ImageView btnAddQty, btnDeletQty;
        EditText edProQuantity;
    }

    private void getPromoTicket(String promo_id, int qty) throws UnsupportedEncodingException, JSONException {
        AppController.getPromoTicket(context, promo_id, qty, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                //Log.i("DataResponse", jsonResponse.toString());
                if (jsonResponse != null && jsonResponse.getInt("success") == 1) {
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//
//                    // Setting Dialog Message
//                    alertDialog.setMessage(jsonResponse.getString("message"));
//
//                    // On pressing Settings button
//                    alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            ((Activity) context).finish();
//                        }
//                    });
//                    // Showing Alert Message
//                    alertDialog.show();
                    ((Activity) context).finish();
                } else if (jsonResponse.getInt("success") == 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                    // Setting Dialog Message
                    alertDialog.setMessage(jsonResponse.getString("message"));

                    // On pressing Settings button
                    alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ((Activity) context).finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                    // Setting Dialog Message
                    alertDialog.setMessage(context.getString(R.string.system_error));

                    // On pressing Settings button
                    alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ((Activity) context).finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                }
            }
        });
    }
}

//        extends BaseAdapter {
//
//
//    private List<PromoModel> playModelList;
//    private LayoutInflater inflater;
//    private Context context;
//
//    public PlayItemAdapter(Context context, List<PromoModel> playModelList) {
//        this.context = context;
//        this.playModelList = playModelList;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount() {
//        return playModelList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return playModelList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        PromoModel playModel = (PromoModel) getItem(position);
//
//        ViewHolder viewHolder;
//
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//
//            convertView = inflater.inflate(R.layout.item_play_layout, null);
//
//            viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
//            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
//            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.txtDesc);
//            viewHolder.txtPoint = (TextView) convertView.findViewById(R.id.txtPoint);
//            viewHolder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.itemLayout);
//            viewHolder.txtNoOfDays = (TextView) convertView.findViewById(R.id.txtNoOfDays);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        viewHolder.itemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
//
//        try {
//            AppUtils.setImage(viewHolder.imgLogo, playModel.getPromoImage());
//
//            viewHolder.txtPoint.setText(playModel.getTicketPoint() + " Points");
//
//            viewHolder.txtTitle.setText(Html.fromHtml(playModel.getName()));
//            viewHolder.txtDesc.setText(Html.fromHtml(playModel.getDescription()));
//            if(!playModel.getDays().equals("0")) {
//                viewHolder.txtNoOfDays.setText(playModel.getDays() + " Day(s) Left");
//            }
//            // viewHolder.txtDesc.setMovementMethod(LinkMovementMethod.getInstance());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return convertView;
//    }
//
//    static class ViewHolder {
//        RelativeLayout itemLayout;
//        ImageView imgLogo;
//        TextView txtTitle, txtDesc, txtPoint, txtNoOfDays;
//    }
//
//}
//
