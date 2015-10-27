package tourguide.tourguide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Map;

public class LayoutDrawer implements Drawer {
    private Bitmap eraserBitmap;
    private Canvas eraserCanvas;
    private Paint eraserPaint;
    private Overlay overlay;
    private Map<View, ViewHighlight> viewMap;
    private int rectPadding;
    private int circlePadding;

    public LayoutDrawer(Context context,
                        @NonNull Overlay overlay,
                        @NonNull Map<View, ViewHighlight> viewMap) {
        this.overlay = overlay;
        this.viewMap = viewMap;

        createPaddings(context);
        createEraser(context);
    }

    @Override
    public void draw(Canvas canvas) {
        eraserBitmap.eraseColor(Color.TRANSPARENT);

        eraserCanvas.drawColor(overlay.getBackgroundColor());

        for (Map.Entry<View, ViewHighlight> entry : viewMap.entrySet()) {
            View view = entry.getKey();
            ViewHighlight viewHighlight = entry.getValue();

            drawInternal(view, viewHighlight);
        }

        canvas.drawBitmap(eraserBitmap, 0, 0, null);
    }

    @Override
    public void cleanup() {
        eraserBitmap = null;
        eraserCanvas.setBitmap(null);
    }

    private void drawInternal(View view, ViewHighlight viewHighlight) {
        if (viewHighlight.getStyle() == ViewHighlight.Style.RECT) {
            drawRect(view, viewHighlight);
        } else if (viewHighlight.getStyle() == ViewHighlight.Style.CIRCLE) {
            drawCircle(view, viewHighlight);
        }
    }

    private void createPaddings(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        rectPadding = (int) (10 * density);
        circlePadding = (int) (20 * density);
    }

    private void drawCircle(View view, ViewHighlight viewHighlight) {
        int centerX = viewHighlight.getX() + view.getWidth() / 2;
        int centerY = viewHighlight.getY() + view.getHeight() / 2;
        eraserCanvas.drawCircle(centerX, centerY, viewHighlight.getRadius() + circlePadding, eraserPaint);

        Paint paintBlur = new Paint();
        paintBlur.set(eraserPaint);
        paintBlur.setColor(Color.WHITE);
        paintBlur.setStrokeWidth(30f);
        paintBlur.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.OUTER));
        eraserCanvas.drawCircle(centerX, centerY, viewHighlight.getRadius() + circlePadding, paintBlur);
    }

    private void drawRect(View view, ViewHighlight viewHighlight) {
        int left = viewHighlight.getX() - rectPadding;
        int top = viewHighlight.getY() - rectPadding;
        int right = viewHighlight.getX() + view.getWidth() + rectPadding;
        int bottom = viewHighlight.getY() + view.getHeight() + rectPadding;

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
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        eraserBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        eraserCanvas = new Canvas(eraserBitmap);
        eraserPaint = createEraserPaint();
    }
}
