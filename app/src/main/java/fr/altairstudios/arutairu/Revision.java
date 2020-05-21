package fr.altairstudios.arutairu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

public class Revision extends AppCompatActivity {
    private com.google.android.material.floatingactionbutton.FloatingActionButton mSound;
    TextToSpeech t1;
    private Boolean mFirst;
    private int state = 0;
    private int max, lesson;
    private AdView mAdView;
    String[] mEnglish, mRomaji, mJpn;
    private Button mNext, mBack;
    LessonsCompleted lessonsCompleted;
    SelectedItemList selectedItemList;
    private TextView mShowJpn, mShowEnglish, mShowRomaji, mCount;
    private LessonsStorage lessonsStorage = new LessonsStorage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision);

        mFirst  = getIntent().getBooleanExtra("FIRST", false);

        mAdView = findViewById(R.id.adViewRevision);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        lessonsCompleted = (LessonsCompleted) getIntent().getSerializableExtra("COMPLETED");

        mSound = findViewById(R.id.sound);
        if (!getIntent().getBooleanExtra("RETRIEVE", false)){
            max = getIntent().getIntExtra("MAX", Integer.MAX_VALUE);
            lesson = getIntent().getIntExtra("LESSON", Integer.MAX_VALUE);
            mEnglish = getResources().getStringArray(lessonsStorage.getSrcRes(lesson));
            mJpn = getResources().getStringArray(lessonsStorage.getJpRes(lesson));
            mRomaji = getResources().getStringArray(lessonsStorage.getRmRes(lesson));
        }else{
            selectedItemList = (SelectedItemList) getIntent().getSerializableExtra("LESSON");
            assert selectedItemList != null;
            mEnglish = selectedItemList.getmFrench().toArray(new String[0]);
            mJpn = selectedItemList.getmJP().toArray(new String[0]);
            mRomaji = selectedItemList.getmRomaji().toArray(new String[0]);
            max = mEnglish.length;
        }
        mNext = findViewById(R.id.next);
        mBack = findViewById(R.id.back);
        mShowRomaji = findViewById(R.id.romaji);
        mShowEnglish = findViewById(R.id.english);
        mShowJpn = findViewById(R.id.jpn);
        mCount = findViewById(R.id.count);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up();
            }
        });

        mNext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                state = max-3;
                up();
                return false;
            }
        });

        mBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                state = -1;
                up();
                return true;
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state--;
                if (state != -1){
                    refresh();
                }else{
                    state++;
                }
            }
        });

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.JAPAN);
                }
            }
        });

        mSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t1.speak(mShowJpn.getText(), TextToSpeech.QUEUE_FLUSH, null, "1");
            }
        });

        refresh();

    }

    private void up() {
        state++;
        if (state != max){
            refresh();
        }else{
            if(getIntent().getBooleanExtra("REVISION", false)){
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(getApplicationContext(), Exercise.class);
                intent.putExtra("MAX", max);
                if(getIntent().getBooleanExtra("RETRIEVE", false)){
                    intent.putExtra("RETRIEVE", true);
                    intent.putExtra("LESSON", selectedItemList);
                }else{
                    intent.putExtra("LESSON", lesson);
                    intent.putExtra("RETRIEVE", false);
                }
                intent.putExtra("COMPLETED", lessonsCompleted);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mFirst){
            showTuto();

        }
    }

    private void showTuto() {

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_revision, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);


        builder.setPositiveButton("Compris !", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //mSpam = 0;
            }
        });

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialog(){
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //Now we need an AlertDialog.Builder object
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("COMPLETED", lessonsCompleted);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setTitle("Quitter la leçon ?");

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //mSpam = 0;
            }
        });

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    private void refresh() {
        mShowJpn.setText(mJpn[state]);
        mShowEnglish.setText(mEnglish[state]);
        mShowRomaji.setText(mRomaji[state]);
        String s = state+1+"/"+max;
        mCount.setText(s);
    }
}
