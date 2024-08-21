/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.genericable;

import com.huawei.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * aipp运行时服务层接口
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
public interface AippRunTimeService {
    /**
     * 指定版本启动一个Aipp
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param version aipp 版本
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @return 实例id
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.start.app")
    String createAippInstance(String aippId, String version, Map<String, Object> initContext, OperationContext context);
}
