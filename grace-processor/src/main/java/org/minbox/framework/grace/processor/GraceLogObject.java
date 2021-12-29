package org.minbox.framework.grace.processor;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

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
     * 解析后的日志内容
     */
    private String content;
    /**
     * 目标方法是否执行成功
     */
    private boolean executionSucceed;
    /**
     * 日志所关联的操作人
     */
    private String operator;
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
    
    public static GraceLogObject initialize() {
        return new GraceLogObject();
    }
}
