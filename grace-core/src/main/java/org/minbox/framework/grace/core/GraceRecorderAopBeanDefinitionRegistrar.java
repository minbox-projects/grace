package org.minbox.framework.grace.core;

import lombok.extern.slf4j.Slf4j;
import org.minbox.framework.util.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 配置日志采集注解切面相关类
 *
 * @author 恒宇少年
 * @see GraceRecorderMethodInterceptor
 * @see GraceRecorderPointcutAdvisor
 */
@Slf4j
public class GraceRecorderAopBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanUtils.registerInfrastructureBeanIfAbsent(registry, GraceRecorderMethodInterceptor.BEAN_NAME, GraceRecorderMethodInterceptor.class);
        BeanUtils.registerInfrastructureBeanIfAbsent(registry, GraceRecorderPointcutAdvisor.BEAN_NAME, GraceRecorderPointcutAdvisor.class);
        log.info("The registration of the Grace log aspect class is completed.");
    }
}
