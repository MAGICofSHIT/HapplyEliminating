package com.xample.happlyeliminating;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeListener implements View.OnTouchListener {
    public GestureDetector gestureDetector; // 创建手势检测器变量
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);     // 当视图被触摸时，将触摸事件传递给gestureDetector来处理
    }
    public OnSwipeListener(Context context) {
        // 初始化手势检测器
        gestureDetector = new GestureDetector(context, new GestureListener());
    }
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public static final int SWIPE_THRESOLD = 100;    // 手势滑动的最小阈值距离
        public static final int SWIPE_VELOCITY_THRESOLD = 100;   // 手势滑动的最小阈值速度

        @Override
        // 处理手势的快速滑动事件
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;     // true表示手势事件已被处理
            float yDiff = e2.getY() - e1.getY();
            float xDiff = e2.getX() - e1.getX();
            if (Math.abs(xDiff) > Math.abs(yDiff)) {
                // 我们解除在水平方向上或者垂直方向上的权利
                if (Math.abs(xDiff) > SWIPE_THRESOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESOLD) {
                    if (xDiff > 0) {    // 右滑
                        onSwipeRight();
                    } else {    // 左滑
                        onSwipeLeft();
                    }
                    result = true;
                }
            } else if (Math.abs(yDiff) > SWIPE_THRESOLD &&
                    Math.abs(velocityY) > SWIPE_VELOCITY_THRESOLD) {
                if (yDiff > 0) {    // 下滑
                    onSwipeBottom();
                } else {    // 上滑
                    onSwipeTop();
                }
                result = true;
            }
            return result;
        }

        @Override
        public boolean onDown(MotionEvent e) {return true;}
    }

    public void onSwipeLeft(){}
    public void onSwipeRight(){}
    public void onSwipeTop(){}
    public void onSwipeBottom(){}

}
