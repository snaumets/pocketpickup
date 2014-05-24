package com.uwcse403.pocketpickup.mapwrapper;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * This class will make a wrapper around an activity that will allow touch and release
 * events/gestures to be intercepted, and a listener can be set to do specific tasks
 * on those events.
 */
public class TouchableWrapper extends FrameLayout {

    public TouchableWrapper(Context context) {
        super(context);
    }

    public void setTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    private OnTouchListener onTouchListener;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchListener.onTouch();
                break;
            case MotionEvent.ACTION_UP:
                onTouchListener.onRelease();
                break;
            default:
            	break;
        }

        return super.dispatchTouchEvent(event);
    }

    public interface OnTouchListener {
        void onTouch();
        void onRelease();
    }
}
