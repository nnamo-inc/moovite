package com.nnamo.interfaces;

import com.nnamo.enums.RealtimeStatus;

public interface RealtimeStatusChangeListener {
    void onChange(RealtimeStatus newStatus);
}
