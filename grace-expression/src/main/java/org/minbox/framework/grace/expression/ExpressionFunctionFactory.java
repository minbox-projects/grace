package org.minbox.framework.grace.expression;

import lombok.extern.slf4j.Slf4j;
import org.minbox.framework.grace.expression.annotation.GraceExpressionFunction;
import org.minbox.framework.grace.expression.annotation.GraceExpressionFunctionDefiner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表达式函数工厂类
 * <p>
 * 会自动扫描并加载项目中使用{@link GraceExpressionFunction}注解描述的函数，缓存到该工厂类的全局集合中
 *
 * @author 恒宇少年
 */
@Order
@Slf4j
public class ExpressionFunctionFactory implements InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private static final Map<String, ExpressionFunctionPackageObject> CACHED_METHOD_MAP = new HashMap();
    private static final String METHOD_FULL_NAME_FORMAT = "%s#%s";

    /**
     * 加载项目中使用{@link GraceExpressionFunction}定义的函数并进行缓存
     * <p>
     * 由于从{@link ApplicationContext}应用上下文中加载的类名比较多，
     * 所以需要判定类上是否定义了{@link GraceExpressionFunctionDefiner}，存在该注解再进行表达式函数的解析
     */
    private void loadDefineFunction() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        List<Class<?>> filteredBeanClassList =
                Arrays.stream(beanNames).map(beanName -> applicationContext.getBean(beanName).getClass())
                        .filter(beanClass -> beanClass.isAnnotationPresent(GraceExpressionFunctionDefiner.class))
                        .collect(Collectors.toList());
        filteredBeanClassList.stream().forEach(beanClass -> {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(beanClass);
            for (Method method : methods) {
                try {
                    if (method.isAnnotationPresent(GraceExpressionFunction.class)) {
                        GraceExpressionFunction function = AnnotationUtils.getAnnotation(method, GraceExpressionFunction.class);
                        ExpressionFunctionPackageObject packageObject =
                                ExpressionFunctionPackageObject.pack(method.getName(), method, function.isBeforeExecute());
                        CACHED_METHOD_MAP.put(method.getName(), packageObject);
                        String methodFullName = String.format(METHOD_FULL_NAME_FORMAT, beanClass.getName(), method.getName());
                        log.debug("Grace表达式函数：{}，加载完成并已被缓存.", methodFullName);
                    }
                } catch (Exception e) {
                    log.error("加载Grace表达式函数异常.", e);
                }
            }
        });
    }

    /**
     * 获取指定名称的方法实例
     *
     * @param methodName 方法名称{@link Method#getName()}
     * @return {@link Method}方法实例
     */
    public static ExpressionFunctionPackageObject getMethod(String methodName) {
        return CACHED_METHOD_MAP.get(methodName);
    }

    /**
     * 获取全部缓存的方法列表
     *
     * @return 缓存方法集合的副本，不允许修改缓存数据
     */
    public static Map<String, ExpressionFunctionPackageObject> getAllCachedMethod() {
        Map<String, ExpressionFunctionPackageObject> tempMethodMap = CollectionUtils.newHashMap(CACHED_METHOD_MAP.size());
        tempMethodMap.putAll(CACHED_METHOD_MAP);
        return tempMethodMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.loadDefineFunction();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
