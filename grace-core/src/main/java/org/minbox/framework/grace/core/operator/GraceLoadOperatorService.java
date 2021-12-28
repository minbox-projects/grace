package org.minbox.framework.grace.core.operator;

import java.util.Map;

/**
 * 加载操作人接口定义
 *
 * @author 恒宇少年
 */
public interface GraceLoadOperatorService {
    /**
     * 配置加载操作人的名称
     *
     * @return 操作人名称
     */
    String getOperatorName();

    /**
     * 配置加载操作人的编号
     *
     * @return 操作人编号
     */
    String getOperatorId();

    /**
     * 配置扩展的数据集合
     * <p>
     * 该方法的返回值会写入到SpEL表达式解析的变量集合中
     *
     * @return 扩展数据列表
     * @see org.minbox.framework.grace.expression.ExpressionVariables
     */
    Map<String, Object> getExtra();
}
