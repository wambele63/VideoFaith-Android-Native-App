package videos.religious.platform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

import ir.mehdiyari.fallery.main.fallery.Fallery;
import ir.mehdiyari.fallery.main.fallery.FalleryBuilder;
import ir.mehdiyari.fallery.main.fallery.FalleryOptions;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link addnews.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class addnews extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String[] country = new String[]{"Select New Location","Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"};
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public addnews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutPage.
     */
    // TODO: Rename and change types and number of parameters
    private EditText newhead,newbody;
    private Button newupload;
    private String newheadtext="",imagetoupload="",newbodytext="";
    private ImageView newimage;
    private FirebaseFirestore db;
    private FirebaseStorage store;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        setContentView(R.layout.fragment_addnews);
        db = FirebaseFirestore.getInstance();
        store = FirebaseStorage.getInstance();
        newhead = findViewById(R.id.newhead);
        newbody = findViewById(R.id.newbody);
        newimage = findViewById(R.id.newimage);
        newupload = findViewById(R.id.newupload);
        MobileAds.initialize(this, "" + R.string.admob_app_id);

        newimage.setOnClickListener(view -> startPicker(1));
        newupload.setOnClickListener(view -> {
        uploadNews();
        });
        Spinner spin = findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
    }
    private void startPicker(int reqCode) {
        final FalleryOptions falleryOptions = new FalleryBuilder()
                .build();

        Fallery.startFalleryFromActivityWithOptions(
                addnews.this, reqCode, falleryOptions
        );
    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        newslocation = country[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
    private String firestoreprofileurl="",firesmall="",newslocation="";
    @SuppressLint("SetTextI18n")
    private void uploadNews(){
        StorageReference sRefs = store.getReference();
        StorageReference pRefs,psref;
        newheadtext = newhead.getText().toString();
        newbodytext = newbody.getText().toString();
        String videofiles = System.currentTimeMillis()+"";
        if(!newheadtext.equals("") && !newbodytext.equals("") && !imagetoupload.equals("") && !newslocation.equals("")) {
            newupload.setText("Saving...");
            final Uri newsimage = Uri.fromFile(new File(imagetoupload));
            final Uri newsmall = Uri.fromFile(new File(postersmall));
            pRefs = sRefs.child("news/" + videofiles + "/" + newsimage.getLastPathSegment());
            //uploadsmall
            psref = sRefs.child("news/" + videofiles + "/" + newsmall.getLastPathSegment());
            if(!postersmall.equals("")) {
                psref.putFile(Uri.fromFile(new File(postersmall))).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        newupload.setText("POST NEW HEADLINE");
                        return null;
                    }
                    return psref.getDownloadUrl();
                }).addOnCompleteListener(task -> firesmall = Objects.requireNonNull(task.getResult()).toString());
            }
            else {
                pRefs.putFile(Uri.fromFile(new File(imagetoupload)));
            }
            //uploadbig
            UploadTask uploadTask = pRefs.putFile(newsimage);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    newupload.setText("POST NEW HEADLINE");
                    return null;
                }
                return pRefs.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    firestoreprofileurl = Objects.requireNonNull(task.getResult()).toString();
                    savenewtofirestore();
                }
            });
        }
        else {
            Snackbar.make(getWindow().getDecorView(),"check if all fields and Image are selected/ filled", BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }
    private void savenewtofirestore(){
        String[] NewKeys = newheadtext.toLowerCase().split(" ");
            Map<String, Object> newChannel = new HashMap<>();
            newChannel.put("head", newheadtext);
            newChannel.put("location", newslocation);
            newChannel.put("image", firestoreprofileurl);
            newChannel.put("imagesmall", firesmall);
            newChannel.put("religion", Constants.myReligion);
            newChannel.put("body", newbodytext);
            newChannel.put("keywords", Arrays.asList(NewKeys));
            newChannel.put("channelname", Constants.myChannelName);
            newChannel.put("timestamp", System.currentTimeMillis());
            CollectionReference dbRef = db.collection("news");
            dbRef.add(newChannel)
                    .addOnSuccessListener(documentReference -> {
                        Map<String,Object> notify = new HashMap<>();
                        notify.put("body",newheadtext);
                        notify.put("contentid",documentReference.getId());
                        notify.put("image",firesmall);
                        notify.put("type","new");
                        notify.put("timestamp",System.currentTimeMillis());
                        notify.put("contentl",newslocation);
                        notify.put("contentd",newbodytext);
                        notify.put("contentv",0);
                        notify.put("religion",Constants.myReligion);
                        notify.put("contenturl",firestoreprofileurl);
                        db.collection("notifications").add(notify);
                        startPushNotification();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "there was an error ", Toast.LENGTH_SHORT).show());
        }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private void startPushNotification(){
        db.collection("serverApp").document("serverApp").get().addOnSuccessListener(serverkeydoc-> {
            if(Objects.requireNonNull(serverkeydoc.getData()).size() > 0){
                String serverkey = serverkeydoc.getString("serverkey");
                Constants.sendFCMPush(getApplicationContext(),
                        "BREAKING NEWS",""+newheadtext,""+firestoreprofileurl
                ,Constants.myReligion,serverkey);
                newhead.setText("");
                newbody.setText("");
                newupload.setText("POST ANOTHER NEW");
            }
            else {
                newupload.setText("POST ANOTHER NEW");
            }
        });
    }

    private void onSingleImageSelected(Uri uri, String tag) {
        //Do something with your Uri
        String fulluriPath = Preferrences.GetFilePathFromDevice.getPath(this, uri);
        String filePath2, filedec;
        File decodedfile;
        switch (tag) {
            case "1":
                filePath2 = getCacheDir() + "/" + "IMG_WAMBURA_" + (System.currentTimeMillis()) + ".jpeg";
                decodedfile = copyFile(fulluriPath, filePath2);
                filedec = decodedfile.getAbsolutePath();
                getDecodedCoverImg(filedec);
                break;
        }
    }
    private void getDecodedCoverImg(final String filetodecode){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(85);
        options.setToolbarTitle("Crop New Image");
        options.setHideBottomControls(true);
        options.setActiveControlsWidgetColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorLightAccentAds));
        options.setImageToCropBoundsAnimDuration(600);
        options.setDimmedLayerColor(getResources().getColor(R.color.colorPrimaryDiluted));
        String filePath2 = getCacheDir() + "/" + "IMG_COVER_WAMBURA_" + (System.currentTimeMillis()) + ".jpeg";
        UCrop.of(Uri.fromFile(new File(filetodecode)),Uri.fromFile(new File(filePath2)))
                .withAspectRatio(3, 2)
                .withOptions(options)
                .withMaxResultSize(640, 426)
                .start(this,111);
    }
    private File copyFile(String sourceLocation, String destLocation) {
        File source = new File(sourceLocation);
        File dest = new File(destLocation);
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
                    Glide.with(this).load(sourceLocation).into(newimage);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Log.d("eeeeeeee",ex.toString());
            dest = source;
        }
        return dest;
    }
    private String postersmall="";
    private void loadImage (File imageFile, ImageView ivImage){
        //Glide is just an example. You can use any image loading library you want;
        Glide.with(this).load(imageFile).into(ivImage);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        final Uri resultUri;
        if(requestCode == 1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> imagePaths = new ArrayList<>(Arrays.asList(Fallery.getResultMediasFromIntent(data)));
                String image = imagePaths.get(0);
                onSingleImageSelected(Uri.parse(image), "1");
            }
        }
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                resultUri = UCrop.getOutput(data);
                imagetoupload = Preferrences.GetFilePathFromDevice.getPath(this, resultUri);
                try {
                    String filecache = "IMG_WAMBURA_" + System.currentTimeMillis();
                    File file2 = new File(getCacheDir() + "/images/");
                    file2.mkdirs();
                    File filesmall = File.createTempFile("thumb_" + filecache, ".jpeg", file2);
                    FileOutputStream outputStreamSmall = new FileOutputStream(filesmall);
                    BufferedOutputStream bossmall = new BufferedOutputStream(outputStreamSmall);
                    Bitmap small = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imagetoupload), 210, 140, false);
                    small.compress(Bitmap.CompressFormat.JPEG, 95, bossmall);
                    postersmall = filesmall.getAbsolutePath();
                    bossmall.flush();
                    bossmall.close();
                }catch (Exception v){
                    postersmall = imagetoupload;
                }
                Glide.with(Objects.requireNonNull(this)).load(postersmall).fitCenter().into(newimage);
            } else if (resultCode == UCrop.RESULT_ERROR) {
                Toast.makeText(this, "Error Getting Photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
@Override
public void onBackPressed(){
        if(!newbodytext.equals("")){
            return;
        }
        super.onBackPressed();
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
}
