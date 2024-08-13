/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.dto.CodeExecuteResDto;

import java.util.Map;

/**
 * 用户代码执行服务接口
 *
 * @author 方誉州
 * @since 2024-07-11
 */
public interface CodeExecuteService {
    /**
     * 执行用户代码
     *
     * @param args 表示用户代码入参的{@link Map}
     * @param code 表示用户代码的{@link String}
     * @param language 表示用户代码的语言的{@link String}
     * @return 用户代码的执行结果
     */
    CodeExecuteResDto run(Map<String, Object> args, String code, String language);
}
