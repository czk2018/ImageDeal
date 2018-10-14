package com.jr.normal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2018\10\14 0014.
 */

public class MatrixImageView extends android.support.v7.widget.AppCompatImageView {

    private final static String TAG = "MatrixImageView";

    private GestureDetector mGestureDetector;

    private Matrix mMatrix = new Matrix();
    private float mImageWidth, mImageHeight;

    public MatrixImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        MatrixTouchListener mListener = new MatrixTouchListener();
        this.setOnTouchListener(mListener);
        mGestureDetector = new GestureDetector(getContext(), new GestureListener(mListener));

        this.setBackgroundColor(Color.BLACK);
        this.setScaleType(ScaleType.FIT_CENTER);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mMatrix.set(getImageMatrix());
        float[] values = new float[9];
        mMatrix.getValues(values);
        mImageWidth = getWidth() / values[Matrix.MSCALE_X];
        mImageHeight = (getHeight() - values[Matrix.MTRANS_Y] * 2) / values[Matrix.MSCALE_Y];
    }

    class MatrixTouchListener implements View.OnTouchListener {

        private static final int MODE_DRAG = 1;
        private static final int MODE_ZOOM = 2;
        private static final int MODE_UNABLE = 3;
        float mMaxScale = 6;
        float mDoubleClickScale = 2;
        private int mMode = 0;
        private float mStartDis;
        private Matrix mCurrentMatrix = new Matrix();
        private PointF startPoint = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mMode = MODE_DRAG;
                    startPoint.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    reSetMatrix();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mMode == MODE_ZOOM) {
                        setZoomMatrix(event);
                    } else if (mMode == MODE_DRAG) {
                        setDragMatrix(event);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (mMode == MODE_UNABLE) return true;
                    mMode = MODE_ZOOM;
                    mStartDis = distance(event);
                    break;
                default:
                    break;
            }
            return mGestureDetector.onTouchEvent(event);
        }

        public void setDragMatrix(MotionEvent event) {
            if (isZoomChanged()) {
                ;
            }
        }

        private boolean isZoomChanged() {
            float[] values = new float[9];
        }

        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);

            return (float) Math.sqrt(dx* dx + dy * dy);
        }

        private void setZoomMatrix(MotionEvent event) {
            if (event.getPointerCount() < 2) return;
            float endDis = distance(event);
            if (endDis > 10f) {
                float scale = endDis / mStartDis;
                mStartDis = endDis;

                mCurrentMatrix.set(getImageMatrix());
                float[] values = new float[9];
                mCurrentMatrix.getValues(values);

                scale = checkMaxScale(scale, values);
                setImageMatrix(mCurrentMatrix);
            }
        }

        private float checkMaxScale(float scale, float[] values) {
            if (scale * values[Matrix.MSCALE_X] > mMaxScale) {
                scale = mMaxScale / values[Matrix.MSCALE_X];
            }

            mCurrentMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);

            return scale;
        }

        private void reSetMatrix() {
            if (checkRest()) {
                mCurrentMatrix.set(mMatrix);
                setImageMatrix(mCurrentMatrix);
            }
        }

        private boolean checkRest() {
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            float scale = values[Matrix.MSCALE_X];
            mMatrix.getValues(values);
            return scale < values[Matrix.MSCALE_X];
        }
    }
}
