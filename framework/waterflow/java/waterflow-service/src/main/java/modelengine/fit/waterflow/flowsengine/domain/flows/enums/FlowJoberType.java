/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import lombok.Getter;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers.EchoJoberParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers.GeneralJoberParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers.GenericableJoberParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers.HttpJoberParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers.JoberParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers.OhScriptJoberParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers.StoreJoberParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers.EchoJoberRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers.GeneralJoberRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers.GenericableJoberRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers.HttpJoberRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers.JoberRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers.OhScriptJoberRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers.StoreJoberRule;

import java.util.Arrays;
import java.util.Locale;

/**
 * 流程定义自动任务类型
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public enum FlowJoberType {
    GENERAL_JOBER("GENERAL_JOBER", new GeneralJoberParser(), new GeneralJoberRule()),
    HTTP_JOBER("HTTP_JOBER", new HttpJoberParser(), new HttpJoberRule()),
    ECHO_JOBER("ECHO_JOBER", new EchoJoberParser(), new EchoJoberRule()),
    OHSCRIPT_JOBER("OHSCRIPT_JOBER", new OhScriptJoberParser(), new OhScriptJoberRule()),
    GENERICABLE_JOBER("GENERICABLE_JOBER", new GenericableJoberParser(), new GenericableJoberRule()),
    STORE_JOBER("STORE_JOBER", new StoreJoberParser(), new StoreJoberRule()),
    ;

    private final String code;

    private final JoberParser joberParser;

    private final JoberRule joberRule;

    FlowJoberType(String code, JoberParser joberParser, JoberRule joberRule) {
        this.code = code;
        this.joberParser = joberParser;
        this.joberRule = joberRule;
    }

    /**
     * getJoberType
     *
     * @param code code
     * @return FlowJoberType
     */
    public static FlowJoberType getJoberType(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "FlowJoberType", code));
    }
}
