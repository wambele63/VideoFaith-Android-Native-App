package videos.religious.platform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import static android.view.View.GONE;

public class PasswordAndEmailReset extends AppCompatActivity {
    private LinearLayout forgotPasswordView,changePasswordView,changeEmailView,verifyAccountView;
    private Button verifyButton,forgotpasswordbutton,sendSmsButton,changeEmailButton,changePasswordButton;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_and_email_reset);
        forgotPasswordView = findViewById(R.id.forgotPassword);
        changePasswordView = findViewById(R.id.resetPassword);
        changeEmailView = findViewById(R.id.resetEmail);
        verifyAccountView = findViewById(R.id.verifyEmail);
        sendSmsButton = findViewById(R.id.sendSms);
        db = FirebaseFirestore.getInstance();
        snackbar = Snackbar.make(getWindow().getDecorView(),"Check Your MailBox For Latest Verification Code", BaseTransientBottomBar.LENGTH_LONG);
        ConstraintLayout main = findViewById(R.id.maincodent);
        main.setFocusableInTouchMode(true);
        Intent TypeOfProccessing = getIntent();
        String activityType = TypeOfProccessing.getStringExtra("activityType");
        assert activityType != null;
        switch(activityType){
            case "forgotPassword":
                //do init forgot
                activityTypeInt = 1;
                initForgotPassword();
                subj="Password Recovery Code";
                forgotPasswordView.setVisibility(View.VISIBLE);
                break;
            case "changePassword":
                //do init
                activityTypeInt = 2;
                subj="Password Reset Code";
                sendSmsButton.setVisibility(GONE);
                initChangePassword();
                changePasswordView.setVisibility(View.VISIBLE);
                break;
            case "verifyEmail":
                //do init verify
                activityTypeInt=3;
                sendSmsButton.setVisibility(GONE);
                subj="Email Verification Code";
                initVerification();
                verifyAccountView.setVisibility(View.VISIBLE);
                break;
            case "changeEmail":
                //do init changePass
                activityTypeInt=4;
                subj="Email Reset Code";
                sendSmsButton.setVisibility(GONE);
                initChangeEmail();
                changeEmailView.setVisibility(View.VISIBLE);
                break;
            default:
                finish();
        }
        sendSmsButton.setOnClickListener(view -> {
            phoneLayout = findViewById(R.id.forgotPhoneLayout);
            YoYo.with(Techniques.FadeIn).duration(300).onStart(onStart->{
                phoneLayout.setVisibility(View.VISIBLE);
            }).playOn(phoneLayout);
        });
        EditText phonenumber = findViewById(R.id.forgotResetPhone);
        Button sendnow = findViewById(R.id.sendnow);
        sendnow.setOnClickListener(v->{
            phonenumbertext = phonenumber.getText().toString();
           if(!phonenumbertext.contains("@") || !phonenumbertext.contains(".")){
               Toast.makeText(this, "Email Looks Invalid", Toast.LENGTH_LONG).show();
               return;
           }
               if(activityTypeInt == 1) {
                   sendMail(lastsixcodes, subj);
                   return;
               }
               if(activityTypeInt == 2) {
                   sendMail(lastsixcodes, subj);
                   return;
               }
           });
    }
    //verify account
    private void initVerification(){
        //todo
            if (lastsixcodes.equals("")) {
                String sixdigits = System.currentTimeMillis() + "";
                lastsixcodes = "V-" + sixdigits.substring(sixdigits.length() - 5);
                sendMail(lastsixcodes, " Is Your Verification Code");
            }
            else {
                snackbar.setText("Enter The Code Sent To Your Email").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
                enableButtons();
            }
    }
    private LinearLayout phoneLayout;
    private String phonenumbertext;
    //verify account
    private void initForgotPassword(){
        //todo
        if (lastsixcodes.equals("")) {
            String sixdigits = System.currentTimeMillis() + "";
            lastsixcodes = "V-" + sixdigits.substring(sixdigits.length() - 5);
        }
        else {
            snackbar.setText("Enter The Code Sent To Your Email").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
            enableButtons();
        }
    }
    //verify account
    private void initChangeEmail(){
        //todo
        if (lastsixcodes.equals("")) {
            String sixdigits = System.currentTimeMillis() + "";
            lastsixcodes = "V-" + sixdigits.substring(sixdigits.length() - 5);
            sendMail(lastsixcodes, " Is Your Email Change Code");
        }
        else {
            snackbar.setText("Enter The Code Sent To Your Email").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
            enableButtons();
        }
    }
    //verify account
    private void initChangePassword(){
        //todo
        if (lastsixcodes.equals("")) {
            String sixdigits = System.currentTimeMillis() + "";
            lastsixcodes = "V-" + sixdigits.substring(sixdigits.length() - 5);
            sendMail(lastsixcodes, " Is Your Password Change Code");
        }
        else {
            snackbar.setText("Enter The Code Sent To Your Email").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
            enableButtons();
        }
    }
    private void enableButtons(){
        switch(activityTypeInt) {
            case 1:
                forgotpasswordbutton = findViewById(R.id.resetForgotNow);
                forgotpasswordbutton.setEnabled(true);
                forgotpasswordbutton.setOnClickListener(v->{
                    forgotpasswordbutton.setEnabled(false);
                    forgotpasswordbutton.setBackgroundColor(getResources().getColor(R.color.colorLightAccentAds));
                    checkVerificationCredentialsAndThenResetForgotPassword();
                });
                break;
            case 2:
                changePasswordButton = findViewById(R.id.resetPasswordNow);
                changePasswordButton.setEnabled(true);
                changePasswordButton.setOnClickListener(v->{
                    changePasswordButton.setEnabled(false);
                    changePasswordButton.setBackgroundColor(getResources().getColor(R.color.colorLightAccentAds));
                    checkVerificationCredentialsAndThenResetPassword();
                });
                break;
            case 3:
                verifyButton = findViewById(R.id.approveVerification);
                verifyButton.setEnabled(true);
                verifyButton.setOnClickListener(v->{
                    verifyButton.setEnabled(false);
                    verifyButton.setBackgroundColor(getResources().getColor(R.color.colorLightAccentAds));
                    checkVerificationCredentialsAndThenVerify();
                });
                break;
            case 4:
                changeEmailButton = findViewById(R.id.resetEmailNow);
                changeEmailButton.setEnabled(true);
                changeEmailButton.setOnClickListener(v->{
                    changeEmailButton.setEnabled(false);
                    changeEmailButton.setBackgroundColor(getResources().getColor(R.color.colorLightAccentAds));
                    checkVerificationCredentialsAndThenChangeEmail();
                });
                break;
        }
    }
    private void checkVerificationCredentialsAndThenVerify(){
        EditText verifiedCode = findViewById(R.id.verificationCode);
        String verifiedCodeText = verifiedCode.getText().toString();
        if(!verifiedCodeText.equals("")){
                   if(!lastsixcodes.equals(verifiedCodeText)){
                       snackbar.setText("Failed: Make Sure It Is The Latest Sent Code").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
                       verifyButton.setEnabled(true);
                       verifyButton.setBackgroundColor(Color.WHITE);
                   return;
                   }
                   db.collection("channels").document(Constants.myId).update("status",true)
                   .addOnSuccessListener(aVoid -> {
                       lastsixcodes = "";
                       Constants.STATUS = true;
                       SharedPreferences preferences = getSharedPreferences("channelInfo", Context.MODE_PRIVATE);
                       preferences.edit().putBoolean("status", true).apply();
                       snackbar.setText("Succesfull: Account Verified").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
                       new Handler().postDelayed(() -> finish(), 3000);
                      finish();
                   }).addOnFailureListener(listervoid->{
                       verifyButton.setEnabled(true);
                       verifyButton.setBackgroundColor(Color.WHITE);
                       Toast.makeText(this, "Check Network Connection", Toast.LENGTH_SHORT).show();
                   });
            }
    }
    private void checkVerificationCredentialsAndThenResetForgotPassword(){
        try {
            if (phonenumbertext.length() > 6) {
                Toast.makeText(this, "enttering 1" +phonenumbertext, Toast.LENGTH_SHORT).show();
                EditText changeForgotCodeEdit, changeForgotEmailEdit, changeForgotNewEdit, changeForgotRepeatEdit;
                changeForgotCodeEdit = findViewById(R.id.forgotResetCode);
                changeForgotEmailEdit = findViewById(R.id.forgotResetEmail);
                changeForgotNewEdit = findViewById(R.id.forgotResetPassword);
                changeForgotRepeatEdit = findViewById(R.id.forgotResetNewPassword);
                String ForgotCodeText = changeForgotCodeEdit.getText().toString();
                String ForgotEmailText = changeForgotEmailEdit.getText().toString().toLowerCase();
                String ForgotNewText = changeForgotNewEdit.getText().toString().toLowerCase();
                String ForgotRepeatText = changeForgotRepeatEdit.getText().toString().toLowerCase();
                String finalForgotNewTextplain = ForgotNewText;
                if (!ForgotCodeText.equals("") || !ForgotEmailText.equals("") || !ForgotNewText.equals("") || !ForgotRepeatText.equals("")) {
                    ForgotEmailText = encryptDecrypt.encryptString(ForgotEmailText);
                    ForgotNewText = encryptDecrypt.encryptString(ForgotNewText);
                    ForgotRepeatText = encryptDecrypt.encryptString(ForgotRepeatText);
                    String finalForgotEmailText = ForgotEmailText;
                    String finalForgotRepeatText = ForgotRepeatText;
                    String finalForgotNewText = ForgotNewText;
                    db.collection("channels")
                            .whereEqualTo("email",encryptDecrypt.encryptString(phonenumbertext))
                            .get().addOnSuccessListener(verifiedCodeDoc -> {
                        if (Objects.requireNonNull(verifiedCodeDoc.getDocuments()).size() > 0) {
                            String onlineEmail = verifiedCodeDoc.getDocuments().get(0).getString("email");
                            String errors = "";
                            Toast.makeText(this, "got 11", Toast.LENGTH_SHORT).show();
                            if (!lastsixcodes.equals(ForgotCodeText)) {
                                errors = "Erros - Wrong Codes";
                            }
                            assert onlineEmail != null;
                            if (!onlineEmail.equals(finalForgotEmailText)) {
                                errors = errors + ", Email Not Recognized";
                            }
                            if (!finalForgotNewText.equals(finalForgotRepeatText)) {
                                errors = errors + ", Passwords Does Not Match";
                            }
                            if (finalForgotNewTextplain.length() < 6) {
                                errors = errors + ", Password Too Short (less than 6)";
                            }
                            if (!errors.equals("")) {
                                snackbar.setText(errors).setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE).show();
                                snackbar.setAction("Close", listener -> {
                                    snackbar.dismiss();
                                });
                                snackbar.setBackgroundTint(Color.WHITE);
                                snackbar.setBackgroundTintMode(PorterDuff.Mode.OVERLAY);
                                snackbar.setTextColor(Color.RED);
                                snackbar.setActionTextColor(Color.BLACK);
                                changePasswordButton.setEnabled(true);
                                changePasswordButton.setBackgroundColor(Color.WHITE);
                                return;
                            }
                            db.collection("channels").whereEqualTo("email", encryptDecrypt.encryptString(phonenumbertext))
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "got", Toast.LENGTH_SHORT).show();
                                        String dr = aVoid.getDocuments().get(0).getId();
                                        if (aVoid.getDocuments().size() > 0) {
                                            db.collection("channels").document(dr).update("timemills", finalForgotNewText);
                                            lastsixcodes = "";
                                            snackbar.setText("Succesfull: Password Changed").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
                                            new Handler().postDelayed(this::finish, 3000);
                                        } else {
                                            snackbar.setText("Error Ressetting").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(fail -> {
                                changePasswordButton.setEnabled(true);
                                changePasswordButton.setBackgroundColor(Color.WHITE);
                                Toast.makeText(this, "Check Network Connection " + fail.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }else{
                            snackbar.show();
                            snackbar.setText("succc 0" );
                        }
                    }).addOnFailureListener(onfailure -> {
                        snackbar.show();
                        snackbar.setText("empty " + onfailure.getMessage());
                    });
                }
            }
        }catch (Exception d){
            Toast.makeText(this, "error " + d.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void checkVerificationCredentialsAndThenResetPassword(){
        EditText changePassCodeEdit,changePassEmailEdit,changePassCurrentEdit,changePassNewEdit;
        changePassCodeEdit = findViewById(R.id.passwordResetCode);
        changePassEmailEdit = findViewById(R.id.passwordResetEmail);
        changePassCurrentEdit = findViewById(R.id.passwordResetPassword);
        changePassNewEdit = findViewById(R.id.passwordResetNew);
        String changePassCodeText = changePassCodeEdit.getText().toString();
        String changePassEmailText = changePassEmailEdit.getText().toString().toLowerCase();
        String changePassCurrentText = changePassCurrentEdit.getText().toString().toLowerCase();
        String changePassNewText = changePassNewEdit.getText().toString().toLowerCase();
        String finalChangePassNewTextPlain = changePassNewText;
        if(!changePassCodeText.equals("") || !changePassEmailText.equals("") || !changePassCurrentText.equals("") ||
                !changePassNewText.equals("")){
            changePassEmailText = encryptDecrypt.encryptString(changePassEmailText);
            changePassCurrentText = encryptDecrypt.encryptString(changePassCurrentText);
            changePassNewText = encryptDecrypt.encryptString(changePassNewText);
            String finalChangePassCurrentText = changePassCurrentText;
            String finalChangePassEmailText = changePassEmailText;
            String finalChangePassNewText = changePassNewText;
            db.collection("channels").document(Constants.myId).get().addOnSuccessListener(verifiedCodeDoc->{
               if(Objects.requireNonNull(verifiedCodeDoc.getData()).size() > 0) {
                   String onlineEmail = verifiedCodeDoc.getString("email");
                   String onlinePassword = verifiedCodeDoc.getString("timemills");
                   String errors="";
                   if(!lastsixcodes.equals(changePassCodeText)){
                       errors = "Erros - Wrong Codes";
                   }
                   assert onlineEmail != null;
                   if(!onlineEmail.equals(finalChangePassEmailText)){
                       errors = errors + ", Email Not Recognized";
                   }
                   assert onlinePassword != null;
                   if(!onlinePassword.equals(finalChangePassCurrentText)){
                       errors = errors + ", Current Password Incorrect";
                   }
                   if(finalChangePassNewTextPlain.length() < 6){
                       errors = errors + ", Password Too Short (less than 6)";
                   }
                   if(!errors.equals("")){
                       snackbar.setText(errors).setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE).show();
                       snackbar.setAction("Close",listener->{
                           snackbar.dismiss();
                       });
                       snackbar.setBackgroundTint(Color.WHITE);
                       snackbar.setBackgroundTintMode(PorterDuff.Mode.OVERLAY);
                       snackbar.setTextColor(Color.RED);
                       snackbar.setActionTextColor(Color.BLACK);
                       changePasswordButton.setEnabled(true);
                       changePasswordButton.setBackgroundColor(Color.WHITE);
                       return;
                   }
                   db.collection("channels").document(Constants.myId).update("timemills", finalChangePassNewText)
                   .addOnSuccessListener(aVoid -> {
                       lastsixcodes = "";
                       snackbar.setText("Succesfull: Password Changed").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
                       new Handler().postDelayed(this::finish, 3000);
                   }).addOnFailureListener(fail->{
                       changePasswordButton.setEnabled(true);
                       changePasswordButton.setBackgroundColor(Color.WHITE);
                       Toast.makeText(this, "Check Network Connection", Toast.LENGTH_SHORT).show();
                   });
                   }
            });
        }
    }
    private void checkVerificationCredentialsAndThenChangeEmail(){
        EditText changeEmailCodeEdit,changeEmailEmailEdit,changeEmailCurrentEdit,changeEmailNewEdit;
        changeEmailCodeEdit = findViewById(R.id.emailResetCode);
        changeEmailEmailEdit = findViewById(R.id.emailResetEmail);
        changeEmailCurrentEdit = findViewById(R.id.emailResetPassword);
        changeEmailNewEdit = findViewById(R.id.emailResetNew);
        String changeEmailCodeText = changeEmailCodeEdit.getText().toString();
        String changeEmailEmailText = changeEmailEmailEdit.getText().toString().toLowerCase();
        String changeEmailPasswordText = changeEmailCurrentEdit.getText().toString().toLowerCase();
        String changeEmailNewText = changeEmailNewEdit.getText().toString().toLowerCase();
        if(!changeEmailCodeText.equals("") || !changeEmailEmailText.equals("") || !changeEmailPasswordText.equals("") || !changeEmailNewText.equals("")){
            changeEmailEmailText = encryptDecrypt.encryptString(changeEmailEmailText);
            changeEmailPasswordText = encryptDecrypt.encryptString(changeEmailPasswordText);
            changeEmailNewText = encryptDecrypt.encryptString(changeEmailNewText);
            String finalChangeEmailEmailText = changeEmailEmailText;
            String finalChangeEmailNewText = changeEmailNewText;
            String finalChangeEmailPasswordText = changeEmailPasswordText;
            CollectionReference checkemail = db.collection("channels");
            Query query = checkemail.whereEqualTo("email", finalChangeEmailNewText);
            query.limit(1).get().addOnCompleteListener(this, task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    snackbar.setText("Email Already Used").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
                }
                else {
                    db.collection("channels").document(Constants.myId).get().addOnSuccessListener(verifiedCodeDoc -> {
                        if (Objects.requireNonNull(verifiedCodeDoc.getData()).size() > 0) {
                            String onlineEmail = verifiedCodeDoc.getString("email");
                            String onlinePassword = verifiedCodeDoc.getString("timemills");
                            String errors = "";
                            if (!lastsixcodes.equals(changeEmailCodeText)) {
                                errors = "Erros - Wrong Codes";
                            }
                            assert onlineEmail != null;
                            if (!onlineEmail.equals(finalChangeEmailEmailText)) {
                                errors = errors + ", Current Email Not Recognized";
                            }
                            assert onlinePassword != null;
                            if (!onlinePassword.equals(finalChangeEmailPasswordText)) {
                                errors = errors + ", Current Password Incorrect";
                            }
                            if (!finalChangeEmailNewText.contains("@") || !finalChangeEmailNewText.contains(".")) {
                                errors = errors + ", Email Not Valid";
                            }
                            if (!errors.equals("")) {
                                snackbar.setText(errors).setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE).show();
                                snackbar.setAction("Close", listener -> {
                                    snackbar.dismiss();
                                });
                                snackbar.setBackgroundTint(Color.WHITE);
                                snackbar.setBackgroundTintMode(PorterDuff.Mode.OVERLAY);
                                snackbar.setTextColor(Color.RED);
                                snackbar.setActionTextColor(Color.BLACK);
                                changePasswordButton.setEnabled(true);
                                changePasswordButton.setBackgroundColor(Color.WHITE);
                                return;
                            }
                            db.collection("channels").document(Constants.myId).update("email", finalChangeEmailNewText)
                                    .addOnSuccessListener(aVoid -> {
                                        lastsixcodes = "";
                                        snackbar.setText("Congraturations: Email Changed").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
                                        new Handler().postDelayed(this::finish, 3000);
                                    }).addOnFailureListener(fail -> {
                                changePasswordButton.setEnabled(true);
                                changePasswordButton.setBackgroundColor(Color.WHITE);
                                Toast.makeText(this, "Check Network Connection", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            });
        }
    }
    private int retry=0;
    private String lastsixcodes = "";
    private int activityTypeInt=0;
    private Snackbar snackbar;
    private String subj;
    private void sendMail(String code, String subject) {
        String emailToBeSentTo="";
        if(!Constants.currenttime.equals("")) {
            emailToBeSentTo = Constants.currenttime;
        }
        if(activityTypeInt == 1){
            YoYo.with(Techniques.FadeIn).duration(300).onEnd(onStart->{
                phoneLayout.setVisibility(GONE);
            }).playOn(phoneLayout);
            emailToBeSentTo = phonenumbertext;
        }
        if(!emailToBeSentTo.equals("")) {
            enableButtons();
            String finalEmailToBeSentTo = emailToBeSentTo;
            BackgroundMail.newBuilder(this)
                    .withUsername("noreplyvideofaith@gmail.com")
                    .withPassword("Eliza1966")
                    .withMailto(emailToBeSentTo)
                    .withType(BackgroundMail.TYPE_PLAIN)
                    .withSubject(subject)
                    .withBody(code + " Is Your " + subject)
                    .withSendingMessage(code)
                    .withOnSuccessCallback(() -> {
                        //do some magic
                        snackbar.setText("Verification Code Sent Succesfully").setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
                    })
                    .withOnFailCallback(() -> {
                        //do some magic
                            retry++;
                            snackbar.setText("Network Failed: try Resending Code to " + finalEmailToBeSentTo).show();
                           new Handler().postDelayed(()->{
                               sendMail(code, subject);
                               Toast.makeText(this, "mail", Toast.LENGTH_SHORT).show();
                           }, 5000);
                    })
                    .build()
                    .send();
        }
    }
    private void sendSmsInstead(String number, String subj) {
        ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setTitle("WARNING:");
            progressDialog.getWindow().setTitleColor(Color.RED);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
            progressDialog.setMessage("Sending Sms May Cost Your Sim Card, if You don't Have Sms Bundle/ Money Sms Will Not Deliver");
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progressDialog.dismiss();
                }
            });
            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "I Agree", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progressDialog.dismiss();
                }
            });
            progressDialog.create();
            progressDialog.show();
    }
}
