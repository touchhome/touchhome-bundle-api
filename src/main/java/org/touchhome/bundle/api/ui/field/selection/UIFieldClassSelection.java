package org.touchhome.bundle.api.ui.field.selection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UIFieldClassSelection {
    Class<?> value();

    String[] basePackages();

    Class<? extends Predicate<Class<?>>> filter() default Identity.class;

    class Identity implements Predicate<Class<?>> {

        @Override
        public boolean test(Class<?> aClass) {
            return true;
        }
    }
}
