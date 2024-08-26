/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.flow.ohscript;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.common.OhscriptExecuteException;
import com.huawei.fit.jober.common.TypeNotSupportException;
import com.huawei.fit.ohscript.external.FitExecutionException;
import com.huawei.fit.ohscript.script.errors.OhPanic;
import com.huawei.fit.ohscript.script.interpreter.ASTEnv;
import com.huawei.fit.ohscript.script.parser.AST;
import com.huawei.fit.ohscript.script.parser.ParserBuilder;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link FlowableService} 的 OhScript 脚本任务的处理器实现。
 *
 * @author 季聿阶
 * @since 2023-10-27
 */
@Component
public class OhScriptTaskHandler implements FlowableService {
    private static final Logger log = Logger.get(OhScriptTaskHandler.class);

    private static final String GENERICABLE_NAME = "handleTask";

    private final Method genericableMethod;

    private final String genericableId;

    private final BrokerClient brokerClient;

    private final BeanContainer beanContainer;

    /**
     * OhScriptTaskHandler构造函数
     *
     * @param container 表示容器
     * @param brokerClient 表示服务调用的代理客户端
     */
    public OhScriptTaskHandler(BeanContainer container, BrokerClient brokerClient) {
        this.genericableMethod = ReflectionUtils.getDeclaredMethod(FlowableService.class, "handleTask", List.class);
        log.info("Get genericable method of flowable service. [method={}]",
                ReflectionUtils.toLongString(this.genericableMethod));
        AnnotationMetadata annotations = container.runtime().resolverOfAnnotations().resolve(this.genericableMethod);
        this.genericableId = annotations.getAnnotation(Genericable.class).id();
        log.info("Get genericable id of flowable service. [id={}]", this.genericableId);
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null.");
        this.beanContainer = container;
    }

    @Override
    @Fitable(id = "OhScript")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        String code = this.getOhScriptCode(flowData)
                .orElseThrow(() -> new IllegalArgumentException("No OhScript code."));
        log.info("Step [OhScriptTaskHandler] [createAstEnv] end, [env.execute] begin");
        ASTEnv env = createAstEnv(flowData, code);
        Object result;
        try {
            result = env.execute();
        } catch (FitExecutionException ex) {
            if (ex.getCause() instanceof TypeNotSupportException) {
                throw (TypeNotSupportException) ex.getCause();
            }
            throw new OhscriptExecuteException(ex.getMessage(), ex.getCause(), ex.getGenericableId(),
                    ex.getFitableId());
        } catch (OhPanic e) {
            throw new OhscriptExecuteException(e.getMessage(), e.getCause(), null, null);
        }
        if (!(result instanceof List)) {
            return Collections.emptyList();
        }
        log.info("Step [OhScriptTaskHandler] [env.execute] end");
        return cast(result);
    }

    private ASTEnv createAstEnv(List<Map<String, Object>> flowData, String code) {
        AST ast = this.getAst(flowData, code);
        ASTEnv env = new ASTEnv(ast);
        env.grant("context", flowData);
        env.setBrokerClient(this.beanContainer, this.brokerClient);
        return env;
    }

    private AST getAst(List<Map<String, Object>> flowData, String code) {
        return buildAst(flowData, code, this.genericableId, this.genericableMethod);
    }

    private static AST buildAst(List<Map<String, Object>> flowData, String code, String genericableId,
                                Method genericableMethod) {
        ParserBuilder parserBuilder = new ParserBuilder();
        parserBuilder.addFitOh(GENERICABLE_NAME, genericableId, genericableMethod.getParameterCount() + 1);
        parserBuilder.addExternalOh("context", flowData);
        return parserBuilder.parseString("", code);
    }

    private Optional<String> getOhScriptCode(List<Map<String, Object>> flowData) {
        if (CollectionUtils.isEmpty(flowData)) {
            return Optional.empty();
        }
        return flowData.stream()
                .filter(MapUtils::isNotEmpty)
                .map(data -> data.get("businessData"))
                .filter(data -> data instanceof Map)
                .map(Map.class::cast)
                .map(data -> data.get("entity"))
                .filter(data -> data instanceof Map)
                .map(Map.class::cast)
                .map(data -> data.get("code"))
                .filter(code -> code instanceof String)
                .map(String.class::cast)
                .findFirst();
    }
}
