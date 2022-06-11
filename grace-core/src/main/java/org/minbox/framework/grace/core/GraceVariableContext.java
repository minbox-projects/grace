package org.minbox.framework.grace.core;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 操作日志变量上下文
 *
 * @author 恒宇少年
 */
public class GraceVariableContext {
    private static final ThreadLocal<Map<String, Object>> CUSTOMIZE_VARIABLES = new ThreadLocal();
    private static final int VARIABLE_MAP_EXPECTED_SIZE = 1;

    public static void setVariable(String key, Object value) {
        boolean isFirstSet = ObjectUtils.isEmpty(CUSTOMIZE_VARIABLES.get());
        Map<String, Object> variables = isFirstSet ? CollectionUtils.newHashMap(VARIABLE_MAP_EXPECTED_SIZE) : CUSTOMIZE_VARIABLES.get();
        variables.put(key, value);
        if (isFirstSet) {
            CUSTOMIZE_VARIABLES.set(variables);
        }
    }

    public static void setVariable(Enum<?> key, Object value) {
        setVariable(key.name(), value);
    }

    public static void putVariables(Map<String, Object> variables) {
        if (!ObjectUtils.isEmpty(variables)) {
            variables.keySet().stream().forEach(key -> setVariable(key, variables.get(key)));
        }
    }

    static Map<String, Object> getCustomizeVariables() {
        return CUSTOMIZE_VARIABLES.get();
    }

    static void remove() {
        if (!ObjectUtils.isEmpty(CUSTOMIZE_VARIABLES.get())) {
            CUSTOMIZE_VARIABLES.remove();
        }
    }
}
