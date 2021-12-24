package org.minbox.framework.grace.expression;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

/**
 * 表达式函数封装对象
 *
 * @author 恒宇少年
 */
@Getter
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpressionFunctionPackageObject {
    /**
     * 函数名称
     */
    private String functionName;
    /**
     * 函数对应绑定的{@link Method}实例
     */
    private Method method;
    /**
     * 是否在业务方法之前执行
     */
    private boolean isBeforeExecute;

    static ExpressionFunctionPackageObject pack(String functionName, Method method, boolean isBeforeExecute) {
        return new ExpressionFunctionPackageObject(functionName, method, isBeforeExecute);
    }
}
