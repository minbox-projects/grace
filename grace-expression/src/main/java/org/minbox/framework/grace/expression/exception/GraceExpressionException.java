package org.minbox.framework.grace.expression.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 表达式运行时异常
 *
 * @author 恒宇少年
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraceExpressionException extends RuntimeException {
    public GraceExpressionException(String message) {
        super(message);
    }

    public GraceExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
