package org.minbox.framework.grace.expression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.minbox.framework.grace.expression.annotation.GraceFunction;
import org.minbox.framework.grace.expression.annotation.GraceFunctionDefiner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 表达式函数工厂类
 * <p>
 * 会自动扫描并加载项目中使用{@link GraceFunction}注解描述的函数，缓存到该工厂类的全局集合中
 *
 * @author 恒宇少年
 */
@Order
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpressionFunctionFactory implements InitializingBean {
    private static final Map<String, ExpressionFunctionPackageObject> CACHED_METHOD_MAP = new HashMap();
    private static final String METHOD_FULL_NAME_FORMAT = "%s#%s";
    private List<String> functionScanPackages;

    public ExpressionFunctionFactory(List<String> functionScanPackages) {
        this.functionScanPackages = functionScanPackages;
        Assert.notNull(functionScanPackages, "表达式函数扫描包参数不可以为空.");
    }

    /**
     * 加载项目中使用{@link GraceFunction}定义的函数并进行缓存
     * <p>
     * 由于从{@link ApplicationContext}应用上下文中加载的类名比较多，
     * 所以需要判定类上是否定义了{@link GraceFunctionDefiner}，存在该注解再进行表达式函数的解析
     */
    private void loadDefineFunction() {
        List<Class<?>> filteredBeanClassList = this.getFunctionDefinerClass();
        filteredBeanClassList.stream().forEach(beanClass -> {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(beanClass);
            for (Method method : methods) {
                try {
                    if (method.isAnnotationPresent(GraceFunction.class)) {
                        GraceFunction function = AnnotationUtils.getAnnotation(method, GraceFunction.class);
                        ExpressionFunctionPackageObject packageObject =
                                ExpressionFunctionPackageObject.pack(method.getName(), method, function.isBeforeExecute());
                        CACHED_METHOD_MAP.put(method.getName(), packageObject);
                        String methodFullName = String.format(METHOD_FULL_NAME_FORMAT, method.getDeclaringClass().getName(), method.getName());
                        log.debug("Grace表达式函数：{}，加载完成并已被缓存.", methodFullName);
                    }
                } catch (Exception e) {
                    log.error("加载Grace表达式函数异常.", e);
                }
            }
        });
    }

    /**
     * 获取定义{@link GraceFunctionDefiner}注解的类列表
     * <p>
     * 根据配置的多个根包名进行扫描符合条件的类
     *
     * @return 类列表
     */
    private List<Class<?>> getFunctionDefinerClass() {
        Set<Class<?>> classSet = new HashSet<>();
        functionScanPackages.stream().forEach(scanPackage -> {
            try {
                ClassScanner scanner = new ClassScanner(scanPackage, true);
                classSet.addAll(scanner.doScanning());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        return classSet.stream()
                .filter(beanClass -> !ObjectUtils.isEmpty(AnnotationUtils.findAnnotation(beanClass, GraceFunctionDefiner.class)))
                .collect(Collectors.toList());
    }

    /**
     * 获取全部缓存的方法列表
     *
     * @return 缓存方法集合的副本，不允许修改缓存数据
     */
    static Map<String, ExpressionFunctionPackageObject> getAllCachedMethod() {
        Map<String, ExpressionFunctionPackageObject> tempMethodMap = CollectionUtils.newHashMap(CACHED_METHOD_MAP.size());
        tempMethodMap.putAll(CACHED_METHOD_MAP);
        return tempMethodMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.loadDefineFunction();
    }
}
