package gupta.pranay.mobilecomputingproject.View;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gelitenight.waveview.library.WaveView;

import java.util.ArrayList;

import gupta.pranay.mobilecomputingproject.R;
import gupta.pranay.mobilecomputingproject.Utils.WaveHelper;

public class SpeechToTextActivity extends BaseActivity {
    private static final String TAG = "TAG";
    private ImageView btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private TextView tvResult ;
    private boolean micState = false ;
    SpeechRecognizer recognizer ;
    Intent intent ;


    private WaveHelper waveHelper ;
    private WaveView waveView ;

    private int mBorderColor = Color.parseColor("#33FFEB3B");
    private int mBorderWidth = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);


        waveView = (WaveView) findViewById(R.id.activity_main_waveview) ;
        waveHelper = new WaveHelper(waveView) ;

        waveView.setShapeType(WaveView.ShapeType.SQUARE);


        tvResult = (TextView) findViewById(R.id.activity_main_tv_result) ;
        btnSpeak = (ImageView) findViewById(R.id.activity_main_btn_mic) ;

        initializeSpeechRecognition();

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!micState){
                    micState = true ;
                    recognizer.startListening(intent);
                    waveHelper.start();
                }
                else{
                    micState = false ;
                    recognizer.stopListening();
                    waveHelper.cancel();
                }

            }
        });


    }

    private void initializeSpeechRecognition() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "shreyanshi.mobilecomputingproject.io");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);


        recognizer = SpeechRecognizer
                .createSpeechRecognizer(this.getApplicationContext());


        RecognitionListener recognitionListener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "onReadyForSpeech() called with: params = [" + params + "]");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech() called");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                waveHelper.cancel();
            }

            @Override
            public void onError(int error) {
                Log.e(TAG, "onError() called with: error = [" + error + "]");
                micState = false ;
                waveHelper.cancel();
            }

            @Override
            public void onResults(Bundle results) {
                Log.d(TAG, "onResults() called with: results = [" + results.toString() + "]");
                tvResult.setText(results.getStringArrayList("results_recognition").get(0));
                micState = false ;
                waveHelper.cancel();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.i(TAG, "onPartialResults: " + partialResults.getStringArrayList("results_recognition"));
                tvResult.setText(partialResults.getStringArrayList("results_recognition").get(0));

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        };

        recognizer.setRecognitionListener(recognitionListener);


        /*try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Your device doesn't support speech",
                    Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    for (int i = 0; i < result.size(); i++) {
                        Log.i(TAG, "onActivityResult: TEXT: " + result.get(i));
                        tvResult.setText(result.get(i));
                    }

                }
                break;
            }

        }
    }
}
