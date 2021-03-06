package org.touchhome.bundle.api.entity.widget;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.exception.ServerException;
import org.touchhome.bundle.api.ui.UISidebarMenu;
import org.touchhome.bundle.api.ui.field.UIField;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@UISidebarMenu(icon = "fas fa-tachometer-alt", bg = "#107D6B")
@Accessors(chain = true)
@NoArgsConstructor
public abstract class WidgetBaseEntityAndSeries<T extends WidgetBaseEntityAndSeries, S extends WidgetSeriesEntity<T>>
        extends WidgetBaseEntity<T> {

    @OrderBy("priority asc")
    @UIField(order = 30, onlyEdit = true)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "widgetEntity",
            targetEntity = WidgetSeriesEntity.class)
    private Set<S> series;

    @Override
    public boolean updateRelations(EntityContext entityContext) {
        return validateSeries(series, entityContext);
    }

    protected boolean validateSeries(Set<S> series, EntityContext entityContext) {
        boolean updated = false;
        if (series != null) {
            for (S item : series) {
                String dataSource = item.getDataSource();
                if (dataSource != null) {
                    BaseEntity entity = entityContext.getEntity(dataSource);
                    if (entity == null) {
                        updated = true;
                        item.setDataSource(null);
                    }
                }
            }
        }
        return updated;
    }

    @Override
    protected void validate() {
        if (getWidgetTabEntity() == null) {
            throw new ServerException("Unable to save widget without attach to tab");
        }
    }

    @Override
    public void afterFetch(EntityContext entityContext) {
        if (updateRelations(entityContext)) {
            entityContext.save(this);
        }
    }

    @Override
    public void copy() {
        super.copy();
        series.forEach(BaseEntity::copy);
    }
}
