package tourguide.tourguidedemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;


public class BasicActivity extends ActionBarActivity {
    public TourGuide mTutorialHandler;
    public Activity mActivity;
    public static final String COLOR_DEMO = "color_demo";
    public static final String GRAVITY_DEMO = "gravity_demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_basic);

        Button button = (Button) findViewById(R.id.button);

        ToolTip toolTip = new ToolTip()
                .setTitle(null)
                .setDescription("Click on Get Started to begin...")
                .setGravity(Gravity.TOP);
        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        mTutorialHandler = TourGuide.init(this)
                .setToolTip(toolTip, findViewById(R.id.tooltipAnchor))
                .setOverlay(new Overlay().setBackgroundColor(Color.parseColor("#66FF0000")).setEnterAnimation(enterAnimation))
                .playOn(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTutorialHandler.cleanUp();
            }
        });
    }
}
