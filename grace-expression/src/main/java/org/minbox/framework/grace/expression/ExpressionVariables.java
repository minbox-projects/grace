package org.minbox.framework.grace.expression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 表达式变量封装实体
 * <p>
 * 存储解析表达式所需要的变量列表
 *
 * @author 恒宇少年
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpressionVariables {
    /**
     * 线程安全的变量集合
     */
    private final Map<String, Object> VARIABLE_MAP = new HashMap<>();

    /**
     * 添加变量到集合
     *
     * @param key   变量Key
     * @param value 变量值
     */
    public void addVariable(String key, Object value) {
        VARIABLE_MAP.put(key, value);
    }

    /**
     * 添加多个变量到集合
     *
     * @param variables 多个待添加的变量
     */
    public void addVariables(Map<String, Object> variables) {
        VARIABLE_MAP.putAll(variables);
    }

    /**
     * 获取全部的变量
     *
     * @return {@link #VARIABLE_MAP}
     */
    public Map<String, Object> getAllVariables() {
        Map<String, Object> tempVariables = CollectionUtils.newHashMap(VARIABLE_MAP.size());
        tempVariables.putAll(VARIABLE_MAP);
        return tempVariables;
    }

    /**
     * 获取指定Key的值
     *
     * @param key 变量Key
     * @return 变量值
     */
    public Object getVariable(String key) {
        return VARIABLE_MAP.get(key);
    }

    /**
     * 提供一个空的{@link ExpressionVariables}变量集合对象
     *
     * @return {@link ExpressionVariables}
     */
    public static ExpressionVariables initialize() {
        return new ExpressionVariables();
    }
}
