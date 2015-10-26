package tourguide.tourguide;

import android.view.Gravity;
import android.view.View;

public class TooltipCalculator {

    private final View highlightedView;

    public TooltipCalculator(View highlightedView) {
        this.highlightedView = highlightedView;
    }

    public int getXForTooTip(int gravity, int toolTipMeasuredWidth, int targetViewX, float adjustment) {
        int x;
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            x = targetViewX - toolTipMeasuredWidth + (int) adjustment;
        } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            x = targetViewX + highlightedView.getWidth() - (int) adjustment;
        } else {
            x = targetViewX + highlightedView.getWidth() / 2 - toolTipMeasuredWidth / 2;
        }
        return x;
    }

    public int getYForTooTip(int gravity, int toolTipMeasuredHeight, int targetViewY, float adjustment) {
        int y;
        if ((gravity & Gravity.TOP) == Gravity.TOP) {

            if (((gravity & Gravity.LEFT) == Gravity.LEFT) || ((gravity & Gravity.RIGHT) == Gravity.RIGHT)) {
                y = targetViewY - toolTipMeasuredHeight + (int) adjustment;
            } else {
                y = targetViewY - toolTipMeasuredHeight - (int) adjustment;
            }
        } else { // this is center
            if (((gravity & Gravity.LEFT) == Gravity.LEFT) || ((gravity & Gravity.RIGHT) == Gravity.RIGHT)) {
                y = targetViewY + highlightedView.getHeight() - (int) adjustment;
            } else {
                y = targetViewY + highlightedView.getHeight() + (int) adjustment;
            }
        }
        return y;
    }
}
