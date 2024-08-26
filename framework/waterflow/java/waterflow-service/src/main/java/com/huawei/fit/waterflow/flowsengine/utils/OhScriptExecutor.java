/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.utils;

import static com.huawei.fit.waterflow.common.Constant.BUSINESS_DATA_KEY;
import static com.huawei.fit.waterflow.common.Constant.PASS_DATA;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.ohscript.script.errors.GrammarSyntaxException;
import com.huawei.fit.ohscript.script.errors.OhPanic;
import com.huawei.fit.ohscript.script.errors.ScriptExecutionException;
import com.huawei.fit.ohscript.script.interpreter.ASTEnv;
import com.huawei.fit.ohscript.script.lexer.Lexer;
import com.huawei.fit.ohscript.script.parser.AST;
import com.huawei.fit.ohscript.script.parser.GrammarBuilder;
import com.huawei.fit.ohscript.script.parser.ParserBuilder;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * OhScript执行类
 *
 * @author 李哲峰
 * @since 2023/12/21
 */
public final class OhScriptExecutor {
    /**
     * OhScript前段脚本用于获取条件节点用户完整数据
     * 最终的用户数据变量名称需要与客户约定
     */
    private static final String EXTERNAL_PARAMETER_KEY = "userData";

    private static final String CODE_SEGMENT_PREFIX = StringUtils.format(
            "let businessData = ext::util" + ".stringToJson(ext::{0}.get(\"{1}\"));  "
                    + "let passData = ext::util.stringToJson(ext::{2}.get(\"{3}\")); ", EXTERNAL_PARAMETER_KEY,
            BUSINESS_DATA_KEY, EXTERNAL_PARAMETER_KEY, PASS_DATA);

    private static final Logger log = Logger.get(OhScriptExecutor.class);

    /**
     * 执行用户给定的OhScript条件判定脚本，返回判定结果
     *
     * @param flowData {@link FlowData} 流程执行过程中的用户数据
     * @param conditionRule 需要进行判断的条件表达式
     * @return 条件表达式的判定结果
     */
    public static boolean evaluateConditionRule(FlowData flowData, String conditionRule) {
        ParserBuilder parserBuilder = new ParserBuilder(new GrammarBuilder(), new Lexer());
        JSONObject businessDataJsonObject = new JSONObject(
                Optional.ofNullable(flowData.getBusinessData()).orElse(new HashMap<>()));
        String businessDataJson = JSON.toJSONString(businessDataJsonObject, SerializerFeature.WriteMapNullValue);
        JSONObject passDataJsonObject = new JSONObject(
                Optional.ofNullable(flowData.getPassData()).orElse(new HashMap<>()));
        String passDataJson = JSON.toJSONString(passDataJsonObject, SerializerFeature.WriteMapNullValue);
        Map<String, Object> userData = new HashMap<>();
        userData.put(BUSINESS_DATA_KEY, businessDataJson);
        userData.put(PASS_DATA, passDataJson);
        parserBuilder.addExternalOh(EXTERNAL_PARAMETER_KEY, userData);
        String formatConditionRule = FlowUtil.formatConditionRule(conditionRule).trim();
        // 兼容历史 !{{var}}类型，且传入的是字符串类型的true/false
        boolean isPatternOfNot = false;
        if (FlowUtil.isPatternOfNot(conditionRule)) {
            isPatternOfNot = true;
            formatConditionRule = formatConditionRule.substring(1);
        }
        String ohScript = CODE_SEGMENT_PREFIX + formatConditionRule;
        String uuid = UUID.randomUUID().toString();
        log.warn("uuid:{0}, evaluateConditionRule:{1}", uuid, ohScript);
        Object execResult = getExecResult(conditionRule, parserBuilder, ohScript);
        log.warn("uuid:{0}, execResult:{1}", uuid, execResult);
        if (execResult instanceof String) {
            if ("TRUE".equalsIgnoreCase(ObjectUtils.cast(execResult))) {
                execResult = true;
            }
            if ("FALSE".equalsIgnoreCase(ObjectUtils.cast(execResult))) {
                execResult = false;
            }
        }
        if (!(execResult instanceof Boolean)) {
            String exceptionMsg = String.format("Unexpected FlowConditionNode OhScript return value. "
                    + "OhScript Content: \"%s\"; Return Value: %s", ohScript, execResult);
            log.error("The FlowConditionNode failed to judge the flow condition, error message: {}", exceptionMsg);
            throw new JobberException(ErrorCodes.TYPE_CONVERT_FAILED);
        }
        boolean result = ObjectUtils.cast(execResult);
        return isPatternOfNot ? !result : result;
    }

    private static Object getExecResult(String conditionRule, ParserBuilder parserBuilder, String ohScript) {
        Object execResult;
        try {
            AST ast = parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            execResult = env.execute();
        } catch (GrammarSyntaxException e) {
            String exceptionMsg = StringUtils.format("Invalid OhScript input. OhScript Content: \"{0}\"", ohScript);
            log.error("The FlowConditionNode failed to judge the flow condition, error message: {}, error cause: {}",
                    exceptionMsg, e.getMessage());
            throw new JobberException(ErrorCodes.FLOW_ENGINE_OHSCRIPT_GRAMMAR_ERROR, ohScript);
        } catch (OhPanic | ScriptExecutionException | NullPointerException e) {
            String exceptionMsg = StringUtils.format("Condition rule cannot be evaluated. Condition Rule: \"{0}\"",
                    conditionRule);
            log.error("The FlowConditionNode failed to judge the flow condition, error message: {}, error cause: {}",
                    exceptionMsg, e.getMessage());
            throw new JobberException(ErrorCodes.FLOW_ENGINE_CONDITION_RULE_PARSE_ERROR, conditionRule);
        }
        return execResult;
    }
}
