package videos.religious.platform;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyMediaFile {
    public static File copyFile(String sourceLocation, String destLocation) {
        File source=new File(sourceLocation);
        File dest;
        dest=new File(destLocation);
        try {
            File sd = Environment.getExternalStorageDirectory();
            if(sd.canWrite()){
                if(!dest.exists()){
                    dest.createNewFile();
                }
                if(source.exists()){
                    InputStream src=new FileInputStream(source);
                    OutputStream dst=new FileOutputStream(dest);
                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = src.read(buf)) > 0) {
                        dst.write(buf, 0, len);
                    }
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception ex) {
            Log.d("canwrite", ex.getCause().getMessage());
            dest = source;
        }
        return dest;
    }
}
