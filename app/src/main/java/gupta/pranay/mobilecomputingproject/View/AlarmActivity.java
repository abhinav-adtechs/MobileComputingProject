package gupta.pranay.mobilecomputingproject.View;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.activity_main_chart)
    LineChart lineChart ;

    private List<Entry> entryList = new ArrayList<>() ;
    private LineDataSet dataSet ;
    private LineData lineData ;

    private boolean btnState = false ;
    int i = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this) ;

        setupChart() ;
    }

    private void setupChart() {

        entryList.add(new Entry(1, 1)) ;

        lineChart.setVisibility(View.INVISIBLE);

        dataSet = new LineDataSet(entryList, "Label");
        dataSet.setValueTextSize(0);
        dataSet.setCircleRadius(0);
        dataSet.setCircleColor(getResources().getColor(R.color.colorYellowBase));
        dataSet.setColor(getResources().getColor(R.color.colorYellowBase));
        dataSet.setValueTextColor(getResources().getColor(R.color.colorPeachLow));

        lineData = new LineData(dataSet);

        Description description = new Description() ;
        description.setText("");

        lineChart.setDescription(description);
        lineChart.setData(lineData);

        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.setTouchEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);


        lineChart.getAxisLeft().setAxisMaximum(50);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.getLegend().setEnabled(false);
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawHighlightIndicators(false);

        lineChart.getXAxis().setEnabled(false);



        YAxis yAxis = new YAxis() ;
        yAxis.setEnabled(false);

        lineChart.getAxis(yAxis.getAxisDependency()).setDrawGridLines(false);



        lineChart.invalidate();
    }

    @OnClick(R.id.btn_start_recording)
    public void btnClickHandle(){

        if (checkForPermissions()){
            if (!btnState){
                lineChart.setVisibility(View.VISIBLE);
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
                                            dataSet.addEntry(new Entry(i++, (float)(-20 * Math.log10(ampli / 32767.0)))) ;

                                            lineChart.setVisibleXRangeMaximum(15);
                                            lineChart.moveViewToX(lineData.getXMax() - 16);

                                            lineData.notifyDataChanged() ;
                                            lineChart.notifyDataSetChanged();
                                            lineChart.invalidate();

                                            if(getAmplitude() > 0)
                                                tvLoudness.setText("~ " + (20 * Math.log10(ampli / 32767.0)) + " db");
                                        }
                                    }
                            );

                            Log.i(TAG, "run: " + (20 * Math.log10(ampli / 32767.0)) + "    : RAW: " + ampli);

                            try {
                                Thread.sleep(400);
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
        if (mRecorder != null)
            mRecorder.release();
        super.onPause();
    }
}
