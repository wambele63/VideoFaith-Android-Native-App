package videos.religious.platform;

import android.animation.Animator;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import io.objectbox.Box;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditAccount.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditAccount extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String fileCoverToUpload ="", FileProfileToUpload = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView pro, cov;
    private ImageView coverpic;
    private IOnBackPressed iOnBackPressed;
    private ImageView profilepic;
    private View view;
    private Box<User> userbox = ObjectBox.getBoxStore().boxFor(User.class);
    private EditText channelname, accountname, phonenumber, email, password;
    private OnFragmentInteractionListener mListener;
    private TextView faberror;
    private File dest, source;
    private static int BackStatus = 0;
    private final int UCROP_COVER_REQUEST_CODE = 4;
    private final int UCROP_PROFILE_REQUEST_CODE = 5;

    public EditAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Preferrences.
     */
    // TODO: Rename and change types and number of parameters
    public static EditAccount newInstance(String param1, String param2) {
        EditAccount fragment = new EditAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_account, container, false);
        profilepic = view.findViewById(R.id.eprofileedit);
        coverpic = view.findViewById(R.id.eprofilecover);
        channelname = view.findViewById(R.id.echannelEditName);
        accountname = view.findViewById(R.id.eAccountName);
        email = view.findViewById(R.id.eEmail);
        phonenumber = view.findViewById(R.id.ePhoneNumber);
        password = view.findViewById(R.id.ePassword);
        Glide.with(profilepic).load(Constants.myChannelProfile).circleCrop().fitCenter().into(profilepic);
        Glide.with(coverpic).load(Constants.myChannelCover).into(coverpic);
        return view;
    }
    public void onSingleImageSelected(Uri uri, String tag) {
        //Do something with your Uri
        String fulluriPath = GetFilePathFromDevice.getPath(getContext(), uri);
        String filePath2, destloc, filedec;
        switch (tag) {
            case "profile":
                filePath2 = getContext().getCacheDir() + "/" + "IMG_WAMBURA_" + (System.currentTimeMillis()) + ".jpeg";
                destloc = getContext().getCacheDir() + "/" + "IMG_CHTUBE_" + (System.currentTimeMillis()) + ".jpeg";
                File decodedfile = copyFile(fulluriPath, filePath2);
                filedec = decodedfile.getAbsolutePath();
                getDecodedProfileImg(filedec);
                break;
            case "cover":
                filePath2 = getContext().getCacheDir() + "/" + "IMG_WAMBURA_" + (System.currentTimeMillis()) + ".jpeg";
                destloc = getContext().getCacheDir() + "/" + "IMG_CHTUBE_" + (System.currentTimeMillis()) + ".jpeg";
                decodedfile = copyFile(fulluriPath, filePath2);
                filedec = decodedfile.getAbsolutePath();
                getDecodedCoverImg(filedec);
                break;
        }
    }
        public void loadImage(File imageFile, ImageView ivImage){
            //Glide is just an example. You can use any image loading library you want;
            Glide.with(EditAccount.this).load(imageFile).skipMemoryCache(true).into(ivImage);
        }
        private void uploadCover (String path){
            File file = new File(path);
        }
        @Override
        public void onViewCreated (@NonNull View view,
                Bundle savedInstanceState){
            super.onViewCreated(view, savedInstanceState);
            TextView coverpicedit = view.findViewById(R.id.eprofilecovereditable);
            TextView imagepic = view.findViewById(R.id.eprofileeditbtn);
            imagepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //fire image new picker
                }
            });
            coverpicedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //fire cover image picker
                }
            });
        }
    public void getDecodedCoverImg(final String filetodecode){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(85);
        options.setToolbarTitle("Crop Profile Cover");
        options.setHideBottomControls(true);
        options.setActiveControlsWidgetColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorLightAccentAds));
        options.setImageToCropBoundsAnimDuration(600);
        options.setDimmedLayerColor(getResources().getColor(R.color.colorPrimaryDiluted));
        String filePath2 = getContext().getCacheDir() + "/" + "IMG_COVER_WAMBURA_" + (System.currentTimeMillis()) + ".jpeg";
        UCrop.of(Uri.fromFile(new File(filetodecode)),Uri.fromFile(new File(filePath2)))
                .withAspectRatio(3, 2)
                .withOptions(options)
                .withMaxResultSize(640, 426)
                .start(getContext(), EditAccount.this,UCROP_COVER_REQUEST_CODE);
    }
    public void getDecodedProfileImg(final String filetodecode){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(75);
        options.setToolbarTitle("Crop Profile Pic");
        options.setHideBottomControls(true);
        options.setActiveControlsWidgetColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorLightAccentAds));
        options.setImageToCropBoundsAnimDuration(600);
        options.setDimmedLayerColor(getResources().getColor(R.color.colorPrimaryDiluted));
        String filePath2 = getContext().getCacheDir() + "/" + "IMG_PRO_WAMBURA_" + (System.currentTimeMillis()) + ".png";
        UCrop.of(Uri.fromFile(new File(filetodecode)),Uri.fromFile(new File(filePath2)))
                .withAspectRatio(1, 1)
                .withOptions(options)
                .withMaxResultSize(360, 360)
                .start(getContext(), EditAccount.this,UCROP_PROFILE_REQUEST_CODE);
    }
    private void audioPlayer(int path){
        MediaPlayer mp = MediaPlayer.create(this.getContext(), path);
        mp.setVolume(.2f,.2f);
        mp.start();
    }
    private void slideInLeft(final View view){
        YoYo.with(Techniques.SlideInLeft).duration(300).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }
        }).playOn(view);
    }
    private void slideInRight(final View view){
        YoYo.with(Techniques.SlideInRight).duration(300).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }
        }).playOn(view);
    }
    private void slideOutRight(final View view){
        YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                view.setVisibility(GONE);
            }
        }).playOn(view);
    }
    private void slideOutLeft(final View view){
        YoYo.with(Techniques.SlideOutLeft).duration(300).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                view.setVisibility(GONE);
            }
        }).playOn(view);
    }
    private ImageButton fabloading;
    private Button fab;
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        fab = view.findViewById(R.id.efabsave);
        final Button next1 = view.findViewById(R.id.efabnext1);
        final Button next2 = view.findViewById(R.id.efabnext2);
        final Button prev1 = view.findViewById(R.id.efabprev1);
        final Button prev2 = view.findViewById(R.id.efabprev2);
        final RelativeLayout startpage = view.findViewById(R.id.estartpage);
        final RelativeLayout nextpage1 = view.findViewById(R.id.eeditdetailsonly);
        fabloading = view.findViewById(R.id.efabloading);
        faberror = view.findViewById(R.id.efaberror);

        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideOutLeft(startpage);
                slideInRight(nextpage1);
                slideOutLeft(next1);
                slideInRight(fab);
                slideInRight(prev1);
            }
        });
        prev1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideInLeft(startpage);
                slideOutRight(nextpage1);
                slideInLeft(next1);
                slideOutRight(prev1);
                slideOutRight(fab);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Glide.with(getContext()).asGif().load(R.drawable.searching).circleCrop().into(fabloading);
                    fabloading.setVisibility(View.VISIBLE);
                    fab.setVisibility(GONE);
                    syncData();
           }
        });
    }
    private FirebaseStorage dbStore = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference pRefs,sRefs;
    private String newChannelName="";
    private String newAccoutName="";
    private void syncData(){
        try {
            //extra data
            newChannelName = channelname.getText().toString();
            newAccoutName = accountname.getText().toString();
                String[] filestrings = new String[]{FileProfileToUpload, fileCoverToUpload};
               if(!newChannelName.equals("")) {
                   db.collection("channels").whereEqualTo("channelname",newChannelName).limit(1).get()
                           .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                               @Override
                               public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                   for(DocumentSnapshot document : queryDocumentSnapshots){
                                       if(document.getData().size() > 0){
                                           Snackbar.make(view, "Channel Name Already Taken", BaseTransientBottomBar.LENGTH_LONG);
                                       return;
                                       }
                                   }
                               }
                           });
               }
               String videofiles = System.currentTimeMillis()+"";
               if(fileCoverToUpload != ""){
                   final Uri profile = Uri.fromFile(new File(fileCoverToUpload));
                    pRefs = dbStore.getReference().child("channels/" + videofiles + "/" + profile.getLastPathSegment());
                   UploadTask uploadTask = pRefs.putFile(profile);
                   uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                       @Override
                       public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           if (!task.isSuccessful()) {
                               return null;
                           }
                           return pRefs.getDownloadUrl();
                       }
                   }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                       @Override
                       public void onComplete(@NonNull Task<Uri> task) {
                           if (task.isSuccessful()) {
                               Log.d("proooooo", "posoooo succ");
                               firestorecoverurl = task.getResult().toString();
                               storeprofile();
                           }
                       }
                   });
               }else {
                   storeprofile();
               }
                audioPlayer(R.raw.save);
                return;
        }catch (Exception exp) {
            Snackbar.make(view, "Internal Error try again!!!", Snackbar.LENGTH_LONG).show();
        }
    }
    private String firestorecoverurl="";
    private String firestoreprofileurl = "";
    private void storeprofile(){
        if(!FileProfileToUpload.equals("")){
            String videofiles= System.currentTimeMillis()+"";
            final Uri profile = Uri.fromFile(new File(FileProfileToUpload));
            sRefs = dbStore.getReference().child("channels/" + videofiles + "/" + profile.getLastPathSegment());
            UploadTask uploadTask = pRefs.putFile(profile);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                        return null;
                    }
                    return pRefs.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Log.d("proooooo", "posoooo succ");
                        firestoreprofileurl = task.getResult().toString();
                        savefirestore();
                    }
                }
            });
        }
        else {
            savefirestore();
        }
    }
    private void savefirestore() {
        Map<String, Object> editmap = new HashMap<>();
        if (!firestorecoverurl.equals("")) {
            editmap.put("cover", firestorecoverurl);
        }
        if (!firestoreprofileurl.equals("")) {
            editmap.put("profile", firestoreprofileurl);
        }
        if (!newAccoutName.equals("")) {
            editmap.put("accountname", newAccoutName);
        }
        if (!newChannelName.equals("")) {
            editmap.put("channelname", newChannelName);
        }
        if (editmap.size() == 0) {
            return;
        }
        db.collection("channels").document(Constants.myId).update(editmap);
                if (!firestoreprofileurl.equals("")) {
                    deleteOldProfile();
                }
                if (!firestorecoverurl.equals("")) {
                    deleteOldCover();
                }
                Toast.makeText(getContext(), "Updating...Please Wait", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(()->{
                    updateSharedPreferenceAccordingly();
                },15000);
    }
    private void updateSharedPreferenceAccordingly(){
        SharedPreferences channelinfo = getActivity().getSharedPreferences("channelInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = channelinfo.edit();
        if (!firestorecoverurl.equals("")) {
            editor.putString("cover", firestorecoverurl);
            Constants.myChannelCover = firestorecoverurl;
        }
        if (!firestoreprofileurl.equals("")) {
            editor.putString("profile", firestoreprofileurl);
            editor.putString("reserveprofile", firestoreprofileurl);
            Constants.myChannelProfile = firestoreprofileurl;
        }
        if (!newAccoutName.equals("")) {
            editor.putString("accountname", newAccoutName);
        }
        if (!newChannelName.equals("")) {
            editor.putString("channelname", newChannelName);
            editor.putString("reservename", newChannelName);
            Constants.myChannelName = newChannelName;
            Constants.reservechannelname = newChannelName;
        }
        editor.apply();
        fabloading.setVisibility(GONE);
        fab.setVisibility(View.VISIBLE);
        Snackbar.make(view, "Account Updated ", BaseTransientBottomBar.LENGTH_LONG).show();
        getActivity().finish();
    }
    private void deleteOldProfile(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.myChannelProfile);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Constants.myChannelProfile = firestoreprofileurl;
                Log.e("firebasestorage", "onSuccess: deleted file");
            }
        }).addOnFailureListener(exception -> {
            // Uh-oh, an error occurred!
            Constants.myChannelProfile = firestoreprofileurl;
            Log.e("firebasestorage", "onFailure: did not delete file");
        });
    }
    private void deleteOldCover(){
                if(!firestorecoverurl.equals("")){
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.myChannelCover);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Constants.myChannelCover = firestorecoverurl;
                Log.e("firebasestorage", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Constants.myChannelCover = firestorecoverurl;
                Log.e("firebasestorage", "onFailure: did not delete file");
            }
        });
    }
    }
    private void saveLocally(String response){
        try {
            JSONArray dataArray = new JSONArray(response);
                JSONObject post = dataArray.getJSONObject(0);
                String channelname = post.getString("channelname");
                String accountname = post.getString("personalname");
                String channelid = post.getString("channelid");
                String channelprofile = post.getString("cprofile");
                String channelcover = post.getString("ccover");
                String clocation = post.getString("clocation");
            SharedPreferences channelinfo = getActivity().getApplicationContext().getSharedPreferences("channelInfo",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = channelinfo.edit();
            editor.putString("channelname", channelname)
                    .putString("channelid",channelid)
                    .putString("accountname",accountname)
                    .putString("cprofile",channelprofile)
                    .putString("ccover",channelcover)
                    .putString("clocation",clocation).apply();
            editor.apply();
            getActivity().finish();
        } catch (Exception jsonerror) {
        }
    }
    private void fillLocally(String response){
        try {
            JSONArray dataArray = new JSONArray(response);
                JSONObject post = dataArray.getJSONObject(0);
                String echannelname = post.getString("channelname");
                String eaccountname = post.getString("personalname");
                String echannelid = post.getString("channelid");
                String echannelprofile = post.getString("cprofile");
                String echannelcover = post.getString("ccover");
                String eclocation = post.getString("clocation");
                channelname.setText(echannelname);
                accountname.setText(eaccountname);
                Glide.with(profilepic).load(echannelprofile).circleCrop().into(profilepic);
            Glide.with(coverpic).load(echannelcover).into(coverpic);
        } catch (Exception jsonerror) {
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            try {
                iOnBackPressed = (IOnBackPressed) context;
            }catch (ClassCastException cl){

            }
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        BackStatus = 0;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public File copyFile(String sourceLocation, String destLocation) {
        source = new File(sourceLocation);
        dest = new File(destLocation);
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
                }else {
                    Glide.with(EditAccount.this.getContext()).load(sourceLocation).into(coverpic);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
           Log.d("eeeeeeee",ex.toString());
           dest = source;
        }
        return dest;
    }
    public static final class GetFilePathFromDevice {
        /**
         * Get file path from URI
         *
         * @param context context of Activity
         * @param uri     uri of file
         * @return path of given URI
         */
        public static String getPath(final Context context, final Uri uri) {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }

        public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        public static  boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        public static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }
    }
    public void onBackPressed(){
        iOnBackPressed.check("No");
    }
    private static String allow;
    public boolean AllowBack(){
        chec checs = new chec();
        allow = checs.checkd();
        if(allow.equals("No")) {
            return false;
        }
        else return true;
    }
private class chec {
        public String checkd(){
            if(BackStatus == 0){
                return "No";
            }
            return "Yes";
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        final Uri resultUri;
        switch (requestCode)
        {
            case UCROP_COVER_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    resultUri = UCrop.getOutput(data);
                    fileCoverToUpload = GetFilePathFromDevice.getPath(getContext(), resultUri);
                    Glide.with(getContext()).load(fileCoverToUpload).fitCenter().into(coverpic);
                }else if(resultCode == UCrop.RESULT_ERROR){
                    Toast.makeText(getContext(), "Error Getting Photo", Toast.LENGTH_SHORT).show();
                }
                    break;
            case UCROP_PROFILE_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    resultUri = UCrop.getOutput(data);
                    FileProfileToUpload = GetFilePathFromDevice.getPath(getContext(), resultUri);
                    Glide.with(getContext()).load(FileProfileToUpload).fitCenter().circleCrop().into(profilepic);
                }else if(resultCode == UCrop.RESULT_ERROR){
                    Toast.makeText(getContext(), "Error Getting Photo", Toast.LENGTH_SHORT).show();
                }
                    break;
                }
    }
}