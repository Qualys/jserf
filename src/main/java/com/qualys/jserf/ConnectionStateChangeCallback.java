package com.qualys.jserf;

public interface ConnectionStateChangeCallback {
    void handleConnectionStateChange(boolean connected);
}
