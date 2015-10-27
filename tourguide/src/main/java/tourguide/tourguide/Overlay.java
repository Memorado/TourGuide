package tourguide.tourguide;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by tanjunrong on 6/20/15.
 */
public class Overlay {
    private int backgroundColor;
    private boolean disableClick;
    private Animation enterAnimation, exitAnimation;
    private View.OnClickListener onClickListener;

    public Overlay() {
        this(true, Color.parseColor("#55000000"));
    }

    public Overlay(boolean disableClick, int backgroundColor) {
        this.disableClick = disableClick;
        this.backgroundColor = backgroundColor;
    }

    public Overlay setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Overlay disableClick(boolean disableClick) {
        this.disableClick = disableClick;
        return this;
    }

    public Overlay setEnterAnimation(Animation enterAnimation) {
        this.enterAnimation = enterAnimation;
        return this;
    }

    public Overlay setExitAnimation(Animation exitAnimation) {
        this.exitAnimation = exitAnimation;
        return this;
    }

    public Overlay setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isDisableClick() {
        return disableClick;
    }

    public Animation getEnterAnimation() {
        return enterAnimation;
    }

    public Animation getExitAnimation() {
        return exitAnimation;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
