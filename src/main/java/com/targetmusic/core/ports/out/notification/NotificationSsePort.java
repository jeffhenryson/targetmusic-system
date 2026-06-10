package com.targetmusic.core.ports.out.notification;

import com.targetmusic.core.domain.model.notification.Notification;

public interface NotificationSsePort {
    void send(String username, Notification notification);
    int activeConnections(String username);
}
