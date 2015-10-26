package tourguide.tourguide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;

public class LayoutDrawer implements Drawer {
    private int radius;
    private int[] viewHolePosition;
    private Bitmap eraserBitmap;
    private Canvas eraserCanvas;
    private Paint eraserPaint;
    private Overlay overlay;
    private View viewHole;
    private int rectPadding;

    public LayoutDrawer(Context context, Overlay overlay, View viewHole) {
        this.overlay = overlay;
        this.viewHole = viewHole;

        float density = context.getResources().getDisplayMetrics().density;
        rectPadding = (int) (10 * density);

        int padding = (int) (20 * density);

        if (viewHole.getHeight() > viewHole.getWidth()) {
            radius = viewHole.getHeight() / 2 + padding;
        } else {
            radius = viewHole.getWidth() / 2 + padding;
        }

        int[] pos = new int[2];
        viewHole.getLocationOnScreen(pos);
        viewHolePosition = pos;

        createEraser(context);
    }

    @Override
    public void draw(Canvas canvas) {
        eraserBitmap.eraseColor(Color.TRANSPARENT);

        if (overlay != null) {
            eraserCanvas.drawColor(overlay.getBackgroundColor());
            if (overlay.getStyle() == Overlay.Style.Rectangle) {
                drawRect();
            } else {
                drawCircle();
            }
        }
        canvas.drawBitmap(eraserBitmap, 0, 0, null);
    }

    @Override
    public void cleanup() {
        eraserCanvas.setBitmap(null);
        eraserBitmap = null;
    }

    private void drawCircle() {
        int centerX = viewHolePosition[0] + viewHole.getWidth() / 2;
        int centerY = viewHolePosition[1] + viewHole.getHeight() / 2;
        eraserCanvas.drawCircle(centerX, centerY, radius, eraserPaint);
    }

    private void drawRect() {
        int left = viewHolePosition[0] - rectPadding;
        int top = viewHolePosition[1] - rectPadding;
        int right = viewHolePosition[0] + viewHole.getWidth() + rectPadding;
        int bottom = viewHolePosition[1] + viewHole.getHeight() + rectPadding;

        eraserCanvas.drawRect(left, top, right, bottom, eraserPaint);
    }

    private Paint createEraserPaint() {
        Paint paint = new Paint();
        paint.setColor(0xFFFFFFFF);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        return paint;
    }

    private void createEraser(Context context) {
        Point size = new Point();
        size.x = context.getResources().getDisplayMetrics().widthPixels;
        size.y = context.getResources().getDisplayMetrics().heightPixels;
        eraserBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        eraserCanvas = new Canvas(eraserBitmap);
        eraserPaint = createEraserPaint();
    }
}
