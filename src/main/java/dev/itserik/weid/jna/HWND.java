package dev.itserik.weid.jna;

import com.sun.jna.Pointer;

public class HWND extends HANDLE {

    /**
     * Instantiates a new hwnd.
     */
    public HWND() {

    }

    /**
     * Instantiates a new hwnd.
     *
     * @param p
     *            the p
     */
    public HWND(Pointer p) {
        super(p);
    }
}
