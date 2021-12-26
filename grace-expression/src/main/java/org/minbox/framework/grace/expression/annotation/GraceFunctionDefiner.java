package org.minbox.framework.grace.expression.annotation;

import org.minbox.framework.grace.expression.ExpressionFunctionFactory;

import java.lang.annotation.*;

/**
 * 标注扫描类的注解
 * <p>
 * {@link ExpressionFunctionFactory}在扫描类加载表达式函数时会根据该注解进行过滤，
 * 如果类上标注该注解则进行方法定义注解{@link GraceFunction}解析
 *
 * @author 恒宇少年
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraceFunctionDefiner {
    //...
}
