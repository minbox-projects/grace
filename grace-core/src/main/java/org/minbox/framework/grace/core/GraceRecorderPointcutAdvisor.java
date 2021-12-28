package org.minbox.framework.grace.core;

import org.aopalliance.aop.Advice;
import org.minbox.framework.grace.expression.annotation.GraceRecorder;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * 配置日志采集者支持{@link GraceRecorder}注解的AOP切入点
 *
 * @author 恒宇少年
 */
public class GraceRecorderPointcutAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {
    /**
     * The bean name of the current class registered in the ioc
     */
    public static final String BEAN_NAME = GraceRecorderPointcutAdvisor.class.getSimpleName();
    private BeanFactory beanFactory;
    private Advice advice;
    private Pointcut pointcut;

    /**
     * 构建删除初始化全局数据
     * <p>
     * {@link Pointcut}注解切入点配置使用类上方法的注解，只生效方法注解
     *
     * @param methodInterceptor {@link GraceRecorder}注解方法拦截器
     */
    public GraceRecorderPointcutAdvisor(GraceRecorderMethodInterceptor methodInterceptor) {
        this.pointcut = AnnotationMatchingPointcut.forMethodAnnotation(GraceRecorder.class);
        this.advice = methodInterceptor;
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        Assert.notNull(this.advice, "The implementation instance of advice cannot be empty.");
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
