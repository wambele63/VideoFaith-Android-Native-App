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
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;
import androidx.emoji.widget.EmojiEditText;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.objectbox.Box;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Preferrences.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Preferrences#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Preferrences extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String fileCoverToUpload = "", FileProfileToUpload = "";

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
    private FirebaseStorage store;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private TextView forgettedPassword;
    private File dest, source;
    private static int BackStatus = 0;
    private final int UCROP_COVER_REQUEST_CODE = 4;
    private final int UCROP_PROFILE_REQUEST_CODE = 5;

    public Preferrences() {
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
    public static Preferrences newInstance(String param1, String param2) {
        Preferrences fragment = new Preferrences();
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
        store = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_preferrences, container, false);
        profilepic = view.findViewById(R.id.profileedit);
        coverpic = view.findViewById(R.id.profilecover);
        channelname = view.findViewById(R.id.channelEditName);
        accountname = view.findViewById(R.id.AccountName);
        email = view.findViewById(R.id.Email);
        ImageView userprofile = view.findViewById(R.id.userprofile);
        TextView username = view.findViewById(R.id.username);
        final CardView oneclicklay = view.findViewById(R.id.oneclicklogin);
        if(Constants.reservechannelname.equals("") || Constants.reservechannelname.equals("No Account")){
            Constants.reservechannelname = "No Account";
            YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    oneclicklay.setVisibility(View.GONE);
                }
            }).playOn(oneclicklay);
        } else {
            YoYo.with(Techniques.SlideInLeft).duration(300).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    oneclicklay.setVisibility(View.VISIBLE);
                }
            }).playOn(oneclicklay);
        }
        username.setText(Constants.reservechannelname);
        Glide.with(userprofile.getContext()).load(Constants.reserveprofile).circleCrop().into(userprofile);
        phonenumber = view.findViewById(R.id.PhoneNumber);
        password = view.findViewById(R.id.Password);
        oneclicklay.setOnClickListener(view -> {
            YoYo.with(Techniques.RubberBand).duration(600).playOn(view);
            if(!Constants.currenttime.equals("") && !Constants.currentime.equals("")){
                loginfun(encryptDecrypt.encryptString(Constants.currenttime),
                        encryptDecrypt.encryptString(Constants.currentime));
            }
            else {
                Toast.makeText(getContext(), "Failed: Please Try Restarting The App", Toast.LENGTH_SHORT).show();
            }
        });
        final TextView buttonlogin = view.findViewById(R.id.loginnow);
        final EmojiEditText emailadresslogin = view.findViewById(R.id.emailtextlogin);
        final EmojiEditText passwordlogin = view.findViewById(R.id.passtextlogin);
        buttonlogin.setOnClickListener(view -> {
            String emaillogintext = emailadresslogin.getText().toString().toLowerCase();
            String passwordlogintext = passwordlogin.getText().toString().toLowerCase();
            if(!emaillogintext.equals("") || passwordlogintext.equals("")){
                emaillogintext = encryptDecrypt.encryptString(
                    emaillogintext);
             passwordlogintext = encryptDecrypt.encryptString(
                    passwordlogintext);
                loginfun(emaillogintext, passwordlogintext);
            }
        });
        switchlogin = view.findViewById(R.id.login);
        final View loginpage = view.findViewById(R.id.loginpage);
        final RelativeLayout scrollView =view.findViewById(R.id.scrollall);
        switchlogin.setOnClickListener(view -> {
            if(switcher == 0){
                switcher = 1;
                switchlogin.setText("Login");
                YoYo.with(Techniques.SlideInDown).duration(300).onEnd(animator -> scrollView.setVisibility(View.VISIBLE)).playOn(scrollView);
                YoYo.with(Techniques.SlideOutDown).duration(300).onStart(animator -> loginpage.setVisibility(View.GONE)).playOn(loginpage);
                        return;
            }
            else
                switcher=0;
            switchlogin.setText("Signup");
            YoYo.with(Techniques.SlideOutUp).duration(300).onStart(animator -> scrollView.setVisibility(View.GONE)).playOn(scrollView);
            YoYo.with(Techniques.SlideInUp).duration(300).onEnd(animator -> loginpage.setVisibility(View.VISIBLE)).playOn(loginpage);
        });
        Glide.with(profilepic.getContext()).load(getResources().getDrawable(R.drawable.ic_avatar)).circleCrop().fitCenter().into(profilepic);
        return view;
    }
    private int switcher=0;
    private Button switchlogin;
    private void loginfun(final String emaillogin, final String passlogin) {
        final Snackbar log = Snackbar.make(view, "Give Us Seconds...", BaseTransientBottomBar.LENGTH_INDEFINITE);
        log.show();
        CollectionReference channelRef = db.collection("channels");
        channelRef.whereEqualTo("email", emaillogin).whereEqualTo("timemills", passlogin)
                .limit(1).get().addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        if (mAuth.getCurrentUser() != null) {
                            mAuth.signOut();
                        }
                        mAuth.signInWithEmailAndPassword(emaillogin, passlogin);
                        SharedPreferences channelinfo = getActivity().getSharedPreferences("channelInfo", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = channelinfo.edit();
                        for (QueryDocumentSnapshot channel : task.getResult()) {
                            Map<String, Object> channeldata = channel.getData();
                            editor.putString("channelname", channeldata.get("channelname") + "")
                                    .putString("email", channeldata.get("email") + "")
                                    .putString("channelid", channel.getId())
                                    .putBoolean("status", (boolean) channeldata.get("status"))
                                    .putString("accountname", channeldata.get("accountname") + "")
                                    .putString("cprofile", channeldata.get("profile") + "")
                                    .putString("ccover", channeldata.get("cover") + "")
                                    .putString("clocation", channeldata.get("country") + "")
                                    .putString("reserveprofile", channeldata.get("profile") + "")
                                    .putString("reservename", channeldata.get("channelname") + "")
                                    .putString("currenttime", channeldata.get("email") + "")
                                    .putString("phone", channeldata.get("phone") + "")
                                    .putString("religion", channeldata.get("religion") + "")
                                    .putBoolean("PRO USER", (boolean) channeldata.get("PRO USER"))
                                    .putString("currentime", channeldata.get("timemills") + "");
                        }
                        log.setText("Successful, Please Wait... 3");
                        editor.apply();
                        new Handler().postDelayed(() -> log.setText("Please Wait... 2"), 1000);
                        new Handler().postDelayed(() -> log.setText("Please Wait... 1"), 2000);
                        new Handler().postDelayed(() -> log.setText("Please Wait... 0"), 3000);
                        new Handler().postDelayed(() -> {
                            getActivity().finishAffinity();
                            System.exit(0);
                        }, 4000);
                    } else {
                        log.setText("Login Failed Try Again").setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                log.dismiss();
                            }
                        });
                    }
                });
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
        public void loadImage (File imageFile, ImageView ivImage){
            //Glide is just an example. You can use any image loading library you want;
            Glide.with(Preferrences.this).load(imageFile).into(ivImage);
        }
        private void uploadCover (String path){
            File file = new File(path);
        }
        @Override
        public void onViewCreated (@NonNull View views,
                Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            TextView coverpicedit = view.findViewById(R.id.profilecovereditable);
            TextView imagepic = view.findViewById(R.id.profileeditbtn);
            imagepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // fire pro picker
                }
            });
            coverpicedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // fire cover picture
                }
            });

            final Button next1 = views.findViewById(R.id.fabnext1);
            final Button prev1 = views.findViewById(R.id.fabprev1);
            final RelativeLayout startpage = views.findViewById(R.id.startpage);
            final RelativeLayout personalinfo = views.findViewById(R.id.editdetailsonly);
            final RelativeLayout editcontacts = views.findViewById(R.id.editcontacts);
            final RelativeLayout lcover = views.findViewById(R.id.layoutcover);
            final RelativeLayout lprofile = views.findViewById(R.id.editprofilelayout);
            forgettedPassword = views.findViewById(R.id.forgottedPassword);
            forgettedPassword.setOnClickListener(view -> {
                Intent activity = new Intent(getActivity(), PasswordAndEmailReset.class);
                activity.putExtra("activityType", "forgotPassword");
                startActivity(activity);
            });
            RadioGroup radioGroupgender = views.findViewById(R.id.radiogenders);
            RadioGroup radioGroupAge = views.findViewById(R.id.radioage);
            RadioGroup radioGroupReligion = views.findViewById(R.id.radioreligions);

            Spinner spin = views.findViewById(R.id.spinnerloc);
            spin.setOnItemSelectedListener(this);
            //Creating the ArrayAdapter instance having the country list
            ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, country);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spin.setAdapter(aa);
            Snackbar errorsnack = Snackbar.make(views, "", BaseTransientBottomBar.LENGTH_INDEFINITE);
            errorsnack.setBackgroundTint(getResources().getColor(R.color.colorPrimary));
            errorsnack.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
            next1.setOnClickListener(view -> {
                Toast.makeText(getContext(), "" + next, Toast.LENGTH_SHORT).show();
                switch (next) {
                    case 0:
                        try {
                            //get Gender
                            int getselectedgenderId = radioGroupgender.getCheckedRadioButtonId();
                            RadioButton gender = views.findViewById(getselectedgenderId);
                            //get age
                            int getselectedageId = radioGroupAge.getCheckedRadioButtonId();
                            RadioButton age = views.findViewById(getselectedageId);
                            //get religion
                            int getselectedreligionId = radioGroupReligion.getCheckedRadioButtonId();
                            RadioButton religion = views.findViewById(getselectedreligionId);
                            //extra data
                            gendertype = gender.getText().toString().toLowerCase();
                            religiontype = religion.getText().toString().toLowerCase();
                            agerange = age.getText().toString();
                            next++;
                            slideOutLeft(startpage);
                            slideOutLeft(switchlogin);
                            slideInRight(personalinfo);
                            slideInRight(prev1);
                        } catch (Exception f) {
                            errorsnack.setText("Check All Fields").setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    errorsnack.dismiss();
                                }
                            }).show();
                        }
                        //report errors
                        break;
                    case 1:
                        newChannelName = channelname.getText().toString();
                        newAccountName = accountname.getText().toString();
                        if (!newAccountName.equals("") ||
                                !newChannelName.equals("") || !userlocation.equals("")) {
                            next++;
                            slideOutLeft(personalinfo);
                            slideInLeft(editcontacts);
                            return;
                        }
                        errorsnack.setText("Fill All Requered Fields").setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                errorsnack.dismiss();
                            }
                        }).show();
                        break;
                    case 2:
                        newEmailName = email.getText().toString().toLowerCase();
                        newPhoneName = phonenumber.getText().toString();
                        newPasswordName = password.getText().toString().toLowerCase();
                        if (!newEmailName.equals("") && !newPasswordName.equals("")) {
                            if (newPasswordName.length() < 6) {
                                errorsnack.setText("Password Too Short")
                                        .setAction("Dismiss", view1 -> errorsnack.dismiss()).show();
                                return;
                            }
                            newPasswordName = encryptDecrypt.encryptString(newPasswordName);
                            newEmailName = encryptDecrypt.encryptString(newEmailName);
                            next++;
                            slideOutLeft(editcontacts);
                            slideInRight(lcover);
                            return;
                        }
                        errorsnack.setText("Fill All The Fields").setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                errorsnack.dismiss();
                            }
                        }).show();
                        break;
                    case 3:
                        next++;
                        slideOutLeft(lcover);
                        slideInRight(lprofile);
                        next1.setText("Create Channel");
                        break;
                    case 4:
                        if (FileProfileToUpload.equals("")) {
                            //report errors
                            errorsnack.setText("Please Select Profile Picture").setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    errorsnack.dismiss();
                                }
                            }).show();
                            return;
                        }
                        checkChannelName();
                        //report errors
                        break;
                    default:
                        //reporterror
                }
            });
            prev1.setOnClickListener(view -> {
                Toast.makeText(getContext(), "" + next, Toast.LENGTH_SHORT).show();
                switch (next) {
                    case 0:
                        next = 0;
                        //report errors
                        break;
                    case 1:
                        next--;
                        slideInLeft(startpage);
                        slideInLeft(switchlogin);
                        slideOutRight(personalinfo);
                        slideOutRight(prev1);
                        break;
                    case 2:
                        next--;
                        slideInLeft(personalinfo);
                        slideOutLeft(editcontacts);
                        break;
                    case 3:
                        next--;
                        slideInLeft(editcontacts);
                        slideOutRight(lcover);
                        break;
                    case 4:
                        next--;
                        slideInLeft(lcover);
                        slideOutRight(lprofile);
                        next1.setText("Next");
                        break;
                }
            });
        }
        private String userlocation="";
    private String[] country = new String[]{"Select Channel Location","Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"};
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if(position > 0) {
            userlocation = country[position];
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
    public void getDecodedCoverImg(final String filetodecode){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(90);
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
                .start(getContext(),Preferrences.this,UCROP_COVER_REQUEST_CODE);
    }
    public void getDecodedProfileImg(final String filetodecode){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(90);
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
                .withMaxResultSize(210, 210)
                .start(getContext(),Preferrences.this,UCROP_PROFILE_REQUEST_CODE);
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
                view.setVisibility(View.GONE);
            }
        }).playOn(view);
    }
    private void slideOutLeft(final View view){
        YoYo.with(Techniques.SlideOutLeft).duration(300).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                view.setVisibility(View.GONE);
            }
        }).playOn(view);
    }
    private int next=0;
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
    }
    private StorageReference pRefs,cRefs;
    private String firestoreprofileurl="",firestorepcoverurl="";
    private String gendertype="",religiontype="",agerange="",newChannelName="",newAccountName="",newEmailName="",location="",
            newPhoneName="",newPasswordName="";
    private void syncData() {
        final Snackbar sc = Snackbar.make(view, "Uploading Your Profiles.....", BaseTransientBottomBar.LENGTH_INDEFINITE);
        sc.show();
        try {
            if ( !newAccountName.equals("") ||
                    !newChannelName.equals("") || !newEmailName.equals("") || !newPasswordName.equals("")) {
                if(fileCoverToUpload.equals("") && !FileProfileToUpload.equals("")){
                    fileCoverToUpload = FileProfileToUpload;
                }
                String[] filestrings = new String[]{FileProfileToUpload, fileCoverToUpload};
                String videofiles = System.currentTimeMillis() + "";
                StorageReference sRef = store.getReference();
                for (int i = 0; i < filestrings.length; i++) {
                    switch (i) {
                        case 0:
                            final Uri profile = Uri.fromFile(new File(filestrings[i]));
                            pRefs = sRef.child("channels/" + videofiles + "/" + profile.getLastPathSegment());
                            UploadTask uploadTask = pRefs.putFile(profile);
                            uploadTask.continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    return null;
                                }
                                return pRefs.getDownloadUrl();
                            }).addOnCompleteListener(getActivity(),task -> {
                                if (task.isSuccessful()) {
                                    firestoreprofileurl = task.getResult().toString();
                                }
                            });
                            break;
                        case 1:
                            Uri cover = Uri.fromFile(new File(filestrings[i]));
                            cRefs = sRef.child("channels/" + videofiles + "/" + cover.getLastPathSegment());
                            UploadTask uploadTasks = cRefs.putFile(cover);
                            uploadTasks.addOnProgressListener(taskSnapshot -> {
                                double incr = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                final int incrr = (int) incr;
                            }).continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    return null;
                                }
                                return cRefs.getDownloadUrl();
                            }).addOnCompleteListener(getActivity(), task -> {
                                if (task.isSuccessful()) {
                                    firestorepcoverurl = task.getResult().toString();
                                    sc.setText("Validating Information....");
                                    saveChannelToFirestore();
                                }
                            });
                            break;
                        default:
                            return;
                    }
                }
                audioPlayer(R.raw.save);
                return;
            }
            Snackbar.make(view, "fill all required fields!!", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Snackbar.make(view, "Some Fields Not Filled!!", Snackbar.LENGTH_SHORT).show();
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
                    Glide.with(Preferrences.this.getContext()).load(sourceLocation).into(coverpic);
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
    private boolean checkEmail = false;
    private void checkEmail (){
        final Snackbar sc = Snackbar.make(view, "Checking Email.....", BaseTransientBottomBar.LENGTH_INDEFINITE);
        sc.show();
        CollectionReference checkemail = db.collection("channels");
        Query query = checkemail.whereEqualTo("email", newEmailName);
        query.limit(1).get().addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                syncData();
            }
            else {
                email.requestFocus();
                Snackbar.make(view,"Email Exists Use Another One",BaseTransientBottomBar.LENGTH_LONG).show();
                Log.d("", "Error getting documents: ", task.getException());
            }
        });
    }
    private boolean checkName = false;
    private void checkChannelName(){
        final Snackbar sc = Snackbar.make(view, "Checking Channel Name.....", BaseTransientBottomBar.LENGTH_INDEFINITE);
        sc.show();
        CollectionReference checkname = db.collection("channels");
        Query query = checkname.whereEqualTo("channelname", newChannelName);
        query.limit(1).get().addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                if(task.getResult().isEmpty()){
                    checkEmail();
                    return;
                }
                Snackbar.make(view,"Channel Name Exists Use Another One",BaseTransientBottomBar.LENGTH_LONG).show();
            } else {
                Log.d("", "Error getting documents: ", task.getException());
            }
        });
    }
    private void saveChannelToFirestore(){
        final Snackbar sc = Snackbar.make(view, "Creating Channel.....", BaseTransientBottomBar.LENGTH_INDEFINITE);
        sc.show();
        String[] stringskey = (newChannelName.toLowerCase() + " " + newAccountName.toLowerCase()).split(" ");
        Map<String, Object> newChannel = new HashMap<>();
        newChannel.put("cover", firestorepcoverurl);
        newChannel.put("profile", firestoreprofileurl);
        newChannel.put("channelname", newChannelName);
        newChannel.put("accountname", newAccountName);
        newChannel.put("gender", gendertype);
        newChannel.put("email", newEmailName);
        newChannel.put("phone", newPhoneName);
        newChannel.put("PRO USER", false);
        newChannel.put("keywords", Arrays.asList(stringskey));
        newChannel.put("timemills", newPasswordName);
        newChannel.put("firebaseAuth",newPasswordName);
        newChannel.put("tokens", Constants.FIREBASE_TOKEN);
        newChannel.put("videos", 0);
        newChannel.put("subsriptions", 0);
        newChannel.put("age", agerange);
        newChannel.put("country", userlocation);
        newChannel.put("status", false);
        newChannel.put("religion", religiontype.toLowerCase());
        newChannel.put("time", System.currentTimeMillis());
        CollectionReference dbRef = db.collection("channels");
        dbRef.add(newChannel)
                .addOnSuccessListener(getActivity(),documentReference -> {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if(mAuth.getCurrentUser() != null){
                        mAuth.signOut();
                    }
                    mAuth.createUserWithEmailAndPassword(newEmailName,newPasswordName);
                    mAuth.signInWithEmailAndPassword(newEmailName,newPasswordName);
                    Toast.makeText(getContext(), "Please Wait...", Toast.LENGTH_LONG).show();
                                    Snackbar.make(view, "Account Submitted Succesful!!!", Snackbar.LENGTH_LONG).show();
                    new Handler().postDelayed(() -> {
                                        getActivity().finishAffinity();
                                        System.exit(0);
                                    }, 5000);
                    savelocally(documentReference.getId());
                });
    }
    private void savelocally(String id){
        SharedPreferences channelinfo = getActivity().getSharedPreferences("channelInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = channelinfo.edit();
        editor.putString("channelname", newChannelName)
                .putString("email",newEmailName)
                .putString("channelid",id)
                .putBoolean("status",false)
                .putString("accountname",newAccountName)
                .putString("cprofile",firestoreprofileurl)
                .putString("ccover",firestorepcoverurl)
                .putString("clocation",userlocation)
                .putString("reserveprofile",firestoreprofileurl)
                .putString("reservename",newChannelName)
                .putString("currenttime",newEmailName)
                .putString("phone",newPhoneName)
                .putBoolean("PRO USER", false)
                .putString("religion",religiontype)
                .putString("currentime",newPasswordName);
        editor.apply();
    }
}