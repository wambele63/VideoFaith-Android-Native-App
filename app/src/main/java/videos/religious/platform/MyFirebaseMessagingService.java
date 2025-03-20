package videos.religious.platform;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.internal.view.SupportMenu;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String ADMIN_CHANNEL_ID = "videofaith_1";
    private static final String IMAGE_URL_EXTRA = "imageUrl";
    private static final String NOTIFICATION_ID_EXTRA = "notificationId";
    private NotificationManager notificationManager;
    public Intent intent;
    public String buttontitle;

    public static class Helper {
        public static boolean isAppRunning(Context context, String str) {
            List<RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses();
            if (runningAppProcesses != null) {
                for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                    if (runningAppProcessInfo.processName.equals(str)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public void onNewToken(String s) {
        Log.d("wambura", s);
        SharedPreferences preferences = getSharedPreferences("channelInfo", Context.MODE_PRIVATE);
        //Assigning local details to variables
        SharedPreferences.Editor edittoken = preferences.edit();
        edittoken.putString("firebase_token", s);
        edittoken.apply();
        if (!Constants.myId.equals("") && s.length() > 0) {
            FirebaseFirestore.getInstance().collection("channels").document(Constants.myId).update("tokens", s);
        }
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        sendNotification(notification, data);
    }

    /**
     * Create and show a custom notification containing the received FCM message.
     *
     * @param notification FCM notification payload received.
     * @param data FCM data payload received.
     */
    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        if (data.size() > 0) {
            sendNotifications(data);
        }
        if (notification != null) {
            data.clear();
            data.put("title", notification.getTitle());
            data.put("body", notification.getBody());
            data.put("image", "" + notification.getImageUrl());
            sendNotifications(data);
        }
    }

    private int bundleReady = 1;
    private int singleNotificationId = 100;
    Intent resultIntent;
    PendingIntent resultPendingIntent;

    @SuppressLint("RestrictedApi")
    private void sendNotifications(Map<String, String> data) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int bundleNotificationId = 100;
        Bitmap myBitmap = getBitmapfromUrl(data.get("image"));
        NotificationCompat.BigPictureStyle bpStyle = new NotificationCompat.BigPictureStyle();
        bpStyle.setBigContentTitle(data.get("body"));
        bpStyle.bigPicture(myBitmap);
        NotificationCompat.InboxStyle bpStyleInbox = new NotificationCompat.InboxStyle();
        String bundle_notification_id = "bundle_notification_" + bundleNotificationId;
        resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("notification", "Welcome Again ");
        resultIntent.putExtra("notification_id", Constants.myFullname);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //We need to update the bundle notification every time a new notification comes up.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(ADMIN_CHANNEL_ID, "VideoFaith", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("VideoFaith");
            notificationChannel.setLightColor(SupportMenu.CATEGORY_MASK);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                .setContentInfo(data.get("body"))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_radio, data.get("body"), resultPendingIntent)
                .setLargeIcon(myBitmap)
                .setColorized(true)
                .setSound(
                        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                File.pathSeparator + File.separator + getPackageName() + "/raw/notification"))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setStyle(bpStyle)
                .setSmallIcon(R.drawable.ic_live_tv_black_24dp)
                .setContentIntent(resultPendingIntent);
        if (Build.VERSION.SDK_INT > 24) {
            NotificationCompat.Builder summaryNotificationBuilder2 = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                    .setGroup("FEEDS")
                    .setGroupSummary(true)
                    .setContentTitle("VideoFaith Feeds ")
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentText("New " + Constants.myReligion.toUpperCase() + " Videos And Headlines Available ")
                    .setSmallIcon(R.drawable.ic_live_tv_black_24dp)
                    .setChannelId(ADMIN_CHANNEL_ID)
                    .setSound(
                            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                    File.pathSeparator + File.separator + getPackageName() + "/raw/notification"))
                    .setContentIntent(resultPendingIntent);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(bundleNotificationId, summaryNotificationBuilder2.build());
            return;
        }
        notificationManager.notify(getIntd(), notification.build());
    }
    private int getIntd(){
        String v = System.currentTimeMillis()+"";
        v = v.substring(v.length() - 4);
        return Integer.parseInt(v);
    }
    private int cid = 1;
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}