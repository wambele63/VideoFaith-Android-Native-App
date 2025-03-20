package videos.religious.platform;

import android.content.Context;

import io.objectbox.BoxStore;
import io.objectbox.BoxStoreBuilder;

public class ObjectBox {
    private static BoxStore mboxStore;

    public static void init(Context context) {
        mboxStore = BoxStore.getDefault();
    }
    public static BoxStore getBoxStore() { return mboxStore; }
}