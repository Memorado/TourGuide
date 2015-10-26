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

@SuppressLint("ViewConstructor")
public class FrameLayoutWithHole extends FrameLayout {
    private View viewHole;

    private Overlay overlay;
    private boolean cleanUpLock = false;

    private Drawer layoutDrawer;

    public void setViewHole(View viewHole) {
        this.viewHole = viewHole;
    }

    public FrameLayoutWithHole(Activity context, View view, Overlay overlay) {
        super(context);
        setWillNotDraw(false);
        this.viewHole = view;
        this.layoutDrawer = new LayoutDrawer(context, overlay, view);
        this.overlay = overlay;
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
                    ((ViewGroup) FrameLayoutWithHole.this.getParent()).removeView(FrameLayoutWithHole.this);
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
        //first check if the location button should handle the touch event
        if (viewHole != null) {
            int[] pos = new int[2];
            viewHole.getLocationOnScreen(pos);
            if (ev.getRawY() >= pos[1] && ev.getRawY() <= (pos[1] + viewHole.getHeight()) && ev.getRawX() >= pos[0] && ev.getRawX() <= (pos[0] + viewHole.getWidth())) { //location button event
                Log.d("tourguide", "to the BOTTOM!");
                Log.d("tourguide", "" + ev.getAction());
                return false;
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
