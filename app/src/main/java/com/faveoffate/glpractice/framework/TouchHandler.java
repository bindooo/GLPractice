package com.faveoffate.glpractice.framework;

import android.view.View.OnTouchListener;

import com.faveoffate.glpractice.framework.Input.TouchEvent;

import java.util.List;

public interface TouchHandler extends OnTouchListener {
    public boolean isTouchDown(int pointer);
    public int getTouchX(int pointer);
    public int getTouchY(int pointer);
    public List<TouchEvent> getTouchEvents();
}
