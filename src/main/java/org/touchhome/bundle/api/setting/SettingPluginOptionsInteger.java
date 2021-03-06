package org.touchhome.bundle.api.setting;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface SettingPluginOptionsInteger extends SettingPluginInteger, SettingPluginOptions<Integer> {
    @Override
    default SettingType getSettingType() {
        return SettingType.SelectBox;
    }

    @Override
    default Collection<OptionModel> getOptions(EntityContext entityContext) {
        List<OptionModel> optionModels = new ArrayList<>();
        for (int value : availableValues()) {
            optionModels.add(OptionModel.key(String.valueOf(value)));
        }
        return optionModels;
    }

    int[] availableValues();
}
