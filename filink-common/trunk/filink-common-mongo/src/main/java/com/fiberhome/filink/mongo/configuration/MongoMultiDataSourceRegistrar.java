package com.fiberhome.filink.mongo.configuration;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Mongo多数据源注册器
 *
 * @author yuanyao@wistronits.com
 * create on 2019-05-29 21:55
 */
@SuppressWarnings("all")
@Slf4j
public class MongoMultiDataSourceRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {


    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String DATABASE = "database";
    private Map<String, SimpleMongoDbFactory> mongoDbFactoryMap = new HashMap<>();

    /**
     * 注册MongoTemplate
     *
     * 目前实现注册出现问题，先使用静态map实现，后续试着改为注册Spring容器
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        int count = 0;
        for (Map.Entry<String, SimpleMongoDbFactory> entry : mongoDbFactoryMap.entrySet()) {
            SimpleMongoDbFactory value = entry.getValue();

            GenericBeanDefinition definition = new GenericBeanDefinition();
            // 设置bean类型
            definition.setBeanClass(MongoTemplate.class);
            // 设置为自定义bean
            definition.setSynthetic(true);
            // 设置不初始化
            definition.setEnforceInitMethod(false);
            if (count == 0) {
                // 第一个设置为primary
                definition.setPrimary(true);
            }
            // 构造函数参数
            ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
            argumentValues.addIndexedArgumentValue(0, value);
            // 设置构造函数参数
            definition.setConstructorArgumentValues(argumentValues);
            // 注册
            registry.registerBeanDefinition(entry.getKey(),definition);
            count++;
        }
        log.info("MongoDB多数据源装载完毕，共:{} 个", count);
    }

    /**
     * 根据配置属性获取mongoTemplate
     *
     * 根据配置文件获取属性还需完善
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {

        // 重构获取mongo属性的方法 使得获取方式兼容性更好
        Map<String, MongoConfig> configMap = getAppMap(environment);
        Assert.notEmpty(configMap,"mongo 配置信息异常");

        log.info("开始装载MongoDB多数据源");
        for (Map.Entry<String, MongoConfig> entry : configMap.entrySet()) {
            MongoConfig value = entry.getValue();
            ServerAddress host = new ServerAddress(value.getHost(), value.getPort());
            MongoClient mongoClient = new MongoClient(host);
            SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient,value.getDatabase());
            mongoDbFactoryMap.put(entry.getKey(), simpleMongoDbFactory);
            log.info("数据源名称:{}", entry.getKey());
        }

    }


    /**
     * 获取配置文件中的属性 以map返回
     *
     * @param environment
     * @return
     */
    @NonNull
    private Map<String, MongoConfig> getAppMap(Environment environment) {

        String[] activeProfiles = environment.getActiveProfiles();
        MutablePropertySources propertySources = ((StandardServletEnvironment) environment).getPropertySources();
        if (activeProfiles.length > 0) {
            // 使用的本地配置文件
            return localPropertiesProcessor(propertySources);

        }else {
            // 通过指定配置文件的方式获取执行配置文件中的mongo属性
            Iterator<PropertySource<?>> iterator = propertySources.iterator();
            // 第一次遍历寻找CompositePropertySource的属性
            // 使用配置中心的配置文件是 CompositePropertySource 其他都不用关注
            while (iterator.hasNext()) {
                return propertySourceProcessor(iterator.next());
//            PropertySource<?> next = iterator.next();
//            if (next instanceof CompositePropertySource) {
//                CompositePropertySource propertySource = (CompositePropertySource) next;
//                Collection<PropertySource<?>> sources = propertySource.getPropertySources();
//                // 找到的source 是一个集和
//                for (PropertySource<?> source : sources) {
//                    // 如果里面的source是CompositePropertySource
//                    if (source instanceof CompositePropertySource) {
//                        CompositePropertySource compositePropertySource = (CompositePropertySource) source;
//                        Collection<PropertySource<?>> sourceCollection = compositePropertySource.getPropertySources();
//                        // 继续遍历
//                        for (PropertySource<?> propertySource1 : sourceCollection) {
//                            Object source1 = propertySource1.getSource();
//                            if (source1 instanceof LinkedHashMap) {
//
//                                // key:数据源属性 vaue：对象
//                                Map<String, MongoConfig> configMap = new HashMap<>();
//                                LinkedHashMap<String,Object> hashMap = (LinkedHashMap) source1;
//                                for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
//                                    if (entry.getKey().contains("spring.data.mongodb")) {
//                                        // 分离每一个属性
//                                        String beanName = getValue(entry.getKey(),3);
//                                        String mongoProperty = getValue(entry.getKey(), 4);
//                                        if (!configMap.containsKey(beanName)) {
//                                            configMap.put(beanName, new MongoConfig());
//                                        }
//                                        setValue(configMap.get(beanName),mongoProperty,entry.getValue());
//                                    }
//                                }
//                                return configMap;
//                            }
//                        }
//
//                    }
//                }
//            }
            }
        }
        return null;
    }

    /**
     * 本地配置文件处理
     *
     * @param propertySources
     * @return
     */
    private Map<String, MongoConfig> localPropertiesProcessor(MutablePropertySources propertySources) {
        Map<String, MongoConfig> configMap = new HashMap<>();
        for (PropertySource<?> propertySource : propertySources) {
            // 如果是defaultProperties 跳过
            if (propertySource.getName().equalsIgnoreCase("defaultProperties")) {
                continue;
            }
            // 类型是MapPropertySource却不能是SystemEnvironmentPropertySource SystemEnvironmentPropertySource继承MapPropertySource
            if (propertySource instanceof MapPropertySource && !(propertySource instanceof SystemEnvironmentPropertySource)) {

                MapPropertySource mapPropertySource = (MapPropertySource) propertySource;
                System.out.println(mapPropertySource);
                Map<String, Object> source = mapPropertySource.getSource();
                // 添加进config
                for (Map.Entry<String, Object> entry : source.entrySet()) {
                    if (entry.getKey().contains("spring.data.mongodb")) {
                        // 分离每一个属性
                        String beanName = getValue(entry.getKey(),3);
                        String mongoProperty = getValue(entry.getKey(), 4);
                        if (!configMap.containsKey(beanName)) {
                            configMap.put(beanName, new MongoConfig());
                        }
                        setValue(configMap.get(beanName),mongoProperty,entry.getValue());
                    }
                }
            }

        }
        return configMap;
    }

    /**
     * 处理PropertySource<?> next
     * @param next
     * @return
     */
    private Map<String, MongoConfig> propertySourceProcessor(PropertySource<?> next) {
        
        // 只处理 CompositePropertySource ，其他返回null
        if (next instanceof CompositePropertySource) {
            CompositePropertySource propertySource = (CompositePropertySource) next;
            Collection<PropertySource<?>> sources = propertySource.getPropertySources();
            // 找到的source 是一个集和
            return sourceProcessor(sources);
            // 找到的source 是一个集和
//            for (PropertySource<?> source : sources) {
//                // 如果里面的source是CompositePropertySource
//                if (source instanceof CompositePropertySource) {
//                    CompositePropertySource compositePropertySource = (CompositePropertySource) source;
//                    Collection<PropertySource<?>> sourceCollection = compositePropertySource.getPropertySources();
//                    // 继续遍历
//                    for (PropertySource<?> propertySource1 : sourceCollection) {
//                        Object source1 = propertySource1.getSource();
//                        if (source1 instanceof LinkedHashMap) {
//
//                            // key:数据源属性 vaue：对象
//                            Map<String, MongoConfig> configMap = new HashMap<>();
//                            LinkedHashMap<String,Object> hashMap = (LinkedHashMap) source1;
//                            for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
//                                if (entry.getKey().contains("spring.data.mongodb")) {
//                                    // 分离每一个属性
//                                    String beanName = getValue(entry.getKey(),3);
//                                    String mongoProperty = getValue(entry.getKey(), 4);
//                                    if (!configMap.containsKey(beanName)) {
//                                        configMap.put(beanName, new MongoConfig());
//                                    }
//                                    setValue(configMap.get(beanName),mongoProperty,entry.getValue());
//                                }
//                            }
//                            return configMap;
//                        }
//                    }
//
//                }
//            }
        }
        return null;
    }

    /**
     * 处理具体 Collection<PropertySource<?>> sources
     * @param sources
     * @return
     */
    private Map<String, MongoConfig> sourceProcessor(Collection<PropertySource<?>> sources) {
        for (PropertySource<?> source : sources) {
            // 如果里面的source是CompositePropertySource
            if (source instanceof CompositePropertySource) {
                CompositePropertySource compositePropertySource = (CompositePropertySource) source;
                Collection<PropertySource<?>> sourceCollection = compositePropertySource.getPropertySources();
                // 继续遍历
                for (PropertySource<?> propertySource1 : sourceCollection) {
                    Object source1 = propertySource1.getSource();
                    if (source1 instanceof LinkedHashMap) {
                        // 找到具体属性 返回map对象
                        return mongoPropertyProcessor(source1);
                    }
                }

            }
            break;
        }
        return null;
    }

    /**
     * 真正处理mongo配置属性方法
     * @param source1
     * @return
     */
    @SuppressWarnings("all")
    private Map<String, MongoConfig> mongoPropertyProcessor(Object source1) {
        // key:数据源属性 vaue：对象
        Map<String, MongoConfig> configMap = new HashMap<>();
        LinkedHashMap<String,Object> hashMap = (LinkedHashMap) source1;
        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            if (entry.getKey().contains("spring.data.mongodb")) {
                // 分离每一个属性
                String beanName = getValue(entry.getKey(),3);
                String mongoProperty = getValue(entry.getKey(), 4);
                if (!configMap.containsKey(beanName)) {
                    configMap.put(beanName, new MongoConfig());
                }
                setValue(configMap.get(beanName),mongoProperty,entry.getValue());
            }
        }
        return configMap;
    }


    /**
     * 获取key值
     * index：3---》bean名称
     * index：4---》属性
     *
     * @param value
     * @param index
     * @return
     */
    public String getValue(String value,int index) {
        return value.split("\\.")[index];
    }

    private void setValue(MongoConfig config,String key,Object value) {
        if (HOST.equalsIgnoreCase(key)) {
            config.setHost((String) value);
        } else if (PORT.equalsIgnoreCase(key)) {
            config.setPort((Integer) value);
        } else if (DATABASE.equalsIgnoreCase(key)) {
            config.setDatabase((String) value);
        }else {
            throw new IllegalArgumentException("配置信息错误");
        }

    }
}


