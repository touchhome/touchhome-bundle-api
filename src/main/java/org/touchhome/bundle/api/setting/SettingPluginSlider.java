package org.touchhome.bundle.api.setting;

import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;

import static org.touchhome.bundle.api.util.TouchHomeUtils.putOpt;

public interface SettingPluginSlider extends SettingPluginInteger {

    @Override
    default SettingType getSettingType() {
        return SettingType.Slider;
    }

    default Integer getStep() {
        return null;
    }

    default String getHeader() {
        return null;
    }

    @Override
    default JSONObject getParameters(EntityContext entityContext, String value) {
        JSONObject parameters = SettingPluginInteger.super.getParameters(entityContext, value);
        putOpt(parameters, "step", getStep());
        putOpt(parameters, "header", getHeader());
        return parameters;
    }
}
