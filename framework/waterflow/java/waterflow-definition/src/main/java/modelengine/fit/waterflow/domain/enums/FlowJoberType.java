/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.enums;

import static modelengine.fit.waterflow.common.ErrorCodes.ENUM_CONVERT_FAILED;

import lombok.Getter;
import modelengine.fit.waterflow.common.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.parsers.nodes.jobers.EchoJoberParser;
import modelengine.fit.waterflow.domain.parsers.nodes.jobers.GeneralJoberParser;
import modelengine.fit.waterflow.domain.parsers.nodes.jobers.HttpJoberParser;
import modelengine.fit.waterflow.domain.parsers.nodes.jobers.JoberParser;
import modelengine.fit.waterflow.domain.parsers.nodes.jobers.OhScriptJoberParser;
import modelengine.fit.waterflow.domain.validators.rules.jobers.EchoJoberRule;
import modelengine.fit.waterflow.domain.validators.rules.jobers.GeneralJoberRule;
import modelengine.fit.waterflow.domain.validators.rules.jobers.HttpJoberRule;
import modelengine.fit.waterflow.domain.validators.rules.jobers.JoberRule;
import modelengine.fit.waterflow.domain.validators.rules.jobers.OhScriptJoberRule;

import java.util.Arrays;
import java.util.Locale;

/**
 * 流程定义自动任务类型
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
public enum FlowJoberType {
    GENERAL_JOBER("GENERAL_JOBER", new GeneralJoberParser(), new GeneralJoberRule()),
    HTTP_JOBER("HTTP_JOBER", new HttpJoberParser(), new HttpJoberRule()),
    ECHO_JOBER("ECHO_JOBER", new EchoJoberParser(), new EchoJoberRule()),
    OHSCRIPT_JOBER("OHSCRIPT_JOBER", new OhScriptJoberParser(), new OhScriptJoberRule());

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
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowJoberType", code));
    }
}
