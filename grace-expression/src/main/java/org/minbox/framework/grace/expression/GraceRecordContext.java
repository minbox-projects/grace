package org.minbox.framework.grace.expression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.minbox.framework.grace.expression.exception.GraceExpressionException;
import org.springframework.util.ObjectUtils;

import java.util.Stack;

/**
 * 日志采集记录上下文实例
 *
 * @author 恒宇少年
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraceRecordContext {
    /**
     * {@link ExpressionVariables}表达式变量栈集合
     * <p>
     * 使用{@link Stack}来隔离一个线程操作内存在多个方法需要记录操作日志的变量
     */
    private static final InheritableThreadLocal<Stack<ExpressionVariables>> VARIABLES_STACK_MAP = new InheritableThreadLocal();

    /**
     * 向变量栈集合添加一个新的{@link ExpressionVariables}实例
     *
     * @param variables 表达式变量集合实例
     */
    public static void pushExpressionVariables(ExpressionVariables variables) {
        boolean isFirstPush = ObjectUtils.isEmpty(VARIABLES_STACK_MAP.get());
        Stack<ExpressionVariables> stack = isFirstPush ? new Stack() : VARIABLES_STACK_MAP.get();
        stack.push(variables);
        if (isFirstPush) {
            VARIABLES_STACK_MAP.set(stack);
        }
    }

    /**
     * 从变量栈集合获取并移除一个{@link ExpressionVariables}实例
     *
     * @return {@link ExpressionVariables} 表达式变量实例
     */
    public static ExpressionVariables popExpressionVariables() {
        Stack<ExpressionVariables> stack = VARIABLES_STACK_MAP.get();
        if (ObjectUtils.isEmpty(stack)) {
            throw new GraceExpressionException("当前线程中并未获取到表达式变量栈集合.");
        }
        ExpressionVariables variables = stack.pop();
        if (stack.empty()) {
            VARIABLES_STACK_MAP.remove();
        }
        return variables;
    }
}
