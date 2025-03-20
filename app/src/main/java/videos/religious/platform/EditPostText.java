package videos.religious.platform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EditPostText extends AppCompatActivity {
    private String UpdatedText,OutDatedText,ChannelId="";
    private EditText updateeditbox;
    private MaterialButton updatebutton;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != getIntent()) {
            Intent intent = getIntent();
            OutDatedText = intent.getStringExtra("outdatedtext");
            ChannelId = intent.getStringExtra("ChannelId");
        }
        setContentView(R.layout.activity_edit_post_text);
        db = FirebaseFirestore.getInstance();
        updateeditbox = findViewById(R.id.editpostbox);
        updatebutton = findViewById(R.id.updatebutton);
        updatebutton.setOnClickListener(view->{
            UpdatedText= updateeditbox.getText().toString();
            if(!UpdatedText.equals("")) {
                updateFirestoreText();
                return;
            }
            updateeditbox.requestFocus();
        });
    }
    private void updateFirestoreText(){
        db.collection("videos").document(ChannelId).update("videotext", Objects.requireNonNull(UpdatedText),
                "time", System.currentTimeMillis()).addOnCompleteListener(m->{
                    if(m.isSuccessful()){
                        Constants.UpdateVideoText = Objects.requireNonNull(UpdatedText);
                        Videos videos = (Videos) getSupportFragmentManager().findFragmentById(R.id.main_content).getParentFragment();
                        finish();
                    }
        });
    }
}