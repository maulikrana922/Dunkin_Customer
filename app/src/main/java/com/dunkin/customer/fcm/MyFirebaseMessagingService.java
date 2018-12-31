package com.dunkin.customer.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.RemoteViews;

import com.dunkin.customer.AppPaymentActivity;
import com.dunkin.customer.CounterOrderPaymentActivity;
import com.dunkin.customer.GiftDetailActivity;
import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.OfferDetailActivity;
import com.dunkin.customer.OfferPaymentActivity;
import com.dunkin.customer.OrderHistoryDetailActivity;
import com.dunkin.customer.PromoCodeDetailActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.SplashActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.service.LogoutService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Dunkin_Log.e("GCMResponse", message.toString());
        int counter = AppUtils.getAppPreference(this).getInt(AppConstants.NOTIFICATION_COUNTER, -1);
        SharedPreferences.Editor editor = AppUtils.getAppPreference(this).edit();
        editor.putInt(AppConstants.NOTIFICATION_COUNTER, counter + 1);
        editor.apply();
        Map data = message.getData();
        sendNotification(data, counter);
    }

    private void sendNotification(Map data, int counter) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("408", getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription( getString(R.string.app_name));
            channel.setShowBadge(false);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = null;
        String packageName = "com.dunkin.customer";

        if (data.get("msgtype") != null && data.get("msgtype").equals("1")) {
            Dunkin_Log.i("MsgType", data.get("msgtype").toString());
            intent = new Intent(this, AppPaymentActivity.class);
            intent.putExtra("order_id", data.get("orderId").toString());
            intent.putExtra("table_id", data.get("table_id").toString());
            intent.putExtra("restaurant_id", data.get("restaurant_id").toString());
            if (isForeground(packageName)) {
                intent.putExtra("isBackHome", false);
            } else {
                intent.putExtra("isBackHome", true);
            }
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("3")) {
            Dunkin_Log.i("MsgType", data.get("msgtype").toString());
            intent = new Intent(this, OrderHistoryDetailActivity.class);
            intent.putExtra("orderId", data.get("orderId").toString());
            if (isForeground(packageName)) {
                intent.putExtra("isBackHome", false);
            } else {
                intent.putExtra("isBackHome", true);
            }
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("4")) {
            Dunkin_Log.i("MsgType", data.get("msgtype").toString());
            intent = new Intent(this, CounterOrderPaymentActivity.class);
            intent.putExtra("orderId", data.get("orderId").toString());
            intent.putExtra("restaurant_name", data.get("restaurant_name").toString());
            if (isForeground(packageName)) {
                intent.putExtra("isBackHome", false);
            } else {
                intent.putExtra("isBackHome", true);
            }
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("5")) {
            Dunkin_Log.i("MsgType", data.get("msgtype").toString());
            intent = new Intent(this, OfferPaymentActivity.class);
            intent.putExtra("offerId", data.get("offerId").toString());
            intent.putExtra("country_id", data.get("country_id").toString());
            intent.putExtra("reference_id", data.get("reference_id").toString());
            if (isForeground(packageName)) {
                intent.putExtra("isBackHome", false);
            } else {
                intent.putExtra("isBackHome", true);
            }
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("6")) {
            Dunkin_Log.i("MsgType", data.get("msgtype").toString());
            intent = new Intent(this, OfferDetailActivity.class);
            intent.putExtra("offerId", data.get("offerId").toString());
            if (isForeground(packageName)) {
                intent.putExtra("isBackHome", false);
            } else {
                intent.putExtra("isBackHome", true);
            }
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("8")) {
            ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

            if (data.get("message").toString().equalsIgnoreCase("Appiness Gift Successfully Sent") && taskInfo.get(0).topActivity.getClassName().contains("HomeActivity")) {
                ((NewHomeActivity) NewHomeActivity.getCustomContext()).opendisalogofShare(data.get("message").toString());
            } else {

                intent = new Intent(this, SplashActivity.class);
                intent.putExtra("FromGCM", true);
                intent.putExtra("MsgType", 8);
            }
            //}
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("9")) {
            Dunkin_Log.i("MsgType", data.get("msgtype").toString());
            intent = new Intent(this, GiftDetailActivity.class);
            intent.putExtra("giftOrderId", data.get("purchaseId").toString());
            if (isForeground(packageName)) {
                intent.putExtra("isBackHome", false);
            } else {
                intent.putExtra("isBackHome", true);
            }
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("11")) {
            startService(new Intent(getApplicationContext(), LogoutService.class));
            Dunkin_Log.i("MsgType", data.get("msgtype").toString());

            intent = new Intent(getApplicationContext(), RegisterActivity.class);
            //Clear all activities and start new task
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("12")) {
            /*ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

            if (taskInfo.get(0).topActivity.getClassName().contains("HomeActivity")) {
                ((HomeActivity) HomeActivity.getCustomContext()).navigateAndCheckItem(AppConstants.MENU_WALLET);
            } else {*/
            intent = new Intent(this, SplashActivity.class);
            intent.putExtra("FromGCM", true);
            intent.putExtra("MsgType", 12);
            //}
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("13")) {
            intent = new Intent(this, PromoCodeDetailActivity.class);
            intent.putExtra("promoId", data.get("promoId").toString());
            if (isForeground(packageName)) {
                intent.putExtra("isBackHome", false);
            } else {
                intent.putExtra("isBackHome", true);
            }
        } else if (data.get("msgtype") != null && data.get("msgtype").equals("14")) {
            ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

            if (taskInfo.get(0).topActivity.getClassName().contains("HomeActivity")) {
                ((NewHomeActivity) NewHomeActivity.getCustomContext()).opendisalogofSharePointReceive(data.get("message").toString());
            } else {
                intent = new Intent(this, SplashActivity.class);
                intent.putExtra("FromGCM", true);
                intent.putExtra("MsgType", 14);
            }
            //}
        } else {
            if (isForeground(packageName)) {
                intent = new Intent(this, NewHomeActivity.class);
                intent.putExtra("FromGCM", true);
                intent.putExtra("isForeground", true);
                intent.putExtra("isNotMsgType", 1);
            } else {
                intent = new Intent(this, NewHomeActivity.class);
                intent.putExtra("FromGCM", true);
                intent.putExtra("isForeground", false);
                intent.putExtra("isNotMsgType", 1);
            }
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"408");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setContentTitle(getString(R.string.app_name));
            } else {
                notificationBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
                notificationBuilder.setSmallIcon(R.drawable.ic_notification);
                notificationBuilder.setContentTitle(getApplication().getString(R.string.app_name));
            }

            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
            contentView.setImageViewResource(R.id.notification_image, R.mipmap.ic_launcher);
            contentView.setTextViewText(R.id.notification_title, getApplication().getString(R.string.app_name));
            contentView.setTextViewText(R.id.notification_text, Html.fromHtml(data.get("message").toString()));
            notificationBuilder.setContent(contentView);

            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            //if(!data.getString("msgtype").equals("11"))
            notificationBuilder.setContentIntent(pendingIntent);

            Notification notification = notificationBuilder.build();

            if (data.containsKey("orderStatus")) {
                if (data.get("orderStatus") != null && data.get("orderStatus").toString().equalsIgnoreCase("Order Complete")) {
                    if (Build.VERSION.SDK_INT >= 16) {
                        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.custom_notification_expanded);
                        notification.bigContentView = expandedView;

                        expandedView.setImageViewResource(R.id.notification_image, R.mipmap.ic_launcher);
                        expandedView.setTextViewText(R.id.notification_title, getApplication().getString(R.string.app_name));
                        expandedView.setTextViewText(R.id.notification_text, Html.fromHtml(data.get("message").toString()));

                        Intent rateUsIntent = new Intent(this, SplashActivity.class);
                        rateUsIntent.putExtra("FromGCM", true);
                        rateUsIntent.putExtra("MsgType", AppConstants.MENU_FEEDBACK);

                        PendingIntent rateUsPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, rateUsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        expandedView.setOnClickPendingIntent(R.id.btnRateUs, rateUsPendingIntent);
                    }
                }
            }

            notificationManager.notify(counter /*ID of notification */, notification);
        }
    }

    public boolean isForeground(String PackageName) {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);
        ComponentName componentInfo = task.get(0).topActivity;
        if (componentInfo.getPackageName().equals(PackageName)) return true;
        return false;
    }
}
