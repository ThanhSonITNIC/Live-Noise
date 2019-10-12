package com.hackathon.livenoisex.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hackathon.livenoisex.utils.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VisualizerView extends View {
    private static int LINE_WIDTH = Math.round(Utils.shared().convertDpToPx(3f));
    ; // width of visualizer lines
    private static int STEP_BLANK = 3; // width of visualizer lines
    private static float MIN_AMP = 40f;
    private static float MAX_AMP = 110f;
    private List<Float> amplitudes = new CopyOnWriteArrayList<>(); // amplitudes for line lengths
    private int width; // width of this View
    private int height; // height of this View
    private Paint linePaint; // specifies line drawing characteristics
    private RectF rect = new RectF();

    // constructor
    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        this.setWillNotDraw(false);
        this.setWillNotCacheDrawing(false);

//        LINE_WIDTH = Utils.shared().convertDpToPx(2);
//        MIN_AMP = Utils.shared().convertDpToPx(40f);
//        MAX_AMP = Utils.shared().convertDpToPx(110f);

        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(Color.WHITE); // set color to green
        linePaint.setStrokeWidth(LINE_WIDTH); // set stroke width
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        linePaint.setTextSize(14);
    }

    // called when the dimensions of the View change
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w; // new width of this View
        height = h; // new height of this View
//        amplitudes = new ArrayList<Float>(width / ((STEP_BLANK + 1) * LINE_WIDTH));
    }

    // clear all amplitudes to prepare for a new visualization
    public void clear() {
        amplitudes.clear();
    }

    // add the given amplitude to the amplitudes ArrayList
    public void addAmplitude(float amplitude) {
        if (amplitudes == null) {
            return;
        }
        amplitudes.add(amplitude); // add newest to the amplitudes ArrayList

        // if the power lines completely fill the VisualizerView
        if ((amplitudes.size() * (2 * LINE_WIDTH)) >= width) {
            amplitudes.remove(0); // remove oldest power value
        }
        invalidate();
    }

    // draw the visualizer with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        int middle = height / 2; // get the middle of the View
        float curX = 0; // start curX at zero
        // for each item in the amplitudes ArrayList
//        Log.d("TimeDraw-START", new Date().getTime() + "");
        for (float scaledHeight : amplitudes) {
            if (scaledHeight < MIN_AMP) {
                scaledHeight = MIN_AMP;
            } else if (scaledHeight > MAX_AMP) {
                scaledHeight = MAX_AMP;
            }
//            scaledHeight = Utils.shared().convertDpToPx(scaledHeight);

            float heightLevel = (float) (Math.pow(scaledHeight - (MIN_AMP - 1), 1.7f)
                    / Math.pow(MAX_AMP - MIN_AMP, 1.7f));
            float halfHeightLine = (heightLevel * height / 2);
            curX += LINE_WIDTH; // increase X by LINE_WIDTH

            // draw a line representing this item in the amplitudes ArrayList
            Log.d("onDraw", "scaledHeight: " + scaledHeight
                    + "\nheightLevel:" + heightLevel
                    + "\nhalfHeightLine:" + halfHeightLine
                    + "\ncurX:" + curX);
            canvas.drawLine(curX, middle + halfHeightLine, curX, middle - halfHeightLine, linePaint);
            curX += LINE_WIDTH; // increase X by LINE_WIDTH

        }
//        Log.d("TimeDraw-END", new Date().getTime() + "");
    }

}