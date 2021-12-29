package org.minbox.framework.grace.processor;

/**
 * 操作日志持久化处理接口定义
 *
 * @author 恒宇少年
 */
public interface GraceLogStorageProcessor {
    /**
     * 持久化日志封装对象
     *
     * @param graceLogObject {@link GraceLogObject} 日志封装对象
     */
    void storage(GraceLogObject graceLogObject);
}
