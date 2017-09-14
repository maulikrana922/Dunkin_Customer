package com.dunkin.customer.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.GPSTracker;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.RestaurantsModel;
import com.dunkin.customer.widget.MapWrapperLayout;
import com.dunkin.customer.widget.OnInfoWindowElemTouchListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NearRestaurantFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap gMap;
    Context context;
    MapWrapperLayout mapWrapperLayout;
    GPSTracker gps;
    RestaurantsModel rm = null;
    CallbackManager callbackManager;
    NearRestaurantFragment fragment;
    private Map<Marker, RestaurantsModel> markers;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private List<RestaurantsModel> restaurantsModelList;
    private OnInfoWindowElemTouchListener infoButtonListener, emailListener, shareListener;

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurent_on_map, container, false);

        callbackManager = CallbackManager.Factory.create();
        fragment = this;
        gps = new GPSTracker(context);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapWrapperLayout = (MapWrapperLayout) rootView.findViewById(R.id.map_relative_layout);

        mapFragment.getMapAsync(this);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Success", "Login");
                Log.d("ACCESS TOKEN", loginResult.getAccessToken().getToken());
                postOnFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.gMap = googleMap;
        mapWrapperLayout.init(gMap, getPixelsFromDp(context, 39 + 20));
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("latitude", String.valueOf(gps.getLatitude()));
            jsonRequest.put("longitude", String.valueOf(gps.getLongitude()));
            jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            AppController.nearByRestaurantsOnMap(context, jsonRequest.toString(), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    //Log.i("DataResponse", jsonResponse.toString());
                    restaurantsModelList = new ArrayList<>();
                    if (jsonResponse.getInt("success") == 1) {
                        restaurantsModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("restaurantList").toString(), new TypeReference<List<RestaurantsModel>>() {
                        });
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }
                    loadDataOnMap(restaurantsModelList);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void loadDataOnMap(List<RestaurantsModel> restaurantsModelList) {
        if (gMap != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gMap.setMyLocationEnabled(true);
            } else {
                gMap.setMyLocationEnabled(true);
            }

            markers = new HashMap<>();

            if (restaurantsModelList != null && restaurantsModelList.size() > 0) {
                for (RestaurantsModel vm : restaurantsModelList) {

                    LatLng loc = new LatLng(Double.parseDouble(vm.getLatitude()), Double.parseDouble(vm.getLongitude()));
                    Marker data = gMap.addMarker(new MarkerOptions().title(vm.getResturantName())
                            .position(loc));
                    markers.put(data, vm);
                }
            }

            zoomToLevel();

            this.infoWindow = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.custom_data_for_map, null);
            this.infoTitle = (TextView) infoWindow.findViewById(R.id.title);
            this.infoSnippet = (TextView) infoWindow.findViewById(R.id.snippet);
            ImageButton imbCall = (ImageButton) infoWindow.findViewById(R.id.imbCall);
            ImageButton imbShare = (ImageButton) infoWindow.findViewById(R.id.imbShare);
            ImageButton imbEmail = (ImageButton) infoWindow.findViewById(R.id.imbEmail);

            // Setting custom OnTouchListener which deals with the pressed state
            // so it shows up
            this.infoButtonListener = new OnInfoWindowElemTouchListener(imbCall,
                    ContextCompat.getDrawable(context, R.drawable.ic_map_phone),
                    ContextCompat.getDrawable(context, R.drawable.ic_map_phone)) {
                @Override
                protected void onClickConfirmed(View v, Marker marker) {
                    // Here we can perform some action triggered after clicking the button
                    rm = markers.get(marker);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + rm.getTelephone()));
                    startActivity(callIntent);
                }
            };
            imbCall.setOnTouchListener(infoButtonListener);

            this.emailListener = new OnInfoWindowElemTouchListener(imbEmail,
                    ContextCompat.getDrawable(context, R.drawable.ic_map_email),
                    ContextCompat.getDrawable(context, R.drawable.ic_map_email)) {
                @Override
                protected void onClickConfirmed(View v, Marker marker) {
                    rm = markers.get(marker);
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                    String[] recipients = new String[]{rm.getEmail(), "",};

                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);

                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");

                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

                    emailIntent.setType("message/rfc822");

                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            };
            imbEmail.setOnTouchListener(emailListener);

            shareListener = new OnInfoWindowElemTouchListener(imbShare,
                    ContextCompat.getDrawable(context, R.drawable.ic_map_facebook),
                    ContextCompat.getDrawable(context, R.drawable.ic_map_facebook)) {
                @Override
                protected void onClickConfirmed(View v, Marker marker) {
                    rm = markers.get(marker);
                    //LoginManager.getInstance().logInWithPublishPermissions(fragment, Arrays.asList("publish_actions"));
                    LoginManager.getInstance().logInWithPublishPermissions(fragment, Collections.singletonList("publish_actions"));
                }
            };
            imbShare.setOnTouchListener(shareListener);

            gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    // Setting up the infoWindow with current's marker info

                    RestaurantsModel rm = markers.get(marker);
                    if (rm != null) {
                        infoTitle.setText(marker.getTitle());
                        infoSnippet.setText(marker.getSnippet());
                        infoButtonListener.setMarker(marker);
                        emailListener.setMarker(marker);
                        shareListener.setMarker(marker);
                        mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                        return infoWindow;
                    }
                    return null;
                }
            });
        }
    }

    private void postOnFacebook(final AccessToken accessToken) {
        View promptsView = LayoutInflater.from(context).inflate(R.layout.restaurant_comment_dialog_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.al_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String data = rm.getResturantName() + "\n" + Html.fromHtml(rm.getAddress()) + "\n" + rm.getEmail() + "\n" + rm.getTelephone()
                                        + "\n Comments : " + userInput.getText().toString();

                                Bundle bundle = new Bundle();
                                bundle.putString("message", data);
                                bundle.putString("link", "http://www.ddlebanon.com");

                                GraphRequest request = new GraphRequest(accessToken, "/me/feed", bundle, HttpMethod.POST, new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        AppUtils.showToastMessage(context, getString(R.string.msg_share_success));
                                        LoginManager.getInstance().logOut();
                                    }
                                });

                                request.executeAsync();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.al_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void zoomToLevel() {
        Set<Marker> mark = new HashSet<>();

        if (markers != null && markers.size() > 0) {
            mark.addAll(markers.keySet());
        }

        LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
//        Marker mMarker = gMap.addMarker(new MarkerOptions()
////                .title("It's Me")
//                .position(latLng));
//
//        mark.add(mMarker);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : mark) {
            builder.include(marker.getPosition());
        }

        builder.include(latLng);

        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        gMap.animateCamera(cu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
