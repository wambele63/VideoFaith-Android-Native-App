package videos.religious.platform;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Some utils methods.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class Utils {

    public static File getVideoCacheDir(Context context) {
        return new File(context.getExternalCacheDir(), "video-cache");
    }

    public static void cleanVideoCacheDir(Context context) throws IOException {
        File videoCacheDir = getVideoCacheDir(context);
        cleanDirectory(videoCacheDir);
    }

    private static void cleanDirectory(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        File[] contentFiles = file.listFiles();
        if (contentFiles != null) {
            for (File contentFile : contentFiles) {
                delete(contentFile);
            }
        }
    }

    private static void delete(File file) {
        if (file.isFile() && file.exists()) {
            deleteOrThrow(file);
        } else {
            try {
                cleanDirectory(file);
                deleteOrThrow(file);
            }catch (IOException v){

            }
        }
    }

    private static void deleteOrThrow(File file) {
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                Log.d("dsdsd",String.format("File %s can't be deleted", file.getAbsolutePath()));
            }
        }
    }
}