package org.minbox.framework.grace.core;

import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志变量上下文
 *
 * @author 恒宇少年
 */
public class GraceVariableContext {
    private static final ThreadLocal<Map<String, Object>> CUSTOMIZE_VARIABLES = new ThreadLocal();

    public static void setVariable(String key, Object value) {
        boolean isFirstSet = ObjectUtils.isEmpty(CUSTOMIZE_VARIABLES.get());
        Map<String, Object> variables = isFirstSet ? new HashMap<>() : CUSTOMIZE_VARIABLES.get();
        variables.put(key, value);
        if (isFirstSet) {
            CUSTOMIZE_VARIABLES.set(variables);
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
