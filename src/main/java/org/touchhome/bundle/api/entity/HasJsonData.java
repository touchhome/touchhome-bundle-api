package org.touchhome.bundle.api.entity;

import org.json.JSONObject;

public interface HasJsonData<T> {

    JSONObject getJsonData();

    default <P> T setJsonData(String key, P value) {
        getJsonData().put(key, value);
        return (T) this;
    }

    default Integer getJsonData(String key, int defaultValue) {
        return getJsonData().optInt(key, defaultValue);
    }

    default <E extends Enum> E getJsonDataEnum(String key, E defaultValue) {
        String jsonData = getJsonData(key);

        for (Enum enumValue : defaultValue.getClass().getEnumConstants()) {
            if (enumValue.name().equals(jsonData)) {
                return (E) enumValue;
            }
        }
        return defaultValue;
    }

    default <E extends Enum> T setJsonDataEnum(String key, E value) {
        setJsonData(key, value == null ? "" : value.name());
        return (T) this;
    }

    default Boolean getJsonData(String key, boolean defaultValue) {
        return getJsonData().optBoolean(key, defaultValue);
    }

    default String getJsonData(String key, String defaultValue) {
        return getJsonData().optString(key, defaultValue);
    }

    default Long getJsonData(String key, long defaultValue) {
        return getJsonData().optLong(key, defaultValue);
    }

    default String getJsonData(String key) {
        return getJsonData().optString(key);
    }

    default Double getJsonData(String key, double defaultValue) {
        return getJsonData().optDouble(key, defaultValue);
    }
}
