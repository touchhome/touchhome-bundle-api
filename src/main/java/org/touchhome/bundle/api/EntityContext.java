package org.touchhome.bundle.api;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.DeviceBaseEntity;
import org.touchhome.bundle.api.entity.UserEntity;
import org.touchhome.bundle.api.model.HasEntityIdentifier;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.repository.AbstractRepository;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;

public interface EntityContext {

    String APP_ID = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());

    static boolean isDevEnvironment() {
        return "true".equals(System.getProperty("development"));
    }

    static boolean isDockerEnvironment() {
        return "true".equals(System.getProperty("docker"));
    }

    static boolean isLinuxEnvironment() {
        return SystemUtils.IS_OS_LINUX && !isDockerEnvironment() && !isDevEnvironment();
    }

    static boolean isLinuxOrDockerEnvironment() {
        return SystemUtils.IS_OS_LINUX && !isDevEnvironment();
    }

    EntityContextUI ui();

    EntityContextEvent event();

    EntityContextUDP udp();

    EntityContextBGP bgp();

    EntityContextSetting setting();

    default <T extends BaseEntity> T getEntity(String entityID) {
        return getEntity(entityID, true);
    }

    default <T extends BaseEntity> T getEntityOrDefault(String entityID, T defEntity) {
        T entity = getEntity(entityID, true);
        return entity == null ? defEntity : entity;
    }

    <T extends BaseEntity> T getEntity(String entityID, boolean useCache);

    default Optional<AbstractRepository> getRepository(BaseEntity baseEntity) {
        return getRepository(baseEntity.getEntityID());
    }

    Optional<AbstractRepository> getRepository(String entityID);

    AbstractRepository getRepository(Class<? extends BaseEntity> entityClass);

    default <T extends BaseEntity> T getEntity(T entity) {
        return getEntity(entity.getEntityID());
    }

    <T extends HasEntityIdentifier> void createDelayed(T entity);

    <T extends HasEntityIdentifier> void updateDelayed(T entity, Consumer<T> fieldUpdateConsumer);

    default <T extends DeviceBaseEntity> void updateDeviceStatus(T entity, Status status, String message) {
        updateDelayed(entity, t -> t.setStatus(status).setStatusMessage(message));
    }

    <T extends HasEntityIdentifier> void save(T entity);

    <T extends BaseEntity> T save(T entity);

    default <T extends BaseEntity> T delete(T entity) {
        return (T) delete(entity.getEntityID());
    }

    <T extends BaseEntity> List<T> findAll(Class<T> clazz);

    <T extends BaseEntity> List<T> findAllByPrefix(String prefix);

    default <T extends BaseEntity> List<T> findAll(T entity) {
        return (List<T>) findAll(entity.getClass());
    }

    BaseEntity<? extends BaseEntity> delete(String entityId);

    AbstractRepository<? extends BaseEntity> getRepositoryByPrefix(String repositoryPrefix);

    <T extends BaseEntity> T getEntityByName(String name, Class<T> entityClass);

    void setFeatureState(String feature, boolean state);

    boolean isFeatureEnabled(String deviceFeature);

    Map<String, Boolean> getDeviceFeatures();

    <T> T getBean(String beanName, Class<T> clazz);

    <T> T getBean(Class<T> clazz);

    <T> Collection<T> getBeansOfType(Class<T> clazz);

    <T> Map<String, T> getBeansOfTypeWithBeanName(Class<T> clazz);

    <T> Map<String, Collection<T>> getBeansOfTypeByBundles(Class<T> clazz);

    default boolean isAdminUserOrNone() {
        UserEntity user = getUser(false);
        return user == null || user.isAdmin();
    }

    default UserEntity getUser(boolean anonymousIfNotFound) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserEntity entity = getEntity((String) authentication.getCredentials());
            if (entity == null && anonymousIfNotFound) {
                entity = UserEntity.ANONYMOUS_USER;
            }
            return entity;
        }
        return null;
    }

    Collection<AbstractRepository> getRepositories();

    <T> List<Class<? extends T>> getClassesWithAnnotation(Class<? extends Annotation> annotation);

    <T> List<Class<? extends T>> getClassesWithParent(Class<T> baseClass, String... packages);

    interface EntityUpdateListener<T> {
        void entityUpdated(T newValue, T oldValue);
    }
}
