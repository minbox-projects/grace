package org.minbox.framework.grace.expression.annotation;

import java.lang.annotation.*;

/**
 * 日志采集注解
 *
 * @author 恒宇少年
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions">SpEL</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraceRecorder {
    /**
     * 业务处理成功后的操作日志模板
     *
     * @return 支持SpEL表达式的模板内容
     */
    String success();

    /**
     * 业务处理失败后的操作日志文本内容
     *
     * @return 操作日志失败的文本版本
     */
    String fail() default "";

    /**
     * 判定是否采集操作日志的条件
     *
     * @return 只是SpEL表达式方式配置，如果表达式解析后返回"true"则执行操作日志采集
     */
    String condition() default "";

    /**
     * 操作日志绑定的业务对象编号
     *
     * @return 操作业务对象的唯一编号，支持使用SpEL表达式提取数据
     */
    String bizNo() default "";

    /**
     * 操作日志的执行人
     *
     * @return 支持使用SpEL表达式提取数据，可以全局配置，也支持配置使用全局变量的方式从上下文中提取数据
     */
    String operator() default "";

    /**
     * 操作日志类别
     *
     * @return 用于对操作进行自定义分组，可以根据分组来处理不同的业务逻辑
     */
    String category();

    /**
     * 操作日志标签
     *
     * @return 用于对操作日志进行标签归类
     */
    String[] tags() default "";
}
