package org.minbox.framework.grace.expression;

import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 用于解析SpEL表达式的上下文实例
 *
 * @author 恒宇少年
 */
public class GraceEvaluationContext extends StandardEvaluationContext {
    /**
     * 实例化{@link GraceEvaluationContext}
     *
     * @param variables 表达式解析所需要的变量
     * @see GraceRecordContext
     */
    GraceEvaluationContext(ExpressionVariables variables) {
        this.setVariables(variables.getAllVariables());
    }
}
