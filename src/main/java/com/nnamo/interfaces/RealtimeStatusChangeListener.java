package com.nnamo.interfaces;

import com.nnamo.enums.RealtimeStatus;

public interface RealtimeStatusChangeListener {
    public void onChange(RealtimeStatus newStatus);
}
