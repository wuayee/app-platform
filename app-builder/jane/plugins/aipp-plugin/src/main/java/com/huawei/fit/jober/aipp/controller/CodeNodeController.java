/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.CodeExecuteParamDto;
import com.huawei.fit.jober.aipp.dto.CodeExecuteResDto;
import com.huawei.fit.jober.aipp.service.CodeExecuteService;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 代码节点IDE代码运行接口
 *
 * @author 方誉州
 * @since 2024-07-09
 */
@Component
@RequestMapping(path = "/v1/api/code", group = "aipp运行时管理接口")
public class CodeNodeController extends AbstractController {
    private static final int EXECUTE_ERR_CODE = -1;

    private final CodeExecuteService codeExecuteService;

    /**
     * CodeNodeController
     *
     * @param authenticator authenticator
     * @param codeExecuteService codeExecuteService实例
     */
    public CodeNodeController(Authenticator authenticator, CodeExecuteService codeExecuteService) {
        super(authenticator);
        this.codeExecuteService = codeExecuteService;
    }

    /**
     * 执行用户代码
     *
     * @param params 包含代码入参和代码的{@link List}
     * @return 表示代码执行结果
     */
    @PostMapping(value = "/run", description = "运行用户的代码")
    public Rsp<Object> run(@RequestBody CodeExecuteParamDto params) {
        if (params == null) {
            return Rsp.err(EXECUTE_ERR_CODE, "Null params");
        }
        CodeExecuteResDto res = codeExecuteService.run(
                params.getArgs(),
                params.getCode(),
                params.getLanguage()
        );

        if (res.getIsOk()) {
            return Rsp.ok(res.getValue());
        } else {
            return Rsp.err(EXECUTE_ERR_CODE, res.getMsg());
        }
    }
}
