package top.histevehu.srpc.core.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import top.histevehu.srpc.common.entity.RpcServiceProperties;
import top.histevehu.srpc.common.extension.ExtensionLoader;
import top.histevehu.srpc.core.annotation.SrpcReference;
import top.histevehu.srpc.core.transport.RpcClient;
import top.histevehu.srpc.core.transport.RpcClientProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ReferenceAnnotationBeanPostProcessor implements
        MergedBeanDefinitionPostProcessor, ApplicationContextAware, InstantiationAwareBeanPostProcessor {

    public static final String BEAN_NAME = "referenceAnnotationBeanPostProcessor";

    private final String ATTRIBUTE_GROUP = "group";
    private final String ATTRIBUTE_NAME = "name";
    private final String ATTRIBUTE_VERSION = "version";

    private ApplicationContext applicationContext;

    private final RpcClient rpcClient;

    private final Set<Class<? extends Annotation>> annotationTypes = new LinkedHashSet<>(1);

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    public ReferenceAnnotationBeanPostProcessor() {
        this.annotationTypes.add(SrpcReference.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcClient.class).getExtension("NettyClient");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }

    private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = buildAutowiringMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {

        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;

        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                MergedAnnotation<?> ann = findReferenceAnnotation(field);
                if (ann != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (log.isInfoEnabled()) {
                            log.info("静态字段不支持@AutoWired注释： " + field);
                        }
                        return;
                    }
                    AnnotationAttributes annotationAttributes = ann.asMap(mergedAnnotation -> new AnnotationAttributes(mergedAnnotation.getType()));
                    String version = annotationAttributes.getString(this.ATTRIBUTE_VERSION);
                    String group = annotationAttributes.getString(this.ATTRIBUTE_GROUP);
                    String name = annotationAttributes.getString(this.ATTRIBUTE_NAME);
                    currElements.add(new ReferenceFieldElement(field, group, name, version));
                }
            });

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return InjectionMetadata.forElements(elements, clazz);
    }

    @Nullable
    private MergedAnnotation<?> findReferenceAnnotation(AccessibleObject ao) {
        MergedAnnotations annotations = MergedAnnotations.from(ao);
        for (Class<? extends Annotation> type : this.annotationTypes) {
            MergedAnnotation<?> annotation = annotations.get(type);
            if (annotation.isPresent()) {
                return annotation;
            }
        }
        return null;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "引用依赖项注入失败", ex);
        }
        return pvs;
    }

    private class ReferenceFieldElement extends InjectionMetadata.InjectedElement {

        private String group;
        private String name;
        private String version;

        protected ReferenceFieldElement(Member member, String group, String name, String version) {
            super(member, null);
            this.name = name;
            this.version = version;
            this.group = group;
        }

        @Override
        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;
            RpcServiceProperties rpcServiceProperties = new RpcServiceProperties();
            if (group != null && !group.isEmpty()) rpcServiceProperties.setGroup(group);
            if (version != null && !version.isEmpty()) rpcServiceProperties.setVersion(version);
            if (name != null && !name.isEmpty()) {
                rpcServiceProperties.setServiceName(name);
            } else {
                rpcServiceProperties.setServiceName(field.getType().getCanonicalName());
            }
            RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceProperties);
            Object value = rpcClientProxy.getProxy(field.getType());
            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(target, value);
            }
        }
    }
}
