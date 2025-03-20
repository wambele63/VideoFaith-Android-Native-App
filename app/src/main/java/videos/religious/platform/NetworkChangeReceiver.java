package videos.religious.platform;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {
    ConnectionChangeCallback connectionChangeCallback;

    public interface ConnectionChangeCallback {
        void onConnectionChange(boolean z);
    }

    public void onReceive(Context context, Intent intent) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean z = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        ConnectionChangeCallback connectionChangeCallback2 = this.connectionChangeCallback;
        if (connectionChangeCallback2 != null) {
            connectionChangeCallback2.onConnectionChange(z);
        }
    }

    public void setConnectionChangeCallback(ConnectionChangeCallback connectionChangeCallback2) {
        this.connectionChangeCallback = connectionChangeCallback2;
    }
}
