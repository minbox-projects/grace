package org.minbox.framework.grace.core;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.minbox.framework.grace.core.operator.GraceLoadOperatorService;
import org.minbox.framework.grace.core.resolve.GraceRecorderResolveProcessor;
import org.minbox.framework.grace.expression.ExpressionVariables;
import org.minbox.framework.grace.expression.GraceCachedExpressionEvaluator;
import org.minbox.framework.grace.expression.GraceEvaluationContext;
import org.minbox.framework.grace.expression.GraceRecordContext;
import org.minbox.framework.grace.expression.annotation.GraceRecorder;
import org.minbox.framework.grace.processor.GraceLogObject;
import org.minbox.framework.grace.processor.GraceLogStorageProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 日志记录者切点后的方法拦截器
 * <p>
 * 执行目标方法之前构建表达式函数、变量到上下文{@link GraceEvaluationContext}实例中
 * 执行目标方法后进行格式化日志模板内容并处理日志数据
 *
 * @author 恒宇少年
 */
@Slf4j
public class GraceRecorderMethodInterceptor implements MethodInterceptor, BeanFactoryAware {
    /**
     * The bean name of the current class registered in the ioc
     */
    public static final String BEAN_NAME = GraceRecorderMethodInterceptor.class.getSimpleName();
    private BeanFactoryResolver beanFactoryResolver;
    private GraceLogStorageProcessor storageProcessor;
    private GraceLoadOperatorService operatorService;

    public GraceRecorderMethodInterceptor(ObjectProvider<GraceLogStorageProcessor> storageProcessorProvider,
                                          ObjectProvider<GraceLoadOperatorService> operatorServiceProvider) {
        this.storageProcessor = storageProcessorProvider.getIfAvailable();
        this.operatorService = operatorServiceProvider.getIfAvailable();
        Assert.notNull(storageProcessor, "无法注入GraceLogStorageProcessor接口实现类实例，请实现该接口.");
    }

    /**
     * 执行目标方法切面业务逻辑处理
     * <p>
     * 切面前：
     * 提取{@link GraceRecorder#bizNo()}、{@link GraceRecorder#category()}、{@link GraceRecorder#operator()}数据
     * 提取参数列表并写入到{@link ExpressionVariables}变量集合内
     * 目标方法执行：
     * 将目标方法返回结果使用"result"变量名写入到{@link ExpressionVariables}
     * 切面后：
     * 提取{@link GraceRecorder#condition()}配置的条件表达，如果该条件的表达式解析结果为"true"则执行操作日志的解析处理
     * 提取{@link GraceRecorder#success()}并解析执行成功后的日志模板
     * 如果执行出现异常则提取{@link GraceRecorder#fail()}的文本内容
     * 封装{@link GraceLogObject}并交付给"processor"进行后续的数据持久化处理
     *
     * @param invocation 目标方法
     * @return 目标方法执行返回的数据结果
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        GraceRecorderAnnotationDataExtractor extractor = null;
        Map<String, Object> customizeVariables = null;
        Object result;
        boolean executionSucceed = true;
        try {
            extractor = new GraceRecorderAnnotationDataExtractor(invocation, this.operatorService);
            Map<String, Object> parameterValueMap = extractor.getParameterValues();
            ExpressionVariables variables = ExpressionVariables.initialize();
            if (!ObjectUtils.isEmpty(operatorService) && !ObjectUtils.isEmpty(operatorService.getExtra())) {
                variables.addVariables(operatorService.getExtra());
            }
            variables.addVariables(parameterValueMap);
            GraceRecordContext.pushExpressionVariables(variables);
            result = invocation.proceed();
            customizeVariables = GraceVariableContext.getCustomizeVariables();
            if (!ObjectUtils.isEmpty(customizeVariables)) {
                variables.addVariables(customizeVariables);
            }
            variables.addVariable(GraceConstants.RESULT_VARIABLE_KEY, result);
        } catch (Exception e) {
            executionSucceed = false;
            throw e;
        } finally {
            GraceVariableContext.remove();
            GraceCachedExpressionEvaluator evaluator = new GraceCachedExpressionEvaluator();
            ExpressionVariables variables = GraceRecordContext.popExpressionVariables();
            GraceEvaluationContext evaluationContext = evaluator.createEvaluationContext(variables);
            evaluationContext.setBeanResolver(this.beanFactoryResolver);
            AnnotatedElementKey elementKey = new AnnotatedElementKey(extractor.getSpecificMethod(), extractor.getTargetClass());
            boolean conditionExecute = true;
            if (!ObjectUtils.isEmpty(extractor.getConditionExpression().trim())) {
                conditionExecute = evaluator.parseExpression(Boolean.class, extractor.getConditionExpression(), elementKey, evaluationContext);
            }
            if (conditionExecute) {
                GraceRecorderResolveProcessor resolveProcessor =
                        new GraceRecorderResolveProcessor(extractor, evaluator, evaluationContext, elementKey, executionSucceed, customizeVariables);
                GraceLogObject graceLogObject = resolveProcessor.processing();
                storageProcessor.storage(graceLogObject);
            }
        }
        return result;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!ObjectUtils.isEmpty(beanFactory) && ObjectUtils.isEmpty(this.beanFactoryResolver)) {
            this.beanFactoryResolver = new BeanFactoryResolver(beanFactory);
        }
    }
}
