package example.gifmanager;

import android.os.Bundle;
import android.view.Window;

/**
 * Created by FIlip on 2018-04-10.
 */

public class ScreenThree extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }
}
