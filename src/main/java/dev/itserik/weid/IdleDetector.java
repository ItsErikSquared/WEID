package dev.itserik.weid;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class IdleDetector implements NativeKeyListener, NativeMouseInputListener, NativeMouseWheelListener, NativeMouseMotionListener {

    /**
     * IDLE & AWAY
     * If the returned Idle time is between 0 and idle, the state will return ONLINE
     * If the returned Idle time is between idle and away, the state will return IDLE
     * If the returned Idle time is between away and Infinity, the state will return AWAY
     * <p>
     * If Online - Do nothing
     * If Idle   - Minimize & Hide taskbar
     * If Away   - On next movement, lock the computer
     */
    public long lastInput = System.currentTimeMillis();
    public long idle;
    public long away;
    public boolean hideTaskbar;

    public void start() {
        try {
            Logger jnhlog = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            jnhlog.setLevel(Level.OFF);
            jnhlog.setUseParentHandlers(false);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);
            GlobalScreen.addNativeMouseMotionListener(this);
            GlobalScreen.addNativeMouseWheelListener(this);
        } catch (NativeHookException e) {
            WEID.showAlert("Error while registering NativeHook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public State getCurrentState() {
        long current = System.currentTimeMillis();
        if (current - lastInput < idle) {
            return State.ONLINE;
        }
        if (away == -1) {
            return State.IDLE;
        }
        if (current - lastInput < away) {
            return State.IDLE;
        }
        return State.AWAY;
    }

    public void now() {
        lastInput = System.currentTimeMillis();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        now();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        now();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        now();
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
        now();
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
        now();
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        now();
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        now();
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
        now();
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent) {
        now();
    }

    enum State {
        ONLINE, IDLE, AWAY
    }

}