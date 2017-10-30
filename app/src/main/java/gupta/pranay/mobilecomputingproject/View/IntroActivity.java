package gupta.pranay.mobilecomputingproject.View;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import gupta.pranay.mobilecomputingproject.R;

public class IntroActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.btn_blind)
    Button btnAlarm;

    @BindView(R.id.btn_deaf)
    Button btnSpeechToText;

    @BindView(R.id.btn_dumb)
    Button btnTextToSpeech;

    Intent intent ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this) ;

        btnAlarm.setOnClickListener(this);
        btnSpeechToText.setOnClickListener(this);
        btnTextToSpeech.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_blind :
                intent = new Intent(this, AlarmActivity.class) ;
                break;
            case R.id.btn_deaf :
                intent = new Intent(this, SpeechToTextActivity.class) ;
                break;
            case R.id.btn_dumb :
                intent = new Intent(this, TextToSpeechActivity.class) ;
                break;
        }

        startActivity(intent);
    }
}
