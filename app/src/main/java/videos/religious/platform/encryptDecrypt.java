package videos.religious.platform;

import android.util.Log;

class encryptDecrypt {
    static String encryptString(String encrypt) {
        try {
            for(int i =0;i<=Constants.plainchar.size()-1;i++){
                if (encrypt.contains(""+Constants.plainchar.get(i))){
                    encrypt = encrypt.replace(""+Constants.plainchar.get(i),""+Constants.securechar.get(i));
                    Log.d("enrrrr", encrypt);
                }
            }
        } catch (Exception g)
        {
        }
        return encrypt;
    }
    static String decryptString(String decrypt){
        try {
            for(int i =0;i<=Constants.securechar.size()-1;i++){
                if (decrypt.contains(""+Constants.securechar.get(i))){
                    decrypt = decrypt.replace(""+Constants.securechar.get(i),""+Constants.plainchar.get(i));
                    Log.d("decrrrr", decrypt);
                }
            }
        } catch (Exception g)
        {
        }
        return decrypt;
    }
}