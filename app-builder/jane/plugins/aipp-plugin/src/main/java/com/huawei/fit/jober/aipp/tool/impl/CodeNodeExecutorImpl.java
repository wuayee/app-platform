/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool.impl;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.jober.aipp.dto.CodeExecuteResDto;
import com.huawei.fit.jober.aipp.service.CodeExecuteService;
import com.huawei.fit.jober.aipp.tool.CodeNodeExecutor;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;

import java.util.Map;

/**
 * Code节点功能类实现
 *
 * @author 方誉州
 * @since 2024-06-21
 */
@Component
public class CodeNodeExecutorImpl implements CodeNodeExecutor {
    private static final Logger logger = Logger.get(CodeNodeExecutorImpl.class);

    private final CodeExecuteService codeExecuteService;

    public CodeNodeExecutorImpl(CodeExecuteService codeExecuteService) {
        this.codeExecuteService = notNull(codeExecuteService, "The code execute service cannot be null");
    }

    @Override
    @Fitable("default")
    public Object executeNodeCode(Map<String, Object> args, String code, String language) {
        CodeExecuteResDto result = codeExecuteService.run(args, code, language);

        if (result.getIsOk()) {
            return result.getValue();
        }
        String errorCode = result.getMsg();
        logger.error("Code execute returns error: {}", errorCode);
        throw new JobberException(ErrorCodes.FLOW_EXECUTE_FITABLE_TASK_FAILED,
                "", "", "", errorCode);
    }
}
