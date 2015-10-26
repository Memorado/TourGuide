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
    private Style style;
    private Animation enterAnimation, exitAnimation;
    private View.OnClickListener onClickListener;

    public enum Style {
        Circle, Rectangle
    }

    public Overlay() {
        this(true, Color.parseColor("#55000000"), Style.Circle);
    }

    public Overlay(boolean disableClick, int backgroundColor, Style style) {
        this.disableClick = disableClick;
        this.backgroundColor = backgroundColor;
        this.style = style;
    }

    public Overlay setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Overlay disableClick(boolean yes_no) {
        disableClick = yes_no;
        return this;
    }

    public Overlay setStyle(Style style) {
        this.style = style;
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

    public Style getStyle() {
        return style;
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
