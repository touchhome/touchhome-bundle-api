package org.touchhome.bundle.api.model;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NaturalId;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.repository.AbstractRepository;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.util.ApplicationContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.function.Supplier;

import static org.touchhome.bundle.api.ui.field.UIFieldType.StaticDate;

@Log4j2
@MappedSuperclass
public abstract class BaseEntity<T extends BaseEntity> implements HasIdIdentifier, HasEntityIdentifier, Serializable {

    @Id
    @GeneratedValue
    @Getter
    private Integer id;

    @NaturalId
    @Column(name = "entityID", unique = true, nullable = false)
    private String entityID;

    @UIField(order = 2, inlineEdit = true)
    @Getter
    private String name;

    @Lob
    @UIField(order = 3, hideOnEmpty = true)
    @Column(length = 1048576)
    @Getter
    private String description;

    @Column(nullable = false)
    @UIField(order = 4, readOnly = true, type = StaticDate)
    @Getter
    private Date creationTime;

    @UIField(order = 5, readOnly = true, type = StaticDate)
    @Getter
    private Date updateTime;

    @Transient
    private String entityIDSupplierStr;

    @Getter
    @Setter
    private Integer xb = 0;

    @Getter
    @Setter
    private Integer yb = 0;

    @Getter
    private Integer bw = 1;

    @Getter
    private Integer bh = 1;

    public static BaseEntity of(String entityID, String name) {
        return new BaseEntity() {
        }.setName(name).setEntityID(entityID);
    }

    public T setBw(Integer bw) {
        this.bw = bw < 1 ? null : bw;
        return (T) this;
    }

    public T setBh(Integer bh) {
        this.bh = bh < 1 ? null : bh;
        return (T) this;
    }

    public T setId(Integer id) {
        this.id = id;
        return (T) this;
    }

    public T setName(@ApiParam("name") String name) {
        this.name = name;
        return (T) this;
    }

    public T setCreationTime(@ApiParam("creationTime") Date creationTime) {
        this.creationTime = creationTime;
        return (T) this;
    }

    public T setDescription(@ApiParam("description") String description) {
        this.description = description;
        return (T) this;
    }

    public String getTitle() {
        String name = getName();
        return name == null || name.trim().length() == 0 ? getEntityID() : name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseEntity that = (BaseEntity) o;
        return entityID != null ? entityID.equals(that.entityID) : that.entityID == null;
    }

    @Override
    public int hashCode() {
        return entityID != null ? entityID.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "{'entityID':'" + getEntityID(false) + "'\'}";
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    @PrePersist
    public final void prePersist() {
        if (this.creationTime == null) {
            this.creationTime = new Date();
        }
        this.updateTime = new Date();
        this.getEntityID(true);
        if (StringUtils.isEmpty(getName())) {
            setName(refreshName());
        }
        this.beforePersist();
        this.validate();
    }

    protected void beforePersist() {

    }

    protected void validate() {

    }

    @PreUpdate
    private void preUpdate() {
        this.updateTime = new Date();
        this.beforeUpdate();
        this.validate();
    }

    protected void beforeUpdate() {

    }

    @PreRemove
    private void preRemove() {
        this.beforeRemove();
    }

    protected void beforeRemove() {

    }

    public String refreshName() {
        return null;
    }

    public T computeEntityID(Supplier<String> entityIDSupplier) {
        entityIDSupplierStr = entityIDSupplier.get();
        if (this.name == null) {
            this.name = entityIDSupplierStr;
        }
        return (T) this;
    }

    public String getEntityID() {
        return getEntityID(false);
    }

    public T setEntityID(@ApiParam("entityID") String entityID) {
        this.entityID = entityID;
        return (T) this;
    }

    public String getEntityID(boolean create) {
        if (create && this.entityID == null) {
            AbstractRepository repo = ApplicationContextHolder.getBean(EntityContext.class).getRepository(getClass());
            String simpleId = entityIDSupplierStr;
            if (simpleId == null) {
                String name = getClass().getSimpleName();
                if (name.endsWith("DeviceEntity")) {
                    name = name.substring(0, name.length() - "DeviceEntity".length());
                }
                simpleId = name + "_" + System.currentTimeMillis();
            }
            this.entityID = simpleId.startsWith(repo.getPrefix()) ? simpleId : repo.getPrefix() + simpleId;
        }
        return this.entityID;
    }

    public void getAllRelatedEntities(Set<BaseEntity> set) {
    }

    @Override
    public String getIdentifier() {
        return getEntityID() == null ? String.valueOf(getId()) : getEntityID();
    }

    public void copy() {
        id = null;
        entityID = null;
        creationTime = null;
        updateTime = null;

        ApplicationContextHolder.getBean(EntityManager.class).detach(this);
    }
}
