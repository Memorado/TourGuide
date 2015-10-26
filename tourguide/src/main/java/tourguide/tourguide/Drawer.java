package tourguide.tourguide;

import android.graphics.Canvas;

public interface Drawer {
    void draw(Canvas canvas);
    void cleanup();
}
