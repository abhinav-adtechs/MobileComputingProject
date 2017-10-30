package gupta.pranay.mobilecomputingproject;

import android.app.Application;


public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/PTSans.ttf");
    }
}
