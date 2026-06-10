package com.targetmusic.core.ports.out.notification;

import com.targetmusic.core.domain.model.notification.NotificationPreference;
import com.targetmusic.core.domain.model.notification.NotificationType;

import java.util.Map;

public interface NotificationPreferenceRepository {

    Map<NotificationType, NotificationPreference> findByUsername(String username);

    void upsert(NotificationPreference preference);
}
