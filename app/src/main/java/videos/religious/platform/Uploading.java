package videos.religious.platform;

import android.animation.Animator;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.widget.NestedScrollView;
import androidx.emoji2.widget.EmojiEditText;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yalantis.ucrop.UCrop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Uploading.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Uploading#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Uploading extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private ImageView imageposter;
    private View inflate;
    private static final int GET_POSTER = 15;
    private File source,dest;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String lastMparam;
    private String mParam1,mParam2,videotext="",videodescription="",duration,userid,channelid,videoToUpload,mPosterToUpload;
    private OnFragmentInteractionListener mListener;
    private int finalwidth,finalheight;
    private PlayerView videoView;
    private SimpleExoPlayer exoPlayer;
    double videosize;
    public Uploading() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Uploading.
     */
    // TODO: Rename and change types and number of parameters
    public static Uploading newInstance(String param1, String param2) {
        Uploading fragment = new Uploading();
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
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
        }
        j = Snackbar.make(getActivity().getWindow().getDecorView(), "Confirm Posting To Different Religion!",BaseTransientBottomBar.LENGTH_INDEFINITE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.upload_new_video,container,false);
        imageposter = inflate.findViewById(R.id.postertoupload);
       initplayer();
        initVideosUploadPage();
        return inflate;
    }
    private void initplayer(){
        videoView = inflate.findViewById(R.id.videotopost);
        exoPlayer = new SimpleExoPlayer.Builder(getContext()).build();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "VideoFaith"));
// This is the MediaSource representing the media to be played.
        MediaSource videoSource =
                new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(mParam1));
// Prepare the player with the source.
        videoView.setPlayer(exoPlayer);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare(videoSource);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private RelativeLayout promptView;
    private void promptUser(String message, int tag) {
        TextView messageTextView= inflate.findViewById(R.id.messageTextView);
        messageTextView.setText(message);
    }
    private void startSendingService(){
        Intent intentDecode;
        intentDecode = new Intent(getActivity(), backjob.class);
        intentDecode.putExtra("videodescription",videodescription);
        intentDecode.putExtra("videotodecode",mParam1);
        intentDecode.putExtra("postersmall",postersmall);
        intentDecode.putExtra("videotext",videotext);
        intentDecode.putExtra("postertoupload",mPosterToUpload);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(intentDecode);
        } else {
            getActivity().startService(intentDecode);
        }
        getActivity().finish();
    }
    Snackbar j;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPoster(mParam1);
            final Button next = inflate.findViewById(R.id.nextUpload);
            final Button send = inflate.findViewById(R.id.sendUpload);
            final Button previous = inflate.findViewById(R.id.previouspics);
            final Button discard = inflate.findViewById(R.id.discard);
            Button okDiscard= inflate.findViewById(R.id.okDiscard);
            Button noDiscard = inflate.findViewById(R.id.noDiscard);
            promptView = inflate.findViewById(R.id.promptView);
            okDiscard.setOnClickListener(view->{
                getActivity().finish();
            });
            noDiscard.setOnClickListener(view->{
                YoYo.with(Techniques.RubberBand).duration(400).onEnd(onstar->{
                    promptView.setVisibility(View.GONE);
                }).playOn(promptView);
            });
        final EmojiEditText addvideotext = inflate.findViewById(R.id.addvideotext);
            discard.setOnClickListener(  v-> {
                YoYo.with(Techniques.RubberBand).duration(400).onStart(onstar -> {
                    promptView.setVisibility(View.VISIBLE);
                }).playOn(promptView);
            });
            send.setOnClickListener(view -> {
                videotext = addvideotext.getText().toString();
                if(videotext.equals("")) {
                    addvideotext.requestFocus();
                    return;
                }
                if(videodescription.equals("")) {
                    Toast.makeText(getContext(), "Select Target Religion", Toast.LENGTH_LONG).show();
                    videodescription = Constants.myReligion;
                    return;
                }
                if(!videodescription.equals(Constants.myReligion) &&
                        Constants.myChannelName.toLowerCase().contains(Constants.myReligion)){
                    Snackbar.make(inflate, "Your Channel Name Does Not Allow You Sending To Different religion! Has Strict Religional word", BaseTransientBottomBar.LENGTH_LONG).show();
                    return;
                }
                if(!Constants.myReligion.equals(videodescription)){
                    j.show();
                    YoYo.with(Techniques.SlideInUp).duration(600).onStart(d->{
                        warningText.setVisibility(View.VISIBLE);
                    }).playOn(warningText);
                    j.setAction("Post",n->{
                       j.dismiss();
                       warningText.setVisibility(View.GONE);
                       startSendingService();
                    });
                    return;
                }
                startSendingService();
            });
            final RelativeLayout texts = getActivity().findViewById(R.id.textscontainer);
            final RelativeLayout pics = getActivity().findViewById(R.id.firstcontainer);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Videotext = Uri.fromFile(new File(mParam1)).getLastPathSegment();
                    Videotext = Videotext.replaceAll("_"," ").replace(".mp4","").replace(".MP4","");
                    addvideotext.setText(Videotext);
                    YoYo.with(Techniques.ZoomOutLeft).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            pics.setVisibility(View.GONE);
                            previous.setEnabled(true);
                            exoPlayer.setPlayWhenReady(false);
                        }
                    }).duration(200).playOn(pics);
                    YoYo.with(Techniques.SlideInRight).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            texts.setVisibility(View.VISIBLE);
                        }
                    }).duration(600).playOn(texts);
                    YoYo.with(Techniques.FlipOutX).duration(300).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            next.setVisibility(View.GONE);
                        }
                    }).playOn(next);
                    YoYo.with(Techniques.FlipInX).duration(600).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            send.setVisibility(View.VISIBLE);
                        }
                    }).playOn(send);
                }
            });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.SlideOutRight).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        texts.setVisibility(View.GONE);
                    }
                }).duration(300).playOn(texts);
                YoYo.with(Techniques.ZoomInLeft).duration(200).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        pics.setVisibility(View.VISIBLE);
                        previous.setEnabled(false);
                        exoPlayer.setPlayWhenReady(true);
                    }
                }).playOn(pics);
                YoYo.with(Techniques.FlipInX).duration(500).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        next.setVisibility(View.VISIBLE);
                    }
                }).playOn(next);
                YoYo.with(Techniques.FlipOutX).duration(300).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        send.setVisibility(View.GONE);
                    }
                }).playOn(send);
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onDestroy() {
        if(null != exoPlayer){
            exoPlayer.release();
        }
        super.onDestroy();
    }
    @Override
    public void onStop() {
        if(null != exoPlayer){
            exoPlayer.setPlayWhenReady(false);
        }
        super.onStop();
    }
    @Override
    public void onPause() {
        if(null != exoPlayer){
            exoPlayer.setPlayWhenReady(false);
        }
        super.onPause();
    }
    @Override
    public void onResume() {
        if(null != exoPlayer){
            exoPlayer.setPlayWhenReady(true);
        }
        super.onResume();
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void initVideosUploadPage(){
        final Button selectVideo = inflate.findViewById(R.id.videouploadbtn);
        Button imagepic = inflate.findViewById(R.id.customposter);
        final NestedScrollView scrollView = inflate.findViewById(R.id.scrollview);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            /* get the maximum height which we have scroll before performing any action */
            int maxDistance = imageposter.getHeight()+100;
            /* how much we have scrolled */
            int movement = scrollView.getScrollY();
            /*finally calculate the alpha factor and set on the view */
            float alphaFactor = ((movement * 1.0f) / (maxDistance));
            if (movement >= 0) {
                /*for image parallax with scroll */
                imageposter.setTranslationY(-movement/2);
                /* set visibility */
            }
        });
        imagepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //fire pro image;
            }
        });
        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.RubberBand).duration(100).playOn(selectVideo);
                startPicker();
            }
        });
        final FloatingActionButton upload = inflate.findViewById(R.id.uploadVideo);
        upload.setOnClickListener(view -> {
            YoYo.with(Techniques.RubberBand).duration(100).playOn(upload);
            final String destloc = getContext().getCacheDir() + "/" + "VID_MFAITH_" + (System.currentTimeMillis()) + ".mp4";

        });
        TextView christian = inflate.findViewById(R.id.vchristian);
        TextView other = inflate.findViewById(R.id.vother);
        warningText = inflate.findViewById(R.id.warningText);
        TextView islam = inflate.findViewById(R.id.vislam);

        christian.setOnClickListener(view -> {
            christian.setTextColor(getResources().getColor(R.color.colorPrimaryDiluted));
            islam.setTextColor(getResources().getColor(R.color.colorText));
            other.setTextColor(getResources().getColor(R.color.colorText));
            videodescription = christian.getText().toString().toLowerCase();
            if(j.isShown()){
                j.dismiss();
                warningText.setVisibility(View.GONE);
            }
        });
        islam.setOnClickListener(view -> {
            christian.setTextColor(getResources().getColor(R.color.colorText));
            islam.setTextColor(getResources().getColor(R.color.colorPrimaryDiluted));
            other.setTextColor(getResources().getColor(R.color.colorText));
            videodescription = islam.getText().toString().toLowerCase();
            if(j.isShown()){
                j.dismiss();
                warningText.setVisibility(View.GONE);
            }
        });
        other.setOnClickListener(view -> {
            christian.setTextColor(getResources().getColor(R.color.colorText));
            islam.setTextColor(getResources().getColor(R.color.colorText));
            other.setTextColor(getResources().getColor(R.color.colorPrimaryDiluted));
            videodescription = other.getText().toString().toLowerCase();
            if(j.isShown()){
                j.dismiss();
                warningText.setVisibility(View.GONE);
            }
        });
    }
    private TextView warningText;
    private void startPicker() {
        ArrayList<String> filepaths = new ArrayList<>();

    }
    private String postersmall;
    public void getPoster(final String destv) {
        try {
            Bitmap poster = ThumbnailUtils.createVideoThumbnail(destv, MediaStore.Images.Thumbnails.MINI_KIND);
            String filecache = "IMG_WAMBURA_" + System.currentTimeMillis();
            File file2 = new File(getContext().getCacheDir() + "/images/");
            file2.mkdirs();
            File filesmall = File.createTempFile("thumb_"+filecache,".jpeg",file2);
            File fileposter = File.createTempFile(filecache, ".jpeg", file2);

            if (poster != null) {
                FileOutputStream outputStream = new FileOutputStream(fileposter);
                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                poster.compress(Bitmap.CompressFormat.JPEG, 90, bos);

                FileOutputStream outputStreamSmall = new FileOutputStream(filesmall);
                BufferedOutputStream bossmall = new BufferedOutputStream(outputStreamSmall);
                Bitmap small = Bitmap.createScaledBitmap(poster,210,140,false);
                small.compress(Bitmap.CompressFormat.JPEG,90,bossmall);
                postersmall = filesmall.getAbsolutePath();
                bos.flush();
                bos.close();
                bossmall.flush();
                bossmall.close();
                saveBitmapToFile(fileposter);
            }
        }catch (Exception e){
        }
    }
    private void saveBitmapToFile(File imgFileOrig){
        try {
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            options.setCompressionQuality(90);
            options.setToolbarTitle("Crop Video Cover");
            options.setHideBottomControls(true);
            options.setActiveControlsWidgetColor(getResources().getColor(R.color.colorPrimary));
            options.setStatusBarColor(getResources().getColor(R.color.colorLightAccentAds));
            options.setImageToCropBoundsAnimDuration(600);
            options.setDimmedLayerColor(getResources().getColor(R.color.colorPrimaryDiluted));
            String filePath2 = getContext().getCacheDir() + "/" + "IMG_WAMBURA_" + (System.currentTimeMillis()) + ".jpeg";
            UCrop.of(Uri.fromFile(imgFileOrig),Uri.fromFile(new File(filePath2)))
                    .withAspectRatio(3, 2)
                    .withOptions(options)
                    .withMaxResultSize(641, 426)
                    .start(getContext(),Uploading.this);
        }
        catch (Exception e) {
            Log.e("uploaderror= ",e.toString());
        }
    }
    private static final class GetFilePathFromDevice {
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

    private File copyFile(String sourceLocation, String destLocation) {
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
                    Toast.makeText(this.getContext(), "can write and source was not present", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("eeeeeeee",ex.toString());

        }
        return dest;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode)
        {
            case 1:
                if(resultCode== RESULT_OK && data!=null)
                {
                    ArrayList<String> videoPaths = new ArrayList<>();
                    videoPaths.addAll(data.getStringArrayListExtra("images"));
                    String video = videoPaths.get(0);
                    startDecode(video);
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                        mPosterToUpload = GetFilePathFromDevice.getPath(getContext(),resultUri);
                    Glide.with(getContext()).load(mPosterToUpload).fitCenter().into(imageposter);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    Toast.makeText(getContext(), "errrrrr " + cropError, Toast.LENGTH_SHORT).show();
                }
        }
    }
    private void startDecode(String video){
        mParam1 = video;
        initplayer();
        getPoster(video);
    }
    public void loadImage (File imageFile, ImageView ivImage){
        //Glide is just an example. You can use any image loading library you want;
        Glide.with(Uploading.this).load(imageFile).skipMemoryCache(true).fitCenter().centerCrop().into(ivImage);
    }
    //Uloading Processes start from here

    private double getFileSizeMegaBytes(File file) {
        return file.length() / (1024 * 1024);
    }
    private class getDecodedVideo extends Thread {
        public void run() {
            final String destloc = getContext().getCacheDir() + "/" + "VID_CHTUBE_" + (System.currentTimeMillis()) + ".mp4";
            final EditText addvideotext = inflate.findViewById(R.id.addvideotext);
            videotext = addvideotext.getText().toString();
            Log.d("upload id","Uploading Video");
          getActivity().runOnUiThread(() -> addvideotext.requestFocus());
        }
        private void sendNotificationUpload(Context context, String title,int progress,boolean t) {
            PendingIntent clickIntent = PendingIntent.getActivity(
                    getContext(), 1, new Intent(getContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "1")
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setProgress(100,progress,t)
                    .setTimeoutAfter(180000)
                    .setContentIntent(clickIntent)
                    .setContentInfo(title)
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId("1");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Notification Channel is required for Android O and above
            notificationManager.notify(1, notificationBuilder.build());
        }
    }
}