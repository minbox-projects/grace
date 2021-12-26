package org.minbox.framework.grace.expression;

import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存解析所使用的表达式实例
 *
 * @author 恒宇少年
 */
public class GraceCachedExpressionEvaluator extends CachedExpressionEvaluator {
    private static final Map<ExpressionKey, Expression> EXPRESSION_MAP = new ConcurrentHashMap();
    /**
     * 表达式前缀字符串
     */
    private static final String EXPRESSION_PREFIX = "{";
    /**
     * 表达式后缀字符串
     * <p>
     * SpEL仅解析前缀后缀字符串内的内容
     */
    private static final String EXPRESSION_SUFFIX = "}";

    /**
     * 创建解析表达式上下文实例
     *
     * @param variables 解析表达式变量集合{@link ExpressionVariables}
     * @return {@link GraceEvaluationContext}
     */
    public GraceEvaluationContext createEvaluationContext(ExpressionVariables variables) {
        return new GraceEvaluationContext(variables);
    }

    /**
     * 解析SpEL表达式模板
     * <p>
     * 由于expression的内容并非全部解析，所以需要重写该方法使用模板解析的方式来配置仅解析"{}"内的内容
     *
     * @param expression 表达式模板
     * @return 解析后的 {@link Expression}实例
     */
    @Override
    protected Expression parseExpression(String expression) {
        return getParser().parseExpression(expression, new TemplateParserContext(EXPRESSION_PREFIX, EXPRESSION_SUFFIX));
    }

    /**
     * 解析表达式
     * <p>
     * 将{@link ExpressionFunctionFactory}缓存的表达式函数列表注册到解析上下文中{@link GraceEvaluationContext}
     *
     * @param conditionExpression 等待解析的表达式
     * @param context             表达式解析时使用的上下文
     * @return 解析后的字符串
     */
    public String parseExpression(String conditionExpression, AnnotatedElementKey elementKey, GraceEvaluationContext context) {
        Map<String, ExpressionFunctionPackageObject> functionPackageObjectMap = ExpressionFunctionFactory.getAllCachedMethod();
        functionPackageObjectMap.keySet()
                .stream().forEach(functionName -> {
                    ExpressionFunctionPackageObject functionPackageObject = functionPackageObjectMap.get(functionName);
                    context.registerFunction(functionPackageObject.getFunctionName(), functionPackageObject.getMethod());
                });
        Expression expression = getExpression(EXPRESSION_MAP, elementKey, conditionExpression);
        return expression.getValue(context, String.class);
    }
}
