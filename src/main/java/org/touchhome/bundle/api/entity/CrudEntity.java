package org.touchhome.bundle.api.entity;

import lombok.Getter;
import org.touchhome.bundle.api.model.HasEntityIdentifier;
import org.touchhome.bundle.api.ui.field.UIField;

import javax.persistence.*;
import java.util.Date;

import static org.touchhome.bundle.api.ui.field.UIFieldType.StaticDate;

@Getter
@MappedSuperclass
public class CrudEntity<T> implements HasEntityIdentifier {
    @Id
    @GeneratedValue
    private Integer id;

    @UIField(order = 4, readOnly = true, type = StaticDate)
    @Column(nullable = false)
    private Date creationTime;

    public T setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
        return (T) this;
    }

    @PrePersist
    public final void prePersist() {
        if (this.creationTime == null) {
            this.creationTime = new Date();
        }
        beforePersist();
    }

    private void beforePersist() {

    }

    @Override
    public String getEntityID() {
        return String.valueOf(id);
    }
}
