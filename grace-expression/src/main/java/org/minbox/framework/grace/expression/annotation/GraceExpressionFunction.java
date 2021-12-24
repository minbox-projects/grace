package org.minbox.framework.grace.expression.annotation;

import org.minbox.framework.grace.expression.ExpressionFunctionFactory;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.*;

/**
 * 表达式函数注解
 * <p>
 * 使用该注解标注的函数会在项目启动时自动加载到{@link ExpressionFunctionFactory}进行全局缓存
 * 在操作日志的AOP执行目标函数之前将缓存的方法列表注册到{@link StandardEvaluationContext}SpEL表达式上下文实例中
 *
 * @author 恒宇少年
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraceExpressionFunction {
    /**
     * 是否在目标函数之前执行
     *
     * @return 默认返回false
     */
    boolean isBeforeExecute() default false;
}
