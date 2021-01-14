package dev.itserik.weid.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class HANDLE extends PointerType {
    private boolean immutable;

    public HANDLE() {
    }

    public HANDLE(Pointer p) {
        setPointer(p);
        immutable = true;
    }

    @Override
    public void setPointer(Pointer p) {
        if (immutable) {
            throw new UnsupportedOperationException("immutable reference");
        }

        super.setPointer(p);
    }

    @Override
    public String toString() {
        return String.valueOf(getPointer());
    }
}
