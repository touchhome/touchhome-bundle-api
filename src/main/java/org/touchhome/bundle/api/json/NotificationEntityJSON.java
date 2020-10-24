package org.touchhome.bundle.api.json;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.util.NotificationType;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.defaultString;

@Getter
@Accessors(chain = true)
public class NotificationEntityJSON implements Comparable<NotificationEntityJSON> {

    private final String entityID;

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private NotificationType notificationType = NotificationType.info;

    private Date creationTime = new Date();

    public NotificationEntityJSON(String entityID) {
        if (entityID == null) {
            throw new IllegalArgumentException("entityId is null");
        }
        this.entityID = entityID;
        this.name = entityID;
    }

    public static NotificationEntityJSON danger(String entityID) {
        return new NotificationEntityJSON(entityID).setNotificationType(NotificationType.error);
    }

    public static NotificationEntityJSON warn(String entityID) {
        return new NotificationEntityJSON(entityID).setNotificationType(NotificationType.warning);
    }

    public static NotificationEntityJSON info(String entityID) {
        return new NotificationEntityJSON(entityID).setNotificationType(NotificationType.info);
    }

    public static NotificationEntityJSON success(String entityID) {
        return new NotificationEntityJSON(entityID).setNotificationType(NotificationType.success);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationEntityJSON that = (NotificationEntityJSON) o;
        return Objects.equals(entityID, that.entityID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityID);
    }

    @Override
    public int compareTo(@NotNull NotificationEntityJSON other) {
        int i = this.notificationType.name().compareTo(other.notificationType.name());
        return i == 0 ? this.name.compareTo(other.name) : i;
    }

    @Override
    public String toString() {
        return defaultString(name, "") + (StringUtils.isNotEmpty(description) ? " | " + description : "");
    }
}
