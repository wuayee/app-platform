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
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

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

    private static final String CODE_SEGMENT_PREFIX = StringUtils.format("let businessDataJson = ext::util"
                    + ".stringToJson(ext::{0}.get(\"{1}\")); let {2} = ext::util.jsonToEntity(businessDataJson); "
                    + "let passDataJson = ext::util.stringToJson(ext::{3}.get(\"{4}\")); "
                    + "let {5} = ext::util.jsonToEntity(passDataJson);", EXTERNAL_PARAMETER_KEY, BUSINESS_DATA_KEY,
            BUSINESS_DATA_KEY, EXTERNAL_PARAMETER_KEY, PASS_DATA, PASS_DATA);

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
        String businessDataJson = new JSONObject(
                Optional.ofNullable(flowData.getBusinessData()).orElse(new HashMap<>())).toJSONString();
        String passDataJson = new JSONObject(
                Optional.ofNullable(flowData.getPassData()).orElse(new HashMap<>())).toJSONString();
        Map<String, Object> userData = new HashMap<>();
        userData.put(BUSINESS_DATA_KEY, businessDataJson);
        userData.put(PASS_DATA, passDataJson);
        parserBuilder.addExternalOh(EXTERNAL_PARAMETER_KEY, userData);
        String ohScript = CODE_SEGMENT_PREFIX + FlowUtil.formatConditionRule(conditionRule);
        String uuid = UUID.randomUUID().toString();
        log.warn("uuid:{0}, evaluateConditionRule:{1}", uuid, ohScript);
        Object execResult = "";
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
        log.warn("uuid:{0}, execResult:{1}", uuid, execResult);
        if (!(execResult instanceof Boolean)) {
            String exceptionMsg = String.format("Unexpected FlowConditionNode OhScript return value. "
                    + "OhScript Content: \"%s\"; Return Value: %s", ohScript, execResult);
            log.error("The FlowConditionNode failed to judge the flow condition, error message: {}", exceptionMsg);
            throw new JobberException(ErrorCodes.TYPE_CONVERT_FAILED);
        }
        return (boolean) execResult;
    }
}
