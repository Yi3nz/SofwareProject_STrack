package sarah.nci.ie.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Up-to-down Animation - Reference: https://www.youtube.com/watch?v=pznCs--BtJA
 *
 * On enter app, display a splash screen with a up-to-down animation.
 * On start button click, enter the main activity.
 */

public class Activity_Splash extends AppCompatActivity {

    //Define the xml elements
    private Button btnStart;
    LinearLayout upperPart;

    //Define the animation
    Animation uptodown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        //Define the xml elements
        btnStart = (Button) findViewById(R.id.btnStart);
        upperPart = (LinearLayout) findViewById(R.id.upperPart);

        //Define the animation xml file
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        upperPart.setAnimation(uptodown);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Define new intent - Activity_main (The ListView page)
                final Intent intent = new Intent(Activity_Splash.this, Activity_Main.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
