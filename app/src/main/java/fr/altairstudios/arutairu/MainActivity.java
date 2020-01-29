package fr.altairstudios.arutairu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ImageView mAltair;
    private TextView mArutairu, mCurrentLanguage;
    private com.google.android.material.button.MaterialButton mStart;
    private Animation fadeAltair2;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fadeAltair2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade2);

        mAltair = findViewById(R.id.altair);
        mCurrentLanguage = findViewById(R.id.currentLanguage);
        mStart = findViewById(R.id.startBtn);
        mArutairu = findViewById(R.id.txtArutairu);
        mAltair.setVisibility(View.INVISIBLE);
        mArutairu.setAlpha(0f);
        mStart.setAlpha(0f);
        mCurrentLanguage.setAlpha(0f);

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Revision.class);
                intent.putExtra("LESSON", 1);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Animation fadeAltair =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade);
        mAltair.startAnimation(fadeAltair);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mArutairu.setAlpha(1f);
                mStart.setAlpha(1f);
                mCurrentLanguage.setAlpha(1f);
                mArutairu.startAnimation(fadeAltair2);
                mStart.startAnimation(fadeAltair2);
                mCurrentLanguage.startAnimation(fadeAltair2);
            }
        },3000);

    }
}
