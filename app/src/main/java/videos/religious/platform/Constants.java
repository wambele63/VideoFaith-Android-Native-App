package videos.religious.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Constants {
    private static final String KEY_RESTART_INTENT = "RESTART";
    static String FIREBASE_TOKEN = "";
    public static String myId = "";
    static String myFullname = "";
    static String myChannelName = "";
    static String myChannelProfile = "";
    static String reserveprofile = "";
    static String UpdateVideoText = "";
    static String reservechannelname = "";
    static String myChannelCover = "";
    static String myReligion="";
    static String myCountry = "";
    static String phone = "";
    static boolean STATUS = false;
    static String currenttime = "";
    private static String FCM_PUSH_URL = "https://fcm.googleapis.com/fcm/send";
    static String currentime = "";
    static boolean PRO_USER = false;
    static String[] plainCha= new String[]{"t","u","v","w","x","y","z","g","h","i","j","k","a","b","c","d","e","f","l","m","n","o","p","q","r","s"
            ,"5","6","7","8","9","@",".","2","3","4"};
    static List plainchar = Arrays.asList(plainCha);
    static List securechar = new ArrayList<String>();
    public static final String OfflineVideoUri = "OfflineVideos";

    static void sendFCMPush(Context mContext,String title, String body, String image,String topicgot, String SERVER_KEY) {
        String topic = "/topics/"+topicgot;

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", body);
            objData.put("title", title);
            objData.put("sound", "default");
            objData.put("image", image); //   icon_name image must be there in drawable
            objData.put("tag", topic);
            objData.put("priority", "high");

            dataobjData = new JSONObject();
            dataobjData.put("body", body);
            dataobjData.put("title", title);
            dataobjData.put("sound","default");
            dataobjData.put("image", image);

            obj.put("to", topic);
            //obj.put("priority", "high");

            obj.put("notification", objData);
            obj.put("data", dataobjData);
            Log.e("!_@rj@_@@_PASS:>", obj.toString());

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_PUSH_URL, obj,
                    response -> {
                        Log.e("!_@@_SUCESSkk", response + "");
                    },
                    error -> Log.e("!_@@_Errorskk--", error + "")) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "key=" + SERVER_KEY);
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            int socketTimeout = 1000 * 120;// 120 seconds
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsObjRequest.setRetryPolicy(policy);
            requestQueue.add(jsObjRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void triggerRebirth(Context context, Intent nextIntent) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_RESTART_INTENT, nextIntent);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }

        Runtime.getRuntime().exit(0);
    }
}