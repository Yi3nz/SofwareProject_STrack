package sarah.nci.ie.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Splash activity - Reference: https://www.youtube.com/watch?v=h_hTuaEpc-8
 */

public class SplashActivity extends AppCompatActivity {
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        iv = (ImageView) findViewById(R.id.splashImage);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        iv.startAnimation(myanim);

        //
        final Intent i = new Intent(this, MainActivity.class);

        Thread timer = new Thread(){
            public void run (){
                try{
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally{
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}
