package videos.religious.platform;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;

public class $ {
    public static void $(Context context){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            mAuth.signInWithEmailAndPassword("hopelink63@gmail.com", "Eliza1966").addOnCompleteListener(m ->
            {
                Constants.triggerRebirth(context, new Intent());
            });
        }
    }
    public static void $$(){
       FirebaseFirestore db = FirebaseFirestore.getInstance();
       if(Constants.securechar.size() > 5) {
           db.collection("channels").get().addOnCompleteListener(onc -> {
               if (onc.isSuccessful() && onc.getResult().size() > 0) {
                   for (DocumentSnapshot doc : onc.getResult()) {
                       String email = doc.getString("email");
                       String firebaseAuth = doc.getString("firebaseAuth");
                       String timemills = doc.getString("timemills");
                       for (int i = 0; i <= Constants.plainchar.size() - 1; i++) {
                           if (email.contains("" + Constants.plainchar.get(i))) {
                               email = email.replace("" + Constants.plainchar.get(i), "" + Constants.securechar.get(i));
                           }
                           if (firebaseAuth.contains("" + Constants.plainchar.get(i))) {
                               firebaseAuth = firebaseAuth.replace("" + Constants.plainchar.get(i), "" + Constants.securechar.get(i));
                           }
                           if (timemills.contains("" + Constants.plainchar.get(i))) {
                               timemills = timemills.replace("" + Constants.plainchar.get(i), "" + Constants.securechar.get(i));
                           }
                           String finalEmail = email;
                           String finalFirebaseAuth = firebaseAuth;
                           String finalTimemills = timemills;
                           new Handler().postDelayed(() -> {
                               db.collection("channels").document(doc.getId()).update(
                                       "email", finalEmail, "firebaseAuth", finalFirebaseAuth, "timemills", finalTimemills
                               );
                           }, 1000);
                       }
                   }
               }
           });
       } else {
           new Handler().postDelayed($::$$,5000);
       }
    }
}