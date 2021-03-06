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

import java.util.HashMap;
import java.util.Map;

public class TourGuide {

    private Map<View, ViewHighlight> viewMap = new HashMap<>();
    private Activity activity;
    private FrameLayoutWithHighlights frameLayout;
    private View toolTipViewGroup;
    private ToolTip toolTip;
    private View toolTipAnchor;
    private Overlay overlay;

    public static TourGuide init(Activity activity) {
        return new TourGuide(activity);
    }

    public TourGuide(Activity activity) {
        this.activity = activity;
    }

    public TourGuide addTarget(View view, ViewHighlight.Style style) {
        viewMap.put(view, ViewHighlight.from(view, style));
        return this;
    }

    public TourGuide play() {
        setupView();
        return this;
    }

    public TourGuide setOverlay(Overlay overlay) {
        this.overlay = overlay;
        return this;
    }

    public TourGuide setToolTip(ToolTip toolTip, View anchor) {
        toolTipAnchor = anchor;
        this.toolTip = toolTip;
        return this;
    }

    public void cleanUp() {
        frameLayout.cleanUp();
        if (toolTipViewGroup != null) {
            ((ViewGroup) activity.getWindow().getDecorView()).removeView(toolTipViewGroup);
        }
    }

    public FrameLayoutWithHighlights getOverlay() {
        return frameLayout;
    }

    private void setupView() {
        frameLayout = new FrameLayoutWithHighlights(activity, viewMap, overlay);
        handleDisableClicking(frameLayout);
        setupFrameLayout(frameLayout);
        setupToolTip(toolTip, toolTipAnchor);
    }

    private void handleDisableClicking(FrameLayoutWithHighlights frameLayoutWithHighlights) {
        // 1. if user provides an overlay listener, use that as 1st priority
        if (overlay != null && overlay.getOnClickListener() != null) {
            frameLayoutWithHighlights.setClickable(true);
            frameLayoutWithHighlights.setOnClickListener(overlay.getOnClickListener());
        }
        // 2. if overlay listener is not provided, check if it's disabled
        else if (overlay != null && overlay.isDisableClick()) {
            Log.w("tourguide", "Overlay's default OnClickListener is null, it will proceed to next tourguide when it is clicked");
            //frameLayoutWithHighlights.setViewHole(highlightedView);
            frameLayoutWithHighlights.setSoundEffectsEnabled(false);
            frameLayoutWithHighlights.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                } // do nothing, disabled.
            });
        }
    }

    private void setupToolTip(final ToolTip toolTip, View anchorView) {
        if (toolTip == null) {
            return;
        }

        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView();

        toolTipViewGroup = initTooltipView(toolTip);
        toolTipViewGroup.startAnimation(toolTip.getEnterAnimation());
        addShadow(toolTip, toolTipViewGroup);

        /* position and size calculation */
        int[] pos = new int[2];
        anchorView.getLocationOnScreen(pos);
        int targetViewX = pos[0];
        final int targetViewY = pos[1];

        // get measured size of tooltip
        toolTipViewGroup.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        int toolTipMeasuredWidth = toolTipViewGroup.getMeasuredWidth();
        int toolTipMeasuredHeight = toolTipViewGroup.getMeasuredHeight();

        Point resultPoint = new Point(); // this holds the final position of tooltip

        final TooltipCalculator tooltipCalculator = new TooltipCalculator(anchorView);
        // calculate x position, based on gravity, tooltipMeasuredWidth, parent max width, x position of target view, adjustment
        if (toolTipMeasuredWidth > parent.getWidth()) {
            resultPoint.x = tooltipCalculator.getXForTooTip(toolTip.getGravity(), parent.getWidth(), targetViewX);
        } else {
            resultPoint.x = tooltipCalculator.getXForTooTip(toolTip.getGravity(), toolTipMeasuredWidth, targetViewX);
        }

        resultPoint.y = tooltipCalculator.getYForTooTip(toolTip.getGravity(), toolTipMeasuredHeight, targetViewY);

        parent.addView(toolTipViewGroup, layoutParams);

        // 1. width < screen check
        if (toolTipMeasuredWidth > parent.getWidth()) {
            toolTipViewGroup.getLayoutParams().width = parent.getWidth();
            toolTipMeasuredWidth = parent.getWidth();
        }
        // 2. x left boundary check
        if (resultPoint.x < 0) {
            toolTipViewGroup.getLayoutParams().width = toolTipMeasuredWidth + resultPoint.x; //since point.x is negative, use plus
            resultPoint.x = 0;
        }
        // 3. x right boundary check
        int tempRightX = resultPoint.x + toolTipMeasuredWidth;
        if (tempRightX > parent.getWidth()) {
            toolTipViewGroup.getLayoutParams().width = parent.getWidth() - resultPoint.x; //since point.x is negative, use plus
        }

        final ViewTreeObserver viewTreeObserver = toolTipViewGroup.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                toolTipViewGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);// make sure this only run once

                int fixedY;
                int toolTipHeightAfterLayouted = toolTipViewGroup.getHeight();
                fixedY = tooltipCalculator.getYForTooTip(toolTip.getGravity(), toolTipHeightAfterLayouted, targetViewY);
                layoutParams.setMargins((int) toolTipViewGroup.getX(), fixedY, 0, 0);
            }
        });

        // set the position using setMargins on the left and top
        layoutParams.setMargins(resultPoint.x, resultPoint.y, 0, 0);
    }

    private void addShadow(ToolTip toolTip, View tooltipView) {
        if (toolTip.isShadow()) {
            tooltipView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.drop_shadow));
        }
    }

    private View initTooltipView(ToolTip toolTip) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
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
        ViewGroup contentArea = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        int[] pos = new int[2];
        contentArea.getLocationOnScreen(pos);
        layoutParams.setMargins(0, -pos[1], 0, 0);
        contentArea.addView(frameLayout, layoutParams);
    }

}
