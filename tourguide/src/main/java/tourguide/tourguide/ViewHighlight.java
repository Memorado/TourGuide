package tourguide.tourguide;

import android.view.View;

public class ViewHighlight {
    private int x;
    private int y;
    private int radius;
    private Style style;

    public enum Style {
        CIRCLE,
        RECT
    }

    public static ViewHighlight from(View view, Style style) {
        int radius;

        if (view.getHeight() > view.getWidth()) {
            radius = view.getHeight() / 2;
        } else {
            radius = view.getWidth() / 2;
        }

        int[] pos = new int[2];
        view.getLocationOnScreen(pos);
        int x = pos[0];
        int y = pos[1];

        return new ViewHighlight(x, y, radius, style);
    }

    public ViewHighlight(int x, int y, int radius, Style style) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.style = style;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public Style getStyle() {
        return style;
    }
}
