package org.minbox.framework.grace.processor;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 日志对象类
 *
 * @author 恒宇少年
 */
@Data
@Accessors(chain = true)
public class GraceLogObject {
    /**
     * 日志所属分组
     */
    private String category;
    /**
     * 日志标签列表
     */
    private String[] tags;
    /**
     * 解析后的日志内容
     */
    private String content;
    /**
     * 目标方法是否执行成功
     */
    private boolean executionSucceed;
    /**
     * 日志所关联的操作人名称
     */
    private String operator;
    /**
     * 日志所关联的操作人编号
     */
    private String operatorId;
    /**
     * 日志所关联的业务编号
     */
    private String bizNo;
    /**
     * 日志生成的位置
     * 格式为："全限定类名#方法名"
     */
    private String generatedLocation;
    /**
     * 日志生成的时间
     */
    private LocalDateTime time = LocalDateTime.now();
    /**
     * 执行目标操作方法遇到异常时的堆栈信息
     */
    private String exceptionStackTrace;
    /**
     * 自定义的变量集合
     * <p>
     * 可用于扩展操作日志存储时所需要的数据
     */
    private Map<String, Object> customizeVariables;

    public static GraceLogObject initialize() {
        return new GraceLogObject();
    }
}
