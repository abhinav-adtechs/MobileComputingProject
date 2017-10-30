package gupta.pranay.mobilecomputingproject.View;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gupta.pranay.mobilecomputingproject.R;

public class AlarmActivity extends BaseActivity {

    private static final String TAG = "TAG";
    private static final int AUDIO_RESPONSE_CONSTANT = 112;
    private MediaRecorder mRecorder = null;

    @BindView(R.id.btn_start_recording)
    Button btnRecording ;

    @BindView(R.id.activity_main_tv_btn_state)
    TextView tvState ;

    @BindView(R.id.activity_main_tv_db)
    TextView tvLoudness ;

    private boolean btnState = false ;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this) ;
    }

    @OnClick(R.id.btn_start_recording)
    public void btnClickHandle(){

        if (checkForPermissions()){
            if (!btnState){
                btnState = true;
                try{
                    start();
                }catch (IOException ioe){
                    Log.i(TAG, "Could not initiate media recorder");
                }
                tvState.setText("State: Pressed");

                startThreadWork() ;
            }else{
                btnState = false ;
                stop();
                tvState.setText("State: Unpressed");
            }
        }


    }

    private boolean checkForPermissions() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_RESPONSE_CONSTANT);
                return false ;
            }else {
                return true ;
            }
        }else {
            return true ;
        }



    }

    private void startThreadWork() {

                /*final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        while (btnState){
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            tvLoudness.setText("~ " + getAmplitude() + " db");
                                        }
                                    }
                            );

                            Log.i(TAG, "run: " + getAmplitude());
                        }
                    }
                }, 1000);*/


    Thread thread = new Thread(){

            @Override
            public void run() {

                        while (btnState){

                            final double ampli = getAmplitude() ;
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            if(getAmplitude() > 0)
                                                tvLoudness.setText("~ " + (20 * Math.log10(ampli / 32767.0)) + " db");
                                        }
                                    }
                            );

                            Log.i(TAG, "run: " + (20 * Math.log10(ampli / 32767.0)) + "    : RAW: " + ampli);

                            try {
                                Thread.sleep(700);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

            }
        };

        thread.start();


    }


    public void start() throws IOException {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            mRecorder.prepare();
            mRecorder.start();
        }
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  mRecorder.getMaxAmplitude();
        else
            return 0;

    }

    @Override
    protected void onPause() {
        mRecorder.release();
        super.onPause();
    }
}
