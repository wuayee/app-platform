/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.enums;

import lombok.Getter;
import modelengine.fit.jade.waterflow.exceptions.WaterflowParamException;
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

import static modelengine.fit.jade.waterflow.ErrorCodes.ENUM_CONVERT_FAILED;

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
