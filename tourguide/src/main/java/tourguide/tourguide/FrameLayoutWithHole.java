package tourguide.tourguide;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;


public class FrameLayoutWithHole extends FrameLayout {
    private Activity mActivity;
    private Paint mEraser;

    Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private View mViewHole;
    private int mRadius;
    private int[] mViewHolePosition;
    private float mDensity;
    private Overlay mOverlay;

    private boolean mCleanUpLock = false;

    public void setViewHole(View viewHole) {
        this.mViewHole = viewHole;
    }


    public FrameLayoutWithHole(Activity context, View view) {
        this(context, view, new Overlay());
    }

    public FrameLayoutWithHole(Activity context, View view, Overlay overlay) {
        super(context);
        mActivity = context;
        mViewHole = view;
        init();
        mOverlay = overlay;

        int[] pos = new int[2];
        mViewHole.getLocationOnScreen(pos);
        mViewHolePosition = pos;

        mDensity = context.getResources().getDisplayMetrics().density;
        int padding = (int) (20 * mDensity);

        if (mViewHole.getHeight() > mViewHole.getWidth()) {
            mRadius = mViewHole.getHeight() / 2 + padding;
        } else {
            mRadius = mViewHole.getWidth() / 2 + padding;
        }
    }

    private void init() {
        setWillNotDraw(false);

        Point size = new Point();
        size.x = mActivity.getResources().getDisplayMetrics().widthPixels;
        size.y = mActivity.getResources().getDisplayMetrics().heightPixels;

        mEraserBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);

        mEraser = new Paint();
        mEraser.setColor(0xFFFFFFFF);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraser.setFlags(Paint.ANTI_ALIAS_FLAG);

        Log.d("tourguide", "getHeight: " + size.y);
        Log.d("tourguide", "getWidth: " + size.x);

    }

    protected void cleanUp() {
        if (getParent() != null) {
            if (mOverlay != null && mOverlay.mExitAnimation != null) {
                performOverlayExitAnimation();
            } else {
                ((ViewGroup) this.getParent()).removeView(this);
            }
        }
    }

    private void performOverlayExitAnimation() {
        if (!mCleanUpLock) {
            final FrameLayout _pointerToFrameLayout = this;
            mCleanUpLock = true;
            Log.d("tourguide", "Overlay exit animation listener is overwritten...");
            mOverlay.mExitAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((ViewGroup) _pointerToFrameLayout.getParent()).removeView(_pointerToFrameLayout);
                }
            });
            this.startAnimation(mOverlay.mExitAnimation);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mEraserCanvas.setBitmap(null);
        mEraserBitmap = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //first check if the location button should handle the touch event
        if (mViewHole != null) {
            int[] pos = new int[2];
            mViewHole.getLocationOnScreen(pos);
            Log.d("tourguide", "[dispatchTouchEvent] mViewHole.getHeight(): " + mViewHole.getHeight());
            Log.d("tourguide", "[dispatchTouchEvent] mViewHole.getWidth(): " + mViewHole.getWidth());

            Log.d("tourguide", "[dispatchTouchEvent] Touch X(): " + ev.getRawX());
            Log.d("tourguide", "[dispatchTouchEvent] Touch Y(): " + ev.getRawY());

            Log.d("tourguide", "[dispatchTouchEvent] X lower bound: " + pos[0]);
            Log.d("tourguide", "[dispatchTouchEvent] X higher bound: " + (pos[0] + mViewHole.getWidth()));

            Log.d("tourguide", "[dispatchTouchEvent] Y lower bound: " + pos[1]);
            Log.d("tourguide", "[dispatchTouchEvent] Y higher bound: " + (pos[1] + mViewHole.getHeight()));

            if (ev.getRawY() >= pos[1] && ev.getRawY() <= (pos[1] + mViewHole.getHeight()) && ev.getRawX() >= pos[0] && ev.getRawX() <= (pos[0] + mViewHole.getWidth())) { //location button event
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
        mEraserBitmap.eraseColor(Color.TRANSPARENT);

        if (mOverlay != null) {
            mEraserCanvas.drawColor(mOverlay.mBackgroundColor);
            int padding = (int) (10 * mDensity);
            if (mOverlay.mStyle == Overlay.Style.Rectangle) {
                mEraserCanvas.drawRect(mViewHolePosition[0] - padding, mViewHolePosition[1] - padding, mViewHolePosition[0] + mViewHole.getWidth() + padding, mViewHolePosition[1] + mViewHole.getHeight() + padding, mEraser);
            } else {
                mEraserCanvas.drawCircle(mViewHolePosition[0] + mViewHole.getWidth() / 2, mViewHolePosition[1] + mViewHole.getHeight() / 2, mRadius, mEraser);
            }
        }
        canvas.drawBitmap(mEraserBitmap, 0, 0, null);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mOverlay != null && mOverlay.mEnterAnimation != null) {
            this.startAnimation(mOverlay.mEnterAnimation);
        }
    }
}
