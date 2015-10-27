package tourguide.tourguidedemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;
import tourguide.tourguide.ViewHighlight;


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

        final View container = findViewById(R.id.container);
        final Button button1 = (Button) findViewById(R.id.button);
        final Button button2 = (Button) findViewById(R.id.button2);

        final ToolTip toolTip = new ToolTip()
                .setTitle(null)
                .setDescription("Click on Get Started to begin...")
                .setGravity(Gravity.TOP);
        final Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        final ViewTreeObserver viewTreeObserver = container.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                mTutorialHandler = TourGuide.init(BasicActivity.this)
                        .setToolTip(toolTip, findViewById(R.id.tooltipAnchor))
                        .setOverlay(new Overlay().setBackgroundColor(Color.parseColor("#A9020C2C")).setEnterAnimation(enterAnimation))
                        .addTarget(button1, ViewHighlight.Style.CIRCLE)
                        .addTarget(button2, ViewHighlight.Style.RECT)
                        .play();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTutorialHandler.cleanUp();
            }
        });
    }
}
