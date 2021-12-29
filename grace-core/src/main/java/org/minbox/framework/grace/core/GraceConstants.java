package org.minbox.framework.grace.core;

import org.minbox.framework.grace.expression.ExpressionVariables;

/**
 * 常量定义
 *
 * @author 恒宇少年
 */
public interface GraceConstants {
    /**
     * 定义方法返回值在{@link ExpressionVariables}变量集合内的Key
     */
    String RESULT_VARIABLE_KEY = "result";
    /**
     * 定义方法参数索引值在{@link ExpressionVariables}变量集合的格式
     */
    String PARAMETER_INDEX_VALUE_FORMAT = "p%d";
    /**
     * 日志产生位置的格式
     */
    String LOG_GENERATED_LOCATION_FORMAT = "%s#%s";
}
