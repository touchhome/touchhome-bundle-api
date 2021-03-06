package org.touchhome.bundle.api.entity.workspace.backup;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.entity.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Accessors(chain = true)
public final class WorkspaceBackupGroupEntity extends BaseEntity<WorkspaceBackupGroupEntity> {

    public static final String PREFIX = "wsbpg_";

    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "workspaceBackupGroupEntity", cascade = CascadeType.ALL)
    private Set<WorkspaceBackupEntity> workspaceBackupEntities;

    @Override
    public String getEntityPrefix() {
        return PREFIX;
    }
}
