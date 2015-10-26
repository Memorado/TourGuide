package tourguide.tourguide;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;

public class ToolTip {
    private String title, description;
    private int backgroundColor, textColor;
    private Animation enterAnimation;
    private boolean shadow;
    private int gravity;
    private View.OnClickListener onClickListener;

    public ToolTip() {
        title = "";
        description = "";
        backgroundColor = Color.WHITE;
        textColor = Color.BLACK;

        enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(1000);
        enterAnimation.setFillAfter(true);
        enterAnimation.setInterpolator(new BounceInterpolator());
        shadow = true;

        gravity = Gravity.CENTER;
    }

    public ToolTip setTitle(String title) {
        this.title = title;
        return this;
    }

    public ToolTip setDescription(String description) {
        this.description = description;
        return this;
    }

    public ToolTip setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public ToolTip setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public ToolTip setEnterAnimation(Animation enterAnimation) {
        this.enterAnimation = enterAnimation;
        return this;
    }

    public ToolTip setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public ToolTip setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public ToolTip setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public Animation getEnterAnimation() {
        return enterAnimation;
    }

    public boolean isShadow() {
        return shadow;
    }

    public int getGravity() {
        return gravity;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
