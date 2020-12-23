package com.example.kotlinobservalo;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class RecyclerTouchEvent implements RecyclerView.OnItemTouchListener {
    ClickListener clickListener;

    public RecyclerTouchEvent(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
        this.clickListener = clickListener;
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
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
        } //clickListener.onOutsideClick(e);


        return false;
    }

    @Override
    public void onTouchEvent(@NotNull RecyclerView rv, @NotNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface ClickListener {
        void onClick(MotionEvent event);
    }
}
