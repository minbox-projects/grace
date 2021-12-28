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
    private String category;
    private String content;
    private boolean executionSucceed;
    private String operator;
    private String bizNo;
    private LocalDateTime time = LocalDateTime.now();

    public static GraceLogObject initialize() {
        return new GraceLogObject();
    }
}
