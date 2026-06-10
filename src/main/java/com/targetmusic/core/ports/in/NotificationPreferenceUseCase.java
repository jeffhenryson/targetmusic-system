package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.notification.NotificationPreference;
import com.targetmusic.core.domain.model.notification.NotificationType;

import java.util.List;

public interface NotificationPreferenceUseCase {

    List<NotificationPreference> getPreferences(String username);

    void updatePreference(String username, NotificationType type, boolean inAppEnabled, boolean emailEnabled);
}
