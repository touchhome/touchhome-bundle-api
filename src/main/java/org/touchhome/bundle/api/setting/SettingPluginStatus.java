package org.touchhome.bundle.api.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.util.NotificationLevel;
import org.touchhome.bundle.api.util.TouchHomeUtils;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public interface SettingPluginStatus extends SettingPlugin<SettingPluginStatus.BundleStatusInfo> {

    BundleStatusInfo ONLINE = new BundleStatusInfo(Status.ONLINE, null);
    BundleStatusInfo UNKNOWN = new BundleStatusInfo(Status.UNKNOWN, null);

    static BundleStatusInfo of(Status status, String message) {
        return new BundleStatusInfo(status, message);
    }

    static BundleStatusInfo error(String message) {
        return new BundleStatusInfo(Status.ERROR, message);
    }

    static BundleStatusInfo error(Throwable th) {
        return new BundleStatusInfo(Status.ERROR, TouchHomeUtils.getErrorMessage(th));
    }

    @Override
    default SettingType getSettingType() {
        return SettingType.Info;
    }

    @Override
    default Class<SettingPluginStatus.BundleStatusInfo> getType() {
        return SettingPluginStatus.BundleStatusInfo.class;
    }

    @Override
    default String getDefaultValue() {
        return Status.UNKNOWN.name();
    }

    @Override
    default BundleStatusInfo parseValue(EntityContext entityContext, String value) {
        String[] split = value.split("#~#", -1);
        return split.length == 0 ? UNKNOWN : new BundleStatusInfo(Status.valueOf(split[0]), split.length > 1 ? split[1] : null);
    }

    @Override
    default String writeValue(BundleStatusInfo value) {
        return value == null ? "" : value.status.name() + "#~#" + defaultIfEmpty(value.message, "");
    }

    @AllArgsConstructor
    class BundleStatusInfo {
        private final Status status;

        @Getter
        private final String message;

        public boolean isOnline() {
            return status == Status.ONLINE;
        }

        public Status getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return status.name() + (isEmpty(message) ? "" : " - " + message);
        }

        public NotificationLevel getLevel() {
            switch (status) {
                case OFFLINE:
                case UNKNOWN:
                    return NotificationLevel.warning;
                case ONLINE:
                    return NotificationLevel.success;
                default:
                    return NotificationLevel.error;
            }
        }
    }
}
