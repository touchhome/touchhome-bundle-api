package org.touchhome.bundle.api.setting;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.util.TouchHomeUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public interface SettingPluginJarInstallButton extends SettingPluginButton {

    @Override
    default boolean isDisabled(EntityContext entityContext) {
        return Files.exists(getLocalPath());
    }

    String getJarFileName();

    String getFolder();

    String getServerJarPath();

    default Path getLocalPath() {
        return TouchHomeUtils.getExternalJarClassPath().resolve(getFolder()).resolve(getJarFileName());
    }
}
