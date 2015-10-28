package tourguide.tourguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import java.util.Map;

@SuppressLint("ViewConstructor")
public class FrameLayoutWithHighlights extends FrameLayout {
    private Overlay overlay;
    private boolean cleanUpLock = false;
    private Map<View, ViewHighlight> viewMap;

    private Drawer layoutDrawer;

    public FrameLayoutWithHighlights(Activity context, Map<View, ViewHighlight> viewMap, Overlay overlay) {
        super(context);
        setWillNotDraw(false);
        this.layoutDrawer = new LayoutDrawer(context, overlay, viewMap);
        this.overlay = overlay;
        this.viewMap = viewMap;
    }

    protected void cleanUp() {
        if (getParent() != null) {
            if (overlay != null && overlay.getExitAnimation() != null) {
                performOverlayExitAnimation();
            } else {
                ((ViewGroup) this.getParent()).removeView(this);
            }
        }
    }

    private void performOverlayExitAnimation() {
        if (!cleanUpLock) {
            cleanUpLock = true;
            Log.d("tourguide", "Overlay exit animation listener is overwritten...");
            overlay.getExitAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((ViewGroup) FrameLayoutWithHighlights.this.getParent()).removeView(FrameLayoutWithHighlights.this);
                }
            });
            this.startAnimation(overlay.getExitAnimation());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        layoutDrawer.cleanup();
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        for (View view : viewMap.keySet()) {
            if (view != null) {
                int[] pos = new int[2];
                view.getLocationOnScreen(pos);
                if (ev.getRawY() >= pos[1] && ev.getRawY() <= (pos[1] + view.getHeight()) && ev.getRawX() >= pos[0] && ev.getRawX() <= (pos[0] + view.getWidth())) {
                    return false;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        layoutDrawer.draw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (overlay != null && overlay.getEnterAnimation() != null) {
            this.startAnimation(overlay.getEnterAnimation());
        }
    }
}
