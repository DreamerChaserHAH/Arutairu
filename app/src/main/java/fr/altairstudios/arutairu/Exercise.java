package fr.altairstudios.arutairu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class Exercise extends AppCompatActivity {
    private TextInputEditText mAnswer;
    private MaterialButton mSubmit;
    private ImageView mcheck;
    private SelectedItemList selectedItemList;
    private InterstitialAd mInterstitialAd;
    public static final String ARUTAIRU_SHARED_PREFS = "ArutairuSharedPrefs";
    private MaterialTextView mText, mState;
    private LessonsStorage lessonsStorage = new LessonsStorage();
    private int state = 0;
    private boolean completed = true;
    private int max, lesson;
    String[] mEnglish, mRomaji, mJpn;
    private String mAnswerText;
    LessonsCompleted lessonsCompleted;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9369103706924521/2427690661");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                SharedPreferences sharedPreferences = getSharedPreferences(ARUTAIRU_SHARED_PREFS, MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(Integer.toString(lesson), completed).apply();
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("COMPLETED", lessonsCompleted);
                startActivity(intent);
                finish();
            }
        });

        mAdView = findViewById(R.id.adViewExercise);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        lessonsCompleted = (LessonsCompleted) getIntent().getSerializableExtra("COMPLETED");

        if (!getIntent().getBooleanExtra("RETRIEVE", false)){
            lesson = getIntent().getIntExtra("LESSON", Integer.MAX_VALUE);
            mEnglish = getResources().getStringArray(lessonsStorage.getSrcRes(lesson));
            mJpn = getResources().getStringArray(lessonsStorage.getJpRes(lesson));
            mRomaji = getResources().getStringArray(lessonsStorage.getRmRes(lesson));
            max = getIntent().getIntExtra("MAX", Integer.MAX_VALUE);
        }else{
            selectedItemList = (SelectedItemList) getIntent().getSerializableExtra("LESSON");
            assert selectedItemList != null;
            mEnglish = selectedItemList.getmFrench().toArray(new String[0]);
            mJpn = selectedItemList.getmJP().toArray(new String[0]);
            mRomaji = selectedItemList.getmRomaji().toArray(new String[0]);
        }

        max = getIntent().getIntExtra("MAX", Integer.MAX_VALUE);

        mcheck = findViewById(R.id.checkExercise);

        mAnswer = findViewById(R.id.Answer);
        mSubmit = findViewById(R.id.submitBtn);
        mText = findViewById(R.id.txtArutairu);
        mState = findViewById(R.id.state);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!getIntent().getBooleanExtra("RETRIEVE", false)){
                    if(mAnswer.getText().toString().equals(mJpn[state])){

                        if (completed && !lessonsCompleted.isCompleted(lesson, state)){
                            lessonsCompleted.addCompleted(lesson, state);
                        }

                        mSubmit.setBackgroundColor(getResources().getColor(R.color.green));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                state++;
                                if (state != max){
                                    completed = true;
                                    refresh();
                                    mAnswer.setText("");
                                }else{
                                    congrats();
                                }
                                mSubmit.setBackgroundColor(getResources().getColor(R.color.grey));
                            }
                        },1000);
                    }else{
                        completed = false;
                        showDialog();

                    }
                }else{
                    if(mAnswer.getText().toString().equals(mJpn[state])){

                        //if (completed && !lessonsCompleted.isCompleted(selectedItemList.getCorrespondingLesson()+1, selectedItemList.getCorrespondingIndex(state))){
                            //lessonsCompleted.addCompleted(selectedItemList.getCorrespondingLesson()+1, selectedItemList.getCorrespondingIndex(state));
                        //}

                        mSubmit.setBackgroundColor(getResources().getColor(R.color.green));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                state++;
                                if (state != max){
                                    completed = true;
                                    refresh();
                                    mAnswer.setText("");
                                }else{
                                    congrats();
                                }
                                mSubmit.setBackgroundColor(getResources().getColor(R.color.grey));
                            }
                        },1000);
                    }else{
                        completed = false;
                        showDialog();

                    }

                }
            }
        });

        refresh();
    }

    private void congrats() {

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.congrats_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);


        builder.setPositiveButton("Revenir aux leçons", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                    SharedPreferences sharedPreferences = getSharedPreferences(ARUTAIRU_SHARED_PREFS, MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(Integer.toString(lesson), completed).apply();
                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                    intent.putExtra("COMPLETED", lessonsCompleted);
                    startActivity(intent);
                    finish();
                }
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

    private void exit() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created

        //Now we need an AlertDialog.Builder object
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated


        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("COMPLETED", lessonsCompleted);
                startActivity(intent);
                finish();
            }
        });

        builder.setTitle("Quitter la leçons ?");

        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    @Override
    public void onBackPressed() {
        exit();

    }

    private void refresh() {
        if(!getIntent().getBooleanExtra("RETRIEVE", false)){
            if (lessonsCompleted.isCompleted(lesson, state)){
                mcheck.setAlpha(1f);
            }else{
                mcheck.setAlpha(0f);
            }
        }else{

            if(lessonsCompleted.isCompleted(selectedItemList.getCorrespondingLesson()+1, selectedItemList.getCorrespondingIndex(state))){
                mcheck.setAlpha(1f);
            }else{
                mcheck.setAlpha(0f);
            }
        }
        mText.setText(mEnglish[state]);
        String s = state+1+"/"+max;
        mState.setText(s);
    }

    private void showDialog() {

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(mJpn[state]);


        builder.setPositiveButton("OK !", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
}
