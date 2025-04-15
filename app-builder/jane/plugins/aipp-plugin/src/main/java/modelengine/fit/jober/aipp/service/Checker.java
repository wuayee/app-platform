/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;

import java.util.List;

/**
 * 节点配置检验器
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
public interface Checker {
    /**
     * 校验配置合法性。
     *
     * @param appCheckDto 待检查的配置。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 检查结果。
     */
    List<CheckResult> validate(AppCheckDto appCheckDto, OperationContext context);
}
