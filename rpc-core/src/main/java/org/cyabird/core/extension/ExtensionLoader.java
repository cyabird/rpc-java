package org.cyabird.core.extension;

import org.cyabird.util.Holder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExtensionLoader<T> {

    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    private final ExtensionFactory objectFactory;

    private final Holder<Object> cachedAdaptiveInstance = new Holder<>();

    private final Class<?> type;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
        objectFactory = (type == ExtensionFactory.class ? null : ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension());
    }

    /**
     * 尝试从 {@link #EXTENSION_LOADERS} 获取 type 的 {@code ExtensionLoader}，否则
     * 将 {@code new ExtensionLoader<T>(type)} 放入 {@link #EXTENSION_LOADERS}
     *
     * @param type 类类型对象
     * @param <T>
     * @return
     * @see #withExtensionAnnotation(Class) 判断该方法是否包含 SPI 注解
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type(" + type + ") is not interface!");
        }
        if (!withExtensionAnnotation(type)) {
            throw new IllegalArgumentException("Extension type(" + type +
                    ") is not extension, because WITHOUT @" + SPI.class.getSimpleName() + " Annotation!");
        }
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            // 如果 key 存在则不做修改
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    public T getAdaptiveExtension() {
        Object instance = cachedAdaptiveInstance.get();
    }

    /**
     * 判断该方法是否包含 SPI 注解
     *
     * @param type 类类型对象
     * @param <T>
     * @return 如果包含 SPI 注解则返回 {@code true}
     */
    private static <T> boolean withExtensionAnnotation(Class<T> type) {
        return type.isAnnotationPresent(SPI.class);
    }
}
