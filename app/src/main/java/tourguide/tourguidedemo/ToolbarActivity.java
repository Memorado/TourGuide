package tourguide.tourguidedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;
import tourguide.tourguide.ViewHighlight;


public class ToolbarActivity extends ActionBarActivity {
    public TourGuide mTutorialHandler;
    public Activity mActivity;
    public static final String STATUS_BAR = "status_bar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Get parameters from main activity */
        Intent intent = getIntent();
        boolean status_bar = intent.getBooleanExtra(STATUS_BAR, false);
        if (!status_bar) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);
        mActivity = this;

        setContentView(R.layout.activity_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        getMenuInflater().inflate(R.menu.menu_demo_main, menu);

        // We need to get the menu item as a View in order to work with TourGuide
        MenuItem menuItem = menu.getItem(0);
        ImageView button = (ImageView) menuItem.getActionView();

        // just adding some padding to look better
        float density = mActivity.getResources().getDisplayMetrics().density;
        int padding = (int) (5 * density);
        button.setPadding(padding, padding, padding, padding);

        // set an image
        button.setImageDrawable(mActivity.getResources().getDrawable(android.R.drawable.ic_dialog_email));

        ToolTip toolTip = new ToolTip()
                .setTitle("Welcome!")
                .setDescription("Click on Get Started to begin...")
                .setGravity(Gravity.LEFT | Gravity.BOTTOM);

        mTutorialHandler = TourGuide.init(this)
                .setToolTip(toolTip, button)
                .setOverlay(new Overlay())
                .addTarget(button, ViewHighlight.Style.CIRCLE)
                .play();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTutorialHandler.cleanUp();
            }
        });

        return true;
    }
}
