package com.example.kotlinobservalo;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerTouchEvent implements RecyclerView.OnItemTouchListener {
    private GestureDetector gestureDetector;
    ClickListener clickListener;

    public RecyclerTouchEvent(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

        });

    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent e) {
        final View childView = rv.findChildViewUnder(e.getX(), e.getY());

        //CHECKPOINT
        int action = MotionEventCompat.getActionMasked(e);
        /*
        if (action == MotionEvent.ACTION_DOWN){
            clickListener.onOutsideClick(e);
        }*/

        if (childView != null && clickListener != null) {
            clickListener.onClick(e);
            //if (action == MotionEvent.ACTION_DOWN){
            //    Log.d("quebuenacancioooon","aaaa");
            //}
            //clickListener.onOutsideClick(e);
        }else if (childView==null&&clickListener!=null){
            //clickListener.onOutsideClick(e);
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public static interface ClickListener {
        public void onClick(MotionEvent event);
    }
}
