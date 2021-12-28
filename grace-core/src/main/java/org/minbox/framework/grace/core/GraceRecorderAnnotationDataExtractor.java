package org.minbox.framework.grace.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.minbox.framework.grace.expression.annotation.GraceRecorder;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link GraceRecorder}注解数据提取者
 * <p>
 * 跟方法切面对象实例{@link MethodInvocation}进行提取解析日志内容所需要的数据
 *
 * @author 恒宇少年
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraceRecorderAnnotationDataExtractor {
    private GraceRecorder graceRecorder;
    @Getter
    private Class<?> targetClass;
    @Getter
    private Method specificMethod;
    private Object[] arguments;
    private Parameter[] parameters;

    public GraceRecorderAnnotationDataExtractor(MethodInvocation invocation) {
        this.graceRecorder = AnnotationUtils.getAnnotation(invocation.getMethod(), GraceRecorder.class);
        Assert.notNull(graceRecorder, "@GraceRecorder注解实例不可以为空.");
        this.targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        Assert.notNull(targetClass, "无法获取切面方法所属目标类.");
        this.specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
        this.arguments = invocation.getArguments();
        this.parameters = this.specificMethod.getParameters();
    }

    /**
     * 获取参数定义以及值的映射集合
     * 通过{@link MethodInvocation}实例获取切面目标的{@link Method}，通过{@link MethodInvocation#getArguments()}获取各个参数的传递值
     *
     * @return 定义参数的值集合
     */
    public Map<String, Object> getParameterValues() {
        Map<String, Object> parameterValues = new HashMap<>();
        if (ObjectUtils.isEmpty(parameters)) {
            return parameterValues;
        }
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = this.arguments[i];
            parameterValues.put(parameter.getName(), value);
            String parameterIndexName = String.format(GraceConstants.PARAMETER_INDEX_VALUE_FORMAT, i);
            parameterValues.put(parameterIndexName, value);
        }
        return parameterValues;
    }

    public String getSuccessTemplate() {
        return this.graceRecorder.success();
    }

    public String getFailText() {
        return this.graceRecorder.fail();
    }

    public String getBizNo() {
        return this.graceRecorder.bizNo();
    }

    public String getCategory() {
        return this.graceRecorder.category();
    }

    public String getConditionExpression() {
        return this.graceRecorder.condition();
    }
}
