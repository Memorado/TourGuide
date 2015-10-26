package tourguide.tourguide;

import android.view.Gravity;
import android.view.View;

public class TooltipCalculator {

    private final View highlightedView;

    public TooltipCalculator(View highlightedView) {
        this.highlightedView = highlightedView;
    }

    public int getXForTooTip(int gravity, int toolTipMeasuredWidth, int targetViewX) {
        int x;
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            x = targetViewX - toolTipMeasuredWidth;
        } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            x = targetViewX + highlightedView.getWidth();
        } else {
            x = targetViewX + highlightedView.getWidth() / 2 - toolTipMeasuredWidth / 2;
        }
        return x;
    }

    public int getYForTooTip(int gravity, int toolTipMeasuredHeight, int targetViewY) {
        int y;
        if ((gravity & Gravity.TOP) == Gravity.TOP) {

            if (((gravity & Gravity.LEFT) == Gravity.LEFT) || ((gravity & Gravity.RIGHT) == Gravity.RIGHT)) {
                y = targetViewY - toolTipMeasuredHeight;
            } else {
                y = targetViewY - toolTipMeasuredHeight;
            }
        } else { // this is center
            if (((gravity & Gravity.LEFT) == Gravity.LEFT) || ((gravity & Gravity.RIGHT) == Gravity.RIGHT)) {
                y = targetViewY + highlightedView.getHeight();
            } else {
                y = targetViewY + highlightedView.getHeight();
            }
        }
        return y;
    }
}
