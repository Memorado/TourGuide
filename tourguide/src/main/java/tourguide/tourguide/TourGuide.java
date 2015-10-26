package tourguide.tourguide;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TourGuide {

    private View mHighlightedView;
    private Activity mActivity;
    private FrameLayoutWithHole mFrameLayout;
    private View mToolTipViewGroup;
    public ToolTip mToolTip;
    public Overlay mOverlay;

    private Sequence mSequence;

    public static TourGuide init(Activity activity) {
        return new TourGuide(activity);
    }

    public TourGuide(Activity activity) {
        mActivity = activity;
    }

    /**
     * Sets the duration
     *
     * @param view the view in which the tutorial button will be placed on top of
     * @return return TourGuide instance for chaining purpose
     */
    public TourGuide playOn(View view) {
        mHighlightedView = view;
        setupView();
        return this;
    }

    /**
     * Sets the overlay
     *
     * @param overlay this overlay object should contain the attributes of the overlay, such as background color, animation, Style, etc
     * @return return TourGuide instance for chaining purpose
     */
    public TourGuide setOverlay(Overlay overlay) {
        mOverlay = overlay;
        return this;
    }

    /**
     * Set the toolTip
     *
     * @param toolTip this toolTip object should contain the attributes of the ToolTip, such as, the title text, and the description text, background color, etc
     * @return return TourGuide instance for chaining purpose
     */
    public TourGuide setToolTip(ToolTip toolTip) {
        mToolTip = toolTip;
        return this;
    }

    /**
     * Clean up the tutorial that is added to the activity
     */
    public void cleanUp() {
        mFrameLayout.cleanUp();
        if (mToolTipViewGroup != null) {
            ((ViewGroup) mActivity.getWindow().getDecorView()).removeView(mToolTipViewGroup);
        }
    }

    public TourGuide playLater(View view) {
        mHighlightedView = view;
        return this;
    }

    /**************************
     * Sequence related method
     **************************/

    public TourGuide playInSequence(Sequence sequence) {
        setSequence(sequence);
        next();
        return this;
    }

    public TourGuide setSequence(Sequence sequence) {
        mSequence = sequence;
        mSequence.setParentTourGuide(this);
        for (TourGuide tourGuide : sequence.mTourGuideArray) {
            if (tourGuide.mHighlightedView == null) {
                throw new NullPointerException("Please specify the view using 'playLater' method");
            }
        }
        return this;
    }

    public TourGuide next() {
        if (mFrameLayout != null) {
            cleanUp();
        }

        if (mSequence.mCurrentSequence < mSequence.mTourGuideArray.length) {
            setToolTip(mSequence.getToolTip());
            setOverlay(mSequence.getOverlay());

            mHighlightedView = mSequence.getNextTourGuide().mHighlightedView;

            setupView();
            mSequence.mCurrentSequence++;
        }
        return this;
    }

    /**
     * @return FrameLayoutWithHole that is used as overlay
     */
    public FrameLayoutWithHole getOverlay() {
        return mFrameLayout;
    }

    /**
     * @return the ToolTip container View
     */
    public View getToolTip() {
        return mToolTipViewGroup;
    }

    private void setupView() {
        final ViewTreeObserver viewTreeObserver = mHighlightedView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHighlightedView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                mFrameLayout = new FrameLayoutWithHole(mActivity, mHighlightedView, mOverlay);
                handleDisableClicking(mFrameLayout);
                setupFrameLayout(mFrameLayout);
                setupToolTip(mToolTip);
            }
        });
    }

    private void handleDisableClicking(FrameLayoutWithHole frameLayoutWithHole) {
        // 1. if user provides an overlay listener, use that as 1st priority
        if (mOverlay != null && mOverlay.getOnClickListener() != null) {
            frameLayoutWithHole.setClickable(true);
            frameLayoutWithHole.setOnClickListener(mOverlay.getOnClickListener());
        }
        // 2. if overlay listener is not provided, check if it's disabled
        else if (mOverlay != null && mOverlay.isDisableClick()) {
            Log.w("tourguide", "Overlay's default OnClickListener is null, it will proceed to next tourguide when it is clicked");
            frameLayoutWithHole.setViewHole(mHighlightedView);
            frameLayoutWithHole.setSoundEffectsEnabled(false);
            frameLayoutWithHole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                } // do nothing, disabled.
            });
        }
    }

    private void setupToolTip(final ToolTip toolTip) {
        if (toolTip == null) {
            return;
        }

        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        ViewGroup parent = (ViewGroup) mActivity.getWindow().getDecorView();

        mToolTipViewGroup = initTooltipView(toolTip);
        mToolTipViewGroup.startAnimation(toolTip.getEnterAnimation());
        addShadow(toolTip, mToolTipViewGroup);

        /* position and size calculation */
        int[] pos = new int[2];
        mHighlightedView.getLocationOnScreen(pos);
        int targetViewX = pos[0];
        final int targetViewY = pos[1];

        // get measured size of tooltip
        mToolTipViewGroup.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        int toolTipMeasuredWidth = mToolTipViewGroup.getMeasuredWidth();
        int toolTipMeasuredHeight = mToolTipViewGroup.getMeasuredHeight();

        Point resultPoint = new Point(); // this holds the final position of tooltip

        final TooltipCalculator tooltipCalculator = new TooltipCalculator(mHighlightedView);
        // calculate x position, based on gravity, tooltipMeasuredWidth, parent max width, x position of target view, adjustment
        if (toolTipMeasuredWidth > parent.getWidth()) {
            resultPoint.x = tooltipCalculator.getXForTooTip(toolTip.getGravity(), parent.getWidth(), targetViewX);
        } else {
            resultPoint.x = tooltipCalculator.getXForTooTip(toolTip.getGravity(), toolTipMeasuredWidth, targetViewX);
        }

        resultPoint.y = tooltipCalculator.getYForTooTip(toolTip.getGravity(), toolTipMeasuredHeight, targetViewY);

        parent.addView(mToolTipViewGroup, layoutParams);

        // 1. width < screen check
        if (toolTipMeasuredWidth > parent.getWidth()) {
            mToolTipViewGroup.getLayoutParams().width = parent.getWidth();
            toolTipMeasuredWidth = parent.getWidth();
        }
        // 2. x left boundary check
        if (resultPoint.x < 0) {
            mToolTipViewGroup.getLayoutParams().width = toolTipMeasuredWidth + resultPoint.x; //since point.x is negative, use plus
            resultPoint.x = 0;
        }
        // 3. x right boundary check
        int tempRightX = resultPoint.x + toolTipMeasuredWidth;
        if (tempRightX > parent.getWidth()) {
            mToolTipViewGroup.getLayoutParams().width = parent.getWidth() - resultPoint.x; //since point.x is negative, use plus
        }

        final ViewTreeObserver viewTreeObserver = mToolTipViewGroup.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mToolTipViewGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);// make sure this only run once

                int fixedY;
                int toolTipHeightAfterLayouted = mToolTipViewGroup.getHeight();
                fixedY = tooltipCalculator.getYForTooTip(toolTip.getGravity(), toolTipHeightAfterLayouted, targetViewY);
                layoutParams.setMargins((int) mToolTipViewGroup.getX(), fixedY, 0, 0);
            }
        });

        // set the position using setMargins on the left and top
        layoutParams.setMargins(resultPoint.x, resultPoint.y, 0, 0);
    }

    private void addShadow(ToolTip toolTip, View tooltipView) {
        if (toolTip.isShadow()) {
            tooltipView.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.drop_shadow));
        }
    }

    private View initTooltipView(ToolTip toolTip) {
        LayoutInflater layoutInflater = mActivity.getLayoutInflater();
        View root = layoutInflater.inflate(R.layout.tooltip, null);
        View toolTipContainer = root.findViewById(R.id.toolTip_container);
        TextView toolTipTitleTV = (TextView) root.findViewById(R.id.title);
        TextView toolTipDescriptionTV = (TextView) root.findViewById(R.id.description);

            /* set tooltip attributes */
        toolTipContainer.setBackgroundColor(toolTip.getBackgroundColor());
        if (toolTip.getTitle() == null) {
            toolTipTitleTV.setVisibility(View.GONE);
        } else {
            toolTipTitleTV.setText(toolTip.getTitle());
        }
        if (toolTip.getDescription() == null) {
            toolTipDescriptionTV.setVisibility(View.GONE);
        } else {
            toolTipDescriptionTV.setText(toolTip.getDescription());
        }

        if (toolTip.getOnClickListener() != null) {
            root.setOnClickListener(toolTip.getOnClickListener());
        }
        return root;
    }


    private void setupFrameLayout(FrameLayout frameLayout) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        ViewGroup contentArea = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        int[] pos = new int[2];
        contentArea.getLocationOnScreen(pos);
        layoutParams.setMargins(0, -pos[1], 0, 0);
        contentArea.addView(frameLayout, layoutParams);
    }

}
