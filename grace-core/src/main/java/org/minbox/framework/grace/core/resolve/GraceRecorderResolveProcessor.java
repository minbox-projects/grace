package org.minbox.framework.grace.core.resolve;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.minbox.framework.grace.core.GraceRecorderAnnotationDataExtractor;
import org.minbox.framework.grace.expression.GraceCachedExpressionEvaluator;
import org.minbox.framework.grace.expression.GraceEvaluationContext;
import org.minbox.framework.grace.expression.annotation.GraceRecorder;
import org.minbox.framework.grace.processor.GraceLogObject;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 操作日志内容解析处理类
 * <p>
 * 解析{@link GraceRecorder}操作日志注解所提供的配置内容，封装成{@link GraceLogObject}日志对象实例交付给后续"processor"
 *
 * @author 恒宇少年
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class GraceRecorderResolveProcessor {
    private GraceLogObject graceLogObject;
    private GraceRecorderAnnotationDataExtractor extractor;
    private GraceCachedExpressionEvaluator evaluator;
    private GraceEvaluationContext evaluationContext;
    private AnnotatedElementKey elementKey;
    private boolean executionSucceed;
    private Map<String, Object> customizeVariables;

    public GraceRecorderResolveProcessor(GraceRecorderAnnotationDataExtractor extractor, GraceCachedExpressionEvaluator evaluator,
                                         GraceEvaluationContext evaluationContext, AnnotatedElementKey elementKey,
                                         boolean executionSucceed, Map<String, Object> customizeVariables) {
        this.extractor = extractor;
        this.graceLogObject = GraceLogObject.initialize();
        this.graceLogObject.setExecutionSucceed(executionSucceed);
        this.evaluator = evaluator;
        this.evaluationContext = evaluationContext;
        this.elementKey = elementKey;
        this.executionSucceed = executionSucceed;
        this.customizeVariables = customizeVariables;
    }

    public GraceLogObject processing() {
        if (!ObjectUtils.isEmpty(extractor.getSuccessTemplate())) {
            this.graceLogObject.setContent(this.executionSucceed ?
                    evaluator.parseExpression(String.class, extractor.getSuccessTemplate(), elementKey, evaluationContext) :
                    extractor.getFailText());
        }
        if (!ObjectUtils.isEmpty(extractor.getBizNo())) {
            String parsedBizNo = evaluator.parseExpression(String.class, extractor.getBizNo(), elementKey, evaluationContext);
            this.graceLogObject.setBizNo(parsedBizNo);
        }
        if (!ObjectUtils.isEmpty(extractor.getOperator())) {
            String parsedOperator = evaluator.parseExpression(String.class, extractor.getOperator(), elementKey, evaluationContext);
            this.graceLogObject.setOperator(parsedOperator);
        }
        if (!ObjectUtils.isEmpty(extractor.getTags())) {
            this.graceLogObject.setTags(extractor.getTags());
        }
        this.graceLogObject.setCategory(extractor.getCategory())
                .setGeneratedLocation(extractor.getGeneratedLocation())
                .setOperatorId(extractor.getOperatorId())
                .setCustomizeVariables(this.customizeVariables);
        return this.graceLogObject;
    }
}
